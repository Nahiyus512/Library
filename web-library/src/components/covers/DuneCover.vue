<template>
  <div class="dune-cover">
    <div class="sand-layer layer-1"></div>
    <div class="sand-layer layer-2"></div>
    <div class="sand-layer layer-3"></div>
    <div class="sun"></div>
    <div class="title-container">
      <h1 class="title">沙丘</h1>
    </div>
    <div class="particles">
      <div v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
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

.title {
  font-family: 'Microsoft YaHei', sans-serif;
  font-size: 3.5rem; /* Reduced from 8rem to fit in card */
  font-weight: 900;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  line-height: 1;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  opacity: 0.9;
  text-align: center;
  width: 100%;
  padding: 0 10px;
  box-sizing: border-box;
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
