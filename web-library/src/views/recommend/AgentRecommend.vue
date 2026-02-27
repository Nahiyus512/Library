<template>
  <div class="agent-page">
    <div class="top-bar card">
      <div class="title-wrap">
        <h2>Agent协同推荐</h2>
        <p>选择阅读目标后，系统动态计算策略权重并刷新推荐。</p>
      </div>
      <div class="action-wrap">
        <el-select v-model="selectedGoal" placeholder="请选择阅读目标" class="goal-select">
          <el-option v-for="item in goalOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button class="recommend-btn" type="primary" :loading="loading" @click="runRecommend">更新推荐</el-button>
      </div>
    </div>

    <div class="main-grid">
      <div class="left-panel">
        <div v-if="result" class="meta-grid">
          <div class="meta-card card">
            <div class="meta-title">识别意图</div>
            <div class="meta-value">{{ intentText(result.intent) }}</div>
          </div>
          <div class="meta-card card">
            <div class="meta-title">融合策略</div>
            <div class="meta-value">混合策略（Hybrid）</div>
          </div>
          <div class="meta-card card wide">
            <div class="meta-title">策略说明</div>
            <div class="meta-value small">{{ strategyDescText(result.strategyDescription) }}</div>
          </div>
        </div>

        <div v-if="result" class="weight-card card">
          <h3>策略权重分布</h3>
          <div v-for="(value, key) in result.weights" :key="key" class="weight-row">
            <span class="key">{{ strategyText(key) }}</span>
            <div class="bar-wrap"><div class="bar" :style="{ width: `${Math.round(value * 100)}%` }"></div></div>
            <span class="val">{{ Math.round(value * 100) }}%</span>
          </div>
        </div>
      </div>

      <div class="right-panel card">
        <div class="right-head">
          <span>推荐结果</span>
          <span class="time">{{ lastUpdated || '未刷新' }}</span>
        </div>

        <div v-if="result && result.recommendedBooks.length > 0" class="book-grid">
          <div v-for="item in result.recommendedBooks" :key="`${item.book.bookId}-${result.decisionTraceId}`" class="book-card" @click="openDetail(item.book)">
            <div class="cover-box">
              <CssBookCover :title="item.book.bookName" :author="item.book.bookAuthor || '未知作者'" :id="item.book.bookId" />
            </div>
            <div class="book-info">
              <h4>{{ item.book.bookName }}</h4>
              <p class="score">融合得分：{{ item.score }}</p>
              <p class="reason">{{ localReason(item) }}</p>
            </div>
          </div>
        </div>

        <el-empty v-else-if="!loading" description="暂无推荐结果" />
      </div>
    </div>

    <BookDetailModal v-model="showDialog" :book="selectedBook" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useCookies } from '@vueuse/integrations/useCookies'
import myAxios from '@/api/index'
import CssBookCover from '@/components/common/CssBookCover.vue'
import BookDetailModal from '@/components/business/BookDetailModal.vue'

interface Book {
  bookId: number
  bookName: string
  bookAuthor?: string
}

interface RecommendItem {
  book: Book
  score: number
  reason: string
  hitStrategies: string[]
}

interface AgentResponse {
  decisionTraceId: string
  intent: string
  strategyUsed: string
  strategyDescription: string
  weights: Record<string, number>
  decisionPath: string[]
  recommendedBooks: RecommendItem[]
}

const cookies = useCookies()
const loading = ref(false)
const userId = ref<number | null>(null)
const result = ref<AgentResponse | null>(null)
const showDialog = ref(false)
const selectedBook = ref<Book | null>(null)
const lastUpdated = ref('')

const goalOptions = [
  { label: '系统学习（入门）', value: 'goal_learning' },
  { label: '专业提升（进阶）', value: 'goal_professional' },
  { label: '兴趣探索（拓展）', value: 'goal_exploration' },
  { label: '休闲阅读（轻松）', value: 'goal_leisure' },
  { label: '平衡推荐（默认）', value: 'goal_default' }
]
const selectedGoal = ref('goal_default')
const demand = computed(() => selectedGoal.value)

const intentText = (intent: string) => {
  const map: Record<string, string> = {
    learning: '学习导向',
    professional: '专业提升',
    leisure: '休闲阅读',
    interest_exploration: '兴趣探索'
  }
  return map[intent] || intent
}

const strategyText = (strategy: string) => {
  const map: Record<string, string> = {
    user_cf: '用户协同',
    item_cf: '物品协同',
    content_based: '内容推荐',
    association_rule: '关联规则',
    lfm: '隐语义模型'
  }
  return map[strategy] || strategy
}

const strategyDescText = (_desc: string) => {
  const intent = result.value?.intent || ''
  return `规则意图识别 + Rank融合，当前意图：${intentText(intent)}`
}

const localReason = (item: RecommendItem) => {
  const strategies = item.hitStrategies.map(strategyText).join(' + ')
  return `命中策略：${strategies || '混合'}`
}

const loadUser = async () => {
  const idFromCookie = cookies.get('userId')
  if (idFromCookie) {
    userId.value = Number(idFromCookie)
    return
  }
  const username = cookies.get('username')
  if (!username) return
  const res = await myAxios.get('http://localhost:8080/user/getUserByName', { params: { name: username } })
  userId.value = Number(res.data?.data?.id)
}

const runRecommend = async () => {
  try {
    if (!userId.value) await loadUser()
    if (!userId.value) {
      ElMessage.warning('未获取到用户信息，请先登录')
      return
    }
    loading.value = true
    const res = await myAxios.post('http://localhost:8080/agent/recommend', {
      userId: userId.value,
      demand: demand.value,
      topN: 4
    })
    result.value = res.data.data
    lastUpdated.value = new Date().toLocaleTimeString()
    ElMessage.success('推荐已更新')
  } catch {
    ElMessage.error('刷新失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

const openDetail = (book: Book) => {
  selectedBook.value = book
  showDialog.value = true
}

onMounted(async () => {
  await loadUser()
  await runRecommend()
})
</script>

<style scoped>
.agent-page {
  height: 100%;
  overflow: hidden;
  padding: 16px;
  background: #f7f7f5;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 12px;
}

.card {
  background: #fff;
  border: 1px solid #dcded9;
  border-radius: 8px;
}

.top-bar {
  padding: 14px 64px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  min-height: 92px;
}

.title-wrap h2 {
  margin: 0;
  font-size: 20px;
  text-align: center;
}

.title-wrap p {
  margin: 6px 0 0;
  color: #5b625b;
  font-size: 13px;
  text-align: center;
}

.action-wrap {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.goal-select {
  width: 250px;
}

:deep(.recommend-btn.el-button--primary) {
  background: #000;
  border-color: #000;
  color: #fff;
  transition: all 0.2s ease;
}

:deep(.recommend-btn.el-button--primary:hover) {
  background: #fff;
  border-color: #000;
  color: #000;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

:deep(.recommend-btn.el-button--primary:active) {
  background: #111;
  border-color: #111;
  color: #fff;
  transform: translateY(1px);
}

:deep(.recommend-btn.el-button--primary.is-disabled) {
  background: #ccc;
  border-color: #ccc;
  color: #fff;
  transform: none;
  box-shadow: none;
}

.main-grid {
  min-height: 0;
  display: grid;
  grid-template-columns: 1.05fr 1.35fr;
  gap: 12px;
}

.left-panel {
  min-height: 0;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 12px;
}

.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.meta-card { padding: 10px; }
.meta-card.wide { grid-column: 1 / -1; }
.meta-title { font-size: 12px; color: #667066; }
.meta-value { margin-top: 4px; font-weight: 700; }
.meta-value.small { font-size: 13px; line-height: 1.45; font-weight: 500; }

.weight-card { padding: 12px; overflow: hidden; }
.weight-card h3 { margin: 0 0 10px; font-size: 15px; }
.weight-row {
  display: grid;
  grid-template-columns: 92px 1fr 50px;
  gap: 6px;
  align-items: center;
  margin-top: 8px;
}
.key, .val { font-size: 12px; }
.bar-wrap { height: 7px; border-radius: 99px; background: #ecefea; }
.bar { height: 100%; border-radius: 99px; background: #3e7a68; }

.right-panel {
  min-height: 0;
  padding: 12px;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 10px;
  overflow: hidden;
}
.right-head { display: flex; justify-content: space-between; font-weight: 600; }
.time { color: #697369; font-size: 12px; }

.book-grid {
  min-height: 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  grid-auto-rows: 154px;
  gap: 10px;
  overflow: hidden;
}
.book-card {
  border: 1px solid #e1e4df;
  border-radius: 8px;
  padding: 8px;
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  gap: 8px;
  cursor: pointer;
  overflow: hidden;
  height: 154px;
  align-items: start;
}
.cover-box { width: 92px; height: 138px; overflow: hidden; border-radius: 4px; flex-shrink: 0; }
.cover-box :deep(.css-book-cover) { width: 92px; height: 138px; }
.book-info h4 { margin: 0; font-size: 14px; line-height: 1.25; }
.score { margin: 6px 0 2px; color: #3e7a68; font-size: 12px; font-weight: 700; }
.reason {
  margin: 0;
  color: #4e594e;
  font-size: 12px;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 1200px) {
  .agent-page { padding: 16px; }
  .top-bar { padding: 14px 48px 12px; }
  .main-grid { grid-template-columns: 1fr; }
  .left-panel { grid-template-rows: auto auto; }
}
</style>
