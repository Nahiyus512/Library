<template>
  <div class="agent-analytics-page">
    <div class="toolbar card">
      <div class="toolbar-left">
        <el-select v-model="days" placeholder="时间范围" style="width: 160px">
          <el-option label="近7天" :value="7" />
          <el-option label="近30天" :value="30" />
          <el-option label="近90天" :value="90" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button color="#000" @click="loadSummary">查询</el-button>
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
        <div class="chart-title">智能体调用量</div>
        <div ref="callsChartRef" class="chart"></div>
      </div>
      <div class="card chart-box">
        <div class="chart-title">调用占比</div>
        <div ref="shareChartRef" class="chart"></div>
      </div>
    </div>

    <div class="detail-grid">
      <div class="card chart-box latency-box">
        <div class="chart-title">平均响应延迟雷达图</div>
        <div ref="latencyChartRef" class="chart"></div>
      </div>

      <div class="card table-box">
        <div class="table-title">智能体明细</div>
        <el-table :data="displayAgents" height="100%" stripe border>
          <el-table-column prop="agentNameZh" label="智能体名称" min-width="220" />
          <el-table-column prop="calls" label="调用次数" width="130" align="center" />
          <el-table-column label="成功率" width="120" align="center">
            <template #default="{ row }">{{ percent(row.successRate) }}</template>
          </el-table-column>
          <el-table-column label="平均响应时延" width="150" align="center">
            <template #default="{ row }">{{ duration(row.avgResponseTimeMs) }}</template>
          </el-table-column>
        </el-table>
      </div>
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

interface AgentMetricItem {
  agentName: string
  calls: number
  successRate: number
  avgResponseTimeMs: number
}

interface AgentMetricSummary {
  days: number
  totalCalls: number
  agents: AgentMetricItem[]
}

type DisplayAgent = AgentMetricItem & {
  agentNameZh: string
}

const AGENT_NAME_MAP: Record<string, string> = {
  featured_recommend_agent: '特色推荐智能体',
  system_catalog_search_agent: '馆藏检索智能体',
  user_data_query_agent: '用户数据查询智能体',
  preference_manage_agent: '偏好管理智能体',
  score_manage_agent: '评分管理智能体',
  book_suggestion_agent: '图书建议智能体',
  feedback_advice_agent: '反馈建议智能体',
  unknown: '未知智能体'
}

const formatAgentName = (name: string) => AGENT_NAME_MAP[name] || name

const days = ref(7)
const summary = ref<AgentMetricSummary>({
  days: 7,
  totalCalls: 0,
  agents: []
})

const callsChartRef = ref<HTMLElement | null>(null)
const shareChartRef = ref<HTMLElement | null>(null)
const latencyChartRef = ref<HTMLElement | null>(null)
let callsChart: echarts.ECharts | null = null
let shareChart: echarts.ECharts | null = null
let latencyChart: echarts.ECharts | null = null

const agents = computed(() => summary.value.agents || [])
const displayAgents = computed<DisplayAgent[]>(() =>
  agents.value.map((item) => ({
    ...item,
    agentNameZh: formatAgentName(item.agentName || 'unknown')
  }))
)

const totalCalls = computed(() => summary.value.totalCalls || 0)
const avgSuccessRate = computed(() => {
  if (!agents.value.length) return 0
  const weightedSuccess = agents.value.reduce((acc, item) => acc + (item.successRate || 0) * (item.calls || 0), 0)
  return totalCalls.value > 0 ? weightedSuccess / totalCalls.value : 0
})
const avgLatencyMs = computed(() => {
  if (!agents.value.length) return 0
  const weightedLatency = agents.value.reduce((acc, item) => acc + (item.avgResponseTimeMs || 0) * (item.calls || 0), 0)
  return totalCalls.value > 0 ? Math.round(weightedLatency / totalCalls.value) : 0
})
const topAgent = computed(() => displayAgents.value[0]?.agentNameZh || '-')

const percent = (v: number) => `${(Number(v || 0) * 100).toFixed(1)}%`
const duration = (ms: number) => `${Math.round(Number(ms || 0))} ms`

const kpiCards = computed(() => [
  { label: '统计周期', value: `${summary.value.days} 天` },
  { label: '总调用次数', value: `${totalCalls.value}` },
  { label: '平均成功率', value: percent(avgSuccessRate.value) },
  { label: '平均响应时延', value: duration(avgLatencyMs.value) },
  { label: '调用最多智能体', value: topAgent.value }
])

const renderCallsChart = () => {
  if (!callsChartRef.value) return
  callsChart = callsChart || echarts.init(callsChartRef.value)
  callsChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 60 },
    xAxis: {
      type: 'category',
      data: displayAgents.value.map((x) => x.agentNameZh),
      axisLabel: { rotate: 25 }
    },
    yAxis: [
      { type: 'value', name: '调用次数' },
      { type: 'value', name: '成功率', min: 0, max: 1 }
    ],
    series: [
      {
        name: '调用次数',
        type: 'bar',
        yAxisIndex: 0,
        itemStyle: { color: '#111' },
        data: displayAgents.value.map((x) => x.calls)
      },
      {
        name: '成功率',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: displayAgents.value.map((x) => x.successRate)
      }
    ]
  })
}

const renderShareChart = () => {
  if (!shareChartRef.value) return
  shareChart = shareChart || echarts.init(shareChartRef.value)
  shareChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['35%', '70%'],
        data: displayAgents.value.map((item) => ({
          name: item.agentNameZh,
          value: item.calls
        }))
      }
    ]
  })
}

const renderLatencyChart = () => {
  if (!latencyChartRef.value) return
  latencyChart = latencyChart || echarts.init(latencyChartRef.value)
  if (!displayAgents.value.length) {
    latencyChart.clear()
    return
  }
  const maxLatency = Math.max(...displayAgents.value.map((x) => Number(x.avgResponseTimeMs || 0)), 100)
  latencyChart.setOption({
    tooltip: { trigger: 'item' },
    radar: {
      center: ['50%', '53%'],
      radius: '66%',
      indicator: displayAgents.value.map((x) => ({
        name: x.agentNameZh,
        max: Math.ceil(maxLatency * 1.2)
      })),
      axisName: { color: '#555' },
      splitArea: { areaStyle: { color: ['#fafafa', '#f5f5f5'] } }
    },
    series: [
      {
        name: '平均响应时延',
        type: 'radar',
        data: [
          {
            name: '平均响应时延',
            value: displayAgents.value.map((x) => Number(x.avgResponseTimeMs || 0))
          }
        ],
        lineStyle: { width: 2, color: '#222' },
        areaStyle: { color: 'rgba(0, 0, 0, 0.18)' },
        symbol: 'circle',
        symbolSize: 6
      }
    ]
  })
}

const loadSummary = async () => {
  try {
    const res = await myAxios.get<ApiResp<AgentMetricSummary>>('/agent/metrics/summary', {
      params: { days: days.value }
    })
    if (res.data.code !== 200) {
      throw new Error(res.data.msg || '查询失败')
    }
    summary.value = res.data.data || summary.value
    await nextTick()
    renderCallsChart()
    renderShareChart()
    renderLatencyChart()
  } catch (e) {
    console.error(e)
    ElMessage.error('加载AgenticAI分析数据失败')
  }
}

const handleResize = () => {
  callsChart?.resize()
  shareChart?.resize()
  latencyChart?.resize()
}

watch(days, () => {
  loadSummary()
})

onMounted(async () => {
  await loadSummary()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  callsChart?.dispose()
  shareChart?.dispose()
  latencyChart?.dispose()
  callsChart = null
  shareChart = null
  latencyChart = null
})
</script>

<style scoped>
.agent-analytics-page {
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
  grid-template-columns: repeat(5, minmax(0, 1fr));
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
  grid-template-columns: 1.3fr 1fr;
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

.detail-grid {
  display: grid;
  grid-template-columns: 0.9fr 1.1fr;
  gap: 10px;
  min-height: 0;
}

.latency-box {
  min-height: 0;
}

@media (max-width: 1300px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .charts-grid {
    grid-template-columns: 1fr;
    height: auto;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
