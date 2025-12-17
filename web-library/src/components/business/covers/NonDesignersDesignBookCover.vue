<template>
  <div class="cover-container" :class="{ 'card-mode': mode === 'card', 'hero': mode === 'hero' }" ref="containerRef" @mousemove="handleMouseMove" @mouseleave="resetTransform">
    <div class="background-grid"></div>
    
    <!-- Visual CRAP Representation -->
    <div class="design-layout" :style="contentStyle">
      <!-- Contrast: Big bold black vs small light gray -->
      <div class="contrast-section">
        <div class="big-circle"></div>
        <div class="small-text">contrast</div>
      </div>

      <!-- Repetition: Repeated shapes -->
      <div class="repetition-section">
        <div class="repeat-shape" v-for="n in 3" :key="n"></div>
        <div class="small-text">repetition</div>
      </div>

      <!-- Alignment: Strong alignment line -->
      <div class="alignment-section">
        <div class="align-line"></div>
        <div class="align-content">
          <div class="align-box"></div>
          <div class="align-text">alignment</div>
        </div>
      </div>

      <!-- Proximity: Grouped elements -->
      <div class="proximity-section">
        <div class="prox-group">
          <div class="prox-dot"></div>
          <div class="prox-dot"></div>
          <div class="prox-dot"></div>
        </div>
        <div class="small-text">proximity</div>
      </div>
    </div>

    <!-- Title Overlay -->
    <div class="title-overlay" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="titleStyle">
      <div class="main-title-cn">写给大家看的设计书</div>
      <div class="sub-title-en">The Non-Designer's Design Book</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

defineProps<{
  mode?: 'card' | 'hero'
}>();

const containerRef = ref<HTMLElement | null>(null);
const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e: MouseEvent) => {
  if (!containerRef.value) return;
  const rect = containerRef.value.getBoundingClientRect();
  // Calculate relative position (-1 to 1) within the card
  mouseX.value = ((e.clientX - rect.left) / rect.width) * 2 - 1;
  mouseY.value = ((e.clientY - rect.top) / rect.height) * 2 - 1;
};

const resetTransform = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const contentStyle = computed(() => ({
  transform: `perspective(500px) rotateX(${mouseY.value * -5}deg) rotateY(${mouseX.value * 5}deg) translateZ(-10px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(-50%, -50%) perspective(500px) rotateX(${mouseY.value * -5}deg) rotateY(${mouseX.value * 5}deg) translateZ(50px)`
}));
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;700&family=Noto+Sans+SC:wght@300;900&display=swap');

.cover-container {
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', sans-serif;
  cursor: default;
}

.background-grid {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: radial-gradient(#ccc 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
}

.design-layout {
  position: absolute;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 10px;
  transition: transform 0.1s ease-out;
}

/* Common Text */
.small-text {
  font-size: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #666;
  margin-top: 5px;
}

/* Sections */
.contrast-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 10px;
}

.big-circle {
  width: 40px;
  height: 40px;
  background: #000;
  border-radius: 50%;
}

.repetition-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #000;
  color: #fff;
}
.repetition-section .small-text { color: #aaa; }

.repeat-shape {
  width: 20px;
  height: 4px;
  background: #fff;
  margin: 2px 0;
}

.alignment-section {
  position: relative;
  background: #e0e0e0;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.align-line {
  position: absolute;
  left: 20px;
  top: 10px;
  bottom: 10px;
  width: 1px;
  background: #f00;
  opacity: 0.5;
}

.align-content {
  margin-left: 15px; /* Visual alignment to line */
}

.align-box {
  width: 20px;
  height: 20px;
  background: #333;
  margin-bottom: 5px;
}

.align-text {
  font-size: 8px;
  font-weight: 700;
}

.proximity-section {
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.prox-group {
  display: flex;
  gap: 2px;
}

.prox-dot {
  width: 8px;
  height: 8px;
  background: #000;
  border-radius: 50%;
}

/* Title Overlay */
.title-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  text-align: center;
  z-index: 10;
  mix-blend-mode: exclusion;
  pointer-events: none;
  transition: transform 0.1s ease-out;
  /* Reset previous positioning */
  bottom: auto;
  right: auto;
}

/* 状态：未展开 */
.state-collapsed .main-title-cn {
  font-family: 'Noto Sans SC', sans-serif;
  font-weight: 900;
  font-size: 24px; /* Card size */
  color: #fff; /* Mix-blend-mode handles color */
  line-height: 1.2;
  margin-bottom: 4px;
  transition: all 0.3s ease;
}

.state-collapsed .sub-title-en {
  font-size: 10px; /* Card size */
  color: #fff;
  opacity: 0.8;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

/* 状态：已展开 */
.state-expanded .main-title-cn {
  font-family: 'Noto Sans SC', sans-serif;
  font-weight: 900;
  font-size: 32px; /* Base size (Hero) */
  color: #fff; /* Mix-blend-mode handles color */
  line-height: 1.2;
  margin-bottom: 4px;
  transition: all 0.3s ease;
}

.state-expanded .sub-title-en {
  font-size: 14px; /* Base size (Hero) */
  color: #fff;
  opacity: 0.8;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

/* Hero Mode Adjustments */
/* .hero .main-title-cn { font-size: 48px; } -> Moved to base */
/* .hero .sub-title-en { font-size: 14px; } -> Moved to base */
.hero .design-layout { gap: 20px; }
.hero .big-circle { width: 80px; height: 80px; }
.hero .repeat-shape { width: 40px; height: 8px; }
.hero .align-box { width: 40px; height: 40px; }
.hero .prox-dot { width: 16px; height: 16px; }
.hero .small-text { font-size: 12px; }
</style>
