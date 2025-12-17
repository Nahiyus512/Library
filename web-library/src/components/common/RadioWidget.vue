<template>
  <div class="radio-widget">
    <!-- Screen -->
    <div class="radio-screen">
      <div class="screen-glass"></div>
      <div class="screen-content" :class="{ 'has-signal': activeStation }">
        <template v-if="activeStation">
          <div class="station-icon" :style="{ background: activeStation.color }">
            {{ activeStation.label }}
          </div>
          <div class="station-name">{{ activeStation.name }}</div>
          <div class="signal-bars">
            <div class="bar" v-for="n in 3" :key="n"></div>
          </div>
        </template>
        <template v-else>
          <div class="static-noise">NO SIGNAL</div>
          <div class="frequency">{{ currentFreq.toFixed(1) }} MHz</div>
        </template>
      </div>
    </div>

    <!-- Controls -->
    <div class="controls-area">
      <div 
        class="tuner-track" 
        ref="trackRef"
        @mousedown="startDrag"
        @touchstart.passive="startDrag"
      >
        <div class="scale-marks">
          <div v-for="n in 21" :key="n" class="mark" :class="{ major: (n-1) % 5 === 0 }"></div>
        </div>
        
        <!-- Station Dots -->
        <div 
          v-for="(station, index) in stations" 
          :key="index"
          class="station-dot"
          :style="{ left: station.pos + '%' }"
          :class="{ active: activeStation?.name === station.name }"
        ></div>

        <div class="needle" :style="{ left: needlePos + '%' }">
          <div class="needle-head"></div>
          <div class="needle-line"></div>
        </div>
      </div>

      <button 
        class="action-btn" 
        :class="{ ready: activeStation }"
        @click="handleGo"
        :disabled="!activeStation"
      >
        GO
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue';

interface Station {
  name: string;
  label: string; // Short text/icon
  url: string;
  pos: number; // 0-100
  color: string;
}

const stations: Station[] = [
  { name: 'Bilibili', label: 'B站', url: 'https://www.bilibili.com/', pos: 15, color: '#fb7299' },
  { name: 'Douyin', label: '抖音', url: 'https://www.douyin.com/', pos: 32, color: '#000000' },
  { name: 'Xiaohongshu', label: '红薯', url: 'https://www.xiaohongshu.com/', pos: 50, color: '#ff2442' },
  { name: 'Zhihu', label: '知乎', url: 'https://www.zhihu.com/', pos: 68, color: '#0084ff' },
  { name: 'Tieba', label: '贴吧', url: 'https://tieba.baidu.com/', pos: 85, color: '#4e71f2' },
];

const needlePos = ref(50); // 0-100
const isDragging = ref(false);
const trackRef = ref<HTMLElement | null>(null);

const currentFreq = computed(() => {
  return 87.5 + (needlePos.value / 100) * (108.0 - 87.5);
});

const activeStation = computed(() => {
  return stations.find(s => Math.abs(s.pos - needlePos.value) < 3);
});

const updatePosition = (clientX: number) => {
  if (!trackRef.value) return;
  const rect = trackRef.value.getBoundingClientRect();
  let percent = ((clientX - rect.left) / rect.width) * 100;
  percent = Math.max(0, Math.min(100, percent));
  
  // Magnetic snap
  const snapTarget = stations.find(s => Math.abs(s.pos - percent) < 2);
  if (snapTarget) {
    percent = snapTarget.pos;
  }
  
  needlePos.value = percent;
};

const startDrag = (e: MouseEvent | TouchEvent) => {
  isDragging.value = true;
  const clientX = 'touches' in e ? e.touches[0].clientX : (e as MouseEvent).clientX;
  updatePosition(clientX);
  
  window.addEventListener('mousemove', onDrag);
  window.addEventListener('touchmove', onDrag);
  window.addEventListener('mouseup', stopDrag);
  window.addEventListener('touchend', stopDrag);
};

const onDrag = (e: MouseEvent | TouchEvent) => {
  if (!isDragging.value) return;
  const clientX = 'touches' in e ? e.touches[0].clientX : (e as MouseEvent).clientX;
  updatePosition(clientX);
};

const stopDrag = () => {
  isDragging.value = false;
  window.removeEventListener('mousemove', onDrag);
  window.removeEventListener('touchmove', onDrag);
  window.removeEventListener('mouseup', stopDrag);
  window.removeEventListener('touchend', stopDrag);
};

const handleGo = () => {
  if (activeStation.value) {
    window.open(activeStation.value.url, '_blank');
  }
};

onUnmounted(() => {
  stopDrag();
});
</script>

<style scoped>
.radio-widget {
  width: 100%;
  height: 100%;
  background: #2c3e50;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;
  color: #fff;
  gap: 15px;
}

.radio-screen {
  flex: 1;
  background: #000;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
  border: 2px solid #555;
  box-shadow: inset 0 0 10px rgba(0,0,0,0.8);
}

.screen-glass {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 100%);
  pointer-events: none;
  z-index: 2;
}

.screen-content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: 'Courier New', monospace;
  transition: all 0.2s;
}

.screen-content.has-signal {
  background: radial-gradient(circle, #1a1a1a 0%, #000 100%);
}

.static-noise {
  color: #555;
  font-size: 18px; /* Increased from 14px */
  letter-spacing: 2px;
  animation: flicker 0.1s infinite;
}

.frequency {
  color: #0f0;
  font-size: 28px; /* Increased from 20px */
  margin-top: 5px;
  text-shadow: 0 0 5px #0f0;
}

.station-icon {
  width: 50px; /* Increased */
  height: 50px; /* Increased */
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 18px; /* Increased */
  color: #fff;
  margin-bottom: 8px;
  box-shadow: 0 0 10px rgba(255,255,255,0.2);
}

.station-name {
  color: #fff;
  font-size: 20px; /* Increased from 16px */
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.signal-bars {
  display: flex;
  gap: 2px;
  margin-top: 5px;
  align-items: flex-end;
  height: 10px;
}

.bar {
  width: 3px;
  background: #0f0;
  animation: signal 0.5s infinite alternate;
}
.bar:nth-child(1) { height: 4px; animation-delay: 0s; }
.bar:nth-child(2) { height: 7px; animation-delay: 0.1s; }
.bar:nth-child(3) { height: 10px; animation-delay: 0.2s; }

.controls-area {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.tuner-track {
  flex: 1;
  height: 30px;
  background: #444;
  border-radius: 15px;
  position: relative;
  cursor: grab;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.5);
}

.tuner-track:active {
  cursor: grabbing;
}

.scale-marks {
  position: absolute;
  top: 50%;
  left: 10px;
  right: 10px;
  height: 10px;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  pointer-events: none;
}

.mark {
  width: 1px;
  height: 6px;
  background: #777;
}
.mark.major {
  height: 10px;
  background: #aaa;
}

.station-dot {
  position: absolute;
  top: 50%;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #666;
  transform: translate(-50%, -50%);
  transition: all 0.2s;
}

.station-dot.active {
  background: #0f0;
  box-shadow: 0 0 5px #0f0;
  width: 8px;
  height: 8px;
}

.needle {
  position: absolute;
  top: 0;
  height: 100%;
  width: 2px;
  background: #f00;
  transform: translateX(-50%);
  pointer-events: none;
  z-index: 10;
}

.needle-head {
  width: 10px;
  height: 10px;
  background: #f00;
  border-radius: 50%;
  position: absolute;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
}

.action-btn {
  height: 30px;
  padding: 0 15px;
  background: #333;
  color: #777;
  border: none;
  border-radius: 4px;
  font-weight: 700;
  cursor: not-allowed;
  transition: all 0.2s;
}

.action-btn.ready {
  background: #0f0;
  color: #000;
  cursor: pointer;
  box-shadow: 0 0 10px rgba(0,255,0,0.3);
}

.action-btn.ready:hover {
  transform: scale(1.05);
}

@keyframes flicker {
  0% { opacity: 0.8; }
  50% { opacity: 0.4; }
  100% { opacity: 0.9; }
}

@keyframes signal {
  from { opacity: 0.5; }
  to { opacity: 1; }
}
</style>
