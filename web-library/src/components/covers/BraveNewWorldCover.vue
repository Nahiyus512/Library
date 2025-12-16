<template>
  <div class="brave-cover" ref="coverRef" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <!-- Background Grid -->
    <div class="grid-bg"></div>
    
    <!-- Central Soma Pill -->
    <div class="soma-pill-container" :style="pillStyle">
      <div class="pill-body">
        <div class="pill-highlight"></div>
        <div class="pill-text">SOMA</div>
      </div>
      <div class="pill-glow"></div>
    </div>

    <!-- Title -->
    <div class="title-container" :style="titleStyle">
      <h1 class="title-en">BRAVE<br>NEW<br>WORLD</h1>
      <div class="motto">COMMUNITY • IDENTITY • STABILITY</div>
      <h2 class="title-cn">美丽新世界</h2>
    </div>

    <!-- Floating Particles -->
    <div class="particles">
      <div v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const coverRef = ref<HTMLElement | null>(null);
const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e: MouseEvent) => {
  if (!coverRef.value) return;
  const rect = coverRef.value.getBoundingClientRect();
  const x = (e.clientX - rect.left) / rect.width;
  const y = (e.clientY - rect.top) / rect.height;
  
  mouseX.value = (x - 0.5) * 2; // -1 to 1
  mouseY.value = (y - 0.5) * 2;
};

const resetParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const pillStyle = computed(() => ({
  transform: `translate(${mouseX.value * 15}px, ${mouseY.value * 15}px) rotate(${mouseX.value * 5}deg)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -10}px, ${mouseY.value * -10}px)`
}));

const getParticleStyle = (n: number) => {
  const size = Math.random() * 4 + 1;
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.5 + 0.1
  };
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@300;700;900&display=swap');

.brave-cover {
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, #1a2a3a 0%, #000000 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Montserrat', sans-serif;
  color: #cffafe;
}

.grid-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(rgba(0, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  perspective: 500px;
  transform: scale(1.2);
}

.soma-pill-container {
  position: absolute;
  z-index: 10;
  transition: transform 0.1s ease-out;
}

.pill-body {
  width: 160px;
  height: 80px;
  background: linear-gradient(135deg, #e0f7fa 0%, #00bcd4 100%);
  border-radius: 50px;
  position: relative;
  box-shadow: 
    inset 5px 5px 15px rgba(255,255,255,0.8),
    inset -5px -5px 15px rgba(0,0,0,0.2),
    0 10px 30px rgba(0, 255, 255, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.pill-body::after {
  content: '';
  position: absolute;
  top: 0; left: 50%;
  width: 2px; height: 100%;
  background: rgba(0,0,0,0.1);
}

.pill-highlight {
  position: absolute;
  top: 15px; left: 20px;
  width: 40px; height: 15px;
  background: rgba(255,255,255,0.9);
  border-radius: 20px;
  filter: blur(2px);
}

.pill-text {
  font-size: 1.2rem;
  font-weight: 900;
  letter-spacing: 2px;
  color: rgba(0,0,0,0.3);
  z-index: 2;
}

.pill-glow {
  position: absolute;
  top: 50%; left: 50%;
  width: 200px; height: 120px;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(0, 255, 255, 0.4) 0%, transparent 70%);
  filter: blur(20px);
  z-index: -1;
  animation: pulse 3s infinite ease-in-out;
}

.title-container {
  position: relative;
  z-index: 20;
  text-align: center;
  mix-blend-mode: overlay;
  pointer-events: none;
}

.title-en {
  font-size: 3.5rem;
  line-height: 0.9;
  font-weight: 900;
  letter-spacing: -2px;
  margin: 0;
  text-transform: uppercase;
  background: linear-gradient(to bottom, #fff, #84ffff);
  -webkit-background-clip: text;
  color: transparent;
  opacity: 0.9;
}

.motto {
  font-size: 0.5rem;
  letter-spacing: 2px;
  margin: 15px 0;
  color: #00bcd4;
  text-transform: uppercase;
  font-weight: 700;
}

.title-cn {
  font-size: 3.5rem;
  font-weight: 300;
  margin: 0;
  letter-spacing: 5px;
  opacity: 0.8;
  transition: font-size 0.3s ease;
}

.brave-cover:not(.hero) .title-cn {
  font-size: 1.8rem;
}

.particle {
  position: absolute;
  background: #00bcd4;
  border-radius: 50%;
  animation: floatUp 10s linear infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.1); }
}

@keyframes floatUp {
  0% { transform: translateY(100vh); opacity: 0; }
  20% { opacity: 0.5; }
  80% { opacity: 0.5; }
  100% { transform: translateY(-10vh); opacity: 0; }
}

/* Card Mode Adjustments */
.brave-cover:not(.hero) .title-en {
  font-size: 1.8rem;
}
.brave-cover:not(.hero) .pill-body {
  width: 100px;
  height: 50px;
}
.brave-cover:not(.hero) .motto {
  display: none;
}
</style>
