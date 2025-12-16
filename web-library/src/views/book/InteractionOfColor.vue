<template>
  <div class="interaction-container">
    
    <!-- Top Left Return Button -->
    <button class="return-btn fixed-top-left" @click="goBackHome">
      <span>← END EXPERIMENT</span>
    </button>

    <!-- Section 0: Hero / Cover -->
    <section class="section section-hero" id="section-0">
      <!-- 8-Square Animated Grid Background -->
      <div class="hero-grid" :style="gridStyle">
        <div 
          v-for="(color, index) in heroColors" 
          :key="index"
          class="hero-block"
          :style="{ 
            backgroundColor: color,
            filter: getHeroBlockFilter(index)
          }"
          @mouseenter="heroHoverIndex = index"
          @mouseleave="heroHoverIndex = null"
        ></div>
      </div>

      <!-- Title Overlay -->
      <div class="hero-title-overlay">
        <h1 class="main-title">色彩互动学</h1>
        <p class="author-quote">“色彩是艺术中最相对的媒介。” — 约瑟夫·阿尔伯斯</p>
        <div class="scroll-hint">向下滑动开始实验</div>
      </div>
    </section>

    <!-- Section 1: Relativity -->
    <section class="section section-relativity" id="section-1">
      <div class="split-bg">
        <div class="bg-half left"></div>
        <div class="bg-half right"></div>
      </div>
      
      <div class="content-overlay">
        <div class="text-panel">
          <h2 class="section-title">色彩相对论</h2>
          <p class="section-desc">“一种颜色看起来像两种颜色。”</p>
          <p class="instruction">拖动左侧方块，观察背景如何改变它。</p>
        </div>

        <div class="relativity-demo-layout">
          <!-- Left Target (Draggable) -->
          <div class="demo-area left-area">
             <div 
              class="draggable-square"
              :style="{ backgroundColor: relColor, transform: `translate(${dragX}px, ${dragY}px)` }"
              @mousedown="startDragRelativity"
            ></div>
          </div>
          <!-- Right Reference -->
          <div class="demo-area right-area">
             <div 
              class="reference-square"
              :style="{ backgroundColor: relColor }"
            ></div>
          </div>
        </div>
      </div>
    </section>

    <!-- Section 2: Transparency -->
    <section class="section section-transparency" id="section-2">
      <div class="content-overlay transparency-overlay">
        <div class="text-panel dark-text small-bottom-text">
          <h2 class="section-title small-title"></h2>
          <p class="section-desc small-desc">上下拖动中间的混合条，寻找完美的透明点。</p>
        </div>
        
        <!-- Button Removed -->

        <div 
          class="transparency-fullscreen" 
          ref="transContainerRef"
          @mousedown="startTransDrag" 
          @touchstart="startTransDrag"
        >
           <!-- Large Background Shapes -->
           <div class="trans-shape shape-left" :style="{ backgroundColor: transColor1 }"></div>
           <div class="trans-shape shape-right" :style="{ backgroundColor: transColor2 }"></div>
           
           <!-- Draggable Mix Strip -->
           <div 
            class="trans-mix-strip vertical-strip"
            :style="{ 
              top: `${transY}px`,
              backgroundColor: mixColorDisplay
            }"
          >
            <!-- <div class="drag-handle-icon">↕</div> -->
          </div>
        </div>
      </div>
    </section>

    <!-- Section 3: Bezold Effect -->
    <section 
      class="section section-bezold" 
      id="section-3"
      :style="{ backgroundColor: `hsl(0, 0%, ${bezoldLightness}%)` }"
    >
      <div class="content-overlay" style="justify-content: flex-start; padding-top: 100px;">
        <div class="text-panel" :class="{ 'dark-text': bezoldLightness > 50 }">
          <h2 class="section-title">贝措尔德效应</h2>
          <p class="section-desc">背景亮度改变感知。</p>
        </div>

        <div class="bezold-grid-square">
           <div 
             v-for="n in 64" 
             :key="n" 
             class="brick-square"
             :style="{ animationDelay: `${n * 0.02}s` }"
           ></div>
        </div>
      </div>
      
      <!-- Moved outside content-overlay for better interaction/z-index -->
      <div class="controls-bottom">
        <input type="range" v-model.number="bezoldLightness" min="0" max="100" step="1" class="full-width-slider">
      </div>
    </section>

    <!-- Section 4: Vibration -->
    <section 
      class="section section-vibration" 
      id="section-4"
      :style="{ backgroundColor: vibBgColor }"
    >
      <div class="content-overlay">
        <div class="text-panel">
          <h2 class="section-title">震动的边界</h2>
          <p class="section-desc">调整亮度，直到边界“震动”。</p>
        </div>

        <div class="vib-square-large" :style="{ backgroundColor: `hsl(180, 50%, ${vibLightness}%)` }"></div>
        
        <div class="controls-bottom">
          <input type="range" v-model.number="vibLightness" min="20" max="80" step="1" class="full-width-slider">
        </div>
      </div>
    </section>

    <!-- Section 5: Subtraction -->
    <section class="section section-subtraction" id="section-5">
      <div class="split-bg">
        <div class="bg-half" :style="{ backgroundColor: subBg1 }"></div>
        <div class="bg-half" :style="{ backgroundColor: subBg2 }"></div>
      </div>

      <div class="content-overlay">
        <div class="text-panel subtraction-text-panel">
          <h2 class="section-title">
            <span style="color: #fff">色彩</span><span style="color: #000">减法</span>
          </h2>
          <p class="section-desc">
            <span style="color: #fff">让两个不同颜色的</span><span style="color: #000">方块看起来一样。</span>
          </p>
        </div>

        <div class="subtraction-full-layout" ref="subContainerRef">
           <!-- Item 1 -->
           <div 
             class="sub-item-large"
             :style="{ 
               backgroundColor: subTarget1, 
               transform: `translate(${subDrag1.x}px, ${subDrag1.y}px)`,
               zIndex: subDrag1.isDragging ? 100 : 10
             }"
             @mousedown="(e) => startSubDrag(e, 1)"
           >
             <span class="sub-number" style="color: rgba(0,0,0,0.6)">1</span>
           </div>
           <!-- Item 2 -->
           <div 
             class="sub-item-large"
             :style="{ 
               backgroundColor: subTarget2, 
               transform: `translate(${subDrag2.x}px, ${subDrag2.y}px)`,
               zIndex: subDrag2.isDragging ? 100 : 10
             }"
             @mousedown="(e) => startSubDrag(e, 2)"
           >
             <span class="sub-number" style="color: rgba(0,0,0,0.6)">2</span>
           </div>
        </div>
      </div>
    </section>

    <!-- Section 6: Afterimage -->
    <section class="section section-afterimage" id="section-6">
      <div 
        class="afterimage-fullscreen" 
        @mousedown="toggleAfterimage"
        @mouseup="toggleAfterimage"
        :class="{ 'show-white': showAfterimageWhite }"
      >
        <div class="ai-content" v-if="!showAfterimageWhite">
           <h2 class="section-title">后像</h2>
           <p class="section-desc">盯着黑点 10 秒，然后点击屏幕。</p>
           <div class="ai-circle-large">
             <div class="ai-dot-large"></div>
           </div>
        </div>
        <div class="ai-result" v-else>
          <h2 class="section-title" style="color:#333">你看到了什么？</h2>
        </div>
      </div>
    </section>

    <!-- Section 7: Recommended Reading -->
    <section class="section section-recommendations" id="section-7">
      <div class="content-overlay">
        <h2 class="section-title">推荐阅读</h2>
        <div class="book-grid-center">
          <div class="book-card-mini" v-for="(book, idx) in recommendedBooks" :key="idx">
            <h3>{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
          </div>
        </div>
        <button class="footer-btn" @click="goBackHome">FINISH STUDY</button>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goBackHome = () => {
  router.push('/main');
};

// --- Hero / Cover Logic ---
const heroColors = [
  '#ef4444', '#f97316', '#f59e0b', '#84cc16',
  '#10b981', '#06b6d4', '#3b82f6', '#8b5cf6'
];
const heroHoverIndex = ref<number | null>(null);

// Animated grid tracks
const gridTime = ref(0);
let animFrame: number;

const gridStyle = computed(() => {
  // REDUCED AMPLITUDE (User Request 1)
  const t = gridTime.value;
  // Previously 0.3, now 0.05 for subtle movement
  const c1 = 1 + Math.sin(t * 0.002) * 0.05;
  const c2 = 1 + Math.sin(t * 0.003 + 2) * 0.05;
  const c3 = 1 + Math.sin(t * 0.001 + 4) * 0.05;
  const c4 = 1 + Math.sin(t * 0.004 + 1) * 0.05;
  
  const r1 = 1 + Math.cos(t * 0.0025) * 0.05;
  const r2 = 1 + Math.cos(t * 0.0035 + 1) * 0.05;

  return {
    display: 'grid',
    gridTemplateColumns: `${c1}fr ${c2}fr ${c3}fr ${c4}fr`,
    gridTemplateRows: `${r1}fr ${r2}fr`,
    height: '100%',
    width: '100%',
    transition: 'all 0.1s linear'
  };
});

const getHeroBlockFilter = (index: number) => {
  if (heroHoverIndex.value === null) return 'brightness(1)';
  if (heroHoverIndex.value === index) return 'brightness(1.4) saturate(1.2)';
  return 'brightness(0.4) saturate(0.5)'; 
};

// Animation Loop (Shared)
const animate = () => {
  gridTime.value += 16;
  
  // Subtraction Inertia Loop
  updateSubInertia();
  
  // Transparency Inertia Loop
  updateTransInertia();

  animFrame = requestAnimationFrame(animate);
};

// --- Relativity ---
const relColor = ref('#9e9e9e'); 
const dragX = ref(0);
const dragY = ref(0);
const isRelDragging = ref(false);

const startDragRelativity = (e: MouseEvent) => {
  isRelDragging.value = true;
  const startX = e.clientX - dragX.value;
  const startY = e.clientY - dragY.value;

  const onMouseMove = (moveEvent: MouseEvent) => {
    dragX.value = moveEvent.clientX - startX;
    dragY.value = moveEvent.clientY - startY;
  };

  const onMouseUp = () => {
    isRelDragging.value = false;
    document.removeEventListener('mousemove', onMouseMove);
    document.removeEventListener('mouseup', onMouseUp);
    dragX.value = 0; 
    dragY.value = 0;
  };
  document.addEventListener('mousemove', onMouseMove);
  document.addEventListener('mouseup', onMouseUp);
};

// --- Transparency (FIXED) ---
const transColor1 = '#ef4444';
const transColor2 = '#3b82f6';
const transY = ref(window.innerHeight / 2); 
const isTransDragging = ref(false);
const transVy = ref(0); // Velocity Y
const transContainerRef = ref<HTMLElement | null>(null);

const mixColorDisplay = computed(() => {
  const h = window.innerHeight;
  const percentage = Math.min(Math.max(transY.value / h, 0), 1);
  const r = Math.round(239 + (59 - 239) * percentage);
  const g = Math.round(68 + (130 - 68) * percentage);
  const b = Math.round(68 + (246 - 68) * percentage);
  return `rgb(${r}, ${g}, ${b})`;
});

const startTransDrag = (e: MouseEvent | TouchEvent) => {
  // Prevent jump: Just start tracking movement
  isTransDragging.value = true;
  transVy.value = 0; // Stop inertia
  
  let lastClientY = 'touches' in e ? e.touches[0].clientY : (e as MouseEvent).clientY;
  let lastTime = performance.now();
  
  const onMove = (moveEvent: MouseEvent | TouchEvent) => {
    const now = performance.now();
    const dt = now - lastTime;
    const currentClientY = 'touches' in moveEvent ? moveEvent.touches[0].clientY : (moveEvent as MouseEvent).clientY;
    
    const dy = currentClientY - lastClientY;
    transY.value += dy;
    
    // Boundary check
    const h = window.innerHeight;
    if (transY.value < 0) transY.value = 0;
    if (transY.value > h) transY.value = h;

    // Calculate Velocity
    if (dt > 0) {
      transVy.value = dy; 
    }
    
    lastClientY = currentClientY;
    lastTime = now;
  };

  const onEnd = () => {
    isTransDragging.value = false;
    document.removeEventListener('mousemove', onMove);
    document.removeEventListener('mouseup', onEnd);
    document.removeEventListener('touchmove', onMove);
    document.removeEventListener('touchend', onEnd);
  };

  document.addEventListener('mousemove', onMove);
  document.addEventListener('mouseup', onEnd);
  document.addEventListener('touchmove', onMove, { passive: false });
  document.addEventListener('touchend', onEnd);
};

const updateTransInertia = () => {
  if (!isTransDragging.value && Math.abs(transVy.value) > 0.1) {
    transVy.value *= 0.95; // Friction
    transY.value += transVy.value;
    
    // Bounce / Bounds
    const h = window.innerHeight;
    if (transY.value > h) { transY.value = h; transVy.value *= -0.5; }
    if (transY.value < 0) { transY.value = 0; transVy.value *= -0.5; }
    
    if (Math.abs(transVy.value) < 0.1) transVy.value = 0;
  }
};

const updateTransPosition = (e: MouseEvent | TouchEvent) => {
   // Deprecated
};

const handleTransMouseMove = (e: MouseEvent) => {
   // Deprecated
};

const stopTransDrag = () => {
  isTransDragging.value = false;
};

// --- Bezold ---
const bezoldLightness = ref(90);

// --- Vibration ---
const vibBgColor = '#ff0000';
const vibLightness = ref(50);

// --- Subtraction (Enhanced with Inertia) ---
const subBg1 = '#000000';
const subBg2 = '#ffffff';
// Inverted Logic:
// Target 1: A darker off-white/grey on BLACK background
// Target 2: A lighter off-white on WHITE background
// Goal: When moved to opposite backgrounds, they look "the same" (or vice versa, move to same background to see difference).
// Let's try to make them look different initially, and same when moved?
// Or: "Make two different colors look the same".
// Let's use two Greys.
// Grey 1 on Black makes it look Lighter.
// Grey 2 on White makes it look Darker.
// If we want them to LOOK the same on their respective backgrounds:
// We need Grey 1 (on Black) to be Darker real color.
// We need Grey 2 (on White) to be Lighter real color.
// Wait, Albers' "Subtraction" is usually:
// 1. Two different colors look the same.
// 2. Two same colors look different.
// User wants "White squares". So they should be bright.
// Let's use:
// const subTarget1 = '#cccccc'; 
// const subTarget2 = '#f5f5f5'; 

// Final decision:
const subTarget1 = '#525252'; // Dark Grey
const subTarget2 = '#d4d4d4'; // Light Grey
const subContainerRef = ref<HTMLElement | null>(null);

// Initial positions: Put them on sides (User Request 4)
// We need to calculate this based on screen width, but refs are dynamic.
// Use offsets relative to center.
// Left item: -25% width, Right item: +25% width
const subDrag1 = ref({ x: -200, y: 0, isDragging: false, vx: 0, vy: 0, lastX: 0, lastY: 0 });
const subDrag2 = ref({ x: 200, y: 0, isDragging: false, vx: 0, vy: 0, lastX: 0, lastY: 0 });

const startSubDrag = (e: MouseEvent, item: number) => {
  const target = item === 1 ? subDrag1 : subDrag2;
  target.value.isDragging = true;
  target.value.vx = 0;
  target.value.vy = 0;
  
  const startX = e.clientX - target.value.x;
  const startY = e.clientY - target.value.y;

  // For inertia calculation
  let lastMouseX = e.clientX;
  let lastMouseY = e.clientY;
  let lastTime = performance.now();

  const onMouseMove = (moveEvent: MouseEvent) => {
    const now = performance.now();
    const dt = now - lastTime;
    
    // Update position
    target.value.x = moveEvent.clientX - startX;
    target.value.y = moveEvent.clientY - startY;

    // Calculate Velocity (pixels per frame approx)
    if (dt > 0) {
      target.value.vx = (moveEvent.clientX - lastMouseX); // Simple delta
      target.value.vy = (moveEvent.clientY - lastMouseY);
    }
    
    lastMouseX = moveEvent.clientX;
    lastMouseY = moveEvent.clientY;
    lastTime = now;
  };

  const onMouseUp = () => {
    target.value.isDragging = false;
    document.removeEventListener('mousemove', onMouseMove);
    document.removeEventListener('mouseup', onMouseUp);
  };
  
  document.addEventListener('mousemove', onMouseMove);
  document.addEventListener('mouseup', onMouseUp);
};

const updateSubInertia = () => {
  [subDrag1, subDrag2].forEach(target => {
    if (!target.value.isDragging) {
      // Apply friction
      target.value.vx *= 0.92;
      target.value.vy *= 0.92;

      // Stop if slow enough
      if (Math.abs(target.value.vx) < 0.1) target.value.vx = 0;
      if (Math.abs(target.value.vy) < 0.1) target.value.vy = 0;

      // Apply velocity
      target.value.x += target.value.vx;
      target.value.y += target.value.vy;
      
      // Boundary checks (Simple bounce or stop) - Optional, keeping it simple
      // Let them fly off screen if user wants, or bound them?
      // Bounding to roughly window size would be nice.
      const w = window.innerWidth / 2;
      const h = window.innerHeight / 2;
      if (target.value.x > w) { target.value.x = w; target.value.vx *= -0.5; }
      if (target.value.x < -w) { target.value.x = -w; target.value.vx *= -0.5; }
      if (target.value.y > h) { target.value.y = h; target.value.vy *= -0.5; }
      if (target.value.y < -h) { target.value.y = -h; target.value.vy *= -0.5; }
    }
  });
};

// --- Afterimage ---
const showAfterimageWhite = ref(false);
const toggleAfterimage = () => {
  showAfterimageWhite.value = !showAfterimageWhite.value;
};

// --- Recommended Reading (User Request 5) ---
const recommendedBooks = [
  { title: '色彩互动学', author: '约瑟夫·阿尔伯斯' },
  { title: '包豪斯', author: '玛格达莱娜·德罗斯特' },
  { title: '色彩的艺术', author: '约翰内斯·伊顿' },
  { title: '颜色论', author: '歌德' }
]; // Reduced to 4

// Lifecycle
onMounted(() => {
  animFrame = requestAnimationFrame(animate);
  
  // Set initial positions for subtraction based on real window width
  if (window.innerWidth > 0) {
    subDrag1.value.x = -window.innerWidth * 0.25;
    subDrag2.value.x = window.innerWidth * 0.25;
  }
});

onUnmounted(() => {
  cancelAnimationFrame(animFrame);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;700;900&display=swap');

.interaction-container {
  --font-modern: 'Inter', 'Helvetica Neue', Arial, sans-serif;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-artistic: 'Inter', sans-serif; /* Bauhaus/Modernist style is clean sans */
}

.interaction-container {
  width: 100%;
  height: 100vh;
  background-color: #f0f0f0;
  color: #333;
  overflow-y: auto;
  overflow-x: hidden;
  font-family: var(--font-modern);
  position: relative;
}

/* Typography Updates */
.main-title {
  font-family: var(--font-artistic);
  font-weight: 900;
  letter-spacing: -2px;
}

.author-quote, .section-desc {
  font-family: var(--font-modern);
}

.section-title, .book-card-mini h3 {
  font-family: var(--font-ui);
  font-weight: 700;
}

.return-btn, .footer-btn, .instruction {
  font-family: var(--font-ui);
}

/* Numbers (if any) */
.number {
  font-family: var(--font-modern);
  font-weight: bold;
}

/* Return Buttons */
.return-btn.fixed-top-left {
  position: fixed;
  top: 24px;
  left: 24px;
  z-index: 2000;
  background: #fff;
  border: none;
  padding: 12px 24px;
  font-family: 'Helvetica Neue', sans-serif;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  color: #333;
}

.return-btn.fixed-top-left:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
  background: #000;
  color: #fff;
}

.footer-action {
  position: absolute;
  bottom: 40px;
  width: 100%;
  display: flex;
  justify-content: center;
  z-index: 100;
  pointer-events: none; /* Let clicks pass through if needed, but button needs pointer-events auto */
}

.footer-btn {
  pointer-events: auto;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  padding: 15px 40px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  transition: all 0.3s;
  letter-spacing: 1px;
  color: #333;
}

.footer-btn:hover {
  background: #000;
  color: #fff;
  transform: translateY(-3px);
}

.section {
  width: 100vw;
  height: 100vh;
  scroll-snap-align: start;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* Common Text Styles */
.content-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none; /* Allow clicks through to demos */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 20;
}

.text-panel {
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(4px);
  padding: 20px 40px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 20px;
  pointer-events: auto; /* Enable text selection */
  max-width: 600px;
}

.text-panel.dark-text {
  background: rgba(255,255,255,0.6);
  color: #111;
}

.text-panel.invert-mix {
  mix-blend-mode: difference;
  background: transparent;
  backdrop-filter: none;
}

.text-panel.subtraction-text-panel {
  background: transparent;
  backdrop-filter: none;
  mix-blend-mode: normal;
  box-shadow: none;
}

.section-title {
  font-size: 3rem;
  margin: 0 0 10px;
  font-weight: 900;
}

.section-desc {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* Hero Section */
.section-hero {
  background: #000;
}

.hero-grid {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
}

.hero-block {
  width: 100%; height: 100%;
  transition: filter 0.3s ease;
  cursor: pointer;
}

.hero-title-overlay {
  pointer-events: none;
  mix-blend-mode: difference;
  text-align: center;
  z-index: 30;
  color: #fff;
}

.main-title {
  font-size: 6rem;
  font-weight: 900;
  margin: 0;
  line-height: 1;
  letter-spacing: -2px;
}

.author-quote {
  font-size: 1.2rem;
  margin-top: 20px;
  font-style: italic;
  opacity: 0.8;
}

.scroll-hint {
  position: absolute; bottom: 40px; left: 50%;
  transform: translateX(-50%);
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 2px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translate(-50%, 0); }
  50% { transform: translate(-50%, 10px); }
}

/* Section 1: Relativity */
.split-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  display: flex;
}
.bg-half { flex: 1; height: 100%; }
.bg-half.left { background-color: #e5e5e5; }
.bg-half.right { background-color: #525252; }

.relativity-demo-layout {
  display: flex;
  width: 100%; height: 100%;
  position: absolute;
  top: 0; left: 0;
  pointer-events: none;
}

.demo-area {
  flex: 1;
  display: flex; align-items: center; justify-content: center;
  pointer-events: auto;
}

.draggable-square, .reference-square {
  width: 120px; height: 120px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  cursor: grab;
}
.draggable-square:active { cursor: grabbing; }

/* Section 2: Transparency */
.section-transparency {
  background: #fdfbf7; 
  color: #333;
}

.transparency-fullscreen {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  cursor: ns-resize;
  pointer-events: auto; /* Fix interaction */
}

.transparency-overlay {
  justify-content: flex-end; /* Move text to bottom */
  padding-bottom: 50px;
}

.small-bottom-text {
  max-width: 80%;
  padding: 10px 20px;
  margin-bottom: 0; /* Override default */
}

.small-title {
  font-size: 1.5rem;
}

.small-desc {
  font-size: 0.9rem;
}

.trans-shape {
  position: absolute;
  height: 80vh;
  width: 50vw; /* Connect in middle */
  top: 50%; transform: translateY(-50%);
  pointer-events: none; /* Let clicks pass to container */
}
.shape-left { left: 0; }
.shape-right { right: 0; }

.trans-mix-strip {
  position: absolute;
  width: 60vw;
  height: 15vh;
  border: 4px solid #fff;
  z-index: 10;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 20px 50px rgba(0,0,0,0.2);
  pointer-events: none; /* Container handles drag */
}

.trans-mix-strip.vertical-strip {
  width: 400px; /* 5x wider (was 80px) */
  height: 80vh; /* Match shapes height */
  border: 2px solid #fff; /* Slightly thinner border */
  transform: translateY(-50%); /* Center anchor */
}
  
  .drag-handle-icon {
    color: #fff;
    font-size: 2rem;
    text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  }
  
  /* Section 3: Bezold */
  .section-bezold {
    transition: background-color 0.1s;
  }
  
  .bezold-grid-square {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    grid-template-rows: repeat(8, 1fr);
    gap: 10px;
    width: 50vh;
    height: 50vh;
    pointer-events: none;
    margin-top: 20px;
  }
  
  .brick-square {
    background-color: #ef4444;
    width: 100%; height: 100%;
    opacity: 0;
    animation: appear 0.6s forwards ease-out;
  }
  
  @keyframes appear {
    0% { transform: scale(0); opacity: 0; }
    100% { transform: scale(1); opacity: 1; }
  }
  
  /* Section 4: Vibration */
  .section-vibration {
    /* bg set by style */
  }
  
  .vib-square-large {
    width: 30vw; height: 30vw;
    max-width: 400px; max-height: 400px;
    pointer-events: none;
  }
  
  /* Section 5: Subtraction */
.subtraction-full-layout {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
}

.sub-item-large {
  position: absolute;
  width: 150px; height: 150px;
  display: flex; align-items: center; justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  color: rgba(0,0,0,0.3);
  box-shadow: none; /* Removed shadow */
  cursor: grab;
  pointer-events: auto;
  top: 50%; left: 50%;
  margin-top: -75px; margin-left: -75px;
}
.sub-item-large:active { cursor: grabbing; }

/* Section 6: Afterimage */
.afterimage-fullscreen {
  width: 100%; height: 100%;
  background-color: #ef4444; /* Red by default */
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
}

.afterimage-fullscreen.show-white {
  background-color: #fff;
}

.ai-content { text-align: center; }
.ai-circle-large {
  width: 300px; height: 300px;
  background-color: #000; /* Contrast */
  border-radius: 50%;
  margin: 40px auto;
  display: flex; align-items: center; justify-content: center;
}
.ai-dot-large {
  width: 20px; height: 20px;
  background-color: #fff;
  border-radius: 50%;
}

/* Section 7: Recommendations */
.section-recommendations {
  background: #111;
}

.book-grid-center {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  max-width: 800px;
  width: 90%;
  pointer-events: auto;
}

.book-card-mini {
  background: #222;
  padding: 30px;
  border-radius: 12px;
  color: #eee;
  text-align: left;
  transition: transform 0.2s;
}

.book-card-mini:hover {
  transform: translateY(-5px);
  background: #333;
}

.book-card-mini h3 { color: #fff; margin-top: 0; font-size: 1.2rem; }
.book-author { color: #888; text-transform: uppercase; font-size: 0.9rem; margin: 0; }

.back-home-btn {
  margin-top: 50px;
  padding: 15px 50px;
  background: #fff;
  color: #000;
  border: none;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  pointer-events: auto;
  transition: transform 0.2s;
}
.back-home-btn:hover { transform: scale(1.1); }

.controls-bottom {
  position: absolute; bottom: 50px;
  width: 80%;
  left: 50%;
  transform: translateX(-50%);
  pointer-events: auto;
  z-index: 50;
}

.full-width-slider {
  width: 100%;
  cursor: pointer;
}

@media (max-width: 768px) {
  .main-title { font-size: 3rem; }
  .hero-grid { grid-template-columns: 1fr 1fr !important; grid-template-rows: repeat(4, 1fr) !important; }
  .split-bg { flex-direction: column; }
  .bg-half { width: 100%; height: 50%; }
  .relativity-demo-layout { flex-direction: column; }
  .trans-shape { width: 100%; height: 40vh; }
  .shape-left { top: 0; transform: none; }
  .shape-right { top: auto; bottom: 0; transform: none; }
  .trans-mix-strip { width: 80vw; height: 10vh; }
  .book-grid-center { grid-template-columns: 1fr; }
}
</style>
