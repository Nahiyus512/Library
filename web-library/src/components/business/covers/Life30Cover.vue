<template>
  <div 
    class="visual-life30" 
    :class="{ 'card-mode': mode === 'card' }"
    ref="cardRef"
    @mousemove="handleMouseMove"
    @mouseleave="handleMouseLeave"
  >
    <div class="network-grid">
      <div class="glow-orb"></div>
      <div class="grid-lines"></div>
      
      <!-- Evolving Shape -->
      <div class="shape-container">
        <div class="shape-layer layer-1"></div>
        <div class="shape-layer layer-2"></div>
        <div class="shape-layer layer-3"></div>
      </div>

      <div class="text-container" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="textTransform">
      <div class="main-title-group">
        <span class="huge-text">生命</span>
        <span class="version-text">3.0</span>
      </div>
      <div class="subtitle-text">未来已来</div>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
  mode?: 'card' | 'hero'
}>();

const cardRef = ref<HTMLElement | null>(null);
const textTransform = ref('');

const handleMouseMove = (e: MouseEvent) => {
  if (!cardRef.value) return;
  
  const rect = cardRef.value.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  
  // Calculate rotation (max 20 degrees)
  const rotateY = ((x - centerX) / centerX) * 20;
  const rotateX = -((y - centerY) / centerY) * 20;

  textTransform.value = `transform: perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.1);`;
};

const handleMouseLeave = () => {
  textTransform.value = 'transform: perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1); transition: transform 0.5s ease;';
};
</script>

<style scoped>
.visual-life30 {
  width: 100%; height: 100%;
  background: #000;
  position: relative; 
  overflow: hidden;
  display: flex; 
  align-items: center; 
  justify-content: center;
}

.network-grid {
  width: 100%; height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at center, #1e1b4b 0%, #000 70%);
}

.glow-orb {
  position: absolute;
  width: 200px; height: 200px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.2) 0%, transparent 70%);
  filter: blur(20px);
  animation: pulse 4s infinite ease-in-out;
}

.grid-lines {
  position: absolute;
  width: 200%; height: 200%;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  transform: perspective(500px) rotateX(60deg) translateY(-50px) translateZ(-100px);
  animation: grid-move 20s linear infinite;
}

.shape-container {
  position: absolute;
  width: 100%; height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.shape-layer {
  position: absolute;
  border: 1px solid rgba(99, 102, 241, 0.3);
  border-radius: 50%;
}

.layer-1 {
  width: 120px; height: 120px;
  border-style: dashed;
  animation: spin 10s linear infinite;
}

.layer-2 {
  width: 160px; height: 160px;
  border: 1px solid rgba(168, 85, 247, 0.2);
  transform: rotate(45deg);
  animation: spin-reverse 15s linear infinite;
}

.layer-3 {
  width: 200px; height: 200px;
  border: 2px solid rgba(255, 255, 255, 0.05);
  animation: pulse-scale 5s ease-in-out infinite;
}

.text-container {
  z-index: 10;
  position: relative; /* Added position relative */
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  /* Removed mix-blend-mode overlay to ensure text visibility */
}

.main-title-group {
  position: relative;
  display: flex;
  align-items: baseline;
  justify-content: center;
}

/* 状态：未展开 */
.state-collapsed .huge-text {
  font-size: 4rem;
  font-weight: 900;
  color: #fff;
  line-height: 1;
  text-shadow: 0 0 20px rgba(11, 161, 211, 0.5);
  margin-right: 10px;
  transition: all 0.3s ease;
}

.state-collapsed .version-text {
  font-size: 1.5rem;
  font-weight: 500;
  color: rgb(162, 169, 194);
  letter-spacing: 2px;
  transition: all 0.3s ease;
}

.state-collapsed .subtitle-text {
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 8px;
  margin-top: 10px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

/* 状态：已展开 */
.state-expanded .huge-text {
  font-size: 6rem; /* Increased significantly */
  font-weight: 900;
  color: #fff;
  line-height: 1;
  text-shadow: 0 0 30px rgba(26, 127, 158, 0.8);
  margin-right: 20px;
  transition: all 0.3s ease;
}

.state-expanded .version-text {
  font-size: 4rem; /* Increased significantly */
  font-weight: 500;
  color: rgb(145, 146, 177);
  letter-spacing: 4px;
  transition: all 0.3s ease;
}

.state-expanded .subtitle-text {
  font-family: 'Inter', sans-serif;
  font-size: 1.5rem; /* Increased */
  color: rgba(255, 255, 255, 0.9);
  letter-spacing: 12px;
  margin-top: 20px;
  text-transform: uppercase;
  font-weight: bold;
  transition: all 0.3s ease;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

@keyframes grid-move {
  0% { transform: perspective(500px) rotateX(60deg) translateY(0); }
  100% { transform: perspective(500px) rotateX(60deg) translateY(40px); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes spin-reverse {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(-360deg); }
}

@keyframes pulse-scale {
  0%, 100% { transform: scale(1); border-color: rgba(255,255,255,0.05); }
  50% { transform: scale(1.05); border-color: rgba(255,255,255,0.2); }
}
</style>
