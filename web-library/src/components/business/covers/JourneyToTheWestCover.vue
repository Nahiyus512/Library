<template>
  <div class="journey-cover" :class="{ 'card-mode': mode === 'card', 'hero': mode === 'hero' }" ref="coverRef" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <!-- Dynamic Cloud Background -->
    <div class="clouds-layer">
      <div class="cloud cloud-1"></div>
      <div class="cloud cloud-2"></div>
      <div class="cloud cloud-3"></div>
    </div>

    <!-- Central Interactive Object: Golden Cudgel -->
    <div class="cudgel-container" :style="cudgelStyle">
      <div class="cudgel-bar">
        <div class="cudgel-shimmer"></div>
        <div class="cudgel-text">如意金箍棒</div>
      </div>
      <div class="cudgel-glow"></div>
    </div>

    <!-- Title Group -->
    <div class="title-container" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="titleStyle">
      <h1 class="title-cn">西游记</h1>
      <div class="motto">PASSION • DISCIPLINE • ENLIGHTENMENT</div>
    </div>

    <!-- Floating Particles -->
    <div class="particles">
      <div v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

defineProps<{
  mode?: 'card' | 'hero'
}>();

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

const cudgelStyle = computed(() => ({
  transform: `translate(${mouseX.value * 20}px, ${mouseY.value * 20}px) rotate(${mouseX.value * 10}deg)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -15}px, ${mouseY.value * -15}px)`
}));

const getParticleStyle = (n: number) => {
  const size = Math.random() * 4 + 1;
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.6 + 0.2
  };
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@300;700;900&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700&display=swap');

.journey-cover {
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, #2c1e15 0%, #000000 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Montserrat', sans-serif;
  color: #ffd700;
}

/* Clouds */
.clouds-layer {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  opacity: 0.2;
  pointer-events: none;
  z-index: 1;
}

.cloud {
  position: absolute;
  background: radial-gradient(circle, rgba(255,255,255,0.8) 0%, rgba(255,255,255,0) 70%);
  border-radius: 50%;
  filter: blur(20px);
}

.cloud-1 { width: 300px; height: 300px; top: -100px; left: -100px; animation: float 25s infinite linear; }
.cloud-2 { width: 400px; height: 400px; bottom: -150px; right: -100px; animation: float 30s infinite reverse linear; }
.cloud-3 { width: 200px; height: 200px; top: 40%; left: 80%; animation: float 20s infinite ease-in-out; }

@keyframes float {
  0% { transform: translate(0, 0); }
  50% { transform: translate(30px, -30px); }
  100% { transform: translate(0, 0); }
}

/* Cudgel */
.cudgel-container {
  position: absolute;
  z-index: 2;
  transition: transform 0.1s ease-out;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cudgel-bar {
  width: 20px;
  height: 160px;
  background: linear-gradient(90deg, #b8860b, #ffd700, #b8860b);
  border-radius: 10px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.5s ease;
}

.cudgel-shimmer {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(
    180deg, 
    transparent 0%, 
    rgba(255, 255, 255, 0.6) 50%, 
    transparent 100%
  );
  transform: translateY(-100%);
  animation: shimmer 3s infinite linear;
}

.cudgel-text {
  writing-mode: vertical-rl;
  color: #5c3a21;
  font-family: "Kaiti SC", "STKaiti", serif;
  font-weight: bold;
  font-size: 1rem;
  letter-spacing: 4px;
  opacity: 0;
  transition: opacity 0.3s;
}

.journey-cover:hover .cudgel-bar {
  height: 240px;
  width: 30px;
  box-shadow: 0 0 40px rgba(255, 215, 0, 0.8);
}

.journey-cover:hover .cudgel-text {
  opacity: 1;
}

.cudgel-glow {
  position: absolute;
  width: 100px; height: 300px;
  background: radial-gradient(ellipse at center, rgba(255,215,0,0.2) 0%, transparent 70%);
  pointer-events: none;
}

@keyframes shimmer {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100%); }
}

/* Title */
.title-container {
  position: relative;
  z-index: 3;
  text-align: center;
  pointer-events: none;
  transition: transform 0.1s ease-out;
  mix-blend-mode: overlay;
}

.title-en {
  font-size: 5rem;
  font-weight: 900;
  line-height: 0.9;
  letter-spacing: -2px;
  margin: 0;
  opacity: 0.8;
  color: #fff;
}

/* 状态：未展开 */
.state-collapsed .title-cn {
  font-family: "Noto Serif SC", serif;
  font-size: 3.5rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: 0.5rem;
  color: #ffd700;
  transition: all 0.3s;
}

.state-collapsed .motto {
  font-size: 0.8rem;
  letter-spacing: 4px;
  margin: 15px 0;
  color: #ffd700;
  font-weight: 700;
  display: none;
}

/* 状态：已展开 */
.state-expanded .title-cn {
  font-family: "Noto Serif SC", serif;
  font-size: 5rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: 0.5rem;
  color: #ffd700;
  transition: all 0.3s;
}

.state-expanded .motto {
  font-size: 0.8rem;
  letter-spacing: 4px;
  margin: 15px 0;
  color: #ffd700;
  font-weight: 700;
  display: block;
}

/* Particles */
.particles {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  background: #ffd700;
  border-radius: 50%;
  animation: float-particle 10s infinite ease-in-out;
}

@keyframes float-particle {
  0%, 100% { transform: translate(0, 0); opacity: 0; }
  50% { opacity: 0.8; }
  25% { transform: translate(20px, -20px); }
  75% { transform: translate(-20px, 20px); }
}

/* Hero Mode Overrides */
.hero .cudgel-bar {
  width: 30px;
  height: 200px;
}
</style>