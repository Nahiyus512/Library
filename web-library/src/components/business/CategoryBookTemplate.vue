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
      <div class="history-layer layer-1"></div>
      <div class="history-layer layer-2"></div>
      <div class="cover-history-container">
        <component :is="coverComponent" mode="card" />
      </div>
      <div class="history-label">
        <span class="history-year">历史</span>
        <span class="history-text">{{ title }}</span>
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
      </div>
      <div class="scifi-info">
        <div class="scifi-glitch" :data-text="title">{{ title }}</div>
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
  if (props.routePath) {
    router.push(props.routePath);
  }
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
  background: #fff;
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
  border: 1px solid rgba(255, 255, 255, 0.4);
  outline: 1px solid rgba(255, 255, 255, 0.1);
  outline-offset: 4px;
}
.shell-philosophy:hover .cover-float-container {
  transform: translateY(-10px) scale(1.02);
  border-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 20px rgba(255,255,255,0.2);
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
}

/* --- KNOWLEDGE --- */
.shell-knowledge {
  width: 100%;
  padding: 15px;
  border: 1px solid rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(5px);
  position: relative;
}
.knowledge-grid-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: linear-gradient(#fff 1px, transparent 1px),
    linear-gradient(90deg, #fff 1px, transparent 1px);
  background-size: 15px 15px;
  background-position: -1px -1px;
  opacity: 0.1;
  z-index: 0;
}
.knowledge-markers .marker {
  position: absolute;
  color: #fff;
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
  border: 1px solid rgba(255,255,255,0.5);
  transition: all 0.3s;
  width: 100%;
  aspect-ratio: 1 / 1;
}
.shell-knowledge:hover .cover-tech-container {
  box-shadow: 6px 6px 0 rgba(255,255,255,0.2);
  transform: translate(-3px, -3px);
}
.knowledge-data {
  margin-top: 10px;
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
  color: #fff;
  z-index: 1;
  position: relative;
}

/* --- HISTORY --- */
.shell-history {
  width: 100%;
  position: relative;
  /* Removed bottom padding to increase visual size */
  padding: 10px; 
}
.history-layer {
  position: absolute;
  width: 95%;
  height: 100%;
  background: #fff;
  border: 1px solid #e0e0e0;
  transition: transform 0.4s ease;
}
.layer-1 { top: 6px; left: 6px; z-index: 0; background: #f4f4f4; }
.layer-2 { top: 12px; left: 12px; z-index: -1; background: #eaeaea; }
.shell-history:hover .layer-1 { transform: translate(6px, 6px) rotate(2deg); }
.shell-history:hover .layer-2 { transform: translate(12px, 12px) rotate(4deg); }

.cover-history-container {
  position: relative;
  z-index: 1;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  width: 100%;
  aspect-ratio: 1 / 1;
  /* Ensure image fits */
  display: flex;
  align-items: center;
  justify-content: center;
}
.history-label {
  margin-top: 15px;
  border-left: 3px solid #fff;
  padding-left: 10px;
}
.history-year {
  display: block;
  font-size: 0.7rem;
  color: rgba(255,255,255,0.7);
  letter-spacing: 2px;
}
.history-text {
  font-weight: bold;
  color: #fff;
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
  background: #fff;
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
}
.shell-life:hover .cover-life-container {
  transform: translateY(-8px) rotate(-1deg);
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
}

/* --- SCIFI --- */
.shell-scifi {
  width: 100%;
  perspective: 800px;
}
.cover-scifi-container {
  position: relative;
  border: 1px solid #fff;
  box-shadow: 0 0 10px rgba(255,255,255,0.3);
  overflow: hidden;
  transition: transform 0.3s;
  width: 100%;
  aspect-ratio: 1 / 1;
}
.shell-scifi:hover .cover-scifi-container {
  transform: rotateY(10deg) rotateX(5deg);
  box-shadow: 0 0 20px rgba(255,255,255,0.5);
}
.scan-line {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 4px;
  background: rgba(255,255,255,0.8);
  box-shadow: 0 0 10px #fff;
  animation: scan 3s linear infinite;
  opacity: 0.5;
  pointer-events: none;
}
@keyframes scan {
  0% { top: -10%; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 110%; opacity: 0; }
}
.scifi-info {
  margin-top: 15px;
  text-align: right;
}
.scifi-glitch {
  font-family: 'Courier New', monospace;
  color: #fff;
  font-weight: bold;
  position: relative;
  font-size: 1rem;
}
.scifi-glitch::before, .scifi-glitch::after {
  content: attr(data-text);
  position: absolute;
  top: 0; left: 0; width: 100%;
  background: #fdfdfd;
}
.shell-scifi:hover .scifi-glitch::before {
  left: 2px;
  text-shadow: -1px 0 red;
  animation: glitch-anim-1 2s infinite linear alternate-reverse;
}
.shell-scifi:hover .scifi-glitch::after {
  left: -2px;
  text-shadow: -1px 0 blue;
  animation: glitch-anim-2 3s infinite linear alternate-reverse;
}

@keyframes glitch-anim-1 {
  0% { clip-path: inset(20% 0 80% 0); }
  20% { clip-path: inset(60% 0 10% 0); }
  40% { clip-path: inset(40% 0 50% 0); }
  60% { clip-path: inset(80% 0 5% 0); }
  80% { clip-path: inset(10% 0 70% 0); }
  100% { clip-path: inset(30% 0 20% 0); }
}
@keyframes glitch-anim-2 {
  0% { clip-path: inset(10% 0 60% 0); }
  20% { clip-path: inset(30% 0 20% 0); }
  40% { clip-path: inset(70% 0 10% 0); }
  60% { clip-path: inset(20% 0 50% 0); }
  80% { clip-path: inset(50% 0 30% 0); }
  100% { clip-path: inset(5% 0 80% 0); }
}

</style>
