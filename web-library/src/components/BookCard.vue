<template>
  <div class="book-card-wrapper" @click="navigateToBook">
    <div class="book-shadow"></div>
    <div class="book-body unified-card" :class="{ 'expanded-mode': isExpanded }">
      <!-- Top Section (5%) -->
      <div class="card-top">
        <span class="book-index">{{ formattedId }}</span>
        <div class="archived-tag">
          <div class="black-square"></div>
          <span>ARCHIVED</span>
        </div>
      </div>

      <!-- Middle Section (55%) -->
      <div class="card-middle">
        <!-- Category Tag -->
        <div class="category-tag">{{ book.category }}</div>
        
        <!-- Dynamic Covers -->
        <div class="cover-wrapper" v-if="book.id === 1"><ThreeBodyCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 2"><SapiensCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 3"><HitchhikersGuideCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 4"><InteractionOfColorCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 5"><NonDesignersDesignBookCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 6"><GridSystemsCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 7"><DuneCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 8"><NineteenEightyFourCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 9"><BraveNewWorldCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 10"><ZenCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 11"><AmusingOurselvesToDeathCover :mode="isExpanded ? 'hero' : 'card'" /></div>
        <div class="cover-wrapper" v-else-if="book.id === 12"><Life30Cover :mode="isExpanded ? 'hero' : 'card'" /></div>
        
        <!-- Default -->
        <div v-else class="visual-default" :style="defaultStyle"></div>
      </div>

      <!-- Bottom Section (30%) -->
      <div class="card-bottom">
        <div class="info-group">
          <h3 class="pixel-title">{{ book.titleCN }}</h3>
          <p class="book-quote">{{ book.quote }}</p>
          <p class="book-author">— {{ book.author }}</p>
        </div>
        <div class="card-footer">
          <div class="barcode">
            <div class="bar" v-for="n in 12" :key="n" :style="{ width: Math.random() * 3 + 1 + 'px' }"></div>
          </div>
          <button class="read-entry-btn">READ ENTRY</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { transitionState } from '../stores/transitionStore';
import ZenCover from './covers/ZenCover.vue';
import ThreeBodyCover from './covers/ThreeBodyCover.vue';
import SapiensCover from './covers/SapiensCover.vue';
import HitchhikersGuideCover from './covers/HitchhikersGuideCover.vue';
import InteractionOfColorCover from './covers/InteractionOfColorCover.vue';
import NonDesignersDesignBookCover from './covers/NonDesignersDesignBookCover.vue';
import GridSystemsCover from './covers/GridSystemsCover.vue';
import DuneCover from './covers/DuneCover.vue';
import NineteenEightyFourCover from './covers/NineteenEightyFourCover.vue';
import BraveNewWorldCover from './covers/BraveNewWorldCover.vue';
import AmusingOurselvesToDeathCover from './covers/AmusingOurselvesToDeathCover.vue';
import Life30Cover from './covers/Life30Cover.vue';

const router = useRouter();

interface BookItem {
  id: number;
  title: string;
  titleCN: string;
  author: string;
  quote: string;
  category: string;
  colorTheme: string;
  isBorder?: boolean;
}

const props = defineProps<{
  book: BookItem;
  isExpanded?: boolean;
}>();

const navigateToBook = () => {
  const bookRoutes: Record<number, string> = {
    1: '/book/ThreeBody',
    2: '/book/Sapiens',
    3: '/book/HitchhikersGuide',
    4: '/book/InteractionOfColor',
    5: '/book/NonDesignersDesignBook',
    6: '/book/GridSystems',
    7: '/book/Dune',
    8: '/book/NineteenEightyFour',
    9: '/book/BraveNewWorld',
    10: '/book/Zen',
    11: '/book/AmusingOurselvesToDeath',
    12: '/book/Life30'
  };

  const path = bookRoutes[props.book.id];
  
  if (path) {
    // Trigger transition animation
    transitionState.startAnimation(props.book.colorTheme, props.book.titleCN);
    
    // Wait for animation to cover screen before navigating
    setTimeout(() => {
      router.push(path);
      
      // End animation after a delay (handled by next page or timeout)
      setTimeout(() => {
        transitionState.endAnimation();
      }, 500);
    }, 350);
  } else {
    router.push({ path: '/book', query: { q: props.book.titleCN } });
  }
};

const formattedId = computed(() => {
  return props.book.id < 10 ? `0${props.book.id}` : `${props.book.id}`;
});

const defaultStyle = computed(() => {
  const style: any = { background: props.book.colorTheme };
  if (props.book.isBorder) {
    style.border = '4px solid #000';
    style.color = '#000';
  }
  return style;
});
</script>

<style scoped>
/* Layout Structure */
.book-card-wrapper {
  margin-bottom: 40px;
  opacity: 0;
  animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  position: relative;
  aspect-ratio: 0.72;
  cursor: pointer;
}

.book-shadow {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-color: #000;
  z-index: 0;
  transition: transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform: translate(4px, 4px);
}

.book-body {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 1;
  background-color: #fff;
  border: 1px solid #000;
  transition: transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transform: translate(-4px, -4px);
}

.unified-card {
  display: flex;
  flex-direction: column;
}

/* Hover Effects */
.book-card-wrapper:hover .book-shadow {
  transform: translate(8px, 8px);
}
.book-card-wrapper:hover .book-body {
  transform: translate(-6px, -6px);
}

/* Sections */
.card-top {
  height: 5%;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  border-bottom: 1px solid #eee;
}

.book-index {
  font-family: monospace;
  font-size: 7px;
  font-weight: 700;
  color: #000;
}

.archived-tag {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 6px;
  font-weight: 700;
  letter-spacing: 1px;
}

.black-square {
  width: 4px; height: 4px;
  background: #000;
}

.card-middle {
  height: 55%;
  position: relative;
  overflow: hidden;
  border-bottom: 1px solid #000;
}

.category-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #000;
  color: #fff;
  padding: 3px 6px;
  font-size: 6px;
  font-weight: 700;
  text-transform: uppercase;
  z-index: 10;
  transform: rotate(5deg);
  transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  letter-spacing: 0.5px;
}

.book-card-wrapper:hover .category-tag {
  transform: rotate(15deg) scale(1.1);
}

.card-bottom {
  height: 40%;
  background: #fff;
  padding: 12px 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* Bottom Section Content */
.info-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pixel-title {
  margin: 0;
  font-family: 'Courier New', Courier, monospace; /* Fallback for pixel feel */
  font-size: 18px;
  font-weight: 900;
  color: #000;
  line-height: 1.1;
  letter-spacing: -1px;
}

.book-quote {
  margin: 0;
  font-size: 10px;
  color: #666;
  font-style: italic;
  line-height: 1.4;
}

.book-author {
  margin: 0;
  font-size: 8px;
  color: #000;
  font-weight: 700;
  text-transform: uppercase;
  width: 100%;
  text-align: right;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.barcode {
  display: flex;
  gap: 1px;
  height: 10px;
  align-items: flex-end;
}

.bar {
  width: 1.5px;
  background: #000;
  height: 100%;
}

.read-entry-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 3px 6px;
  font-size: 6px;
  font-weight: 700;
  cursor: pointer;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Animations CSS Copied */
@keyframes fadeUp { from { opacity: 0; transform: translateY(50px); } to { opacity: 1; transform: translateY(0); } }







.visual-default {
  width: 100%;
  height: 100%;
  position: relative;
}

.cover-wrapper {
  width: 100%;
  height: 100%;
}

/* Expanded Mode Scaling */
.expanded-mode .book-index { font-size: 11px; }
.expanded-mode .archived-tag { font-size: 9px; gap: 4px; }
.expanded-mode .black-square { width: 6px; height: 6px; }
.expanded-mode .pixel-title { font-size: 32px; }
.expanded-mode .book-quote { font-size: 16px; }
.expanded-mode .book-author { font-size: 12px; }
.expanded-mode .barcode { height: 18px; gap: 2px; }
.expanded-mode .bar { width: 2.5px; }
.expanded-mode .read-entry-btn { font-size: 10px; padding: 6px 10px; }
</style>
