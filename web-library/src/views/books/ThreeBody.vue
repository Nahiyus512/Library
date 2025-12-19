<template>
  <div class="three-body-container">
    <!-- Fixed Elements -->
    <div class="death-countdown">
      {{ countdownDisplay }}
    </div>

    <nav class="sticky-nav" :class="{ 'nav-visible': showNav }">
      <button class="back-home-btn nav-back-btn" @click="goBackHome">
        <span>[脱离游戏]</span>
      </button>
      <div 
        v-for="(section, index) in sections" 
        :key="index"
        class="nav-item"
        :class="{ active: currentSection === index }"
        @click="scrollToSection(index)"
      >
        {{ section.name }}
      </div>
    </nav>

    <!-- Section 1: Hero (Cover) -->
    <section class="section section-hero" id="section-0">
      <div class="hero-content">
        <div class="star-system-hero">
           <div class="star-hero star-1"></div>
           <div class="star-hero star-2"></div>
           <div class="star-hero star-3"></div>
        </div>
        <h1 class="main-title glitch" data-text="三体">三体</h1>
        <h2 class="sub-title warning-blink">不要回答</h2>
      </div>
      <div class="scroll-indicator">下滑进入三体世界</div>
    </section>

    <!-- Section 2: Archives (Books) -->
    <section class="section section-archives" id="section-1">
      <h2 class="section-heading">档案</h2>
      <div class="books-grid">
        <!-- Book 1 -->
        <div class="book-card card-red">
          <div class="book-year">2006</div>
          <h3 class="book-title">三体</h3>
          <p class="book-intro">地球往事</p>
          <div class="book-excerpt">
            “给岁月以文明，而不是给文明以岁月。”
          </div>
        </div>
        <!-- Book 2 -->
        <div class="book-card card-blue">
          <div class="book-year">2008</div>
          <h3 class="book-title">黑暗森林</h3>
          <p class="book-intro">面壁者计划</p>
          <div class="book-excerpt">
            “宇宙就是一座黑暗森林，每个文明都是带枪的猎人。”
          </div>
        </div>
        <!-- Book 3 -->
        <div class="book-card card-black">
          <div class="book-year">2010</div>
          <h3 class="book-title">死神永生</h3>
          <p class="book-intro">阶梯计划</p>
          <div class="book-excerpt">
            “弱小和无知不是生存的障碍，傲慢才是。”
          </div>
        </div>
      </div>
    </section>

    <!-- Section 3: Personnel -->
    <section class="section section-personnel" id="section-2">
      <h2 class="section-heading">人物</h2>
      <div class="personnel-grid">
        <div class="person-item" v-for="(person, index) in personnel" :key="index">
          <div class="person-role">{{ person.role }}</div>
          <div class="person-name">{{ person.name }}</div>
        </div>
      </div>
    </section>

    <!-- Section 4: Concepts (Classified) -->
    <section class="section section-concepts" id="section-3">
      <h2 class="section-heading">绝密</h2>
      <div class="concepts-list">
        <div class="concept-item" v-for="(concept, index) in concepts" :key="index">
          <h3 class="concept-title">{{ concept.title }}</h3>
          <p class="concept-desc">{{ concept.desc }}</p>
        </div>
      </div>
    </section>

    <!-- Section 5: Timeline -->
    <section class="section section-timeline" id="section-4">
      <h2 class="section-heading">编年史</h2>
      <div class="timeline-container-full">
        <div class="timeline-line"></div>
        <div class="timeline-event" v-for="(event, index) in timeline" :key="index">
          <div class="event-year">{{ event.year }}</div>
          <div class="event-title">{{ event.title }}</div>
        </div>
      </div>
      
      <!-- Footer Return -->
      <div class="footer-return-container">
        <div v-if="!hasRated" class="rating-threebody">
            <button v-if="!showRatingOptions" class="trisolaris-btn" @click="showRatingOptions = true">发送广播</button>
            <div v-else class="trisolaris-options">
                <button class="trisolaris-opt" @click="rateBook(0)">不要回答 (0)</button>
                <button class="trisolaris-opt" @click="rateBook(1)">中立 (1)</button>
                <button class="trisolaris-opt" @click="rateBook(2)">入侵 (2)</button>
            </div>
        </div>
        <button v-else class="footer-return-btn" @click="goBackHome">
          &lt;&lt; 返回地球 &gt;&gt;
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import myAxios from "@/api/index";
import { useCookies } from '@vueuse/integrations/useCookies';
import { ElMessage } from "element-plus";

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
      bookId: 1, // Three Body ID
      bookName: "三体",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('广播已发送');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '信号被干扰');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('智子屏蔽');
  }
};

const sections = [
  { name: '首页' },
  { name: '档案' },
  { name: '人物' },
  { name: '绝密' },
  { name: '编年史' }
];

const personnel = ref([
  { name: '叶文洁', role: '天体物理学家 / 地球三体组织(ETO)统帅 / 开启者' },
  { name: '罗辑', role: '面壁者 / 执剑人 / 宇宙社会学家 / 守墓人' },
  { name: '章北海', role: '太空军政治部主任 / 自然选择号舰长 / 星舰地球之父' },
  { name: '程心', role: '第二任执剑人 / 阶梯计划候选人 / 圣母' },
  { name: '托马斯·维德', role: 'PIA局长 / 行星防御理事会(PDC)战略情报局局长' },
  { name: '丁仪', role: '理论物理学家 / 可控核聚变专家 / 首次接触水滴' },
  { name: '史强', role: '警察 / 地球防务安全部(PDC)核心成员 / 罗辑护卫' },
  { name: '汪淼', role: '纳米材料专家 / 飞刃计划主导者 / 古筝行动核心' },
  { name: '云天明', role: '阶梯计划执行人 / 三体舰队观测员 / 童话暗示者' },
  { name: '智子', role: '三体驻地球大使 / 拟人化AI / 茶道专家' }
]);

const concepts = ref([
  { title: '智子 (Sophon)', desc: '三体人将质子进行二维展开并蚀刻电路后制造的微观超级计算机。通过量子纠缠实现实时通讯，并能干扰粒子加速器，锁死地球基础科学。' },
  { title: '水滴 (Droplet)', desc: '三体强相互作用力宇宙探测器。表面绝对光滑，温度处于绝对零度，原子核被强互作用力紧密锁死，无坚不摧，仅凭撞击摧毁了人类2000艘恒星级战舰。' },
  { title: '黑暗森林法则', desc: '宇宙社会学核心公理：1.生存是文明的第一需要；2.文明不断增长和扩张，但宇宙物质总量保持不变。推论：宇宙是一座黑暗森林，每个文明都是带枪的猎人，一旦发现坐标必须开枪消灭。' },
  { title: '二向箔 (Dual-Vector Foil)', desc: '歌者文明使用的降维打击武器。封装状态下为一张晶莹剔透的卡片，激活后导致三维空间向二维跌落，整个太阳系因此被二维化，形成一幅巨画。' },
  { title: '曲率驱动 (Curvature Propulsion)', desc: '利用时空曲率差异驱动飞船，是实现光速飞行的唯一途径。飞船驶过留下的航迹会降低光速，形成“黑域”或“慢雾”。' },
  { title: '面壁计划 (Wallfacer Project)', desc: '利用三体人思维透明、无法理解欺诈的弱点，选出四位面壁者，完全依靠大脑制定战略，不与外界交流，以期欺骗智子和三体世界。' }
]);

const timeline = ref([
  { year: '1453', title: '魔法师之死：高维碎片接触地球，君士坦丁堡陷落。' },
  { year: '1967', title: '红岸基地建立，旨在搜寻地外文明。' },
  { year: '1971', title: '叶文洁向太阳发射信号，利用恒星增益向宇宙广播。' },
  { year: '1979', title: '叶文洁收到三体世界和平主义者回复：“不要回答！不要回答！不要回答！”' },
  { year: '2007', title: '古筝行动：汪淼利用“飞刃”切割“审判日”号，截获三体信息。' },
  { year: '危机纪元 3年', title: '面壁计划启动，罗辑、泰勒、雷迪亚兹、希恩斯成为面壁者。' },
  { year: '危机纪元 205年', title: '末日之战：水滴抵达太阳系，摧毁人类太空舰队。' },
  { year: '危机纪元 208年', title: '罗辑在叶文洁墓前悟出黑暗森林法则，通过咒语实验验证，建立威慑。' },
  { year: '威慑纪元', title: '三体人传授引力波技术，地球引力波发射台建立。罗辑担任执剑人54年。' },
  { year: '威慑纪元 62年', title: '程心接任执剑人，水滴摧毁引力波发射台，威慑失败。' },
  { year: '广播纪元 7年', title: '万有引力号启动引力波广播，三体世界坐标暴露。' },
  { year: '广播纪元 8年', title: '三体星系被光粒摧毁。' },
  { year: '掩体纪元 67年', title: '太阳系遭遇二向箔打击，开始二维化。' },
  { year: '银河纪元 409年', title: '云天明与程心在拉格朗日点短暂相会，讲述三个童话。' },
  { year: 'DX3906星系', title: '程心与关一帆在蓝星建立殖民地，错过云天明。' },
  { year: '18906416', title: '归零者发布回归运动声明，程心留下5公斤生态球，等待宇宙重启。' }
]);

const currentSection = ref(0);
const showNav = ref(false);

const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement;
  const scrollTop = target.scrollTop;
  const height = target.clientHeight;
  
  // Nav visibility
  showNav.value = scrollTop > 100;

  // Current Section
  const index = Math.round(scrollTop / height);
  currentSection.value = index;
};

const scrollToSection = (index: number) => {
  const el = document.getElementById(`section-${index}`);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
};

const goBackHome = () => {
  router.back();
};

// Countdown Logic
const remainingTime = ref(1194 * 3600 + 50 * 60 + 39); // Initial seconds
const countdownDisplay = computed(() => {
  if (remainingTime.value <= 0) return "00:00:00";
  const h = Math.floor(remainingTime.value / 3600);
  const m = Math.floor((remainingTime.value % 3600) / 60);
  const s = Math.floor(remainingTime.value % 60);
  return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
});

let timer: any;

onMounted(() => {
  timer = setInterval(() => {
    remainingTime.value--;
  }, 1000);

  // Add scroll listener
  const container = document.querySelector('.three-body-container');
  if (container) {
    container.addEventListener('scroll', handleScroll);
  }
});

onUnmounted(() => {
  clearInterval(timer);
  const container = document.querySelector('.three-body-container');
  if (container) {
    container.removeEventListener('scroll', handleScroll);
  }
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Orbitron:wght@400;700;900&family=Noto+Serif+SC:wght@400;700&display=swap');

.three-body-container {
  --font-tech: 'Share Tech Mono', monospace;
  --font-hero: 'Orbitron', sans-serif;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
}

.three-body-container {
  width: 100%;
  height: 100vh;
  background-color: #000;
  color: #fff;
  overflow-x: hidden;
  overflow-y: auto;
  font-family: var(--font-ui);
  position: relative;
  scroll-behavior: smooth;
}

/* Typography Updates */
.main-title {
  font-family: var(--font-hero);
  letter-spacing: 10px;
}

.sub-title, .warning-blink {
  font-family: var(--font-tech);
  color: #ff0000;
}

.death-countdown, .book-year, .person-role, .concept-title {
  font-family: var(--font-tech);
}

.book-title, .section-heading {
  font-family: var(--font-ui);
  font-weight: 900;
}

.book-excerpt, .concept-desc, .timeline-desc {
  font-family: var(--font-text);
}

.nav-item, .back-home-btn {
  font-family: var(--font-ui);
}


/* Sticky Navigation */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 20px 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  z-index: 1000;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  border-bottom: 1px solid rgba(255, 0, 0, 0.3);
}

.sticky-nav.nav-visible {
  transform: translateY(0);
}

.nav-back-btn {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  margin: 0;
}

.nav-item {
  cursor: pointer;
  color: #666;
  font-size: 14px;
  letter-spacing: 2px;
  transition: all 0.3s;
  padding: 5px 10px;
  position: relative;
}

.nav-item:hover, .nav-item.active {
  color: #fff;
  text-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; width: 100%; height: 2px;
  background: #ff0000;
  box-shadow: 0 0 8px #ff0000;
}

.section {
  width: 100%;
  min-height: 100vh; /* Changed from height to min-height */
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  box-sizing: border-box;
}

.section-heading {
  font-size: 2.5rem;
  margin-bottom: 50px;
  border-bottom: 2px solid #fff;
  padding-bottom: 10px;
  z-index: 10;
}

/* Fixed Elements */
.death-countdown {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) perspective(1000px) rotateX(10deg);
  font-family: 'Share Tech Mono', monospace;
  font-size: 15vw;
  font-weight: 700;
  color: rgba(255, 0, 0, 0.4); /* Increased opacity for vividness */
  text-shadow: 0 0 20px rgba(255, 0, 0, 0.3); /* Added glow */
  pointer-events: none;
  z-index: 1; 
  white-space: nowrap;
  mix-blend-mode: screen;
}

/* Back Button Styles */
.back-home-btn.fixed-top-left {
  position: fixed;
  top: 30px;
  left: 30px;
  z-index: 2000;
  background: rgba(0, 0, 0, 0.7);
  border: 1px solid #ff0000;
  color: #ff0000;
  padding: 10px 20px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.back-home-btn.fixed-top-left:hover {
  background: #ff0000;
  color: #000;
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.5);
}

.footer-return-container {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 60px 0 20px;
}

.footer-return-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.6);
  padding: 15px 40px;
  font-family: 'SimHei', serif;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.4s;
  letter-spacing: 4px;
}

.footer-return-btn:hover {
  border-color: #fff;
  color: #fff;
  text-shadow: 0 0 10px #fff;
  background: rgba(255, 255, 255, 0.05);
}

/* Section 1: Hero */
.section-hero {
  background: #000;
  height: 100vh; /* Hero stays full height */
}

.hero-content {
  text-align: center;
  z-index: 10;
  position: relative;
}

.main-title {
  font-family: 'SimHei', 'Microsoft YaHei', sans-serif; /* Boxy font */
  font-size: 24rem;
  font-weight: 900;
  margin: 0;
  line-height: 1;
  letter-spacing: 0px;
  text-shadow: 0 0 20px rgba(255,255,255,0.5);
  margin-top: 100px;
}

/* Star System Hero */
.star-system-hero {
  position: absolute;
  top: 50%; left: 50%;
  width: 100%; height: 100%;
  transform: translate(-50%, -50%);
  z-index: -1;
  pointer-events: none;
}

.star-hero {
  position: absolute;
  top: 50%; left: 50%;
  border-radius: 50%;
  filter: blur(5px);
  box-shadow: 0 0 30px currentColor;
}

.star-1 {
  width: 40px; height: 40px;
  background: #ffaa00;
  color: #ffaa00;
  animation: orbit1 15s infinite linear;
}

.star-2 {
  width: 25px; height: 25px;
  background: #00aaff;
  color: #00aaff;
  animation: orbit2 20s infinite linear;
}

.star-3 {
  width: 50px; height: 50px;
  background: #ff4444;
  color: #ff4444;
  animation: orbit3 18s infinite linear;
}

@keyframes orbit1 {
  0% { transform: rotate(0deg) translateX(350px) rotate(0deg); }
  100% { transform: rotate(360deg) translateX(350px) rotate(-360deg); }
}

@keyframes orbit2 {
  0% { transform: rotate(120deg) translateX(450px) rotate(-120deg); }
  100% { transform: rotate(480deg) translateX(450px) rotate(-480deg); }
}

@keyframes orbit3 {
  0% { transform: rotate(240deg) translateX(280px) rotate(-240deg); }
  100% { transform: rotate(600deg) translateX(280px) rotate(-600deg); }
}

.sub-title {
  font-size: 3rem;
  color: #ff0000;
  margin-top: 20px;
  letter-spacing: 10px;
}

.warning-blink {
  animation: blink 2s infinite;
}

.scroll-indicator {
  position: absolute; bottom: 30px;
  font-size: 14px; opacity: 0.5;
  animation: float 2s infinite;
}

/* Back Button */
.back-home-btn {
  margin-top: 0px;
  padding: 12px 30px;
  background: transparent;
  border: 1px solid #fff;
  color: #fff;
  font-family: 'Noto Serif SC', serif;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 20;
}

.back-home-btn:hover {
  background: #fff;
  color: #000;
  box-shadow: 0 0 20px rgba(255,255,255,0.5);
}

/* Section 2: Archives */
.section-archives {
  background: #050505;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  width: 100%;
  max-width: 1200px;
  z-index: 10;
}

.book-card {
  padding: 30px;
  height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform 0.3s;
  border: 1px solid rgba(255,255,255,0.1);
}

.book-card:hover {
  transform: translateY(-10px);
}

.card-red { background: linear-gradient(135deg, #300 0%, #000 100%); border-color: #500; }
.card-blue { background: linear-gradient(135deg, #002 0%, #000 100%); border-color: #005; }
.card-black { background: linear-gradient(135deg, #111 0%, #000 100%); border-color: #333; }

.book-year {
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.2rem;
  opacity: 0.6;
}

.book-title {
  font-size: 2.5rem;
  margin: 20px 0 10px;
}

.book-intro {
  font-size: 1.2rem;
  opacity: 0.8;
  margin-bottom: auto;
}

.book-excerpt {
  font-style: italic;
  font-size: 0.9rem;
  line-height: 1.6;
  opacity: 0.7;
  border-left: 2px solid rgba(255,255,255,0.3);
  padding-left: 15px;
}

/* Section 3: Personnel */
.section-personnel {
  background: #080808;
}

.personnel-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  width: 100%;
  max-width: 1000px;
  z-index: 10;
}

.person-item {
  background: rgba(255,255,255,0.05);
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.person-item:hover {
  background: #fff;
  color: #000;
  transform: scale(1.05);
}

.person-role {
  font-size: 0.8rem;
  opacity: 0.7;
  margin-bottom: 5px;
}

.person-name {
  font-size: 1.5rem;
  font-weight: bold;
}

/* Section 4: Concepts */
.section-concepts {
  background: #030303;
}

.concepts-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  width: 100%;
  max-width: 1000px;
  z-index: 10;
}

.concept-item {
  border: 1px solid #333;
  padding: 30px;
  background: rgba(0,0,0,0.5);
}

.concept-title {
  font-size: 1.5rem;
  color: #ff3333;
  margin-bottom: 15px;
  font-family: 'Share Tech Mono', monospace;
}

.concept-desc {
  line-height: 1.6;
  opacity: 0.8;
}

/* Section 5: Timeline */
.section-timeline {
  background: #000;
  height: auto; /* Allow auto height */
  min-height: 100vh;
}

.timeline-container-full {
  width: 100%;
  max-width: 800px;
  position: relative;
  z-index: 10;
  padding: 50px 0;
  /* Removed max-height and overflow-y to allow full page expansion */
}

.timeline-event {
  display: flex;
  margin-bottom: 50px;
  position: relative;
  padding-left: 30px;
  opacity: 0.8;
  transition: opacity 0.3s;
}

.timeline-event:hover {
  opacity: 1;
}

.timeline-event::before {
  content: '';
  position: absolute;
  left: 0;
  top: 5px;
  width: 10px;
  height: 10px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 10px #fff;
}

.timeline-line {
  position: absolute;
  left: 4px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: rgba(255, 255, 255, 0.2);
}


.event-year {
  width: 120px;
  font-family: 'Share Tech Mono', monospace;
  color: #ff3333;
  font-weight: bold;
  flex-shrink: 0;
}

.event-title {
  font-size: 1.1rem;
}

/* Animations */
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(10px); } }

/* Glitch Effect */
.glitch {
  position: relative;
}
.glitch::before, .glitch::after {
  content: attr(data-text);
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: #000;
}
.glitch::before {
  left: 2px; text-shadow: -1px 0 #ff0000; clip: rect(24px, 550px, 90px, 0);
  animation: glitch-anim-2 3s infinite linear alternate-reverse;
}
.glitch::after {
  left: -2px; text-shadow: -1px 0 #00ff00; clip: rect(85px, 550px, 140px, 0);
  animation: glitch-anim-2 2s infinite linear alternate-reverse;
}

@keyframes glitch-anim-2 {
  0% { clip: rect(10px, 9999px, 30px, 0); }
  20% { clip: rect(80px, 9999px, 100px, 0); }
  40% { clip: rect(40px, 9999px, 60px, 0); }
  60% { clip: rect(20px, 9999px, 80px, 0); }
  80% { clip: rect(90px, 9999px, 95px, 0); }
  100% { clip: rect(60px, 9999px, 70px, 0); }
}

/* Responsive */
@media (max-width: 768px) {
  .books-grid, .personnel-grid, .concepts-list {
    grid-template-columns: 1fr;
  }
  .main-title { font-size: 6rem; }
  .death-countdown { font-size: 20vw; }
}
/* Rating Styles */
.rating-threebody {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    margin-top: 20px;
}

.trisolaris-btn {
    background: transparent;
    border: 1px solid #ef4444;
    color: #ef4444;
    padding: 10px 30px;
    font-family: 'Courier New', monospace;
    letter-spacing: 2px;
    cursor: pointer;
    transition: all 0.3s;
}

.trisolaris-btn:hover {
    background: #ef4444;
    color: #000;
    box-shadow: 0 0 15px #ef4444;
}

.trisolaris-options {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
}

.trisolaris-opt {
    background: rgba(0,0,0,0.8);
    border: 1px solid #fff;
    color: #fff;
    padding: 8px 20px;
    cursor: pointer;
    font-family: 'Courier New', monospace;
}

.trisolaris-opt:hover {
    background: #fff;
    color: #000;
}
</style>
