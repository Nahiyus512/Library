<template>
  <div class="visual-color" :class="{ hero: mode === 'hero' }" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <!-- Albers Squares Composition -->
    <div class="squares-container" :style="containerStyle">
      <div class="square outer" :style="outerStyle"></div>
      <div class="square middle" :style="middleStyle"></div>
      <div class="square inner" :style="innerStyle"></div>
    </div>
    
    <div class="title-layer" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="titleStyle">
      <div class="cn-title">色彩互动学</div>
      <div class="en-title">INTERACTION OF COLOR</div>
    </div>

    <div class="color-info">
      <div class="color-value">{{ currentColorHex }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';

const props = defineProps<{
  mode?: 'card' | 'hero'
}>();

const mouseX = ref(0);
const mouseY = ref(0);
const hue = ref(0);

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

const containerStyle = computed(() => ({
  transform: `translate(${mouseX.value * 0.02}px, ${mouseY.value * 0.02}px)`
}));

// Dynamic colors based on time and mouse interaction
const outerStyle = computed(() => ({
  backgroundColor: `hsl(${hue.value}, 40%, 80%)`,
  transform: `translate(${mouseX.value * 0.02}px, ${mouseY.value * 0.02}px)`
}));

const middleStyle = computed(() => ({
  backgroundColor: `hsl(${(hue.value + 120) % 360}, 50%, 60%)`,
  transform: `translate(${mouseX.value * 0.05}px, ${mouseY.value * 0.05}px)`
}));

const innerStyle = computed(() => ({
  backgroundColor: `hsl(${(hue.value + 240) % 360}, 60%, 40%)`,
  transform: `translate(${mouseX.value * 0.08}px, ${mouseY.value * 0.08}px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -0.1}px, ${mouseY.value * -0.1}px)`,
  color: `hsl(${(hue.value + 180) % 360}, 80%, 20%)`
}));

const currentColorHex = computed(() => {
  return `H: ${Math.round(hue.value)}°`;
});

let animationFrame: number;

const animate = () => {
  hue.value = (hue.value + 0.2) % 360;
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

.visual-color {
  width: 100%; height: 100%;
  background-color: #f5f5f5; /* Neutral Grey Background */
  position: relative; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
  font-family: 'Noto Serif SC', serif;
  perspective: 1000px;
  cursor: pointer;
}

.squares-container {
  position: absolute;
  width: 100%; height: 100%;
  display: flex; align-items: flex-end; justify-content: center;
  padding-bottom: 20px;
}

.square {
  position: absolute;
  transition: background-color 0.1s ease;
}

.outer {
  width: 220px; height: 220px;
  bottom: 40px;
}

.middle {
  width: 160px; height: 160px;
  bottom: 55px;
}

.inner {
  width: 100px; height: 100px;
  bottom: 70px;
}

.title-layer {
  position: relative;
  z-index: 10;
  text-align: center;
  transition: transform 0.1s ease-out;
  mix-blend-mode: difference;
}

/* 状态：未展开 */
.state-collapsed .cn-title {
  font-size: 2.0rem;
  font-weight: 900;
  margin: 0;
  letter-spacing: 5px;
  mix-blend-mode: exclusion;
  transition: all 0.3s ease;
}

.state-collapsed .en-title {
  font-size: 1.2rem;
  letter-spacing: 4px;
  opacity: 0.8;
  font-weight: bold;
  margin-top: 8px;
}

/* 状态：已展开 */
.state-expanded .cn-title {
  font-size: 4.0rem;
  font-weight: 900;
  margin: 0;
  letter-spacing: 2px;
  mix-blend-mode: exclusion;
  transition: all 0.3s ease;
}

.state-expanded .en-title {
  font-size: 1.6rem;
  letter-spacing: 4px;
  opacity: 0.8;
  font-weight: bold;
  margin-top: 8px;
}

.color-info {
  position: absolute;
  bottom: 15px;
  right: 15px;
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
  opacity: 0.5;
}
</style>
