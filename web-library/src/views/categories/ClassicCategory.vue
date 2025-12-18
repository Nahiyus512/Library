<template>
  <div class="category-page classic-page" ref="pageRef">
    <div class="bg-texture"></div>
    
    <button class="return-btn" @click="goBack">
      <span class="icon">⇐</span>
      <span class="text">返回</span>
    </button>

    <header class="classic-header">
      <h1 class="fade-in-up">经典</h1>
      <p class="subtitle fade-in-up delay-1">时间的试金石</p>
    </header>
    
    <div class="content-container">
      <div class="books-grid">
        <!-- 《西游记》 -->
        <div class="book-item scroll-reveal" style="--delay: 0.1s">
          <CategoryBookTemplate
            :cover-component="JourneyToTheWestCover"
            :title="getBook(13).titleCN"
            :author="getBook(13).author"
            :theme-color="getBook(13).colorTheme"
            variant="classic"
            :route-path="getBook(13).path"
          />
        </div>

        <!-- 《三国演义》 -->
        <div class="book-item scroll-reveal" style="--delay: 0.3s">
          <CategoryBookTemplate
            :cover-component="ThreeKingdomsCover"
            :title="getBook(14).titleCN"
            :author="getBook(14).author"
            :theme-color="getBook(14).colorTheme"
            variant="classic"
            :route-path="getBook(14).path"
          />
        </div>
        
        <!-- 《1984》 -->
        <div class="book-item scroll-reveal" style="--delay: 0.2s">
          <CategoryBookTemplate
            :cover-component="NineteenEightyFourCover"
            :title="getBook(8).titleCN"
            :author="getBook(8).author"
            :theme-color="getBook(8).colorTheme"
            variant="classic"
            :route-path="getBook(8).path"
          />
        </div>

        <!-- 《美丽新世界》 -->
        <div class="book-item scroll-reveal" style="--delay: 0.4s">
          <CategoryBookTemplate
            :cover-component="BraveNewWorldCover"
            :title="getBook(9).titleCN"
            :author="getBook(9).author"
            :theme-color="getBook(9).colorTheme"
            variant="classic"
            :route-path="getBook(9).path"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import CategoryBookTemplate from '@/components/business/CategoryBookTemplate.vue';
import ThreeKingdomsCover from '@/components/business/covers/ThreeKingdomsCover.vue';
import JourneyToTheWestCover from '@/components/business/covers/JourneyToTheWestCover.vue';
import NineteenEightyFourCover from '@/components/business/covers/NineteenEightyFourCover.vue';
import BraveNewWorldCover from '@/components/business/covers/BraveNewWorldCover.vue';
import { books } from '@/data/books';

import { transitionState } from '@/store/transitionStore';

const router = useRouter();
const pageRef = ref<HTMLElement | null>(null);

const getBook = (id: number) => books.find(b => b.id === id)!;

const goBack = () => {
  router.push('/main');
};

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
  background-color: #B8A99A;
  color: #3e3a35;
  position: relative;
  overflow: hidden;
  padding: 40px;
  --theme-color: #554433;
  display: flex;
  flex-direction: column;
}

.bg-texture {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 0;
}

.return-btn {
  position: fixed;
  top: 40px;
  right: 40px;
  z-index: 100;
  background: none;
  border: 1px solid var(--theme-color);
  padding: 10px 20px;
  font-family: 'Georgia', serif;
  color: var(--theme-color);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 10px;
}
.return-btn:hover {
  background: var(--theme-color);
  color: #fff;
}

.classic-header {
  margin-top: 20px;
  text-align: center;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}
.classic-header h1 {
  font-family: 'Playfair Display', serif;
  font-size: 4rem;
  margin: 0;
  color: var(--theme-color);
  letter-spacing: -2px;
}
.subtitle {
  font-family: 'Georgia', serif;
  font-style: italic;
  font-size: 1.2rem;
  color: #888;
  margin-top: 10px;
}

.content-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
  width: 90%;
  max-width: 1400px;
  align-items: center;
}

.book-item {
  transition: all 0.8s cubic-bezier(0.22, 1, 0.36, 1);
  opacity: 0;
  transform: translateY(50px);
  width: 100%;
  max-width: 300px;
  margin: 0 auto;
}
.book-item.visible {
  opacity: 1;
  transform: translateY(0);
}

/* Animations */
.fade-in-up {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 1s ease forwards;
}
.delay-1 { animation-delay: 0.2s; }
.delay-2 { animation-delay: 0.4s; }

@keyframes fadeInUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>
