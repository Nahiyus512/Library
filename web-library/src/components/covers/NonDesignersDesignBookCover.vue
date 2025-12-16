<template>
  <div class="cover-container">
    <div class="background-pattern"></div>
    
    <!-- Title Group -->
    <div class="title-group" ref="titleRef">
      <div class="main-title">
        <span class="char c1">写</span>
        <span class="char c2">给</span>
        <span class="char c3">大</span>
        <span class="char c4">家</span>
        <span class="char c5">看</span>
        <span class="char c6">的</span>
      </div>
      <div class="design-title">
        <span class="d-char d1">设</span>
        <span class="d-char d2">计</span>
        <span class="d-char d3">书</span>
      </div>
      <div class="subtitle">The Non-Designer's Design Book</div>
    </div>

    <!-- Visual Metaphors for CRAP -->
    <div class="visual-elements">
      <!-- Contrast: Black Circle vs Small White Dot -->
      <div class="element contrast-circle"></div>
      
      <!-- Repetition: 3 Lines -->
      <div class="element repetition-lines">
        <div class="r-line"></div>
        <div class="r-line"></div>
        <div class="r-line"></div>
      </div>

      <!-- Alignment: Vertical Line -->
      <div class="element alignment-guide"></div>

      <!-- Proximity: Group of squares -->
      <div class="element proximity-group">
        <div class="p-sq"></div>
        <div class="p-sq"></div>
        <div class="p-sq"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';

const titleRef = ref<HTMLElement | null>(null);

const handleMouseMove = (e: MouseEvent) => {
  if (!titleRef.value) return;
  const { clientX, clientY } = e;
  const { innerWidth, innerHeight } = window;
  
  const x = (clientX - innerWidth / 2) / 20;
  const y = (clientY - innerHeight / 2) / 20;
  
  titleRef.value.style.transform = `translate(${x}px, ${y}px)`;
};

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove);
});

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;700;900&display=swap');

.cover-container {
  width: 100%;
  height: 100%;
  background-color: #f5f5f7;
  position: relative;
  overflow: hidden;
  font-family: 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
  color: #1d1d1f;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.title-group {
  z-index: 10;
  text-align: center;
  transition: transform 0.1s ease-out;
  mix-blend-mode: multiply;
}

.main-title {
  font-size: 1.2rem;
  font-weight: 300;
  letter-spacing: 0.2em;
  margin-bottom: 0.5rem;
  display: flex;
  justify-content: center;
  gap: 0.2em;
}

.design-title {
  font-size: 3.5rem;
  font-weight: 900;
  line-height: 1;
  letter-spacing: -0.05em;
  margin-bottom: 0.5rem;
  display: flex;
  justify-content: center;
  gap: 0.1em;
}

.d-char {
  display: inline-block;
  position: relative;
}

/* Contrast in Title */
.d1 { color: #000; }
.d2 { color: #333; transform: translateY(-5px); }
.d3 { color: #1d1d1f; }

.subtitle {
  font-size: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  opacity: 0.6;
  font-family: 'Inter', sans-serif;
}

/* Visual Elements */
.visual-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

/* Contrast */
.contrast-circle {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  background: #000;
  border-radius: 50%;
  opacity: 0.1;
}

/* Repetition */
.repetition-lines {
  position: absolute;
  bottom: 20px;
  left: 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.r-line {
  width: 40px;
  height: 2px;
  background: #1d1d1f;
  opacity: 0.2;
}

/* Alignment */
.alignment-guide {
  position: absolute;
  top: 0;
  left: 50%;
  width: 1px;
  height: 100%;
  background: rgba(0,0,0,0.05);
  transform: translateX(-50%);
}

/* Proximity */
.proximity-group {
  position: absolute;
  top: 20%;
  left: 10%;
  display: flex;
  gap: 2px;
  flex-wrap: wrap;
  width: 20px;
}
.p-sq {
  width: 6px;
  height: 6px;
  background: #d2e603; /* Yellow-green accent */
}

/* Hover Effects handled by parent mainly, but internal ones here */
</style>
