<template>
  <div class="book-page" ref="pageRef" @scroll="handleScroll" @wheel="handleWheel">
    <!-- Fixed Background -->
    <div class="fixed-bg"></div>

    <!-- Hero Section -->
    <div id="hero" class="hero-wrapper">
      <div class="hero-cover" ref="heroContainerRef" @mousemove="handleHeroMouseMove" @mouseleave="resetHeroTransform">
        <div class="hero-background-grid"></div>

        <div class="hero-design-layout" :style="heroContentStyle">
          <div class="hero-contrast-section">
            <div class="hero-big-circle"></div>
            <div class="hero-small-text">contrast</div>
          </div>

          <div class="hero-repetition-section">
            <div class="hero-repeat-shape" v-for="n in 3" :key="n"></div>
            <div class="hero-small-text">repetition</div>
          </div>

          <div class="hero-alignment-section">
            <div class="hero-align-line"></div>
            <div class="hero-align-content">
              <div class="hero-align-box"></div>
              <div class="hero-align-text">alignment</div>
            </div>
          </div>

          <div class="hero-proximity-section">
            <div class="hero-prox-group">
              <div class="hero-prox-dot"></div>
              <div class="hero-prox-dot"></div>
              <div class="hero-prox-dot"></div>
            </div>
            <div class="hero-small-text">proximity</div>
          </div>
        </div>

        <div class="hero-title-overlay" :style="heroTitleStyle">
          <div class="hero-main-title-cn">写给大家看的设计书</div>
          <div class="hero-sub-title-en">The Non-Designer's Design Book</div>
        </div>
      </div>
      
      <div class="scroll-hint" :style="{ opacity: Math.max(0, 1 - scrollY / 300) }">
        <span>SCROLL TO LEARN</span>
        <div class="line"></div>
      </div>
    </div>

    <!-- Sticky Navigation -->
    <nav class="sticky-nav" :class="{ 'is-stuck': isNavStuck }">
      <button class="return-btn nav-back-btn" @click="goBackHome">
        <span>← CLOSE BOOK</span>
      </button>
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
          <div class="pattern-row" v-for="i in 6" :key="i" :class="{ 'dir-right': i % 2 === 0 }">
            <div class="pattern-track">
              <span v-for="j in 24" :key="`a-${i}-${j}`" class="pattern-item">REPEAT</span>
              <span v-for="j in 24" :key="`b-${i}-${j}`" class="pattern-item">REPEAT</span>
            </div>
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
          <h2>亲密</h2>
          <h3>Proximity</h3>
        </div>
        <div class="proximity-demo">
           <div class="business-card" :class="{ organized: proximityEnabled }">
             <div class="card-element name">Jane Doe</div>
             <div class="card-element title">Graphic Designer</div>
             <div class="card-element contact-group">
               <div class="contact-item">555-0199</div>
               <div class="contact-item">jane@design.com</div>
               <div class="contact-item">123 Art Street</div>
             </div>
           </div>
           <p class="instruction-text">
             {{ proximityEnabled ? 'Items grouped by relationship.' : 'Items scattered logically disconnected.' }}
           </p>
           <button class="toggle-btn" @click="proximityEnabled = !proximityEnabled">
             {{ proximityEnabled ? 'SCATTER' : 'GROUP RELATED ITEMS' }}
           </button>
        </div>
        
        <div class="footer-action">
          <button class="footer-btn" @click="goBackHome">DESIGN COMPLETE</button>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const pageRef = ref<HTMLElement | null>(null);
const scrollY = ref(0);
const proximityEnabled = ref(false);

const goBackHome = () => {
  router.back();
};

const isNavStuck = ref(false);
const activeSection = ref('hero');

// Interactive States
const contrastEnabled = ref(false);
const alignmentEnabled = ref(false);

const heroContainerRef = ref<HTMLElement | null>(null);
const heroMouseX = ref(0);
const heroMouseY = ref(0);

const handleHeroMouseMove = (e: MouseEvent) => {
  if (!heroContainerRef.value) return;
  const rect = heroContainerRef.value.getBoundingClientRect();
  heroMouseX.value = ((e.clientX - rect.left) / rect.width) * 2 - 1;
  heroMouseY.value = ((e.clientY - rect.top) / rect.height) * 2 - 1;
};

const resetHeroTransform = () => {
  heroMouseX.value = 0;
  heroMouseY.value = 0;
};

const heroContentStyle = computed(() => ({
  transform: `perspective(900px) rotateX(${heroMouseY.value * -6}deg) rotateY(${heroMouseX.value * 6}deg) translateZ(-20px)`
}));

const heroTitleStyle = computed(() => ({
  transform: `translate(-50%, -50%) perspective(900px) rotateX(${heroMouseY.value * -6}deg) rotateY(${heroMouseX.value * 6}deg) translateZ(60px)`
}));

const sectionOrder = ['hero', 'intro', 'contrast', 'repetition', 'alignment', 'proximity'] as const;
const isWheelLocked = ref(false);

const scrollToIndex = (index: number) => {
  if (!pageRef.value) return;
  const safeIndex = Math.min(sectionOrder.length - 1, Math.max(0, index));
  pageRef.value.scrollTo({ top: safeIndex * window.innerHeight, behavior: 'smooth' });
};

const handleWheel = (e: WheelEvent) => {
  e.preventDefault();
  if (isWheelLocked.value) return;

  const currentIndex = sectionOrder.indexOf(activeSection.value as (typeof sectionOrder)[number]);
  const direction = e.deltaY > 0 ? 1 : -1;
  const nextIndex = Math.min(sectionOrder.length - 1, Math.max(0, currentIndex + direction));
  if (nextIndex === currentIndex) return;

  isWheelLocked.value = true;
  activeSection.value = sectionOrder[nextIndex];
  scrollToIndex(nextIndex);
  window.setTimeout(() => {
    isWheelLocked.value = false;
  }, 900);
};

const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement;
  scrollY.value = target.scrollTop;
  
  // Sticky Nav Logic
  const vh = Math.max(1, window.innerHeight);
  const index = Math.round(target.scrollTop / vh);
  const safeIndex = Math.min(sectionOrder.length - 1, Math.max(0, index));
  activeSection.value = sectionOrder[safeIndex];
  isNavStuck.value = safeIndex > 0;
};

const scrollTo = (id: string) => {
  const index = sectionOrder.indexOf(id as (typeof sectionOrder)[number]);
  if (index >= 0) {
    activeSection.value = sectionOrder[index];
    scrollToIndex(index);
    return;
  }

  const el = document.getElementById(id);
  el?.scrollIntoView({ behavior: 'smooth' });
};

onMounted(() => {
  // Init observers if needed
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;700;900&display=swap');

.book-page {
  --nav-h: 70px;
  width: 100%;
  max-width: 100%;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
  scroll-behavior: smooth;
  scroll-snap-type: y mandatory;
  scroll-padding-top: var(--nav-h);
  background-color: #f5f5f7;
  color: #1d1d1f;
  font-family: 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
  position: relative;
}

.book-page,
.book-page * {
  box-sizing: border-box;
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

/* Return Buttons */
.return-btn {
  /* Position handled by nav-back-btn when inside nav */
  z-index: 2000;
  background: #000;
  border: 2px solid #fff;
  padding: 10px 20px;
  font-family: 'Helvetica Neue', sans-serif;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  color: #fff;
}

.nav-back-btn {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  margin: 0;
}

.return-btn:hover {
  background: #fff;
  color: #000;
  box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
}

.footer-action {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 60px;
}

.footer-btn {
  background: #000;
  color: #fff;
  border: 2px solid #000;
  padding: 15px 40px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.footer-btn:hover {
  background: #fff;
  color: #000;
}

/* Hero Wrapper */
.hero-wrapper {
  height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background-color: #f5f5f7;
  scroll-snap-align: start;
  scroll-snap-stop: always;
}

.hero-cover {
  position: absolute;
  inset: 0;
  overflow: hidden;
  font-family: 'Helvetica Neue', sans-serif;
  cursor: default;
  isolation: isolate;
}

.hero-background-grid {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(#ccc 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.35;
}

.hero-design-layout {
  position: absolute;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 20px;
  transition: transform 0.1s ease-out;
}

.hero-small-text {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #666;
  margin-top: 8px;
}

.hero-contrast-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 20px;
}

.hero-big-circle {
  width: 80px;
  height: 80px;
  background: #000;
  border-radius: 50%;
}

.hero-repetition-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #000;
  color: #fff;
}

.hero-repetition-section .hero-small-text {
  color: #aaa;
}

.hero-repeat-shape {
  width: 40px;
  height: 8px;
  background: #fff;
  margin: 4px 0;
}

.hero-alignment-section {
  position: relative;
  background: #e0e0e0;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hero-align-line {
  position: absolute;
  left: 32px;
  top: 20px;
  bottom: 20px;
  width: 1px;
  background: #f00;
  opacity: 0.5;
}

.hero-align-content {
  margin-left: 26px;
}

.hero-align-box {
  width: 40px;
  height: 40px;
  background: #333;
  margin-bottom: 10px;
}

.hero-align-text {
  font-size: 12px;
  font-weight: 700;
}

.hero-proximity-section {
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.hero-prox-group {
  display: flex;
  gap: 6px;
}

.hero-prox-dot {
  width: 16px;
  height: 16px;
  background: #000;
  border-radius: 50%;
}

.hero-title-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  text-align: center;
  z-index: 10;
  mix-blend-mode: difference;
  pointer-events: none;
  transition: transform 0.1s ease-out;
}

.hero-main-title-cn {
  font-weight: 900;
  font-size: 48px;
  color: #fff;
  line-height: 1.15;
  margin-bottom: 6px;
}

.hero-sub-title-en {
  font-size: 14px;
  color: #fff;
  opacity: 0.8;
  letter-spacing: 0.5px;
}

.scroll-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  z-index: 10;
  pointer-events: none;
  font-family: 'Helvetica Neue', sans-serif;
  font-weight: bold;
  font-size: 12px;
  letter-spacing: 2px;
  color: #000;
}

.scroll-hint .line {
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

/* Sticky Nav */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  background: rgba(245, 245, 247, 0.92);
  backdrop-filter: blur(20px);
  z-index: 100;
  padding: 0;
  border-bottom: 1px solid rgba(0,0,0,0.05);
  transition: all 0.3s ease;
  transform: translateY(-100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.sticky-nav.is-stuck {
  transform: translateY(0);
}

.nav-links {
  display: flex;
  justify-content: center;
  gap: 40px;
  flex-wrap: wrap;
  padding: 0 20px;
}

.nav-links a {
  text-decoration: none;
  font-weight: 700;
  font-size: 1rem;
  position: relative;
  
  /* Gradient Text Effect */
  background: linear-gradient(120deg, #333 0%, #000 50%, #d2e603 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  background-clip: text;
  color: #666; /* Hides gradient by default */
  
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transition-delay: 0.2s; /* Delay when returning to inactive */
}

.nav-links a.active, .nav-links a:hover {
  color: transparent; /* Reveals gradient */
  background-position: 100% center;
  transition-delay: 0s; /* Immediate when becoming active */
}

.nav-links a.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #000, #d2e603);
}

/* Content Sections */
.content-wrapper {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.content-section {
  height: 100vh;
  padding: clamp(20px, 4vh, 56px) 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  scroll-snap-align: start;
  scroll-snap-stop: always;
  overflow: hidden;
}

.section-header {
  text-align: center;
  margin-bottom: clamp(18px, 4vh, 60px);
}

.section-header h2 {
  font-size: clamp(2.2rem, 6vw, 4rem);
  font-weight: 900;
  margin: 0;
  line-height: 1;
}

.section-header h3 {
  font-size: clamp(0.9rem, 2vw, 1.2rem);
  font-weight: 300;
  text-transform: uppercase;
  letter-spacing: 0.2em;
  margin: 10px 0 0;
  color: #666;
}

.text-block {
  max-width: min(600px, 90vw);
  text-align: center;
  font-size: clamp(1rem, 2.2vw, 1.2rem);
  line-height: 1.8;
}

.chinese-text {
  font-weight: 700;
  margin-top: 12px;
}

/* Contrast Demo */
.contrast-demo {
  perspective: 1000px;
}

.demo-card {
  width: 500px;
  max-width: 100%;
  padding: clamp(20px, 4vw, 40px);
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
  margin-top: 20px;
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
  width: 100%;
  overflow: hidden;
  max-width: 1200px;
  margin: 18px auto 10px;
  transform: translateY(-10px);
}

.pattern-row {
  width: 100%;
  overflow: hidden;
  font-size: clamp(2.2rem, 6vw, 4rem);
  font-weight: 900;
  color: rgba(0,0,0,0.05);
  line-height: 1;
}

.pattern-track {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
  width: max-content;
  animation: repeatMarqueeLeft 16s linear infinite;
}

.pattern-row.dir-right .pattern-track {
  animation-name: repeatMarqueeRight;
}

.pattern-item {
  margin-right: clamp(16px, 3vw, 40px);
}

.repetition-section .text-block {
  transform: translateY(-8px);
}

@keyframes repeatMarqueeLeft {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

@keyframes repeatMarqueeRight {
  0% { transform: translateX(-50%); }
  100% { transform: translateX(0); }
}

/* Alignment Demo */
.alignment-demo {
  position: relative;
  width: 500px;
  max-width: 100%;
  height: min(400px, 45vh);
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
  bottom: 16px;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.business-card {
  width: 400px;
  max-width: 100%;
  height: 250px;
  background: #fff;
  border: 1px solid #ddd;
  position: relative;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  transition: all 0.5s ease;
  overflow: hidden;
}

.card-element {
  position: absolute;
  transition: all 0.8s cubic-bezier(0.25, 0.8, 0.25, 1);
  font-family: var(--font-ui);
}

.name {
  font-size: 1.5rem;
  font-weight: 900;
  top: 20px; left: 20px;
}

.title {
  font-size: 1rem;
  color: #666;
  bottom: 20px; left: 20px;
}

.contact-group {
  top: 20px; right: 20px;
  text-align: right;
  font-size: 0.9rem;
  color: #444;
}

/* Scattered State (Default) */
.business-card:not(.organized) .name {
  top: 10%; left: 10%;
}
.business-card:not(.organized) .title {
  bottom: 80%; left: 60%;
}
.business-card:not(.organized) .contact-group {
  top: 70%; right: 50%;
  text-align: left;
}

/* Organized State */
.business-card.organized .name {
  top: 40px; left: 40px;
}
.business-card.organized .title {
  top: 75px; left: 40px; /* Grouped with name */
  bottom: auto;
}
.business-card.organized .contact-group {
  bottom: 40px; right: 40px; /* Grouped at bottom right */
  top: auto;
  text-align: right;
}

.instruction-text {
  font-family: var(--font-text);
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
