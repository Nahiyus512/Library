<template>
  <div class="reading-decision-page">
    <div class="header card">
      <div>
        <h2>阅读决策</h2>
        <p>AI 引导你逐步选择偏好，再输出推荐结果。</p>
      </div>
      <el-button class="ai-style-btn trace-btn" :disabled="!latestTraceId" @click="openDecisionTrace(latestTraceId)">
        查看推荐依据
      </el-button>
    </div>

    <div ref="chatRef" class="chat card">
      <div
        v-for="msg in messages"
        :key="msg.id"
        class="msg"
        :class="msg.role === 'user' ? 'user-msg' : 'ai-msg'"
      >
        <div v-if="msg.kind === 'text'" class="bubble">{{ msg.text }}</div>
        <div v-else class="cards-wrap">
          <div
            v-for="item in msg.cards"
            :key="`${item.book.bookId}-${msg.traceId}`"
            class="book-card"
          >
            <div class="cover-box">
              <CssBookCover
                :title="item.book.bookName"
                :author="item.book.bookAuthor || '未知作者'"
                :id="item.book.bookId"
              />
            </div>
            <div class="book-info">
              <h4>{{ item.book.bookName }}</h4>
              <p class="reason">{{ localReason(item) }}</p>
              <div class="card-actions">
                <template v-if="isFeatureStageMessage(msg)">
                  <el-button
                    size="small"
                    class="ai-style-btn card-feature-btn"
                    @click="openFeatureExperience({ bookName: item.book.bookName, from: 'feature_stage' })"
                  >
                    进入特色页面
                  </el-button>
                </template>
                <template v-else>
                  <el-button size="small" class="card-white-btn" @click="openDetail(item.book)">查看详情</el-button>
                  <el-button
                    size="small"
                    class="card-white-btn"
                    :disabled="isBookshelfStatusUnknown(item.book.bookId)"
                    @click="toggleBookshelf(item.book)"
                  >
                    {{ bookshelfActionText(item.book.bookId) }}
                  </el-button>
                  <el-button
                    size="small"
                    class="card-white-btn trace-link"
                    @click="openDecisionTrace(msg.traceId, item.book.bookId, item.book.bookName)"
                  >
                    查看推荐依据
                  </el-button>
                  <el-button
                    v-if="isFeatureBook(item.book)"
                    size="small"
                    class="ai-style-btn card-feature-btn"
                    @click="openFeatureExperience({ bookId: item.book.bookId, bookName: item.book.bookName, from: 'card' })"
                  >
                    特色介绍
                  </el-button>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="options card">
      <div class="question">{{ currentQuestion }}</div>
      <div v-if="loading" class="loading">正在生成推荐...</div>
      <div v-else class="btn-group" :style="buttonGridStyle">
        <el-button
          v-for="option in currentOptions"
          :key="option.value"
          class="opt-btn card-white-btn"
          @click="selectOption(option)"
        >
          {{ option.label }}
        </el-button>
      </div>
    </div>

    <BookDetailModal v-model="showDialog" :book="selectedBook" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useCookies } from '@vueuse/integrations/useCookies'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import myAxios from '@/api/index'
import CssBookCover from '@/components/common/CssBookCover.vue'
import BookDetailModal from '@/components/business/BookDetailModal.vue'

type Step = 'feature_entry' | 'feature_pick' | 'feature_after_return' | 'goal' | 'classify' | 'hot' | 'similar' | 'feedback'

interface BookClassItem {
  classify: string
}

interface Book {
  bookId: number
  bookName: string
  bookAuthor?: string
  bookClassify?: string
  bookImge?: string
  bookPublic?: string
}

interface RecommendItem {
  book: Book
  score: number
  reason: string
  hitStrategies: string[]
  explanation?: {
    mainReason?: string
    keyFeatures?: string[]
  }
}

interface AgentResponse {
  decisionTraceId: string
  recommendedBooks: RecommendItem[]
}

interface OptionItem {
  label: string
  value: string
}

interface ChatMessage {
  id: number
  role: 'ai' | 'user'
  kind: 'text' | 'cards'
  text?: string
  cards?: RecommendItem[]
  traceId?: string
}

const SESSION_KEY = 'reading_decision_session_v1'
const FEATURE_RETURN_KEY = 'reading_decision_feature_return_v1'
const FEATURE_STAGE_TRACE = 'feature_stage'

const cookies = useCookies()
const router = useRouter()
const chatRef = ref<HTMLElement | null>(null)
const loading = ref(false)
const step = ref<Step>('feature_entry')
const userId = ref<number>(0)
const latestTraceId = ref('')
const messages = ref<ChatMessage[]>([])
const msgId = ref(1)

const hotScoreByBook = ref<Record<number, number>>({})
const interestClassWeight = ref<Record<string, number>>({})
const rerollCursor = ref(0)
const bookshelfState = ref<Record<number, boolean>>({})
const bookshelfSyncing = ref<Record<number, boolean>>({})
const featureBatchCursor = ref(0)
const featureBatchNames = ref<string[]>([])

const showDialog = ref(false)
const selectedBook = ref<Book | null>(null)

const selections = ref({
  goal: 'goal_default',
  classify: '不限',
  hot: 'no',
  similar: 'no'
})

const goalOptions: OptionItem[] = [
  { label: '学习提升', value: 'goal_learning' },
  { label: '兴趣探索', value: 'goal_exploration' },
  { label: '轻松阅读', value: 'goal_leisure' },
  { label: '专业进阶', value: 'goal_professional' }
]

const hotOptions: OptionItem[] = [
  { label: '优先热门', value: 'yes' },
  { label: '不强调热门', value: 'no' }
]

const similarOptions: OptionItem[] = [
  { label: '优先相似兴趣用户', value: 'yes' },
  { label: '保持均衡', value: 'no' }
]

const featureEntryOptions: OptionItem[] = [
  { label: '想先看看', value: 'feature_yes' },
  { label: '直接进入阅读决策', value: 'feature_no' }
]

const featureAfterReturnOptions: OptionItem[] = [
  { label: '继续看特色页面', value: 'feature_continue' },
  { label: '进入下一步', value: 'feature_next' }
]

const featurePickActionOptions: OptionItem[] = [
  { label: '换一批', value: 'feature_refresh' },
  { label: '跳过这个阶段', value: 'feature_skip' }
]

const feedbackOptions: OptionItem[] = [
  { label: '换一批', value: 'reroll' },
  { label: '更偏向当前分类', value: 'more_classify' },
  { label: '更热门', value: 'more_hot' },
  { label: '更符合历史偏好', value: 'more_history' }
]

const classifyOptions = ref<OptionItem[]>([{ label: '不限', value: '不限' }])

const currentQuestion = ref('是否先体验特色阅读页面？')
const currentOptions = ref<OptionItem[]>(featureEntryOptions)

const featureRouteByBookName: Record<string, string> = {
  '娱乐至死': '/book/AmusingOurselvesToDeath',
  '美丽新世界': '/book/BraveNewWorld',
  '沙丘': '/book/Dune',
  '平面设计中的网格系统': '/book/GridSystems',
  '银河系漫游指南': '/book/HitchhikersGuide',
  '色彩互动学': '/book/InteractionOfColor',
  '西游记': '/book/JourneyToTheWest',
  '生命3.0': '/book/Life30',
  '1984': '/book/NineteenEightyFour',
  '写给大家看的设计书': '/book/NonDesignersDesignBook',
  '人类简史': '/book/Sapiens',
  '三体': '/book/ThreeBody',
  '三国演义': '/book/ThreeKingdoms',
  '禅与摩托车维修艺术': '/book/Zen'
}

const featureBookNames = Object.keys(featureRouteByBookName)

const buttonGridStyle = computed(() => ({ gridTemplateColumns: 'repeat(4, minmax(0, 1fr))' }))

const persistState = () => {
  const payload = {
    messages: messages.value,
    msgId: msgId.value,
    step: step.value,
    latestTraceId: latestTraceId.value,
    selections: selections.value,
    currentQuestion: currentQuestion.value,
    currentOptions: currentOptions.value,
    rerollCursor: rerollCursor.value,
    bookshelfState: bookshelfState.value,
    featureBatchCursor: featureBatchCursor.value,
    featureBatchNames: featureBatchNames.value
  }
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(payload))
}

const restoreState = () => {
  const raw = sessionStorage.getItem(SESSION_KEY)
  if (!raw) return false
  try {
    const saved = JSON.parse(raw)
    if (!saved || !Array.isArray(saved.messages)) return false
    messages.value = saved.messages
    msgId.value = Number(saved.msgId || 1)
    step.value = (saved.step || 'feature_entry') as Step
    latestTraceId.value = String(saved.latestTraceId || '')
    selections.value = {
      goal: saved.selections?.goal || 'goal_default',
      classify: saved.selections?.classify || '不限',
      hot: saved.selections?.hot || 'no',
      similar: saved.selections?.similar || 'no'
    }
    currentQuestion.value = String(saved.currentQuestion || '是否先体验特色阅读页面？')
    currentOptions.value = Array.isArray(saved.currentOptions) ? saved.currentOptions : featureEntryOptions
    rerollCursor.value = Number(saved.rerollCursor || 0)
    bookshelfState.value = saved.bookshelfState || {}
    bookshelfSyncing.value = {}
    featureBatchCursor.value = Number(saved.featureBatchCursor || 0)
    featureBatchNames.value = Array.isArray(saved.featureBatchNames) ? saved.featureBatchNames : []
    return true
  } catch {
    return false
  }
}

const pushText = (role: 'ai' | 'user', text: string) => {
  messages.value.push({ id: msgId.value++, role, kind: 'text', text })
}

const pushCards = (cards: RecommendItem[], traceId: string) => {
  messages.value.push({ id: msgId.value++, role: 'ai', kind: 'cards', cards, traceId })
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatRef.value) {
    chatRef.value.scrollTop = chatRef.value.scrollHeight
  }
}

const ask = (question: string, options: OptionItem[], nextStep: Step) => {
  currentQuestion.value = question
  currentOptions.value = options
  step.value = nextStep
}

const startGoalFlow = () => {
  pushText('ai', '好的，我们进入阅读目标选择。')
  ask('你更希望这次阅读达到什么目标？', goalOptions, 'goal')
}

const localReason = (item: RecommendItem) => {
  const strategyMap: Record<string, string> = {
    user_cf: '相似读者协同',
    item_cf: '相似书籍关联',
    content_based: '内容特征匹配',
    association_rule: '行为共现规则',
    lfm: '隐语义兴趣建模',
    fallback_popularity: '热度补全',
    feature_stage: '特色交互阅读'
  }
  const hits = (item.hitStrategies || []).map((x) => strategyMap[x] || '综合策略')
  const classify = item.book.bookClassify ? `，分类偏向：${item.book.bookClassify}` : ''
  if (hits.length === 0) return `基于你的阅读目标进行综合匹配${classify}。`
  if (hits.length === 1) return `主要依据「${hits[0]}」进行匹配${classify}。`
  return `综合「${hits.slice(0, 2).join(' + ')}」策略进行推荐${classify}。`
}

const getFeatureRoute = (bookName: string) => {
  return featureRouteByBookName[bookName] || ''
}

const isFeatureBook = (book: Book) => Boolean(getFeatureRoute(book.bookName))
const isFeatureStageMessage = (msg: ChatMessage) => msg.traceId === FEATURE_STAGE_TRACE

const refreshFeatureBatch = () => {
  if (featureBookNames.length === 0) {
    featureBatchNames.value = []
    return
  }
  const start = featureBatchCursor.value % featureBookNames.length
  const doubled = [...featureBookNames, ...featureBookNames]
  featureBatchNames.value = doubled.slice(start, start + Math.min(4, featureBookNames.length))
  featureBatchCursor.value = (featureBatchCursor.value + 4) % featureBookNames.length
}

const buildFeatureBatchCards = (): RecommendItem[] => {
  return featureBatchNames.value.map((name, index) => ({
    book: {
      bookId: 900000 + featureBatchCursor.value + index,
      bookName: name,
      bookAuthor: '特色阅读',
      bookClassify: '特色阅读'
    },
    score: 0,
    reason: '该书提供特色交互阅读页面，可直接进入体验。',
    hitStrategies: ['feature_stage']
  }))
}

const pushFeatureBatchCards = () => {
  const cards = buildFeatureBatchCards()
  if (cards.length > 0) {
    pushCards(cards, FEATURE_STAGE_TRACE)
  }
}

const buildDemand = (feedback = 'none') => {
  const goal = selections.value.goal
  const classify = selections.value.classify
  return `${goal};classify=${classify};hot=${selections.value.hot};similar=${selections.value.similar};feedback=${feedback}`
}

const parseClassifies = (text?: string) => {
  if (!text) return []
  return text
    .split(/[，,;|\s]+/)
    .map((x) => x.trim().toLowerCase())
    .filter((x) => x.length > 0)
}

const isClassifyMatch = (book: Book, classify: string) => {
  if (!classify || classify === '不限') return false
  return parseClassifies(book.bookClassify).includes(classify.trim().toLowerCase())
}

const hotScore = (bookId?: number) => {
  if (!bookId) return 0
  return hotScoreByBook.value[bookId] || 0
}

const historyScore = (book: Book) => {
  const classes = parseClassifies(book.bookClassify)
  if (classes.length === 0) return 0
  let sum = 0
  classes.forEach((c) => {
    sum += interestClassWeight.value[c] || 0
  })
  return sum / classes.length
}

const sortByFeedback = (candidates: RecommendItem[], feedback: string) => {
  const sorted = [...candidates]
  if (feedback === 'more_classify' && selections.value.classify !== '不限') {
    sorted.sort((a, b) => {
      const am = isClassifyMatch(a.book, selections.value.classify) ? 1 : 0
      const bm = isClassifyMatch(b.book, selections.value.classify) ? 1 : 0
      if (bm !== am) return bm - am
      return b.score - a.score
    })
    return sorted
  }
  if (feedback === 'more_hot') {
    sorted.sort((a, b) => {
      const diff = hotScore(b.book.bookId) - hotScore(a.book.bookId)
      if (diff !== 0) return diff
      return b.score - a.score
    })
    return sorted
  }
  if (feedback === 'more_history') {
    sorted.sort((a, b) => {
      const diff = historyScore(b.book) - historyScore(a.book)
      if (diff !== 0) return diff
      return b.score - a.score
    })
    return sorted
  }
  return sorted
}

const pickDisplayCards = (candidates: RecommendItem[], feedback: string) => {
  if (candidates.length <= 4) return candidates
  const sorted = sortByFeedback(candidates, feedback)
  if (feedback === 'reroll') {
    const start = rerollCursor.value % sorted.length
    const doubled = [...sorted, ...sorted]
    return doubled.slice(start, start + 4)
  }
  return sorted.slice(0, 4)
}

const loadUserId = async () => {
  const id = Number(cookies.get('userId') || 0)
  if (id) {
    userId.value = id
    return
  }
  const username = cookies.get('username')
  if (!username) return
  const res = await myAxios.get('/user/getUserByName', { params: { name: username } })
  userId.value = Number(res.data?.data?.id || 0)
}

const loadClassifies = async () => {
  try {
    const res = await myAxios.get('/class/get')
    const list = (res.data?.data || []) as BookClassItem[]
    const values = list.map((x) => String(x.classify || '').trim()).filter((x) => x.length > 0).slice(0, 12)
    classifyOptions.value = [{ label: '不限', value: '不限' }, ...values.map((x) => ({ label: x, value: x }))]
  } catch {
    classifyOptions.value = [
      { label: '不限', value: '不限' },
      { label: '文学', value: '文学' },
      { label: '历史', value: '历史' },
      { label: '科幻', value: '科幻' }
    ]
  }
}

const loadHotMap = async () => {
  try {
    const res = await myAxios.get('/bookLike/top')
    const rows = res.data?.data || []
    const map: Record<number, number> = {}
    rows.forEach((x: any) => {
      const id = Number(x.bookId || 0)
      if (id) map[id] = Number(x.count || 0)
    })
    hotScoreByBook.value = map
  } catch {
    hotScoreByBook.value = {}
  }
}

const loadInterestProfile = async () => {
  if (!userId.value) return
  try {
    const res = await myAxios.get('/agent/profile/insight', { params: { userId: userId.value } })
    const raw = res.data?.data?.interestCategoryDistribution || {}
    const map: Record<string, number> = {}
    Object.keys(raw).forEach((k) => {
      map[String(k).trim().toLowerCase()] = Number(raw[k] || 0)
    })
    interestClassWeight.value = map
  } catch {
    interestClassWeight.value = {}
  }
}

const isInBookshelf = (bookId?: number) => {
  if (!bookId) return false
  return Boolean(bookshelfState.value[bookId])
}

const isBookshelfStatusUnknown = (bookId?: number) => {
  if (!bookId) return true
  return bookshelfState.value[bookId] === undefined || Boolean(bookshelfSyncing.value[bookId])
}

const bookshelfActionText = (bookId?: number) => {
  if (isBookshelfStatusUnknown(bookId)) return '状态加载中'
  return isInBookshelf(bookId) ? '移出书架' : '加入书架'
}

const syncBookshelfStatus = async (cards: RecommendItem[]) => {
  if (!userId.value || cards.length === 0) return
  const pending = cards
    .map((x) => x.book.bookId)
    .filter((id): id is number => Boolean(id))
    .filter((id) => bookshelfState.value[id] === undefined && !bookshelfSyncing.value[id])
  if (pending.length === 0) return

  await Promise.all(
    pending.map(async (bookId) => {
      bookshelfSyncing.value[bookId] = true
      try {
        const res = await myAxios.get('/bookLike/status', { params: { userId: userId.value, bookId } })
        const level = Number(res.data?.data ?? -1)
        bookshelfState.value[bookId] = level === 2
      } catch {
        bookshelfState.value[bookId] = false
      } finally {
        bookshelfSyncing.value[bookId] = false
      }
    })
  )
}

const syncBookshelfStatusFromMessages = async () => {
  if (!userId.value) return
  const cards = messages.value
    .filter((msg) => msg.kind === 'cards' && !isFeatureStageMessage(msg))
    .flatMap((msg) => msg.cards || [])
  await syncBookshelfStatus(cards)
}

const runRecommend = async (feedback = 'none') => {
  if (!userId.value) {
    ElMessage.warning('未获取到用户信息，请重新登录')
    return
  }

  try {
    loading.value = true

    if (feedback === 'more_hot' && Object.keys(hotScoreByBook.value).length === 0) {
      await loadHotMap()
    }
    if ((feedback === 'more_history' || feedback === 'more_classify') && Object.keys(interestClassWeight.value).length === 0) {
      await loadInterestProfile()
    }

    if (feedback === 'reroll') {
      rerollCursor.value += 4
    } else {
      rerollCursor.value = 0
    }

    const res = await myAxios.post<{ data: AgentResponse }>('/agent/recommend', {
      userId: userId.value,
      demand: buildDemand(feedback),
      topN: 12
    })

    const data = res.data?.data
    if (!data) {
      ElMessage.error('推荐失败，请稍后重试')
      return
    }

    const displayCards = pickDisplayCards(data.recommendedBooks || [], feedback)
    await syncBookshelfStatus(displayCards)
    latestTraceId.value = data.decisionTraceId

    pushText('ai', '已根据你的选择生成推荐结果。')
    pushCards(displayCards, data.decisionTraceId)
    pushText('ai', '这批结果是否满意？可以继续调整方向。')
    ask('继续怎么调？', feedbackOptions, 'feedback')
    await scrollToBottom()
  } catch {
    ElMessage.error('推荐失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

const selectOption = async (option: OptionItem) => {
  pushText('user', option.label)

  if (step.value === 'feature_entry') {
    if (option.value === 'feature_yes') {
      if (featureBookNames.length === 0) {
        pushText('ai', '当前暂无可用的特色页面，先进入阅读目标选择。')
        startGoalFlow()
        await scrollToBottom()
        return
      }
      refreshFeatureBatch()
      pushText('ai', '这里先给你 4 本有特色交互页的书，你可以直接进入体验。')
      pushFeatureBatchCards()
      ask('可直接点击上方书籍卡片进入特色页面，或继续操作：', featurePickActionOptions, 'feature_pick')
    } else {
      startGoalFlow()
    }
  } else if (step.value === 'feature_pick') {
    if (option.value === 'feature_refresh') {
      refreshFeatureBatch()
      pushText('ai', '已为你换一批特色书。')
      pushFeatureBatchCards()
      ask('可直接点击上方书籍卡片进入特色页面，或继续操作：', featurePickActionOptions, 'feature_pick')
    } else if (option.value === 'feature_skip') {
      startGoalFlow()
    }
  } else if (step.value === 'feature_after_return') {
    if (option.value === 'feature_continue') {
      refreshFeatureBatch()
      pushText('ai', '继续给你展示一批特色阅读书籍。')
      pushFeatureBatchCards()
      ask('可直接点击上方书籍卡片进入特色页面，或继续操作：', featurePickActionOptions, 'feature_pick')
    } else {
      startGoalFlow()
    }
  } else if (step.value === 'goal') {
    selections.value.goal = option.value
    pushText('ai', '收到。再选一个偏好分类。')
    ask('你更想读哪类书？', classifyOptions.value, 'classify')
  } else if (step.value === 'classify') {
    selections.value.classify = option.value
    pushText('ai', '是否优先热门书单？')
    ask('热门程度偏好：', hotOptions, 'hot')
  } else if (step.value === 'hot') {
    selections.value.hot = option.value
    pushText('ai', '是否优先参考相似兴趣用户？')
    ask('协同偏好：', similarOptions, 'similar')
  } else if (step.value === 'similar') {
    selections.value.similar = option.value
    await runRecommend()
  } else if (step.value === 'feedback') {
    if (option.value === 'more_hot') {
      selections.value.hot = 'yes'
      if (selections.value.goal === 'goal_default') selections.value.goal = 'goal_leisure'
    }
    if (option.value === 'more_history') {
      selections.value.similar = 'yes'
    }
    if (option.value === 'more_classify' && selections.value.classify === '不限') {
      const topClass = Object.entries(interestClassWeight.value).sort((a, b) => b[1] - a[1])[0]?.[0]
      if (topClass) {
        const found = classifyOptions.value.find((x) => x.value.toLowerCase() === topClass)
        if (found) selections.value.classify = found.value
      }
    }
    await runRecommend(option.value)
  }

  await scrollToBottom()
}

const openDecisionTrace = async (traceId?: string, bookId?: number, bookName?: string) => {
  if (!traceId) return
  await router.push({
    path: '/decisionTrace',
    query: {
      traceId,
      userId: String(userId.value || ''),
      scope: bookId ? 'book' : 'all',
      bookId: bookId ? String(bookId) : '',
      bookName: bookName || ''
    }
  })
}

const openDetail = (book: Book) => {
  selectedBook.value = book
  showDialog.value = true
}

const openFeatureExperience = async (book: { bookId?: number; bookName: string; from?: 'card' | 'feature_stage' }) => {
  const routePath = getFeatureRoute(book.bookName)
  if (!routePath) {
    ElMessage.warning('该书暂无特色交互页')
    return
  }
  sessionStorage.setItem(
    FEATURE_RETURN_KEY,
    JSON.stringify({
      bookId: book.bookId || 0,
      bookName: book.bookName,
      from: book.from || 'card',
      at: Date.now()
    })
  )
  await router.push(routePath)
}

const toggleBookshelf = async (book: Book) => {
  const username = cookies.get('username')
  if (!userId.value || !username) {
    ElMessage.warning('未登录，无法加入书架')
    return
  }
  const inShelf = isInBookshelf(book.bookId)
  const targetLikeLevel = inShelf ? 0 : 2
  try {
    const res = await myAxios.put('/bookLike/like', {
      userId: userId.value,
      userName: username,
      bookId: book.bookId,
      bookName: book.bookName,
      likeLevel: targetLikeLevel
    })
    if (res.data?.code === 200) {
      bookshelfState.value[book.bookId] = !inShelf
      ElMessage.success(inShelf ? '已移出书架' : '已加入书架')
      return
    }
    ElMessage.error(inShelf ? '移出书架失败' : '加入书架失败')
  } catch {
    ElMessage.error(inShelf ? '移出书架失败' : '加入书架失败')
  }
}

const consumeFeatureRatingFeedback = async () => {
  const raw = sessionStorage.getItem(FEATURE_RETURN_KEY)
  if (!raw) return
  sessionStorage.removeItem(FEATURE_RETURN_KEY)

  try {
    const payload = JSON.parse(raw || '{}')
    const bookId = Number(payload.bookId || 0)
    const bookName = String(payload.bookName || '该书')
    const from = String(payload.from || '')

    if (from === 'feature_stage') {
      pushText('ai', `你已从《${bookName}》特色页返回。还想继续看特色页面，还是进入下一步阅读决策？`)
      ask('下一步怎么走？', featureAfterReturnOptions, 'feature_after_return')
    }

    if (!userId.value || !bookId) return

    const res = await myAxios.get('/bookLike/status', {
      params: { userId: userId.value, bookId }
    })
    const level = Number(res.data?.data ?? -1)
    if (![0, 1, 2].includes(level)) {
      pushText('ai', `你已返回《${bookName}》。请先在特色页底部完成评分，再用于后续推荐。`)
      await scrollToBottom()
      return
    }

    const levelTextMap: Record<number, string> = {
      0: '0（不喜欢）',
      1: '1（一般）',
      2: '2（喜欢）'
    }
    pushText('ai', `已读取你在《${bookName}》特色页的评分：${levelTextMap[level]}。我会直接用这个评分调整后续推荐。`)
    await scrollToBottom()
  } catch {
    // no-op: don't block main flow when return feedback fetch fails
  }
}

onMounted(async () => {
  await Promise.all([loadUserId(), loadClassifies()])
  const restored = restoreState()
  if (!restored) {
    pushText('ai', '你好，我会通过几个选择帮你快速做阅读决策。')
    pushText('ai', '开始前，你想先查看特色阅读页面吗？')
    ask('是否先体验特色阅读页面？', featureEntryOptions, 'feature_entry')
    persistState()
  }
  await syncBookshelfStatusFromMessages()
  await consumeFeatureRatingFeedback()
  await scrollToBottom()
})

watch([messages, step, latestTraceId, selections, currentQuestion, currentOptions, featureBatchCursor, featureBatchNames], () => persistState(), { deep: true })
watch(messages, async () => {
  await syncBookshelfStatusFromMessages()
}, { deep: true })
</script>

<style scoped>
.reading-decision-page {
  height: 100%;
  min-height: 0;
  padding: 20px 24px 24px;
  background: #f7f7f5;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr) 110px 18px;
  gap: 10px;
  box-sizing: border-box;
}

.card {
  background: #fff;
  border: 1px solid #dcded9;
  border-radius: 8px;
}

.header {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.header p {
  margin: 4px 0 0;
  color: #667066;
  font-size: 13px;
}

.trace-btn {
  border-radius: 8px;
}

:deep(.ai-style-btn.el-button) {
  background: #000;
  border-color: #000;
  color: #fff;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

:deep(.ai-style-btn.el-button:hover) {
  background: #fff;
  border-color: #000;
  color: #000;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.ai-style-btn.el-button:active) {
  transform: translateY(0);
}

:deep(.ai-style-btn.el-button.is-disabled),
:deep(.ai-style-btn.el-button.is-disabled:hover) {
  background: #ccc;
  border-color: #ccc;
  color: #fff;
  transform: none;
  box-shadow: none;
}

:deep(.card-white-btn.el-button) {
  background: #fff;
  border-color: #000;
  color: #000;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

:deep(.card-white-btn.el-button:hover) {
  background: #000;
  border-color: #000;
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chat {
  min-height: 0;
  overflow-y: auto;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.msg {
  display: flex;
}

.ai-msg {
  justify-content: flex-start;
}

.user-msg {
  justify-content: flex-end;
}

.bubble {
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 8px;
  line-height: 1.5;
  font-size: 14px;
}

.ai-msg .bubble {
  background: #f3f6f4;
  color: #2d3a2f;
}

.user-msg .bubble {
  background: #1f4f40;
  color: #fff;
}

.cards-wrap {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.book-card {
  border: 1px solid #e3e8e2;
  border-radius: 8px;
  padding: 8px;
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr);
  gap: 8px;
  align-items: start;
}

.cover-box {
  width: 120px;
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
}

.cover-box :deep(.css-book-cover) {
  width: 120px;
  height: 180px;
  padding: 6px 8px;
}

.cover-box :deep(.cover-content) {
  padding: 8px 10px;
}

.cover-box :deep(.cover-title) {
  font-size: 17px;
  line-height: 1.2;
}

.cover-box :deep(.cover-title.long-title) {
  font-size: 14px;
}

.cover-box :deep(.cover-author) {
  font-size: 10px;
  letter-spacing: 0.2px;
  text-transform: none;
  white-space: normal;
  word-break: normal;
  overflow-wrap: anywhere;
  line-height: 1.15;
  max-width: 100%;
}

.cover-box :deep(.author-section) {
  max-height: none;
  overflow: visible;
  padding: 0 8px;
}

.book-info h4 {
  margin: 0;
  font-size: 14px;
  line-height: 1.3;
}

.reason {
  margin: 6px 0 8px;
  font-size: 12px;
  color: #4e594e;
  line-height: 1.4;
}

.card-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
}

.trace-link {
  margin: 0;
}

.card-actions :deep(.el-button) {
  width: 100%;
  margin: 0;
  font-size: 12px;
  padding-left: 6px;
  padding-right: 6px;
  min-height: 34px;
}

.card-actions :deep(.el-button .el-button__text) {
  white-space: normal;
  line-height: 1.25;
  word-break: break-word;
}

.card-feature-btn {
  width: 100%;
  margin-top: 0;
}

.options {
  grid-row: 3;
  padding: 12px;
  min-height: 110px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-bottom: 0;
}

.question {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 14px;
}

.btn-group {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  flex: 1;
  grid-auto-rows: minmax(0, 1fr);
  column-gap: 12px;
  row-gap: 12px;
  width: 100%;
  align-items: stretch;
}

.opt-btn {
  border-radius: 8px;
  min-height: 0;
  height: 100%;
  width: 100%;
  margin: 0;
}

.btn-group :deep(.el-button .el-button__text) {
  white-space: normal;
  line-height: 1.3;
  word-break: break-word;
}

.btn-group :deep(.el-button + .el-button) {
  margin-left: 0;
}

.loading {
  color: #677267;
  font-size: 13px;
}

@media (max-width: 900px) {
  .reading-decision-page {
    padding: 16px;
    grid-template-rows: auto minmax(0, 1fr) 130px 14px;
  }

  .options {
    min-height: 130px;
    margin-bottom: 0;
  }

  .cards-wrap {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .bubble {
    max-width: 88%;
  }
}

@media (max-width: 600px) {
  .cards-wrap {
    grid-template-columns: 1fr;
  }
}
</style>
