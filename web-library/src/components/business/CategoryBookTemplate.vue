<template>
  <div 
    class="book-template-wrapper" 
    :class="[`variant-${variant}`, { 'is-clickable': !!routePath }]" 
    :style="wrapperStyle"
    @click="handleClick"
  >
    
    <!-- CLASSIC VARIANT: Museum Frame -->
    <div v-if="variant === 'classic'" class="shell-classic">
      <div class="classic-frame-outer">
        <div class="classic-frame-inner">
          <div class="cover-container">
            <component :is="coverComponent" mode="card" />
          </div>
        </div>
        <div class="classic-plaque">
          <span class="classic-title">{{ title }}</span>
          <span class="classic-author">{{ author }}</span>
        </div>
      </div>
    </div>

    <!-- PHILOSOPHY VARIANT: Floating Void -->
    <div v-else-if="variant === 'philosophy'" class="shell-philosophy">
      <div class="philosophy-orb"></div>
      <div class="cover-float-container has-border">
        <component :is="coverComponent" mode="card" />
      </div>
      <div class="philosophy-info">
        <h3>{{ title }}</h3>
        <p>{{ author }}</p>
      </div>
    </div>

    <!-- KNOWLEDGE VARIANT: Blueprint Grid -->
    <div v-else-if="variant === 'knowledge'" class="shell-knowledge">
      <div class="knowledge-grid-bg"></div>
      <div class="knowledge-markers">
        <div class="marker m-tl">+</div>
        <div class="marker m-tr">+</div>
        <div class="marker m-bl">+</div>
        <div class="marker m-br">+</div>
      </div>
      <div class="cover-tech-container">
        <component :is="coverComponent" mode="card" />
      </div>
      <div class="knowledge-data">
        <div class="data-line">ID: {{ Math.floor(Math.random() * 1000) }}</div>
        <div class="data-title">{{ title }} // {{ author }}</div>
      </div>
    </div>

    <!-- HISTORY VARIANT: Time Layers -->
    <div v-else-if="variant === 'history'" class="shell-history">
      <div class="history-visual-wrapper">
        <div class="history-layer layer-1"></div>
        <div class="history-layer layer-2"></div>
        <div class="cover-history-container">
          <component :is="coverComponent" mode="card" />
        </div>
      </div>
    </div>

    <!-- LIFE VARIANT: Organic Card -->
    <div v-else-if="variant === 'life'" class="shell-life">
      <div class="life-blob"></div>
      <div class="cover-life-container">
        <component :is="coverComponent" mode="card" />
      </div>
      <div class="life-tag">
        <span>{{ title }}</span>
      </div>
    </div>

    <!-- SCI-FI VARIANT: Hologram -->
    <div v-else-if="variant === 'scifi'" class="shell-scifi">
      <div class="hologram-base"></div>
      <div class="cover-scifi-container">
        <component :is="coverComponent" mode="card" />
        <div class="scan-line"></div>
        <div class="corner-bracket tl"></div>
        <div class="corner-bracket tr"></div>
        <div class="corner-bracket bl"></div>
        <div class="corner-bracket br"></div>
      </div>
      <div class="scifi-info-panel">
        <div class="scifi-id">ID: {{ Math.floor(Math.random() * 9000) + 1000 }}</div>
        <div class="scifi-title-box">
          <div class="scifi-glitch-title" :data-text="title">{{ title }}</div>
        </div>
        <div class="scifi-author">AUTHOR: {{ author }}</div>
      </div>
    </div>

    <!-- DEFAULT FALLBACK -->
    <div v-else class="shell-default">
      <component :is="coverComponent" mode="card" />
      <h3>{{ title }}</h3>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { transitionState } from '@/store/transitionStore';

const props = defineProps<{
  coverComponent: any;
  title: string;
  author: string;
  themeColor: string;
  variant: 'classic' | 'philosophy' | 'knowledge' | 'history' | 'life' | 'scifi' | 'default';
  routePath?: string;
}>();

const router = useRouter();

const wrapperStyle = computed(() => ({
  '--theme-color': props.themeColor,
}));

const handleClick = () => {
  if (!props.routePath) return;

  transitionState.startAnimation(
    props.themeColor,
    props.title,
    '#ffffff'
  );

  setTimeout(() => {
    router.push(props.routePath);

    setTimeout(() => {
      transitionState.endAnimation();
    }, 500);
  }, 350);
};
</script>

<style scoped>
.book-template-wrapper {
  position: relative;
  margin: 30px;
  perspective: 1000px;
  transition: transform 0.2s;
}
.book-template-wrapper.is-clickable {
  cursor: pointer;
}
.book-template-wrapper.is-clickable:active {
  transform: scale(0.98);
}

/* Common Cover Container Style for 1:1 Aspect Ratio */
.cover-container-common {
  width: 100%;
  aspect-ratio: 1 / 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* --- CLASSIC --- */
.shell-classic {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: #fdfbf7;
  box-shadow: 0 10px 20px -5px rgba(0,0,0,0.3);
  border: 1px solid #d4c5b0;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.4s ease;
}
.shell-classic:hover {
  transform: translateY(-5px) rotateX(5deg);
}
.classic-frame-outer {
  position: relative;
  width: 100%;
  border: 4px double var(--theme-color);
  padding: 10px;
}
.classic-frame-inner {
  box-shadow: inset 0 0 15px rgba(0,0,0,0.2);
}
.cover-container {
  width: 100%;
  aspect-ratio: 1 / 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.classic-plaque {
  margin-top: 15px;
  text-align: center;
  font-family: 'Georgia', serif;
  border-top: 1px solid var(--theme-color);
  padding-top: 8px;
  width: 100%;
}
.classic-title {
  display: block;
  font-weight: bold;
  font-size: 1.1rem;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.classic-author {
  font-size: 0.9rem;
  color: #666;
  font-style: italic;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* --- PHILOSOPHY --- */
.shell-philosophy {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}
.philosophy-orb {
  position: absolute;
  width: 80%;
  padding-bottom: 80%;
  background: var(--theme-color);
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.15;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.5s ease;
}
.shell-philosophy:hover .philosophy-orb {
  opacity: 0.3;
  width: 90%;
  padding-bottom: 90%;
}
.cover-float-container {
  z-index: 2;
  transition: transform 0.5s ease;
  box-shadow: 0 15px 30px rgba(0,0,0,0.3);
  width: 100%;
  aspect-ratio: 1 / 1;
}
.cover-float-container.has-border {
  border: 2px solid var(--theme-color);
  box-shadow: 0 0 15px var(--theme-color);
}
.shell-philosophy:hover .cover-float-container {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 0 30px var(--theme-color);
}
.philosophy-info {
  margin-top: 20px;
  text-align: center;
  opacity: 0.9;
  color: #fff;
  transition: opacity 0.3s;
  width: 100%;
}
.shell-philosophy:hover .philosophy-info {
  opacity: 1;
  color: var(--theme-color);
}

/* --- KNOWLEDGE --- */
.shell-knowledge {
  width: 100%;
  padding: 15px;
  border: 1px solid var(--theme-color);
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(5px);
  position: relative;
}
.knowledge-grid-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: linear-gradient(var(--theme-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--theme-color) 1px, transparent 1px);
  background-size: 15px 15px;
  background-position: -1px -1px;
  opacity: 0.1;
  z-index: 0;
}
.knowledge-markers .marker {
  position: absolute;
  color: var(--theme-color);
  font-weight: bold;
  font-size: 12px;
}
.m-tl { top: 2px; left: 2px; }
.m-tr { top: 2px; right: 2px; }
.m-bl { bottom: 2px; left: 2px; }
.m-br { bottom: 2px; right: 2px; }
.cover-tech-container {
  position: relative;
  z-index: 1;
  border: 1px solid var(--theme-color);
  transition: all 0.3s;
  width: 100%;
  aspect-ratio: 1 / 1;
}
.shell-knowledge:hover .cover-tech-container {
  box-shadow: 6px 6px 0 var(--theme-color);
  transform: translate(-3px, -3px);
}
.knowledge-data {
  margin-top: 10px;
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
  color: var(--theme-color);
  z-index: 1;
  position: relative;
}

/* --- HISTORY --- */
.shell-history {
  width: 100%;
  position: relative;
  padding: 10px; 
  display: flex;
  flex-direction: column;
  align-items: center;
}

.history-visual-wrapper {
  position: relative;
  width: 180%;
  aspect-ratio: 1 / 1;
  margin-left: -40%;
  margin-bottom: 20px;
}

.history-layer {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #fff;
  border: 1px solid var(--theme-color);
  transition: transform 0.4s ease;
}
.layer-1 { top: 6px; left: 6px; z-index: 0; background: #f4f4f4; }
.layer-2 { top: 12px; left: 12px; z-index: -1; background: #eaeaea; }

.shell-history:hover .layer-1 { transform: translate(10px, 10px) rotate(3deg); }
.shell-history:hover .layer-2 { transform: translate(20px, 20px) rotate(6deg); }

.cover-history-container {
  position: relative;
  z-index: 1;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--theme-color);
  background: #fff;
  transition: transform 0.4s ease;
}

.shell-history:hover .cover-history-container {
  transform: rotate(-3deg);
  box-shadow: 0 15px 30px rgba(0,0,0,0.2);
}

.history-label {
  margin-top: 10px;
  border-left: 3px solid var(--theme-color);
  padding-left: 10px;
  width: 100%;
  align-self: flex-start;
}

.history-text {
  font-weight: bold;
  color: var(--theme-color);
  font-size: 1rem;
}

/* --- LIFE --- */
.shell-life {
  width: 100%;
  position: relative;
}
.life-blob {
  position: absolute;
  width: 120%;
  height: 90%;
  background: var(--theme-color);
  bottom: -20px;
  left: -10%;
  border-radius: 40% 60% 70% 30% / 40% 50% 60% 50%;
  opacity: 0.2;
  transition: all 0.5s ease;
  z-index: 0;
}
.shell-life:hover .life-blob {
  border-radius: 60% 40% 30% 70% / 50% 40% 50% 60%;
  transform: scale(1.1);
}
.cover-life-container {
  position: relative;
  z-index: 1;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
  transition: transform 0.3s;
  width: 100%;
  aspect-ratio: 1 / 1;
  border: 2px solid var(--theme-color);
}
.shell-life:hover .cover-life-container {
  transform: translateY(-8px) rotate(-1deg);
  box-shadow: 0 15px 30px var(--theme-color);
}
.life-tag {
  position: absolute;
  bottom: -10px;
  right: 10px;
  background: #fff;
  padding: 6px 15px;
  border-radius: 20px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  z-index: 2;
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--theme-color);
  border: 1px solid var(--theme-color);
}

/* --- SCIFI --- */
.shell-scifi {
  width: 100%;
  perspective: 800px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.cover-scifi-container {
  position: relative;
  border: 1px solid rgba(0, 240, 255, 0.3);
  box-shadow: 0 0 15px rgba(0, 240, 255, 0.1);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  width: 100%;
  aspect-ratio: 1 / 1;
  background: rgba(5, 5, 16, 0.6);
}

.shell-scifi:hover .cover-scifi-container {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 0 25px rgba(0, 240, 255, 0.4);
  border-color: rgba(0, 240, 255, 0.8);
}

.scan-line {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 2px;
  background: #00F0FF;
  box-shadow: 0 0 10px #00F0FF;
  animation: scan 3s linear infinite;
  opacity: 0.8;
  pointer-events: none;
  z-index: 10;
}

.corner-bracket {
  position: absolute;
  width: 10px; height: 10px;
  border-color: #00F0FF;
  border-style: solid;
  transition: all 0.3s ease;
  z-index: 5;
}
.tl { top: 0; left: 0; border-width: 2px 0 0 2px; }
.tr { top: 0; right: 0; border-width: 2px 2px 0 0; }
.bl { bottom: 0; left: 0; border-width: 0 0 2px 2px; }
.br { bottom: 0; right: 0; border-width: 0 2px 2px 0; }

.shell-scifi:hover .corner-bracket {
  width: 20px; height: 20px;
  box-shadow: 0 0 5px #00F0FF;
}

@keyframes scan {
  0% { top: -10%; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 110%; opacity: 0; }
}

.scifi-info-panel {
  background: rgba(0, 20, 40, 0.6);
  border: 1px solid rgba(0, 240, 255, 0.2);
  padding: 10px;
  position: relative;
  overflow: hidden;
  clip-path: polygon(10px 0, 100% 0, 100% calc(100% - 10px), calc(100% - 10px) 100%, 0 100%, 0 10px);
  transition: all 0.3s;
}

.scifi-info-panel::before {
  content: '';
  position: absolute;
  top: 0; left: 0; width: 4px; height: 100%;
  background: #00F0FF;
  opacity: 0.5;
  transition: all 0.3s;
}

.shell-scifi:hover .scifi-info-panel {
  background: rgba(0, 40, 80, 0.8);
  border-color: rgba(0, 240, 255, 0.5);
}
.shell-scifi:hover .scifi-info-panel::before {
  opacity: 1;
  box-shadow: 0 0 8px #00F0FF;
}

.scifi-id {
  font-family: 'Courier New', monospace;
  font-size: 0.6rem;
  color: #00F0FF;
  opacity: 0.7;
  letter-spacing: 2px;
  margin-bottom: 5px;
}

.scifi-title-box {
  margin-bottom: 5px;
  border-bottom: 1px dashed rgba(0, 240, 255, 0.3);
  padding-bottom: 5px;
}

.scifi-glitch-title {
  font-family: 'DotGothic16', sans-serif; /* Use pixel font */
  color: #fff;
  font-size: 1.1rem;
  font-weight: bold;
  position: relative;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.scifi-glitch-title::before {
  content: attr(data-text);
  position: absolute;
  left: -2px;
  text-shadow: 1px 0 #ff00c1;
  top: 0;
  color: white;
  background: rgba(0, 20, 40, 0.6);
  overflow: hidden;
  clip: rect(0, 900px, 0, 0);
  animation: noise-anim-2 3s infinite linear alternate-reverse;
}

.scifi-glitch-title::after {
  content: attr(data-text);
  position: absolute;
  left: 2px;
  text-shadow: -1px 0 #00fff9;
  top: 0;
  color: white;
  background: rgba(0, 20, 40, 0.6);
  overflow: hidden;
  clip: rect(0, 900px, 0, 0);
  animation: noise-anim 2s infinite linear alternate-reverse;
}

@keyframes noise-anim {
  0% { clip: rect(2px, 9999px, 1px, 0); }
  5% { clip: rect(15px, 9999px, 20px, 0); }
  10% { clip: rect(5px, 9999px, 45px, 0); }
  15% { clip: rect(35px, 9999px, 10px, 0); }
  20% { clip: rect(55px, 9999px, 60px, 0); }
  25% { clip: rect(5px, 9999px, 40px, 0); }
  30% { clip: rect(25px, 9999px, 10px, 0); }
  35% { clip: rect(10px, 9999px, 80px, 0); }
  40% { clip: rect(45px, 9999px, 50px, 0); }
  45% { clip: rect(5px, 9999px, 30px, 0); }
  50% { clip: rect(65px, 9999px, 20px, 0); }
  55% { clip: rect(10px, 9999px, 5px, 0); }
  60% { clip: rect(35px, 9999px, 55px, 0); }
  65% { clip: rect(55px, 9999px, 25px, 0); }
  70% { clip: rect(5px, 9999px, 35px, 0); }
  75% { clip: rect(20px, 9999px, 10px, 0); }
  80% { clip: rect(50px, 9999px, 45px, 0); }
  85% { clip: rect(10px, 9999px, 55px, 0); }
  90% { clip: rect(35px, 9999px, 20px, 0); }
  95% { clip: rect(5px, 9999px, 75px, 0); }
  100% { clip: rect(15px, 9999px, 40px, 0); }
}

@keyframes noise-anim-2 {
  0% { clip: rect(15px, 9999px, 40px, 0); }
  20% { clip: rect(5px, 9999px, 75px, 0); }
  40% { clip: rect(35px, 9999px, 20px, 0); }
  60% { clip: rect(10px, 9999px, 55px, 0); }
  80% { clip: rect(50px, 9999px, 45px, 0); }
  100% { clip: rect(2px, 9999px, 1px, 0); }
}

.scifi-author {
  font-family: 'Courier New', monospace;
  font-size: 0.7rem;
  color: #aaa;
  text-transform: uppercase;
}

</style>
