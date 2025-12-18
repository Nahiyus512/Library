<template>
  <div class="category-page life-page">
    <div class="organic-bg"></div>
    
    <button class="return-btn" @click="goBack">
      <div class="dot"></div> 返回
    </button>

    <div class="life-container">
      <header class="life-header">
        <h1 class="pop-in">生活</h1>
        <p class="subtitle pop-in delay-1">在冬天的深处，我发现自己内心有一个不可战胜的夏天。</p>
      </header>

      <div class="content-container">
        <div class="collage-grid">
          <!-- 《禅与摩托车维修艺术》 -->
          <div class="collage-item item-1 scroll-reveal">
             <CategoryBookTemplate
              :cover-component="ZenCover"
              title="禅与摩托车维修艺术"
              author="罗伯特·皮尔西格"
              theme-color="#8F9A8C"
              variant="life"
              route-path="/book/Zen"
            />
          </div>

          <!-- 《娱乐至死》 -->
          <div class="collage-item item-2 scroll-reveal delay-2">
            <CategoryBookTemplate
              :cover-component="AmusingOurselvesToDeathCover"
              title="娱乐至死"
              author="尼尔·波兹曼"
              theme-color="#8F9A8C"
              variant="life"
              route-path="/book/AmusingOurselvesToDeath"
            />
          </div>

          <div class="collage-sticker sticker-1 scroll-reveal">
            <span>🌿</span>
          </div>
          
          <div class="collage-sticker sticker-2 scroll-reveal delay-3">
            <span>☀️</span>
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
import AmusingOurselvesToDeathCover from '@/components/business/covers/AmusingOurselvesToDeathCover.vue';
import ZenCover from '@/components/business/covers/ZenCover.vue';

const router = useRouter();
const goBack = () => router.push('/main');

let observer: IntersectionObserver;

onMounted(() => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
      }
    });
  }, { threshold: 0.1 });

  document.querySelectorAll('.scroll-reveal').forEach(el => observer.observe(el));
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.category-page {
  height: 100vh;
  background-color: #8F9A8C;
  color: #fff;
  position: relative;
  overflow: hidden;
  --theme-color: #fff;
  display: flex;
  flex-direction: column;
}

.organic-bg {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.2) 0%, transparent 40%),
              radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 40%);
  pointer-events: none;
}

.return-btn {
  position: fixed;
  top: 30px; right: 30px;
  background: #fff;
  border-radius: 30px;
  padding: 10px 25px;
  font-family: 'Helvetica Neue', sans-serif;
  font-weight: 600;
  color: #8F9A8C;
  border: none;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  cursor: pointer;
  z-index: 100;
  display: flex; align-items: center; gap: 8px;
  transition: all 0.3s;
}
.return-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}
.dot {
  width: 8px; height: 8px;
  background: #8F9A8C;
  border-radius: 50%;
}

.life-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.life-header {
  text-align: center;
  padding-top: 60px;
  padding-bottom: 20px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}
.life-header h1 {
  font-family: 'Helvetica Neue', sans-serif;
  font-weight: 800;
  font-size: 5rem;
  margin: 0;
  color: #fff;
  letter-spacing: -2px;
}
.subtitle {
  font-family: 'Georgia', serif;
  font-style: italic;
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.8);
  max-width: 600px;
  margin: 10px auto;
}

.content-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.collage-grid {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 100px;
  position: relative;
  width: 100%;
  max-width: 1000px;
}

.collage-item {
  transform: scale(0.9) rotate(0deg);
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
  width: 100%;
  max-width: 300px;
}
.collage-item.visible {
  opacity: 1;
  transform: scale(1) rotate(var(--rot, 0deg));
}

.item-1 { --rot: -3deg; }
.item-2 { --rot: 4deg; }

.collage-sticker {
  position: absolute;
  font-size: 4rem;
  opacity: 0;
  transform: scale(0);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.collage-sticker.visible {
  opacity: 1;
  transform: scale(1);
}

.sticker-1 { top: -20px; left: 15%; transform: rotate(-15deg); }
.sticker-2 { bottom: -20px; right: 15%; transform: rotate(15deg); }

/* Animations */
.pop-in {
  animation: popIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
  opacity: 0;
  transform: scale(0.8);
}
.delay-1 { animation-delay: 0.2s; }
.delay-2 { transition-delay: 0.2s; }
.delay-3 { transition-delay: 0.4s; }

@keyframes popIn {
  to { opacity: 1; transform: scale(1); }
}
</style>
