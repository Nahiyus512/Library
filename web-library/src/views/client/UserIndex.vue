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

    <!-- User Activity Stats Card -->
    <div class="card stats-card">
      <div class="card-header">
        <h3>我的动态</h3>
        <span class="card-meta">累计</span>
      </div>
      <div class="card-body stats-body">
        <div class="stat-item">
          <span class="stat-value">{{ likeStats.totalLikes }}</span>
          <span class="stat-label">收藏书籍</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ likeStats.totalComments }}</span>
          <span class="stat-label">发表评论</span>
        </div>
      </div>
    </div>

    <!-- System Stats Card -->
    <div class="card list-card">
      <div class="card-header">
        <h3>馆藏概览</h3>
      </div>
      <div class="card-body">
        <ul class="clean-list">
          <li>
            <span class="rank"><el-icon size="20"><Reading /></el-icon></span>
            <span class="text">馆藏书籍: {{ systemStats.totalBooks }} 本</span>
          </li>
          <li>
            <span class="rank"><el-icon size="20"><Collection /></el-icon></span>
            <span class="text">书籍分类: {{ systemStats.totalCategories }} 类</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Like Distribution Chart Card (Moved to Top Row) -->
    <div class="card chart-card-small">
      <div class="card-header">
        <h3>收藏偏好</h3>
      </div>
      <div class="card-body">
         <div ref="chartContainer" class="chart-container-small"></div>
      </div>
    </div>

    <!-- Bottom Row: Leaderboards -->
    <!-- Top Liked Books -->
    <div class="card list-card leaderboard-card">
      <div class="card-header">
        <h3>图书喜爱榜</h3>
        <span class="card-meta">TOP 10</span>
      </div>
      <div class="card-body">
        <ul class="clean-list">
          <li v-for="(book, index) in topLikedBooks" :key="book.bookId">
            <span class="rank" :class="index < 3 ? 'top-rank' : ''">{{ String(index + 1).padStart(2, '0') }}</span>
            <span class="text">{{ book.bookName }}</span>
            <span class="badge love">{{ book.count }} 喜欢</span>
          </li>
          <li v-if="topLikedBooks.length === 0" class="empty-state">暂无数据</li>
        </ul>
      </div>
    </div>

    <!-- Top Commented Books -->
    <div class="card list-card leaderboard-card">
      <div class="card-header">
        <h3>书籍热评榜</h3>
        <span class="card-meta">TOP 10</span>
      </div>
      <div class="card-body">
        <ul class="clean-list">
          <li v-for="(book, index) in topCommentedBooks" :key="book.bookId">
            <span class="rank" :class="index < 3 ? 'top-rank' : ''">{{ String(index + 1).padStart(2, '0') }}</span>
            <span class="text">{{ book.bookName }}</span>
            <span class="badge comment">{{ book.count }} 评论</span>
          </li>
          <li v-if="topCommentedBooks.length === 0" class="empty-state">暂无数据</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive, nextTick } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';
import myAxios from '@/api/index';
import { useCookies } from '@vueuse/integrations/useCookies';

const cookie = useCookies();

import { Collection, Reading } from '@element-plus/icons-vue';

// Weather Data
const weather = reactive({
  condition: 'Sunny',
  temperature: '25',
});

const apiKey = '5ecb7642635132e69a12e2e419805bff';

// Stats Data
const likeStats = reactive({
  totalLikes: 0,
  totalComments: 0,
});

const systemStats = reactive({
  totalBooks: 0,
  totalCategories: 0,
});

const likedBooks = ref<any[]>([]);
const topLikedBooks = ref<any[]>([]);
const topCommentedBooks = ref<any[]>([]);

const chartContainer = ref<HTMLDivElement | null>(null);
let myChart: echarts.ECharts | null = null;

const initChart = (data: { name: string, value: number }[]) => {
  if (!chartContainer.value) return;
  
  if (myChart) {
    myChart.dispose();
  }

  myChart = echarts.init(chartContainer.value);
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: '5%',
      top: 'center'
    },
    series: [
      {
        name: '收藏分类',
        type: 'pie',
        radius: ['50%', '80%'],
        center: ['65%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 5,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: false
          }
        },
        labelLine: {
          show: false
        },
        data: data.length > 0 ? data : [{ value: 0, name: '暂无数据' }]
      }
    ]
  };

  myChart.setOption(option);
  
  window.addEventListener('resize', () => {
    myChart?.resize();
  });
};

onMounted(async () => {
  await fetchData();
  fetchWeatherData(apiKey, '440600'); // Foshan Adcode
});

const fetchData = async () => {
  const username = cookie.get('username');
  if (!username) return;

  try {
    // 1. Get User Info to get ID
    const userRes = await myAxios.get(`/user/getUserByName?name=${username}`);
    if (userRes.data.code === 200) {
      const userId = userRes.data.data.id;
      
      // 2. Get User Likes
      const likeRes = await myAxios.get(`/bookLike/list?userId=${userId}`);
      if (likeRes.data.code === 200) {
        likedBooks.value = likeRes.data.data;
        likeStats.totalLikes = likedBooks.value.length;
        
        // Process data for chart (Group by Category)
        const categoryMap = new Map<string, number>();
        likedBooks.value.forEach(book => {
          const category = book.bookClassify || '其他';
          categoryMap.set(category, (categoryMap.get(category) || 0) + 1);
        });
        
        const chartData = Array.from(categoryMap.entries()).map(([name, value]) => ({ name, value }));
        initChart(chartData);
      }
    }

    // 3. Get User Comments
    const commentRes = await myAxios.get(`/advice/get?userName=${username}`);
    if (commentRes.data.code === 200) {
      likeStats.totalComments = commentRes.data.data.length;
    }

    // 4. Get System Stats
    const booksRes = await myAxios.get('/book/all');
    if (booksRes.data.code === 200) {
      systemStats.totalBooks = booksRes.data.data.length;
    }
    
    const classRes = await myAxios.get('/class/get');
    if (classRes.data.code === 200) {
      systemStats.totalCategories = classRes.data.data.length;
    }

    // 5. Get Leaderboards
    const topLikeRes = await myAxios.get('/bookLike/top');
    if (topLikeRes.data.code === 200) {
      topLikedBooks.value = topLikeRes.data.data;
    }

    const topCommentRes = await myAxios.get('/advice/topCommented');
    if (topCommentRes.data.code === 200) {
      topCommentedBooks.value = topCommentRes.data.data;
    }

  } catch (e) {
    console.error('Error fetching dashboard data:', e);
  }
};

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
  border-radius: 12px;
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
  justify-content: space-between;
}

.clean-list .rank {
  font-family: 'Courier New', monospace;
  color: #ccc;
  margin-right: 15px;
  font-size: 12px;
}

.clean-list .text {
  font-weight: 500;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 10px;
}

.badge {
  font-size: 12px;
}

.empty-state {
  color: #999;
  font-size: 14px;
  text-align: center;
  margin-top: 20px;
}

/* Chart Card */
.chart-container-small {
  width: 100%;
  height: 200px;
}

/* Leaderboard Cards */
.leaderboard-card {
  grid-column: span 2;
  min-height: 450px;
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
