<template>
  <div class="category-page knowledge-page">
    <div class="grid-bg"></div>
    
    <button class="return-btn" @click="goBack">
      <span class="bracket">[</span> 返回 <span class="bracket">]</span>
    </button>

    <div class="knowledge-layout">
      <div class="sidebar">
        <div class="data-stream">
          <span v-for="i in 20" :key="i" class="data-bit">{{ Math.random() > 0.5 ? '1' : '0' }}</span>
        </div>
      </div>

      <div class="main-content">
        <header class="knowledge-header">
          <h1 class="slide-in-left">知识</h1>
          <div class="tech-line"></div>
          <p class="subtitle slide-in-left delay-1">结构是自由的基础。</p>
        </header>

        <div class="blueprint-grid">
          <!-- 《写给大家看的设计书》 -->
           <div class="blueprint-item scroll-reveal">
             <CategoryBookTemplate
              :cover-component="NonDesignersDesignBookCover"
              :title="getBook(5).titleCN"
              :author="getBook(5).author"
              :theme-color="getBook(5).colorTheme"
              variant="knowledge"
              :route-path="getBook(5).path"
            />
          </div>

          <!-- 《平面设计中的网格系统》 -->
          <div class="blueprint-item scroll-reveal delay-2">
            <CategoryBookTemplate
              :cover-component="GridSystemsCover"
              :title="getBook(6).titleCN"
              :author="getBook(6).author"
              :theme-color="getBook(6).colorTheme"
              variant="knowledge"
              :route-path="getBook(6).path"
            />
          </div>
          
           <!-- 《色彩互动学》 -->
           <div class="blueprint-item scroll-reveal delay-3">
            <CategoryBookTemplate
              :cover-component="InteractionOfColorCover"
              :title="getBook(4).titleCN"
              :author="getBook(4).author"
              :theme-color="getBook(4).colorTheme"
              variant="knowledge"
              :route-path="getBook(4).path"
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
import GridSystemsCover from '@/components/business/covers/GridSystemsCover.vue';
import InteractionOfColorCover from '@/components/business/covers/InteractionOfColorCover.vue';
import NonDesignersDesignBookCover from '@/components/business/covers/NonDesignersDesignBookCover.vue';
import { books } from '@/data/books';
import { transitionState } from '@/store/transitionStore';

const router = useRouter();
const goBack = () => router.push('/main');

const getBook = (id: number) => books.find(b => b.id === id)!;

let observer: IntersectionObserver;

onMounted(() => {
  setTimeout(() => {
    transitionState.endAnimation();
  }, 500);

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
  background-color: #6C7A89;
  color: #fff;
  position: relative;
  overflow: hidden;
  --theme-color: #fff;
  display: flex;
  flex-direction: column;
}

.grid-bg {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  z-index: 0;
}

.return-btn {
  position: fixed;
  top: 40px; right: 40px;
  background: rgba(108, 122, 137, 0.8);
  border: 1px solid #fff;
  color: #fff;
  padding: 8px 16px;
  font-family: 'Courier New', monospace;
  cursor: pointer;
  z-index: 100;
  transition: all 0.3s;
}
.return-btn:hover {
  background: #fff;
  color: #6C7A89;
}
.bracket { opacity: 0.5; }

.knowledge-layout {
  height: 100%;
  display: flex;
}

.sidebar {
  width: 60px;
  border-right: 1px solid rgba(255,255,255,0.2);
  display: flex;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}
.data-stream {
  writing-mode: vertical-rl;
  font-family: 'Courier New', monospace;
  font-size: 10px;
  opacity: 0.5;
  margin-top: 100px;
  animation: stream 20s linear infinite;
}
@keyframes stream {
  from { transform: translateY(-50%); }
  to { transform: translateY(0); }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 40px;
  position: relative;
  z-index: 1;
}

.knowledge-header {
  margin-bottom: 20px;
  flex-shrink: 0;
}
.knowledge-header h1 {
  font-weight: 300;
  font-size: 3.5rem;
  margin: 0;
  letter-spacing: 5px;
}
.tech-line {
  width: 100px; height: 2px;
  background: #fff;
  margin: 10px 0;
  position: relative;
}
.tech-line::after {
  content: '';
  position: absolute;
  right: 0; top: -2px;
  width: 6px; height: 6px;
  background: #fff;
  border-radius: 50%;
}
.subtitle {
  font-family: 'Courier New', monospace;
  font-size: 1rem;
  opacity: 0.8;
}

.blueprint-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.blueprint-item {
  opacity: 0;
  transform: translateX(-20px);
  transition: all 0.5s ease;
  width: 100%;
  max-width: 300px;
  margin: 0 auto;
}
.blueprint-item.visible {
  opacity: 1;
  transform: translateX(0);
}

/* Animations */
.slide-in-left {
  animation: slideInLeft 0.8s ease forwards;
  opacity: 0;
  transform: translateX(-20px);
}
.delay-1 { animation-delay: 0.2s; }
.delay-2 { transition-delay: 0.2s; }
.delay-3 { transition-delay: 0.4s; }

@keyframes slideInLeft {
  to { opacity: 1; transform: translateX(0); }
}
</style>
