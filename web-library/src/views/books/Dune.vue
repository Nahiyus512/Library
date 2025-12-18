<template>
  <div class="dune-page" ref="pageRef" @mousemove="handleMouseMove">
    <!-- Sticky Navigation -->
    <nav class="sticky-nav" :class="{ 'visible': showNav }">
      <button class="nav-return-btn" @click="goBackHome">
        <span>← LEAVE ARRAKIS</span>
      </button>
      <div class="nav-links">
        <a
          v-for="(section, index) in sections"
          :key="index"
          :href="`#${section.id}`"
          @click.prevent="scrollToSection(section.id)"
        >
          {{ section.name }}
        </a>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-background" :style="heroStyle">
        <div class="sun"></div>
        <div class="dune-layer layer-1"></div>
        <div class="dune-layer layer-2"></div>
        <div class="dune-layer layer-3"></div>
      </div>
      <div class="hero-content">
        <div class="data-stream">DATA_STREAM_1 PROCESSING...</div>
        <h1 class="hero-title" :style="titleStyle">沙丘</h1>
        <p class="hero-subtitle">弗兰克·赫伯特</p>
        <p class="litany">"不要恐惧。恐惧是思维的杀手。<br>恐惧是带来彻底毁灭的小小死亡。<br>我将正视我的恐惧。"</p>
        <div class="scroll-indicator">↓ 下滑探索帝国档案</div>
      </div>
    </section>

    <!-- Section 1: Arrakis (Planet) -->
    <section id="arrakis" class="content-section arrakis-section" ref="sectionRefs[0]">
      <div class="meta-data top-left">ID: ARRAKIS_001</div>
      <div class="meta-data top-right">SEC: CLASSIFIED</div>
      
      <div class="section-content">
        <h2 class="section-header">01. 厄拉科斯</h2>
        <div class="sub-header">行星生态 PLANETARY ECOLOGY <span class="highlight">98%完整</span></div>
        
        <div class="planet-visual">
          <div class="planet-sphere"></div>
          <div class="orbit-ring"></div>
        </div>
        
        <p class="description">
          已知宇宙最危险的星球。无雨，只有无尽沙漠、沙虫，也是唯一香料产地。<br>
          <span class="mono">生态链：沙虫、香料、沙漠与人相互依存。</span><br>
          关于稀缺与适应的寓言。
        </p>
      </div>
    </section>

    <!-- Section 2: The Spice (Melange) -->
    <section id="spice" class="content-section spice-section" ref="sectionRefs[1]">
      <div class="meta-data top-left">ID: SPICE_002</div>
      <div class="meta-data top-right">SEC: CLASSIFIED</div>
      
      <div class="floating-particles">
        <div v-for="n in 50" :key="n" class="spice-particle" :style="getSpiceStyle(n)"></div>
      </div>
      
      <div class="section-content">
        <h2 class="section-header">02. 香料美琅脂</h2>
        <div class="sub-header">最佳计算进行中...</div>
        
        <p class="quote">"谁控制香料，谁控制宇宙。"</p>
        
        <div class="spice-interactive" @mousemove="disperseSpice">
          <div class="spice-heap"></div>
        </div>
        
        <p class="description">
          延寿、扩展意识，并驱动星际航行。<br>
          宇航公会垄断星际航行，帝国历法由此开始。<br>
          <span class="mono">警告：成瘾性极高。</span>
        </p>
      </div>
    </section>

    <!-- Section 3: The Houses (Politics) -->
    <section id="houses" class="content-section houses-section" ref="sectionRefs[2]">
      <div class="meta-data top-left">DATA_STREAM_2</div>
      <div class="meta-data top-right">PROCESSING...</div>
      
      <div class="section-content">
        <h2 class="section-header">03. 封建政治</h2>
        <div class="sub-header">FEUDAL POLITICS <span class="highlight">85%</span></div>
        
        <p class="description center-text">
          后AI时代的人类回到封建秩序。<br>家族阴谋与背叛是故事骨架。
        </p>

        <div class="houses-grid">
          <div class="house-card atreides">
            <h3>亚崔迪家族</h3>
            <div class="sigil hawk"></div>
            <p class="house-motto">"我在此，我留此。"</p>
            <p class="house-desc">来自卡拉丹，崇尚荣誉与美德，却因此招致毁灭。</p>
          </div>
          <div class="house-card harkonnen">
            <h3>哈克南家族</h3>
            <div class="sigil beast"></div>
            <p class="house-motto">"权力至上。"</p>
            <p class="house-desc">来自吉迪主星，残暴贪婪、工业化，是亚崔迪死敌。</p>
          </div>
        </div>
        
        <div class="imperial-decree">
          <div class="seal">御玺</div>
          <p>奉帕迪沙皇帝之令，持此契约者获准取得厄拉科斯编年史。</p>
          <p class="mono-small">AUTH: SHADDAM_IV</p>
        </div>
      </div>
    </section>

    <!-- Section 4: The Fremen & Prophecy -->
    <section id="fremen" class="content-section fremen-section" ref="sectionRefs[3]">
       <div class="meta-data top-left">ID: PROPHECY_003</div>
       <div class="meta-data top-right">SEC: CLASSIFIED</div>

      <div class="blue-eyes-container">
        <div class="eye-pair" v-for="n in 8" :key="n" :style="{ top: `${Math.random() * 80 + 10}%`, left: `${Math.random() * 80 + 10}%` }">
          <div class="eye"></div>
          <div class="eye"></div>
        </div>
      </div>
      
      <div class="section-content">
        <h2 class="section-header">04. 宗教工程</h2>
        <div class="sub-header">RELIGIOUS ENGINEERING <span class="highlight">92%</span></div>
        
        <p class="quote">"愿你的刀锋碎而不折。"</p>
        
        <div class="two-col">
          <div class="col">
             <h3>弗雷曼人</h3>
             <p>厄拉科斯原住民。适应极端环境，蓝中之蓝眼睛，能驾驭沙虫（沙伊-胡鲁德）。</p>
          </div>
          <div class="col">
             <h3>贝尼·杰瑟里特</h3>
             <p>播种神话以备后用。育种计划寻找魁萨茨·哈德拉克。保罗·亚崔迪是那个变量。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Section 5: Timeline (Golden Path) -->
    <section id="timeline" class="content-section timeline-section" ref="sectionRefs[4]">
      <div class="section-content">
        <h2 class="section-header">05. 黄金之路</h2>
        
        <div class="timeline-container">
           <div class="timeline-line"></div>
           
           <div class="timeline-item">
             <div class="year">200 BG</div>
             <div class="event">
               <h3>巴特兰圣战</h3>
               <p>"汝等不得制造机器去假冒人类思维。"</p>
             </div>
           </div>

           <div class="timeline-item">
             <div class="year">0 AG</div>
             <div class="event">
               <h3>宇航公会成立</h3>
               <p>公会垄断星际航行，帝国历法开始。</p>
             </div>
           </div>

           <div class="timeline-item highlight-event">
             <div class="year">10,191 AG</div>
             <div class="event">
               <h3>亚崔迪迁往沙丘</h3>
               <p>帕迪沙皇帝的陷阱。</p>
             </div>
           </div>

           <div class="timeline-item">
             <div class="year">10,193 AG</div>
             <div class="event">
               <h3>神帝莱托二世</h3>
               <p>保罗之子与沙虫融合，推行“黄金之路”。</p>
             </div>
           </div>
           
           <div class="timeline-item future">
             <div class="year">13,724 AG</div>
             <div class="event">
               <h3>高姆贾巴毒针</h3>
               <p>测试结束前不得抽回。</p>
             </div>
           </div>
        </div>

        <div class="golden-path-visual">
          <svg viewBox="0 0 100 20" class="path-svg">
            <path d="M0,10 Q50,0 100,10" fill="none" stroke="#FFD700" stroke-width="1" stroke-dasharray="2" />
          </svg>
        </div>
      </div>
    </section>

    <!-- Section 4: Footer -->
    <footer class="dune-footer">
       <p class="final-words">"Fear is the mind-killer."</p>
       <button class="footer-btn" @click="goBackHome">DEPART PLANET</button>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const goBackHome = () => {
  router.back();
};

const pageRef = ref<HTMLElement | null>(null);
const sectionRefs = ref<HTMLElement[]>([]);
const showNav = ref(false);
const mouseX = ref(0);
const mouseY = ref(0);

const sections = [
  { id: 'arrakis', name: '厄拉科斯' },
  { id: 'spice', name: '香料' },
  { id: 'houses', name: '家族' },
  { id: 'fremen', name: '宗教' },
  { id: 'timeline', name: '编年史' }
];

// Parallax & Mouse Interaction
const handleMouseMove = (e: MouseEvent) => {
  mouseX.value = e.clientX / window.innerWidth;
  mouseY.value = e.clientY / window.innerHeight;
};

const heroStyle = computed(() => ({
  transform: `translate(${mouseX.value * -20}px, ${mouseY.value * -20}px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * 30}px, ${mouseY.value * 30}px)`
}));

// Spice Particles
const getSpiceStyle = (n: number) => ({
  left: `${Math.random() * 100}%`,
  top: `${Math.random() * 100}%`,
  animationDuration: `${Math.random() * 5 + 3}s`,
  opacity: Math.random()
});

const disperseSpice = (e: MouseEvent) => {
  // Logic handled by CSS hover mostly, or future canvas upgrade
};

// Navigation
const scrollToSection = (id: string) => {
  const el = document.getElementById(id);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
};

const goBack = () => {
  router.back();
};

// Scroll Observer
let observer: IntersectionObserver;

onMounted(() => {
  const options = {
    threshold: 0.15
  };

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
      }
    });
  }, options);

  sections.forEach(sec => {
    const el = document.getElementById(sec.id);
    if (el) observer.observe(el);
  });

  const hero = document.querySelector('.hero-section');
  const navObserver = new IntersectionObserver((entries) => {
    showNav.value = !entries[0].isIntersecting;
  }, { threshold: 0.1 });
  
  if (hero) navObserver.observe(hero);
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700;900&family=Montserrat:wght@300;400;700&display=swap');

.dune-page {
  --font-epic: 'Cinzel', serif;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
  --font-tech: 'Montserrat', sans-serif; /* For HUD elements */
}

.dune-page {
  width: 100%;
  height: 100vh;
  background-color: #0f0a06;
  color: #e0c090;
  overflow-x: hidden;
  overflow-y: auto;
  font-family: var(--font-text);
  position: relative;
  scroll-behavior: smooth;
}

/* Typography Updates */
.hero-title {
  font-family: var(--font-epic);
  letter-spacing: 10px;
}

.hero-subtitle, .litany {
  font-family: var(--font-text);
  font-style: italic;
}

.section-header {
  font-family: var(--font-ui); /* Or Cinzel for headers too? User said "Small titles use Heiti" */
  font-weight: 900;
}

.sub-header {
  font-family: var(--font-tech);
  letter-spacing: 2px;
}

.meta-data, .nav-return-btn, .sticky-nav a {
  font-family: var(--font-ui);
}

/* Numbers */
.meta-data {
  font-family: var(--font-tech);
}

.mono { font-family: var(--font-tech); }
.mono-small { font-family: var(--font-tech); font-size: 0.8rem; opacity: 0.7; }

/* Return Buttons */
.footer-btn {
  background: transparent;
  border: 2px solid #c89650;
  color: #c89650;
  padding: 15px 40px;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 2px;
  margin-top: 30px;
  text-transform: uppercase;
}

.footer-btn:hover {
  background: #c89650;
  color: #000;
  box-shadow: 0 0 30px #c89650;
}

/* Nav */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  padding: 0;
  background: rgba(123, 51, 6, 0.95);
  border-bottom: 1px solid rgba(253, 245, 230, 0.1);
  backdrop-filter: blur(10px);
  z-index: 100;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.nav-return-btn {
  position: absolute;
  left: 40px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: 1px solid #c89650;
  color: #c89650;
  padding: 5px 15px;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.nav-return-btn:hover {
  background: #c89650;
  color: #000;
  box-shadow: 0 0 10px #c89650;
}

.sticky-nav.visible {
  transform: translateY(0);
}

.nav-links {
  display: flex;
  gap: 3rem;
  margin: 0;
  padding: 0;
}

.nav-links a {
  color: #FDF5E6;
  text-decoration: none;
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 2px;
  opacity: 0.6;
  transition: all 0.3s;
}

.sticky-nav a:hover {
  opacity: 1;
  color: #FFD700;
}

/* Hero Section */
.hero-section {
  height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  perspective: 1000px;
}

.hero-background {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 0;
}

.sun {
  position: absolute;
  top: 15%; right: 15%;
  width: 120px; height: 120px;
  background: #FFD700;
  border-radius: 50%;
  box-shadow: 0 0 80px #FF8C00;
}

.dune-layer {
  position: absolute;
  bottom: 0; left: 0; width: 100%;
  background-repeat: no-repeat;
  background-size: cover;
}

.layer-1 {
  height: 45%;
  background: #964B00;
  border-radius: 100% 0 0 0;
  bottom: -10%; left: -10%; width: 120%;
}
.layer-2 {
  height: 35%;
  background: #B35E05;
  border-radius: 0 100% 0 0;
  bottom: -5%; width: 110%; right: -10%;
}
.layer-3 {
  height: 25%;
  background: #D97706;
  border-radius: 50% 50% 0 0;
  bottom: 0; width: 100%;
}

.hero-content {
  z-index: 10;
  text-align: center;
  mix-blend-mode: overlay;
  padding: 0 20px;
}

.data-stream {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.9rem;
  letter-spacing: 2px;
  margin-bottom: 20px;
  opacity: 0.7;
  animation: blink 2s infinite;
}

.hero-title {
  font-family: 'Microsoft YaHei', sans-serif;
  font-size: 15rem; /* Exaggerated huge size */
  margin: 0;
  line-height: 0.8;
  font-weight: 900;
  letter-spacing: -10px;
  text-shadow: 0 20px 60px rgba(0,0,0,0.6);
  transform: scaleY(1.1); /* Slight vertical stretch for impact */
  z-index: 20;
}

.hero-subtitle {
  font-size: 1.5rem;
  letter-spacing: 8px;
  margin-top: 120px;
  margin-bottom: 40px;
  opacity: 0.9;
  text-transform: uppercase;
  z-index: 21;
}

.litany {
  font-size: 1.1rem;
  line-height: 1.6;
  font-style: italic;
  opacity: 0.8;
  border-left: 2px solid #FFD700;
  padding-left: 20px;
  display: inline-block;
  text-align: left;
}

.scroll-indicator {
  position: absolute;
  bottom: 40px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.8rem;
  opacity: 0.6;
  animation: bounce 2s infinite;
}

/* Content Sections */
.content-section {
  min-height: 100vh;
  padding: 100px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  opacity: 0;
  transform: translateY(50px);
  transition: opacity 1s ease, transform 1s ease;
  overflow: hidden;
}

.content-section.visible {
  opacity: 1;
  transform: translateY(0);
}

.meta-data {
  position: absolute;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.8rem;
  color: rgba(253, 245, 230, 0.3);
  letter-spacing: 1px;
}
.top-left { top: 40px; left: 40px; }
.top-right { top: 40px; right: 40px; }

.section-content {
  max-width: 900px;
  width: 100%;
  text-align: center;
  z-index: 5;
}

.section-header {
  font-size: 4rem;
  margin: 0;
  color: #FFD700;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.2);
  line-height: 1;
}

.sub-header {
  font-family: 'JetBrains Mono', monospace;
  font-size: 1rem;
  margin-bottom: 40px;
  opacity: 0.6;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.highlight {
  color: #FFD700;
  font-weight: bold;
}

.description {
  font-size: 1.2rem;
  line-height: 1.8;
  max-width: 700px;
  margin: 0 auto;
}

.center-text { text-align: center; }

/* Arrakis */
.arrakis-section {
  background: linear-gradient(to bottom, #7B3306, #5D2604);
}
.planet-visual {
  width: 250px; height: 250px;
  margin: 40px auto;
  position: relative;
}
.planet-sphere {
  width: 100%; height: 100%;
  background: radial-gradient(circle at 30% 30%, #D97706, #5D2604);
  border-radius: 50%;
  box-shadow: inset -30px -30px 60px rgba(0,0,0,0.6);
}
.orbit-ring {
  position: absolute;
  top: 50%; left: 50%;
  width: 350px; height: 100px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%) rotate(-15deg);
}

/* Spice */
.spice-section {
  background: #4A1E03;
}
.spice-particle {
  position: absolute;
  width: 3px; height: 3px;
  background: #00BFFF;
  border-radius: 50%;
  box-shadow: 0 0 8px #00BFFF;
  animation: float linear infinite;
}
.quote {
  font-size: 1.5rem;
  font-style: italic;
  margin-bottom: 30px;
  color: #FFD700;
}
.spice-interactive {
  width: 100%;
  height: 150px;
  background: radial-gradient(ellipse at center, rgba(184, 134, 11, 0.3) 0%, transparent 70%);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
}

/* Houses */
.houses-section {
  background: #2C1001;
}
.houses-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin: 60px 0;
}
.house-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.05);
  padding: 40px 20px;
  transition: all 0.4s;
}
.house-card:hover {
  background: rgba(255,255,255,0.08);
  transform: translateY(-10px);
}
.sigil {
  width: 60px; height: 60px;
  margin: 20px auto;
  border-radius: 50%;
}
.atreides .sigil { background: #006400; box-shadow: 0 0 20px rgba(0,100,0,0.4); }
.harkonnen .sigil { background: #8B0000; box-shadow: 0 0 20px rgba(139,0,0,0.4); }
.house-motto { font-style: italic; margin-bottom: 10px; color: #aaa; }
.house-desc { font-size: 0.9rem; opacity: 0.8; }

.imperial-decree {
  margin-top: 60px;
  border: 1px solid #FFD700;
  padding: 20px;
  display: inline-block;
  position: relative;
}
.seal {
  position: absolute;
  top: -15px; left: 50%; transform: translateX(-50%);
  background: #2C1001;
  padding: 0 10px;
  color: #FFD700;
  font-weight: bold;
}

/* Fremen */
.fremen-section {
  background: #000;
}
.blue-eyes-container {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
}
.eye-pair {
  position: absolute;
  display: flex;
  gap: 12px;
  opacity: 0.5;
  animation: blinkEyes 5s infinite;
}
.eye {
  width: 8px; height: 8px;
  background: #00BFFF;
  border-radius: 50%;
  box-shadow: 0 0 15px #00BFFF;
}
.two-col {
  display: flex;
  gap: 60px;
  margin-top: 40px;
  text-align: left;
}
.col h3 { color: #FFD700; border-bottom: 1px solid rgba(255,215,0,0.3); padding-bottom: 10px; }

/* Timeline */
.timeline-section {
  background: linear-gradient(to bottom, #2C1001, #7B3306);
}
.timeline-container {
  position: relative;
  max-width: 600px;
  margin: 0 auto;
  padding-left: 30px;
  border-left: 2px solid rgba(255,215,0,0.3);
  text-align: left;
}
.timeline-item {
  position: relative;
  margin-bottom: 40px;
  padding-left: 20px;
}
.timeline-item::before {
  content: '';
  position: absolute;
  left: -36px;
  top: 5px;
  width: 10px; height: 10px;
  background: #7B3306;
  border: 2px solid #FFD700;
  border-radius: 50%;
}
.highlight-event::before {
  background: #FFD700;
  box-shadow: 0 0 10px #FFD700;
}
.year {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.9rem;
  color: #FFD700;
  margin-bottom: 5px;
}
.event h3 { margin: 0 0 5px 0; font-size: 1.2rem; }
.event p { margin: 0; font-size: 0.9rem; opacity: 0.8; }
.future { opacity: 0.5; }

/* Footer */
.page-footer {
  padding: 60px 20px;
  background: #150802;
  text-align: center;
  border-top: 1px solid rgba(255,255,255,0.05);
}
.footer-data {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.8rem;
  color: #444;
  margin-bottom: 30px;
}
.back-btn {
  padding: 15px 40px;
  font-size: 1rem;
  background: transparent;
  color: #FDF5E6;
  border: 1px solid rgba(253, 245, 230, 0.3);
  cursor: pointer;
  transition: all 0.3s;
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: 1px;
}
.back-btn:hover {
  border-color: #FFD700;
  color: #FFD700;
  background: rgba(255, 215, 0, 0.05);
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(10px); }
}
@keyframes blink {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 0.3; }
}
@keyframes blinkEyes {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}
@keyframes float {
  0% { transform: translateY(0); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translateY(-50px); opacity: 0; }
}

@media (max-width: 768px) {
  .hero-title { font-size: 4rem; }
  .hero-subtitle { margin-top: 40px; }
  .houses-grid, .two-col { grid-template-columns: 1fr; flex-direction: column; gap: 30px; }
  .section-header { font-size: 3rem; }
}
</style>
