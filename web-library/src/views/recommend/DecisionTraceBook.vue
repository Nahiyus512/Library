<template>
  <div class="trace-book-page">
    <div class="top card">
      <div>
        <h2>单本推荐依据分析</h2>
        <p>聚焦《{{ selectedBookRow?.bookName || targetBookName || '当前书籍' }}》的推荐逻辑、信号权重与关键证据。</p>
      </div>
      <div class="ops">
        <el-button class="trace-btn trace-btn-back" @click="openAllTrace">查看全部依据</el-button>
        <el-button class="trace-btn trace-btn-back" @click="goBack">返回推荐页</el-button>
        <el-button class="trace-btn trace-btn-refresh" :loading="loading" @click="reloadAll">刷新数据</el-button>
      </div>
    </div>

    <el-empty v-if="!traceId" description="缺少决策记录标识，无法分析" />

    <template v-else>
      <el-empty v-if="!selectedBookRow" description="未找到该书的推荐依据" />

      <div v-else class="content-grid">
        <div class="card section overview">
          <h3>核心结论</h3>
          <p class="summary-line">
            本次推荐以「{{ strategyText(trace?.strategyUsed || '') }}」为主策略，面向「{{ intentText(trace?.intent || '') }}」意图生成。
          </p>
          <p class="main-reason">{{ selectedBookRow.explanation?.mainReason || selectedBookRow.reason || '暂无推荐说明' }}</p>
          <div class="metric-grid">
            <div class="metric-item">
              <span>推荐位次</span>
              <b>{{ rankText }}</b>
            </div>
            <div class="metric-item">
              <span>命中策略数</span>
              <b>{{ hitStrategies.length }}</b>
            </div>
            <div class="metric-item">
              <span>主导信号</span>
              <b>{{ dominantSignal }}</b>
            </div>
            <div class="metric-item">
              <span>关键证据数</span>
              <b>{{ keyFeatures.length }}</b>
            </div>
          </div>
        </div>

        <div class="card section strategy">
          <h3>策略命中分析</h3>
          <p class="summary-line">
            命中策略：{{ hitStrategiesText }}；本书综合命中强度：{{ hitIntensityText }}。
          </p>
          <div class="section-body strategy-list">
            <div v-if="hitStrategies.length === 0" class="empty-note">暂无策略命中记录</div>
            <div v-for="(strategy, idx) in hitStrategies" :key="`${strategy}-${idx}`" class="strategy-item">
              <div class="strategy-head">
                <span class="index">{{ idx + 1 }}</span>
                <strong>{{ strategyText(strategy) }}</strong>
              </div>
              <p>{{ strategyHint(strategy) }}</p>
              <div class="strategy-tag-row">
                <span class="strategy-tag">命中类型</span>
                <span class="strategy-tag">{{ strategyCategory(strategy) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="card section contribution">
          <h3>信号贡献占比</h3>
          <div class="section-body">
            <div v-if="weightContributions.length === 0" class="empty-note">暂无信号贡献数据</div>
            <div v-for="item in weightContributions" :key="item.key" class="contrib-row">
              <div class="contrib-head">
                <span>{{ contributionText(item.key) }}</span>
                <b>{{ item.percent }}%</b>
              </div>
              <div class="bar-wrap"><div class="bar" :style="{ width: `${item.percent}%` }"></div></div>
            </div>
          </div>
        </div>

        <div class="card section evidence">
          <h3>关键证据</h3>
          <div class="section-body evidence-list">
            <div v-if="keyFeatures.length === 0" class="empty-note">暂无关键证据</div>
            <div v-for="(feature, idx) in keyFeatures" :key="`${feature}-${idx}`" class="evidence-item">
              <span class="dot"></span>
              <span>{{ feature }}</span>
            </div>
          </div>
        </div>

        <div class="card section flow">
          <h3>决策路径解读</h3>
          <div class="section-body flow-list">
            <div v-if="translatedDecisionFlow.length === 0" class="empty-note">暂无决策路径</div>
            <div v-for="(step, idx) in translatedDecisionFlow" :key="`${step.title}-${idx}`" class="flow-item">
              <div class="flow-index">{{ idx + 1 }}</div>
              <div>
                <div class="flow-title">{{ step.title }}</div>
                <div class="flow-desc">{{ step.desc }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="card section context">
          <h3>决策上下文</h3>
          <div class="section-body context-list">
            <div class="line"><span>用户意图</span><b>{{ intentText(trace?.intent || '') }}</b></div>
            <div class="line"><span>主策略</span><b>{{ strategyText(trace?.strategyUsed || '') }}</b></div>
            <div class="line"><span>生成时间</span><b>{{ formatTime(trace?.createdAt) }}</b></div>
            <div class="line"><span>决策记录ID</span><b class="mono">{{ trace?.id || '-' }}</b></div>
            <div class="line"><span>目标书籍ID</span><b class="mono">{{ targetBookId || '-' }}</b></div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCookies } from '@vueuse/integrations/useCookies'
import myAxios from '@/api/index'

interface Trace {
  id: string
  userId?: number
  intent: string
  strategyUsed: string
  decisionPath: string[]
  recommendSnapshot: Array<{
    bookId?: number
    bookName: string
    reason: string
    hitStrategies?: string[]
    explanation?: {
      mainReason?: string
      keyFeatures?: string[]
      weightContributions?: Record<string, number>
    }
  }>
  createdAt: string
}

const route = useRoute()
const router = useRouter()
const cookies = useCookies()

const loading = ref(false)
const trace = ref<Trace | null>(null)
const traceId = ref(String(route.query.traceId || ''))
const targetBookId = ref(Number(route.query.bookId || 0))
const targetBookName = ref(String(route.query.bookName || ''))
const userId = ref(Number(route.query.userId || 0))

const selectedBookRow = computed(() => {
  if (!trace.value || !targetBookId.value) return null
  return (trace.value.recommendSnapshot || []).find((x) => Number(x.bookId) === targetBookId.value) || null
})

const rankText = computed(() => {
  if (!trace.value || !selectedBookRow.value) return '-'
  const index = (trace.value.recommendSnapshot || []).findIndex((x) => Number(x.bookId) === targetBookId.value)
  if (index < 0) return '-'
  return `${index + 1} / ${trace.value.recommendSnapshot.length}`
})

const hitStrategies = computed(() => selectedBookRow.value?.hitStrategies || [])
const keyFeatures = computed(() => selectedBookRow.value?.explanation?.keyFeatures || [])

const weightContributions = computed(() => {
  const raw = selectedBookRow.value?.explanation?.weightContributions || {}
  return Object.entries(raw)
    .map(([key, value]) => ({ key, percent: Math.round((Number(value) || 0) * 100) }))
    .sort((a, b) => b.percent - a.percent)
})

const dominantSignal = computed(() => {
  const top = weightContributions.value[0]
  if (!top) return '暂无'
  return `${contributionText(top.key)} (${top.percent}%)`
})

const hitStrategiesText = computed(() => {
  if (hitStrategies.value.length === 0) return '无'
  return hitStrategies.value.map((s) => strategyText(s)).join('、')
})

const hitIntensityText = computed(() => {
  const count = hitStrategies.value.length
  if (count >= 4) return '高'
  if (count >= 2) return '中'
  if (count === 1) return '低'
  return '无'
})

const intentText = (intent: string) => {
  const map: Record<string, string> = {
    learning: '学习导向',
    professional: '专业提升',
    leisure: '休闲阅读',
    interest_exploration: '兴趣探索'
  }
  return map[intent] || intent || '-'
}

const strategyText = (strategy: string) => {
  const map: Record<string, string> = {
    hybrid_strategy: '混合策略（Hybrid）',
    user_cf: '用户协同',
    item_cf: '物品协同',
    content_based: '内容推荐',
    association_rule: '关联规则',
    lfm: '隐语义模型'
  }
  return map[strategy] || strategy || '-'
}

const strategyHint = (strategy: string) => {
  const map: Record<string, string> = {
    hybrid_strategy: '融合多路信号，平衡准确率与多样性。',
    user_cf: '参考相似读者的历史行为进行匹配。',
    item_cf: '根据相似书籍关系补充候选结果。',
    content_based: '依据内容标签与兴趣关键词匹配。',
    association_rule: '根据共读行为与关联规则推荐。',
    lfm: '通过隐语义向量捕捉潜在兴趣。'
  }
  return map[strategy] || '该策略参与了本次推荐计算。'
}

const contributionText = (key: string) => {
  const map: Record<string, string> = {
    historyPref: '历史高分偏好',
    likeCategoryShare: '兴趣分类匹配',
    similarUserBehavior: '相似用户行为',
    contentSimilarity: '内容相似度',
    popularity: '热度信号',
    featureAnalytics: '特色页分析信号'
  }
  return map[key] || key
}

const strategyCategory = (strategy: string) => {
  const map: Record<string, string> = {
    hybrid_strategy: '融合策略',
    user_cf: '协同过滤',
    item_cf: '协同过滤',
    content_based: '内容匹配',
    association_rule: '规则匹配',
    lfm: '隐语义建模'
  }
  return map[strategy] || '综合信号'
}

const formatTime = (text?: string) => {
  if (!text) return '-'
  const d = new Date(text)
  return Number.isNaN(d.getTime()) ? text : d.toLocaleString()
}

const parseProfileStep = (raw: string) => {
  const content = raw.replace('profile(', '').replace(')', '')
  const rows = content.split(',').map((s) => s.trim())
  const record: Record<string, string> = {}
  rows.forEach((r) => {
    const [k, v] = r.split('=')
    if (k && v !== undefined) record[k.trim()] = v.trim()
  })
  return `评分次数 ${record.scoreCount || '0'}，点赞次数 ${record.likeCount || '0'}，分类多样性 ${record.classifyDiversity || '0'}`
}

const translatedDecisionFlow = computed(() => {
  const path = trace.value?.decisionPath || []
  return path.map((step) => {
    if (step.startsWith('intent_recognition=')) {
      const intent = step.split('=')[1] || ''
      return { title: '意图识别', desc: intentText(intent) }
    }
    if (step.startsWith('profile(')) {
      return { title: '用户画像分析', desc: parseProfileStep(step) }
    }
    if (step.startsWith('strategy=')) {
      const strategy = step.split('=')[1] || ''
      return { title: '策略选择', desc: strategyText(strategy) }
    }
    if (step.startsWith('weights=')) {
      return { title: '权重分配', desc: '已完成多算法权重归一化' }
    }
    if (step.startsWith('result=')) {
      const result = step.split('=')[1] || ''
      return { title: '结果生成', desc: result === 'hit' ? '命中推荐结果' : '触发冷启动兜底' }
    }
    return { title: '决策步骤', desc: step }
  })
})

const fetchTrace = async () => {
  if (!traceId.value) return
  const res = await myAxios.get(`/agent/decisionTrace/${traceId.value}`)
  trace.value = res.data?.data || null
}

const resolveUserId = () => {
  if (userId.value) return
  if (trace.value?.userId) {
    userId.value = Number(trace.value.userId)
    return
  }
  const idFromCookie = cookies.get('userId')
  if (idFromCookie) userId.value = Number(idFromCookie)
}

const reloadAll = async () => {
  try {
    loading.value = true
    await fetchTrace()
    resolveUserId()
  } catch {
    ElMessage.error('单本推荐依据加载失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/readingDecision')
}

const openAllTrace = () => {
  router.push({
    path: '/decisionTrace',
    query: {
      traceId: traceId.value,
      userId: String(userId.value || '')
    }
  })
}

onMounted(async () => {
  await reloadAll()
})
</script>

<style scoped>
.trace-book-page {
  height: 100%;
  min-height: 0;
  padding: 14px;
  background: #f7f7f5;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 12px;
  overflow: hidden;
}

.card {
  background: #fff;
  border: 1px solid #dcded9;
  border-radius: 8px;
  padding: 10px;
}

.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.top h2 {
  margin: 0;
  font-size: 20px;
}

.top p {
  margin: 4px 0 0;
  color: #666;
  font-size: 13px;
}

.ops {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

:deep(.trace-btn.el-button) {
  border-radius: 8px;
  transition: all 0.2s ease;
}

:deep(.trace-btn-back.el-button) {
  background: #fff;
  border-color: #000;
  color: #000;
}

:deep(.trace-btn-back.el-button:hover) {
  background: #f5f5f5;
  border-color: #000;
  color: #000;
}

:deep(.trace-btn-refresh.el-button) {
  background: #000;
  border-color: #000;
  color: #fff;
}

:deep(.trace-btn-refresh.el-button:hover) {
  background: #fff;
  border-color: #000;
  color: #000;
}

.content-grid {
  min-height: 0;
  height: 100%;
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  grid-template-rows: repeat(2, minmax(0, 1fr));
  grid-template-areas:
    'overview contribution strategy'
    'flow evidence context';
  overflow: hidden;
}

.section {
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.35;
  color: #22352d;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.section-body {
  min-height: 0;
  overflow: visible;
  padding-right: 0;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
  flex: 1 1 auto;
}

.overview {
  grid-area: overview;
}
.strategy {
  grid-area: strategy;
}
.contribution {
  grid-area: contribution;
}
.evidence {
  grid-area: evidence;
}
.flow {
  grid-area: flow;
}
.context {
  grid-area: context;
}

.main-reason {
  margin: 2px 0 8px;
  color: #2f3c35;
  font-size: 14px;
  line-height: 1.6;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.summary-line {
  margin: 0 0 6px;
  color: #506159;
  font-size: 12px;
  line-height: 1.5;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  align-items: stretch;
}

.metric-item {
  border: 1px solid #e0e7e2;
  border-radius: 8px;
  background: #f8faf9;
  padding: 8px;
  display: grid;
  gap: 4px;
  align-content: center;
}

.metric-item span {
  font-size: 12px;
  color: #68756d;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.metric-item b {
  font-size: 14px;
  color: #22352d;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.strategy-list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 8px;
}

.strategy-item {
  border: 1px solid #dce6e1;
  border-radius: 8px;
  background: #f9fbfa;
  padding: 8px;
}

.strategy-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.index {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #2f6b57;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
}

.strategy-item p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #4b5a53;
  line-height: 1.4;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.strategy-tag-row {
  margin-top: 6px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.strategy-tag {
  border: 1px solid #d8e6de;
  background: #f2f7f4;
  color: #335547;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 11px;
  line-height: 1.3;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.contrib-row {
  margin-top: 8px;
}

.contrib-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #2d3d35;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.bar-wrap {
  margin-top: 4px;
  height: 8px;
  border-radius: 99px;
  background: #e9efeb;
}

.bar {
  height: 100%;
  border-radius: 99px;
  background: #3e7a68;
}

.evidence-list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 8px;
}

.evidence-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  border: 1px solid #e3ebe6;
  border-radius: 8px;
  padding: 7px 9px;
  background: #fafcfb;
  font-size: 12px;
  color: #30443b;
  line-height: 1.45;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.dot {
  width: 8px;
  height: 8px;
  margin-top: 5px;
  border-radius: 50%;
  background: #3e7a68;
  flex: 0 0 auto;
}

.flow-list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 8px;
}

.flow-item {
  display: grid;
  grid-template-columns: 26px minmax(0, 1fr);
  gap: 10px;
  border: 1px solid #dfe7e1;
  border-radius: 8px;
  background: #f8fbf9;
  padding: 8px;
}

.flow-index {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #2f6b57;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.flow-title {
  font-size: 13px;
  font-weight: 700;
  color: #2f5f52;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.flow-desc {
  margin-top: 2px;
  font-size: 12px;
  color: #4e594e;
  line-height: 1.35;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.context-list .line {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin: 0;
  font-size: 13px;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.context-list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 8px;
}

.context-list .line span {
  color: #5f6d65;
}

.context-list .line b {
  color: #1f2d27;
  text-align: right;
  font-size: 13px;
  line-height: 1.35;
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
}

.mono {
  font-family: "PingFang SC", "Microsoft YaHei", "Noto Sans SC", sans-serif;
  font-size: 13px;
}

.empty-note {
  color: #7f8a83;
  font-size: 13px;
}

@media (max-width: 1080px) {
  .trace-book-page {
    overflow: hidden;
  }

  .content-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    grid-template-rows: repeat(3, minmax(0, 1fr));
    grid-template-areas:
      'overview contribution'
      'strategy flow'
      'evidence context';
  }
}

@media (max-width: 760px) {
  .trace-book-page {
    padding: 10px;
    overflow-y: auto;
  }

  .content-grid {
    height: auto;
    grid-template-columns: 1fr;
    grid-template-rows: none;
    grid-template-areas:
      'overview'
      'strategy'
      'contribution'
      'context'
      'evidence'
      'flow';
    overflow: visible;
  }

  .section-body {
    overflow: visible;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }
}
</style>
