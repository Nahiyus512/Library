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
                title="人类简史"
                author="尤瓦尔·赫拉利"
                theme-color="#8C6D62"
                variant="history"
                route-path="/book/Sapiens"
              />
            </div>
          </div>

          <!-- 《三国演义》 -->
          <div class="event-item top scroll-reveal delay-2">
            <div class="date-marker">公元220年</div>
             <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="ThreeKingdomsCover"
                title="三国演义"
                author="罗贯中"
                theme-color="#8C6D62"
                variant="history"
                route-path="/book/ThreeKingdoms"
              />
            </div>
          </div>

          <!-- 《1984》 -->
          <div class="event-item bottom scroll-reveal delay-3">
            <div class="date-marker">1984年</div>
            <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="NineteenEightyFourCover"
                title="1984"
                author="乔治·奥威尔"
                theme-color="#8C6D62"
                variant="history"
                route-path="/book/NineteenEightyFour"
              />
            </div>
          </div>

          <!-- 《生命3.0》 -->
          <div class="event-item top scroll-reveal delay-4">
            <div class="date-marker">未来</div>
            <div class="book-wrapper">
              <CategoryBookTemplate
                :cover-component="Life30Cover"
                title="生命3.0"
                author="迈克斯·泰格马克"
                theme-color="#8C6D62"
                variant="history"
                route-path="/book/Life30"
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
}

.timeline-line {
  position: absolute;
  top: 50%; left: 10%;
  width: 80%; height: 2px;
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
  width: 200px; /* Base width */
  opacity: 0;
  transition: all 0.8s ease;
}

.book-wrapper {
  width: 100%;
  max-width: 220px; /* Constrain size */
}

.event-item.top {
  transform: translateY(-20px);
  justify-content: flex-end;
  margin-bottom: 40px; /* Push up from line */
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
  font-size: 1.2rem;
  color: rgba(255,255,255,0.9);
  margin: 10px 0;
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
</style>
