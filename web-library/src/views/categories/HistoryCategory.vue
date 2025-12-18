<template>
  <div class="category-page history-page">
    
    <button class="return-btn" @click="goBack">
      <span class="arrow">←</span> 返回
    </button>

    <div class="history-container">
      <header class="history-header">
        <h1 class="fade-up">历史</h1>
        <p class="subtitle fade-up delay-1">过去从未消亡，它甚至没有过去。</p>
      </header>

      <div class="timeline-wrapper">
        <div class="timeline-line"></div>
        <div class="timeline-events">
          <!-- 《人类简史》 -->
          <div class="event-item bottom scroll-reveal delay-1">
            <div class="date-marker">史前</div>
            <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="SapiensCover"
                :title="getBook(2).titleCN"
                :author="getBook(2).author"
                :theme-color="getBook(2).colorTheme"
                variant="history"
                :route-path="getBook(2).path"
              />
            </div>
          </div>

          <!-- 《三国演义》 -->
          <div class="event-item top scroll-reveal delay-2">
            <div class="date-marker">公元220年</div>
             <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="ThreeKingdomsCover"
                :title="getBook(14).titleCN"
                :author="getBook(14).author"
                :theme-color="getBook(14).colorTheme"
                variant="history"
                :route-path="getBook(14).path"
              />
            </div>
          </div>

          <!-- 《1984》 -->
          <div class="event-item bottom scroll-reveal delay-3">
            <div class="date-marker">1984年</div>
            <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="NineteenEightyFourCover"
                :title="getBook(8).titleCN"
                :author="getBook(8).author"
                :theme-color="getBook(8).colorTheme"
                variant="history"
                :route-path="getBook(8).path"
              />
            </div>
          </div>

          <!-- 《生命3.0》 -->
          <div class="event-item top scroll-reveal delay-4">
            <div class="date-marker">未来</div>
            <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="Life30Cover"
                :title="getBook(12).titleCN"
                :author="getBook(12).author"
                :theme-color="getBook(12).colorTheme"
                variant="history"
                :route-path="getBook(12).path"
              />
            </div>
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
import SapiensCover from '@/components/business/covers/SapiensCover.vue';
import ThreeKingdomsCover from '@/components/business/covers/ThreeKingdomsCover.vue';
import NineteenEightyFourCover from '@/components/business/covers/NineteenEightyFourCover.vue';
import Life30Cover from '@/components/business/covers/Life30Cover.vue';
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
  background-color: #8C6D62;
  color: #fff;
  position: relative;
  overflow: hidden;
  --theme-color: #8C6D62;
  display: flex;
  flex-direction: column;
}

.history-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 800px; /* Ensure enough height */
}

.return-btn {
  position: fixed;
  top: 30px; right: 30px;
  background: #fff;
  color: var(--theme-color);
  border: none;
  padding: 10px 20px;
  font-family: 'Georgia', serif;
  cursor: pointer;
  z-index: 100;
  display: flex; align-items: center; gap: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
  transition: transform 0.2s;
}
.return-btn:hover {
  transform: translateY(-2px);
}

.history-header {
  text-align: center;
  padding-top: 40px;
  padding-bottom: 20px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}
.history-header h1 {
  font-family: 'Cinzel', serif;
  font-size: 3.5rem;
  margin: 0;
  color: #fff;
}
.subtitle {
  font-family: 'Georgia', serif;
  font-style: italic;
  font-size: 1rem;
  color: rgba(255,255,255,0.8);
}

.timeline-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  overflow-x: auto; /* Enable horizontal scroll */
  padding: 0 0px; /* Add padding for scroll start/end */
}

.timeline-line {
  position: absolute;
  top: 50%; left: 0%;
  width: 100%; height: 2px; /* Full width */
  background: rgba(255, 255, 255, 0.3);
  z-index: 0;
}

.timeline-events {
  display: flex;
  justify-content: space-between;
  width: 80%;
  position: relative;
  z-index: 1;
}

.event-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 300px; /* Doubled from ~200px */
  opacity: 0;
  transition: all 0.8s ease;
}

.book-wrapper {
  width: 100%;
  max-width: 300px; /* Doubled from 220px */
}

.event-item.top {
  transform: translateY(-20px);
  justify-content: flex-start; /* Changed from flex-end to align with column-reverse (start=bottom) */
  margin-bottom: 40px; /* Push up from line */
  flex-direction: column-reverse; /* Put date below book */
}
.event-item.bottom {
  transform: translateY(20px);
  justify-content: flex-start;
  margin-top: 40px; /* Push down from line */
}

.event-item.visible {
  opacity: 1;
  transform: translateY(0);
}

.date-marker {
  font-family: 'Cinzel', serif;
  font-size: 1.4rem;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
  margin: 15px 0;
  letter-spacing: 1px;
}

/* Connectors */
.event-item.top::after {
  content: '';
  position: absolute;
  bottom: -40px; left: 50%;
  width: 1px; height: 40px;
  background: rgba(255,255,255,0.5);
}
.event-item.bottom::before {
  content: '';
  position: absolute;
  top: -40px; left: 50%;
  width: 1px; height: 40px;
  background: rgba(255,255,255,0.5);
}

/* Animations */
.fade-up {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 1s ease forwards;
}
.delay-1 { animation-delay: 0.2s; }
.delay-2 { transition-delay: 0.2s; }
.delay-3 { transition-delay: 0.4s; }

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

/* Enhanced Hover Animation for Covers - Suspended Right-Bottom Movement */
:deep(.shell-history .cover-history-container) {
  transition: transform 0.4s ease, box-shadow 0.4s ease;
}

:deep(.shell-history:hover .cover-history-container) {
  transform: translate(8px, 8px) rotate(2deg);
  /* Adjust shadow to enhance suspended 3D effect */
  box-shadow: -2px -2px 10px rgba(0,0,0,0.1);
}
</style>
