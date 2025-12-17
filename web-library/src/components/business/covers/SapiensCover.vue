<template>
  <div class="visual-sapiens" :class="{ 'card-mode': mode === 'card' }" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <!-- Background Texture -->
    <div class="bg-texture"></div>

    <div class="evolution-spiral" :style="spiralStyle">
      <div class="spiral-ring ring-1"></div>
      <div class="spiral-ring ring-2"></div>
      <div class="spiral-ring ring-3"></div>
      <div class="center-symbol">
        <div class="hand-print"></div>
      </div>
    </div>
    
    <div class="title-layer" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="titleStyle">
      <div class="cn-title">人类简史</div>
      <div class="en-title">SAPIENS</div>
    </div>

    <div class="data-overlay">
      <div class="year-counter">{{ currentYearDisplay }}</div>
      <div class="era-label">{{ currentEra }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';

defineProps<{
  mode?: 'card' | 'hero'
}>();

const year = ref(-70000);
const mouseX = ref(0);
const mouseY = ref(0);

// Parallax logic
const handleMouseMove = (e: MouseEvent) => {
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
  const x = e.clientX - rect.left - rect.width / 2;
  const y = e.clientY - rect.top - rect.height / 2;
  mouseX.value = x;
  mouseY.value = y;
};

const resetParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const spiralStyle = computed(() => ({
  transform: `translate(${mouseX.value * 0.05}px, ${mouseY.value * 0.05}px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -0.1}px, ${mouseY.value * -0.1}px)`
}));

const currentYearDisplay = computed(() => {
  return year.value < 0 ? `前 ${Math.abs(year.value)}` : `${year.value}`;
});

const currentEra = computed(() => {
  if (year.value < -12000) return "认知革命";
  if (year.value < 1500) return "农业革命";
  return "科学革命";
});

let animationFrame: number;

const animate = () => {
  // Fast forward history
  year.value += 100;
  if (year.value > 2024) {
    year.value = -70000;
  }
  animationFrame = requestAnimationFrame(animate);
};

onMounted(() => {
  animationFrame = requestAnimationFrame(animate);
});

onUnmounted(() => {
  cancelAnimationFrame(animationFrame);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@900&display=swap');

.visual-sapiens {
  width: 100%; height: 100%;
  background-color: #f4e4bc; /* Unified Pale Beige */
  position: relative; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
  font-family: 'Noto Serif SC', serif;
  color: #3e2723;
  perspective: 1000px;
  cursor: pointer;
}

.bg-texture {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg width='6' height='6' viewBox='0 0 6 6' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%235d4037' fill-opacity='0.05' fill-rule='evenodd'%3E%3Cpath d='M5 0h1L0 6V5zM6 5v1H5z'/%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
}

.evolution-spiral {
  position: absolute;
  width: 240px; height: 240px;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.1s ease-out;
  opacity: 0.15; /* Subtle background element */
}

.spiral-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px dashed #5d4037;
}

.ring-1 {
  width: 100%; height: 100%;
  animation: spin 60s linear infinite;
  border-width: 1px;
}

.ring-2 {
  width: 70%; height: 70%;
  border-color: #8d6e63;
  border-style: dotted;
  animation: spin 40s linear infinite reverse;
}

.ring-3 {
  width: 40%; height: 40%;
  border: 4px solid rgba(93, 64, 55, 0.2);
  border-top-color: #5d4037;
  animation: spin 10s ease-in-out infinite;
}

.center-symbol {
  width: 10px; height: 10px;
  background: #3e2723;
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(62, 39, 35, 0.5);
  animation: pulse 2s infinite;
}

.title-layer {
  position: relative;
  z-index: 10;
  text-align: center;
  transition: transform 0.1s ease-out;
  mix-blend-mode: multiply;
}

/* 状态：未展开 */
.state-collapsed .cn-title {
  font-size: 2.8rem;
  font-weight: 900;
  margin: 0;
  letter-spacing: 5px;
  transition: all 0.3s ease;
}

.state-collapsed .en-title {
  font-size: 0.7rem;
  letter-spacing: 8px;
  opacity: 0.6;
  font-weight: bold;
  margin-top: 5px;
}

/* 状态：已展开 */
.state-expanded .cn-title {
  font-size: 4.5rem;
  font-weight: 900;
  margin: 0;
  letter-spacing: 5px;
  transition: all 0.3s ease;
}

.state-expanded .en-title {
  font-size: 0.7rem;
  letter-spacing: 8px;
  opacity: 0.6;
  font-weight: bold;
  margin-top: 5px;
}

.data-overlay {
  position: absolute;
  bottom: 15px;
  text-align: center;
  width: 100%;
  pointer-events: none;
}

.year-counter {
  font-size: 1.2rem;
  font-weight: 700;
  font-family: 'Courier New', monospace;
  font-variant-numeric: tabular-nums;
}

.era-label {
  font-size: 0.6rem;
  letter-spacing: 2px;
  margin-top: 2px;
  opacity: 0.6;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.8; }
  50% { transform: scale(1.5); opacity: 0.4; }
}
</style>
