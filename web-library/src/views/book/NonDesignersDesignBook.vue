<template>
  <div class="book-page" ref="pageRef" @scroll="handleScroll">
    <!-- Fixed Background -->
    <div class="fixed-bg"></div>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content" :style="{ transform: `translateY(${scrollY * 0.5}px)` }">
        <h1 class="hero-title">
          <span class="block-reveal">写给</span>
          <span class="block-reveal">大家看的</span>
          <span class="block-reveal highlight">设计书</span>
        </h1>
        <p class="hero-subtitle">The Non-Designer's Design Book</p>
        <div class="hero-scroll-indicator">
          <span>SCROLL TO LEARN</span>
          <div class="line"></div>
        </div>
      </div>
      
      <!-- Decorative Elements (Parallax) -->
      <div class="hero-shapes">
        <div class="shape circle" :style="{ transform: `translateY(${scrollY * -0.2}px)` }"></div>
        <div class="shape square" :style="{ transform: `translateY(${scrollY * -0.1}px) rotate(${scrollY * 0.1}deg)` }"></div>
      </div>
    </section>

    <!-- Sticky Navigation -->
    <nav class="sticky-nav" :class="{ 'is-stuck': isNavStuck }">
      <div class="nav-links">
        <a href="#intro" :class="{ active: activeSection === 'intro' }" @click.prevent="scrollTo('intro')">启蒙</a>
        <a href="#contrast" :class="{ active: activeSection === 'contrast' }" @click.prevent="scrollTo('contrast')">对比</a>
        <a href="#repetition" :class="{ active: activeSection === 'repetition' }" @click.prevent="scrollTo('repetition')">重复</a>
        <a href="#alignment" :class="{ active: activeSection === 'alignment' }" @click.prevent="scrollTo('alignment')">对齐</a>
        <a href="#proximity" :class="{ active: activeSection === 'proximity' }" @click.prevent="scrollTo('proximity')">亲密</a>
      </div>
    </nav>

    <!-- Content Sections -->
    <div class="content-wrapper">
      
      <!-- 1. Intro (Enlightenment) -->
      <section id="intro" class="content-section intro-section">
        <div class="section-header">
          <h2>启蒙</h2>
          <h3>Joshua Tree Epiphany</h3>
        </div>
        <div class="text-block">
          <p>Once you can name something, you're conscious of it.</p>
          <p class="chinese-text">一旦你能够叫出什么东西的名字，你就拥有了对它的意识。</p>
          <p>掌握这四个基本原则，你将看懂所有设计。</p>
        </div>
      </section>

      <!-- 2. Contrast (Contrast) -->
      <section id="contrast" class="content-section contrast-section">
        <div class="section-header">
          <h2>对比</h2>
          <h3>Contrast</h3>
        </div>
        <div class="interactive-demo contrast-demo">
          <div class="demo-card" :class="{ 'high-contrast': contrastEnabled }">
            <div class="card-content">
              <h4>Don't be a wimp.</h4>
              <p>If two items are not exactly the same, make them <strong>different</strong>. Really different.</p>
            </div>
            <button class="toggle-btn" @click="contrastEnabled = !contrastEnabled">
              {{ contrastEnabled ? 'DISABLE CONTRAST' : 'ENABLE CONTRAST' }}
            </button>
          </div>
        </div>
      </section>

      <!-- 3. Repetition (Repetition) -->
      <section id="repetition" class="content-section repetition-section">
        <div class="section-header">
          <h2>重复</h2>
          <h3>Repetition</h3>
        </div>
        <div class="repetition-container">
          <div class="pattern-row" v-for="i in 5" :key="i" :style="{ transform: `translateX(${i % 2 === 0 ? scrollY * 0.1 : scrollY * -0.1}px)` }">
            <span v-for="j in 20" :key="j" class="pattern-item">REPEAT</span>
          </div>
        </div>
        <div class="text-block">
          <p>Repetition unifies and strengthens a piece by tying together otherwise separate parts.</p>
          <p class="chinese-text">重复的目的就是统一，并增强视觉效果。</p>
        </div>
      </section>

      <!-- 4. Alignment (Alignment) -->
      <section id="alignment" class="content-section alignment-section">
        <div class="section-header">
          <h2>对齐</h2>
          <h3>Alignment</h3>
        </div>
        <div class="alignment-demo">
          <div class="grid-line" v-if="alignmentEnabled"></div>
          <div class="floating-box box-1" :class="{ aligned: alignmentEnabled }">Nothing</div>
          <div class="floating-box box-2" :class="{ aligned: alignmentEnabled }">Arbitrary</div>
          <div class="floating-box box-3" :class="{ aligned: alignmentEnabled }">Should Be</div>
          <button class="align-btn" @mousedown="alignmentEnabled = true" @mouseup="alignmentEnabled = false" @mouseleave="alignmentEnabled = false">
            HOLD TO ALIGN
          </button>
        </div>
      </section>

      <!-- 5. Proximity (Proximity) -->
      <section id="proximity" class="content-section proximity-section">
        <div class="section-header">
          <h2>亲密性</h2>
          <h3>Proximity</h3>
        </div>
        <div class="proximity-demo" @mousemove="handleProximityMove">
          <div class="scatter-group" :class="{ gathered: isGathered }">
             <div class="dot d1">Email</div>
             <div class="dot d2">Phone</div>
             <div class="dot d3">Address</div>
             <div class="dot d4">Name</div>
             <div class="dot dCenter">CONTACT INFO</div>
          </div>
          <p class="instruction">Hover over the center to group related items.</p>
        </div>
      </section>

    </div>

    <!-- Footer -->
    <footer class="page-footer">
      <button class="return-home-btn" @click="router.push('/main')">返回首页</button>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const scrollY = ref(0);
const pageRef = ref<HTMLElement | null>(null);
const isNavStuck = ref(false);
const activeSection = ref('intro');

// Interactive States
const contrastEnabled = ref(false);
const alignmentEnabled = ref(false);
const isGathered = ref(false);

const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement;
  scrollY.value = target.scrollTop;
  
  // Sticky Nav Logic
  isNavStuck.value = scrollY.value > window.innerHeight - 100;

  // Active Section Logic
  const sections = ['intro', 'contrast', 'repetition', 'alignment', 'proximity'];
  for (const section of sections) {
    const el = document.getElementById(section);
    if (el) {
      const rect = el.getBoundingClientRect();
      if (rect.top < window.innerHeight / 2 && rect.bottom > window.innerHeight / 2) {
        activeSection.value = section;
      }
    }
  }
};

const scrollTo = (id: string) => {
  const el = document.getElementById(id);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
};

const handleProximityMove = (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  // Simple proximity check logic could go here, but using CSS hover for simplicity/performance
  // interacting with the .scatter-group container
  const center = target.closest('.proximity-demo')?.querySelector('.dCenter');
  if (center) {
     const rect = center.getBoundingClientRect();
     const dist = Math.hypot(e.clientX - (rect.left + rect.width/2), e.clientY - (rect.top + rect.height/2));
     isGathered.value = dist < 150;
  }
};

onMounted(() => {
  // Init observers if needed
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;700;900&display=swap');

.book-page {
  width: 100%;
  height: 100vh;
  overflow-y: auto;
  background-color: #f5f5f7;
  color: #1d1d1f;
  font-family: 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
  position: relative;
  scroll-behavior: smooth;
}

.fixed-bg {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: radial-gradient(#e5e5e5 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
  pointer-events: none;
  z-index: 0;
}

/* Hero Section */
.hero-section {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.hero-content {
  text-align: center;
  z-index: 2;
}

.hero-title {
  font-size: 8rem;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-shadow: 10px 10px 0px rgba(0,0,0,0.05);
}

.block-reveal {
  display: block;
}

.hero-title .highlight {
  color: #d2e603; /* Yellow Green */
  -webkit-text-stroke: 2px #000;
  text-shadow: 15px 15px 0px #000;
  transform: skewX(-10deg);
}

.hero-subtitle {
  font-size: 1.5rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  font-weight: 300;
  margin-top: 2rem;
}

.hero-scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  font-size: 0.8rem;
  letter-spacing: 0.1em;
  opacity: 0.6;
}

.line {
  width: 1px;
  height: 60px;
  background: #000;
  animation: growDown 2s infinite;
}

@keyframes growDown {
  0% { height: 0; opacity: 0; }
  50% { height: 60px; opacity: 1; }
  100% { height: 60px; opacity: 0; transform: translateY(60px); }
}

.hero-shapes .shape {
  position: absolute;
  opacity: 0.1;
  z-index: 1;
}

.circle {
  width: 400px; height: 400px;
  border: 40px solid #000;
  border-radius: 50%;
  top: -100px; left: -100px;
}

.square {
  width: 300px; height: 300px;
  background: #2563eb; /* Blue */
  bottom: 10%; right: 10%;
}

/* Sticky Nav */
.sticky-nav {
  position: sticky;
  top: 0;
  width: 100%;
  background: rgba(245, 245, 247, 0.8);
  backdrop-filter: blur(20px);
  z-index: 100;
  padding: 20px 0;
  border-bottom: 1px solid rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

.nav-links {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.nav-links a {
  text-decoration: none;
  color: #666;
  font-weight: 700;
  font-size: 1rem;
  position: relative;
  transition: color 0.3s;
}

.nav-links a.active, .nav-links a:hover {
  color: #000;
}

.nav-links a.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #000;
}

/* Content Sections */
.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-section {
  min-height: 80vh;
  padding: 100px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-header h2 {
  font-size: 4rem;
  font-weight: 900;
  margin: 0;
  line-height: 1;
}

.section-header h3 {
  font-size: 1.2rem;
  font-weight: 300;
  text-transform: uppercase;
  letter-spacing: 0.2em;
  margin: 10px 0 0;
  color: #666;
}

.text-block {
  max-width: 600px;
  text-align: center;
  font-size: 1.2rem;
  line-height: 1.8;
}

.chinese-text {
  font-weight: 700;
  margin-top: 20px;
}

/* Contrast Demo */
.contrast-demo {
  perspective: 1000px;
}

.demo-card {
  width: 500px;
  padding: 40px;
  background: #e5e5e5;
  color: #999;
  border-radius: 20px;
  text-align: center;
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.demo-card h4 {
  font-size: 2rem;
  margin: 0 0 20px;
  transition: inherit;
}

.demo-card.high-contrast {
  background: #000;
  color: #fff;
  transform: scale(1.05);
  box-shadow: 0 20px 50px rgba(0,0,0,0.3);
}

.demo-card.high-contrast h4 {
  color: #d2e603;
}

.toggle-btn {
  margin-top: 30px;
  padding: 15px 30px;
  border: 2px solid currentColor;
  background: transparent;
  color: inherit;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
}

.toggle-btn:hover {
  background: rgba(255,255,255,0.1);
}

/* Repetition Demo */
.repetition-container {
  width: 100vw;
  overflow: hidden;
  margin: 40px 0;
}

.pattern-row {
  white-space: nowrap;
  font-size: 4rem;
  font-weight: 900;
  color: rgba(0,0,0,0.05);
  line-height: 1;
  transition: transform 0.1s linear;
}

.pattern-item {
  margin-right: 40px;
}

/* Alignment Demo */
.alignment-demo {
  position: relative;
  width: 500px;
  height: 400px;
  border: 2px dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.grid-line {
  position: absolute;
  top: 0; bottom: 0; left: 20%;
  width: 2px;
  background: #d2e603;
  z-index: 10;
}

.floating-box {
  position: absolute;
  padding: 10px 20px;
  background: #fff;
  border: 1px solid #000;
  font-weight: 700;
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55);
}

.box-1 { top: 20%; left: 60%; transform: rotate(5deg); }
.box-2 { top: 50%; left: 40%; transform: rotate(-10deg); }
.box-3 { bottom: 20%; right: 20%; transform: rotate(15deg); }

.box-1.aligned { left: 20%; transform: rotate(0); }
.box-2.aligned { left: 20%; transform: rotate(0); }
.box-3.aligned { left: 20%; transform: rotate(0); }

.align-btn {
  position: absolute;
  bottom: -60px;
  padding: 15px 30px;
  background: #000;
  color: #fff;
  border: none;
  font-weight: 700;
  cursor: grab;
}

.align-btn:active {
  cursor: grabbing;
  transform: scale(0.95);
}

/* Proximity Demo */
.proximity-demo {
  width: 100%;
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.scatter-group {
  position: relative;
  width: 300px;
  height: 300px;
}

.dot {
  position: absolute;
  padding: 10px;
  background: #fff;
  border: 1px solid #000;
  font-size: 0.8rem;
  transition: all 0.5s ease;
}

.dCenter {
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  background: #000;
  color: #fff;
  z-index: 10;
  padding: 20px;
  border-radius: 50%;
  cursor: pointer;
}

.d1 { top: 10%; left: 10%; }
.d2 { top: 10%; right: 10%; }
.d3 { bottom: 10%; left: 10%; }
.d4 { bottom: 10%; right: 10%; }

.scatter-group.gathered .d1 { top: 35%; left: 35%; }
.scatter-group.gathered .d2 { top: 35%; right: 35%; }
.scatter-group.gathered .d3 { bottom: 35%; left: 35%; }
.scatter-group.gathered .d4 { bottom: 35%; right: 35%; }

.instruction {
  margin-top: 40px;
  color: #666;
  font-style: italic;
}

/* Footer */
.page-footer {
  padding: 100px 0;
  text-align: center;
  background: #000;
  color: #fff;
}

.return-home-btn {
  padding: 20px 60px;
  background: #d2e603;
  color: #000;
  border: none;
  font-size: 1.2rem;
  font-weight: 900;
  cursor: pointer;
  transition: transform 0.3s;
}

.return-home-btn:hover {
  transform: scale(1.1);
}
</style>
