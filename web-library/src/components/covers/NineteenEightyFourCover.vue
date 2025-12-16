<template>
  <div class="cover-1984" ref="coverRef" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <div class="scanlines"></div>
    <div class="eye-container" :style="eyeStyle">
      <div class="sclera">
        <div class="iris">
          <div class="pupil"></div>
          <div class="reflection"></div>
        </div>
      </div>
      <div class="eyelid-top"></div>
      <div class="eyelid-bottom"></div>
    </div>
    
    <div class="title-container" :style="titleStyle">
      <h1 class="glitch-text" data-text="1984">1984</h1>
      <div class="sub-text">BIG BROTHER IS WATCHING YOU</div>
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
  // Normalize -1 to 1
  mouseX.value = ((e.clientX - rect.left) / rect.width) * 2 - 1;
  mouseY.value = ((e.clientY - rect.top) / rect.height) * 2 - 1;
};

const resetParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const eyeStyle = computed(() => {
  return {
    transform: `translate(${mouseX.value * 10}px, ${mouseY.value * 10}px)`
  };
});

const titleStyle = computed(() => {
  return {
    transform: `translate(${mouseX.value * -5}px, ${mouseY.value * -5}px)`
  };
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Courier+Prime:wght@700&display=swap');

.cover-1984 {
  width: 100%;
  height: 100%;
  background-color: #050505;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-family: 'Courier Prime', monospace;
  color: #39ff14; /* Terminal Green */
}

.scanlines {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0),
    rgba(255, 255, 255, 0) 50%,
    rgba(0, 0, 0, 0.2) 50%,
    rgba(0, 0, 0, 0.2)
  );
  background-size: 100% 4px;
  pointer-events: none;
  z-index: 10;
  animation: scrollScanlines 10s linear infinite;
}

.eye-container {
  width: 120px;
  height: 120px;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.1s ease-out;
  margin-bottom: 20px;
}

.sclera {
  width: 100px;
  height: 60px;
  background: #e0e0e0;
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(57, 255, 20, 0.2);
}

.iris {
  width: 40px;
  height: 40px;
  background: #222;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 2px solid #39ff14;
  animation: lookAround 5s infinite;
}

.pupil {
  width: 15px;
  height: 15px;
  background: #000;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.reflection {
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  position: absolute;
  top: 20%;
  left: 70%;
}

.eyelid-top, .eyelid-bottom {
  position: absolute;
  width: 110%;
  height: 50%;
  background: #050505;
  z-index: 5;
  transition: height 0.2s;
}

.eyelid-top {
  top: -10%;
  border-bottom: 2px solid #39ff14;
  animation: blinkTop 4s infinite;
}

.eyelid-bottom {
  bottom: -10%;
  border-top: 2px solid #39ff14;
  animation: blinkBottom 4s infinite;
}

.title-container {
  text-align: center;
  z-index: 5;
  transition: transform 0.1s ease-out;
}

.glitch-text {
  font-size: 3rem;
  font-weight: 900;
  margin: 0;
  letter-spacing: -2px;
  position: relative;
  color: #fff;
  text-shadow: 2px 2px #39ff14;
}

.glitch-text::before,
.glitch-text::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #050505;
}

.glitch-text::before {
  left: 2px;
  text-shadow: -1px 0 #ff00c1;
  clip: rect(44px, 450px, 56px, 0);
  animation: glitch-anim 5s infinite linear alternate-reverse;
}

.glitch-text::after {
  left: -2px;
  text-shadow: -1px 0 #00fff9;
  clip: rect(44px, 450px, 56px, 0);
  animation: glitch-anim2 5s infinite linear alternate-reverse;
}

.sub-text {
  font-size: 0.6rem;
  margin-top: 5px;
  opacity: 0.8;
  letter-spacing: 2px;
}

@keyframes lookAround {
  0%, 100% { transform: translate(-50%, -50%); }
  25% { transform: translate(-30%, -50%); }
  50% { transform: translate(-50%, -30%); }
  75% { transform: translate(-70%, -50%); }
}

@keyframes blinkTop {
  0%, 48%, 52%, 100% { top: -20px; }
  50% { top: 25px; }
}

@keyframes blinkBottom {
  0%, 48%, 52%, 100% { bottom: -20px; }
  50% { bottom: 25px; }
}

@keyframes glitch-anim {
  0% { clip: rect(13px, 9999px, 81px, 0); }
  5% { clip: rect(82px, 9999px, 86px, 0); }
  /* ... simplified for brevity ... */
  100% { clip: rect(67px, 9999px, 20px, 0); }
}

@keyframes glitch-anim2 {
  0% { clip: rect(65px, 9999px, 100px, 0); }
  100% { clip: rect(10px, 9999px, 60px, 0); }
}

@keyframes scrollScanlines {
  0% { background-position: 0 0; }
  100% { background-position: 0 100%; }
}
</style>
