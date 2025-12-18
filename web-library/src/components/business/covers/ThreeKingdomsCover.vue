<template>
  <div class="three-kingdoms-cover" :class="{ 'card-mode': mode === 'card', hero: mode === 'hero' }" @mousemove="handleMouseMove" @mouseleave="resetState">
    <!-- Background Texture -->
    <div class="paper-texture"></div>
    
    <!-- Ink Void / Chaos -->
    <div class="void-container" :style="voidStyle">
      <!-- Three Dragons / Ink Circles representing Wei, Shu, Wu -->
      <div class="ink-circle wei" :style="weiStyle"></div>
      <div class="ink-circle shu" :style="shuStyle"></div>
      <div class="ink-circle wu" :style="wuStyle"></div>
    </div>

    <!-- Falling Petals (Brotherhood/Fragility) -->
    <div class="petals-container">
      <div v-for="n in 12" :key="n" class="petal" :style="getPetalStyle(n)"></div>
    </div>

    <!-- Title Group -->
    <div class="title-group" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="titleStyle">
      <div class="main-title">三国</div>
      <div class="sub-title-seal">演义</div>
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
  const target = e.currentTarget as HTMLElement;
  const rect = target.getBoundingClientRect();
  
  // Normalize -1 to 1
  mouseX.value = ((e.clientX - rect.left) / rect.width - 0.5) * 2;
  mouseY.value = ((e.clientY - rect.top) / rect.height - 0.5) * 2;
};

const resetState = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

// Parallax and separation effects
const voidStyle = computed(() => ({
  transform: `scale(${1 + Math.abs(mouseX.value) * 0.1})`
}));

const weiStyle = computed(() => ({
  transform: `translate(${mouseX.value * -20}px, ${mouseY.value * -20}px) rotate(${mouseX.value * 10}deg)`
}));

const shuStyle = computed(() => ({
  transform: `translate(${mouseX.value * 25}px, ${mouseY.value * 10}px) rotate(${mouseY.value * -5}deg)`
}));

const wuStyle = computed(() => ({
  transform: `translate(${mouseX.value * 5}px, ${mouseY.value * 30}px) rotate(${mouseX.value * -10}deg)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * 10}px, ${mouseY.value * 10}px)`
}));

const getPetalStyle = (n: number) => {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.7 + 0.3,
    transform: `scale(${Math.random() * 0.5 + 0.5}) rotate(${Math.random() * 360}deg)`
  };
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&family=Noto+Serif+SC:wght@700;900&display=swap');

.three-kingdoms-cover {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  background-color: #eaddcf; /* Aged paper base */
  font-family: 'Ma Shan Zheng', cursive;
  display: flex;
  justify-content: center;
  align-items: center;
  user-select: none;
}

.paper-texture {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg width='200' height='200' viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.1'/%3E%3C/svg%3E");
  mix-blend-mode: multiply;
  opacity: 0.6;
}

/* Ink Circles - Using CSS Gradients and Border Radius to simulate ink blots */
.void-container {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.2s ease-out;
}

.ink-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(8px) contrast(1.2);
  opacity: 0.8;
  mix-blend-mode: multiply;
}

/* Wei (Blue/Black - Cold, Calculating) */
.wei {
  width: 180px; height: 160px;
  background: radial-gradient(circle at 30% 30%, #2c3e50, #000000);
  top: 20%; left: 25%;
  clip-path: polygon(10% 0%, 100% 0%, 90% 100%, 0% 80%);
  animation: float 6s ease-in-out infinite;
}

/* Shu (Red/Warm - Benevolence, Passion) */
.shu {
  width: 160px; height: 180px;
  background: radial-gradient(circle at 70% 30%, #8E2323, #4a0f0f);
  bottom: 20%; left: 35%;
  clip-path: polygon(20% 0%, 100% 20%, 80% 100%, 0% 80%);
  animation: float 7s ease-in-out infinite reverse;
}

/* Wu (Green/Gold - Balance, Water) */
.wu {
  width: 150px; height: 150px;
  background: radial-gradient(circle at 50% 50%, #556b2f, #1a2608);
  top: 40%; right: 20%;
  clip-path: polygon(0% 10%, 90% 0%, 100% 90%, 10% 100%);
  animation: float 8s ease-in-out infinite 1s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(10px, -10px); }
}

/* Petals */
.petals-container {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
}

.petal {
  position: absolute;
  top: -10px;
  width: 8px; height: 12px;
  background: #ffb7b2;
  border-radius: 100% 0 100% 0;
  opacity: 0.6;
  animation: fall 8s linear infinite;
}

@keyframes fall {
  to { transform: translate(50px, 600px) rotate(720deg); opacity: 0; }
}

/* Typography */
.title-group {
  position: relative;
  z-index: 10;
  text-align: center;
  transition: transform 0.1s ease-out;
  color: #f7f7f7;
}

/* 状态：未展开 */
.state-collapsed .main-title {
  font-size: 4.8rem;
  line-height: 1;
  text-shadow: 2px 2px 0px rgba(255, 255, 255, 0.991);
  writing-mode: vertical-rl; /* Traditional vertical writing */
  text-orientation: upright;
  margin-right: 20px;
  transition: all 0.3s ease;
}

/* 状态：已展开 */
.state-expanded .main-title {
  font-size: 8rem;
  line-height: 1;
  text-shadow: 2px 2px 0px rgba(255, 255, 255, 0.991);
  writing-mode: vertical-rl; /* Traditional vertical writing */
  text-orientation: upright;
  margin-right: 20px;
  transition: all 0.3s ease;
}

.sub-title-seal {
  position: absolute;
  bottom: 10px;
  left: 100px;
  background-color: #8E2323; /* Cinnabar Red Seal */
  color: #fff;
  font-family: 'Noto Serif SC', serif;
  font-size: 1rem;
  padding: 4px 2px;
  border-radius: 4px;
  writing-mode: vertical-rl;
  box-shadow: 2px 2px 4px rgba(0,0,0,0.3);
  font-weight: bold;
}

.hero .ink-circle {
  filter: blur(12px) contrast(1.5);
  opacity: 0.9;
}

/* Hover State on Container (from CSS parent) */
.unified-card:hover .three-kingdoms-cover .ink-circle {
  filter: blur(4px) contrast(1.1); /* Sharpen on hover */
}
</style>
