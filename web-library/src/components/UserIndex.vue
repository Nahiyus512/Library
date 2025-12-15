<template>
  <div class="dashboard-grid">
    <!-- Weather Card -->
    <div class="card weather-card">
      <div class="card-header">
        <h3>本地天气</h3>
        <span class="card-meta">佛山</span>
      </div>
      <div class="card-body">
        <div class="weather-info">
          <span class="temp">{{ weather.temperature }}°</span>
          <span class="condition">{{ weather.condition }}</span>
        </div>
      </div>
    </div>

    <!-- Borrow Stats Card -->
    <div class="card stats-card">
      <div class="card-header">
        <h3>今日活动</h3>
        <span class="card-meta">今日</span>
      </div>
      <div class="card-body stats-body">
        <div class="stat-item">
          <span class="stat-value">{{ borrowStats.todayBorrow }}</span>
          <span class="stat-label">借阅量</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ borrowStats.todayReturn }}</span>
          <span class="stat-label">归还量</span>
        </div>
      </div>
    </div>

    <!-- Popular Books Card -->
    <div class="card list-card">
      <div class="card-header">
        <h3>热门借阅</h3>
      </div>
      <div class="card-body">
        <ul class="clean-list">
          <li>
            <span class="rank">01</span>
            <span class="text">明日你好</span>
          </li>
          <li>
            <span class="rank">02</span>
            <span class="text">C++面向对象</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Recommended Card -->
    <div class="card list-card">
      <div class="card-header">
        <h3>馆长推荐</h3>
      </div>
      <div class="card-body">
        <ul class="clean-list">
          <li>
            <span class="rank">01</span>
            <span class="text">失落的阴影</span>
          </li>
          <li>
            <span class="rank">02</span>
            <span class="text">面向对象编程</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Traffic Chart Card (Full Width) -->
    <div class="card chart-card">
      <div class="card-header">
        <h3>访问流量</h3>
        <span class="card-meta">每日概览</span>
      </div>
      <div class="card-body">
         <div ref="chartContainer" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

// Weather Data
const weather = reactive({
  condition: 'Sunny',
  temperature: '25',
});

const apiKey = '5ecb7642635132e69a12e2e419805bff';

// Stats Data
const borrowStats = ref({
  todayBorrow: 88,
  todayReturn: 66,
});

// Chart Data
const morningVolume = 20;
const noonVolume = 80;
const afternoonVolume = 100;
const eveningVolume = 50;

const chartContainer = ref<HTMLDivElement | null>(null);

const initChart = () => {
  if (!chartContainer.value) return;
  
  const myChart = echarts.init(chartContainer.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#eee',
      textStyle: { color: '#000' }
    },
    grid: {
      top: '10%',
      left: '0%',
      right: '0%',
      bottom: '0%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['上午', '中午', '下午', '晚上'],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#999' }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: { type: 'dashed', color: '#eee' }
      },
      axisLabel: { color: '#999' }
    },
    series: [
      {
        name: '流量',
        type: 'bar',
        barWidth: '20%',
        data: [morningVolume, noonVolume, afternoonVolume, eveningVolume],
        itemStyle: {
          color: '#000'
        },
        showBackground: true,
        backgroundStyle: {
          color: '#f5f5f5'
        }
      },
    ],
  };

  myChart.setOption(option);
  
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

onMounted(() => {
  initChart();
  fetchWeatherData(apiKey, '440600'); // Foshan Adcode
});

async function fetchWeatherData(apiKey: string, cityCode: string) {
  const url = `https://restapi.amap.com/v3/weather/weatherInfo?key=${apiKey}&city=${cityCode}&extensions=all&output=json`;
  try {
    const response = await axios.get(url);
    if (response.data.status === '1' && response.data.forecasts?.[0]?.casts?.[0]) {
      const cast = response.data.forecasts[0].casts[0];
      weather.condition = cast.dayweather;
      weather.temperature = cast.daytemp;
    }
  } catch (error) {
    console.error('Weather API Error:', error);
  }
}
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: auto auto;
  gap: 24px;
  width: 100%;
}

.card {
  background: #fff;
  padding: 30px;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  min-height: 200px;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
}

.card-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.card-header h3 {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #999;
  margin: 0;
}

.card-meta {
  font-size: 12px;
  color: #ccc;
}

/* Weather Card */
.weather-card {
  grid-column: span 1;
}

.weather-info {
  display: flex;
  flex-direction: column;
}

.temp {
  font-size: 48px;
  font-weight: 300;
  line-height: 1;
}

.condition {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

/* Stats Card */
.stats-card {
  grid-column: span 1;
}

.stats-body {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 400;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* List Cards */
.list-card {
  grid-column: span 1;
}

.clean-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.clean-list li {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
}

.clean-list .rank {
  font-family: 'Courier New', monospace;
  color: #ccc;
  margin-right: 15px;
  font-size: 12px;
}

.clean-list .text {
  font-weight: 500;
}

/* Chart Card */
.chart-card {
  grid-column: span 4; /* Full width */
  min-height: 400px;
}

.chart-container {
  width: 100%;
  height: 350px;
}

@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-card {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  .chart-card {
    grid-column: span 1;
  }
}
</style>
