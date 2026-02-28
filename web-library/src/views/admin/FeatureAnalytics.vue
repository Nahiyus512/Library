<template>
  <div class="feature-analytics-page">
    <div class="toolbar card">
      <div class="toolbar-left">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          range-separator="至"
        />
        <el-select v-model="bookIdForFunnel" clearable placeholder="漏斗书籍（可选）" style="width: 220px">
          <el-option v-for="book in bookOptions" :key="book.bookId || book.bookName" :label="book.bookName" :value="book.bookId" />
        </el-select>
        <el-select v-model="bookSort" placeholder="榜单排序" style="width: 180px">
          <el-option label="接受率" value="acceptRate" />
          <el-option label="完成率" value="completeRate" />
          <el-option label="进入人数（去重）" value="enterUv" />
          <el-option label="平均停留" value="avgDurationMs" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button color="#000" @click="loadAll">查询</el-button>
      </div>
    </div>

    <div class="kpi-grid">
      <div class="kpi card" v-for="k in kpiCards" :key="k.label">
        <div class="kpi-label">{{ k.label }}</div>
        <div class="kpi-value">{{ k.value }}</div>
      </div>
    </div>

    <div class="charts-grid">
      <div class="card chart-box">
        <div class="chart-title">趋势</div>
        <div ref="trendChartRef" class="chart"></div>
      </div>
      <div class="card chart-box">
        <div class="chart-title">漏斗</div>
        <div ref="funnelChartRef" class="chart"></div>
      </div>
      <div class="card chart-box">
        <div class="chart-title">来源分布</div>
        <div ref="sourceChartRef" class="chart"></div>
      </div>
    </div>

    <div class="card table-box">
      <div class="table-title">书籍榜单</div>
      <el-table :data="books" height="100%" stripe border>
        <el-table-column prop="bookName" label="书名" min-width="180" />
        <el-table-column prop="enterUv" label="进入人数（去重）" width="140" align="center" />
        <el-table-column prop="enterPv" label="进入次数" width="110" align="center" />
        <el-table-column label="完成率" width="120" align="center">
          <template #default="{ row }">{{ percent(row.completeRate) }}</template>
        </el-table-column>
        <el-table-column label="平均停留" width="130" align="center">
          <template #default="{ row }">{{ duration(row.avgDurationMs) }}</template>
        </el-table-column>
        <el-table-column label="评分分布(0/1/2)" width="170" align="center">
          <template #default="{ row }">{{ `${row.like0Count}/${row.like1Count}/${row.like2Count}` }}</template>
        </el-table-column>
        <el-table-column label="接受率" width="120" align="center">
          <template #default="{ row }">{{ percent(row.acceptRate) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import myAxios from '@/api/index'
import { ElMessage } from 'element-plus'

interface ApiResp<T> {
  code: number
  msg?: string
  data: T
}

interface Overview {
  enterUv: number
  enterRate: number
  completeRate: number
  rateSubmitRate: number
  avgDurationMs: number
  like2Rate: number
  acceptRate24h: number
}

interface TrendPoint {
  date: string
  enterUv: number
  completeRate: number
  rateSubmitRate: number
  acceptRate24h: number
}

interface BookItem {
  bookId: number
  bookName: string
  enterUv: number
  enterPv: number
  completeRate: number
  avgDurationMs: number
  like0Count: number
  like1Count: number
  like2Count: number
  acceptRate: number
}

interface Funnel {
  enter: number
  checkpoint: number
  rate: number
  recommendAccept: number
}

interface SourceItem {
  source: string
  enterPv: number
  trafficShare: number
  completeRate: number
  acceptRate: number
}

const today = new Date()
const sevenDaysAgo = new Date(today)
sevenDaysAgo.setDate(today.getDate() - 6)

const formatDate = (d: Date) => {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const dateRange = ref<[string, string]>([formatDate(sevenDaysAgo), formatDate(today)])
const bookSort = ref('acceptRate')
const bookIdForFunnel = ref<number | undefined>(undefined)

const overview = ref<Overview>({
  enterUv: 0,
  enterRate: 0,
  completeRate: 0,
  rateSubmitRate: 0,
  avgDurationMs: 0,
  like2Rate: 0,
  acceptRate24h: 0
})
const trend = ref<TrendPoint[]>([])
const books = ref<BookItem[]>([])
const funnel = ref<Funnel>({ enter: 0, checkpoint: 0, rate: 0, recommendAccept: 0 })
const sources = ref<SourceItem[]>([])

const trendChartRef = ref<HTMLElement | null>(null)
const funnelChartRef = ref<HTMLElement | null>(null)
const sourceChartRef = ref<HTMLElement | null>(null)
let trendChart: echarts.ECharts | null = null
let funnelChart: echarts.ECharts | null = null
let sourceChart: echarts.ECharts | null = null

const from = computed(() => dateRange.value?.[0] || formatDate(sevenDaysAgo))
const to = computed(() => dateRange.value?.[1] || formatDate(today))
const bookOptions = computed(() => books.value.filter((b) => !!b.bookId))

const percent = (v: number) => `${(Number(v || 0) * 100).toFixed(1)}%`
const duration = (ms: number) => `${Math.round((Number(ms || 0) / 1000))}秒`

const kpiCards = computed(() => [
  { label: '进入人数（去重）', value: `${overview.value.enterUv}` },
  { label: '阅读决策来源进入率', value: percent(overview.value.enterRate) },
  { label: '体验完成率', value: percent(overview.value.completeRate) },
  { label: '评分提交率', value: percent(overview.value.rateSubmitRate) },
  { label: '平均停留', value: duration(overview.value.avgDurationMs) },
  { label: '高分率', value: percent(overview.value.like2Rate) },
  { label: '24小时接受率', value: percent(overview.value.acceptRate24h) }
])

const sourceLabelMap: Record<string, string> = {
  reading_decision_card: '阅读决策-卡片',
  reading_decision_feature_stage: '阅读决策-特色阶段',
  category_page: '分类页',
  direct: '直达'
}

const sourceLabel = (source: string) => sourceLabelMap[source] || source

const renderTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = trendChart || echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['进入人数', '完成率', '评分提交率', '24小时接受率'] },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: trend.value.map((x) => x.date) },
    yAxis: [
      { type: 'value', name: '人数' },
      { type: 'value', name: '比率', min: 0, max: 1 }
    ],
    series: [
      { name: '进入人数', type: 'line', yAxisIndex: 0, data: trend.value.map((x) => x.enterUv) },
      { name: '完成率', type: 'line', yAxisIndex: 1, data: trend.value.map((x) => x.completeRate) },
      { name: '评分提交率', type: 'line', yAxisIndex: 1, data: trend.value.map((x) => x.rateSubmitRate) },
      { name: '24小时接受率', type: 'line', yAxisIndex: 1, data: trend.value.map((x) => x.acceptRate24h) }
    ]
  })
}

const renderFunnelChart = () => {
  if (!funnelChartRef.value) return
  funnelChart = funnelChart || echarts.init(funnelChartRef.value)
  funnelChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: ['进入', '关键交互', '评分', '接受推荐']
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: [funnel.value.enter, funnel.value.checkpoint, funnel.value.rate, funnel.value.recommendAccept],
        itemStyle: { color: '#111' }
      }
    ]
  })
}

const renderSourceChart = () => {
  if (!sourceChartRef.value) return
  sourceChart = sourceChart || echarts.init(sourceChartRef.value)
  sourceChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['35%', '70%'],
        data: sources.value.map((s) => ({
          name: sourceLabel(s.source),
          value: s.enterPv
        }))
      }
    ]
  })
}

const loadAll = async () => {
  try {
    const [overviewRes, trendRes, booksRes, sourceRes] = await Promise.all([
      myAxios.get<ApiResp<Overview>>('/feature/dashboard/overview', { params: { from: from.value, to: to.value } }),
      myAxios.get<ApiResp<TrendPoint[]>>('/feature/dashboard/trend', { params: { from: from.value, to: to.value, granularity: 'day' } }),
      myAxios.get<ApiResp<BookItem[]>>('/feature/dashboard/books', { params: { from: from.value, to: to.value, sort: bookSort.value, limit: 20 } }),
      myAxios.get<ApiResp<SourceItem[]>>('/feature/dashboard/source', { params: { from: from.value, to: to.value } })
    ])

    overview.value = overviewRes.data.data || overview.value
    trend.value = trendRes.data.data || []
    books.value = booksRes.data.data || []
    sources.value = sourceRes.data.data || []
    await loadFunnel()
    await nextTick()
    renderTrendChart()
    renderFunnelChart()
    renderSourceChart()
  } catch (e) {
    console.error(e)
    ElMessage.error('加载特色页看板失败')
  }
}

const loadFunnel = async () => {
  const res = await myAxios.get<ApiResp<Funnel>>('/feature/dashboard/funnel', {
    params: {
      bookId: bookIdForFunnel.value,
      from: from.value,
      to: to.value
    }
  })
  funnel.value = res.data.data || funnel.value
}

const handleResize = () => {
  trendChart?.resize()
  funnelChart?.resize()
  sourceChart?.resize()
}

watch(bookIdForFunnel, async () => {
  await loadFunnel()
  renderFunnelChart()
})

onMounted(async () => {
  await loadAll()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  funnelChart?.dispose()
  sourceChart?.dispose()
  trendChart = null
  funnelChart = null
  sourceChart = null
})
</script>

<style scoped>
.feature-analytics-page {
  height: 100%;
  padding: 20px;
  background: #f5f7fa;
  display: grid;
  grid-template-rows: auto auto 320px minmax(0, 1fr);
  gap: 14px;
  box-sizing: border-box;
}

.card {
  background: #fff;
  border: 1px solid #e7e7e7;
  border-radius: 8px;
}

.toolbar {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 10px;
}

.kpi {
  padding: 12px;
}

.kpi-label {
  font-size: 12px;
  color: #7b7b7b;
}

.kpi-value {
  margin-top: 6px;
  font-size: 20px;
  font-weight: 700;
  color: #111;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1.3fr 1fr 1fr;
  gap: 10px;
}

.chart-box {
  padding: 10px;
  display: flex;
  flex-direction: column;
}

.chart-title,
.table-title {
  font-size: 14px;
  font-weight: 600;
  color: #222;
  margin-bottom: 8px;
}

.chart {
  flex: 1;
  min-height: 220px;
}

.table-box {
  padding: 10px;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

@media (max-width: 1300px) {
  .kpi-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
  .charts-grid {
    grid-template-columns: 1fr;
    height: auto;
  }
}
</style>
