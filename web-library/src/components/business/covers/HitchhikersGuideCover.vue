<template>
  <div class="cover-container" :class="mode" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <div class="crt-overlay"></div>
    <div class="scanline"></div>
    
    <div class="content-wrapper" :style="parallaxStyle">
      <div class="graphic-layer">
        <div class="thumb-circle">
          <div class="thumb-icon">👍</div>
        </div>
        <div class="orbit-rings">
          <div class="ring ring-1"></div>
          <div class="ring ring-2"></div>
          <div class="ring ring-3"></div>
        </div>
      </div>
      
      <div class="text-layer" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'">
        <h1 class="title-cn">银河系<br>漫游指南</h1>
        <div class="subtitle">THE HITCHHIKER'S GUIDE TO THE GALAXY</div>
        <div class="status-badge">DON'T PANIC</div>
      </div>
    </div>
    
    <div class="data-stream">
      <span v-for="n in 20" :key="n" class="bit" :style="{ animationDelay: `${Math.random() * 2}s`, left: `${Math.random() * 100}%` }">
        {{ Math.random() > 0.5 ? '1' : '0' }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const props = defineProps<{
  mode?: 'card' | 'hero'
}>();

const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e: MouseEvent) => {
  const { left, top, width, height } = (e.currentTarget as HTMLElement).getBoundingClientRect();
  const x = (e.clientX - left) / width - 0.5;
  const y = (e.clientY - top) / height - 0.5;
  
  mouseX.value = x;
  mouseY.value = y;
};

const resetParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const parallaxStyle = computed(() => {
  return {
    transform: `perspective(1000px) rotateY(${mouseX.value * 15}deg) rotateX(${-mouseY.value * 15}deg) translateZ(20px)`
  };
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@900&family=VT323&display=swap');

.cover-container {
  width: 100%;
  height: 100%;
  background-color: #000;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  perspective: 1000px;
}

.crt-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: radial-gradient(circle, rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.6) 100%),
              linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.1) 50%);
  background-size: 100% 2px;
  pointer-events: none;
  z-index: 10;
}

.scanline {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 10px;
  background: rgba(0, 255, 0, 0.1);
  animation: scan 2s linear infinite;
  z-index: 11;
  pointer-events: none;
}

.content-wrapper {
  position: relative;
  width: 80%;
  height: 80%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transform-style: preserve-3d;
  transition: transform 0.1s ease-out;
}

.graphic-layer {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) translateZ(-20px);
  width: 300px;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.thumb-circle {
  width: 100px;
  height: 100px;
  background: #00ff00;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0 20px #00ff00;
  z-index: 2;
  animation: pulse 2s infinite;
}

.thumb-icon {
  font-size: 50px;
  color: #000;
  transform: rotate(-15deg);
}

.orbit-rings {
  position: absolute;
  width: 100%;
  height: 100%;
  animation: spin 10s linear infinite;
}

.ring {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 1px solid rgba(0, 255, 0, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.ring-1 { width: 140px; height: 140px; border-top-color: #00ff00; animation: rotateRing 4s infinite linear; }
.ring-2 { width: 200px; height: 200px; border-right-color: #00ff00; animation: rotateRing 6s infinite reverse linear; }
.ring-3 { width: 260px; height: 260px; border-bottom-color: #00ff00; animation: rotateRing 8s infinite linear; }

.text-layer {
  position: relative;
  z-index: 5;
  text-align: center;
  transform: translateZ(40px);
}

/* 状态：未展开 */
.state-collapsed .title-cn {
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  font-size: 2.2rem;
  line-height: 1.1;
  color: #00ff00;
  text-shadow: 2px 2px 0px #003300;
  margin: 0;
  letter-spacing: 2px;
  transition: all 0.3s;
}

.state-collapsed .subtitle {
  font-family: 'VT323', monospace;
  color: #00ff00;
  font-size: 0.8rem;
  margin-top: 10px;
  opacity: 0.8;
  display: none;
}

.state-collapsed .status-badge {
  display: inline-block;
  margin-top: 10px;
  padding: 2px 8px;
  border: 2px solid #00ff00;
  color: #00ff00;
  font-family: 'VT323', monospace;
  font-size: 1rem;
  font-weight: bold;
  background: rgba(0, 255, 0, 0.1);
  box-shadow: 0 0 10px rgba(0, 255, 0, 0.5);
  animation: blink 1s step-end infinite;
}

/* 状态：已展开 */
.state-expanded .title-cn {
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  font-size: 3.5rem;
  line-height: 1.1;
  color: #00ff00;
  text-shadow: 2px 2px 0px #003300;
  margin: 0;
  letter-spacing: 5px;
  transition: all 0.3s;
}

.state-expanded .subtitle {
  font-family: 'VT323', monospace;
  color: #00ff00;
  font-size: 1rem;
  margin-top: 10px;
  opacity: 0.8;
  display: block;
}

.state-expanded .status-badge {
  display: inline-block;
  margin-top: 20px;
  padding: 5px 15px;
  border: 2px solid #00ff00;
  color: #00ff00;
  font-family: 'VT323', monospace;
  font-size: 1.5rem;
  font-weight: bold;
  background: rgba(0, 255, 0, 0.1);
  box-shadow: 0 0 10px rgba(0, 255, 0, 0.5);
  animation: blink 1s step-end infinite;
}

/* Default/Card Mode overrides for graphics only */
.cover-container:not(.hero) .graphic-layer {
  width: 150px;
  height: 150px;
}
.cover-container:not(.hero) .thumb-circle {
  width: 60px;
  height: 60px;
}
.cover-container:not(.hero) .thumb-icon {
  font-size: 30px;
}
.cover-container:not(.hero) .ring-1 { width: 80px; height: 80px; }
.cover-container:not(.hero) .ring-2 { width: 120px; height: 120px; }
.cover-container:not(.hero) .ring-3 { width: 150px; height: 150px; }

.data-stream {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0;
}

.bit {
  position: absolute;
  top: -20px;
  color: rgba(0, 255, 0, 0.2);
  font-family: monospace;
  font-size: 12px;
  animation: rain 3s linear infinite;
}

@keyframes scan {
  0% { top: -10%; }
  100% { top: 110%; }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 20px #00ff00; }
  50% { transform: scale(1.1); box-shadow: 0 0 40px #00ff00; }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes rotateRing {
  0% { transform: translate(-50%, -50%) rotateX(60deg) rotateZ(0deg); }
  100% { transform: translate(-50%, -50%) rotateX(60deg) rotateZ(360deg); }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

@keyframes rain {
  0% { transform: translateY(0); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(100vh); opacity: 0; }
}
</style>
