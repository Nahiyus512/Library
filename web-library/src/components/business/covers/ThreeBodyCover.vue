<template>
  <div 
    class="visual-three-body" 
    :class="{ 'card-mode': mode === 'card', hero: mode === 'hero' }"
    ref="cardRef"
    @mousemove="handleMouseMove"
    @mouseleave="handleMouseLeave"
  >
    <div class="sky-chaos">
      <div class="death-countdown-overlay">{{ countdownDisplay }}</div>
      <div class="star-system">
        <div class="star star-1"></div>
        <div class="star star-2"></div>
        <div class="star star-3"></div>
      </div>
      <div class="three-body-text-container" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'" :style="textTransform">
        <div class="huge-text">三体</div>
        <div class="warning-wrapper">
          <div class="warning-text">不要回答</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';

const props = defineProps<{
  mode?: 'card' | 'hero'
}>();

// --- Interactive Title Logic ---
const cardRef = ref<HTMLElement | null>(null);
const textTransform = ref('');

const handleMouseMove = (e: MouseEvent) => {
  if (!cardRef.value) return;
  
  const rect = cardRef.value.getBoundingClientRect();
  const x = e.clientX - rect.left; // x position within the element
  const y = e.clientY - rect.top;  // y position within the element
  
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  
  // Calculate rotation (max 15 degrees)
  const rotateY = ((x - centerX) / centerX) * 15;
  const rotateX = -((y - centerY) / centerY) * 15;

  textTransform.value = `transform: perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.1);`;
};

const handleMouseLeave = () => {
  textTransform.value = 'transform: perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1); transition: transform 0.5s ease;';
};

// --- Countdown Logic ---
const remainingTime = ref(1194 * 3600 + 50 * 60 + 39); // Initial seconds
const countdownDisplay = computed(() => {
  if (remainingTime.value <= 0) return "00:00:00";
  const h = Math.floor(remainingTime.value / 3600);
  const m = Math.floor((remainingTime.value % 3600) / 60);
  const s = Math.floor(remainingTime.value % 60);
  return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
});

let timer: any;

onMounted(() => {
  timer = setInterval(() => {
    remainingTime.value--;
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timer);
});
// --- End Countdown Logic ---
</script>

<style scoped>
/* --- Three-Body Problem (Red Coast) --- */
.visual-three-body {
  width: 100%; height: 100%;
  background: #000;
  position: relative; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}

.sky-chaos {
  position: absolute; width: 100%; height: 100%;
  top: 0; left: 0;
  background: #000;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
}

.three-body-text-container {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  z-index: 10;
  mix-blend-mode: difference;
}

/* 状态：未展开 */
.state-collapsed .huge-text {
  font-size: 4.8rem;
  font-weight: 900;
  letter-spacing: 0px;
  color: #fff;
  text-shadow: 0 0 20px rgba(255,255,255,0.5);
  margin: 0;
  z-index: 10;
  mix-blend-mode: difference;
  animation: breathe 4s ease-in-out infinite;
  transition: all 0.3s ease;
}

/* 状态：已展开 */
.state-expanded .huge-text {
  font-size: 7rem;
  font-weight: 900;
  letter-spacing: 0px;
  color: #fff;
  text-shadow: 0 0 20px rgba(255,255,255,0.5);
  margin: 0;
  z-index: 10;
  mix-blend-mode: difference;
  animation: breathe 4s ease-in-out infinite;
  transition: all 0.3s ease;
}

.warning-wrapper {
  margin-top: 5px;
  opacity: 1; /* Always visible */
}

/* Removed hover opacity change */

.warning-text {
  font-size: 0.4rem;
  color: #ff0000;
  font-weight: bold;
  letter-spacing: 3px;
  animation: blinkWarning 2s infinite;
}

.death-countdown-overlay {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%) perspective(500px) rotateX(20deg) scale(1.5);
  font-family: monospace;
  font-size: 1.5rem;
  font-weight: 700;
  color: rgba(255, 0, 0, 0.8);
  pointer-events: none;
  white-space: nowrap;
  z-index: 1;
  width: 100%;
  text-align: center;
  opacity: 1; /* Always visible */
}

/* Removed hover opacity change */

@keyframes blinkWarning {
  0%, 100% { opacity: 1; text-shadow: 0 0 10px #ff0000; }
  50% { opacity: 0.3; text-shadow: none; }
}

/* Three-Star System */
.star-system {
  position: absolute;
  top: 50%; left: 50%;
  width: 100%; height: 100%; /* Cover full container to allow wider orbits */
  transform: translate(-50%, -50%);
  z-index: 0;
  pointer-events: none;
}

.star {
  position: absolute;
  top: 50%; left: 50%; /* Center start point */
  border-radius: 50%;
  filter: blur(2px);
  box-shadow: 0 0 10px currentColor;
}

.star-1 {
  width: 15px; height: 15px;
  background: #ffaa00;
  color: #ffaa00;
  animation: orbit1 4s infinite linear;
}

.star-2 {
  width: 10px; height: 10px;
  background: #00aaff;
  color: #00aaff;
  animation: orbit2 5s infinite linear;
}

.star-3 {
  width: 20px; height: 20px;
  background: #ff4444;
  color: #ff4444;
  animation: orbit3 6s infinite linear;
}

/* Chaotic orbits around center */
@keyframes orbit1 {
  0% { transform: rotate(0deg) translateX(80px) rotate(0deg); }
  100% { transform: rotate(360deg) translateX(80px) rotate(-360deg); }
}

@keyframes orbit2 {
  0% { transform: rotate(120deg) translateX(100px) rotate(-120deg); }
  100% { transform: rotate(480deg) translateX(100px) rotate(-480deg); }
}

@keyframes orbit3 {
  0% { transform: rotate(240deg) translateX(60px) rotate(-240deg); }
  100% { transform: rotate(600deg) translateX(60px) rotate(-600deg); }
}
</style>
