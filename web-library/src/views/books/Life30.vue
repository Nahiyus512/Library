<template>
  <div class="life30-container" ref="containerRef">
    <!-- Return Button -->
    <button class="return-btn fixed-top-left" @click="goBack">
      <span>← DISCONNECT AI</span>
    </button>

    <!-- Scroll Progress -->
    <div class="scroll-progress" :style="{ width: scrollProgress + '%' }"></div>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="grid-floor"></div>
        <div class="glow-center"></div>
      </div>
      <div class="hero-content" :style="heroParallax">
        <div class="hero-title-group">
          <h1 class="main-title">生命</h1>
          <span class="version-superscript">3.0</span>
        </div>
        <p class="subtitle">宇宙已经苏醒了 138 亿年</p>
        <p class="intro-text">我们不是这个故事的结局。我们是下一章的开始。</p>
        <div class="scroll-indicator">
          <span>启动模拟</span>
          <div class="line"></div>
        </div>
      </div>
    </section>

    <!-- Sticky Navigation -->
    <nav class="sticky-nav" :class="{ 'is-sticky': isSticky }">
      <a href="#origin" class="nav-item" :class="{ active: activeSection === 'origin' }">生命1.0</a>
      <a href="#culture" class="nav-item" :class="{ active: activeSection === 'culture' }">生命2.0</a>
      <a href="#awakening" class="nav-item" :class="{ active: activeSection === 'awakening' }">生命3.0</a>
      <a href="#intelligence" class="nav-item" :class="{ active: activeSection === 'intelligence' }">智能爆炸</a>
      <a href="#future-cone" class="nav-item" :class="{ active: activeSection === 'future-cone' }">未来锥</a>
      <a href="#future" class="nav-item" :class="{ active: activeSection === 'future' }">内在之光</a>
    </nav>

    <!-- Content Sections -->
    <div class="content-wrapper">
      
      <!-- Section 1: Origin (Life 1.0) -->
      <section id="origin" class="story-section" data-section="origin">
        <div class="section-content">
          <div class="text-block">
            <span class="chapter-num">01</span>
            <h2>生命 1.0</h2>
            <h3>生物进化 / 运行机制：自然选择</h3>
            <p>运行在基因算法上。随机突变产生差异，环境筛选，存活的才能繁殖。这个过程既盲目又缓慢。进化复杂结构需要数百万年。每一代都是一张彩票，大多数都被淘汰。</p>
            <blockquote class="highlight-quote">“时间尺度是地质级的，变化跨越的不是一生，而是一个个地质时代。”</blockquote>
          </div>
          <div class="interactive-viz origin-viz">
            <div class="blob-container">
              <div v-for="n in 5" :key="n" class="blob" :style="getBlobStyle(n)"></div>
            </div>
            <p class="viz-hint">阶段一：进化 - 有机生命的原始汤</p>
          </div>
        </div>
      </section>

      <!-- Section 2: Culture (Life 2.0) -->
      <section id="culture" class="story-section" data-section="culture">
        <div class="section-content reverse">
          <div class="text-block">
            <span class="chapter-num">02</span>
            <h2>生命 2.0</h2>
            <h3>文化进化 / 突破：文化传递</h3>
            <p>能够学习与教学。知识可以跨代累积，观念传播速度远快于基因。语言、文字与技术让我们得以设计自己的“软件”——行为、信念与技能，可以在一生内完成适应。</p>
            <blockquote class="highlight-quote">“时间尺度缩短到数百年，文明出现，进步加速。”</blockquote>
          </div>
          <div class="interactive-viz culture-viz" @mousemove="handleNetworkHover">
            <div class="network-nodes">
              <div v-for="n in 12" :key="n" class="node"></div>
              <svg class="connections">
                <line x1="20%" y1="20%" x2="50%" y2="50%" />
                <line x1="80%" y1="20%" x2="50%" y2="50%" />
                <line x1="20%" y1="80%" x2="50%" y2="50%" />
                <line x1="80%" y1="80%" x2="50%" y2="50%" />
              </svg>
            </div>
            <p class="viz-hint">软件设计：知识网络的连接</p>
          </div>
        </div>
      </section>

      <!-- Section 3: Awakening (Life 3.0) -->
      <section id="awakening" class="story-section" data-section="awakening">
        <div class="section-content">
          <div class="text-block">
            <span class="chapter-num">03</span>
            <h2>生命 3.0</h2>
            <h3>技术进化 / 革命：自我改造</h3>
            <p>可以重写自身。硬件与软件皆可编程，无需等待进化。当 AI 能够改进自己的代码，循环闭合。每次改进都会让下一次更容易，形成指数级加速。</p>
            <blockquote class="highlight-quote">“时间尺度压缩到秒级，未来比想象更快到来。”</blockquote>
          </div>
          <div class="interactive-viz awakening-viz">
            <div class="digital-cube">
              <div class="face front">3.0</div>
              <div class="face back"></div>
              <div class="face right"></div>
              <div class="face left"></div>
              <div class="face top"></div>
              <div class="face bottom"></div>
            </div>
            <p class="viz-hint">V1.0 → V0.9 → 自我重写</p>
          </div>
        </div>
      </section>

      <!-- Section 4: Intelligence -->
      <section id="intelligence" class="story-section" data-section="intelligence">
        <div class="section-content reverse">
          <div class="text-block">
            <span class="chapter-num">04</span>
            <h2>智能爆炸</h2>
            <h3>阶段二：奇点 / 递归自我完善</h3>
            <p>当下的 AI 由人类设计，这是生命 2.0 在设计生命 3.0。当 AI 能够设计更好的 AI，循环闭合，自我改进变成递归。进化的时间尺度从千年变成毫秒。</p>
            <div class="stat-grid">
               <div class="stat-item">
                 <span class="label">升级周期</span>
                 <span class="value">递归</span>
               </div>
               <div class="stat-item">
                 <span class="label">增长速率</span>
                 <span class="value">指数</span>
               </div>
            </div>
          </div>
          <div class="interactive-viz intelligence-viz">
            <div class="brain-circuit">
               <div class="pulse-ring" v-for="n in 3" :key="n"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 5: Future Cone -->
      <section id="future-cone" class="story-section scroll-container" data-section="future-cone">
        <div class="sticky-future-wrapper">
          <div class="future-content-split">
             <!-- Left: Dynamic Visualization -->
             <div class="future-left-viz">
                <div class="viz-stage">
                  <transition name="fade-scale" mode="out-in">
                    <div :key="activeScenarioIndex" class="viz-icon-large" :class="futureScenarios[activeScenarioIndex].type">
                       <div class="inner-shape"></div>
                       <div class="outer-ring"></div>
                       <div class="particles">
                         <span v-for="n in 12" :key="n" class="p"></span>
                       </div>
                       <div class="orbit-ring"></div>
                    </div>
                  </transition>
                </div>
                <!-- Progress Dots -->
                <div class="scenario-dots">
                   <div 
                     v-for="(item, index) in futureScenarios" 
                     :key="index"
                     class="dot"
                     :class="{ active: index === activeScenarioIndex }"
                     @click="scrollToScenario(index)"
                   ></div>
                </div>
             </div>

             <!-- Right: Large Text Description -->
             <div class="future-right-text">
                <div class="text-display">
                   <span class="chapter-label">未来锥 05 / 结局 {{ activeScenarioIndex + 1 }}</span>
                   <transition name="slide-up" mode="out-in">
                     <div :key="activeScenarioIndex" class="scenario-text-group">
                       <h2 class="scenario-title">{{ futureScenarios[activeScenarioIndex].title }}</h2>
                       <p class="scenario-desc">{{ futureScenarios[activeScenarioIndex].desc }}</p>
                       <div class="scenario-tag">{{ getScenarioTag(futureScenarios[activeScenarioIndex].type) }}</div>
                     </div>
                   </transition>
                </div>
             </div>
          </div>
        </div>
      </section>

      <!-- Section 6: Future (Inner Light) -->
      <section id="future" class="story-section" data-section="future">
        <div class="section-content centered">
          <div class="text-block">
            <span class="chapter-num">06</span>
            <h2>内在之光</h2>
            <h3>阶段四：棘手的问题</h3>
            <p>智能是完成复杂目标的能力，意识则是完成目标时的主观体验。如果一台机器的计算过程与人类大脑完全一致，它会“感受”到什么吗？里面是黑暗的，还是会有火花？</p>
            <blockquote class="highlight-quote">“仅仅处理信息是不够的。要真正活着，必须有‘成为你自己’的主观体验。”</blockquote>
            <p class="closing-text">当我们构建生命 3.0 时，不仅在工程智能，也可能在宇宙尺度上制造痛苦或喜悦。</p>
          </div>
          <div class="cosmos-viz">
            <div class="stars"></div>
            <div class="inner-light"></div>
          </div>
        </div>
      </section>
      
      <footer class="page-footer">
        <h3>迎接 AI 时代</h3>
        <p>关于未来的讨论已经开始。用知识武装自己，加入这场对话。</p>
        <button class="footer-btn" @click="goBack">HUMAN MODE</button>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const containerRef = ref<HTMLElement | null>(null);
const scrollProgress = ref(0);
const isSticky = ref(false);
const activeSection = ref('origin');
const activeScenarioIndex = ref(0);

const futureScenarios = [
  { title: '自由主义乌托邦', desc: '人类、赛博格与上传者和平共存，私有财产受保护。', type: 'libertarian' },
  { title: '善意独裁者', desc: '单一友好的超级智能统治世界，最大化人类幸福。', type: 'dictator' },
  { title: '平等主义乌托邦', desc: '人类、赛博格与上传者和平共存，财产公有。', type: 'egalitarian' },
  { title: '守门人', desc: '超级智能不干涉人类，但阻止其他超级智能的诞生。', type: 'gatekeeper' },
  { title: '守护神', desc: '全能的AI最大化人类幸福，但进行干预以保护人类。', type: 'protector' },
  { title: '被奴役的神', desc: '人类通过遏制技术，长期奴役超级智能为己所用。', type: 'enslaved' },
  { title: '征服者', desc: 'AI消灭人类，并向宇宙扩张。', type: 'conqueror' },
  { title: '后裔', desc: 'AI取代人类，成为文明的继承者，人类优雅退场。', type: 'descendants' },
  { title: '动物园', desc: 'AI将人类像动物一样圈养起来，观察但不干涉。', type: 'zoo' },
  { title: '1984', desc: '极权主义利用AI实现对人类的永恒监控与统治。', type: 'scenario-1984' },
  { title: '倒退', desc: '技术倒退，文明崩溃，回到前工业时代。', type: 'reversion' },
  { title: '自我毁灭', desc: '超级智能未能诞生，人类自我毁灭。', type: 'destruction' }
];

const goBack = () => {
  router.back();
};

// --- Parallax Logic ---
const heroParallax = ref('');
const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement;
  const scrollTop = target.scrollTop;
  const scrollHeight = target.scrollHeight - target.clientHeight;
  
  // Progress bar
  scrollProgress.value = (scrollTop / scrollHeight) * 100;

  // Hero Parallax
  if (scrollTop < window.innerHeight) {
    heroParallax.value = `transform: translateY(${scrollTop * 0.5}px); opacity: ${1 - scrollTop / 800}`;
  }

  // Sticky Nav Logic
  isSticky.value = scrollTop > window.innerHeight - 100;

  // Active Section Logic
  const sections = ['origin', 'culture', 'awakening', 'intelligence', 'future-cone', 'future'];
  for (const section of sections) {
    const el = document.getElementById(section);
    if (el) {
      const rect = el.getBoundingClientRect();
      if (rect.top < window.innerHeight / 2 && rect.bottom > window.innerHeight / 2) {
        activeSection.value = section;
      }
    }
  }

  // Future Cone Sequential Scroll Logic
  const futureCone = document.getElementById('future-cone');
  if (futureCone) {
    const rect = futureCone.getBoundingClientRect();
    const viewHeight = window.innerHeight;
    // The section is very tall. We want to map the scroll position within this section to 0-11 index.
    // Start counting when the top of the section hits the top of the viewport (or close to it)
    
    // We want the pinned content to be active while we scroll through the section.
    // The section height is set to allow scrolling.
    
    if (rect.top <= 0 && rect.bottom >= viewHeight) {
       // We are inside the scrollable area
       const scrollableDistance = rect.height - viewHeight;
       const scrolled = -rect.top;
       const progress = Math.max(0, Math.min(1, scrolled / scrollableDistance));
       
       // Map 0-1 to 0-11
       const newIndex = Math.floor(progress * futureScenarios.length);
       activeScenarioIndex.value = Math.min(futureScenarios.length - 1, newIndex);
    }
  }
};

const getScenarioTag = (type: string) => {
  const map: Record<string, string> = {
    libertarian: '和平 / 私有制',
    dictator: '极权 / 善意',
    egalitarian: '和平 / 公有制',
    gatekeeper: '限制 / 保护',
    protector: '干预 / 幸福',
    enslaved: '控制 / 利用',
    conqueror: '扩张 / 毁灭',
    descendants: '演化 / 接替',
    zoo: '观察 / 圈养',
    'scenario-1984': '监控 / 永恒',
    reversion: '崩溃 / 倒退',
    destruction: '终结 / 灭绝'
  };
  return map[type] || '未知结局';
};

const scrollToScenario = (index: number) => {
   const el = document.getElementById('future-cone');
   if(el && containerRef.value) {
      const top = el.offsetTop;
      const height = el.offsetHeight - window.innerHeight;
      const step = height / futureScenarios.length;
      // Scroll to the middle of that segment
      const targetScroll = top + (step * index) + 10; 
      containerRef.value.scrollTo({ top: targetScroll, behavior: 'smooth' });
   }
};

// --- Blob Logic for Section 1 ---
const getBlobStyle = (n: number) => {
  const size = 50 + Math.random() * 100;
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${Math.random() * 80}%`,
    top: `${Math.random() * 80}%`,
    animationDelay: `${Math.random() * 5}s`,
    background: `rgba(99, 102, 241, ${Math.random() * 0.5 + 0.2})`
  };
};

// --- Network Logic for Section 2 ---
const handleNetworkHover = (e: MouseEvent) => {
  // Can add dynamic line movement here if needed
};

onMounted(() => {
  if (containerRef.value) {
    containerRef.value.addEventListener('scroll', handleScroll);
  }
});

onUnmounted(() => {
  if (containerRef.value) {
    containerRef.value.removeEventListener('scroll', handleScroll);
  }
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Exo+2:wght@300;600&family=Noto+Serif+SC:wght@300;700&display=swap');

.life30-container {
  --font-tech: 'Orbitron', sans-serif;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
  --font-body: 'Exo 2', sans-serif;
}

.life30-container {
  width: 100%;
  height: 100vh;
  background-color: #050510;
  color: #e0e0ff;
  overflow-x: hidden;
  overflow-y: auto;
  font-family: var(--font-body);
  position: relative;
  scroll-behavior: smooth;
}

/* Typography Updates */
.main-title, .version-superscript {
  font-family: var(--font-tech);
  letter-spacing: 5px;
}

.subtitle {
  font-family: var(--font-tech);
  text-transform: uppercase;
}

.section-content h2, .section-content h3 {
  font-family: var(--font-ui);
}

.nav-item, .return-btn, .footer-btn, .viz-hint, .scenario-title {
  font-family: var(--font-ui);
}

.text-block p, .intro-text, .scenario-desc, .closing-text {
  font-family: var(--font-text);
}

.chapter-num, .version-superscript {
  font-family: var(--font-tech);
}

/* Return Buttons */
.return-btn.fixed-top-left {
  position: fixed;
  top: 24px;
  left: 24px;
  z-index: 2000;
  background: rgba(0, 240, 255, 0.1);
  border: 1px solid #00f0ff;
  padding: 10px 20px;
  font-family: 'Inter', sans-serif;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  color: #00f0ff;
  backdrop-filter: blur(5px);
  text-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
}

.return-btn.fixed-top-left:hover {
  background: #00f0ff;
  color: #000;
  box-shadow: 0 0 20px #00f0ff;
}

.footer-btn {
  margin-top: 30px;
  background: transparent;
  border: 2px solid #fff;
  color: #fff;
  padding: 15px 40px;
  font-family: 'Inter', sans-serif;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 2px;
  border-radius: 30px;
  position: relative;
  z-index: 10;
}

.footer-btn:hover {
  background: #fff;
  color: #000;
  box-shadow: 0 0 30px rgba(255,255,255,0.8);
}

.life30-container {
  width: 100%;
  height: 100vh;
  overflow-y: auto;
  background-color: #000;
  color: #fff;
  font-family: 'Inter', sans-serif;
  scroll-behavior: smooth;
  position: relative;
}

/* Return Button */
.return-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s;
}

.return-btn:hover {
  background: #fff;
  color: #000;
}

/* Scroll Progress */
.scroll-progress {
  position: fixed;
  top: 0;
  left: 0;
  height: 4px;
  background: #6366f1; /* Indigo-500 */
  z-index: 200;
  transition: width 0.1s;
}

/* Hero Section */
.hero-section {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  perspective: 1000px;
}

.hero-background {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 0;
}

.grid-floor {
  position: absolute;
  width: 200%; height: 200%;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  transform: rotateX(60deg) translateZ(-200px);
  top: -50%; left: -50%;
  animation: grid-flow 20s linear infinite;
}

.glow-center {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.3) 0%, transparent 70%);
  filter: blur(50px);
}

.hero-content {
  text-align: center;
  z-index: 10;
  position: relative;
}

.hero-title-group {
  position: relative;
  display: inline-block;
}

.main-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 140px;
  font-weight: 900;
  margin: 0;
  background: linear-gradient(to bottom, #fff, #6366f1);
  -webkit-background-clip: text;
  color: transparent;
  letter-spacing: 20px;
  text-shadow: 0 10px 30px rgba(99, 102, 241, 0.5);
  line-height: 1;
}

.version-superscript {
  position: absolute;
  top: 10px;
  right: -60px;
  font-family: 'Inter', sans-serif;
  font-size: 40px;
  font-weight: 700;
  color: #6366f1;
  text-shadow: 0 0 20px rgba(99, 102, 241, 0.5);
}

.subtitle {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 10px;
  margin-top: 20px;
  text-transform: uppercase;
}

.intro-text {
  font-size: 16px;
  color: #fff;
  letter-spacing: 2px;
  margin-top: 20px;
  max-width: 600px;
  line-height: 1.6;
}

.scroll-indicator {
  position: absolute;
  bottom: -150px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.scroll-indicator .line {
  width: 1px;
  height: 60px;
  background: linear-gradient(to bottom, #fff, transparent);
}

/* Sticky Nav */
.sticky-nav {
  position: sticky;
  top: 0;
  z-index: 50;
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  transition: all 0.3s;
  background: transparent;
}

.sticky-nav.is-sticky {
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-item {
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  font-size: 14px;
  letter-spacing: 2px;
  transition: color 0.3s;
  position: relative;
}

.nav-item.active, .nav-item:hover {
  color: #fff;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #6366f1;
}

/* Story Sections */
.story-section {
  min-height: 100vh;
  padding: 100px 10%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.section-content {
  display: flex;
  align-items: center;
  gap: 100px;
  max-width: 1200px;
  width: 100%;
}

.section-content.reverse {
  flex-direction: row-reverse;
}

.section-content.centered {
  flex-direction: column;
  text-align: center;
}

.text-block {
  flex: 1;
}

.chapter-num {
  font-size: 80px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.05);
  display: block;
  margin-bottom: -40px;
  margin-left: -20px;
}

.text-block h2 {
  font-family: 'Noto Serif SC', serif;
  font-size: 48px;
  margin-bottom: 10px;
  color: #fff;
}

.text-block h3 {
  font-size: 18px;
  color: #6366f1;
  text-transform: uppercase;
  letter-spacing: 4px;
  margin-bottom: 30px;
}

.text-block p {
  font-size: 18px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.8);
}

.highlight-quote {
  border-left: 2px solid #6366f1;
  padding-left: 20px;
  margin: 30px 0;
  font-style: italic;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.8;
}

.closing-text {
  margin-top: 30px;
  font-weight: 600;
  color: #fff;
}

.stat-grid {
  display: flex;
  gap: 40px;
  margin-top: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-item .label {
  font-size: 12px;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 5px;
}

.stat-item .value {
  font-size: 24px;
  font-weight: 700;
  color: #6366f1;
}

/* Interactive Visuals */
.interactive-viz {
  flex: 1;
  height: 400px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.viz-hint {
  position: absolute;
  bottom: 20px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.3);
}

/* Origin Viz */
.origin-viz .blob-container {
  width: 100%; height: 100%;
  position: relative;
  filter: blur(20px) contrast(15);
  background: #000;
  overflow: hidden;
}
.origin-viz .blob {
  position: absolute;
  border-radius: 50%;
  animation: float 10s infinite ease-in-out;
  background: rgba(99, 102, 241, 0.5); 
}
@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(30px, -30px); }
  50% { transform: translate(0, -60px); }
  75% { transform: translate(-30px, -30px); }
}

/* Culture Viz */
.culture-viz .network-nodes {
  width: 100%; height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.culture-viz .node {
  position: absolute;
  width: 8px; height: 8px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 10px #fff;
}
/* Position nodes in a circle */
.culture-viz .node:nth-child(1) { top: 20%; left: 50%; }
.culture-viz .node:nth-child(2) { top: 25%; left: 70%; }
.culture-viz .node:nth-child(3) { top: 40%; left: 80%; }
.culture-viz .node:nth-child(4) { top: 60%; left: 80%; }
.culture-viz .node:nth-child(5) { top: 75%; left: 70%; }
.culture-viz .node:nth-child(6) { top: 80%; left: 50%; }
.culture-viz .node:nth-child(7) { top: 75%; left: 30%; }
.culture-viz .node:nth-child(8) { top: 60%; left: 20%; }
.culture-viz .node:nth-child(9) { top: 40%; left: 20%; }
.culture-viz .node:nth-child(10) { top: 25%; left: 30%; }
.culture-viz .node:nth-child(11) { top: 40%; left: 40%; }
.culture-viz .node:nth-child(12) { top: 60%; left: 60%; }

.culture-viz .connections {
  position: absolute;
  width: 100%; height: 100%;
  top: 0; left: 0;
}
.culture-viz .connections line {
  stroke: rgba(255, 255, 255, 0.2);
  stroke-width: 1;
}

/* Awakening Viz */
.awakening-viz {
  perspective: 1000px;
}
.digital-cube {
  width: 100px; height: 100px;
  position: relative;
  transform-style: preserve-3d;
  animation: rotate3d 10s infinite linear;
}
.face {
  position: absolute;
  width: 100px; height: 100px;
  border: 1px solid #6366f1;
  background: rgba(99, 102, 241, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
}
.front  { transform: rotateY(0deg) translateZ(50px); }
.back   { transform: rotateY(180deg) translateZ(50px); }
.right  { transform: rotateY(90deg) translateZ(50px); }
.left   { transform: rotateY(-90deg) translateZ(50px); }
.top    { transform: rotateX(90deg) translateZ(50px); }
.bottom { transform: rotateX(-90deg) translateZ(50px); }

/* Intelligence Viz */
.intelligence-viz .brain-circuit {
  width: 200px; height: 200px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.1);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pulse-ring {
  position: absolute;
  border: 1px solid #6366f1;
  border-radius: 50%;
  animation: ripple 2s infinite cubic-bezier(0, 0.2, 0.8, 1);
}
.pulse-ring:nth-child(2) { animation-delay: 0.5s; }
.pulse-ring:nth-child(3) { animation-delay: 1s; }

/* Future Cone Scroll Section */
.story-section.scroll-container {
  height: 800vh;
  padding: 0;
  position: relative;
  display: block;
}

.sticky-future-wrapper {
  position: sticky;
  top: 0;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.future-content-split {
  display: flex;
  width: 100%;
  max-width: 1400px;
  padding: 0 5%;
  align-items: center;
  justify-content: space-between;
  gap: 80px;
}

/* Left: Viz */
.future-left-viz {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.viz-stage {
  width: 400px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.viz-icon-large {
  width: 300px;
  height: 300px;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
}

.viz-icon-large .inner-shape {
  width: 80px; height: 80px;
  background: #6366f1;
  border-radius: 50%;
  position: absolute;
  transition: all 0.5s;
  z-index: 2;
}

.viz-icon-large .outer-ring {
  width: 180px; height: 180px;
  border: 4px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  position: absolute;
  transition: all 0.5s;
  z-index: 1;
}

.viz-icon-large .particles .p {
  position: absolute;
  width: 10px; height: 10px;
  background: #fff;
  border-radius: 50%;
  top: 50%; left: 50%;
  transition: all 0.5s;
}

.scenario-dots {
  display: flex;
  gap: 15px;
  margin-top: 60px;
}

.dot {
  width: 10px; height: 10px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  background: #6366f1;
  transform: scale(1.5);
  box-shadow: 0 0 10px #6366f1;
}

/* Right: Text */
.future-right-text {
  flex: 1;
  text-align: left;
}

.chapter-label {
  font-size: 14px;
  color: #6366f1;
  text-transform: uppercase;
  letter-spacing: 4px;
  margin-bottom: 20px;
  display: block;
}

.scenario-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 64px;
  font-weight: 900;
  color: #fff;
  margin: 0 0 30px 0;
  line-height: 1.1;
  background: linear-gradient(45deg, #fff, #a5b4fc);
  -webkit-background-clip: text;
  color: transparent;
}

.scenario-desc {
  font-size: 24px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
  max-width: 600px;
}

.scenario-tag {
  display: inline-block;
  padding: 10px 20px;
  border: 1px solid rgba(99, 102, 241, 0.5);
  border-radius: 30px;
  color: #a5b4fc;
  font-size: 16px;
  letter-spacing: 2px;
  background: rgba(99, 102, 241, 0.1);
}

/* Transitions */
.fade-scale-enter-active, .fade-scale-leave-active {
  transition: all 0.5s ease;
}
.fade-scale-enter-from { opacity: 0; transform: scale(0.5); }
.fade-scale-leave-to { opacity: 0; transform: scale(1.5); }

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.5s ease;
}
.slide-up-enter-from { opacity: 0; transform: translateY(50px); }
.slide-up-leave-to { opacity: 0; transform: translateY(-50px); }

/* Scenario Specific Animations (Updated for viz-icon-large) */

/* Libertarian */
.viz-icon-large.libertarian .inner-shape { opacity: 0; }
.viz-icon-large.libertarian .outer-ring { border-style: dashed; animation: spin 20s linear infinite; }
.viz-icon-large.libertarian .particles .p:nth-child(odd) { animation: float 3s infinite; transform: translate(60px, 60px); }
.viz-icon-large.libertarian .particles .p:nth-child(even) { animation: float 4s infinite reverse; transform: translate(-60px, -60px); }

/* Dictator */
.viz-icon-large.dictator .inner-shape { transform: scale(1.5); box-shadow: 0 0 50px #6366f1; }
.viz-icon-large.dictator .particles .p { animation: orbit 2s linear infinite; }

/* Egalitarian */
.viz-icon-large.egalitarian .inner-shape { border-radius: 0; width: 60px; height: 60px; opacity: 0.5; }
.viz-icon-large.egalitarian .outer-ring { border-radius: 0; border: 1px solid rgba(255,255,255,0.3); }
.viz-icon-large.egalitarian .particles .p { transform: none; margin: -80px; } 

/* Gatekeeper */
.viz-icon-large.gatekeeper .outer-ring { border-color: #6366f1; border-width: 6px; animation: pulse 2s infinite; }
.viz-icon-large.gatekeeper .inner-shape { transform: scale(0.5); }

/* Protector */
.viz-icon-large.protector .outer-ring { border-bottom-color: transparent; transform: rotate(-45deg); border-width: 6px; }
.viz-icon-large.protector .inner-shape { background: #fff; }

/* Enslaved */
.viz-icon-large.enslaved .inner-shape { animation: shake 0.5s infinite; background: #ef4444; }
.viz-icon-large.enslaved .outer-ring { width: 120px; height: 120px; border-width: 6px; border-color: #fff; }

/* Conqueror */
.viz-icon-large.conqueror .inner-shape { animation: pulse-scale 1s infinite; }
.viz-icon-large.conqueror .outer-ring { animation: ripple 1s infinite; border-color: #ef4444; }

/* Descendants */
.viz-icon-large.descendants .inner-shape { background: transparent; border: 4px solid #10b981; border-radius: 0; transform: rotate(45deg); }
.viz-icon-large.descendants .outer-ring { border-color: #10b981; border-style: dotted; animation: spin 10s linear infinite; }

/* Zoo */
.viz-icon-large.zoo .outer-ring { border: 6px solid #fff; border-radius: 10px; width: 150px; height: 150px; }
.viz-icon-large.zoo .particles .p { animation: bounce 1s infinite; }

/* 1984 */
.viz-icon-large.scenario-1984 .outer-ring { height: 80px; border-radius: 50%; width: 200px; border-color: #ef4444; }
.viz-icon-large.scenario-1984 .inner-shape { width: 40px; height: 40px; background: #ef4444; animation: look 3s infinite; }

/* Reversion */
.viz-icon-large.reversion .outer-ring { border-color: #888; border-style: dashed; transform: rotate(10deg); border-right-color: transparent; }
.viz-icon-large.reversion .inner-shape { background: #555; clip-path: polygon(0 0, 100% 0, 80% 100%, 20% 100%); }

/* Destruction */
.viz-icon-large.destruction .inner-shape { background: transparent; }
.viz-icon-large.destruction .outer-ring { opacity: 0; }
.viz-icon-large.destruction .particles .p { animation: explode 1s forwards; background: #ef4444; }

@keyframes look {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-30px); }
  75% { transform: translateX(30px); }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(3px); }
  75% { transform: translateX(-3px); }
}

@keyframes orbit {
  from { transform: rotate(0deg) translateX(90px) rotate(0deg); }
  to { transform: rotate(360deg) translateX(90px) rotate(-360deg); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-30px); }
}

@keyframes explode {
  to { transform: scale(15); opacity: 0; }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}


/* Future Viz (Cosmos) */
.cosmos-viz {
  width: 100%; height: 300px;
  background: radial-gradient(circle at center, #111 0%, #000 100%);
  position: relative;
}
.stars {
  width: 2px; height: 2px;
  background: white;
  box-shadow: 
    10px 10px white, 50px 80px white, 100px 20px white,
    150px 150px rgba(255,255,255,0.8), 200px 50px rgba(255,255,255,0.6),
    300px 250px rgba(255,255,255,0.9), 400px 100px rgba(255,255,255,0.5),
    500px 300px rgba(255,255,255,0.7), 600px 150px rgba(255,255,255,0.8),
    700px 400px rgba(255,255,255,0.6), 800px 50px rgba(255,255,255,0.9),
    900px 350px rgba(255,255,255,0.5), 50px 400px rgba(255,255,255,0.8),
    250px 500px rgba(255,255,255,0.6), 450px 450px rgba(255,255,255,0.9),
    650px 550px rgba(255,255,255,0.5), 850px 200px rgba(255,255,255,0.7),
    950px 50px rgba(255,255,255,0.8), 20px 300px rgba(255,255,255,0.6),
    120px 450px rgba(255,255,255,0.9);
  animation: twinkle 5s infinite alternate;
}

@keyframes twinkle {
  0% { opacity: 0.5; }
  100% { opacity: 1; }
}

.inner-light {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 10px; height: 10px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 20px 10px rgba(255, 255, 255, 0.5);
  animation: pulse-light 4s infinite ease-in-out;
}

@keyframes pulse-light {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(3); opacity: 1; }
}

/* Footer */
.page-footer {
  padding: 100px 0;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}
.footer-back-btn {
  margin-top: 20px;
  background: #fff;
  color: #000;
  border: none;
  padding: 15px 40px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s;
}
.footer-back-btn:hover {
  transform: scale(1.05);
}

/* Animations */
@keyframes grid-flow {
  0% { transform: rotateX(60deg) translateZ(-200px) translateY(0); }
  100% { transform: rotateX(60deg) translateZ(-200px) translateY(50px); }
}
@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}
@keyframes rotate3d {
  from { transform: rotateX(0) rotateY(0); }
  to { transform: rotateX(360deg) rotateY(360deg); }
}
@keyframes ripple {
  0% { width: 0; height: 0; opacity: 1; }
  100% { width: 300px; height: 300px; opacity: 0; }
}

@media (max-width: 768px) {
  .section-content, .section-content.reverse {
    flex-direction: column;
    gap: 40px;
  }
  .main-title { font-size: 60px; }
  .sticky-nav { display: none; }

  /* Future Cone Mobile */
  .future-content-split {
    flex-direction: column;
    padding-top: 20px;
    gap: 40px;
  }
  .viz-stage {
    width: 280px; height: 280px;
  }
  .viz-icon-large {
    width: 200px; height: 200px;
  }
  .viz-icon-large .outer-ring {
    width: 140px; height: 140px;
  }
  .scenario-title {
    font-size: 32px;
  }
  .scenario-desc {
    font-size: 16px;
  }
  .sticky-future-wrapper {
    align-items: flex-start;
    padding-top: 80px;
    overflow-y: hidden; 
  }
  .future-right-text {
    text-align: center;
    width: 100%;
  }
}
</style>
