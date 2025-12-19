<template>
  <div class="grid-page-wrapper" ref="pageWrapper" @scroll="handleScroll">
    <!-- Progress Bar -->
    <div class="scroll-progress" :style="{ width: scrollProgress + '%' }"></div>

    <!-- Hero Section -->
    <section class="hero-section" id="hero">
      <GridSystemsCover mode="hero" />
      <div class="scroll-indicator">
        <span>SCROLL TO EXPLORE THE ORDER</span>
        <div class="line"></div>
      </div>
    </section>

    <!-- Sticky Navigation -->
    <nav class="sticky-nav" :class="{ visible: showNav }">
      <button class="return-btn nav-back-btn" @click="goBackHome">
        <span>← EXIT GRID</span>
      </button>
      <div class="nav-content">
        <div class="nav-brand">GRID SYSTEMS</div>
        <ul class="nav-links">
          <li v-for="(section, index) in sections" :key="index">
            <a 
              :href="'#' + section.id" 
              :class="{ active: activeSection === section.id }"
              @click.prevent="scrollTo(section.id)"
            >
              <span class="nav-num">0{{ index + 1 }}</span>
              <span class="nav-text">{{ section.name }}</span>
            </a>
          </li>
        </ul>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="content-container">
      
      <!-- 1. Philosophy: Order & Freedom -->
      <section id="philosophy" class="content-section philosophy-section">
        <div class="grid-overlay"></div>
        <div class="content-wrapper">
          <div class="huge-text-block" ref="reveal1">
            <h2 class="section-heading">秩序<br>与自由</h2>
            <p class="section-subtitle">ORDER IS THE FOUNDATION OF FREEDOM.</p>
          </div>
          <div class="text-columns">
            <div class="col">
              <p>网格系统是平面设计中的一种辅助工具，而非保证。它允许各种可能的用途，每个设计师都可以寻找适合其个人风格的解决方案。但必须学会如何使用网格；这是一门需要练习的艺术。</p>
            </div>
            <div class="col">
              <p>The grid system is an aid, not a guarantee. It permits a number of possible uses and each designer can look for a solution appropriate to his personal style. But one must learn how to use the grid; it is an art that requires practice.</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 2. Construction: Interactive Grid Tool -->
      <section id="construction" class="content-section construction-section">
        <div class="content-wrapper">
          <h2 class="section-heading">构建网格</h2>
          <p class="section-desc">"The grid system is an aid, not a guarantee." —— 拖动滑块探索网格的韵律。</p>
          
          <div class="interactive-grid-tool">
            <!-- Controls (Left) -->
            <div class="grid-controls">
              <div class="control-group">
                 <div class="control-header">
                   <label>COLUMNS</label>
                   <span class="control-val">{{ gridConfig.columns }}</span>
                 </div>
                 <input type="range" v-model.number="gridConfig.columns" min="1" max="18" step="1" class="swiss-range" />
              </div>
              
              <div class="control-group">
                 <div class="control-header">
                   <label>GUTTER</label>
                   <span class="control-val">{{ gridConfig.gutter }}px</span>
                 </div>
                 <input type="range" v-model.number="gridConfig.gutter" min="0" max="50" step="1" class="swiss-range" />
              </div>

              <div class="control-group">
                 <div class="control-header">
                   <label>MARGIN</label>
                   <span class="control-val">{{ gridConfig.margin }}px</span>
                 </div>
                 <input type="range" v-model.number="gridConfig.margin" min="0" max="150" step="1" class="swiss-range" />
              </div>
            </div>

            <!-- Visual Display (Right) -->
            <div class="grid-display-frame">
              <transition-group name="grid-col" tag="div" class="grid-display-container" :style="gridContainerStyle">
                 <div 
                   v-for="bar in bars" 
                   :key="bar.id" 
                   class="grid-col-bar"
                   :class="bar.colorClass"
                   :style="{ 
                     gridColumn: `span ${bar.colSpan}`,
                     height: bar.height
                   }"
                 >
                 </div>
              </transition-group>
            </div>
          </div>
        </div>
      </section>

      <!-- 3. Typography: Hierarchy -->
      <section id="typography" class="content-section typography-section">
        <div class="content-wrapper">
          <h2 class="section-heading">排版层级</h2>
          <div class="type-scale-demo">
            <div class="scale-item" v-for="(size, index) in typeSizes" :key="index">
              <div class="scale-label">{{ size }}px</div>
              <div class="scale-text" :style="{ fontSize: size + 'px' }">
                设计是思维的视觉化。
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 4. Swiss Style: Generative Art -->
      <section id="swiss-style" class="content-section swiss-section">
        <div class="content-wrapper">
          <h2 class="section-heading">瑞士风格生成器</h2>
          <p class="section-desc">"Objective. Clear. Mathematical." —— 点击生成随机设计。</p>
          
          <div class="generator-wrapper" @click="generateSwissPoster">
             <div class="swiss-canvas">
                <div class="paper-grid-bg"></div>
                <div class="generated-elements">
                  <div 
                    v-for="(el, i) in swissElements" 
                    :key="i"
                    class="swiss-el"
                    :style="el.style"
                    :class="el.class"
                  >
                    <span v-if="el.content" v-html="el.content"></span>
                  </div>
                </div>
                <div class="canvas-overlay">
                  <span>CLICK TO GENERATE</span>
                </div>
             </div>
          </div>
        </div>
      </section>

      <!-- 5. Practice: The Manual -->
      <section id="practice" class="content-section practice-section">
        <div class="content-wrapper">
          <h2 class="section-heading">设计手册</h2>
          <div class="book-3d-wrapper">
             <div class="book-3d">
               <div class="book-cover">
                 <div class="spine"></div>
                 <div class="front">
                   <h1>GRID<br>SYSTEMS</h1>
                 </div>
               </div>
             </div>
          </div>
          <p class="final-quote">
            "Objective, clear, mathematical."
          </p>
        </div>
        
        <button class="back-home-btn" @click="goBackHome">
          <span>返回首页</span>
        </button>
      </section>

      <!-- 3. Footer -->
      <footer class="grid-footer">
        <div class="footer-grid-bg"></div>
        <div class="footer-content">
          <p class="swiss-quote">"Design is the method of putting form and content together."</p>
          
          <div v-if="!hasRated" class="rating-grid">
            <button v-if="!showRatingOptions" class="footer-btn" @click="showRatingOptions = true">评价设计</button>
            <div v-else class="grid-options">
                <button class="grid-opt" @click="rateBook(0)">
                    <span class="grid-num">0</span> 混乱
                </button>
                <button class="grid-opt" @click="rateBook(1)">
                    <span class="grid-num">1</span> 秩序
                </button>
                <button class="grid-opt" @click="rateBook(2)">
                    <span class="grid-num">2</span> 瑞士风格
                </button>
            </div>
          </div>
          <button v-else class="footer-btn" @click="goBackHome">返回首页</button>
        </div>
      </footer>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, reactive, watch } from 'vue';
import { useRouter } from 'vue-router';
import GridSystemsCover from '@/components/business/covers/GridSystemsCover.vue';
import myAxios from "@/api/index";
import { useCookies } from '@vueuse/integrations/useCookies';
import { ElMessage } from "element-plus";

const pageWrapper = ref<HTMLElement | null>(null);
const router = useRouter();
const cookies = useCookies();

// Rating Logic
const hasRated = ref(false);
const showRatingOptions = ref(false);

const rateBook = async (level: number) => {
  const username = cookies.get('username');
  const userId = cookies.get('userId');
  if (!username) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  try {
    const res = await myAxios.put('/bookLike/like', {
      userId: userId,
      userName: username,
      bookId: 4, // Grid Systems ID
      bookName: "Grid Systems",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('秩序已建立');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '网格错位');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('系统错误');
  }
};

// --- Navigation ---
const sections = [
  { id: 'philosophy', name: '秩序与自由' },
  { id: 'construction', name: '构建网格' },
  { id: 'typography', name: '排版层级' },
  { id: 'swiss-style', name: '瑞士风格' },
  { id: 'practice', name: '设计手册' }
];

const activeSection = ref('hero');
const showNav = ref(false);
const scrollProgress = ref(0);

const scrollTo = (id: string) => {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
};

const goBackHome = () => {
  router.back();
};

// --- Interactive Grid Tool ---
const gridConfig = reactive({
  columns: 12,
  gutter: 20,
  margin: 40
});

interface Bar {
  id: number;
  colSpan: number;
  height: string;
  colorClass: string;
}

const bars = ref<Bar[]>([]);

const randomizeBars = () => {
  bars.value = Array.from({ length: 18 }, (_, i) => ({
    id: i,
    colSpan: Math.random() > 0.6 ? 2 : 1, // 40% chance of spanning 2 cols
    height: Math.random() > 0.5 ? '150px' : '300px',
    colorClass: i % 2 === 0 ? 'is-black' : 'is-red'
  }));
};

// Initial random state
randomizeBars();

// Re-randomize on column change to create dynamic effect
watch(() => gridConfig.columns, randomizeBars);

const gridContainerStyle = computed(() => ({
  display: 'grid',
  gridTemplateColumns: `repeat(${gridConfig.columns}, 1fr)`,
  gap: `${gridConfig.gutter}px`,
  paddingLeft: `${gridConfig.margin}px`,
  paddingRight: `${gridConfig.margin}px`,
  gridAutoFlow: 'dense' // Helps pack different sizes
}));


// --- Swiss Generator ---
interface SwissElement {
  style: any;
  class: string;
  content?: string;
}

const swissElements = ref<SwissElement[]>([]);

const words = ["MUSICA", "VIVA", "KUNST", "JAZZ", "BALLET", "THEATER", "FILM", "DESIGN", "ZÜRICH", "BASEL"];
const colors = ["#000", "#ff3333", "#000", "#000", "#333"];

const generateSwissPoster = () => {
  const els: SwissElement[] = [];
  const grid = 12;
  const unit = 100 / grid; // percentage unit

  // 1. Big Title (Rotated or Standard)
  const isRotated = Math.random() > 0.5;
  const title = words[Math.floor(Math.random() * words.length)];
  els.push({
    class: 'swiss-title',
    style: {
      top: `${Math.floor(Math.random() * 8) * unit}%`,
      left: `${Math.floor(Math.random() * 8) * unit}%`,
      fontSize: `${Math.max(4, Math.random() * 10)}rem`,
      transform: isRotated ? 'rotate(-90deg)' : 'none',
      transformOrigin: 'top left'
    },
    content: title
  });

  // 2. Geometric Shape (Circle or Rect)
  const shapeType = Math.random() > 0.5 ? 'circle' : 'rect';
  const color = colors[Math.floor(Math.random() * colors.length)];
  els.push({
    class: `swiss-shape ${shapeType}`,
    style: {
      top: `${Math.floor(Math.random() * 10) * unit}%`,
      left: `${Math.floor(Math.random() * 10) * unit}%`,
      width: `${Math.floor(Math.random() * 4 + 2) * unit}%`,
      height: `${Math.floor(Math.random() * 4 + 2) * unit}%`,
      backgroundColor: color,
      borderRadius: shapeType === 'circle' ? '50%' : '0'
    }
  });

  // 3. Thick Rule / Line
  if (Math.random() > 0.3) {
    const isVertical = Math.random() > 0.5;
    els.push({
      class: 'swiss-line',
      style: {
        top: `${Math.floor(Math.random() * 12) * unit}%`,
        left: `${Math.floor(Math.random() * 12) * unit}%`,
        width: isVertical ? '10px' : `${Math.floor(Math.random() * 6 + 2) * unit}%`,
        height: isVertical ? `${Math.floor(Math.random() * 6 + 2) * unit}%` : '10px',
        backgroundColor: '#000'
      }
    });
  }

  // 4. Small Info Text
  els.push({
    class: 'swiss-info',
    style: {
      bottom: '5%',
      left: '5%',
      textAlign: 'left'
    },
    content: `Tonhalle<br>Zürich<br>20.00 Uhr`
  });

  swissElements.value = els;
};

// --- Type Scale ---
const typeSizes = [12, 14, 16, 20, 24, 36, 48, 60, 72];

// --- Scroll Logic ---
const handleScroll = () => {
  if (!pageWrapper.value) return;
  const winScroll = pageWrapper.value.scrollTop;
  const height = pageWrapper.value.scrollHeight - pageWrapper.value.clientHeight;
  scrollProgress.value = (winScroll / height) * 100;
  
  showNav.value = winScroll > window.innerHeight * 0.8;
};

let observer: IntersectionObserver;

onMounted(() => {
  // window.addEventListener('scroll', handleScroll); // Removed: listening on div
  
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        activeSection.value = entry.target.id;
        // Trigger reveal animations if needed
        if (entry.target.classList.contains('content-section')) {
            entry.target.classList.add('visible');
        }
      }
    });
  }, { 
    threshold: 0.3,
    root: pageWrapper.value // Important: observe within the scrolling container
  });

  sections.forEach(s => {
    const el = document.getElementById(s.id);
    if (el) observer.observe(el);
  });
});

onUnmounted(() => {
  // window.removeEventListener('scroll', handleScroll);
  if (observer) observer.disconnect();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700;900&display=swap');

.grid-page-wrapper {
  --font-swiss: 'Inter', 'Helvetica Neue', Arial, sans-serif;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-serif: 'Songti SC', 'SimSun', serif; /* Though Grid Systems is pure sans, user rule applies */
}

.grid-page-wrapper {
  width: 100%;
  height: 100vh;
  background-color: #ffffff;
  color: #1a1a1a;
  overflow-y: auto;
  overflow-x: hidden;
  font-family: var(--font-swiss);
  position: relative;
  scroll-behavior: smooth;
}

/* Typography Updates */
.section-heading, .section-subtitle, .nav-brand {
  font-family: var(--font-swiss);
  font-weight: 700;
  letter-spacing: -1px;
}

.nav-text, .return-btn {
  font-family: var(--font-ui);
}

.text-columns p {
  font-family: var(--font-swiss); /* Keep English sans */
}

/* Apply Songti to Chinese text if we can target it, or generally to p tags if mixed */
/* For Grid Systems, sticking to Sans is very thematic, but user asked for "Literary text, excerpts... use Songti". 
   Philosophy text (p) here is kind of an excerpt. Let's use vars. */

.text-columns .col:first-child p { /* Chinese column */
   font-family: var(--font-serif);
   font-weight: bold;
}

.control-header label, .control-val {
  font-family: var(--font-ui);
  font-weight: bold;
}

/* Numbers */
.nav-num, .control-val {
  font-family: var(--font-swiss);
}

/* Return Buttons */
.return-btn {
  /* Position handled by nav-back-btn when inside nav */
  z-index: 2000;
  background: #f0f0f0;
  border: 1px solid #333;
  padding: 10px 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  font-weight: bold;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
  letter-spacing: 1px;
}

.nav-back-btn {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  margin: 0;
}

.return-btn:hover {
  background: #ff4500;
  color: #fff;
  border-color: #ff4500;
}

.footer-btn {
  background: transparent;
  border: 2px solid #fff;
  color: #fff;
  padding: 15px 40px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 2px;
  margin-top: 30px;
  text-transform: uppercase;
}

.footer-btn:hover {
  background: #fff;
  color: #000;
}

.scroll-progress {
  position: fixed;
  top: 0; left: 0;
  height: 4px;
  background-color: #ff3333;
  z-index: 9999;
  transition: width 0.1s;
}

/* Hero */
.hero-section {
  height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 2px;
  animation: bounce 2s infinite;
}

.scroll-indicator .line {
  width: 1px;
  height: 40px;
  background-color: #000;
  margin-top: 10px;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateX(-50%) translateY(0); }
  40% { transform: translateX(-50%) translateY(-10px); }
  60% { transform: translateX(-50%) translateY(-5px); }
}

/* Nav */
.sticky-nav {
  position: fixed;
  top: 0; left: 0; width: 100%;
  padding: 20px 40px;
  background: rgba(244, 244, 240, 0.9);
  backdrop-filter: blur(10px);
  z-index: 1000;
  border-bottom: 1px solid rgba(0,0,0,0.1);
  transform: translateY(-100%);
  transition: transform 0.4s ease;
}

.sticky-nav.visible {
  transform: translateY(0);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.nav-brand {
  font-weight: 900;
  letter-spacing: -1px;
  font-size: 1.2rem;
}

.nav-links {
  list-style: none;
  display: flex;
  gap: 30px;
  margin: 0; padding: 0;
}

.nav-links a {
  text-decoration: none;
  color: #666;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: color 0.3s;
}

.nav-links a.active, .nav-links a:hover {
  color: #ff3333;
}

.nav-num {
  font-family: monospace;
  font-size: 0.8rem;
}

/* Content */
.content-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.content-section {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  padding: 100px 0;
  opacity: 0;
  transform: translateY(50px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}

.content-section.visible {
  opacity: 1;
  transform: translateY(0);
}

.section-heading {
  font-family: 'Noto Serif SC', serif;
  font-size: 4rem;
  font-weight: 900;
  margin-bottom: 20px;
  line-height: 1.1;
  position: relative;
}

.section-heading::before {
  content: '';
  position: absolute;
  left: -20px; top: 0; bottom: 0;
  width: 4px;
  background-color: #ff3333;
}

/* 1. Philosophy */
.philosophy-section .text-columns {
  display: flex;
  gap: 40px;
  margin-top: 60px;
}

.philosophy-section .col {
  flex: 1;
  font-size: 1.2rem;
  line-height: 1.6;
}

/* 2. Construction: Interactive Grid Tool */
.interactive-grid-tool {
  margin-top: 40px;
  display: flex;
  flex-direction: row;
  gap: 40px;
  align-items: flex-start;
}

.grid-display-frame {
  flex: 1;
  /* Remove fixed height, allow content to dictate height */
  min-height: 200px;
  background: transparent;
  border: 1px solid #ddd;
  overflow: hidden;
  display: flex;
  align-items: flex-start; /* Align to top */
  transition: all 0.5s ease;
}

.grid-display-container {
  width: 100%;
  /* Height is auto */
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.grid-col-bar {
  /* Height set inline */
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255,255,255,0.5);
  font-family: monospace;
  font-size: 0.8rem;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1); /* Bouncy spring transition */
  width: 100%; /* Fill the grid cell */
  transform-origin: center top;
}

/* Add enter/leave transitions for list items if needed, but we are just moving them */
.grid-col-move {
  transition: transform 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.grid-col-bar.is-red {
  background-color: #ff3333;
}
.grid-col-bar.is-black {
  background-color: #000;
}

.grid-controls {
  display: flex;
  flex-direction: column;
  gap: 40px;
  width: 250px;
  flex-shrink: 0;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.control-header {
  display: flex;
  justify-content: space-between;
  font-weight: 700;
  font-size: 0.8rem;
  color: #999;
}

.control-val {
  color: #000;
  font-family: monospace;
}

/* Range Slider Styling */
.swiss-range {
  -webkit-appearance: none;
  width: 100%;
  height: 4px;
  background: #ddd;
  outline: none;
  transition: background 0.2s;
}

.swiss-range::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 16px;
  height: 16px;
  background: #000;
  cursor: pointer;
  transition: transform 0.2s;
}

.swiss-range::-webkit-slider-thumb:hover {
  transform: scale(1.2);
  background: #ff3333;
}

/* 3. Typography */
.type-scale-demo {
  margin-top: 40px;
}

.scale-item {
  display: flex;
  align-items: baseline;
  border-bottom: 1px solid #ddd;
  padding: 20px 0;
}

.scale-label {
  width: 100px;
  font-family: monospace;
  color: #ff3333;
}

/* 4. Swiss Style Generator */
.generator-wrapper {
  margin-top: 60px;
  display: flex;
  justify-content: center;
}

.swiss-canvas {
  width: 500px;
  height: 700px; /* DIN ratio approx */
  background: #f4f4f0;
  position: relative;
  box-shadow: 0 20px 50px rgba(0,0,0,0.1);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}

.swiss-canvas:active {
  transform: scale(0.99);
}

.paper-grid-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(rgba(0,0,0,0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0,0,0,0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  pointer-events: none;
}

.generated-elements {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
}

.swiss-el {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
}

.swiss-title {
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  line-height: 0.8;
  color: #000;
  mix-blend-mode: multiply;
  white-space: nowrap;
}

.swiss-shape {
  mix-blend-mode: multiply;
}

.swiss-info {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 700;
  line-height: 1.4;
  color: #000;
}

.canvas-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  font-size: 1.5rem;
  font-weight: 900;
  letter-spacing: 2px;
  opacity: 0;
  transition: opacity 0.3s;
}

.swiss-canvas:hover .canvas-overlay {
  opacity: 1;
}

/* Hide overlay if elements exist (optional logic, but simple hover is fine) */
.swiss-canvas:hover .generated-elements + .canvas-overlay {
  opacity: 0; /* Only show on empty or specific state? No, let's keep it simple: hover shows hint if needed, but maybe better to just show it initially or not at all. Let's make it fade OUT when generated. */
}

/* Actually, let's make the overlay appear only on hover */
.swiss-canvas:hover .canvas-overlay {
  opacity: 0.1;
}

/* 5. Practice */
.practice-section {
  text-align: center;
}

.book-3d-wrapper {
  margin: 60px auto;
  perspective: 1000px;
  width: 300px;
  height: 400px;
}

.book-3d {
  width: 100%; height: 100%;
  transform-style: preserve-3d;
  transform: rotateY(-30deg);
  transition: transform 0.5s;
}

.book-3d:hover {
  transform: rotateY(0deg) scale(1.05);
}

.book-cover {
  width: 100%; height: 100%;
  background: #fff;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 10px 10px 30px rgba(0,0,0,0.2);
}

.book-cover h1 {
  font-size: 3rem;
  line-height: 1;
  text-align: left;
}

.final-quote {
  font-size: 2rem;
  font-style: italic;
  margin-bottom: 40px;
}

.back-home-btn {
  padding: 15px 40px;
  background: #000;
  color: #fff;
  border: none;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.3s;
}

.back-home-btn:hover {
  background: #ff3333;
}

/* Rating System */
.rating-grid {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.grid-options {
  display: flex;
  gap: 20px;
}

.grid-opt {
  background: transparent;
  border: 1px solid #fff;
  color: #fff;
  padding: 15px 30px;
  font-family: var(--font-swiss);
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.grid-num {
  font-size: 24px;
  display: block;
}

.grid-opt:hover {
  background: #fff;
  color: #000;
  transform: translateY(-5px);
}
</style>
