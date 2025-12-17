<template>
  <div class="page-container" ref="pageContainer" :class="{ 'torture-mode': tortureMode }">
    <!-- Top Left Return Button -->
    <button class="return-btn fixed-top-left" @click="goBackHome">
      <span>← LOGOUT</span>
    </button>

    <!-- Navigation -->
    <nav class="sticky-nav" :class="{ 'visible': showNav }">
      <div class="nav-links">
        <a href="#section-big-brother" @click.prevent="scrollTo('section-big-brother')" :class="{ active: activeSection === 'section-big-brother' }">老大哥</a>
        <a href="#section-party" @click.prevent="scrollTo('section-party')" :class="{ active: activeSection === 'section-party' }">党</a>
        <a href="#section-newspeak" @click.prevent="scrollTo('section-newspeak')" :class="{ active: activeSection === 'section-newspeak' }">新话</a>
        <a href="#section-brotherhood" @click.prevent="scrollTo('section-brotherhood')" :class="{ active: activeSection === 'section-brotherhood' }">兄弟会</a>
        <a href="#section-room-101" @click.prevent="scrollTo('section-room-101')" :class="{ active: activeSection === 'section-room-101' }">101室</a>
      </div>
    </nav>

    <!-- Hero Section -->
    <section id="hero" class="hero-section" @mousemove="handleHeroMouseMove">
      <div class="scanlines"></div>
      <div class="hero-content">
        <div class="eye-wrapper" :style="eyeStyle">
          <div class="giant-eye">
            <div class="pupil"></div>
            <div class="glint"></div>
          </div>
        </div>
        <h1 class="hero-title" :style="titleStyle">1984</h1>
        <p class="hero-subtitle">老大哥在看着你</p>
      </div>
      <div class="scroll-indicator">
        <span>向下滚动以服从</span>
        <div class="arrow">↓</div>
      </div>
    </section>

    <!-- Section 1: Big Brother -->
    <section id="section-big-brother" class="content-section big-brother-section">
      <div class="slogans">
        <div class="slogan" v-for="(slogan, index) in slogans" :key="index" :style="{ animationDelay: index * 0.5 + 's' }">
          {{ slogan }}
        </div>
      </div>
      <div class="telescreen">
        <div class="screen-content">
          <div class="face">
            <div class="mustache"></div>
          </div>
          <p class="broadcast">注意，同志们</p>
        </div>
      </div>
    </section>

    <!-- Section 2: The Party -->
    <section id="section-party" class="content-section party-section">
      <h2 class="section-title">四部</h2>
      <div class="ministries-grid">
        <div class="ministry-card" v-for="ministry in ministries" :key="ministry.name">
          <h3>{{ ministry.name }}</h3>
          <p class="ministry-desc">{{ ministry.desc }}</p>
          <div class="ministry-icon">{{ ministry.icon }}</div>
        </div>
      </div>
    </section>

    <!-- Section 3: Newspeak -->
    <section id="section-newspeak" class="content-section newspeak-section">
      <h2 class="section-title">新话词典</h2>
      <p class="intro-text">缩减思维的范围。思想犯罪将变得不可能。</p>
      <div class="word-cloud">
        <div 
          v-for="(word, index) in newspeakWords" 
          :key="index" 
          class="newspeak-word"
        >
          <span class="word-normal">{{ word.normal }}</span>
          <span class="word-newspeak">{{ word.newspeak }}</span>
        </div>
      </div>
    </section>

    <!-- Section 4: The Brotherhood -->
    <section id="section-brotherhood" class="content-section brotherhood-section">
      <div class="glitch-container">
        <h2 class="section-title glitch" data-text="兄弟会">兄弟会</h2>
      </div>
      <div class="manifesto">
        <p>我们是死者。未来属于无产者。</p>
        <p class="hidden-msg">打倒老大哥</p>
      </div>
    </section>

    <!-- Section 5: Room 101 -->
    <section id="section-room-101" class="content-section room-101-section">
      <div class="fear-container">
        <h2 class="room-title">101室</h2>
        <p class="fear-text">这里有你最害怕的东西。</p>
        <div class="rats">
          <span v-for="n in 5" :key="n" class="rat">🐀</span>
        </div>
        <div class="math-problem">
          <span>2 + 2 = </span>
          <div class="math-options" v-if="!truthAccepted">
            <button class="math-btn btn-4" @click="chooseFour">4</button>
            <button class="math-btn btn-5" @click="chooseFive">5</button>
          </div>
          <span class="truth-reveal" v-else>5</span>
        </div>
      </div>
    </section>

    <!-- Section 6: Ending (Hidden until unlocked) -->
    <section id="section-ending" class="content-section ending-section" v-show="truthAccepted">
      <div class="ending-content">
        <p class="final-quote" :class="{ 'visible': quoteVisible }">
          他抬头望向那张巨大的脸。花了四十年，他才明白黑色小胡子下藏着怎样的微笑。噢，残酷而毫无必要的误解！噢，固执而倔强地流放自己于慈爱胸怀之外！
        </p>
        <h1 class="final-statement">{{ typewriterText }}<span class="cursor">_</span></h1>
      </div>
    </section>

    <!-- Footer -->
    <footer class="page-footer">
      <button class="return-home" @click="router.push('/main')">返回首页</button>
    </footer>

    <!-- Global CRT Overlay -->
    <div class="crt-overlay"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goBackHome = () => {
  router.push('/main');
};

const pageContainer = ref<HTMLElement | null>(null);
const showNav = ref(false);
const activeSection = ref('');
const truthAccepted = ref(false);
const tortureMode = ref(false);

// Hero Parallax
const mouseX = ref(0);
const mouseY = ref(0);

const handleHeroMouseMove = (e: MouseEvent) => {
  const { innerWidth, innerHeight } = window;
  mouseX.value = (e.clientX / innerWidth) * 2 - 1;
  mouseY.value = (e.clientY / innerHeight) * 2 - 1;
};

const eyeStyle = computed(() => ({
  transform: `translate(${mouseX.value * 20}px, ${mouseY.value * 20}px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -10}px, ${mouseY.value * -10}px)`
}));

// Data
const slogans = ['战争即和平', '自由即奴役', '无知即力量'];
const ministries = [
  { name: '真理部', desc: '负责新闻、娱乐、教育和艺术。实际上负责篡改历史。', icon: 'Minitrue' },
  { name: '和平部', desc: '负责战争。', icon: 'Minipax' },
  { name: '友爱部', desc: '维持法律和秩序。实际上负责拷打和洗脑。', icon: 'Miniluv' },
  { name: '富裕部', desc: '负责经济事务。实际上负责制造匮乏。', icon: 'Miniplenty' },
];

const newspeakWords = ref([
  { normal: '极好', newspeak: '加倍好' },
  { normal: '坏', newspeak: '不好' },
  { normal: '科学', newspeak: '无此词' },
  { normal: '自由', newspeak: '思想罪' },
  { normal: '改革', newspeak: '旧思想' },
  { normal: '快乐', newspeak: '享乐营' },
  { normal: '鸭子', newspeak: '鸭语' },
]);

// Room 101 Logic
const chooseFour = () => {
  tortureMode.value = true;
  // Shake effect and red flash
  setTimeout(() => {
    tortureMode.value = false;
  }, 500);
};

const chooseFive = () => {
  truthAccepted.value = true;
  // Auto scroll to ending after a short delay
  setTimeout(() => {
    scrollTo('section-ending');
  }, 1000);
};

// Ending Logic
const quoteVisible = ref(false);
const typewriterText = ref('');
const fullTypewriterText = '他爱老大哥';
const typewriterStarted = ref(false);

const startEndingSequence = () => {
  if (typewriterStarted.value) return;
  typewriterStarted.value = true;
  
  // Show quote first
  setTimeout(() => {
    quoteVisible.value = true;
  }, 500);

  // Start typewriter after quote is read (approx 3s)
  setTimeout(() => {
    let i = 0;
    const interval = setInterval(() => {
      typewriterText.value += fullTypewriterText.charAt(i);
      i++;
      if (i >= fullTypewriterText.length) clearInterval(interval);
    }, 300);
  }, 4000);
};

const scrollTo = (id: string) => {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
};

// Scroll Observer
let observer: IntersectionObserver;

onMounted(() => {
  const options = {
    threshold: 0.2
  };

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        activeSection.value = entry.target.id;
        
        // Handle Nav Visibility
        if (entry.target.id !== 'hero') {
          showNav.value = true;
        } else {
          showNav.value = false;
        }

        // Handle Ending Sequence
        if (entry.target.id === 'section-ending' && truthAccepted.value) {
          startEndingSequence();
        }
      }
    });
  }, options);

  document.querySelectorAll('section').forEach(section => {
    observer.observe(section);
  });
});

// Watch for truth acceptance to observe the new section
watch(truthAccepted, (newVal) => {
  if (newVal) {
    setTimeout(() => {
      const endingSection = document.getElementById('section-ending');
      if (endingSection && observer) observer.observe(endingSection);
    }, 100);
  }
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=VT323&family=Black+Ops+One&display=swap');

.page-container {
  --font-hero: 'Black Ops One', cursive;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
  --font-mono: 'Share Tech Mono', monospace;
  --font-pixel: 'VT323', monospace;
}

.page-container {
  width: 100%;
  height: 100vh;
  background-color: #0a0a0a;
  color: #d4d4d4;
  overflow-x: hidden;
  overflow-y: auto;
  font-family: var(--font-mono);
  position: relative;
}

/* Typography Updates */
.hero-title {
  font-family: var(--font-hero);
  letter-spacing: 10px;
}

.hero-subtitle, .slogan, .broadcast, .intro-text, .ministry-desc, .fear-text {
  font-family: var(--font-text);
}

.nav-links a, .return-btn {
  font-family: var(--font-ui);
}

.date-display, .stability-meter span, .newspeak-word, .section-title {
  font-family: var(--font-ui);
  font-weight: bold;
}

/* Numbers (if any specific) */
.number-display {
  font-family: var(--font-mono);
}

.page-container.torture-mode {
  background-color: #500000;
  animation: shake 0.1s infinite;
}

@keyframes shake {
  0% { transform: translate(0, 0); }
  25% { transform: translate(5px, 5px); }
  50% { transform: translate(-5px, -5px); }
  75% { transform: translate(5px, -5px); }
  100% { transform: translate(-5px, 5px); }
}

/* CRT Overlay */
.crt-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.25) 50%), linear-gradient(90deg, rgba(255, 0, 0, 0.06), rgba(0, 255, 0, 0.02), rgba(0, 0, 255, 0.06));
  background-size: 100% 2px, 3px 100%;
  pointer-events: none;
  z-index: 9999;
}

/* Return Buttons */
.return-btn.fixed-top-left {
  position: fixed;
  top: 10px;
  left: 24px;
  z-index: 2000;
  background: #cc0000;
  border: 2px solid #000;
  padding: 10px 20px;
  font-family: 'Courier New', monospace;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  color: #000;
  box-shadow: 4px 4px 0 #000;
}

.return-btn.fixed-top-left:hover {
  background: #000;
  color: #cc0000;
  border-color: #cc0000;
  box-shadow: 4px 4px 0 #cc0000;
}

.footer-action {
  margin-top: 30px;
  width: 100%;
  display: flex;
  justify-content: center;
}

.footer-btn {
  background: #000;
  border: 2px solid #cc0000;
  color: #cc0000;
  padding: 15px 40px;
  font-family: 'Courier New', monospace;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.footer-btn:hover {
  background: #cc0000;
  color: #000;
  box-shadow: 0 0 20px #cc0000;
}

/* Navigation */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 20px;
  background: rgba(5, 5, 5, 0.9);
  backdrop-filter: blur(10px);
  z-index: 100;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  border-bottom: 1px solid #39ff14;
}

.sticky-nav.visible {
  transform: translateY(0);
}

.nav-links {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.nav-links a {
  color: #39ff14;
  text-decoration: none;
  font-weight: bold;
  opacity: 0.5;
  transition: all 0.3s;
  text-transform: uppercase;
}

.nav-links a:hover, .nav-links a.active {
  opacity: 1;
  text-shadow: 0 0 10px #39ff14;
}

/* Hero Section */
.hero-section {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.hero-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 15vw;
  font-weight: 900;
  margin: 0;
  color: #e0e0e0;
  text-shadow: 4px 4px #000;
  z-index: 10;
  line-height: 1;
}

.hero-subtitle {
  font-size: 1.5rem;
  letter-spacing: 0.5rem;
  margin-top: 20px;
  background: #39ff14;
  color: #000;
  padding: 5px 10px;
  font-weight: bold;
}

.giant-eye {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle at center, #fff 0%, #ddd 60%, #999 100%);
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 50px rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1;
}

.pupil {
  width: 100px;
  height: 100px;
  background: #000;
  border-radius: 50%;
  position: relative;
}

.glint {
  width: 20px;
  height: 20px;
  background: #fff;
  border-radius: 50%;
  position: absolute;
  top: 20px;
  left: 20px;
}

.eye-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  opacity: 0.2;
}

.scroll-indicator {
  position: absolute;
  bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  animation: pulse 2s infinite;
}

/* Sections General */
.content-section {
  min-height: 100vh;
  padding: 100px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  border-bottom: 1px dashed #333;
}

.section-title {
  font-size: 4rem;
  margin-bottom: 50px;
  font-family: 'Noto Serif SC', serif;
  text-transform: uppercase;
  border-bottom: 4px solid #39ff14;
}

/* Big Brother Section */
.slogans {
  display: flex;
  flex-direction: column;
  gap: 40px;
  text-align: center;
  margin-bottom: 60px;
}

.slogan {
  font-size: 3rem;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 5px;
  animation: float 3s ease-in-out infinite;
}

/* Party Section */
.ministries-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
  max-width: 1000px;
  width: 100%;
}

.ministry-card {
  border: 2px solid #39ff14;
  padding: 30px;
  background: #000;
  transition: all 0.3s;
}

.ministry-card:hover {
  background: #39ff14;
  color: #000;
  transform: scale(1.05);
}

.ministry-card h3 {
  font-size: 2rem;
  margin-top: 0;
}

/* Newspeak Section */
.word-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  max-width: 800px;
}

.newspeak-word {
  display: grid;
  place-items: center;
  font-size: 1.8rem;
  padding: 10px 20px;
  border: 1px solid #333;
  cursor: help;
  transition: all 0.2s;
  min-width: 120px;
  text-align: center;
}

.word-normal, .word-newspeak {
  grid-area: 1 / 1;
  transition: opacity 0.2s;
}

.newspeak-word .word-newspeak {
  opacity: 0;
  color: #ff0000;
  font-weight: bold;
}

.newspeak-word:hover {
  background: #000;
  border-color: #ff0000;
}

.newspeak-word:hover .word-normal {
  opacity: 0;
}

.newspeak-word:hover .word-newspeak {
  opacity: 1;
  animation: glitch-text 0.3s infinite;
}

/* Room 101 */
.room-101-section {
  background: #000;
  color: #ff0000; /* Red for danger */
}

.room-101-section .section-title {
  border-color: #ff0000;
}

.rats {
  font-size: 3rem;
  margin: 20px 0;
  animation: jitter 0.2s infinite;
}

.math-problem {
  font-size: 5rem;
  font-weight: bold;
  margin-top: 40px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.math-options {
  display: flex;
  gap: 20px;
}

.math-btn {
  font-size: 4rem;
  background: transparent;
  border: 2px solid #ff0000;
  color: #ff0000;
  width: 100px;
  height: 100px;
  cursor: pointer;
  font-family: 'Courier Prime', monospace;
  transition: all 0.2s;
}

.math-btn:hover {
  background: #ff0000;
  color: #000;
}

.truth-reveal {
  font-size: 8rem;
  color: #39ff14; /* Green for acceptance */
  animation: slam 0.2s;
}

/* Ending Section */
.ending-section {
  background: #000;
  color: #fff;
  min-height: 100vh;
  justify-content: center;
}

.ending-content {
  max-width: 800px;
  text-align: center;
}

.final-quote {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.5rem;
  line-height: 2;
  margin-bottom: 50px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 2s ease-out;
}

.final-quote.visible {
  opacity: 1;
  transform: translateY(0);
}

.final-statement {
  font-size: 5rem;
  color: #39ff14;
  font-family: 'Noto Serif SC', serif;
  font-weight: 900;
  text-shadow: 0 0 20px rgba(57, 255, 20, 0.5);
}

.cursor {
  animation: blink 1s infinite;
}

/* Footer */
.page-footer {
  padding: 40px;
  text-align: center;
  border-top: 1px solid #333;
}

.return-home {
  background: transparent;
  color: #39ff14;
  border: 2px solid #39ff14;
  padding: 15px 40px;
  font-size: 1.2rem;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.3s;
  text-transform: uppercase;
}

.return-home:hover {
  background: #39ff14;
  color: #000;
  box-shadow: 0 0 20px #39ff14;
}

/* Animations */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes jitter {
  0% { transform: translate(0, 0); }
  25% { transform: translate(2px, 2px); }
  50% { transform: translate(-2px, -2px); }
  75% { transform: translate(2px, -2px); }
  100% { transform: translate(-2px, 2px); }
}

@keyframes slam {
  0% { transform: scale(5); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

@keyframes glitch-text {
  0% { transform: translate(0); }
  20% { transform: translate(-2px, 2px); }
  40% { transform: translate(-2px, -2px); }
  60% { transform: translate(2px, 2px); }
  80% { transform: translate(2px, -2px); }
  100% { transform: translate(0); }
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title { font-size: 20vw; }
  .ministries-grid { grid-template-columns: 1fr; }
  .nav-links { font-size: 0.8rem; gap: 10px; }
  .math-problem { font-size: 3rem; flex-direction: column; }
  .final-statement { font-size: 3rem; }
}
</style>
