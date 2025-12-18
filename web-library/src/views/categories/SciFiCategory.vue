<template>
  <div class="category-page scifi-page">
    <div class="grid-overlay"></div>
    <div class="scanner-line"></div>
    
    <button class="return-btn" @click="goBack">
      [ <span class="blink">RETURN</span> ]
    </button>

    <div class="holo-container">
      <header class="scifi-header">
        <h1 class="glitch-text" data-text="科幻">科幻</h1>
        <div class="system-status">
          <span>SYSTEM: ONLINE</span>
          <span class="blink">_</span>
        </div>
      </header>

      <div class="content-container">
        <div class="holo-deck">
          <!-- 《三体》 -->
          <div class="holo-item delay-1">
             <CategoryBookTemplate
              :cover-component="ThreeBodyCover"
              title="三体"
              author="刘慈欣"
              theme-color="#00F0FF"
              variant="scifi"
              route-path="/book/ThreeBody"
            />
          </div>

          <!-- 《沙丘》 -->
          <div class="holo-item delay-2">
            <CategoryBookTemplate
              :cover-component="DuneCover"
              title="沙丘"
              author="Frank Herbert"
              theme-color="#00F0FF"
              variant="scifi"
              route-path="/book/Dune"
            />
          </div>

          <!-- 《银河系漫游指南》 -->
          <div class="holo-item delay-3">
             <CategoryBookTemplate
              :cover-component="HitchhikersGuideCover"
              title="银河系漫游指南"
              author="Douglas Adams"
              theme-color="#00F0FF"
              variant="scifi"
              route-path="/book/HitchhikersGuide"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import CategoryBookTemplate from '@/components/business/CategoryBookTemplate.vue';
import ThreeBodyCover from '@/components/business/covers/ThreeBodyCover.vue';
import DuneCover from '@/components/business/covers/DuneCover.vue';
import HitchhikersGuideCover from '@/components/business/covers/HitchhikersGuideCover.vue';

const router = useRouter();
const goBack = () => router.push('/main');

let observer: IntersectionObserver;

onMounted(() => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('active');
      }
    });
  }, { threshold: 0.1 });

  document.querySelectorAll('.holo-item').forEach(el => observer.observe(el));
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.category-page {
  height: 100vh;
  background-color: #050510;
  color: #00F0FF;
  position: relative;
  overflow: hidden;
  --theme-color: #00F0FF;
  font-family: 'Courier New', monospace;
  display: flex;
  flex-direction: column;
}

.grid-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(rgba(0, 240, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.1) 1px, transparent 1px);
  background-size: 40px 40px;
  perspective: 1000px;
  transform: scale(1.1);
  pointer-events: none;
  z-index: 0;
}
.scanner-line {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 5px;
  background: #00F0FF;
  box-shadow: 0 0 15px #00F0FF;
  animation: scan 4s linear infinite;
  opacity: 0.3;
  pointer-events: none;
  z-index: 0;
}

.return-btn {
  position: fixed;
  top: 30px; right: 30px;
  background: rgba(0, 240, 255, 0.1);
  border: 1px solid #00F0FF;
  color: #00F0FF;
  padding: 10px 20px;
  font-family: 'Courier New', monospace;
  font-weight: bold;
  cursor: pointer;
  z-index: 100;
  text-transform: uppercase;
  letter-spacing: 2px;
  transition: all 0.2s;
}
.return-btn:hover {
  background: #00F0FF;
  color: #000;
  box-shadow: 0 0 20px #00F0FF;
}

.holo-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.scifi-header {
  text-align: center;
  margin-top: 30px;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
  text-shadow: 0 0 10px #00F0FF;
  flex-shrink: 0;
}
.glitch-text {
  font-size: 4rem;
  margin: 0;
  position: relative;
  animation: glitch 3s infinite;
}
.glitch-text::before, .glitch-text::after {
  content: attr(data-text);
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: #050510;
}
.glitch-text::before {
  left: 2px;
  text-shadow: -1px 0 red;
  clip: rect(24px, 550px, 90px, 0);
  animation: glitch-anim-1 2.5s infinite linear alternate-reverse;
}
.glitch-text::after {
  left: -2px;
  text-shadow: -1px 0 blue;
  clip: rect(85px, 550px, 140px, 0);
  animation: glitch-anim-2 3s infinite linear alternate-reverse;
}

.system-status {
  font-size: 0.9rem;
  letter-spacing: 4px;
  margin-top: 10px;
  opacity: 0.8;
}

.content-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.holo-deck {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 40px;
  width: 95%;
  max-width: 1600px;
  perspective: 1000px;
}

.holo-item {
  width: 100%;
  max-width: 250px;
  opacity: 0;
  transform: translateY(50px) rotateX(20deg);
  transition: all 0.5s ease;
  position: relative;
}
.holo-item.active {
  opacity: 1;
  transform: translateY(0) rotateX(0);
}
.holo-item:hover {
  transform: translateY(-20px) scale(1.05);
  z-index: 10;
}
.holo-item::after {
  content: '';
  position: absolute;
  bottom: -30px; left: 50%;
  width: 80%; height: 10px;
  background: radial-gradient(ellipse at center, rgba(0, 240, 255, 0.5) 0%, transparent 70%);
  transform: translateX(-50%);
  filter: blur(5px);
}

.blink { animation: blink 1s infinite; }
@keyframes blink { 50% { opacity: 0; } }
@keyframes scan { 0% { top: 0; } 100% { top: 100%; } }

@keyframes glitch-anim-1 {
  0% { clip: rect(20px, 9999px, 80px, 0); }
  20% { clip: rect(60px, 9999px, 10px, 0); }
  100% { clip: rect(40px, 9999px, 90px, 0); }
}
@keyframes glitch-anim-2 {
  0% { clip: rect(80px, 9999px, 30px, 0); }
  20% { clip: rect(10px, 9999px, 110px, 0); }
  100% { clip: rect(50px, 9999px, 60px, 0); }
}

.delay-1 { transition-delay: 0.1s; }
.delay-2 { transition-delay: 0.2s; }
.delay-3 { transition-delay: 0.3s; }
.delay-4 { transition-delay: 0.4s; }
.delay-5 { transition-delay: 0.5s; }
</style>
