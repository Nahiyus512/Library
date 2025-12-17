<template>
  <div class="split-layout">
    <!-- Left Panel: Categories -->
    <div 
      class="panel left-panel" 
      :class="{ 'expanded': activeSide === 'left', 'shrunk': activeSide === 'right' || isMasonryExpanded }"
    >
      <CategoryNav 
        :is-expanded="activeSide === 'left'"
        @toggle-expand="expandLeftPanel"
        @collapse="collapseLeftPanel"
        @select="navigateToCategory"
      />
    </div>

    <!-- Right Panel: AI Prompt Books Masonry -->
    <div 
      class="panel right-panel" 
      :class="{ 'expanded': activeSide === 'right' || isMasonryExpanded, 'shrunk': activeSide === 'left' }"
      @click="expandMasonry"
    >
      <!-- Expanded Layout Container -->
      <div class="layout-container" :class="{ 'is-expanded-mode': isMasonryExpanded }">
        
        <!-- Left Sidebar (Always present, width animated) -->
        <div class="sidebar left-sidebar" :class="{ 'collapsed': !isMasonryExpanded }">
          <div class="watermark-vertical">LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY</div>
        </div>

        <!-- Main Content Area -->
        <div class="content-area" :class="{ 'beige-grid': isMasonryExpanded }">
          <button class="return-btn" v-if="isMasonryExpanded" @click.stop="collapseMasonry">⇒</button>
          <div class="bg-watermark" v-if="isMasonryExpanded">LIBRARY</div>
          
          <div class="scroll-container" ref="scrollContainer" :class="{ 'expanded-padding': isMasonryExpanded }">
            <div class="masonry-wrapper">
              <!-- Column 1 -->
              <div class="masonry-column">
                <template v-for="(book, index) in col1" :key="`${book.id}-${index}-1`">
                  <InteractiveWidget 
                    v-if="book.widgetType" 
                    :type="book.widgetType"
                  />
                  <BookCard 
                    v-else
                    :book="book"
                    :is-expanded="isMasonryExpanded"
                  />
                </template>
              </div>

              <!-- Column 2 (Middle) -->
              <div class="masonry-column middle-column">
                <!-- Fancy Expand Button with Down Arrow -->
                <div class="expand-action-wrapper" v-if="!isMasonryExpanded">
                  <div class="arrow-hint">↓</div>
                  <button class="fancy-expand-btn" @click.stop="expandMasonry">
                    <span class="btn-icon">＋</span>
                    <span class="btn-text">EXPLORE</span>
                  </button>
                </div>

                <template v-for="(book, index) in col2" :key="`${book.id}-${index}-2`">
                  <InteractiveWidget 
                    v-if="book.widgetType" 
                    :type="book.widgetType"
                  />
                  <BookCard 
                    v-else
                    :book="book"
                    :is-expanded="isMasonryExpanded"
                  />
                </template>
              </div>

              <!-- Column 3 -->
              <div class="masonry-column">
                <template v-for="(book, index) in col3" :key="`${book.id}-${index}-3`">
                  <InteractiveWidget 
                    v-if="book.widgetType" 
                    :type="book.widgetType"
                  />
                  <BookCard 
                    v-else
                    :book="book"
                    :is-expanded="isMasonryExpanded"
                  />
                </template>
              </div>
            </div>
            <!-- Loading Sentinel -->
            <div ref="sentinel" class="sentinel"></div>
          </div>
        </div>

        <!-- Right Sidebar (Always present, width animated) -->
        <div class="sidebar right-sidebar" :class="{ 'collapsed': !isMasonryExpanded }">
          <div class="watermark-vertical">LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY</div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import BookCard from '@/components/business/BookCard.vue';
import InteractiveWidget from '@/components/common/InteractiveWidget.vue';
import CategoryNav from '@/components/business/CategoryNav.vue';
import { books as rawBooks, type BookItem } from '@/data/books';

const router = useRouter();
const activeSide = ref<string | null>(null);
const isMasonryExpanded = ref(false);
const scrollContainer = ref<HTMLElement | null>(null);
const sentinel = ref<HTMLElement | null>(null);

const col1 = ref<BookItem[]>([]);
const col2 = ref<BookItem[]>([]);
const col3 = ref<BookItem[]>([]);
const loopCount = ref(0);

const getBookStyle = (book: BookItem) => {
  const style: any = { background: book.colorTheme };
  if (book.isBorder) {
    style.border = '4px solid #000';
    style.color = '#000';
  }
  return style;
};

const isLightColor = (color: string) => {
  // Simple heuristic for hex codes or known light colors
  if (color.startsWith('#')) {
    const hex = color.replace('#', '');
    const r = parseInt(hex.substr(0, 2), 16);
    const g = parseInt(hex.substr(2, 2), 16);
    const b = parseInt(hex.substr(4, 2), 16);
    const brightness = ((r * 299) + (g * 587) + (b * 114)) / 1000;
    return brightness > 155;
  }
  return color === '#ffffff' || color.includes('#fef3c7') || color.includes('#cffafe');
};

const expandLeftPanel = () => {
  activeSide.value = 'left';
};

const collapseLeftPanel = () => {
  activeSide.value = null;
};

const handlePanelClick = (side: string) => {
  if (side === 'left' && activeSide.value !== 'left') {
     expandLeftPanel();
  }
};

const expandMasonry = () => {
  if (!isMasonryExpanded.value) {
    isMasonryExpanded.value = true;
    activeSide.value = 'right';
  }
};

const collapseMasonry = () => {
  isMasonryExpanded.value = false;
  activeSide.value = null;
};

const navigateToCategory = (category: string) => {
  activeSide.value = 'left';
  setTimeout(() => {
    router.push(`/category/${category}`);
  }, 600);
};

const distributeBooks = (books: BookItem[]) => {
  books.forEach((book, index) => {
    // Distribute round-robin based on total count to maintain order flow
    const totalIndex = col1.value.length + col2.value.length + col3.value.length;
    const remainder = totalIndex % 3;
    if (remainder === 0) col1.value.push(book);
    else if (remainder === 1) col2.value.push(book);
    else col3.value.push(book);
  });
};

const prepareBatch = () => {
  const batch = [...rawBooks];
  const widgetType = loopCount.value % 2 === 0 ? 'bookshelf' : 'radio';
  
  // Create widget item
  const widgetItem: BookItem = {
    id: -1 - loopCount.value, // unique negative ID
    title: 'Interactive Widget',
    titleCN: 'Interactive Widget',
    author: '',
    quote: '',
    categories: [],
    colorTheme: '#fff',
    path: '',
    widgetType: widgetType as 'bookshelf' | 'radio'
  };

  // Insert at index 4 (5th position)
  batch.splice(4, 0, widgetItem);
  
  loopCount.value++;
  return batch;
};

const loadMore = () => {
  const newBatch = prepareBatch();
  distributeBooks(newBatch);
};

let observer: IntersectionObserver;

onMounted(() => {
  loadMore(); // Initial load

  observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) {
      loadMore();
    }
  }, {
    root: scrollContainer.value,
    rootMargin: '200px', // Load before reaching bottom
    threshold: 0.1
  });

  if (sentinel.value) {
    observer.observe(sentinel.value);
  }
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.split-layout {
  height: 100%;
  width: 100%;
  display: flex;
  overflow: hidden;
  font-family: 'Inter', sans-serif;
  background: #fff;
}

.panel {
  height: 100%;
  transition: all 0.6s cubic-bezier(0.65, 0, 0.35, 1);
  overflow: hidden;
  position: relative;
}

/* Ensure padding is zero or controlled */
.panel * {
  box-sizing: border-box;
}

.left-panel {
  width: 50%;
  background-color: #fff;
  border-right: 1px solid rgba(0,0,0,0.05);
  z-index: 2;
}

.right-panel {
  width: 50%;
  background-color: #fcfcfc;
  z-index: 1;
  transition: width 0.6s cubic-bezier(0.65, 0, 0.35, 1);
}

.panel.expanded { width: 100% !important; }
.panel.shrunk { width: 0% !important; opacity: 0; pointer-events: none; }

.layout-container {
  width: 100%;
  height: 100%;
  display: flex;
}

/* Expanded Layout Components */
.sidebar {
  width: 60px; /* White margin width */
  background: #fff;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end; /* Bottom alignment */
  padding-top: 20px;
  z-index: 10;
  border-left: 1px solid #000;
  border-right: 1px solid #000;
  overflow: hidden;
  transition: width 0.3s ease, opacity 0.3s ease, border-width 0.3s ease;
}

/* Collapsed state for sidebars: width 0, no border */
.sidebar.collapsed {
  width: 0;
  border-left-width: 0;
  border-right-width: 0;
  padding: 0;
  opacity: 0;
}

.left-sidebar { border-left: none; }
.right-sidebar { border-right: none; }

.content-area {
  flex: 1;
  height: 100%;
  position: relative;
  overflow: hidden;
  background-color: #fcfcfc; /* Default background */
  transition: background 0.3s ease;
}

.content-area.beige-grid {
  background-color: #faf8f5; /* Pale beige close to white */
  background-image: 
    linear-gradient(#e5e5e5 1px, transparent 1px),
    linear-gradient(90deg, #e5e5e5 1px, transparent 1px);
  background-size: 125px 125px; /* Larger grid (2.5x of 50px) */
}

.scroll-container {
  height: 100%;
  width: 100%;
  overflow-y: auto;
  padding: 15px 40px 0 40px; /* Reduced top padding, remove bottom padding */
  box-sizing: border-box;
  scrollbar-width: none;
  position: relative;
  z-index: 5; /* Above watermark */
  transition: padding 0.3s ease;
}
.scroll-container.expanded-padding {
  padding: 15px 15% 0 15%; /* Add horizontal padding to shrink cards */
}
.scroll-container::-webkit-scrollbar { display: none; }

/* Watermarks */
.bg-watermark {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-family: 'Inter', sans-serif;
  font-weight: 900;
  font-size: 200px;
  color: rgba(0,0,0,0.03);
  pointer-events: none;
  z-index: 0;
  letter-spacing: 20px;
}

.watermark-vertical {
  writing-mode: vertical-rl;
  transform: rotate(180deg); /* Bottom to top direction */
  font-family: 'Inter', sans-serif;
  font-weight: 900;
  font-size: 24px;
  color: rgba(0,0,0,0.1);
  letter-spacing: 4px;
  white-space: nowrap;
  padding: 20px 0;
  margin: 0;
  /* Text close to black border: alignment handled by flex item alignment or margins */
}

.left-sidebar .watermark-vertical {
  margin-left: 20px; /* Push towards border? No, flex align center. */
  align-self: flex-end; /* Close to right border (black border) */
  margin-right: 5px;
}

.right-sidebar .watermark-vertical {
  align-self: flex-start; /* Close to left border (black border) */
  margin-left: 5px;
}

/* Return Button */
.return-btn {
  width: 32px; height: 32px;
  background: #000; /* Black background */
  color: #fff; /* White text/icon */
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.1s;
  position: absolute; /* Fixed position relative to content-area */
  top: 20px;
  left: 20px;
  z-index: 20; /* Ensure it's above everything */
}

.return-btn:active {
  transform: scale(0.95);
}

/* Custom Fancy Expand Button */
.expand-action-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  gap: 8px;
}

.arrow-hint {
  font-size: 20px;
  color: #000;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
  40% {transform: translateY(-5px);}
  60% {transform: translateY(-3px);}
}

.fancy-expand-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 14px 32px;
  border-radius: 100px;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  font-size: 12px;
  letter-spacing: 2px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
}

.fancy-expand-btn::before {
  content: '';
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(255,255,255,0.1);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.fancy-expand-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0,0,0,0.2);
}

.fancy-expand-btn:hover::before {
  opacity: 1;
}

.fancy-expand-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.15);
}

.btn-icon {
  font-size: 14px;
  line-height: 1;
}

.panel-content {
  height: 100%;
  width: 100%;
  padding: 40px;
  box-sizing: border-box;
  opacity: 1;
  transition: opacity 0.3s ease;
}

/* Left Panel */
.category-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 80px;
}

.section-title {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: #999;
  margin-bottom: 40px;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-item {
  display: flex;
  align-items: baseline;
  gap: 15px;
  cursor: pointer;
  transition: transform 0.3s ease;
  color: #333;
}

.category-item:hover {
  transform: translateX(10px);
  color: #000;
}

.category-index {
  font-size: 12px;
  color: #ccc;
  font-family: monospace;
}

.category-name {
  font-size: 28px;
  font-weight: 300;
  letter-spacing: 1px;
}

/* Right Panel Old Classes (Kept for default state compatibility) */
.art-content {
  /* This class is now effectively replaced by .scroll-container logic in expanded mode,
     but kept for structure if needed or we can migrate styles. 
     Actually, let's ensure default view uses .scroll-container style logic.
  */
}

.masonry-wrapper {
  display: flex;
  gap: 40px;
  width: 100%;
}

.masonry-column {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.middle-column {
  margin-top: 80px; /* Stagger effect */
}


/* Book card styles moved to BookCard.vue */

.sentinel {
  height: 20px;
  width: 100%;
}


@media (max-width: 1024px) {
  .masonry-wrapper { flex-direction: column; }
  .middle-column { margin-top: 0; }
  .left-panel, .right-panel { width: 100%; height: 50%; }
  .split-layout { flex-direction: column; }
}

@media (max-width: 640px) {
  .masonry-wrapper { flex-direction: column; }
  .middle-column { margin-top: 0; }
}
</style>
