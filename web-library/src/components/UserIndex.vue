<template>
<div class="user-div">
  <div class="library-dashboard">
    <div class="left-container">
      <!-- 天气组件 -->
      <div class="weather-widget">
        <h3>邵阳气温</h3>
        <p>天气:{{ weather.condition }}</p>
        <p>温度:{{ weather.temperature }} ℃</p>
      </div>

      <div class="weather-widget">
        <h3>书本借阅排行</h3>
        <p>1:明天你好</p>
        <p>2:C++面向对象</p>
      </div>

      <div class="weather-widget">
        <h3>推荐图书</h3>
        <p>1:消失的影子</p>
        <p>2:面向对象</p>
      </div>
      <!-- 借阅统计组件 -->
      <div class="borrow-stats-widget">
        <h3>图书馆借还量</h3>
        <p>今日借书量: {{ borrowStats.todayBorrow }}</p>
        <p>今日还书量: {{ borrowStats.todayReturn }}</p>
      </div>
      <div class="left-container-bottom">
      </div>

      <div class="container-bottom-image">
        <img src="../assets/index-bottom.png" alt="图书" width="670">
      </div>
    </div>
    <!-- 这里可以继续添加其他组件内容 -->
    <div class="traffic-volume-container">
      <h2>今日人流量统计</h2>
      <div class="stats">
        <p>早晨: {{ morningVolume }}</p>
        <p>中午: {{ noonVolume }}</p>
        <p>下午: {{ afternoonVolume }}</p>
        <p>晚上: {{ eveningVolume }}</p>
      </div>
      <div ref="chartContainer" :style="{width: '100%', height: '400px'}"></div>
    </div>
  </div>

</div>
</template>

<script setup lang="ts">
import {onMounted, ref,reactive,watchEffect} from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';
import { useGeolocation } from '@vueuse/core'

const coordinates = ref<null | { latitude: number, longitude: number }>(null);
// 定义天气组件的数据
const weather = reactive({
  condition: '',
  temperature: '',
});

const apiKey = '5ecb7642635132e69a12e2e419805bff';//'你的高德地图API密钥'; // 替换为你的API密钥



// 定义借阅统计组件的数据
const borrowStats = ref({
  todayBorrow: 88,
  todayReturn: 66,
});


// 示例人流量数据
const morningVolume = 20;
const noonVolume = 80;
const afternoonVolume = 100;
const eveningVolume = 50;



const option = reactive({
  tooltip: {},
  legend: {
    data: ['人流量'],
  },
  xAxis: {
    type: 'category',
    data: ['早晨', '中午', '下午', '晚上'],
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: 200,
    interval: 40, // 可选，根据实际情况调整
  },
  splitLine: {
    show: true, // 显示分割线
  },
  series: [
    {
      name: '人流量',
      type: 'bar',
      data: [morningVolume, noonVolume, afternoonVolume, eveningVolume],
    },
  ],
});

const chartContainer = ref<HTMLDivElement | null>(null);



onMounted(() => {
  const chartDom = echarts.init(chartContainer.value as HTMLElement);
  chartDom.setOption(option);

  // 假设我们直接使用城市名称深圳作为示例，实际应用中应根据纬度经度查询城市名称
  const location = '邵阳'; // 示例城市，实际应用中应动态获取或输入

  // 动态获取地理位置并更新天气组件
  // 监听地理位置变化，当有有效值时更新天气信息
    fetchWeatherData(apiKey, location);
});


async function fetchCurrentLocationName(lat: number | undefined, lon: number | undefined): Promise<string | null> {
  if (lat === undefined || lon === undefined) return null;

  try {
    const url = `https://restapi.amap.com/v3/geocode/regeo?key=${apiKey}&location=${lon},${lat}`;
    const response = await axios.get(url);
    //console.log(response);
    if (response.data.status === '0' && response.data.regeocode.addressComponent.city) {
      return response.data.regeocode.addressComponent.city;
    } else {
      console.error('Failed to fetch location name:', response.data);
      return null;
    }
  } catch (error) {
    console.error('Error fetching location name:', error);
    return null;
  }
}

async function fetchWeatherData(apiKey: string, location: string) {
  const url = `https://restapi.amap.com/v3/weather/weatherInfo?key=${apiKey}&city=${location}&extensions=all&output=json`;
  try {
    const response = await axios.get(url);
    if (response.data.status === '1') {
      //console.log(response.data);
      weather.condition = response.data.forecasts[0].casts[0].dayweather;
      weather.temperature = response.data.forecasts[0].casts[0].daytemp;
    } else {
      console.error('Failed to fetch weather data:', response.data);
    }
  } catch (error) {
    console.error('Error fetching weather data:', error);
  }
}


</script>

<style scoped>

.user-div {
  width: 100%;
  height: 550px;
  max-width: 100vw; /* 确保在窄屏设备上不水平滚动 */
  background-color: #9edad4;
  /*display: flex;*/
  /*justify-content: center; !* 水平居中对齐 *!*/
  /*align-items: center; !* 垂直居中对齐，假设没有其他垂直内容 *!*/
  display: flex;
  justify-content: center; /* 保持水平居中 */
  align-items: flex-start; /* 修改为顶部对齐，取消垂直居中 */
}

.left-container {
  width: 60%;
  height: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  margin-top: 50px;
}

.library-dashboard {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: flex-start; /* 或根据需要调整 */
  gap: 10px;
  padding: 10px;
  width: 100%; /* 让容器随父元素宽度变化 */
  max-width: 90%; /* 防止过宽导致水平滚动 */
  margin: 0 auto; /* 居中 */
}

.stats {
  display: flex;
  width: 100%;
}
.stats p {
  margin-right: 10px;
}

/* 媒体查询适应小屏设备 */
@media (max-width: 768px) {
  .library-dashboard {
    flex-direction: column; /* 在小屏上恢复为垂直布局 */
  }
}

/* 确保ECharts图表容器不会导致滚动 */
.traffic-volume-container > div {
  max-height: 400px; /* 与现有高度相同，作为最大高度限制 */
  overflow: hidden; /* 超出部分隐藏 */
}
.weather-widget,
.borrow-stats-widget {
  background-color: #616672;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 3px 3px 5px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 300px;
}

.weather-widget h3,
.borrow-stats-widget h3 {
  margin-bottom: 5px;
}

.weather-widget p,
.borrow-stats-widget p {
  margin: 0;
}

.left-container-bottom {
  width: 670px;
  background-color: #a2a3a4;
  height: 10px;
  border-radius: 5px;
  box-shadow: 3px 3px 5px 6px rgba(0, 0, 0, 0.1);
}

.container-bottom-image {
  width: 670px;
}



</style>