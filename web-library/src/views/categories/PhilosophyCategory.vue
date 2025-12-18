<template>
  <div class="category-page philosophy-page">
    <div class="zen-circle"></div>
    
    <button class="return-btn" @click="goBack">
      <span class="icon">×</span>
    </button>

    <div class="content-container">
      <header class="philosophy-header">
        <h1 class="fade-in">哲学</h1>
        <div class="divider-line"></div>
        <p class="subtitle fade-in delay-1">未经审视的人生不值得过。</p>
      </header>
      
      <div class="books-floating">
        <!-- 《禅与摩托车维修艺术》 -->
        <div class="book-item float-anim delay-0">
          <CategoryBookTemplate
            :cover-component="ZenCover"
            :title="getBook(10).titleCN"
            :author="getBook(10).author"
            :theme-color="getBook(10).colorTheme"
            variant="philosophy"
            :route-path="getBook(10).path"
          />
        </div>
        
        <div class="quote-block scroll-reveal">
          "一只手拍掌的声音是什么？"
        </div>

        <!-- 《人类简史：从动物到上帝》 -->
        <div class="book-item float-anim delay-2">
           <CategoryBookTemplate
            :cover-component="SapiensCover"
            :title="getBook(2).titleCN"
            :author="getBook(2).author"
            :theme-color="getBook(2).colorTheme"
            variant="philosophy"
            :route-path="getBook(2).path"
          />
        </div>

        <!-- 《娱乐至死》 -->
        <div class="book-item float-anim delay-0">
           <CategoryBookTemplate
            :cover-component="AmusingOurselvesToDeathCover"
            :title="getBook(11).titleCN"
            :author="getBook(11).author"
            :theme-color="getBook(11).colorTheme"
            variant="philosophy"
            :route-path="getBook(11).path"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import CategoryBookTemplate from '@/components/business/CategoryBookTemplate.vue';
import ZenCover from '@/components/business/covers/ZenCover.vue';
import SapiensCover from '@/components/business/covers/SapiensCover.vue';
import AmusingOurselvesToDeathCover from '@/components/business/covers/AmusingOurselvesToDeathCover.vue';
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
  background-color: #4E5A6A;
  color: #fff;
  position: relative;
  overflow: hidden;
  padding: 40px;
  --theme-color: #4E5A6A;
  display: flex;
  flex-direction: column;
}

.zen-circle {
  position: fixed;
  top: 50%; left: 50%;
  width: 60vh; height: 60vh;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
}
.zen-circle::after {
  content: '';
  position: absolute;
  top: 50%; left: 50%;
  width: 40vh; height: 40vh;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.return-btn {
  position: fixed;
  top: 40px; right: 40px;
  width: 50px; height: 50px;
  border-radius: 50%;
  border: 1px solid #fff;
  background: transparent;
  color: #fff;
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex; align-items: center; justify-content: center;
  z-index: 10;
}
.return-btn:hover {
  background: #fff;
  color: var(--theme-color);
  transform: rotate(90deg);
}

.content-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.philosophy-header {
  text-align: center;
  margin-top: 20px;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}
.philosophy-header h1 {
  font-weight: 300;
  font-size: 3rem;
  letter-spacing: 10px;
  margin: 0;
  color: #fff;
}
.divider-line {
  width: 1px; height: 40px;
  background: #fff;
  margin: 10px auto;
}
.subtitle {
  font-style: italic;
  opacity: 0.8;
  color: #fff;
}

.books-floating {
  flex: 1;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 60px;
  width: 100%;
}

.book-item {
  width: 100%;
  max-width: 260px;
}

.quote-block {
  font-size: 1.5rem;
  font-weight: 300;
  text-align: center;
  max-width: 200px;
  opacity: 0;
  transform: scale(0.9);
  transition: all 1s ease;
}
.quote-block.visible {
  opacity: 1;
  transform: scale(1);
}

/* Animations */
.fade-in {
  opacity: 0;
  animation: fadeIn 1.5s ease forwards;
}
.delay-1 { animation-delay: 0.5s; }
.delay-0 { animation-delay: 0s; }
.delay-2 { animation-delay: 0.5s; }

@keyframes fadeIn {
  to { opacity: 1; }
}

.float-anim {
  animation: float 6s ease-in-out infinite;
}
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}
</style>
