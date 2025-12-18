<template>
  <div class="amusing-page" ref="pageContainer" @scroll="handleScroll">
    <!-- Top Left Return Button -->
    <button class="return-btn fixed-top-left" @click="turnOffTv">
      <span>← 关闭电视</span>
    </button>

    <!-- CRT Overlay -->
    <div class="crt-overlay"></div>
    <div class="scanlines"></div>

    <!-- Transition Overlay (Intro) -->
    <Transition name="noise-implode">
      <div v-if="showIntro" class="intro-overlay">
        <div class="white-noise"></div>
      </div>
    </Transition>

    <!-- VCR Navigation -->
    <nav class="vcr-nav" :class="{ visible: !showIntro }">
      <div class="vcr-buttons">
        <button class="vcr-btn" @click="scrollTo('hero')">
          <span class="icon">▶</span> 播放
        </button>
        <button class="vcr-btn" @click="scrollTo('prophecy')">
          <span class="icon">❚❚</span> 暂停
        </button>
        <button class="vcr-btn" @click="scrollTo('decay')">
          <span class="icon">⏪</span> 倒带
        </button>
        <button class="vcr-btn highlight" @click="scrollTo('shallows')">
          <span class="icon">⏩</span> 快进
        </button>
      </div>
      <div class="channel-display">频道 03: 娱乐至死</div>
    </nav>

    <!-- Main Content -->
    <main class="main-scroller">
      
      <!-- Section 1: The Medium is the Metaphor (Hero) -->
      <section id="hero" class="section hero-section">
        <div class="monitor-wall" @mousemove="handleMagnet">
          <div 
            v-for="n in 16" 
            :key="n" 
            class="monitor"
            :style="getMonitorStyle(n)"
          >
            <div class="monitor-screen">
              <div class="screen-content" :class="`content-${n % 4}`"></div>
              <div class="screen-glare"></div>
            </div>
          </div>
        </div>
        <div class="hero-title-container">
          <h1 class="hero-title glitch-layers" data-text="娱乐至死">
            娱乐至死
          </h1>
          <p class="subtitle">媒介即隐喻</p>
        </div>
      </section>

      <!-- Section 2: Orwell vs Huxley (The Prophecy) -->
      <section id="prophecy" class="section prophecy-section" ref="prophecySection">
        <div class="split-screen">
          <!-- Orwell (Left) -->
          <div class="side orwell-side">
            <div class="side-content">
              <h2>1984</h2>
              <div class="prison-bars"></div>
              <p>老大哥在看着你</p>
              <div class="camera-icon">🎥</div>
              <p class="fear-text">恐惧痛苦</p>
            </div>
          </div>
          
          <!-- Huxley (Right) - Expands on Scroll -->
          <div class="side huxley-side" :style="{ width: huxleyWidth + '%' }">
            <div class="side-content">
              <h2>美丽新世界</h2>
              <div class="confetti-bg"></div>
              <p>狂欢-狂欢！</p>
              <div class="pill-icon">💊</div>
              <p class="pleasure-text">恐惧欢愉</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 3: Typographic Decay -->
      <section id="decay" class="section decay-section" ref="decaySection">
        <div class="decay-content">
          <div class="stable-text">
            <h3>演艺娱乐时代</h3>
          </div>
          <div class="decaying-paragraph">
            <span 
              v-for="(word, index) in decayWords" 
              :key="index" 
              class="decay-word"
              :style="getDecayStyle(index)"
            >
              {{ word }}
            </span>
          </div>
        </div>
      </section>

      <!-- Section 4: Peek-a-Boo World -->
      <section id="peekaboo" class="section peekaboo-section">
        <h2 class="section-header">躲猫猫的世界</h2>
        <div class="collage-container">
          <div 
            v-for="(item, index) in popupItems" 
            :key="index"
            class="popup-window"
            :style="item.style"
            @click="explodeItem(index)"
            v-show="!item.exploded"
          >
            <div class="window-header">
              <span>{{ item.title }}</span>
              <div class="win-btns"><span class="win-btn close"></span></div>
            </div>
            <div class="window-body">
              {{ item.content }}
            </div>
          </div>
        </div>
      </section>

      <!-- Section 5: The Shallows -->
      <section id="shallows" class="section shallows-section">
        <div class="static-storm" :style="{ opacity: staticOpacity }"></div>
        <div class="power-container" v-if="!isPoweredOff">
          
          <!-- Rating Interface -->
          <div v-if="!hasRated" class="rating-interface-crt">
            <div v-if="!showRatingOptions" class="rating-start">
               <button class="crt-text-btn blink-text" @click="showRatingOptions = true">
                 &gt; 评价广播_
               </button>
            </div>
            <div v-else class="rating-options-crt">
              <div class="crt-scanline-overlay"></div>
              <button class="crt-opt-btn" @click="rateBook(0)">
                <span class="channel-num">频道 0</span> <span class="opt-text">无信号</span>
              </button>
              <button class="crt-opt-btn" @click="rateBook(1)">
                <span class="channel-num">频道 1</span> <span class="opt-text">干扰</span>
              </button>
              <button class="crt-opt-btn highlight" @click="rateBook(2)">
                <span class="channel-num">频道 2</span> <span class="opt-text">清晰</span>
              </button>
            </div>
          </div>

          <!-- Original Exit -->
          <div v-else class="exit-control">
            <button class="power-btn" @click="turnOffTv">
              <div class="power-symbol">⏻</div>
            </button>
            <p>好... 现在</p>
          </div>

        </div>
        <div class="tv-off-animation" v-else>
          <div class="collapse-line"></div>
        </div>
      </section>

    </main>

    <!-- Breaking News Ticker -->
    <footer class="news-ticker" :style="{ '--scroll-speed': tickerSpeed + 's' }">
      <div class="ticker-wrap">
        <!-- Set 1 -->
        <div class="ticker-item">突发新闻：猫咪视频治愈抑郁症 • </div>
        <div class="ticker-item">全球智商下降 50 点 • </div>
        <div class="ticker-item">哈基米南北绿豆 • </div>
        <div class="ticker-item">新款 iPhone 植入大脑 • </div>
        <div class="ticker-item">现实因收视率过低被取消 • </div>
        <div class="ticker-item">专家建议：思考会导致脱发 • </div>
        <div class="ticker-item">书籍已过时，请直接下载知识 • </div>
        <div class="ticker-item">植入式广告 • </div>
        <div class="ticker-item">睡眠现已按分钟收费 • </div>
        <div class="ticker-item">历史书因“太无聊”被重写 • </div>
        <div class="ticker-item">甚至连这个滚动条都是假的 • </div>
        <div class="ticker-item">仿生人到底会不会梦到电子羊 • </div>
        <div class="ticker-item">一！五！ • </div>
        <div class="ticker-item">不要整你的现实阿西嘎亚库那路 • </div>
        <div class="ticker-item">老大哥在看着你的浏览器历史 • </div>
        
        <!-- Duplicate for loop -->
        <div class="ticker-item">突发新闻：猫咪视频治愈抑郁症 • </div>
        <div class="ticker-item">突发新闻：猫咪视频治愈抑郁症 • </div>
        <div class="ticker-item">全球智商下降 50 点 • </div>
        <div class="ticker-item">总统向“沉默”宣战 • </div>
        <div class="ticker-item">新款 iPhone 植入大脑 • </div>
        <div class="ticker-item">现实因收视率过低被取消 • </div>
        <div class="ticker-item">专家建议：思考会导致脱发 • </div>
        <div class="ticker-item">书籍已过时，请直接下载知识 • </div>
        <div class="ticker-item">你的梦想包含植入式广告 • </div>
        <div class="ticker-item">睡眠现已按分钟收费 • </div>
        <div class="ticker-item">历史书因“太无聊”被重写 • </div>
        <div class="ticker-item">甚至连这个滚动条都是假的 • </div>
        <div class="ticker-item">1984 不是说明书 • </div>
        <div class="ticker-item">赫胥黎是对的 • </div>
        <div class="ticker-item">请不要调整你的现实 • </div>
        <div class="ticker-item">老大哥在看着你的浏览器历史 • </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, onUnmounted } from 'vue';
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
  if (!username) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  try {
    const res = await myAxios.put('/bookLike/like', {
      userName: username,
      bookId: 11, // Amusing Ourselves to Death ID
      bookName: "娱乐至死",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('信号已接收');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '信号干扰');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('传输失败');
  }
};

const goBackHome = () => {
  router.back();
};

const pageContainer = ref<HTMLElement | null>(null);
const showIntro = ref(true);
const scrollY = ref(0);

// --- Transition ---
onMounted(() => {
  setTimeout(() => {
    showIntro.value = false;
  }, 800); // Intro duration
});

// --- Scroll Logic ---
const handleScroll = () => {
  if (!pageContainer.value) return;
  scrollY.value = pageContainer.value.scrollTop;
  
  // Update Ticker Speed (Inverse relationship: faster scroll = smaller duration)
  // Base 20s, min 2s
  const speed = Math.max(2, 20 - (scrollY.value / 100));
  tickerSpeed.value = speed;

  calculateHuxleyWidth();
  calculateStaticOpacity();
};

const scrollTo = (id: string) => {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
};

// --- Hero: Monitor Wall ---
const getMonitorStyle = (n: number) => {
  // Random subtle offsets for "Magnet" effect placeholder
  return {
    animationDelay: `${n * 0.1}s`
  };
};

const handleMagnet = (e: MouseEvent) => {
  // Magnet effect logic could go here (altering CSS vars)
};

// --- Prophecy: Split Screen ---
const prophecySection = ref<HTMLElement | null>(null);
const huxleyWidth = ref(0);

const calculateHuxleyWidth = () => {
  if (!prophecySection.value || !pageContainer.value) return;
  const rect = prophecySection.value.getBoundingClientRect();
  const viewHeight = window.innerHeight;
  
  // Start expanding when section enters view
  if (rect.top < viewHeight && rect.bottom > 0) {
    const progress = (viewHeight - rect.top) / (viewHeight + rect.height);
    // Map progress 0-1 to width 0-100
    huxleyWidth.value = Math.min(100, Math.max(0, progress * 150 - 25)); 
  }
};

// --- Decay: Typographic ---
const decaySection = ref<HTMLElement | null>(null);
const text = "当一个民族因琐事而分心，当文化生活被重新定义为永无止境的娱乐循环，当严肃的公共对话变成一种婴儿语言，简而言之，当人民成为观众，他们的公共事务成为杂耍表演，那么一个民族就会发现自己处于危险之中；文化消亡已是清晰的可能性。";
const decayWords = text.split('');

const getDecayStyle = (index: number) => {
  if (!decaySection.value) return {};
  const rect = decaySection.value.getBoundingClientRect();
  const triggerPoint = window.innerHeight * 0.5;
  
  if (rect.top < triggerPoint) {
    const depth = (triggerPoint - rect.top);
    // Random fall physics based on index hash
    const randomX = (index % 5 - 2) * 50;
    const randomRotate = (index % 10 - 5) * 45;
    
    // Stagger the fall
    const threshold = index * 20; 
    if (depth > threshold) {
        return {
            transform: `translate(${randomX}px, ${depth - threshold + 100}px) rotate(${randomRotate + depth}deg)`,
            opacity: Math.max(0, 1 - depth/1000),
            color: index % 2 === 0 ? '#ff00de' : '#00ffff' // Turn to RGB pixels
        };
    }
  }
  return {};
};

// --- Peek-a-Boo ---
const popupItems = reactive([
  { title: "广告", content: "立即购买幸福！", style: { top: '10%', left: '10%' }, exploded: false },
  { title: "警报", content: "您的注意力持续时间过低", style: { top: '30%', right: '15%' }, exploded: false },
  { title: "热点八卦", content: "某名人吃了个三明治", style: { top: '50%', left: '20%' }, exploded: false },
  { title: "中奖啦", content: "您是第1,000,000位访客", style: { top: '15%', right: '30%' }, exploded: false },
  { title: "系统", content: "需要更新", style: { bottom: '20%', left: '40%' }, exploded: false },
  { title: "网络迷因", content: "哈哈", style: { bottom: '10%', right: '10%' }, exploded: false },
  // Added items
  { title: "警告", content: "检测到思想犯罪", style: { top: '40%', left: '5%' }, exploded: false },
  { title: "特价", content: "自由仅售 $9.99", style: { bottom: '30%', right: '5%' }, exploded: false },
  { title: "通知", content: "老大哥关注了你", style: { top: '5%', left: '40%' }, exploded: false },
  { title: "娱乐", content: "请勿思考", style: { bottom: '5%', left: '15%' }, exploded: false },
  { title: "错误", content: "现实未找到", style: { top: '60%', right: '25%' }, exploded: false },
  { title: "直播", content: "有人正在睡觉", style: { top: '25%', left: '30%' }, exploded: false },
  { title: "抽奖", content: "赢取新的身份", style: { bottom: '40%', right: '40%' }, exploded: false },
  { title: "突发", content: "这行字是红色的", style: { top: '70%', left: '50%' }, exploded: false },
  { title: "提示", content: "向左滑动以忽略真理", style: { top: '20%', right: '5%' }, exploded: false },
  { title: "社交", content: "100人喜欢了你的孤独", style: { bottom: '15%', left: '60%' }, exploded: false },
  { title: "下载", content: "更多多巴胺", style: { top: '80%', left: '10%' }, exploded: false },
  { title: "隐私", content: "我们已知道一切", style: { top: '45%', right: '45%' }, exploded: false },
]);

const explodeItem = (index: number) => {
  popupItems[index].exploded = true;
  // Check if all exploded
  if (popupItems.every(item => item.exploded)) {
    alert("恭喜！你赢得了... 虚无！\n(Congratulations! You won... nothing!)");
    // Optional: Reset or cause chaos
    setTimeout(() => {
        popupItems.forEach(item => item.exploded = false);
    }, 2000);
  }
};

// --- Shallows ---
const staticOpacity = ref(0);
const isPoweredOff = ref(false);

const calculateStaticOpacity = () => {
    // Increase static as we reach bottom
    if (!pageContainer.value) return;
    const scrollHeight = pageContainer.value.scrollHeight - window.innerHeight;
    const current = pageContainer.value.scrollTop;
    if (current > scrollHeight - 500) {
        staticOpacity.value = (current - (scrollHeight - 500)) / 500;
    } else {
        staticOpacity.value = 0;
    }
};

const turnOffTv = () => {
    isPoweredOff.value = true;
    setTimeout(() => {
        router.back();
    }, 2000);
};

// --- Ticker ---
const tickerSpeed = ref(20);

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=VT323&family=Press+Start+2P&display=swap');

.amusing-page {
  --font-pixel: 'Press Start 2P', cursive;
  --font-mono: 'VT323', monospace;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
}

.amusing-page {
  width: 100%;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #1a1a1a;
  color: #fff;
  font-family: var(--font-mono);
  position: relative;
  cursor: crosshair;
}

/* Typography Updates */
.hero-title {
  font-family: var(--font-pixel);
  letter-spacing: -2px;
}

.subtitle, .side h2, .section-title, .stable-text h3 {
  font-family: var(--font-ui);
  font-weight: 900;
}

.fear-text, .pleasure-text, .decaying-paragraph, .intro-text {
  font-family: var(--font-text);
}

.vcr-btn, .channel-display, .return-btn, .footer-btn {
  font-family: var(--font-ui);
}

/* Numbers */
.channel-display {
  font-family: var(--font-mono);
  font-size: 1.2rem;
}

/* ... existing styles ... */

/* Return Buttons */
.return-btn.fixed-top-left {
  position: fixed;
  top: 100px;
  left: 24px;
  z-index: 2000;
  background: #000;
  border: 2px solid #00ff00;
  padding: 10px 20px;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #00ff00;
  text-shadow: 0 0 5px #00ff00;
  box-shadow: 0 0 5px #00ff00;
}

.return-btn.fixed-top-left:hover {
  background: #003300;
  box-shadow: 0 0 15px #00ff00;
}

.footer-btn {
  margin-top: 20px;
  background: transparent;
  border: 2px solid #ff0055;
  color: #ff0055;
  padding: 15px 40px;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
  text-shadow: 0 0 5px #ff0055;
  box-shadow: 0 0 5px #ff0055;
  text-transform: uppercase;
}

.footer-btn:hover {
  background: #ff0055;
  color: #000;
  box-shadow: 0 0 20px #ff0055;
}

/* CRT Overlay */
.crt-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: radial-gradient(circle, rgba(18,16,16,0) 50%, rgba(0,0,0,0.4) 100%), linear-gradient(rgba(18,16,16,0) 50%, rgba(0,0,0,0.1) 50%);
  background-size: 100% 100%, 100% 4px;
  pointer-events: none;
  z-index: 999;
}

.scanlines {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: linear-gradient(rgba(18,16,16,0) 50%, rgba(0,0,0,0.1) 50%);
    background-size: 100% 4px;
    pointer-events: none;
    z-index: 998;
}

/* Transition Intro */
.intro-overlay {
    position: fixed;
    top: 0; left: 0; width: 100%; height: 100%;
    background: #fff;
    z-index: 2000;
    display: flex;
    justify-content: center;
    align-items: center;
}

.white-noise {
    width: 100%; height: 100%;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='1'/%3E%3C/svg%3E");
    animation: noise 0.2s steps(4) infinite;
}

.noise-implode-leave-active {
  transition: transform 0.5s ease-in, opacity 0.5s ease-in;
}

.noise-implode-leave-to {
  transform: scale(0);
  opacity: 0;
}

/* VCR Nav */
.vcr-nav {
    position: fixed;
    top: 0;
    width: 100%;
    height: 60px;
    background: #111;
    border-bottom: 2px solid #333;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    z-index: 1000;
    transform: translateY(-100%);
    transition: transform 0.5s ease;
}

.vcr-nav.visible {
    transform: translateY(0);
}

.vcr-buttons {
    display: flex;
    gap: 15px;
}

.vcr-btn {
    background: #222;
    border: 2px solid #444;
    color: #888;
    padding: 5px 15px;
    font-family: 'Press Start 2P', cursive;
    font-size: 0.7rem;
    cursor: pointer;
    box-shadow: inset 1px 1px 0 #555, inset -1px -1px 0 #000;
}

.vcr-btn:active {
    box-shadow: inset 1px 1px 0 #000;
}

.vcr-btn.highlight {
    color: #ff00de;
    border-color: #ff00de;
}

.channel-display {
    color: #00ff00;
    font-size: 1.5rem;
    text-shadow: 0 0 5px #00ff00;
    text-align: right;
    margin-left: auto; /* Ensure it stays on the right */
    padding-right: 20px;
    white-space: nowrap;
}

/* Ticker */
.news-ticker {
    position: fixed;
    bottom: 0;
    width: 100%;
    height: 60px; /* Increased height significantly */
    background: #ffff00;
    color: #000;
    z-index: 1000;
    overflow: hidden;
    display: flex;
    align-items: center;
    font-weight: bold;
    font-size: 1.2rem; /* Increased font size */
    border-top: 4px solid #000;
}

.ticker-wrap {
    display: flex;
    white-space: nowrap;
    animation: ticker var(--scroll-speed) linear infinite;
}

@keyframes ticker {
    0% { transform: translateX(0); }
    100% { transform: translateX(-50%); }
}

/* Section 1: Hero */
.hero-section {
    height: 100vh;
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
}

.monitor-wall {
    position: absolute;
    top: -10%; left: -10%;
    width: 120%; height: 120%;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    grid-template-rows: repeat(4, 1fr);
    gap: 5px;
    z-index: 1;
    filter: brightness(0.4);
}

.monitor {
    background: #000;
    border: 2px solid #333;
    border-radius: 10px;
    overflow: hidden;
    position: relative;
}

.screen-content {
    width: 100%; height: 100%;
    opacity: 0.5;
}

.content-0 { background: repeating-linear-gradient(45deg, #222, #222 10px, #333 10px, #333 20px); }
.content-1 { background: repeating-radial-gradient(circle, #222, #222 10px, #444 10px, #444 20px); }
.content-2 { background: #111; }
.content-3 { background: repeating-linear-gradient(90deg, #222, #222 5px, #111 5px, #111 10px); }

.hero-title-container {
    z-index: 2;
    text-align: center;
    mix-blend-mode: hard-light;
}

.hero-title {
    font-family: 'Microsoft YaHei', sans-serif;
    font-size: 10rem;
    line-height: 1.2;
    color: #fff;
    text-shadow: 4px 4px 0 #ff00de, -4px -4px 0 #00ffff;
    animation: glitch-skew 2s infinite;
}

.subtitle {
    font-family: 'Microsoft YaHei', sans-serif;
    font-size: 4rem;
    margin-top: 20px;
    color: #ffff00;
    background: #000;
    display: inline-block;
    padding: 5px 15px;
    line-height: 1;
}

/* Section 2: Prophecy */
.prophecy-section {
    height: 150vh; /* Extra height for scroll effect */
    position: relative;
    background: #000;
}

.split-screen {
    position: sticky;
    top: 60px; /* Below nav */
    height: calc(100vh - 100px);
    display: flex;
    width: 100%;
    overflow: hidden;
}

.side {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    transition: width 0.1s linear;
}

.orwell-side {
    flex: 1;
    background: #222;
    color: #888;
    border-right: 2px solid #fff;
}

.huxley-side {
    width: 0%; /* Starts at 0 */
    background: #ff00de;
    color: #fff;
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 2;
}

.side-content {
    text-align: center;
    padding: 20px;
    min-width: 300px; /* Prevent text wrap during resize */
}

.side-content h2 {
    font-size: 3rem;
    margin-bottom: 20px;
}

.camera-icon, .pill-icon {
    font-size: 5rem;
    margin: 20px 0;
}

/* Section 3: Decay */
.decay-section {
    min-height: 150vh;
    padding: 100px 20px;
    background: #eee;
    color: #000;
    position: relative;
}

.decay-content {
    max-width: 800px;
    margin: 0 auto;
}

.stable-text h3 {
    font-family: 'Times New Roman', 'SimSun', serif;
    font-size: 3rem;
    margin-bottom: 50px;
    border-bottom: 2px solid #000;
}

.decaying-paragraph {
    font-family: 'Times New Roman', 'SimSun', serif;
    font-size: 1.5rem;
    line-height: 1.6;
}

.decay-word {
    display: inline-block;
    margin-right: 8px;
    transition: transform 0.1s ease-out, opacity 0.1s;
}

/* Section 4: Peekaboo */
.peekaboo-section {
    min-height: 100vh;
    background: #0000ff; /* Blue screen blue */
    position: relative;
    overflow: hidden;
}

.section-header {
    text-align: center;
    color: #fff;
    font-size: 4rem;
    padding-top: 50px;
}

.popup-window {
    position: absolute;
    background: #ccc;
    border: 2px solid #fff;
    box-shadow: 5px 5px 0 #000;
    width: 250px;
    color: #000;
    cursor: pointer;
    transition: transform 0.2s;
}

.popup-window:hover {
    transform: scale(1.1);
    z-index: 10;
}

.window-header {
    background: linear-gradient(90deg, #000080, #1084d0);
    color: #fff;
    padding: 5px;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
}

.window-body {
    padding: 20px;
    font-size: 1.2rem;
    text-align: center;
}

/* Section 5: Shallows */
.shallows-section {
    height: 100vh;
    background: #000;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
}

.static-storm {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.6' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='1'/%3E%3C/svg%3E");
    pointer-events: none;
    z-index: 1;
}

.power-btn {
    background: transparent;
    border: none;
    color: #ff0000;
    font-size: 5rem;
    cursor: pointer;
    z-index: 2;
    animation: pulse 2s infinite;
}

.power-btn:hover {
    color: #fff;
    text-shadow: 0 0 20px #fff;
}

.power-container p {
    color: #666;
    margin-top: 20px;
    font-size: 1.5rem;
}

.tv-off-animation {
    position: fixed;
    top: 0; left: 0; width: 100%; height: 100%;
    background: #000;
    z-index: 2000;
    display: flex;
    justify-content: center;
    align-items: center;
}

.collapse-line {
    width: 100%;
    height: 2px;
    background: #fff;
    animation: tv-off 0.5s forwards;
}

@keyframes tv-off {
    0% { transform: scaleX(1) scaleY(1); }
    50% { transform: scaleX(1) scaleY(0.01); }
    100% { transform: scaleX(0) scaleY(0.01); background: #fff; }
}

@keyframes pulse {
    0% { transform: scale(1); opacity: 0.8; }
    50% { transform: scale(1.1); opacity: 1; }
    100% { transform: scale(1); opacity: 0.8; }
}

@keyframes glitch-skew {
    0% { transform: skew(0deg); }
    20% { transform: skew(-10deg); }
    40% { transform: skew(10deg); }
    60% { transform: skew(-5deg); }
    80% { transform: skew(5deg); }
    100% { transform: skew(0deg); }
}

/* Scrollbar Hide */
.amusing-page::-webkit-scrollbar { display: none; }
.amusing-page { -ms-overflow-style: none; scrollbar-width: none; }

/* Rating Interface Styles */
.rating-interface-crt {
    z-index: 10;
    position: relative;
    text-align: center;
}

.crt-text-btn {
    background: transparent;
    border: none;
    color: #0f0;
    font-family: 'Courier New', Courier, monospace;
    font-size: 2rem;
    cursor: pointer;
    text-shadow: 0 0 5px #0f0;
    letter-spacing: 2px;
}

.crt-text-btn:hover {
    color: #fff;
    text-shadow: 0 0 10px #fff;
}

.rating-options-crt {
    display: flex;
    flex-direction: column;
    gap: 15px;
    background: rgba(0, 20, 0, 0.9);
    padding: 20px;
    border: 2px solid #0f0;
    box-shadow: 0 0 15px #0f0, inset 0 0 10px #0f0;
    position: relative;
}

.crt-opt-btn {
    background: transparent;
    border: 1px solid #0f0;
    color: #0f0;
    padding: 10px 20px;
    font-family: 'Courier New', monospace;
    font-size: 1.2rem;
    cursor: pointer;
    text-align: left;
    display: flex;
    justify-content: space-between;
    width: 300px;
    transition: all 0.2s;
}

.crt-opt-btn:hover {
    background: #0f0;
    color: #000;
    box-shadow: 0 0 10px #0f0;
}

.channel-num {
    font-weight: bold;
}

.exit-control {
    display: flex;
    flex-direction: column;
    align-items: center;
}
</style>