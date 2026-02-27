<template>
  <div class="three-kingdoms-container" @scroll="handleScroll">
    <!-- Sticky Header Navigation -->
    <header class="tk-nav" :class="{ 'visible': showNav }">
      <button class="nav-return-btn" @click="goBack">
        <span>← 鸣金收兵</span>
      </button>
      <div class="nav-links">
        <a href="#oath" @click.prevent="scrollToSection('oath')" :class="{ active: activeSection === 'oath' }">桃园结义</a>
        <a href="#warlords" @click.prevent="scrollToSection('warlords')" :class="{ active: activeSection === 'warlords' }">群雄逐鹿</a>
        <a href="#redcliffs" @click.prevent="scrollToSection('redcliffs')" :class="{ active: activeSection === 'redcliffs' }">赤壁鏖兵</a>
        <a href="#threekingdoms" @click.prevent="scrollToSection('threekingdoms')" :class="{ active: activeSection === 'threekingdoms' }">三分天下</a>
        <a href="#unity" @click.prevent="scrollToSection('unity')" :class="{ active: activeSection === 'unity' }">九九归一</a>
      </div>
    </header>

    <!-- Background Elements (Dynamic) -->
    <div class="bg-layer ink-wash"></div>
    <div class="bg-layer clouds" :style="{ transform: `translateY(${scrollY * 0.2}px)` }"></div>
    <div class="bg-layer fire-particles" v-if="activeSection === 'redcliffs'"></div>

    <!-- Opening Poem -->
    <section id="opening" class="section opening-section">
      <div class="watermark-bg">三国演义</div>
      <div class="poem-container">
        <h1 class="main-title">滚滚长江东逝水</h1>
        <p class="poem-line">浪花淘尽英雄</p>
        <p class="poem-line">是非成败转头空</p>
        <p class="poem-line">青山依旧在</p>
        <p class="poem-line">几度夕阳红</p>
      </div>
      <div class="scroll-hint">▼ 阅 史 ▼</div>
    </section>

    <!-- 1. Oath (Peach Garden) -->
    <section id="oath" class="section oath-section" @click="advanceOath">
      <!-- Background Trees -->
      <div class="peach-forest-bg"></div>
      <!-- Falling Petals -->
      <div class="falling-petals">
        <div v-for="n in 100" :key="n" class="petal" :style="getPetalStyle()"></div>
      </div>
      
      <div class="content-wrapper">
        <h2 class="section-title">桃园结义</h2>
        <div class="oath-text-container">
          <p v-for="(line, index) in revealedOathLines" :key="index" class="oath-line fade-in-up">
            {{ line }}
          </p>
          <p v-if="revealedOathLines.length < oathLines.length" class="click-hint">
            (点击屏幕宣誓)
          </p>
        </div>
        
        <div class="peach-blossom-interactive" v-if="revealedOathLines.length === oathLines.length">
          <div class="brothers">
            <div class="brother liu" @click.stop="showBrotherDialog('liu')">
              刘
              <div class="brother-popup" v-if="activeBrother === 'liu'">{{ brotherDialog }}</div>
            </div>
            <div class="brother guan" @click.stop="showBrotherDialog('guan')">
              关
              <div class="brother-popup" v-if="activeBrother === 'guan'">{{ brotherDialog }}</div>
            </div>
            <div class="brother zhang" @click.stop="showBrotherDialog('zhang')">
              张
              <div class="brother-popup" v-if="activeBrother === 'zhang'">{{ brotherDialog }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 2. Warlords (Strategy/Chaos) -->
    <section id="warlords" class="section warlords-section">
      <div class="content-wrapper">
        <h2 class="section-title">群雄逐鹿</h2>
        <p class="narrative">"天下大势，分久必合，合久必分。"</p>
        <div class="map-grid">
          <div class="warlord-card-container" v-for="(lord, index) in warlords" :key="lord.name" @click="toggleWarlord(index)">
            <div class="warlord-card" :class="{ flipped: lord.flipped }">
              <div class="card-face card-front">
                <h3>{{ lord.name }}</h3>
              </div>
              <div class="card-face card-back">
                <h3>{{ lord.name }}</h3>
                <p class="motto">{{ lord.motto }}</p>
                <p class="desc">{{ lord.desc }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 3. Red Cliffs (War/Fire) -->
    <section id="redcliffs" class="section redcliffs-section">
      <div class="content-wrapper">
        <h2 class="section-title">赤壁鏖兵</h2>
        <p class="narrative">"羽扇纶巾，谈笑间，樯橹灰飞烟灭。"</p>
        
        <div class="interaction-zone">
          <button class="borrow-arrows-btn" @click="borrowArrows" :disabled="isShooting">
            草船借箭
          </button>
          <div class="fire-attack" @mouseenter="igniteFire" @click="igniteFire">
            <span>🔥 火烧连营</span>
          </div>
        </div>

        <!-- Flying Arrows Animation Container -->
        <div class="arrows-container">
          <div v-for="arrow in flyingArrows" :key="arrow.id" class="flying-arrow" :style="arrow.style">➵</div>
        </div>
        
        <!-- Fire Overlay -->
        <div class="fire-overlay" :class="{ active: isFireActive }">
           <div class="flame" v-for="n in 20" :key="n" :style="getFlameStyle()"></div>
        </div>
      </div>
    </section>

    <!-- 4. Three Kingdoms (Balance) -->
    <section id="threekingdoms" class="section three-kingdoms-section">
      
      <!-- Side Texts -->
      <div class="side-text left-text">
        <span>北让曹操占天时</span>
        <span>南让孙权占地利</span>
      </div>
      <div class="side-text right-text">
        <span>将军可占人和</span>
        <span>先取荆州为家</span>
      </div>

      <div class="content-wrapper">
        <h2 class="section-title">三分天下</h2>
        <div class="tri-force">
          <div class="kingdom wei">
            <span class="kingdom-name">魏</span>
            <div class="kingdom-info">
              <p>曹操</p>
              <p>五子良将</p>
              <p>据有中原</p>
            </div>
          </div>
          <div class="kingdom shu">
            <span class="kingdom-name">蜀</span>
            <div class="kingdom-info">
              <p>刘备</p>
              <p>五虎上将</p>
              <p>据有益州</p>
            </div>
          </div>
          <div class="kingdom wu">
            <span class="kingdom-name">吴</span>
            <div class="kingdom-info">
              <p>孙权</p>
              <p>十二虎臣</p>
              <p>据有江东</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 5. Unity (Ending) -->
    <section id="unity" class="section unity-section">
      <div class="content-wrapper">
        <h2 class="section-title">九九归一</h2>
        <p class="poem-line">古今多少事</p>
        <p class="poem-line">都付笑谈中</p>
        
        <div v-if="!hasRated" class="rating-kingdoms">
            <button v-if="!showRatingOptions" class="final-return-btn" @click="showRatingOptions = true">记录历史</button>
            <div v-else class="kingdom-options">
                <button class="kingdom-opt" @click="rateBook(0)">乱世 (0)</button>
                <button class="kingdom-opt" @click="rateBook(1)">分久必合 (1)</button>
                <button class="kingdom-opt" @click="rateBook(2)">天下归一 (2)</button>
            </div>
        </div>
        <button v-else class="final-return-btn" @click="goBack">鸣金收兵</button>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
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
      bookName: "三国演义",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('青梅煮酒');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '败走华容道');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('天机不可泄露');
  }
};

const scrollY = ref(0);
const activeSection = ref('opening');
const showNav = ref(false);
const isShooting = ref(false);
const isFireActive = ref(false);
const flyingArrows = ref<{id: number, style: any}[]>([]);
const activeBrother = ref<string | null>(null);
const brotherDialog = ref('');

// Oath Section Logic
const oathLines = [
  "念刘备、关羽、张飞，虽然异姓，既结为兄弟，则同心协力，救困扶危；",
  "上报国家，下安黎庶。不求同年同月同日生，只愿同年同月同日死。",
  "皇天后土，实鉴此心，背义忘恩，天人共戮！",
  "（誓毕，拜玄德为兄，关羽次之，张飞为弟。）"
];
const revealedOathLines = ref<string[]>([]);

const advanceOath = () => {
  if (revealedOathLines.value.length < oathLines.length) {
    revealedOathLines.value.push(oathLines[revealedOathLines.value.length]);
  }
};

const showBrotherDialog = (brother: string) => {
  activeBrother.value = brother;
  if (brother === 'liu') {
    brotherDialog.value = '二弟、三弟';
  } else if (brother === 'guan') {
    brotherDialog.value = '大哥、三弟';
  } else if (brother === 'zhang') {
    brotherDialog.value = '大哥、二哥';
  }
  
  setTimeout(() => {
    activeBrother.value = null;
  }, 2000);
};

const getPetalStyle = () => {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.7 + 0.3,
    transform: `scale(${Math.random() * 0.5 + 0.5}) rotate(${Math.random() * 360}deg)`
  };
};

const warlords = ref([
  { name: '曹操', seal: '魏', motto: '宁教我负天下人', desc: '挟天子以令诸侯，统一北方，魏武之强。', flipped: false },
  { name: '袁绍', seal: '袁', motto: '四世三公', desc: '出身名门，河北霸主，优柔寡断。', flipped: false },
  { name: '孙坚', seal: '吴', motto: '江东猛虎', desc: '勇猛刚烈，奠定东吴基业。', flipped: false },
  { name: '吕布', seal: '布', motto: '人中吕布', desc: '武艺天下第一，然有勇无谋，反复无常。', flipped: false },
]);

const toggleWarlord = (index: number) => {
  warlords.value[index].flipped = !warlords.value[index].flipped;
};

const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement;
  scrollY.value = target.scrollTop;
  
  // Show nav when scrolled past opening section
  showNav.value = target.scrollTop > window.innerHeight * 0.8;

  // Simple scroll spy
  const sections = ['opening', 'oath', 'warlords', 'redcliffs', 'threekingdoms', 'unity'];
  for (const id of sections) {
    const el = document.getElementById(id);
    if (el) {
      const rect = el.getBoundingClientRect();
      if (rect.top < window.innerHeight / 2 && rect.bottom > window.innerHeight / 2) {
        activeSection.value = id;
      }
    }
  }
};

const scrollToSection = (id: string) => {
  const el = document.getElementById(id);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
};

const goBack = () => {
  router.back();
};

const borrowArrows = () => {
  isShooting.value = true;
  const newArrows = Array.from({ length: 20 }, (_, i) => ({
    id: Date.now() + i,
    style: {
      top: `${Math.random() * 80 + 10}%`,
      left: '-50px',
      animationDuration: `${Math.random() * 1 + 0.5}s`,
      animationDelay: `${Math.random() * 0.5}s`
    }
  }));
  
  flyingArrows.value.push(...newArrows);

  // Clean up arrows
  setTimeout(() => {
    flyingArrows.value = flyingArrows.value.filter(a => !newArrows.includes(a));
    isShooting.value = false;
  }, 2000);
};

const igniteFire = () => {
  isFireActive.value = true;
  setTimeout(() => {
    isFireActive.value = false;
  }, 3000);
};

const getFlameStyle = () => {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 0.5}s`,
    animationDuration: `${Math.random() * 1 + 1}s`,
    height: `${Math.random() * 100 + 50}px`
  };
};

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&family=Noto+Serif+SC:wght@400;700&display=swap');

/* Base Layout */
* {
  box-sizing: border-box;
}

.three-kingdoms-container {
  height: 100vh;
  overflow-y: auto;
  scroll-behavior: smooth;
  scroll-snap-type: y mandatory; /* Scroll Snapping */
  background-color: #f0ebe5; /* Rice Paper */
  color: #1a1a1a;
  font-family: 'Noto Serif SC', serif;
  position: relative;
}

/* Typography */
h1, h2, h3, .oath-line {
  font-family: 'Ma Shan Zheng', cursive;
}

.main-title {
  font-size: 4rem;
  margin-bottom: 2rem;
  writing-mode: vertical-rl;
  text-orientation: upright;
  height: 300px;
}

.section-title {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  border-bottom: 3px solid #8E2323;
  display: inline-block;
  padding-bottom: 0.5rem;
}

.narrative {
  font-size: 1.2rem;
  max-width: 600px;
  margin: 0 auto 2rem;
  line-height: 1.8;
  color: #4a4a4a;
}

.poem-line {
  font-size: 1.5rem;
  margin: 1rem 0;
  font-family: 'Ma Shan Zheng', cursive;
}

/* Navigation (Dune Style Refined) */
.tk-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px; /* Fixed height for better centering calculation */
  background: rgba(240, 235, 229, 0.95);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  z-index: 100;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0; /* Remove padding to avoid flex/centering issues */
}

.tk-nav.visible {
  transform: translateY(0);
}

.nav-return-btn {
  position: absolute;
  left: 40px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: 1px solid #8E2323;
  color: #8E2323;
  padding: 5px 15px;
  font-family: 'Noto Serif SC', serif;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-return-btn:hover {
  background: #8E2323;
  color: #fff;
  box-shadow: 0 0 10px rgba(142, 35, 35, 0.3);
}

.nav-links {
  display: flex;
  gap: 3rem;
  margin: 0;
  padding: 0;
}

.nav-links a {
  text-decoration: none;
  color: #555;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  opacity: 0.6;
  transition: all 0.3s;
  position: relative;
  font-family: 'Noto Serif SC', serif;
}

.nav-links a:hover, .nav-links a.active {
  opacity: 1;
  color: #8E2323;
}

.nav-links a.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #8E2323;
  transform: scaleX(1);
  transition: transform 0.3s;
}

.withdraw-btn, .final-return-btn {
  background: #1a1a1a;
  color: #fff;
  border: none;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-family: 'Noto Serif SC', serif;
}

/* Sections */
.section {
  height: 100vh;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  position: relative;
  padding: 4rem 2rem;
  scroll-snap-align: start;
  scroll-snap-stop: always;
  overflow: hidden;
  box-sizing: border-box; /* Explicitly ensure border-box */
}

/* Opening */
.opening-section {
  background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M0 50 Q 25 25, 50 50 T 100 50' fill='none' stroke='%23d0d0d0' stroke-width='2'/%3E%3C/svg%3E");
  background-size: 200px 100px;
  position: relative;
  overflow: hidden;
}

.watermark-bg {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-family: 'Ma Shan Zheng', cursive;
  font-size: 15rem;
  color: rgba(123, 2, 2, 0.04);
  z-index: 0;
  pointer-events: none;
  white-space: nowrap;
  letter-spacing: 1rem;
  writing-mode: horizontal-tb; /* Ensure it's horizontal */
}

.poem-container {
  position: relative;
  z-index: 1;
}

/* Oath */
.oath-section {
  background: radial-gradient(circle, #fff0f5 0%, #f0ebe5 80%);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

/* Removed peach-forest-bg */

.falling-petals {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.petal {
  position: absolute;
  top: -10px;
  width: 10px; height: 14px;
  background: #ffb7b2;
  border-radius: 100% 0 100% 0;
  opacity: 0.6;
  animation: fall 8s linear infinite;
}

@keyframes fall {
  to { transform: translate(50px, 100vh) rotate(720deg); opacity: 0; }
}

.oath-text-container {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.oath-line {
  font-size: 1.5rem;
  color: #2c1e15;
  font-weight: bold;
  font-family: 'Noto Serif SC', serif;
}

.click-hint {
  font-size: 0.9rem;
  color: #999;
  animation: pulse 2s infinite;
  margin-top: 1rem;
}

@keyframes pulse { 0%, 100% { opacity: 0.5; } 50% { opacity: 1; } }

.fade-in-up {
  animation: fadeInUp 0.5s ease-out forwards;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.brothers {
  display: flex;
  gap: 4rem;
  justify-content: center;
  margin-top: 2rem;
  animation: zoomIn 0.5s ease-out;
}

@keyframes zoomIn { from { transform: scale(0); } to { transform: scale(1); } }

.brother {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid #1a1a1a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  transition: transform 0.3s;
  position: relative;
  cursor: pointer;
}

.brother:hover {
  transform: scale(1.1);
}

.brother-popup {
  position: absolute;
  top: -60px;
  left: 50%;
  transform: translateX(-50%);
  background: #fff;
  border: 1px solid #8E2323;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 1rem;
  white-space: nowrap;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  z-index: 10;
  animation: fadeIn 0.2s;
}

.brother-popup::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 6px 6px 0;
  border-style: solid;
  border-color: #8E2323 transparent transparent transparent;
}

@keyframes fadeIn { from { opacity: 0; transform: translateX(-50%) translateY(10px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }

.brother.liu { color: #8E2323; }
.brother.guan { color: #2c3e50; }
.brother.zhang { color: #000; }

/* Warlords */
.warlords-section {
  background-color: #000;
  color: #fff;
}

.warlords-section .section-title {
  border-bottom-color: #fff;
}

.warlords-section .narrative {
  color: #ccc;
}

.map-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  margin-top: 2rem;
}

.warlord-card-container {
  width: 220px;
  height: 300px;
  perspective: 1000px;
  cursor: pointer;
}

.warlord-card {
  width: 100%;
  height: 100%;
  position: relative;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.warlord-card.flipped {
  transform: rotateY(180deg);
}

.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  box-shadow: 0 4px 15px rgba(255,255,255,0.1);
  border: 1px solid #444;
}

.card-front {
  background: #1a1a1a;
  color: #fff;
}

.card-front h3 {
  font-size: 2.5rem;
  margin: 0;
}

.card-back {
  background: #fff;
  color: #1a1a1a;
  transform: rotateY(180deg);
  text-align: left;
  align-items: flex-start;
}

.card-back h3 {
  margin-bottom: 1rem;
  border-bottom: 2px solid #8E2323;
  width: 100%;
  padding-bottom: 0.5rem;
}

.card-back .motto {
  font-weight: bold;
  color: #8E2323;
  margin-bottom: 0.5rem;
}

.card-back .desc {
  font-size: 0.9rem;
  line-height: 1.5;
}

.redcliffs-section {
  background: linear-gradient(to bottom, #2c1e15, #5a1a1a);
  color: #fff;
}

.redcliffs-section .section-title {
  border-color: #ff4d4d;
  color: #ff4d4d;
}

.redcliffs-section .narrative {
  color: #ddd;
}

.borrow-arrows-btn {
  background: #C5A059;
  border: none;
  padding: 1rem 2rem;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  margin-right: 1rem;
  transition: background 0.2s;
}

.borrow-arrows-btn:hover {
  background: #e6bf70;
}

.fire-attack {
  display: inline-block;
  padding: 1rem 2rem;
  border: 2px solid #ff4d4d;
  color: #ff4d4d;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.fire-attack:hover {
  background: #ff4d4d;
  color: #fff;
  box-shadow: 0 0 20px #ff4d4d;
}

/* Flying Arrows */
.arrows-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.flying-arrow {
  position: absolute;
  font-size: 2rem;
  color: #fff; /* White arrows */
  animation: flyRight 1s linear forwards;
  opacity: 0.9;
  text-shadow: 0 0 5px rgba(0,0,0,0.5);
}

.fire-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.5s;
  background: radial-gradient(circle at center, rgba(255, 69, 0, 0.2), transparent);
}

.fire-overlay.active {
  opacity: 1;
}

.flame {
  position: absolute;
  bottom: 0;
  width: 20px;
  background: linear-gradient(to top, #ff4500, #ff8c00, transparent);
  border-radius: 50% 50% 0 0;
  filter: blur(4px);
  animation: burn 1s infinite alternate;
}

@keyframes burn {
  0% { transform: scaleY(1); opacity: 0.8; }
  100% { transform: scaleY(1.2) translate(5px, -10px); opacity: 1; }
}

.map-bg {
  display: none;
}

.side-text {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  writing-mode: vertical-rl;
  text-orientation: upright;
  font-family: 'Ma Shan Zheng', cursive;
  font-size: 1.8rem;
  color: #555;
  height: 60%;
  display: flex;
  gap: 1.5rem;
  opacity: 0.8;
}

.left-text { left: 5%; }
.right-text { right: 5%; }

@keyframes flyRight {
  from { transform: translateX(0); }
  to { transform: translateX(120vw); }
}

/* Three Kingdoms */
.three-kingdoms-section {
  background-color: #f0ebe5;
}

.tri-force {
  position: relative;
  width: 600px;
  height: 400px;
  margin: 0 auto;
}

.kingdom {
  position: absolute;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
  transition: transform 0.3s;
  cursor: default;
}

.kingdom:hover {
  transform: scale(1.05);
  z-index: 10;
}

.kingdom-name {
  font-size: 3rem;
  font-weight: bold;
  font-family: 'Ma Shan Zheng', cursive;
  margin-bottom: 0.5rem;
}

.kingdom-info {
  font-size: 0.8rem;
  line-height: 1.4;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.kingdom-info p {
  margin: 0;
}

.kingdom.wei {
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #2c3e50, #4a698a);
}

.kingdom.shu {
  bottom: 20px;
  left: 50px;
  background: linear-gradient(135deg, #8E2323, #c0392b);
}

.kingdom.wu {
  bottom: 20px;
  right: 50px;
  background: linear-gradient(135deg, #2f552f, #556b2f);
}

/* Unity */
.unity-section {
  background: #fff;
}

/* Ink Wash Background (Global) */
.bg-layer {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0;
}

.ink-wash {
  opacity: 0.05;
  background-image: url("data:image/svg+xml,%3Csvg width='400' height='400' viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='f'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.01' numOctaves='3'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23f)'/%3E%3C/svg%3E");
}

/* Scrollbar */
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-track {
  background: #f0ebe5;
}
::-webkit-scrollbar-thumb {
  background: #8E2323;
  border-radius: 4px;
}
</style>
