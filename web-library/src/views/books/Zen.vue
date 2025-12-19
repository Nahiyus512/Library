<template>
  <div class="zen-page" ref="pageContainer">
    <!-- Global Canvas Background -->
    <canvas ref="roadCanvas" class="road-canvas"></canvas>

    <!-- Hero Section -->
    <section class="hero-section" ref="heroSection">
      <div class="hero-content" :style="parallaxStyle">
        <div class="title-group">
          <h1 class="zen-char">禅</h1>
          <div class="english-title-group">
            <span class="et-top">ZEN AND THE ART OF</span>
            <span class="et-bottom">MOTORCYCLE MAINTENANCE</span>
          </div>
          <div class="chinese-title-sub">摩托车维修艺术</div>
        </div>
        <div class="scroll-indicator">↓ 开启旅程</div>
      </div>
    </section>

    <!-- Sticky Navigation -->
    <nav class="sticky-nav" :class="{ 'visible': showNav }">
      <button class="nav-return-btn" @click="goBack">
        <span>← 返回尘世</span>
      </button>
      <div class="nav-links">
        <a href="#section-landscape" class="nav-link" :class="{ active: activeSection === 'landscape' }">I. 旅途</a>
        <a href="#section-machine" class="nav-link" :class="{ active: activeSection === 'machine' }">II. 机器</a>
        <a href="#section-ghost" class="nav-link" :class="{ active: activeSection === 'ghost' }">III. 幽灵</a>
        <a href="#section-stuckness" class="nav-link" :class="{ active: activeSection === 'stuckness' }">IV. 困顿</a>
        <a href="#section-quality" class="nav-link" :class="{ active: activeSection === 'quality' }">V. 良质</a>
      </div>
    </nav>

    <!-- Content Sections -->
    <div class="content-wrapper">
      
      <!-- Section 1: The Landscape (Romantic) -->
      <section id="section-landscape" class="content-section landscape-section" ref="sectionLandscape">
        <div class="glass-card">
          <h2 class="section-header">I. 旅途 <span class="sub-header">The Landscape</span></h2>
          <div class="quote-block">
            "在一个急促、混乱的时代，这种旅行是一种从俗务中解脱出来的方式。"
          </div>
          <div class="text-content">
            <p>你看到的，也不再是框子里的一幅幅风景，而是身临其境的真实感受。你身在其中，不再是观众，而是参与者。这种存在感是压倒性的。</p>
            <p>此时此刻，我们正行驶在空旷的公路上，风从耳边呼啸而过。没有车窗玻璃的阻隔，路边的树木、草地和远山都变得触手可及。</p>
          </div>
          <div class="interaction-area riding-game" @mousemove="handleRiding">
            <p class="instruction">移动鼠标感受风的阻力</p>
            <div class="wind-lines" :style="windStyle">
              <div v-for="n in 20" :key="n" class="wind-line" :style="getWindLineStyle(n)"></div>
            </div>
            <div class="bike-handle" :style="handleStyle"></div>
          </div>
        </div>
      </section>

      <!-- Section 2: The Machine (Classical) -->
      <section id="section-machine" class="content-section machine-section" ref="sectionMachine">
        <div class="glass-card machine-layout">
          <div class="machine-text">
            <h2 class="section-header align-left">II. 机器 <span class="sub-header">The Machine</span></h2>
            <div class="quote-block no-border">
              "摩托车主要是一种心理现象。修理摩托车，就是修理我们自己。"
            </div>
            <div class="analysis-text">
              <p>古典视角关注底层的形式。</p>
              <p>每一个零件都有其确定的位置和功能。这不是枯燥，这是宇宙的秩序。</p>
              <p>虽然摩托车主要是由钢铁、塑料和橡胶制造的，但它也是由观念制造的。它是所谓“理性”的具体化身。一张电路图，就是理性的完美展示。</p>
            </div>
          </div>
          <div class="machine-visual-wrapper">
             <p class="machine-instruction" v-if="!engineAssembled">点击散落的零件进行组装</p>
             <p class="machine-instruction success" v-else>系统运行中</p>
             <div class="machine-visual">
               <div class="engine-assembly-game">
                 <svg viewBox="0 0 200 250" class="engine-svg">
                   <!-- Ghost Outline (Target) -->
                   <g class="engine-ghost" :style="{ opacity: engineAssembled ? 0 : 0.2 }">
                     <path d="M60,20 L140,20 L140,60 L60,60 Z" fill="none" stroke="#000" stroke-dasharray="4 2"/>
                     <path d="M60,60 L140,60 L140,140 L60,140 Z" fill="none" stroke="#000" stroke-dasharray="4 2"/>
                     <path d="M40,140 L160,140 L150,220 L50,220 Z" fill="none" stroke="#000" stroke-dasharray="4 2"/>
                   </g>
  
                   <!-- Active Parts -->
                    <g v-for="part in engineParts" :key="part.id"
                       class="engine-part"
                       :class="{ 
                         'assembled': part.isAssembled, 
                         'running': engineAssembled && part.canMove,
                         'piston-part': part.id === 3 
                       }"
                       :style="getPartStyle(part)"
                       @click="assemblePart(part)">
                       <path :d="part.d" :fill="part.color" stroke="#333" stroke-width="2"/>
                       <text v-if="!part.isAssembled" :x="getCenter(part).x" :y="getCenter(part).y" 
                            text-anchor="middle" fill="#000" dy="5">{{ part.name }}</text>
                    </g>
                   
                   <!-- Spark Effect when running -->
                   <circle v-if="engineAssembled" cx="100" cy="40" r="5" fill="#ef4444" class="spark-plug-fire">
                     <animate attributeName="opacity" values="0;1;0" dur="0.1s" repeatCount="indefinite"/>
                   </circle>
                 </svg>
               </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 3: The Ghost (Phaedrus) -->
      <section id="section-ghost" class="content-section ghost-section" ref="sectionGhost">
        <div class="glass-card ghost-card">
          <div class="ghost-terminal">
            <div class="terminal-content">
               <span class="cursor">> </span>
               <span class="typed-text">{{ displayedGhostText }}</span>
               <span class="blinking-cursor">_</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 4: The Stuckness (Gumption Trap) -->
      <section id="section-stuckness" class="content-section stuckness-section" ref="sectionStuckness">
        <div class="glass-card">
          <h2 class="section-header">IV. 困顿 <span class="sub-header">Gumption Trap</span></h2>
          <div class="quote-block">
            "那个该死的螺丝不动。你越用力，它越顽固。"
          </div>
          <div class="text-content">
             <p>困顿（Stuckness）是良质的前奏。当你感到困顿，意味着你原有的认知地图已经失效了。</p>
             <p>这正是发现新事物的机会。不要强行扭动那颗螺丝，停下来，看着它，直到你真正理解它。</p>
          </div>
          <div class="interaction-area screw-game">
            <p class="instruction" v-if="!screwSolved">找到那个“恰当”的力道 (长按)</p>
            <p class="instruction success" v-else>良质显现</p>
            
            <div class="screw-head" 
                 :class="{ 'shaking': isForcing, 'turning': isTurning, 'solved': screwSolved }"
                 @mousedown="startUnscrew" 
                 @mouseup="stopUnscrew"
                 @mouseleave="stopUnscrew">
              <div class="screw-slot"></div>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: screwProgress + '%' }"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- Section 5: Quality (Tao) -->
      <section id="section-quality" class="content-section quality-section" ref="sectionQuality">
        <div class="glass-card">
          <h2 class="section-header">V. 良质 <span class="sub-header">Quality</span></h2>
          <div class="quote-block">
            "良质就是佛。良质就是道。良质就是这一刻的现实。"
          </div>
          <div class="text-content">
            <p>良质不是一种物体，而是一个事件。它是主观意识到客观存在的那个瞬间。</p>
            <p>良质是主客体分裂之前的源头。在那个瞬间，没有主体，没有客体，只有良质。</p>
          </div>
          <div class="interaction-area balance-game" @mousemove="handleBalance">
            <p class="instruction">保持内心的平衡</p>
            <div class="balance-scale">
              <div class="balance-bubble" :style="bubbleStyle"></div>
              <div class="center-marker"></div>
            </div>
            <div class="quality-text" :style="{ opacity: qualityOpacity }">
              主客体合一
            </div>
          </div>
        </div>
      </section>

      <footer class="page-footer">
        <div class="footer-content">
          <h3>Zen and the Art of Motorcycle Maintenance</h3>
          <p>Robert M. Pirsig</p>
          
          <div v-if="!hasRated" class="rating-zen">
             <button v-if="!showRatingOptions" class="quality-btn" @click="showRatingOptions = true">检查质量</button>
             <div v-else class="quality-options">
                 <button class="quality-opt" @click="rateBook(0)">劣质 (0)</button>
                 <button class="quality-opt" @click="rateBook(1)">维修 (1)</button>
                 <button class="quality-opt" @click="rateBook(2)">禅 (2)</button>
             </div>
          </div>
          <button v-else class="back-home-btn" @click="goBack">回到现实</button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
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
      bookId: 2, // Zen ID
      bookName: "Zen and the Art of Motorcycle Maintenance",
      likeLevel: level
    });

    if (res.data.code === 200) {
      ElMessage.success('良质已发现');
      hasRated.value = true;
    } else {
      ElMessage.error(res.data.msg || '卡滞');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('无法维修');
  }
};

const pageContainer = ref<HTMLElement | null>(null);
const roadCanvas = ref<HTMLCanvasElement | null>(null);
const mouseX = ref(0);
const mouseY = ref(0);
const scrollY = ref(0);

// Sections
const heroSection = ref<HTMLElement | null>(null);
const sectionLandscape = ref<HTMLElement | null>(null);
const sectionMachine = ref<HTMLElement | null>(null);
const sectionGhost = ref<HTMLElement | null>(null);
const sectionStuckness = ref<HTMLElement | null>(null);
const sectionQuality = ref<HTMLElement | null>(null);

const activeSection = ref('');
const showNav = ref(false);

// Interaction States
const bikeAngle = ref(0);

// Engine Assembly Game
interface EnginePart {
  id: number;
  name: string;
  d: string;
  initialX: number;
  initialY: number;
  isAssembled: boolean;
  color: string;
  canMove: boolean; // For animation after assembly
}

const engineParts = ref<EnginePart[]>([
    { id: 1, name: '气缸盖', d: 'M60,20 L140,20 L140,60 L60,60 Z', initialX: -50, initialY: -40, isAssembled: false, color: '#d1d5db', canMove: false },
    { id: 2, name: '气缸体', d: 'M60,60 L140,60 L140,140 L60,140 Z', initialX: 50, initialY: -30, isAssembled: false, color: '#9ca3af', canMove: false },
    { id: 3, name: '活塞', d: 'M70,80 L130,80 L130,130 L70,130 Z', initialX: -50, initialY: 40, isAssembled: false, color: '#e5e7eb', canMove: true },
    { id: 4, name: '曲轴箱', d: 'M40,140 L160,140 L150,220 L50,220 Z', initialX: 40, initialY: 50, isAssembled: false, color: '#4b5563', canMove: false }
  ]);

const engineAssembled = computed(() => engineParts.value.every(p => p.isAssembled));

const assemblePart = (part: EnginePart) => {
  if (!part.isAssembled) {
    part.isAssembled = true;
  }
};

const getPartStyle = (part: EnginePart) => {
  if (part.isAssembled) {
    return {
      transform: 'translate(0, 0)',
      cursor: 'default'
    };
  }
  return {
    transform: `translate(${part.initialX}px, ${part.initialY}px)`,
    cursor: 'pointer'
  };
};

const getCenter = (part: EnginePart) => {
  // Simple approximation for label positioning
  // This logic works because we use translate on the group, so internal coordinates are local
  // Wait, if I use translate on the group, the internal coords are relative to 0,0 of the group?
  // No, the group is translated. The path coords are fixed.
  // So for the text, we should use the bounding box center of the path.
  // Hardcoding roughly based on path data:
  if (part.id === 1) return { x: 100, y: 40 };
  if (part.id === 2) return { x: 100, y: 100 };
  if (part.id === 3) return { x: 100, y: 105 };
  if (part.id === 4) return { x: 100, y: 180 };
  return { x: 0, y: 0 };
};

// Ghost Section Logic
const fullGhostText = `SYSTEM ERROR: DUALITY NOT FOUND
SEARCHING FOR QUALITY...
SUBJECT_OBJECT_SPLIT: CRITICAL FAILURE
PHAEDRUS_V1.0 LOADED

斐德洛认为，学校教会你模仿，而不是创造。
理性是一把刀，把世界切成碎片，而我们却忘记了世界原本是完整的。
他试图寻找那种被理性分析所遗漏的东西——那个幽灵。

良质（Quality）不是物质，不是精神，也不是两者的总和。
它是万物的源头。`;
const displayedGhostText = ref('');

// Screw Game
const isForcing = ref(false);
const isTurning = ref(false);
const screwProgress = ref(0);
const screwSolved = ref(false);
let screwInterval: any = null;

// Balance Game
const balanceX = ref(0);
const qualityOpacity = computed(() => {
  return 1 - Math.abs(balanceX.value) / 50;
});

// Event Handlers
const handleMouseMove = (e: MouseEvent) => {
  mouseX.value = (e.clientX / window.innerWidth) - 0.5;
  mouseY.value = (e.clientY / window.innerHeight) - 0.5;
};

const handleRiding = (e: MouseEvent) => {
  const x = (e.clientX / window.innerWidth) - 0.5;
  bikeAngle.value = x * 45; 
};

const handleBalance = (e: MouseEvent) => {
  const rect = (e.target as HTMLElement).getBoundingClientRect();
  const relativeX = e.clientX - rect.left - rect.width / 2;
  balanceX.value = relativeX * 0.5; 
};

// Screw Game
const startUnscrew = () => {
  if (screwSolved.value) return;
  isForcing.value = true;
  isTurning.value = true;
  isForcing.value = false;

  screwInterval = setInterval(() => {
    if (screwProgress.value < 100) {
      screwProgress.value += 1;
    } else {
      screwSolved.value = true;
      isTurning.value = false;
      clearInterval(screwInterval);
    }
  }, 50);
};

const stopUnscrew = () => {
  isForcing.value = false;
  isTurning.value = false;
  if (screwInterval) clearInterval(screwInterval);
};

// Computed Styles
const parallaxStyle = computed(() => ({
  transform: `translate(${mouseX.value * 30}px, ${mouseY.value * 30}px)`
}));

const windStyle = computed(() => ({
  transform: `skewX(${-bikeAngle.value}deg)`
}));

const handleStyle = computed(() => ({
  transform: `rotate(${bikeAngle.value}deg)`
}));

const bubbleStyle = computed(() => ({
  transform: `translateX(${balanceX.value}px)`
}));

const getWindLineStyle = (n: number) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 2}s`,
    opacity: Math.random()
  };
};

// Navigation & Scroll
const goBack = () => router.back();

let observer: IntersectionObserver;
let navObserver: IntersectionObserver;
let animationFrameId: number;

// Road Canvas Logic
const initRoad = () => {
  const canvas = roadCanvas.value;
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  const resize = () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  };
  window.addEventListener('resize', resize);
  resize();

  const draw = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Sky/Ground
    const horizonY = canvas.height * 0.4;
    
    // Sky
    const skyGradient = ctx.createLinearGradient(0, 0, 0, horizonY);
    skyGradient.addColorStop(0, '#87CEEB');
    skyGradient.addColorStop(1, '#e0f2fe');
    ctx.fillStyle = skyGradient;
    ctx.fillRect(0, 0, canvas.width, horizonY);

    // Ground
    ctx.fillStyle = '#047857'; // Zen Green
    ctx.fillRect(0, horizonY, canvas.width, canvas.height - horizonY);

    // Road Parameters
    const roadWidth = canvas.width * 0.6; // Bottom width
    const roadTopWidth = 10;
    const segmentCount = 100;
    const segmentHeight = (canvas.height - horizonY) / segmentCount;
    
    // Scroll affects "Z" position
    const zOffset = scrollY.value * 0.5; 

    ctx.beginPath();
    // Draw road segments from bottom to top
    for (let i = 0; i < segmentCount; i++) {
      const yBottom = canvas.height - (i * segmentHeight);
      const yTop = canvas.height - ((i + 1) * segmentHeight);
      
      const progressBottom = i / segmentCount;
      const progressTop = (i + 1) / segmentCount;

      // Perspective scale (1 at bottom, small at top)
      const scaleBottom = 1 - progressBottom;
      const scaleTop = 1 - progressTop;

      // Curve Logic (Sine wave based on Z + index)
      // "Winding": Changes over distance (i)
      const curveFreq = 0.05;
      const curveAmp = canvas.width * 0.5;
      
      // Calculate center X for this segment
      // zOffset moves us "forward" along the sine wave
      const realZBottom = zOffset + i;
      const xOffsetBottom = Math.sin(realZBottom * curveFreq) * curveAmp * progressBottom * progressBottom; // Square progress to curve more at distance? No, less at bottom.
      // Actually, we want the road to curve in the distance.
      // At bottom (screen), xOffset should be 0 (center) relative to camera, unless turning.
      // Let's make the curve purely a function of Z (world position).
      
      const worldZBottom = zOffset + i * 2; // Map screen Y to World Z roughly
      const worldZTop = zOffset + (i + 1) * 2;

      // Curve function
      const getCurve = (z: number) => Math.sin(z * 0.02) * 300;

      const curveBottom = getCurve(worldZBottom);
      const curveTop = getCurve(worldZTop);

      // Perspective projection
      // xScreen = (xWorld - xCamera) * scale + xCenter
      // We assume xCamera follows the road? Or fixed?
      // Fixed camera, winding road.
      
      const wBottom = roadWidth * scaleBottom + (roadTopWidth * (1-scaleBottom));
      const wTop = roadWidth * scaleTop + (roadTopWidth * (1-scaleTop));

      const xCenterBottom = (canvas.width / 2) + curveBottom * scaleBottom; // Apply perspective to curve offset
      const xCenterTop = (canvas.width / 2) + curveTop * scaleTop;

      // Draw Trapezoid
      ctx.fillStyle = (Math.floor(worldZBottom / 20) % 2 === 0) ? '#333' : '#3a3a3a'; // Striped road? Or just solid
      ctx.fillStyle = '#333';
      
      // Draw Grass/Ground side details? Nah, just road.
      
      // Main Road Body
      ctx.beginPath();
      ctx.moveTo(xCenterBottom - wBottom / 2, yBottom);
      ctx.lineTo(xCenterBottom + wBottom / 2, yBottom);
      ctx.lineTo(xCenterTop + wTop / 2, yTop);
      ctx.lineTo(xCenterTop - wTop / 2, yTop);
      ctx.fill();

      // Center Line
      if (Math.floor(worldZBottom / 10) % 2 === 0) {
        const lineWBottom = 5 * scaleBottom;
        const lineWTop = 5 * scaleTop;
        ctx.fillStyle = '#fff';
        ctx.beginPath();
        ctx.moveTo(xCenterBottom - lineWBottom / 2, yBottom);
        ctx.lineTo(xCenterBottom + lineWBottom / 2, yBottom);
        ctx.lineTo(xCenterTop + lineWTop / 2, yTop);
        ctx.lineTo(xCenterTop - lineWTop / 2, yTop);
        ctx.fill();
      }
    }

    animationFrameId = requestAnimationFrame(draw);
  };
  draw();
};

onMounted(() => {
  const container = document.querySelector('.zen-page');
  if (container) {
    container.addEventListener('scroll', () => {
      scrollY.value = container.scrollTop;
      
      // Ghost Text Logic
      if (sectionGhost.value) {
        const rect = sectionGhost.value.getBoundingClientRect();
        const windowHeight = window.innerHeight;
        
        // Start typing when section top is at 80% of viewport height (entering)
        // Finish typing when section top is at 20% of viewport height (fully visible)
        const startPoint = windowHeight * 0.8;
        const endPoint = windowHeight * 0.2;
        const totalDistance = startPoint - endPoint;
        
        let progress = (startPoint - rect.top) / totalDistance;
        progress = Math.min(1, Math.max(0, progress));

        const charCount = Math.floor(progress * fullGhostText.length);
        displayedGhostText.value = fullGhostText.substring(0, charCount);
      }
    });
  }

  initRoad();

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        if (entry.target.id === 'section-landscape') activeSection.value = 'landscape';
        if (entry.target.id === 'section-machine') activeSection.value = 'machine';
        if (entry.target.id === 'section-ghost') activeSection.value = 'ghost';
        if (entry.target.id === 'section-stuckness') activeSection.value = 'stuckness';
        if (entry.target.id === 'section-quality') activeSection.value = 'quality';
      }
    });
  }, { threshold: 0.1 });

  navObserver = new IntersectionObserver((entries) => {
    // Show nav when hero section is not intersecting (scrolled out of view)
    showNav.value = !entries[0].isIntersecting;
  }, { threshold: 0.1 });

  if (heroSection.value) navObserver.observe(heroSection.value);

  [sectionLandscape, sectionMachine, sectionGhost, sectionStuckness, sectionQuality].forEach(s => {
    if (s.value) observer.observe(s.value);
  });
});

onUnmounted(() => {
  if (observer) observer.disconnect();
  if (navObserver) navObserver.disconnect();
  if (screwInterval) clearInterval(screwInterval);
  cancelAnimationFrame(animationFrameId);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700&family=Share+Tech+Mono&display=swap');

  /* New Styles for Added Content */
  .text-content {
    margin-top: 20px;
    font-size: 1rem;
    line-height: 1.8;
    text-align: left;
    color: #444;
  }
  .text-content p {
    margin-bottom: 15px;
    text-indent: 2em;
  }

  /* Engine Assembly Game */
  .machine-visual-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
  }

  .machine-instruction {
    margin-bottom: 20px;
    font-size: 0.9rem;
    color: #666;
    text-transform: uppercase;
    letter-spacing: 1px;
    text-align: center;
    font-family: 'Microsoft YaHei', sans-serif;
  }
  .machine-instruction.success {
    color: #047857;
    font-weight: bold;
  }

  .engine-assembly-game {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
  }
  .engine-svg {
    width: 100%;
    height: 300px;
    overflow: visible;
  }
  .engine-part {
    transition: transform 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
  }
  .engine-part text {
    opacity: 0.8;
    pointer-events: none;
    font-family: 'Microsoft YaHei', sans-serif;
    font-size: 14px;
    font-weight: bold;
  }
  .engine-part:hover text {
    opacity: 1;
    font-weight: bold;
  }
  .engine-part.running {
    animation: engineVibration 0.1s infinite;
  }
  /* Specific part animations when running */
  .engine-part.running.piston-part {
    animation: pistonMove 0.2s infinite alternate ease-in-out;
  }
  
  @keyframes engineVibration {
    0% { transform: translate(0, 0); }
    25% { transform: translate(1px, 1px); }
    50% { transform: translate(-1px, 0); }
    75% { transform: translate(0, -1px); }
    100% { transform: translate(0, 0); }
  }
  
  @keyframes pistonMove {
    from { transform: translateY(0); }
    to { transform: translateY(20px); }
  }

  /* Ghost Explanation */
  .typed-text {
    white-space: pre-wrap;
  }
  
  .zen-page {
  width: 100%;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #e5e5e5;
  color: #111;
  font-family: 'Microsoft YaHei', sans-serif;
  position: relative;
  scroll-behavior: smooth;
}

/* Global Road Background */
.road-canvas {
  position: fixed;
  top: 0; left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

  /* Hero Section */
  .hero-section {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    z-index: 10;
  }
  
  .hero-content {
    text-align: center;
  }
  
  .zen-char {
    font-size: 20rem;
    line-height: 1;
    opacity: 0.8;
    color: #111;
    font-weight: 900;
    text-shadow: 0 10px 30px rgba(0,0,0,0.2);
  }
  
  .english-title-group {
    display: flex;
    flex-direction: column;
    font-family: 'Arial Black', sans-serif;
    text-transform: uppercase;
    line-height: 0.9;
    margin-bottom: 20px;
  }
  
  .et-top {
    font-size: 1.5rem;
    letter-spacing: 5px;
    color: #047857;
  }
  
  .et-bottom {
    font-size: 2.5rem;
    letter-spacing: -2px;
    color: #111;
  }
  
  .chinese-title-sub {
    font-size: 1.5rem;
    letter-spacing: 10px;
    font-weight: 300;
    border-top: 2px solid #000;
    padding-top: 20px;
    display: inline-block;
  }
  
  .scroll-indicator {
    margin-top: 50px;
    animation: bounce 2s infinite;
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 2px;
  }

 /* Nav */
.sticky-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(10px);
  padding: 15px 40px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  z-index: 50;
  border-bottom: 1px solid rgba(0,0,0,0.1);
  transform: translateY(-100%);
  transition: transform 0.3s ease;
}
.sticky-nav.visible { transform: translateY(0); }

.nav-return-btn {
  justify-self: start;
  background: transparent;
  border: 1px solid #047857;
  color: #047857;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-family: 'Noto Serif SC', serif;
}

.nav-return-btn:hover {
  background: #047857;
  color: #fff;
}

.nav-links {
  grid-column: 2;
  display: flex;
  gap: 30px;
}

.nav-link {
  text-decoration: none;
  color: #666;
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  transition: color 0.3s;
}
.nav-link.active, .nav-link:hover { color: #047857; }

/* Content Sections */
.content-wrapper {
  position: relative;
  z-index: 5;
  padding-bottom: 100px;
}

.content-section {
  min-height: 100vh;
  padding: 100px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Glass Card Style */
.glass-card {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
  padding: 60px;
  max-width: 900px;
  width: 100%;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.1);
}

.section-header {
  font-size: 2.5rem;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #111;
}

.section-header.align-left {
  align-items: flex-start;
}

.sub-header {
  font-size: 1rem;
  color: #555;
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-top: 10px;
}

.quote-block {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.2rem;
  color: #333;
  font-style: italic;
  margin-bottom: 50px;
  padding: 0 40px;
  border-left: 3px solid #047857;
}

.quote-block.no-border { border-left: none; padding-left: 0; }

.interaction-area {
  min-height: 300px;
  background: rgba(255,255,255,0.5);
  border-radius: 10px;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.instruction {
  position: absolute;
  top: 20px;
  font-size: 0.8rem;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 1px;
  width: 100%;
  text-align: center;
}

/* Section 1: Riding */
.riding-game { background: transparent; }
.wind-lines { position: absolute; width: 100%; height: 100%; pointer-events: none; }
.wind-line { position: absolute; width: 2px; height: 100px; background: rgba(0,0,0,0.2); animation: windFall 0.5s linear infinite; }
@keyframes windFall { from { transform: translateY(-100px); } to { transform: translateY(400px); } }
.bike-handle {
  width: 200px; height: 20px; background: #333;
  border-radius: 10px;
  position: absolute; bottom: 50px;
  transition: transform 0.1s ease-out;
}

/* Section 2: Machine (Redesign) */
.machine-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: center;
}
.machine-visual {
  height: 400px;
  background: #1e293b;
  border-radius: 10px;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: crosshair;
}
.blueprint-grid {
  position: absolute; top:0; left:0; width:100%; height:100%;
  background-image: linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px),
  linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.5;
}
.mechanical-system {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.gear-assembly {
  position: absolute;
  left: 20%;
  width: 150px;
  height: 150px;
}
.gear {
  position: absolute;
  transform-origin: center;
}
.big-gear {
  width: 100px; height: 100px;
  top: 0; left: 0;
}
.small-gear {
  width: 60px; height: 60px;
  top: 85px; left: 85px;
}
.piston-assembly {
  position: absolute;
  right: 20%;
  width: 100px;
  height: 200px;
  border: 2px solid rgba(255,255,255,0.2);
  display: flex;
  justify-content: center;
  align-items: center;
}
.cylinder-block {
  width: 80%;
  height: 80%;
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.piston {
  width: 30px;
  height: 40px;
  background: rgba(255,255,255,0.8);
  border-radius: 4px;
}
.analysis-text { margin-top: 20px; font-size: 1rem; line-height: 1.8; color: #333; }

/* Section 3: Ghost (Typewriter) */
.ghost-card {
  background: #000 !important; /* Force black background for ghost section */
  border: 1px solid #333;
}
.ghost-terminal {
  font-family: 'Share Tech Mono', monospace;
  color: #0f0;
  min-height: 300px;
  display: flex;
  align-items: flex-start;
  padding: 20px;
}
.terminal-content { white-space: pre-wrap; text-align: left; }
.typed-text { text-shadow: 0 0 5px #0f0; }
.blinking-cursor { animation: blink 1s infinite; }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

/* Section 4: Stuckness */
.screw-game { background: rgba(255,255,255,0.5); }
.screw-head {
  width: 100px; height: 100px; border-radius: 50%;
  background: #d1d5db;
  border: 1px solid #9ca3af;
  position: relative;
  cursor: pointer;
  transition: transform 0.1s;
  display: flex; justify-content: center; align-items: center;
  box-shadow: inset 2px 2px 5px rgba(255,255,255,0.8), inset -2px -2px 5px rgba(0,0,0,0.2);
}
.screw-slot { width: 80%; height: 15px; background: #6b7280; border-radius: 2px; }
.screw-head.shaking { animation: shake 0.1s infinite; background: #fca5a5; }
.screw-head.turning { transform: rotate(15deg); transition: transform 2s linear; }
.screw-head.solved { transform: rotate(90deg); background: #86efac; transition: transform 0.5s ease; }
.progress-bar { width: 200px; height: 4px; background: #eee; margin-top: 30px; border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: #047857; width: 0%; transition: width 0.1s linear; }
@keyframes shake { 0% { transform: translate(1px, 1px) rotate(0deg); } 20% { transform: translate(-1px, -1px) rotate(-1deg); } 100% { transform: translate(0,0); } }

/* Section 5: Quality */
.quality-section .glass-card { border: 2px solid #047857; }
.balance-game { background: transparent; }
.balance-scale {
  width: 300px; height: 10px; background: #eee; border-radius: 5px;
  position: relative; overflow: hidden;
}
.balance-bubble {
  width: 20px; height: 20px; background: #047857; border-radius: 50%;
  position: absolute; top: 50%; left: 50%; margin-left: -10px; margin-top: -10px;
  transition: transform 0.1s ease-out;
  box-shadow: 0 0 10px #047857;
}
.center-marker {
  position: absolute; left: 50%; top: 0; height: 100%; width: 2px; background: rgba(0,0,0,0.2);
}
.quality-text {
  font-size: 2rem; font-weight: 900; color: #047857; margin-top: 30px;
  transition: opacity 0.2s;
}

/* Footer */
.page-footer {
  padding: 50px 0;
  text-align: center;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 10;
}
.back-home-btn {
  margin-top: 20px;
  background: #000;
  color: #fff;
  border: none;
  padding: 10px 30px;
  cursor: pointer;
}

@keyframes bounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(10px); } }
/* Rating Styles */
.rating-zen {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    margin-top: 20px;
}
.quality-btn {
    background: transparent;
    color: #047857;
    border: 2px solid #047857;
    padding: 10px 30px;
    font-family: 'Noto Serif SC', serif;
    font-weight: bold;
    font-size: 1.1rem;
    cursor: pointer;
    border-radius: 30px;
    transition: all 0.3s;
}
.quality-btn:hover {
    background: #047857;
    color: #fff;
}
.quality-options {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
}
.quality-opt {
    background: #fff;
    border: 1px solid #ccc;
    color: #333;
    padding: 8px 20px;
    cursor: pointer;
    font-family: 'Noto Serif SC', serif;
    border-radius: 20px;
}
.quality-opt:hover {
    border-color: #047857;
    color: #047857;
}
</style>
