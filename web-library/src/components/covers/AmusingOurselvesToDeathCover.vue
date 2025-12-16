<template>
  <div class="amusing-cover">
    <!-- Background: Grid of turned-off screens -->
    <div class="screen-grid">
      <div v-for="n in 20" :key="n" class="mini-screen"></div>
    </div>

    <!-- Main TV Set -->
    <div class="tv-set-container" @mouseenter="isHovered = true" @mouseleave="isHovered = false">
      <div class="tv-chassis">
        <div class="tv-screen-bezel">
          <div class="tv-screen">
            <!-- Static Noise (Visible on Hover) -->
            <div class="static-noise" :class="{ active: isHovered }"></div>
          </div>
        </div>
        <!-- TV Controls -->
        <div class="tv-controls">
          <div class="knob top-knob"></div>
          <div class="knob bottom-knob"></div>
          <div class="speaker-grill">
            <div v-for="i in 5" :key="i" class="grill-line"></div>
          </div>
        </div>
      </div>
      
      <!-- Smiley Overlay (Positioned absolutely to allow dripping out) -->
      <div class="smiley-overlay" :class="{ 'glitching': isHovered }">
        <svg viewBox="0 0 100 150" class="melting-smiley" style="overflow: visible;">
          <defs>
            <filter id="goo">
              <feGaussianBlur in="SourceGraphic" stdDeviation="2" result="blur" />
              <feColorMatrix in="blur" mode="matrix" values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7" result="goo" />
              <feComposite in="SourceGraphic" in2="goo" operator="atop"/>
            </filter>
          </defs>
          <g filter="url(#goo)">
            <!-- Face -->
            <circle cx="50" cy="50" r="30" fill="#FFD700" />
            <!-- Melting Drips (Extended) -->
            <path d="M25 70 Q 30 90 25 130" stroke="#FFD700" stroke-width="8" fill="none" class="drip drip-1" />
            <path d="M50 80 Q 55 100 50 140" stroke="#FFD700" stroke-width="12" fill="none" class="drip drip-2" />
            <path d="M75 70 Q 70 90 75 125" stroke="#FFD700" stroke-width="8" fill="none" class="drip drip-3" />
            <!-- Eyes -->
            <circle cx="38" cy="40" r="4" fill="#000" class="eye left-eye" />
            <circle cx="62" cy="40" r="4" fill="#000" class="eye right-eye" />
            <!-- Mouth -->
            <path d="M35 60 Q 50 75 65 60" stroke="#000" stroke-width="3" fill="none" class="mouth" />
          </g>
        </svg>
      </div>

      <!-- Antenna -->
      <div class="antenna left-ant"></div>
      <div class="antenna right-ant"></div>
    </div>

    <div class="title-wrapper">
      <h1 class="title-cn">娱乐至死</h1>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const isHovered = ref(false);
</script>

<style scoped>
.amusing-cover {
  width: 100%;
  height: 100%;
  background-color: #050505;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
  font-family: sans-serif;
}

.title-wrapper {
  margin-top: -10px;
  z-index: 10;
  text-align: center;
}

.title-cn {
  font-family: 'Noto Serif SC', serif;
  font-size: 3.5rem;
  font-weight: 900;
  color: #fff;
  margin: 0;
  letter-spacing: 4px;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
  transition: font-size 0.3s ease;
}

.amusing-cover:not(.hero) .title-cn {
  font-size: 2.2rem;
}

/* Background Grid */
.screen-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(5, 1fr);
  gap: 2px;
  opacity: 0.2;
  z-index: 1;
}

.mini-screen {
  background: #111;
  border: 1px solid #222;
  border-radius: 2px;
  box-shadow: inset 0 0 5px #000;
}

/* TV Set */
.tv-set-container {
  position: relative;
  width: 180px;
  height: 140px;
  z-index: 10;
  transform: scale(0.9);
  transition: transform 0.3s ease;
}

.tv-set-container:hover {
  transform: scale(1.05) rotate(-1deg);
}

.tv-chassis {
  width: 100%;
  height: 100%;
  background: #2a2a2a;
  border-radius: 10px;
  padding: 8px;
  display: flex;
  box-shadow: 
    inset 2px 2px 5px rgba(255,255,255,0.1),
    inset -2px -2px 5px rgba(0,0,0,0.5),
    5px 5px 15px rgba(0,0,0,0.5);
  position: relative;
  z-index: 2;
}

.tv-screen-bezel {
  flex: 3;
  background: #111;
  border-radius: 20px; /* CRT curve */
  padding: 4px;
  box-shadow: inset 1px 1px 3px rgba(255,255,255,0.1);
  overflow: hidden;
  position: relative;
}

.tv-screen {
  width: 100%;
  height: 100%;
  background: #000;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.8);
}

.tv-screen::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.1), transparent 40%);
  pointer-events: none;
  z-index: 10;
  border-radius: 16px;
}

/* Controls */
.tv-controls {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-left: 5px;
}

.knob {
  width: 16px;
  height: 16px;
  background: #444;
  border-radius: 50%;
  margin: 5px 0;
  box-shadow: 1px 1px 2px #000;
  position: relative;
}

.knob::after {
  content: "";
  position: absolute;
  top: 2px;
  left: 50%;
  width: 2px;
  height: 6px;
  background: #888;
  transform: translateX(-50%);
}

.speaker-grill {
  margin-top: 10px;
  width: 20px;
}

.grill-line {
  height: 2px;
  background: #111;
  margin-bottom: 2px;
  border-radius: 1px;
}

/* Antenna */
.antenna {
  position: absolute;
  bottom: 95%;
  width: 2px;
  height: 60px;
  background: #888;
  z-index: 1;
}

.left-ant {
  left: 40%;
  transform: rotate(-30deg);
  transform-origin: bottom center;
}

.right-ant {
  right: 40%;
  transform: rotate(30deg);
  transform-origin: bottom center;
}

/* Smiley & Melting */
.smiley-overlay {
  position: absolute;
  top: 12px;
  left: 12px;
  width: 115px; /* Based on flex 3:1 layout approx */
  height: 116px;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 20;
  pointer-events: none;
}

.melting-smiley {
  width: 90%;
  height: 110%; /* Taller to allow dripping out */
  transform: translateY(10%); /* Shift down slightly */
}

.drip {
  animation: dripAnim 3s infinite ease-in;
}

.drip-1 { animation-delay: 0s; }
.drip-2 { animation-delay: 1s; }
.drip-3 { animation-delay: 2s; }

@keyframes dripAnim {
  0% { stroke-dasharray: 0 60; stroke-dashoffset: 0; }
  50% { stroke-dasharray: 30 60; stroke-dashoffset: -10; }
  100% { stroke-dasharray: 0 60; stroke-dashoffset: -50; }
}

.eye {
  animation: blink 4s infinite;
}

@keyframes blink {
  0%, 48%, 52%, 100% { transform: scaleY(1); }
  50% { transform: scaleY(0.1); }
}

/* Static Noise */
.static-noise {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='1'/%3E%3C/svg%3E");
  opacity: 0;
  pointer-events: none;
  z-index: 5;
  mix-blend-mode: overlay;
}

.static-noise.active {
  opacity: 0.4;
  animation: noise-shift 0.2s steps(4) infinite;
}

@keyframes noise-shift {
  0% { transform: translate(0,0); }
  25% { transform: translate(-5px, 5px); }
  50% { transform: translate(5px, -5px); }
  75% { transform: translate(-5px, -5px); }
  100% { transform: translate(5px, 5px); }
}

.smiley-overlay.glitching {
  animation: glitch-skew 0.3s infinite;
  filter: url(#goo) hue-rotate(90deg); /* Color shift on hover */
}

@keyframes glitch-skew {
  0% { transform: skew(0deg); }
  20% { transform: skew(-10deg); }
  40% { transform: skew(10deg); }
  60% { transform: skew(-5deg); }
  80% { transform: skew(5deg); }
  100% { transform: skew(0deg); }
}
</style>
