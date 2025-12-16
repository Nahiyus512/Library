<template>
  <div class="cover-container" :class="mode" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <div class="grid-background" :style="gridStyle">
      <!-- Vertical Grid Lines -->
      <div v-for="n in 12" :key="'v'+n" class="grid-line vertical" :style="{ left: `${(n-1) * 8.33}%` }"></div>
      <!-- Horizontal Grid Lines -->
      <div v-for="n in 12" :key="'h'+n" class="grid-line horizontal" :style="{ top: `${(n-1) * 8.33}%` }"></div>
    </div>

    <div class="content-wrapper" :style="parallaxStyle">
      <div class="swiss-layout">
        <div class="block red-block"></div>
        <div class="block black-block"></div>
        
        <div class="title-group">
          <h1 class="title-cn">平面设计中的<br>网格系统</h1>
          <div class="subtitle">GRID SYSTEMS IN GRAPHIC DESIGN</div>
        </div>

        <div class="author-name">JOSEF MÜLLER-BROCKMANN</div>
        
        <div class="grid-spec">
          <span>8 COLUMN</span>
          <span>20 PX GUTTER</span>
        </div>
      </div>
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
  // Normalize -0.5 to 0.5
  mouseX.value = (e.clientX - left) / width - 0.5;
  mouseY.value = (e.clientY - top) / height - 0.5;
};

const resetParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const parallaxStyle = computed(() => {
  const intensity = props.mode === 'hero' ? 30 : 10;
  return {
    transform: `translate(${mouseX.value * intensity}px, ${mouseY.value * intensity}px)`
  };
});

const gridStyle = computed(() => {
  const intensity = props.mode === 'hero' ? 15 : 5;
  return {
    transform: `translate(${mouseX.value * -intensity}px, ${mouseY.value * -intensity}px) scale(1.1)`
  };
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@700;900&family=Inter:wght@400;700&display=swap');

.cover-container {
  width: 100%;
  height: 100%;
  position: relative;
  background-color: #f4f4f0; /* Off-white Swiss paper */
  overflow: hidden;
  user-select: none;
  font-family: 'Inter', sans-serif;
  color: #000;
}

.grid-background {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  pointer-events: none;
  transition: transform 0.2s ease-out;
}

.grid-line {
  position: absolute;
  background-color: rgba(255, 0, 0, 0.1); /* Subtle red guide lines */
}

.grid-line.vertical {
  top: 0; bottom: 0; width: 1px;
}

.grid-line.horizontal {
  left: 0; right: 0; height: 1px;
}

.content-wrapper {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  padding: 20px;
  box-sizing: border-box;
}

.swiss-layout {
  position: relative;
  width: 100%;
  height: 100%;
  max-width: 400px; /* Constrain for card look */
}

.block {
  position: absolute;
  opacity: 0.9;
}

.red-block {
  top: 10%; left: 10%;
  width: 30%; height: 10%;
  background-color: #ff3333; /* Swiss Red */
  mix-blend-mode: multiply;
  animation: float 6s ease-in-out infinite;
}

.black-block {
  bottom: 20%; right: 10%;
  width: 20%; height: 40%;
  background-color: #000;
  mix-blend-mode: multiply;
  opacity: 0.1;
  animation: float 8s ease-in-out infinite reverse;
}

.title-group {
  position: absolute;
  top: 25%;
  left: 8.33%; /* Align to grid */
  z-index: 10;
}

.title-cn {
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  font-size: 3rem;
  line-height: 1.1;
  margin: 0;
  color: #000;
  letter-spacing: -1px;
  transition: font-size 0.3s ease;
}

.cover-container:not(.hero) .title-cn {
  font-size: 1.6rem;
}

.subtitle {
  margin-top: 10px;
  font-size: 0.5rem;
  font-weight: 700;
  letter-spacing: 1px;
}

.author-name {
  position: absolute;
  bottom: 10%;
  left: 8.33%;
  font-size: 0.7rem;
  font-weight: 700;
  transform: rotate(-90deg);
  transform-origin: left bottom;
  white-space: nowrap;
}

.grid-spec {
  position: absolute;
  top: 5%;
  right: 5%;
  display: flex;
  flex-direction: column;
  text-align: right;
  font-size: 0.5rem;
  font-family: monospace;
  color: #666;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(5px, -5px); }
}

/* Hero Mode Adjustments */
.cover-container.hero .title-cn {
  font-size: 3rem;
}

.cover-container.hero .swiss-layout {
  max-width: 1200px;
}
</style>
