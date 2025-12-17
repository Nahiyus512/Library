<template>
  <div class="dune-cover" :class="{ 'card-mode': mode === 'card' }" ref="coverRef" @mousemove="handleMouseMove" @mouseleave="resetState">
    <div class="sand-layer layer-1" :style="layer1Style"></div>
    <div class="sand-layer layer-2" :style="layer2Style"></div>
    <div class="sand-layer layer-3" :style="layer3Style"></div>
    
    <!-- The Eye (Hidden until interaction?) or Sun -->
    <div class="sun" :style="sunStyle"></div>
    
    <div class="title-container" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="titleStyle">
      <h1 class="title">沙丘</h1>
    </div>
    
    <div class="particles">
      <div v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const props = defineProps<{
  mode?: 'card' | 'hero'
}>();

const coverRef = ref<HTMLElement | null>(null);
const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e: MouseEvent) => {
  if (!coverRef.value) return;
  const rect = coverRef.value.getBoundingClientRect();
  // Normalize -1 to 1
  mouseX.value = ((e.clientX - rect.left) / rect.width) * 2 - 1;
  mouseY.value = ((e.clientY - rect.top) / rect.height) * 2 - 1;
};

const resetState = () => {
  // Optional: slowly return to center or leave as is
  // mouseX.value = 0; 
  // mouseY.value = 0;
};

// Computed styles for parallax effect
const layer1Style = computed(() => ({
  transform: `translate(${mouseX.value * 10}px, ${mouseY.value * 5}px)`
}));

const layer2Style = computed(() => ({
  transform: `translate(${mouseX.value * 20}px, ${mouseY.value * 10}px)`
}));

const layer3Style = computed(() => ({
  transform: `translate(${mouseX.value * 30}px, ${mouseY.value * 15}px)`
}));

const sunStyle = computed(() => ({
  transform: `translate(${mouseX.value * -15}px, ${mouseY.value * -15}px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * 5}px, ${mouseY.value * 5}px)`
}));

const getParticleStyle = (n: number) => {
  const size = Math.random() * 3 + 1;
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.7 + 0.3
  };
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@900&display=swap');

.dune-cover {
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, #7B3306, #4A1E03);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-container {
  z-index: 10;
  position: relative;
  mix-blend-mode: overlay;
}

/* 状态：未展开 */
.state-collapsed .title {
  font-size: 4.5rem;
  font-weight: 900;
  color: #d2b48c; /* Tan/Sand color */
  text-transform: uppercase;
  letter-spacing: 10px;
  text-shadow: 2px 2px 10px rgba(0,0,0,0.5);
  z-index: 10;
  mix-blend-mode: overlay;
  opacity: 0.9;
  transition: all 0.3s ease;
}

/* 状态：已展开 */
.state-expanded .title {
  font-size: 8rem;
  font-weight: 900;
  color: #d2b48c; /* Tan/Sand color */
  text-transform: uppercase;
  letter-spacing: 10px;
  text-shadow: 2px 2px 10px rgba(0,0,0,0.5);
  z-index: 10;
  mix-blend-mode: overlay;
  opacity: 0.9;
  transition: all 0.3s ease;
}

/* Sand Layers (Abstract Curves) */
.sand-layer {
  position: absolute;
  width: 150%;
  height: 60%;
  border-radius: 50%;
  bottom: -20%;
  left: -25%;
  filter: blur(2px);
  transition: transform 0.2s ease-out;
}

.layer-1 {
  background: #964B00;
  bottom: -30%;
  z-index: 1;
  animation: shift 10s infinite alternate ease-in-out;
}

.layer-2 {
  background: #B35E05;
  bottom: -40%;
  left: -10%;
  z-index: 2;
  opacity: 0.8;
  animation: shift 15s infinite alternate-reverse ease-in-out;
}

.layer-3 {
  background: #D97706;
  bottom: -50%;
  left: 10%;
  z-index: 3;
  opacity: 0.6;
  animation: shift 12s infinite alternate ease-in-out;
}

.sun {
  position: absolute;
  top: 10%;
  right: 10%;
  width: 60px;
  height: 60px;
  background: #FFD700;
  border-radius: 50%;
  box-shadow: 0 0 40px #FF8C00;
  opacity: 0.8;
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 5;
}

.particle {
  position: absolute;
  background: #FFD700;
  border-radius: 50%;
  animation: float 5s infinite linear;
}

@keyframes shift {
  0% { transform: translateX(-2%) translateY(0); }
  100% { transform: translateX(2%) translateY(5%); }
}

@keyframes float {
  0% { transform: translateY(0) translateX(0); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translateY(-50px) translateX(20px); opacity: 0; }
}
</style>
