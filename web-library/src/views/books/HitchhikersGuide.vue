<template>
  <div class="hitchhiker-page-wrapper">
    <div class="hitchhiker-page" :style="pageStyle">
      <div class="scanlines"></div>
      
      <nav class="sticky-nav" :class="{ 'visible': showNav }">
        <button class="abort-btn nav-back-btn" @click="goBackHome">
          <span>[DON'T PANIC]</span>
        </button>
        <div class="nav-content">
          <div class="nav-brand">THE GUIDE</div>
          <ul class="nav-links">
            <li v-for="section in sections" :key="section.id">
              <a :href="`#${section.id}`" @click.prevent="scrollTo(section.id)" :class="{ active: activeSection === section.id }">
                <span class="nav-num">0{{ section.index }}</span>
                {{ section.label }}
              </a>
            </li>
          </ul>
        </div>
      </nav>

      <!-- Hero Section -->
      <section class="hero-section">
        <HitchhikersGuideCover class="hero-cover" mode="hero" />
        <div class="scroll-prompt" @click="scrollTo('dont-panic')">
          <div class="prompt-text">INITIATING GUIDE SYSTEMS...</div>
          <div class="arrow">↓</div>
        </div>
      </section>

      <!-- Section 1: Don't Panic -->
      <section id="dont-panic" class="content-section dont-panic-section" ref="sectionRefs">
        <div class="container">
          <h2 class="section-title">不要恐慌</h2>
          <div class="panic-content">
            <div class="guide-entry">
              <p>《银河系漫游指南》已经取代了伟大的《银河系百科全书》，成为所有知识和智慧的标准库。</p>
              <p>原因有二：</p>
              <ol>
                <li>它稍微便宜一点。</li>
                <li>它的封面上用大而友好的字母写着“<strong>不要恐慌</strong>”。</li>
              </ol>
            </div>
            <div class="towel-display" @click="toggleTowel">
              <div class="towel" :class="{ folded: isTowelFolded }">
                <span class="towel-text">TOWEL</span>
              </div>
              <p class="caption">点击折叠/展开您的毛巾 (星际旅行必备)</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 2: Galaxy Map & Database -->
      <section id="galaxy-map" class="content-section map-section" ref="sectionRefs">
        <div class="container">
          <h2 class="section-title">指南数据库</h2>
          <div class="galaxy-viewport" @mousemove="handleMapHover">
            <div class="star-field">
              <div v-for="(star, index) in stars" :key="index" class="star" 
                   :style="{ 
                     left: `${star.left}%`, 
                     top: `${star.top}%`,
                     animationDelay: `${star.animationDelay}s`
                   }">
              </div>
            </div>
            
            <div class="map-overlay-ui">
               <div class="sector-label">SECTOR ZZ9 PLURAL Z ALPHA</div>
               <div class="system-status-container">
                 <div class="system-status">System Alert: Audio Hazard</div>
               </div>
            </div>

            <div 
              v-for="planet in planets" 
              :key="planet.id"
              class="pixel-planet-container"
              :style="{ left: planet.x + '%', top: planet.y + '%' }"
              @click="handlePlanetClick(planet)"
            >
              <div class="pixel-planet" :class="`size-${planet.size}`"></div>
              <div v-if="planet.isDestroyed" class="destruction-msg">
                  已由沃贡建设舰队为了修建超空间快速通道而拆除。
              </div>
              <div class="planet-label" :class="{ 'align-right': planet.x > 80, 'align-left': planet.x < 20 }">
                 {{ planet.name }}
                 <div v-if="planet.desc" class="planet-desc">{{ planet.desc }}</div>
                 <div v-if="planet.status" class="planet-status-text">{{ planet.status }}</div>
              </div>
            </div>
          </div>
          
          <div class="database-grid">
             <div class="db-entry kit">
                <h3>生存工具包</h3>
                <p>星际旅行的必备装备。购买单元</p>
                <div class="kit-items">
                   <span class="item">毛巾 (未包含)</span>
                   <span class="item">电子拇指</span>
                   <span class="item">通天鱼</span>
                </div>
             </div>
             <div class="db-entry related">
                <h3>扩展知识库</h3>
                <ul class="related-list">
                   <li>
                      <span class="id">ID: THE-CYBERIAD</span>
                      <span class="name">赛博寓言</span>
                      <span class="desc">构造体单元的控制论寓言</span>
                   </li>
                   <li>
                      <span class="id">ID: GOOD-OMENS</span>
                      <span class="name">好兆头</span>
                      <span class="desc">世界末日迫在眉睫。生存指南</span>
                   </li>
                   <li>
                      <span class="id">ID: FOUNDATION</span>
                      <span class="name">基地</span>
                      <span class="desc">档案访问：银河百科全书残卷</span>
                   </li>
                </ul>
             </div>
          </div>
        </div>
      </section>

      <!-- Section 3: Vogon Poetry -->
      <section id="vogon-poetry" class="content-section poetry-section" ref="sectionRefs">
        <div class="container">
          <h2 class="section-title danger">警告：沃贡诗歌</h2>
          <div class="poetry-container">
            <div class="poetry-reader">
              <p class="verse" v-for="(line, index) in currentPoetry" :key="index" :style="{ filter: `blur(${poetryBlur}px)` }">
                {{ line }}
              </p>
            </div>
            <div class="poetry-controls">
              <div class="hazard-level">
                HAZARD LEVEL: <span class="blink">CRITICAL</span>
              </div>
              <button class="btn-risk" @mousedown="startReading" @mouseup="stopReading" @mouseleave="stopReading">
                按住试听 (后果自负)
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 4: Deep Thought -->
      <section id="deep-thought" class="content-section deep-thought-section" ref="sectionRefs">
        <div class="container">
          <h2 class="section-title">深思</h2>
          <div class="computer-interface">
            <div class="screen">
              <div class="output-log">
                <div v-for="(log, i) in deepThoughtLogs" :key="i" class="log-line">{{ log }}</div>
              </div>
              <div class="main-display">
                <span v-if="answerRevealed" class="the-answer">42</span>
                <span v-else class="thinking-loader">{{ thinkingStatus }}</span>
              </div>
            </div>
            <div class="controls">
              <button class="btn-ask" @click="askQuestion" :disabled="isThinking">
                查询关于生命、宇宙及一切的终极答案
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 5: Improbability Drive -->
      <section id="improbability-drive" class="content-section drive-section" ref="sectionRefs">
        <div class="container">
          <h2 class="section-title">无限非概率驱动</h2>
          <div class="drive-core-container">
            <div class="drive-status-panel">
               <div class="status-line">Status: <span class="status-ok">Online</span></div>
               <div class="status-line">Infinite Improbability Drive</div>
               <div class="status-desc">启动此驱动器将同时穿过每一个可能的宇宙中的每一个可能的点。</div>
            </div>

            <div class="drive-interaction">
               <button class="improbability-btn" @click="activateDrive" :disabled="driveActive">
                 <span class="btn-text">{{ driveActive ? 'CALCULATING...' : 'INITIATE JUMP' }}</span>
                 <div class="probability-field">PROBABILITY: 2^{{ probabilityPower }} : 1</div>
               </button>
            </div>

            <div class="chaos-log-terminal" v-if="chaosLogs.length > 0">
              <div v-for="(log, i) in chaosLogs" :key="i" class="chaos-entry">
                 > {{ log }}
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="hitchhiker-footer">
         <div v-if="!hasRated" class="rating-hitchhiker">
            <button v-if="!showRatingOptions" class="galaxy-btn" @click="showRatingOptions = true">评价银河</button>
            <div v-else class="galaxy-options">
                <button class="galaxy-opt" @click="rateBook(0)">恐慌 (0)</button>
                <button class="galaxy-opt" @click="rateBook(1)">基本无害 (1)</button>
                <button class="galaxy-opt" @click="rateBook(2)">42 (2)</button>
            </div>
         </div>
         <button v-else class="abort-btn footer-exit" @click="goBackHome">
            <span>[EXIT UNIVERSE]</span>
         </button>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import myAxios from "@/api/index";
import { useCookies } from '@vueuse/integrations/useCookies';
import { ElMessage } from "element-plus";
import HitchhikersGuideCover from '@/components/business/covers/HitchhikersGuideCover.vue';

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
      bookId: 3, // Hitchhiker's Guide ID
      bookName: "银河系漫游指南",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('指南已更新');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '检测到沃贡诗歌');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('计算机拒绝回答');
  }
};

// Fixed Star Field to prevent re-rendering glitches
const stars = ref(Array.from({ length: 50 }, () => ({
  left: Math.random() * 100,
  top: Math.random() * 100,
  animationDelay: Math.random() * 2
})));

const goBackHome = () => {
  router.back();
};

const showNav = ref(false);
const activeSection = ref('');
const sectionRefs = ref<HTMLElement[]>([]);

const sections = [
  { id: 'dont-panic', label: '不要恐慌', index: 1 },
  { id: 'galaxy-map', label: '指南数据库', index: 2 },
  { id: 'vogon-poetry', label: '沃贡诗歌', index: 3 },
  { id: 'deep-thought', label: '深思', index: 4 },
  { id: 'improbability-drive', label: '非概率驱动', index: 5 },
];

// Towel Logic
const isTowelFolded = ref(false);
const toggleTowel = () => {
  isTowelFolded.value = !isTowelFolded.value;
};

// Map Logic
interface Planet {
  id: string;
  name: string;
  x: number;
  y: number;
  size: number; // 1=small, 2=medium, 3=large
  desc?: string;
  status?: string;
  isDestroyed?: boolean;
}

const planets = ref<Planet[]>([]);

const generatePlanets = () => {
  const corePlanets: Planet[] = [
    { id: 'earth', name: '地球', x: 20, y: 70, size: 3, desc: '基本无害', isDestroyed: false },
    { id: 'vogsphere', name: '沃贡球形体', x: 80, y: 20, size: 3, status: '危险遏制：沃贡诗歌' },
    { id: 'magrathea', name: '马格拉西亚', x: 50, y: 50, size: 3 },
    { id: 'betelgeuse', name: '参宿四', x: 75, y: 75, size: 3 },
    { id: 'krikkit', name: '克里克特', x: 15, y: 30, size: 2 },
    { id: 'frogstar', name: '青蛙之星B', x: 85, y: 85, size: 2 },
    { id: 'traal', name: '特拉尔', x: 30, y: 15, size: 2, desc: '贪婪的巴格野兽' },
    { id: 'kakrafoon', name: '卡克拉冯', x: 60, y: 80, size: 2 },
    { id: 'santraginus', name: '桑特拉基努斯V', x: 40, y: 30, size: 2, desc: '大理石沙滩' },
    { id: 'damogran', name: '达莫格兰', x: 10, y: 50, size: 2, desc: '黄金之心' },
  ];

  const randomPlanets: Planet[] = [];
  // Ensure random planets don't overlap too much with existing ones (simple random for now)
  for (let i = 0; i < 30; i++) {
    randomPlanets.push({
      id: `p-${i}`,
      name: `Sector-${Math.floor(Math.random() * 900) + 100}`,
      x: Math.random() * 90 + 5, // 5% to 95%
      y: Math.random() * 90 + 5,
      size: 1
    });
  }
  
  planets.value = [...corePlanets, ...randomPlanets];
};

const handlePlanetClick = (planet: Planet) => {
  if (planet.id === 'earth') {
    planet.isDestroyed = true;
    setTimeout(() => {
       planet.isDestroyed = false;
    }, 5000);
  }
};

const handleMapHover = (e: MouseEvent) => {
  // subtle parallax for map could go here
};

onMounted(() => {
  generatePlanets();
});


// Poetry Logic
const poetryBlur = ref(0);
const readingInterval = ref<any>(null);
const currentPoetry = [
  "Oh freddled gruntbuggly,",
  "Thy micturations are to me,",
  "As plurdled gabbleblotchits,",
  "On a lurgid bee.",
  "Groop, I implore thee,",
  "And foopingly wah-hoo!"
];
const startReading = () => {
  readingInterval.value = setInterval(() => {
    if (poetryBlur.value < 10) {
      poetryBlur.value += 0.5;
    }
  }, 100);
};
const stopReading = () => {
  clearInterval(readingInterval.value);
  poetryBlur.value = 0;
};

// Deep Thought Logic
const deepThoughtLogs = ref<string[]>([]);
const isThinking = ref(false);
const answerRevealed = ref(false);
const thinkingStatus = ref('READY');

const askQuestion = () => {
  if (isThinking.value) return;
  isThinking.value = true;
  answerRevealed.value = false;
  deepThoughtLogs.value = [];
  thinkingStatus.value = 'COMPUTING...';
  
  const steps = [
    "正在访问矩阵...",
    "编译行星数据...",
    "忽略无关变量...",
    "喝杯茶...",
    "计算最终结果..."
  ];
  
  let step = 0;
  const process = setInterval(() => {
    if (step < steps.length) {
      deepThoughtLogs.value.push(steps[step]);
      step++;
    } else {
      clearInterval(process);
      isThinking.value = false;
      answerRevealed.value = true;
      deepThoughtLogs.value.push("计算完成。");
    }
  }, 1000);
};

// Improbability Drive Logic
const probabilityPower = ref(100000);
const driveActive = ref(false);
const chaosLogs = ref<string[]>([]);
const pageStyle = ref({});

const activateDrive = () => {
  if (driveActive.value) return;
  driveActive.value = true;
  chaosLogs.value = [];
  
  const scenarios = [
    "您变成了一盆矮牵牛花。",
    "哦，不，又来了。",
    "此时此刻，一只抹香鲸突然出现在半空中。",
    "不仅是宇宙，而且是所有的一切。",
    "你感觉自己像个沙发。",
    "重新校准现实..."
  ];

  let step = 0;
  const interval = setInterval(() => {
    probabilityPower.value = Math.floor(Math.random() * 99999999);
    if (Math.random() > 0.5 && step < scenarios.length) {
       chaosLogs.value.push(scenarios[Math.floor(Math.random() * scenarios.length)]);
    }
    step++;
    
    if (step > 20) {
      clearInterval(interval);
      driveActive.value = false;
      chaosLogs.value.push("JUMP COMPLETE. LOCATION: UNKNOWN.");
    }
  }, 100);
};

// Navigation
const scrollTo = (id: string) => {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
};

// Intersection Observer for Nav
let observer: IntersectionObserver;

onMounted(() => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        activeSection.value = entry.target.id;
      }
    });
  }, { threshold: 0.5 });
  
  // Observe sections
  document.querySelectorAll('.content-section').forEach(section => {
    observer.observe(section);
  });
  
  // Show nav after hero
  const heroObserver = new IntersectionObserver((entries) => {
    showNav.value = !entries[0].isIntersecting;
  }, { threshold: 0.1 });
  
  const hero = document.querySelector('.hero-section');
  if (hero) heroObserver.observe(hero);
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Fredoka+One&family=VT323&family=Comic+Neue:wght@400;700&display=swap');

.hitchhiker-page-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
  z-index: 2000;
  background-color: #000;
  --font-guide: 'VT323', monospace;
  --font-friendly: 'Fredoka One', cursive;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
}

.hitchhiker-page {
  width: 100%;
  min-height: 100vh;
  background-color: #1a2a3a;
  color: #c0d0e0;
  overflow-x: hidden;
  font-family: var(--font-guide);
  position: relative;
  scroll-behavior: smooth;
}

/* Typography Updates */
.section-title {
  font-family: var(--font-friendly);
  letter-spacing: 1px;
}

.nav-brand {
  font-family: var(--font-friendly);
}

.prompt-text, .sector-label, .system-status {
  font-family: var(--font-guide);
}

.system-status-container {
  width: 100%;
  display: flex;
  justify-content: center;
  position: absolute;
  top: 40px; /* Adjust based on sector-label height */
  left: 0;
}

.system-status {
  color: #ff0000;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 90%;
  background: rgba(0, 0, 0, 0.7);
  padding: 2px 10px;
  border: 1px solid #ff0000;
}

.guide-entry p, .planet-desc, .destruction-msg {
  font-family: var(--font-text);
}

.nav-links a, .abort-btn, .caption {
  font-family: var(--font-ui);
}

/* Numbers */
.nav-num {
  font-family: var(--font-guide);
}

.scanlines {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.25) 50%), linear-gradient(90deg, rgba(255, 0, 0, 0.06), rgba(0, 255, 0, 0.02), rgba(0, 0, 255, 0.06));
  background-size: 100% 2px, 3px 100%;
  pointer-events: none;
  z-index: 999;
}

/* ABORT BTN */
.abort-btn {
  /* Position handled by nav-back-btn when inside nav */
  z-index: 2000;
  background: #000;
  border: 2px solid #00ff00;
  color: #00ff00;
  padding: 8px 16px;
  font-family: 'Courier New', monospace;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 0 5px #00ff00;
  transition: all 0.2s;
}

.nav-back-btn {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.abort-btn:hover {
  background: #003300;
  box-shadow: 0 0 15px #00ff00;
}

.guide-footer {
  padding: 60px 0;
  text-align: center;
  background: #000;
  border-top: 1px solid #003300;
}

.return-normality-btn {
  background: #000;
  color: #00ff00;
  border: 2px solid #00ff00;
  padding: 15px 30px;
  font-family: 'Courier New', monospace;
  font-size: 1.2rem;
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.return-normality-btn:hover {
  background: #00ff00;
  color: #000;
  box-shadow: 0 0 20px #00ff00;
}

.footer-msg {
  color: #005500;
  font-size: 0.8rem;
}

/* Nav */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 15px 30px;
  background: rgba(0, 20, 0, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #00ff00;
  z-index: 100;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  display: flex;
  justify-content: center;
}

.sticky-nav.visible {
  transform: translateY(0);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 1200px;
}

.nav-brand {
  font-weight: bold;
  font-size: 1.2rem;
  letter-spacing: 2px;
}

.nav-links {
  display: flex;
  gap: 30px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-links a {
  color: #00aa00;
  text-decoration: none;
  font-size: 1.1rem;
  transition: all 0.2s;
}

.nav-links a:hover, .nav-links a.active {
  color: #00ff00;
  text-shadow: 0 0 5px #00ff00;
}

.nav-num {
  font-size: 0.8em;
  opacity: 0.6;
  margin-right: 5px;
}

/* Hero */
.hero-section {
  height: 100vh;
  position: relative;
}

.hero-cover {
  width: 100%;
  height: 100%;
}

.scroll-prompt {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  cursor: pointer;
  animation: bounce 2s infinite;
}

.arrow {
  font-size: 2rem;
  margin-top: 10px;
}

/* Content Sections */
.content-section {
  min-height: 100vh;
  padding: 100px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #003300;
  position: relative;
}

.container {
  max-width: 1000px;
  width: 100%;
  position: relative;
}

.section-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 3rem;
  margin-bottom: 50px;
  border-bottom: 2px solid #00ff00;
  display: inline-block;
  padding-bottom: 10px;
}

.section-title.danger {
  color: #ff0000;
  border-color: #ff0000;
}

/* Section 1: Don't Panic */
.panic-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
  align-items: center;
}

.guide-entry {
  font-size: 1.5rem;
  line-height: 1.6;
  border: 1px solid #00ff00;
  padding: 20px;
  background: rgba(0, 255, 0, 0.05);
}

.towel {
  width: 200px;
  height: 300px;
  background: #eee;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55);
  cursor: pointer;
  box-shadow: 5px 5px 15px rgba(0,0,0,0.5);
}

.towel.folded {
  height: 80px;
  width: 180px;
  transform: rotate(-5deg);
}

.towel-text {
  color: #333;
  font-weight: bold;
  font-family: sans-serif;
}

.caption {
  text-align: center;
  margin-top: 20px;
  font-size: 0.9rem;
  opacity: 0.7;
}

/* Section 2: Map */
.galaxy-viewport {
  height: 500px;
  background: #050505;
  border: 2px solid #00ff00;
  position: relative;
  overflow: hidden;
  cursor: crosshair;
}

.star {
  position: absolute;
  width: 2px;
  height: 2px;
  background: #fff;
  animation: twinkle 3s infinite;
}

.pixel-planet-container {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
}
.pixel-planet-container:hover {
  z-index: 10;
}

.pixel-planet {
  background-color: #00ff00;
  box-shadow: 0 0 4px #00ff00;
  transition: all 0.3s;
}

.pixel-planet.size-1 {
  width: 4px; height: 4px;
}
.pixel-planet.size-2 {
  width: 8px; height: 8px;
  box-shadow: 0 0 6px #00ff00;
}
.pixel-planet.size-3 {
  width: 12px; height: 12px;
  box-shadow: 0 0 10px #00ff00;
  border: 1px solid #fff;
}

.pixel-planet-container:hover .pixel-planet {
  transform: scale(1.5);
  box-shadow: 0 0 15px #00ff00;
  background-color: #fff;
}

.planet-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  color: #00ff00;
  margin-top: 5px;
  text-shadow: 0 0 2px #000;
  white-space: nowrap;
  pointer-events: none;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.pixel-planet-container:hover .planet-label {
  opacity: 1;
  font-weight: bold;
}

.planet-desc {
  font-size: 0.7rem;
  color: #00aa00;
  display: none;
}
.pixel-planet-container:hover .planet-desc {
  display: block;
}

.planet-status-text {
  color: #ff3300;
  font-size: 0.7rem;
  animation: blink 1s infinite;
  display: none;
}
.pixel-planet-container:hover .planet-status-text {
  display: block;
}

.destruction-msg {
  position: absolute;
  bottom: 20px; /* Position above the planet */
  top: auto;
  left: 50%;
  transform: translateX(-50%);
  width: max-content;
  max-width: 250px;
  background: rgba(20, 0, 0, 0.9);
  border: 1px solid #ff0000;
  color: #ff0000;
  padding: 8px 12px;
  font-size: 0.8rem;
  z-index: 20;
  text-align: center;
  white-space: normal;
  line-height: 1.4;
  box-shadow: 0 0 10px rgba(255, 0, 0, 0.3);
}

.planet-label.align-right {
  transform: translateX(-30%);
}

.planet-label.align-left {
  transform: translateX(30%);
}

/* Section 3: Poetry */
.poetry-container {
  text-align: center;
}

.poetry-reader {
  font-family: 'Times New Roman', serif;
  font-size: 2rem;
  margin-bottom: 40px;
  min-height: 200px;
}

.btn-risk {
  background: #ff0000;
  color: #000;
  border: none;
  padding: 15px 30px;
  font-size: 1.2rem;
  font-family: 'VT323', monospace;
  cursor: pointer;
  transition: transform 0.1s;
}

.btn-risk:active {
  transform: scale(0.95);
}

.hazard-level {
  color: #ff0000;
  margin-bottom: 10px;
  font-size: 1.2rem;
}

.blink {
  animation: blink 0.5s infinite;
}

/* Section 4: Deep Thought */
.computer-interface {
  background: #111;
  border: 10px solid #333;
  border-radius: 20px;
  padding: 40px;
  max-width: 600px;
  margin: 0 auto;
}

.screen {
  background: #000;
  height: 300px;
  border: 2px solid #00ff00;
  margin-bottom: 20px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.log-line {
  font-size: 0.9rem;
  color: #00aa00;
  margin-bottom: 5px;
}

.main-display {
  text-align: center;
  font-size: 2rem;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.the-answer {
  font-size: 5rem;
  font-weight: bold;
  text-shadow: 0 0 20px #00ff00;
  animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.btn-ask {
  width: 100%;
  padding: 15px;
  background: #003300;
  color: #00ff00;
  border: 1px solid #00ff00;
  font-family: 'VT323', monospace;
  font-size: 1.2rem;
  cursor: pointer;
}

.btn-ask:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-ask:hover:not(:disabled) {
  background: #004400;
}

/* Section 2: Map & Database */
.map-overlay-ui {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 5;
  font-family: 'Share Tech Mono', monospace;
  pointer-events: none;
}
.sector-label {
  color: #00ff00;
  font-size: 1.2rem;
  border: 1px solid #00ff00;
  padding: 5px 10px;
  background: rgba(0, 20, 0, 0.8);
}
.system-status {
  color: #ff3300;
  margin-top: 5px;
  font-size: 0.9rem;
  animation: blink 1s infinite;
}
.planet-status {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  font-size: 0.7rem;
  white-space: nowrap;
  margin-top: 5px;
}
.planet-status.warning { color: #ff3300; }

.database-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 30px;
  z-index: 5;
  position: relative;
}
.db-entry {
  background: rgba(0, 20, 0, 0.8);
  border: 1px solid #00ff00;
  padding: 20px;
  color: #00ff00;
  font-family: 'Share Tech Mono', monospace;
}
.db-entry h3 {
  border-bottom: 1px solid #004400;
  padding-bottom: 10px;
  margin-bottom: 15px;
  font-size: 1.2rem;
}
.kit-items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}
.kit-items .item {
  border: 1px solid #004400;
  padding: 5px 10px;
  font-size: 0.9rem;
}
.related-list {
  list-style: none;
  padding: 0;
}
.related-list li {
  margin-bottom: 15px;
  border-left: 2px solid #004400;
  padding-left: 10px;
}
.related-list span { display: block; }
.related-list .id { color: #008800; font-size: 0.8rem; }
.related-list .name { font-weight: bold; margin: 2px 0; }
.related-list .desc { font-size: 0.9rem; opacity: 0.8; }

/* Section 5: Improbability Drive Updated */
.drive-core-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 600px;
  margin: 0 auto;
}
.drive-status-panel {
  background: rgba(0,0,0,0.8);
  border: 2px solid #00ff00;
  padding: 20px;
  text-align: left;
  font-family: 'Share Tech Mono', monospace;
  color: #00ff00;
}
.status-line { margin-bottom: 10px; font-size: 1.1rem; }
.status-ok { color: #00ff00; font-weight: bold; }
.status-desc { font-size: 0.9rem; color: #00aa00; margin-top: 10px; border-top: 1px dashed #004400; padding-top: 10px; }

.drive-interaction {
  display: flex;
  justify-content: center;
}
.improbability-btn {
  background: #000;
  border: 2px solid #00ff00;
  color: #00ff00;
  padding: 20px 40px;
  font-family: 'Share Tech Mono', monospace;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
  position: relative;
  overflow: hidden;
}
.improbability-btn:hover:not(:disabled) {
  background: #002200;
  box-shadow: 0 0 20px #00ff00;
}
.improbability-btn:disabled {
  opacity: 0.7;
  cursor: wait;
  border-color: #ff3300;
  color: #ff3300;
}
.probability-field {
  font-size: 0.8rem;
  margin-top: 5px;
  opacity: 0.7;
}

.chaos-log-terminal {
  background: #000;
  border: 1px solid #004400;
  padding: 15px;
  height: 150px;
  overflow-y: auto;
  font-family: 'VT323', monospace;
  text-align: left;
  color: #00ff00;
}
.chaos-entry {
  margin-bottom: 5px;
  animation: typing 0.5s steps(40, end);
}

@keyframes typing {
  from { width: 0; }
  to { width: 100%; }
}

/* Footer */
.footer {
  padding: 50px;
  text-align: center;
  border-top: 1px solid #003300;
}

.back-btn {
  background: transparent;
  color: #00ff00;
  border: 1px solid #00ff00;
  padding: 10px 30px;
  font-family: 'VT323', monospace;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #00ff00;
  color: #000;
}

/* Animations */
@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {transform: translateX(-50%) translateY(0);}
  40% {transform: translateX(-50%) translateY(-20px);}
  60% {transform: translateX(-50%) translateY(-10px);}
}

@keyframes twinkle {
  0%, 100% { opacity: 0.2; }
  50% { opacity: 1; }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

@keyframes popIn {
  0% { transform: scale(0); }
  100% { transform: scale(1); }
}

@media (max-width: 768px) {
  .section-title { font-size: 2rem; }
  .panic-content { grid-template-columns: 1fr; }
  .sticky-nav { padding: 10px; }
  .nav-brand { display: none; }
  .nav-links { gap: 15px; }
  .nav-num { display: none; }
}
/* Rating Styles */
.hitchhiker-footer {
    padding: 60px 20px;
    background: #000;
    display: flex;
    justify-content: center;
    align-items: center;
    border-top: 2px solid #0f0;
}
.rating-hitchhiker {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
}
.galaxy-btn {
    background: #0f0;
    color: #000;
    border: none;
    padding: 15px 40px;
    font-family: 'Courier New', monospace;
    font-weight: bold;
    font-size: 1.2rem;
    cursor: pointer;
    box-shadow: 0 0 10px #0f0;
}
.galaxy-btn:hover {
    background: #fff;
    box-shadow: 0 0 20px #fff;
}
.galaxy-options {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
}
.galaxy-opt {
    background: #000;
    border: 1px solid #0f0;
    color: #0f0;
    padding: 10px 20px;
    cursor: pointer;
    font-family: 'Courier New', monospace;
}
.galaxy-opt:hover {
    background: #0f0;
    color: #000;
}
.footer-exit {
    padding: 15px 40px;
    background: transparent;
    border: 1px solid #ff0000;
    color: #ff0000;
    font-family: 'Courier New', monospace;
    cursor: pointer;
}
.footer-exit:hover {
    background: #ff0000;
    color: #fff;
}
</style>
