<template>
  <div class="bookshelf-widget">
    <!-- Top Display -->
    <div class="shelf-display">
      <transition name="fade" mode="out-in">
        <span :key="hoveredBook ? hoveredBook.id : 'default'" class="display-text">
          {{ hoveredBook ? hoveredBook.titleCN : '我的书架' }}
        </span>
      </transition>
    </div>

    <!-- Books Row -->
    <div class="books-container">
      <div 
        v-for="(book, index) in displayBooks" 
        :key="book.id"
        class="book-spine"
        :style="getBookStyle(book, index)"
        @mouseenter="hoveredBook = book"
        @mouseleave="hoveredBook = null"
        @click.stop="goToBook(book)"
      >
        <div class="spine-content">
          <span class="spine-title">{{ book.titleCN }}</span>
        </div>
      </div>
    </div>

    <!-- Wood Shelf -->
    <div class="wood-shelf"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { transitionState } from '../../store/transitionStore';
import { books } from '../../data/books';
import type { BookItem } from '../../data/books';

const router = useRouter();
const hoveredBook = ref<BookItem | null>(null);
const displayBooks = ref<BookItem[]>([]);
const randomHeights = ref<number[]>([]);

const goToBook = (book: BookItem) => {
  const path = book.path;
  if (path) {
    transitionState.startAnimation(
      book.colorTheme, 
      book.titleCN,
      book.textColor || '#ffffff'
    );
    
    setTimeout(() => {
      router.push(path);
      setTimeout(() => {
        transitionState.endAnimation();
      }, 500);
    }, 350);
  }
};

onMounted(() => {
  // Randomly select 6 distinct books
  const shuffled = [...books].sort(() => 0.5 - Math.random());
  displayBooks.value = shuffled.slice(0, 6);
  // Generate random heights between 75% and 95%
  randomHeights.value = displayBooks.value.map(() => Math.random() * 20 + 75);
});

const getBookStyle = (book: BookItem, index: number) => {
  return {
    backgroundColor: book.colorTheme,
    color: book.textColor || '#fff',
    borderColor: book.isBorder ? '#000' : 'transparent',
    height: `${randomHeights.value[index]}%`
  };
};
</script>

<style scoped>
.bookshelf-widget {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 20px 10px 0;
  box-sizing: border-box;
  background: #fdfbf7;
  position: relative;
  overflow: hidden;
}

.shelf-display {
  position: absolute;
  top: 15px;
  left: 0;
  width: 100%;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: #333;
  height: 20px;
  z-index: 10;
}

.display-text {
  display: block;
}

.books-container {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 4px;
  flex: 1;
  padding-bottom: 12px; /* Space for shelf */
  perspective: 500px;
}

.book-spine {
  width: 14%;
  /* Height is set dynamically */
  border: 1px solid rgba(0,0,0,0.1);
  border-radius: 2px;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-spine:hover {
  transform: translateY(-15px) scale(1.05);
  z-index: 5;
  box-shadow: 4px 4px 10px rgba(0,0,0,0.2);
}

.spine-content {
  writing-mode: vertical-rl;
  text-orientation: upright; /* Or mixed for standard vertical CJK */
  font-size: 14px; /* Increased font size */
  font-weight: 800; /* Increased font weight */
  letter-spacing: 2px;
  overflow: hidden;
  white-space: nowrap;
  max-height: 90%;
  opacity: 0.9;
}

.wood-shelf {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 12px;
  background: #d4a373;
  border-top: 2px solid #a98467;
  box-shadow: 0 -2px 5px rgba(0,0,0,0.1);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
