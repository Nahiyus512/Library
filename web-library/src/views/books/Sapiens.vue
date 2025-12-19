<template>
  <div class="sapiens-container" @scroll="handleScroll" ref="containerRef">
    <!-- Background Texture (Light Mode) -->
    <div class="bg-texture"></div>

    <!-- TOP NAVIGATION -->
    <nav class="sticky-nav" :class="{ 'nav-visible': showNav }">
      <button class="fixed-exit-btn nav-back-btn" @click="goBackHome">
        <span>← 退出历史</span>
      </button>
      <div class="nav-content">
        <div 
          v-for="(section, index) in sections" 
          :key="index"
          class="nav-item"
          :class="{ active: currentSection === index }"
          @click="scrollToSection(index)"
        >
          {{ section.name }}
        </div>
      </div>
    </nav>

    <!-- LEFT DYNAMIC TIMELINE (Watch Dial Style) -->
    <div class="left-timeline-dial">
      <div class="dial-window">
        <div class="dial-list" :style="dialListStyle">
          <div 
            v-for="(year, index) in timelineYears" 
            :key="index"
            class="dial-item"
            :class="{ active: isYearActive(index) }"
          >
            {{ year.label }}
          </div>
        </div>
        <div class="dial-selection-bar"></div>
      </div>
      <div class="era-display">{{ currentEra }}</div>
    </div>

    <!-- 1. ORIGINS (起源) - Hero Section -->
    <section class="section section-hero" id="section-0" @mousemove="handleHeroMouseMove" @mouseleave="resetHeroParallax">
      <div class="hero-content" :style="parallaxStyle">
        <!-- Cover Animations Integration -->
        <div class="evolution-spiral" :style="spiralStyle">
          <div class="spiral-ring ring-1"></div>
          <div class="spiral-ring ring-2"></div>
          <div class="spiral-ring ring-3"></div>
          <div class="center-symbol">
            <div class="hand-print"></div>
          </div>
        </div>
        
        <div class="title-layer" :style="titleStyle">
          <h1 class="main-title cn-title">人类简史</h1>
          <h2 class="sub-title en-title">SAPIENS</h2>
        </div>

        <div class="hero-data-overlay">
          <div class="year-counter">{{ heroYearDisplay }}</div>
          <div class="era-label">{{ heroEra }}</div>
        </div>
      </div>
      <div class="scroll-indicator">
        <span class="scroll-text">向下探索历史长河</span>
        <div class="scroll-line"></div>
      </div>
    </section>

    <!-- 2. COGNITIVE (认知) - Solar System of Species -->
    <section class="section section-cognitive" id="section-1">
      <div class="section-header">
        <h2 class="section-title">认知革命</h2>
        <p class="section-desc">曾几何时，我们只是众多人类物种之一。如今，只剩我们孤独仰望星空。</p>
      </div>
      
      <!-- Solar System Interaction -->
      <div class="solar-system-wrapper">
        <div class="solar-system">
          <!-- Sun: Sapiens -->
          <div class="sun-sapiens">
            <div class="sun-body">
              <span class="sun-icon">👤</span>
            </div>
            <div class="sun-label">智人</div>
          </div>

          <!-- Orbits -->
          <div 
            v-for="(orbit, i) in orbits" 
            :key="i" 
            class="orbit-ring"
            :style="{ 
              width: orbit.size + 'px', 
              height: orbit.size + 'px',
              zIndex: 10 - i
            }"
          >
            <!-- Orbit Spinner (Rotates) -->
            <div 
              class="orbit-spinner"
              :style="{ animationDuration: orbit.duration + 's' }"
            >
              <!-- Planets -->
              <div 
                v-for="(specie, j) in orbit.species" 
                :key="j"
                class="planet-wrapper"
                :style="{ transform: `rotate(${j * (360 / orbit.species.length)}deg)` }"
              >
                <!-- Planet Position (Push to radius) -->
                <div 
                  class="planet-pos"
                  :style="{ transform: `translateX(${orbit.size / 2}px)` }"
                >
                  <!-- Planet Counter-Rotation (Keep upright) -->
                  <div 
                    class="planet-inner"
                    :style="{ animationDuration: orbit.duration + 's' }"
                  >
                    <!-- Static Orientation Fix (Counteract position rotation) -->
                    <div 
                      class="planet-fix-orientation"
                      :style="{ transform: `rotate(-${j * (360 / orbit.species.length)}deg)` }"
                    >
                      <div 
                        class="planet-species"
                        :class="{ extinct: specie.isExtinct }"
                        @click.stop="eliminateSpecies(specie)"
                      >
                        <div class="planet-body">
                          <span class="planet-icon">{{ specie.icon }}</span>
                        </div>
                        <div class="planet-name-container">
                          <div class="planet-label">{{ specie.name }}</div>
                        </div>
                        <div class="extinct-mark" v-if="specie.isExtinct">❌</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <p class="interaction-hint">点击其他人种，见证残酷的历史</p>
      </div>
    </section>

    <!-- 3. AGRICULTURAL (农业) - The Wheat Trap -->
    <section class="section section-agricultural" id="section-2">
      <div class="section-header">
        <h2 class="section-title">农业革命</h2>
        <p class="section-desc">历史上最大的骗局。小麦驯化了我们。</p>
      </div>

      <div class="wheat-field-container">
        <div class="wheat-grid">
          <div 
            v-for="n in 207" 
            :key="n" 
            class="wheat-stalk"
            @mouseenter="revealToil(n)"
            @click="revealToil(n)"
            :class="{ 'revealed': revealedWheat.has(n) }"
          >
            {{ revealedWheat.has(n) ? '😓' : '🌾' }}
          </div>
        </div>
        <div class="wheat-overlay-text" v-if="revealedWheat.size > 20">
          <h3>劳作无止境</h3>
          <p>人口增加了，但生活更苦了。</p>
        </div>
      </div>
    </section>

    <!-- 4. UNIFICATION (融合) - Imagined Orders -->
    <section class="section section-unification" id="section-3">
      <div class="section-header">
        <h2 class="section-title">人类的融合</h2>
        <p class="section-desc">金钱、帝国、宗教。虚构的秩序连接了全人类。</p>
      </div>

      <div class="interaction-area-orbit">
        <!-- Center Brain -->
        <div class="sapiens-brain-center" :class="{ 'brain-full': brainFull }">
          <div class="brain-icon">🧠</div>
          <div class="brain-text">{{ brainText }}</div>
        </div>

        <!-- Orbiting Concepts -->
        <div 
          v-for="(concept, index) in orbitConcepts" 
          :key="index"
          class="orbit-concept"
          :class="{ 'absorbed': concept.absorbed }"
          :style="getConceptStyle(index, concept.absorbed)"
          @click="absorbConcept(index)"
        >
          <div class="concept-icon">{{ concept.icon }}</div>
          <div class="concept-name">{{ concept.name }}</div>
        </div>
      </div>
      <p class="interaction-hint" v-if="!brainFull">点击周围的虚构概念，将其融入秩序</p>
    </section>

    <!-- 5. FUTURE (终局) - Scrolling Stream -->
    <section class="section section-future" id="section-4">
      <div class="section-header">
        <h2 class="section-title">科学革命与终局</h2>
        <p class="section-desc">我们正站在生物学的尽头。</p>
      </div>

      <div class="future-stream">
        <div class="stream-line"></div>
        <div 
          class="stream-event" 
          v-for="(ev, i) in futureEvents" 
          :key="i"
          ref="eventRefs"
          :class="{ visible: visibleEventIndices.has(i) }"
        >
          <div class="event-year">{{ ev.year }}</div>
          <div class="event-content">
            <h3>{{ ev.title }}</h3>
            <p>{{ ev.desc }}</p>
          </div>
        </div>
      </div>

      <div class="final-statement">
        <h3 class="end-quote">“历史从无正义。”</h3>
      </div>
      
      <div class="footer-action">
        <div v-if="!hasRated" class="rating-sapiens">
           <button v-if="!showRatingOptions" class="history-btn" @click="showRatingOptions = true">评价历史</button>
           <div v-else class="history-options">
               <button class="history-opt" @click="rateBook(0)">认知失调 (0)</button>
               <button class="history-opt" @click="rateBook(1)">农业陷阱 (1)</button>
               <button class="history-opt" @click="rateBook(2)">科学革命 (2)</button>
           </div>
        </div>
        <button v-else class="footer-home-btn" @click="goBackHome">
          <span>回到当下</span>
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
const containerRef = ref<HTMLElement | null>(null);

// Rating Logic
const hasRated = ref(false);
const showRatingOptions = ref(false);

const rateBook = async (level: number) => {
  console.log('rateBook called', level);
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
      bookId: 12, // Sapiens ID
      bookName: "人类简史",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('历史已记录');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '认知崩塌');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('进化失败');
  }
};

// --- Navigation & Timeline ---
const showNav = ref(false);
const currentSection = ref(0);
const scrollProgress = ref(0); // 0 to 1
const activeTimelineIndex = ref(0); // Index in timelineYears

const sections = [
  { name: '起源', id: 'section-0', range: [-2500000, -70000] },
  { name: '认知革命', id: 'section-1', range: [-70000, -12000] },
  { name: '农业革命', id: 'section-2', range: [-12000, 1500] },
  { name: '人类融合', id: 'section-3', range: [1500, 2000] },
  { name: '科学终局', id: 'section-4', range: [2000, 2025] },
];

const scrollToSection = (index: number) => {
  const el = document.getElementById(`section-${index}`);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
};

const goBackHome = () => {
  router.back();
};

// --- Timeline Dial Logic ---
const timelineYears = [
  { val: -2500000, label: '250万年前' },
  { val: -2000000, label: '200万年前' },
  { val: -1000000, label: '100万年前' },
  { val: -500000, label: '50万年前' },
  { val: -200000, label: '20万年前' },
  { val: -100000, label: '10万年前' },
  { val: -70000, label: '7万年前' }, // Cognitive
  { val: -45000, label: '4.5万年前' },
  { val: -30000, label: '3万年前' },
  { val: -12000, label: '1.2万年前' }, // Agricultural
  { val: -5000, label: '5000年前' },
  { val: -3000, label: '3000年前' },
  { val: -2000, label: '2000年前' },
  { val: -1000, label: '1000年前' },
  { val: 0, label: '公元元年' },
  { val: 500, label: '公元500年' },
  { val: 1000, label: '公元1000年' },
  { val: 1500, label: '公元1500年' }, // Scientific
  { val: 1800, label: '公元1800年' },
  { val: 1900, label: '公元1900年' },
  { val: 1950, label: '公元1950年' },
  { val: 2000, label: '公元2000年' },
  { val: 2025, label: '公元2025年' },
];

const dialListStyle = computed(() => {
  const itemHeight = 40; // px
  // We want the active item to be at the center (100px)
  // TranslateY = Center - (Index * Height) - HalfItem
  const translateY = 100 - (activeTimelineIndex.value * itemHeight) - (itemHeight / 2);
  
  return {
    transform: `translateY(${translateY}px)`
  };
});

const isYearActive = (index: number) => {
  return index === activeTimelineIndex.value;
};

const currentEra = computed(() => {
  const idx = currentSection.value;
  switch(idx) {
    case 0: return '史前时代';
    case 1: return '认知革命';
    case 2: return '农业革命';
    case 3: return '融合时代';
    case 4: return '科学革命';
    default: return '';
  }
});

// --- Scroll Handling ---
const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement;
  const scrollTop = target.scrollTop;
  const scrollHeight = target.scrollHeight - target.clientHeight;
  scrollProgress.value = Math.min(1, Math.max(0, scrollTop / scrollHeight));

  // Determine active section and timeline year
  let activeSecIndex = 0;
  
  for (let i = 0; i < sections.length; i++) {
    const el = document.getElementById(`section-${i}`);
    if (el) {
      const rect = el.getBoundingClientRect();
      // If section is in view (top is above middle of screen, bottom is below)
      if (rect.top <= window.innerHeight / 2 && rect.bottom >= window.innerHeight / 2) {
        activeSecIndex = i;
        
        // Calculate progress within section
        // rect.top is position relative to viewport.
        // When rect.top is 0, we are at start.
        // When rect.bottom is window.innerHeight, we are at end.
        // Actually, let's simplify: map [rect.top, rect.bottom] to [0, 1] relative to viewport center?
        // Standard progress: (scrollTop - el.offsetTop) / el.height
        
        const elTop = el.offsetTop;
        const elHeight = el.clientHeight;
        const relativeScroll = scrollTop - elTop;
        // Add half viewport offset so we see "start" when it enters middle?
        // Let's stick to standard top-alignment for calculation
        const p = Math.min(1, Math.max(0, (scrollTop - elTop + window.innerHeight/3) / elHeight));
        
        // Map p to year range
        const range = sections[i].range;
        const year = range[0] + (range[1] - range[0]) * p;
        
        // Find closest timeline index
        let minDist = Infinity;
        let closestIdx = 0;
        timelineYears.forEach((t, idx) => {
          const dist = Math.abs(t.val - year);
          if (dist < minDist) {
            minDist = dist;
            closestIdx = idx;
          }
        });
        activeTimelineIndex.value = closestIdx;
      }
    }
  }
  
  currentSection.value = activeSecIndex;

  // Nav visibility: Show only after scrolling past the first section (Hero)
  const heroSection = document.getElementById('section-0');
  if (heroSection) {
    // Show nav if scrolled past 80% of hero section
    showNav.value = scrollTop > heroSection.clientHeight * 0.8;
  }
};


// --- 1. Hero Parallax & Animations ---
const heroYear = ref(-70000);
const mouseX = ref(0);
const mouseY = ref(0);
let heroAnimationFrame: number;

const handleHeroMouseMove = (e: MouseEvent) => {
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
  const x = e.clientX - rect.left - rect.width / 2;
  const y = e.clientY - rect.top - rect.height / 2;
  mouseX.value = x;
  mouseY.value = y;
};

const resetHeroParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const spiralStyle = computed(() => ({
  transform: `translate(${mouseX.value * 0.05}px, ${mouseY.value * 0.05}px)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -0.1}px, ${mouseY.value * -0.1}px)`
}));

const heroYearDisplay = computed(() => {
  return heroYear.value < 0 ? `前 ${Math.abs(heroYear.value)}` : `${heroYear.value}`;
});

const heroEra = computed(() => {
  if (heroYear.value < -12000) return "认知革命";
  if (heroYear.value < 1500) return "农业革命";
  return "科学革命";
});

const animateHero = () => {
  heroYear.value += 100;
  if (heroYear.value > 2024) {
    heroYear.value = -70000;
  }
  heroAnimationFrame = requestAnimationFrame(animateHero);
};

const parallaxStyle = computed(() => {
  return {
    transform: `translateY(${scrollProgress.value * 50}px)`,
    opacity: 1 - scrollProgress.value * 3
  };
});



// --- 2. Cognitive Revolution (Solar System) ---
interface Species {
  name: string;
  icon: string;
  isExtinct: boolean;
}

interface Orbit {
  size: number;
  duration: number;
  species: Species[];
}

const orbits = ref<Orbit[]>([
  {
    size: 300,
    duration: 20,
    species: [
      { name: '尼安德特人', icon: '🦴', isExtinct: false },
      { name: '直立人', icon: '🔥', isExtinct: false },
      { name: '梭罗人', icon: '🌴', isExtinct: false },
    ]
  },
  {
    size: 500,
    duration: 35,
    species: [
      { name: '丹尼索瓦人', icon: '🏔️', isExtinct: false },
      { name: '鲁道夫人', icon: '🗿', isExtinct: false },
      { name: '匠人', icon: '🔨', isExtinct: false },
      { name: '弗洛勒斯人', icon: '🏝️', isExtinct: false },
    ]
  }
]);

const eliminateSpecies = (specie: Species) => {
  specie.isExtinct = true;
};

// --- 3. Agricultural (Wheat) ---
const revealedWheat = ref(new Set<number>());
const revealToil = (n: number) => {
  revealedWheat.value.add(n);
};

// --- 4. Unification (Orbiting Concepts) ---
interface Concept {
  name: string;
  icon: string;
  absorbed: boolean;
}

const orbitConcepts = ref<Concept[]>([
  { name: '金钱', icon: '💰', absorbed: false },
  { name: '帝国', icon: '👑', absorbed: false },
  { name: '宗教', icon: '⛪', absorbed: false },
  { name: '法律', icon: '⚖️', absorbed: false }, // Added 4th
]);

const brainText = ref('虚构秩序');
const brainFull = computed(() => orbitConcepts.value.every(c => c.absorbed));

const getConceptStyle = (index: number, absorbed: boolean) => {
  if (absorbed) {
    return {
      top: '50%',
      left: '50%',
      transform: 'translate(-50%, -50%) scale(0.5)',
      opacity: 0,
    };
  }
  
  // Arrange in circle (radius 220px)
  const total = orbitConcepts.value.length;
  const angle = (index / total) * 360; // 0, 90, 180, 270
  const rad = (angle * Math.PI) / 180;
  const r = 220;
  
  // Center is 50%, 50%.
  // x = r * cos, y = r * sin
  const x = r * Math.cos(rad);
  const y = r * Math.sin(rad);
  
  return {
    top: `calc(50% + ${y}px)`,
    left: `calc(50% + ${x}px)`,
    transform: 'translate(-50%, -50%)',
    opacity: 1
  };
};

const absorbConcept = (index: number) => {
  const concept = orbitConcepts.value[index];
  if (!concept.absorbed) {
    concept.absorbed = true;
    if (brainFull.value) {
      setTimeout(() => {
        brainText.value = '全球融合';
      }, 500);
    }
  }
};

// --- 5. Future ---
const futureEvents = [
  { year: '1500', title: '科学革命', desc: '人类承认自己的无知，开始寻求新能力。' },
  { year: '1750', title: '工业革命', desc: '能源转换的革命，同时也带来了生态危机。' },
  { year: '1945', title: '原子时代', desc: '人类掌握了毁灭自身的力量。' },
  { year: '1969', title: '登月', desc: '人类首次踏上另一个星球，历史进入新纪元。' },
  { year: '2000', title: '信息时代', desc: '互联网连接全球，数据成为新石油。' },
  { year: '2050', title: '奇点降临', desc: '人工智能超越人类，碳基生命与硅基生命融合。' },
  { year: '2100', title: '智能设计', desc: '自然选择被智能设计取代，智人进化为神人。' },
];

const visibleEventIndices = ref(new Set<number>());
const eventRefs = ref<HTMLElement[]>([]);

// Use intersection observer in onMounted (Simplified for now)
onMounted(() => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        // Just a visual effect logic placeholder
      }
    });
  });
  
  // Start Hero Animation
  heroAnimationFrame = requestAnimationFrame(animateHero);
});

onUnmounted(() => {
  if (heroAnimationFrame) {
    cancelAnimationFrame(heroAnimationFrame);
  }
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700;900&family=Playfair+Display:ital,wght@0,400;0,700;1,400&display=swap');

/* --- Hero Animation Styles --- */
.evolution-spiral {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px; height: 400px;
  display: flex; align-items: center; justify-content: center;
  opacity: 0.15;
  pointer-events: none;
  z-index: 0;
}

.spiral-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px dashed #5d4037;
}

.ring-1 {
  width: 100%; height: 100%;
  animation: spin 60s linear infinite;
  border-width: 1px;
}

.ring-2 {
  width: 70%; height: 70%;
  border-color: #8d6e63;
  border-style: dotted;
  animation: spin 40s linear infinite reverse;
}

.ring-3 {
  width: 40%; height: 40%;
  border: 4px solid rgba(93, 64, 55, 0.2);
  border-top-color: #5d4037;
  animation: spin 10s ease-in-out infinite;
}

.center-symbol {
  width: 10px; height: 10px;
  background: #3e2723;
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(62, 39, 35, 0.5);
  animation: pulse 2s infinite;
}

.title-layer {
  position: relative;
  z-index: 10;
  text-align: center;
  mix-blend-mode: multiply;
}

.cn-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 5rem;
  font-weight: 900;
  letter-spacing: 10px;
  color: #3e2723;
  margin-bottom: 0;
}

.en-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.5rem;
  letter-spacing: 15px;
  opacity: 0.6;
  font-weight: bold;
  margin-top: 10px;
  color: #3e2723;
}

.hero-data-overlay {
  margin-top: 30px;
  text-align: center;
  color: #3e2723;
}

.year-counter {
  font-size: 1.5rem;
  font-weight: 700;
  font-family: 'Courier New', monospace;
  font-variant-numeric: tabular-nums;
}

.era-label {
  font-size: 0.9rem;
  letter-spacing: 2px;
  margin-top: 5px;
  opacity: 0.8;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.8; }
  50% { transform: scale(1.5); opacity: 0.4; }
}

/* --- Global & Container --- */
.sapiens-container {
  --font-hero: 'Cinzel', serif;
  --font-ui: 'Microsoft YaHei', 'Heiti SC', sans-serif;
  --font-text: 'Songti SC', 'SimSun', serif;
  --font-number: 'Playfair Display', serif;
}

.sapiens-container {
  width: 100%;
  height: 100vh;
  background-color: #f4e4bc;
  color: #2c241b;
  overflow-x: hidden;
  overflow-y: auto;
  font-family: var(--font-text);
  position: relative;
  scroll-behavior: smooth;
}

/* Typography Updates */
h1, h2, h3, h4 {
  font-family: var(--font-hero);
}

.cn-title {
  font-family: var(--font-hero); /* Cinzel works well for Latin, but for CN title maybe use Songti or a heavy serif */
  font-weight: 900;
}

.en-title {
  font-family: var(--font-hero);
  letter-spacing: 0.5em;
}

.section-desc, .description-text, .quote-text {
  font-family: var(--font-text);
}

.nav-item, .fixed-exit-btn, .dial-item, .scroll-text {
  font-family: var(--font-ui);
}

.year-counter, .era-label, .timeline-year, .stat-value {
  font-family: var(--font-number);
}

.bg-texture {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, #ffffff 0%, #e0e0d0 100%);
  opacity: 0.5;
  pointer-events: none;
  z-index: 0;
}

/* --- Fixed Exit Button --- */
.fixed-exit-btn {
  /* Position handled by nav-back-btn when inside nav */
  z-index: 2000;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #ddd;
  padding: 10px 20px;
  font-family: 'Times New Roman', serif;
  font-style: italic;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  border-radius: 4px;
}

.nav-back-btn {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  margin: 0;
}

.fixed-exit-btn:hover {
  background: #000;
  color: #fff;
  /* transform: translateY(-2px); Removed hover move */
  box-shadow: 0 6px 15px rgba(0,0,0,0.15);
}

.footer-action {
  margin-top: 60px;
  display: flex;
  justify-content: center;
}

.footer-home-btn {
  background: transparent;
  border: 2px solid #333;
  color: #333;
  padding: 15px 40px;
  font-size: 18px;
  font-family: 'Times New Roman', serif;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.footer-home-btn:hover {
  background: #333;
  color: #fff;
}

/* --- Navigation (Sticky Top) --- */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: rgba(240, 240, 230, 0.9);
  backdrop-filter: blur(10px);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transform: translateY(-100%);
}

.sticky-nav.nav-visible {
  transform: translateY(0);
}

.nav-content {
  display: flex;
  gap: 30px;
}

.nav-item {
  cursor: pointer;
  padding: 8px 16px;
  font-size: 0.9rem;
  color: #666;
  transition: color 0.3s, background 0.3s;
  border-radius: 20px;
  font-weight: 500;
}

.nav-item:hover, .nav-item.active {
  color: #000;
  background: rgba(0, 0, 0, 0.05);
}

/* --- Left Dynamic Timeline (Watch Dial) --- */
.left-timeline-dial {
  position: fixed;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 900;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  pointer-events: none;
}

.dial-window {
  width: 140px;
  height: 200px;
  overflow: hidden;
  position: relative;
  /* Masking effects */
  mask-image: linear-gradient(to bottom, transparent, black 10%, black 90%, transparent);
  -webkit-mask-image: linear-gradient(to bottom, transparent, black 10%, black 90%, transparent);
  border-right: 2px solid #ccc;
}

.dial-list {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  padding-right: 20px;
  transition: transform 0.1s linear; /* Smooth but quick */
}

.dial-item {
  height: 40px;
  line-height: 40px;
  font-size: 0.9rem;
  color: #999;
  font-weight: bold;
  transition: all 0.2s;
  white-space: nowrap;
  transform-origin: right center;
}

.dial-item.active {
  font-size: 1.3rem;
  color: #333;
  transform: scale(1.1);
}

.dial-selection-bar {
  position: absolute;
  top: 50%;
  right: 0;
  width: 10px;
  height: 2px;
  background: #ff4500;
  transform: translateY(-50%);
}

.era-display {
  font-size: 1rem;
  color: #ff4500;
  font-weight: bold;
  letter-spacing: 2px;
  writing-mode: vertical-rl;
  text-orientation: upright;
  margin-top: 10px;
}

/* --- Section Generic --- */
.section {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  box-sizing: border-box;
  z-index: 1;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
  max-width: 800px;
}

.section-title {
  font-size: 3rem;
  margin-bottom: 10px;
  color: #222;
}

.section-desc {
  font-size: 1.2rem;
  color: #666;
  line-height: 1.6;
}

/* --- 1. Hero --- */
.hero-content {
  text-align: center;
}

.main-title {
  font-size: 5rem;
  letter-spacing: 10px;
  margin-bottom: 10px;
  text-transform: uppercase;
  color: #000;
}

.sub-title {
  font-size: 2rem;
  font-weight: 300;
  color: #555;
}

.scroll-indicator {
  position: absolute;
  bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  opacity: 0.7;
  animation: bounce 2s infinite;
  color: #333;
}

.scroll-line {
  width: 1px;
  height: 60px;
  background: linear-gradient(to bottom, #333, transparent);
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(10px); }
}

/* --- 2. Cognitive (Solar System) --- */
.solar-system-wrapper {
  width: 100%;
  height: 700px;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  flex-direction: column;
}

.solar-system {
  position: relative;
  width: 600px;
  height: 600px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.sun-sapiens {
  position: absolute;
  z-index: 20;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.sun-body {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #ffd700, #ff8c00);
  box-shadow: 0 0 50px rgba(255, 215, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
}

.sun-label {
  margin-top: 10px;
  font-weight: bold;
  color: #b8860b;
}

.orbit-ring {
  position: absolute;
  border: 1px dashed rgba(0, 0, 0, 0.2); /* Darker dashes */
  border-radius: 50%;
  pointer-events: none; 
}

.orbit-spinner {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  animation: spin linear infinite;
  pointer-events: none;
}

.planet-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* Crucial: pass clicks through */
}

.planet-pos {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  pointer-events: none;
}

.planet-inner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: spin-reverse linear infinite;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
}

.planet-fix-orientation {
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
}

.planet-species {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s;
  width: 100px;
  pointer-events: auto; /* Re-enable clicks */
}

.planet-species:hover {
  transform: scale(1.1);
}

.planet-species.extinct {
  opacity: 0.5;
  filter: grayscale(100%);
}

.planet-body {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #fff, #ccc);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.5rem;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 2;
  border: 1px solid #999;
}

.planet-name-container {
  margin-top: 8px;
  background: rgba(255, 255, 255, 0.8);
  padding: 2px 6px;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.planet-label {
  font-size: 0.85rem;
  color: #333;
  white-space: nowrap;
}

.extinct-mark {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  color: red;
  pointer-events: none;
  z-index: 3;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes spin-reverse {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(-360deg); }
}

.interaction-hint {
  color: #666;
  font-size: 0.9rem;
  margin-top: 20px;
}

/* --- 3. Agricultural --- */
.wheat-field-container {
  width: 800px; /* Expanded width */
  max-width: 90vw;
  margin: 0 auto; /* Center */
  perspective: 1000px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.wheat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(30px, 1fr)); /* Dense wheat */
  gap: 5px;
  width: 100%;
}

.wheat-stalk {
  font-size: 1.5rem;
  cursor: pointer;
  transition: transform 0.2s;
  user-select: none;
  text-align: center;
}

.wheat-stalk:hover {
  transform: scale(1.2);
}

.wheat-overlay-text {
  margin-top: 20px;
  text-align: center;
  animation: fadeIn 1s;
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* --- 4. Unification --- */
.interaction-area-orbit {
  position: relative;
  width: 600px;
  height: 600px;
  /* border: 1px dashed #ccc; */ /* Debug ring */
}

.sapiens-brain-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 150px;
  height: 150px;
  border: 2px dashed #666;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transition: all 0.5s;
  background: #fff;
  z-index: 10;
}

.sapiens-brain-center.brain-full {
  border-color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
  box-shadow: 0 0 50px rgba(255, 215, 0, 0.3);
  transform: translate(-50%, -50%) scale(1.2);
}

.brain-icon {
  font-size: 3rem;
  margin-bottom: 5px;
}

.orbit-concept {
  position: absolute;
  /* top/left set by JS */
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.8s cubic-bezier(0.22, 1, 0.36, 1);
  background: #fff;
  padding: 10px;
  border-radius: 50%;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  width: 60px;
  height: 60px;
  justify-content: center;
}

.orbit-concept:hover {
  transform: translate(-50%, -50%) scale(1.1) !important;
  z-index: 20;
}

.concept-icon {
  font-size: 1.5rem;
}

.concept-name {
  font-size: 0.8rem;
  margin-top: 5px;
  font-weight: bold;
}

/* --- 5. Future --- */
.future-stream {
  width: 100%;
  max-width: 600px;
  position: relative;
  margin-bottom: 50px;
}

.stream-line {
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #ccc;
}

.stream-event {
  margin-left: 50px;
  margin-bottom: 40px;
  position: relative;
  opacity: 1;
}

.stream-event::before {
  content: '';
  position: absolute;
  left: -34px;
  top: 5px;
  width: 10px;
  height: 10px;
  background: #000;
  border-radius: 50%;
}

.event-year {
  font-size: 0.9rem;
  color: #000;
  margin-bottom: 5px;
  font-weight: bold;
}

.event-content h3 {
  margin: 0 0 5px 0;
  font-size: 1.2rem;
  color: #222;
}

.event-content p {
  margin: 0;
  color: #666;
  font-size: 0.95rem;
}

.final-statement {
  margin-bottom: 40px;
  text-align: center;
}

.end-quote {
  font-size: 2rem;
  font-family: 'Times New Roman', serif;
  font-style: italic;
  color: #333;
}

.back-home-btn {
  padding: 12px 30px;
  font-size: 1.1rem;
  background: #333;
  color: #fff;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.back-home-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .left-timeline-dial {
    display: none; 
  }
  
  .wheat-field-container {
    width: 100%;
  }
  
  .interaction-area-orbit {
    width: 100%;
    height: 400px;
  }
  
  .solar-system {
    transform: scale(0.6);
  }
}
/* Rating Styles */
.rating-sapiens {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    pointer-events: auto;
    z-index: 100;
    position: relative;
}
.history-btn {
    background: #f0f0e6;
    color: #5d4037;
    border: 2px solid #5d4037;
    padding: 12px 30px;
    font-family: 'Noto Serif SC', serif;
    font-size: 1.1rem;
    cursor: pointer;
    pointer-events: auto;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    transition: all 0.3s;
}
.history-btn:hover {
    background: #5d4037;
    color: #f0f0e6;
}
.history-options {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
}
.history-opt {
    background: rgba(255,255,255,0.8);
    border: 1px solid #5d4037;
    color: #5d4037;
    padding: 10px 20px;
    cursor: pointer;
    font-family: 'Noto Serif SC', serif;
}
.history-opt:hover {
    background: #5d4037;
    color: #fff;
}
</style>
