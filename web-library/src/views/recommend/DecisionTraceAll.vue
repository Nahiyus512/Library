<template>
  <div class="trace-page">
    <div class="top card">
      <div>
        <h2>{{ isBookMode ? '单本推荐依据' : '决策透明化看板' }}</h2>
        <p v-if="isBookMode">
          {{ targetBookName || '当前书籍' }} 的推荐解释、命中策略与信号贡献。
        </p>
        <p v-else>查看当前策略、决策流程、推荐解释、策略调整记录和用户兴趣画像。</p>
      </div>
      <div class="ops">
        <el-button v-if="isBookMode" class="trace-btn trace-btn-back" @click="openAllTrace">查看全部依据</el-button>
        <el-button class="trace-btn trace-btn-back" @click="goBack">返回推荐页</el-button>
        <el-button class="trace-btn trace-btn-refresh" :loading="loading" @click="reloadAll">刷新数据</el-button>
      </div>
    </div>

    <div v-if="trace && isBookMode" class="single-grid">
      <div v-if="selectedBookRow" class="card single-card">
        <h3>{{ selectedBookRow.bookName }}</h3>
        <div class="single-reason">{{ selectedBookRow.explanation?.mainReason || selectedBookRow.reason }}</div>
      </div>
      <div v-if="selectedBookRow" class="card single-card">
        <h3>命中策略</h3>
        <div class="strategy-list">
          <div v-if="!(selectedBookRow.hitStrategies || []).length" class="empty-note">暂无策略命中</div>
          <div v-for="(s, idx) in (selectedBookRow.hitStrategies || [])" :key="idx" class="strategy-item">
            <div class="strategy-item-head">
              <span class="strategy-index">{{ idx + 1 }}</span>
              <b>{{ strategyText(s) }}</b>
            </div>
            <p class="strategy-item-desc">{{ strategyHint(s) }}</p>
          </div>
        </div>
      </div>
      <div v-if="selectedBookRow" class="card single-card">
        <h3>信号贡献</h3>
        <div class="section-body">
          <div
            v-for="(v, k) in (selectedBookRow.explanation?.weightContributions || {})"
            :key="k"
            class="line"
          >
            <span>{{ contributionText(k) }}</span><b>{{ Math.round((v || 0) * 100) }}%</b>
          </div>
        </div>
      </div>
      <div v-if="selectedBookRow" class="card single-card">
        <h3>关键依据</h3>
        <div class="feature-list">
          <div v-if="!(selectedBookRow.explanation?.keyFeatures || []).length" class="empty-note">暂无关键依据</div>
          <div v-for="(f, idx) in (selectedBookRow.explanation?.keyFeatures || [])" :key="idx" class="feature-item">
            <span class="feature-dot"></span>
            <span>{{ f }}</span>
          </div>
        </div>
      </div>
      <el-empty v-if="!selectedBookRow" description="未找到该书的推荐依据" />
    </div>

    <div v-else-if="trace" class="grid">
      <div class="card section area-strategy">
        <h3>当前推荐策略</h3>
        <div class="section-body">
          <div class="line"><span>意图</span><b>{{ intentText(trace.intent) }}</b></div>
          <div class="line"><span>策略</span><b>{{ strategyText(trace.strategyUsed) }}</b></div>
          <div class="line"><span>时间</span><b>{{ formatTime(trace.createdAt) }}</b></div>
          <div class="weights">
            <div v-for="(v, k) in trace.weights" :key="k" class="weight-row">
              <span>{{ strategyText(k) }}</span>
              <div class="bar-wrap"><div class="bar" :style="{ width: `${Math.round(v * 100)}%` }"></div></div>
              <span>{{ Math.round(v * 100) }}%</span>
            </div>
          </div>
        </div>
      </div>

      <div class="card section area-flow">
        <h3>决策流程图</h3>
        <div class="section-body">
          <div class="flow-wrap">
            <template v-for="(step, idx) in translatedDecisionFlow" :key="`${idx}-${step.title}`">
              <div class="flow-node">
                <div class="flow-title">{{ step.title }}</div>
                <div class="flow-desc">{{ step.desc }}</div>
              </div>
              <div v-if="idx < translatedDecisionFlow.length - 1" class="flow-arrow">→</div>
            </template>
          </div>
        </div>
      </div>

      <div class="card section area-explain">
        <h3>推荐解释结构</h3>
        <div class="section-body">
          <div v-for="(row, idx) in trace.recommendSnapshot" :key="idx" class="book-row">
            <div class="book-name">{{ row.bookName }}</div>
            <div class="reason">{{ row.explanation?.mainReason || row.reason }}</div>
            <div class="chips">
              <span v-for="(feature, fIdx) in (row.explanation?.keyFeatures || [])" :key="fIdx" class="chip">{{ feature }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="card section area-latest">
        <h3>最近3次策略调整记录</h3>
        <div class="section-body">
          <div v-if="latest.length === 0" class="empty">暂无记录</div>
          <div v-for="item in latest" :key="item.id" class="latest-item">
            <div>{{ formatTime(item.createdAt) }}</div>
            <div>{{ intentText(item.intent) }} / {{ strategyText(item.strategyUsed) }}</div>
          </div>
        </div>
      </div>

      <div class="card section area-profile">
        <h3>用户兴趣画像</h3>
        <div class="section-body">
          <el-tabs v-model="profileTab" class="profile-tabs" @tab-change="onProfileTabChange">
            <el-tab-pane label="兴趣分类分布" name="interest">
              <div class="chart-stage">
                <div ref="interestChartRef" class="chart"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="评分区间分布" name="score">
              <div class="chart-stage">
                <div ref="scoreChartRef" class="chart"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="历史行为统计" name="history">
              <div class="chart-stage">
                <div ref="historyChartRef" class="chart"></div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无决策记录" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import myAxios from '@/api/index'
import { useCookies } from '@vueuse/integrations/useCookies'
import * as echarts from 'echarts'

interface Trace {
  id: string
  userId?: number
  intent: string
  strategyUsed: string
  weights: Record<string, number>
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

interface Profile {
  interestCategoryDistribution: Record<string, number>
  readingActiveTrend: Array<{ period: string; count: number }>
  scoreRangeDistribution: Record<string, number>
  historyBehaviorStats: Record<string, number>
}

const route = useRoute()
const router = useRouter()
const cookies = useCookies()
const loading = ref(false)
const trace = ref<Trace | null>(null)
const latest = ref<Trace[]>([])
const profile = ref<Profile>({
  interestCategoryDistribution: {},
  readingActiveTrend: [],
  scoreRangeDistribution: {},
  historyBehaviorStats: {}
})

const scoreChartRef = ref<HTMLElement | null>(null)
const historyChartRef = ref<HTMLElement | null>(null)
const interestChartRef = ref<HTMLElement | null>(null)
let scoreChart: echarts.ECharts | null = null
let historyChart: echarts.ECharts | null = null
let interestChart: echarts.ECharts | null = null
let chartResizeObserver: ResizeObserver | null = null

const traceId = ref(String(route.query.traceId || ''))
const userId = ref(Number(route.query.userId || 0))
const targetBookId = ref(Number(route.query.bookId || 0))
const targetBookName = ref(String(route.query.bookName || ''))
const profileTab = ref('interest')
const isBookMode = computed(() => false)
const selectedBookRow = computed(() => {
  if (!trace.value || !isBookMode.value) return null
  return (trace.value.recommendSnapshot || []).find((x) => Number(x.bookId) === targetBookId.value) || null
})

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
    hybrid_strategy: '混合策略（Hybrid）',
    user_cf: '用户协同',
    item_cf: '物品协同',
    content_based: '内容推荐',
    association_rule: '关联规则',
    lfm: '隐语义模型'
  }
  return map[strategy] || strategy
}

const strategyHint = (strategy: string) => {
  const map: Record<string, string> = {
    hybrid_strategy: '融合多路信号，平衡准确率与多样性。',
    user_cf: '基于相似读者的历史行为进行匹配。',
    item_cf: '根据与你关注书籍相近的内容进行联想。',
    content_based: '依据书籍内容特征与兴趣标签对齐。',
    association_rule: '利用共现与行为关联关系补充推荐。',
    lfm: '通过隐语义兴趣向量捕捉潜在偏好。'
  }
  return map[strategy] || '该策略参与了本次综合推荐计算。'
}

const statText = (key: string) => {
  const map: Record<string, string> = {
    totalScores: '累计评分次数',
    totalLikes: '累计点赞次数',
    distinctInterestCategories: '兴趣分类数',
    recent30dScores: '近30天评分',
    recent30dLikes: '近30天点赞'
  }
  return map[key] || key
}

const contributionText = (key: string) => {
  const map: Record<string, string> = {
    historyPref: '历史高分偏好',
    likeCategoryShare: '兴趣分类匹配',
    similarUserBehavior: '相似用户行为',
    contentSimilarity: '内容相似度',
    popularity: '热度信号'
  }
  return map[key] || key
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
      return { title: '权重分配', desc: '多算法权重已计算并归一化' }
    }
    if (step.startsWith('result=')) {
      const result = step.split('=')[1] || ''
      return { title: '结果生成', desc: result === 'hit' ? '命中推荐结果' : '触发冷启动兜底' }
    }
    return { title: '决策步骤', desc: step }
  })
})

const renderScoreChart = () => {
  if (!scoreChartRef.value) return
  scoreChart = scoreChart || echarts.init(scoreChartRef.value)
  const entries = Object.entries(profile.value.scoreRangeDistribution || {})
  scoreChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 26, right: 18, top: 18, bottom: 22, containLabel: true },
    xAxis: {
      type: 'category',
      data: entries.map(([k]) => k),
      axisLine: { lineStyle: { color: '#97a39b' } },
      axisTick: { show: false },
      axisLabel: { color: '#4f5b53', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#ecefea' } },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#4f5b53', fontSize: 11 }
    },
    series: [
      {
        name: '评分区间分布',
        type: 'bar',
        barMaxWidth: 34,
        data: entries.length > 0 ? entries.map(([, value]) => value) : [0],
        itemStyle: { color: '#3e7a68', borderRadius: [6, 6, 0, 0] }
      }
    ]
  })
}

const renderHistoryChart = () => {
  if (!historyChartRef.value) return
  historyChart = historyChart || echarts.init(historyChartRef.value)
  const entries = Object.entries(profile.value.historyBehaviorStats || {})
  historyChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 90, right: 18, top: 14, bottom: 14, containLabel: true },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#ecefea' } },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#4f5b53', fontSize: 11 }
    },
    yAxis: {
      type: 'category',
      data: entries.length > 0 ? entries.map(([name]) => statText(name)) : ['暂无数据'],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#4f5b53', fontSize: 11 }
    },
    series: [
      {
        name: '历史行为统计',
        type: 'bar',
        barMaxWidth: 20,
        data: entries.length > 0 ? entries.map(([, value]) => value) : [0],
        itemStyle: { color: '#3f8b56', borderRadius: [0, 6, 6, 0] }
      }
    ]
  })
}

const renderInterestChart = () => {
  if (!interestChartRef.value) return
  interestChart = interestChart || echarts.init(interestChartRef.value)
  const entries = Object.entries(profile.value.interestCategoryDistribution || {})
  interestChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        if (!params || !params.length) return ''
        const row = params[0]
        return `${row.name}: ${Math.round((row.value || 0) * 100)}%`
      }
    },
    grid: { left: 90, right: 18, top: 14, bottom: 14, containLabel: true },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#ecefea' } },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#4f5b53',
        fontSize: 11,
        formatter: (v: number) => `${Math.round(v * 100)}%`
      }
    },
    yAxis: {
      type: 'category',
      data: entries.length > 0 ? entries.map(([name]) => name) : ['暂无数据'],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#4f5b53', fontSize: 11 }
    },
    series: [
      {
        type: 'bar',
        barMaxWidth: 18,
        data: entries.length > 0 ? entries.map(([, value]) => value) : [0],
        itemStyle: { color: '#3f8b56', borderRadius: [0, 6, 6, 0] }
      }
    ]
  })
}

const renderCharts = async () => {
  await nextTick()
  if (profileTab.value === 'interest') {
    renderInterestChart()
    interestChart?.resize()
  } else if (profileTab.value === 'score') {
    renderScoreChart()
    scoreChart?.resize()
  } else {
    renderHistoryChart()
    historyChart?.resize()
  }
}

const onProfileTabChange = async () => {
  await renderCharts()
}

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
  if (idFromCookie) {
    userId.value = Number(idFromCookie)
  }
}

const fetchLatest = async () => {
  if (!userId.value) return
  const res = await myAxios.get('/agent/decisionTrace/latest', {
    params: { userId: userId.value, limit: 3 }
  })
  latest.value = res.data?.data || []
}

const fetchProfile = async () => {
  if (!userId.value) return
  const res = await myAxios.get('/agent/profile/insight', {
    params: { userId: userId.value }
  })
  profile.value = res.data?.data || profile.value
}

const reloadAll = async () => {
  try {
    loading.value = true
    await fetchTrace()
    resolveUserId()
    await Promise.all([fetchLatest(), fetchProfile()])
    await renderCharts()
  } catch {
    ElMessage.error('决策数据加载失败')
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
      userId: String(userId.value || ''),
      scope: 'all'
    }
  })
}

onMounted(async () => {
  await reloadAll()
  window.addEventListener('resize', renderCharts)
  chartResizeObserver = new ResizeObserver(() => {
    interestChart?.resize()
    scoreChart?.resize()
    historyChart?.resize()
  })
  if (interestChartRef.value) chartResizeObserver.observe(interestChartRef.value)
  if (scoreChartRef.value) chartResizeObserver.observe(scoreChartRef.value)
  if (historyChartRef.value) chartResizeObserver.observe(historyChartRef.value)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', renderCharts)
  chartResizeObserver?.disconnect()
  chartResizeObserver = null
  interestChart?.dispose()
  scoreChart?.dispose()
  historyChart?.dispose()
  interestChart = null
  scoreChart = null
  historyChart = null
})
</script>

<style scoped>
.trace-page {
  height: 100%;
  min-height: 0;
  padding: 12px;
  background: #f7f7f5;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 10px;
  overflow-y: auto;
  overflow-x: hidden;
}
.card { background: #fff; border: 1px solid #dcded9; border-radius: 8px; padding: 10px; min-height: 0; }
.top { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.top h2 { margin: 0; font-size: 20px; }
.top p { margin: 4px 0 0; color: #666; font-size: 13px; }
.ops { display: flex; gap: 8px; }
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
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}
:deep(.trace-btn-refresh.el-button:active) {
  background: #111;
  border-color: #111;
  color: #fff;
  transform: translateY(1px);
}
:deep(.trace-btn.el-button.is-loading),
:deep(.trace-btn.el-button.is-disabled) {
  opacity: 0.85;
}
.grid {
  min-height: 0;
  overflow: hidden;
  overflow-x: hidden;
  display: grid;
  gap: 10px;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: minmax(250px, 1.15fr) minmax(0, 1fr) minmax(0, 1fr);
  grid-template-areas:
    "strategy flow"
    "profile latest"
    "profile explain";
}
.single-grid {
  min-height: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  overflow-y: auto;
  overflow-x: hidden;
  align-content: start;
}
.single-card {
  min-height: 180px;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 8px;
  overflow: hidden;
}
.single-card h3 {
  margin: 0;
  font-size: 16px;
}
.single-reason {
  font-size: 14px;
  line-height: 1.6;
  color: #35443b;
}
.strategy-list {
  display: grid;
  gap: 8px;
  align-content: start;
}
.strategy-item {
  border: 1px solid #d7e2dc;
  border-radius: 10px;
  background: linear-gradient(180deg, #f7fbf9 0%, #f2f7f4 100%);
  padding: 8px 10px;
}
.strategy-item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #1f3f35;
}
.strategy-index {
  min-width: 18px;
  height: 18px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #2f6b57;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
}
.strategy-item-desc {
  margin: 6px 0 0;
  color: #4e5f57;
  font-size: 12px;
  line-height: 1.45;
}
.feature-list {
  display: grid;
  gap: 8px;
  align-content: start;
}
.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 6px 8px;
  border: 1px solid #e3ebe6;
  border-radius: 8px;
  background: #fafcfb;
  font-size: 13px;
  color: #30443b;
  line-height: 1.45;
}
.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3e7a68;
  margin-top: 5px;
  flex: 0 0 auto;
}
.area-strategy { grid-area: strategy; }
.area-flow { grid-area: flow; }
.area-explain { grid-area: explain; }
.area-latest { grid-area: latest; }
.area-profile { grid-area: profile; }
.section {
  min-height: 0;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 8px;
}
.section h3 { margin: 0; font-size: 15px; }
.section-body { min-height: 0; overflow-y: auto; overflow-x: hidden; padding-right: 2px; }
.area-strategy .section-body {
  overflow: hidden;
  padding-bottom: 0;
}
.area-profile .section-body { overflow: hidden; display: flex; min-height: 0; }
.line { display: flex; justify-content: space-between; margin-top: 6px; font-size: 13px; gap: 8px; }
.weights { margin-top: 8px; display: grid; gap: 6px; }
.weight-row { display: grid; grid-template-columns: 110px 1fr 46px; gap: 8px; align-items: center; font-size: 12px; }
.bar-wrap { height: 7px; border-radius: 99px; background: #ecefea; }
.bar { height: 100%; border-radius: 99px; background: #3e7a68; }
.bar.green { background: #3f8b56; }
.flow-wrap { display: flex; flex-wrap: wrap; align-items: stretch; gap: 8px; overflow-x: hidden; }
.flow-node { min-width: 0; flex: 1 1 180px; max-width: 100%; border: 1px solid #dfe7e1; background: #f8fbf9; border-radius: 8px; padding: 8px; }
.flow-title { font-size: 13px; font-weight: 700; color: #2f5f52; }
.flow-desc { margin-top: 4px; font-size: 12px; color: #4e594e; line-height: 1.4; }
.flow-arrow { display: flex; align-items: center; color: #3e7a68; font-weight: 700; }
.book-row { border: 1px solid #ecefea; border-radius: 6px; padding: 8px; margin-top: 8px; }
.book-name { font-weight: 700; font-size: 13px; }
.reason { color: #4e594e; font-size: 12px; margin-top: 4px; word-break: break-word; }
.chips { margin-top: 6px; display: flex; flex-wrap: wrap; gap: 6px; }
.chip { background: #f0f5f2; border: 1px solid #d8e6de; border-radius: 999px; padding: 2px 8px; font-size: 12px; }
.latest-item { margin-top: 8px; border-bottom: 1px dashed #e5e9e6; padding-bottom: 6px; font-size: 13px; }
.sub-title { margin-top: 10px; font-size: 12px; color: #677267; font-weight: 700; }
.empty { color: #9aa39a; font-size: 13px; margin-top: 6px; }
.empty-note { color: #7f8a83; font-size: 13px; }
.chart { width: 100%; max-width: 100%; height: 240px; margin-top: 6px; }
.profile-tabs { flex: 1; min-height: 0; display: flex; flex-direction: column; width: 100%; }
.profile-tabs :deep(.el-tabs__header) { flex: 0 0 auto; margin: 0 0 8px; }
.profile-tabs :deep(.el-tabs__content) { flex: 1; min-height: 0; overflow: hidden; display: flex; }
.profile-tabs :deep(.el-tab-pane) { flex: 1; min-height: 0; }
.chart-stage { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.profile-tabs .chart { width: calc(100% - 8px); height: calc(100% - 8px); margin: 0; }
.profile-tabs :deep(.el-tabs__item:hover) { color: #000; }
.profile-tabs :deep(.el-tabs__item.is-active) { color: #000; }
.profile-tabs :deep(.el-tabs__active-bar) { background-color: #000; }
@media (max-width: 1100px) {
  .trace-page { grid-template-rows: auto 1fr; }
  .single-grid {
    grid-template-columns: 1fr;
    overflow-y: auto;
    overflow-x: hidden;
  }
  .grid {
    grid-template-columns: 1fr;
    grid-template-rows: minmax(280px, auto) repeat(4, minmax(220px, auto));
    grid-template-areas:
      "strategy"
      "flow"
      "profile"
      "latest"
      "explain";
    overflow-y: auto;
    overflow-x: hidden;
  }
}
</style>
