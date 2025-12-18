<template>
  <div class="journey-vn-container" :class="sceneClass">
    <div class="custom-cursor"></div>
    
    <!-- Global HUD -->
    <div class="hud" v-if="currentScene > 0">
      <div class="hud-top">
        <button class="return-btn" @click="goBackHome">
          <span>← 返回藏书阁</span>
        </button>
        <div class="karma-meter">
          <span>修行值</span>
          <div class="bar-container">
            <div class="bar-fill" :style="{ width: karma + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Transition Overlay -->
    <transition name="fade">
      <div class="chapter-transition" v-if="showTransition">
        <div class="transition-content">
          <h2>{{ transitionTitle }}</h2>
          <p>{{ transitionSubtitle }}</p>
        </div>
      </div>
    </transition>

    <!-- Scene Content -->
    <transition name="fade-morph" mode="out-in">

      <!-- Scene 0: Intro (Home - Cover Style) -->
      <div v-if="currentScene === 0" key="scene0" class="scene scene-intro" @click="startJourney" @mousemove="handleMouseMove" @mouseleave="resetParallax">
        <!-- Background Clouds -->
        <div class="clouds-bg"></div>

        <!-- Central Cudgel -->
        <div class="cudgel-container" :style="cudgelStyle">
          <div class="cudgel-bar">
            <div class="cudgel-shimmer"></div>
            <div class="cudgel-text">如意金箍棒</div>
          </div>
          <div class="cudgel-glow"></div>
        </div>

        <!-- Title -->
        <div class="title-container" :style="titleStyle">
          <h1 class="title-cn">西游记</h1>
          <div class="motto">PASSION • DISCIPLINE • ENLIGHTENMENT</div>
          <div class="start-hint fade-in-delayed-3">点击开启修行</div>
        </div>

        <!-- Floating Particles -->
        <div class="particles">
          <div v-for="n in 20" :key="n" class="particle" :style="getParticleStyle()"></div>
        </div>
      </div>
      
      <!-- Scene 1: Stone Monkey (Birth) -->
      <div v-else-if="currentScene === 1" key="scene1" class="scene scene-stone">
        <h2 class="scene-header">灵石孕育</h2>
        <p class="excerpt">"盖自开辟以来，每受天真地秀，日精月华，感之既久，遂有灵通之意。"</p>
        <div class="stone-container" @click="crackStone" :class="{ 'cracked': isCracked }">
          <div class="stone-core">
            <div class="fissure"></div>
            <div class="inner-glow"></div>
          </div>
          <p class="instruction">{{ isCracked ? '石破天惊！' : '点击灵石唤醒心猿' }}</p>
        </div>
      </div>

      <!-- Scene 2: Havoc (Rebellion) -->
      <div v-else-if="currentScene === 2" key="scene2" class="scene scene-havoc">
        <div class="havoc-stage">
          <div class="palace-fragment" v-for="(item, index) in palaceItems" :key="index" @click="destroyFragment(index)" :class="{ 'destroyed': item.destroyed }">
            <div class="fragment-icon">{{ item.icon }}</div>
            <div class="fragment-name">{{ item.name }}</div>
          </div>
        </div>
        <p class="excerpt">"强者为尊该让我，英雄只此敢争先。"</p>
        <p class="instruction">击碎天宫旧序 ({{ destroyedCount }}/{{ palaceItems.length }})</p>
      </div>
      
      <!-- Scene 3: Five Elements (Suppression) -->
      <div v-else-if="currentScene === 3" key="scene3" class="scene scene-mountain">
        <div class="mountain-shape">
          <div class="mountain-triangle"></div>
          <div class="seal-talisman">唵嘛呢叭咪吽</div>
        </div>
        <p class="excerpt">"恶贯满盈身受困，善根不绝气还升。"</p>
        <div class="meditation-area" @mousedown="startMeditate" @mouseup="stopMeditate" @mouseleave="stopMeditate">
          <div class="meditation-circle" :style="{ transform: `scale(${meditationProgress / 100})` }"></div>
          <p>{{ meditationProgress >= 100 ? '心猿归正' : '长按以此静心' }}</p>
        </div>
      </div>

      <!-- Scene 4: The Journey (Trials) -->
      <div v-else-if="currentScene === 4" key="scene4" class="scene scene-trials">
        <p class="excerpt">"心生，种种魔生；心灭，种种魔灭。"</p>
        <div class="trial-path">
          <div class="trial-node" 
               v-for="(trial, index) in trials" 
               :key="index"
               :class="{ 'completed': trial.completed, 'active': currentTrialIndex === index }"
               @click="attemptTrial(index)">
            <div class="node-icon">{{ trial.icon }}</div>
            <div class="node-label">{{ trial.name }}</div>
          </div>
        </div>
        <div class="dialogue-box" v-if="currentTrialIndex < trials.length">
          <p class="dialogue-text">"{{ trials[currentTrialIndex].riddle }}"</p>
          <div class="choices">
            <button v-for="choice in trials[currentTrialIndex].choices" :key="choice" @click="resolveTrial(choice)">
              {{ choice }}
            </button>
          </div>
        </div>
      </div>

      <!-- Scene 5: Enlightenment (Ending) -->
      <div v-else-if="currentScene === 5" key="scene5" class="scene scene-enlightenment">
        <div class="void-light"></div>
        <div class="scripture-rain">
          <div class="rain-col" v-for="n in 10" :key="n" :style="{ animationDelay: Math.random() * 5 + 's', left: Math.random() * 100 + '%' }">
            般若波罗蜜多心经
          </div>
        </div>
        <div class="final-text">
          <h1>斗战胜佛</h1>
          <p>本来无一物，何处惹尘埃。</p>
          <button class="restart-btn" @click="restart">重入红尘</button>
        </div>
      </div>

    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentScene = ref(0);
const karma = ref(0); // Progress/Karma meter
const showTransition = ref(false);
const transitionTitle = ref('');
const transitionSubtitle = ref('');

// --- Scene 0: Intro Logic ---
const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e: MouseEvent) => {
  const width = window.innerWidth;
  const height = window.innerHeight;
  mouseX.value = (e.clientX / width - 0.5) * 2;
  mouseY.value = (e.clientY / height - 0.5) * 2;
};

const resetParallax = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

const cudgelStyle = computed(() => ({
  transform: `translate(${mouseX.value * 20}px, ${mouseY.value * 20}px) rotate(${mouseX.value * 10}deg)`
}));

const titleStyle = computed(() => ({
  transform: `translate(${mouseX.value * -15}px, ${mouseY.value * -15}px)`
}));

const getParticleStyle = () => ({
  left: `${Math.random() * 100}%`,
  top: `${Math.random() * 100}%`,
  animationDelay: `${Math.random() * 5}s`,
  opacity: Math.random() * 0.6 + 0.2
});

const startJourney = () => {
  triggerTransition('第一回', '灵根育孕源流出', 1);
};

// --- Scene 1: Stone Logic ---
const isCracked = ref(false);
const crackStone = () => {
  if (isCracked.value) return;
  isCracked.value = true;
  karma.value += 20;
  setTimeout(() => {
    triggerTransition('第二回', '乱蟠桃大圣偷丹', 2);
  }, 2000);
};

// --- Scene 2: Havoc Logic ---
const palaceItems = ref([
  { name: '凌霄殿', icon: '🏛️', destroyed: false },
  { name: '炼丹炉', icon: '🔥', destroyed: false },
  { name: '蟠桃园', icon: '🍑', destroyed: false },
  { name: '生死簿', icon: '📜', destroyed: false }
]);
const destroyedCount = computed(() => palaceItems.value.filter(i => i.destroyed).length);

const destroyFragment = (index: number) => {
  if (!palaceItems.value[index].destroyed) {
    palaceItems.value[index].destroyed = true;
    if (destroyedCount.value === palaceItems.value.length) {
      karma.value += 20;
      setTimeout(() => {
        triggerTransition('第三回', '五行山下定心猿', 3);
      }, 1500);
    }
  }
};

// --- Scene 3: Five Elements Logic ---
const meditationProgress = ref(0);
let meditationInterval: any = null;

const startMeditate = () => {
  meditationInterval = setInterval(() => {
    if (meditationProgress.value < 100) {
      meditationProgress.value += 2;
    } else {
      clearInterval(meditationInterval);
      karma.value += 20;
      setTimeout(() => {
        triggerTransition('第四回', '九九八十一难', 4);
      }, 1000);
    }
  }, 50);
};

const stopMeditate = () => {
  clearInterval(meditationInterval);
  if (meditationProgress.value < 100) {
    meditationProgress.value = 0;
  }
};

// --- Scene 4: Trials Logic ---
const currentTrialIndex = ref(0);
const trials = ref([
  { name: '白骨精', icon: '💀', completed: false, riddle: '眼见未必为实，何以破妄？', choices: ['火眼金睛', '听信谗言'], correct: '火眼金睛' },
  { name: '女儿国', icon: '👑', completed: false, riddle: '问世间情为何物？', choices: ['留恋红尘', '一心向佛'], correct: '一心向佛' },
  { name: '真假猴王', icon: '🐒', completed: false, riddle: '二心竞斗，谁是真我？', choices: ['打死六耳', '自证清白'], correct: '打死六耳' }
]);

const resolveTrial = (choice: string) => {
  const currentTrial = trials.value[currentTrialIndex.value];
  if (choice === currentTrial.correct) {
    currentTrial.completed = true;
    currentTrialIndex.value++;
    if (currentTrialIndex.value >= trials.value.length) {
      karma.value += 20;
      setTimeout(() => {
        triggerTransition('第五回', '径回东土 五圣成真', 5);
      }, 1000);
    }
  } else {
    // Reincarnation Logic (Restart Chapter)
    triggerTransition('轮回', '一念之差 重入轮回', 4, () => {
      currentTrialIndex.value = 0;
      trials.value.forEach(t => t.completed = false);
    });
  }
};
const attemptTrial = (index: number) => {
  // Logic handled by choices
  console.log('Attempting trial', index);
};

// --- Scene 5: Enlightenment Logic ---
const restart = () => {
  currentScene.value = 0;
  karma.value = 0;
  isCracked.value = false;
  palaceItems.value.forEach(i => i.destroyed = false);
  meditationProgress.value = 0;
  currentTrialIndex.value = 0;
  trials.value.forEach(t => t.completed = false);
};

// --- Helpers ---
const triggerTransition = (title: string, subtitle: string, nextSceneIdx: number, onComplete?: () => void) => {
  transitionTitle.value = title;
  transitionSubtitle.value = subtitle;
  showTransition.value = true;
  setTimeout(() => {
    currentScene.value = nextSceneIdx;
    if (onComplete) onComplete();
    showTransition.value = false;
  }, 2000);
};

const goBackHome = () => {
  router.back();
};

const sceneClass = computed(() => {
  switch (currentScene.value) {
    case 0: return 'bg-intro';
    case 1: return 'bg-stone';
    case 2: return 'bg-havoc';
    case 3: return 'bg-mountain';
    case 4: return 'bg-trials';
    case 5: return 'bg-enlightenment';
    default: return '';
  }
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@300;700;900&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&display=swap');

.journey-vn-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  color: #ffd700;
  font-family: 'Noto Serif SC', serif;
  transition: background 1s ease;
}

/* Backgrounds */
.bg-intro { background: radial-gradient(circle, #2c1e15 0%, #000 100%); }
.bg-stone { background: #1a1510; }
.bg-havoc { background: #200; }
.bg-mountain { background: #111; }
.bg-trials { background: #0a0a1a; }
.bg-enlightenment { background: #fffdf0; color: #333; }

/* HUD */
.hud {
  position: absolute;
  top: 0; left: 0; width: 100%;
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  z-index: 100;
  box-sizing: border-box;
}

.hud-top {
  display: flex;
  gap: 40px;
  align-items: center;
}

.date-display {
  font-family: 'Montserrat', sans-serif;
  font-weight: 700;
  letter-spacing: 2px;
  border-bottom: 2px solid #ffd700;
  padding-bottom: 5px;
}

.karma-meter {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.bar-container {
  width: 150px;
  height: 4px;
  background: rgba(255, 215, 0, 0.2);
}

.bar-fill {
  height: 100%;
  background: #ffd700;
  transition: width 0.5s ease;
  box-shadow: 0 0 10px #ffd700;
}

.return-btn {
  background: none;
  border: 1px solid #ffd700;
  color: #ffd700;
  padding: 8px 16px;
  cursor: pointer;
  font-family: 'Noto Serif SC', serif;
  transition: all 0.3s;
}

.return-btn:hover {
  background: #ffd700;
  color: #000;
}

/* Transitions */
.chapter-transition {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: #2c1e15; /* Dark Brown */
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #ffd700; /* Gold */
}

.transition-content h2 {
  font-family: 'Ma Shan Zheng', cursive;
  font-size: 4rem;
  margin: 0;
}

.transition-content p {
  font-size: 1.5rem;
  letter-spacing: 0.5rem;
  text-align: center;
  margin-top: 1rem;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.5s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.fade-morph-enter-active, .fade-morph-leave-active { transition: opacity 0.8s ease; }
.fade-morph-enter-from, .fade-morph-leave-to { opacity: 0; }

/* Scene Styles */
.scene {
  width: 100%; height: 100%;
  position: absolute;
  top: 0; left: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* Scene 0: Intro (Reused from Cover) */
.scene-intro { cursor: pointer; }
.clouds-bg {
  position: absolute; width: 100%; height: 100%; opacity: 0.2;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
}
.cudgel-container {
  position: absolute; z-index: 10;
  transition: transform 0.1s ease-out;
}
.cudgel-bar {
  width: 30px; height: 800px;
  background: linear-gradient(90deg, #b8860b, #ffd700, #b8860b);
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.4);
  display: flex; justify-content: center; align-items: center;
  overflow: hidden; position: relative;
}
.cudgel-shimmer {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(180deg, transparent, rgba(255,255,255,0.6), transparent);
  animation: shimmer 3s infinite linear;
}
.cudgel-text {
  writing-mode: vertical-rl; color: #5c3a21; font-weight: bold; letter-spacing: 4px;
}
.title-container {
  z-index: 20; text-align: center; pointer-events: none; mix-blend-mode: overlay;
}
.title-en { font-family: 'Montserrat'; font-size: 6rem; font-weight: 900; line-height: 0.9; margin: 0; opacity: 0.8; color: #fff; }
.motto { letter-spacing: 4px; margin: 15px 0; color: #ffd700; font-weight: 700; }
.title-cn { font-size: 15rem; margin: 0; letter-spacing: 0.5rem; }
.start-hint { margin-top: 2rem; animation: pulse 2s infinite; font-size: 0.9rem; }
.particles { position: absolute; width: 100%; height: 100%; pointer-events: none; }
.particle { position: absolute; background: #ffd700; border-radius: 50%; width: 4px; height: 4px; }

/* Scene 1: Stone */
.scene-stone {
  text-align: center;
}
.excerpt {
  font-family: 'Kaiti SC', serif;
  font-size: 1.2rem;
  color: #ffd700;
  opacity: 0.8;
  margin: 0 auto 2rem;
  max-width: 600px;
  line-height: 1.6;
}
.stone-container {
  cursor: pointer;
  text-align: center;
}
.stone-core {
  width: 200px; height: 200px; background: #333; border-radius: 50%;
  position: relative; margin: 0 auto 2rem;
  transition: transform 0.3s;
  box-shadow: inset 0 0 40px #000;
}
.fissure {
  position: absolute; top: 10%; left: 45%; width: 10%; height: 80%;
  background: #000; transform: scaleY(0); transition: transform 0.5s;
}
.stone-container.cracked .fissure { transform: scaleY(1); }
.inner-glow {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  border-radius: 50%;
  background: radial-gradient(circle, #ffd700 0%, transparent 70%);
  opacity: 0; transition: opacity 0.5s;
}
.stone-container.cracked .inner-glow { opacity: 1; box-shadow: 0 0 100px #ffd700; }

/* Scene 2: Havoc */
.havoc-stage {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 40px;
}
.palace-fragment {
  padding: 20px; border: 1px solid #ffd700; text-align: center; cursor: crosshair;
  transition: all 0.3s;
}
.palace-fragment:hover { background: rgba(255, 215, 0, 0.1); }
.palace-fragment.destroyed { opacity: 0.2; transform: scale(0.9) rotate(10deg); text-decoration: line-through; pointer-events: none; }
.fragment-icon { font-size: 3rem; margin-bottom: 10px; }

/* Scene 3: Mountain */
.mountain-shape {
  position: absolute; top: 20%; left: 50%; transform: translateX(-50%);
  width: 0; height: 0;
  border-left: 200px solid transparent;
  border-right: 200px solid transparent;
  border-bottom: 300px solid #222;
  transition: transform 0.1s;
}
.mountain-shape.shaking {
  animation: shake 0.1s infinite;
}
.seal-talisman {
  position: absolute; top: 50px; left: -25px; writing-mode: vertical-rl;
  font-family: 'Ma Shan Zheng', cursive; font-size: 2rem; color: #ffd700;
  background: #800000; padding: 15px 5px; box-shadow: 2px 2px 10px rgba(0,0,0,0.5);
  transform: rotate(15deg);
}
.meditation-area {
  z-index: 10; cursor: pointer; position: absolute; bottom: 20%;
  left: 50%; transform: translateX(-50%); text-align: center;
}
.meditation-circle {
  width: 150px; height: 150px; border: 2px solid #ffd700; border-radius: 50%;
  margin: 0 auto 20px; background: rgba(255, 215, 0, 0.1);
  transition: transform 0.1s linear;
}

@keyframes shake {
  0% { transform: translateX(-50%) translate(1px, 1px) rotate(0deg); }
  10% { transform: translateX(-50%) translate(-1px, -2px) rotate(-1deg); }
  20% { transform: translateX(-50%) translate(-3px, 0px) rotate(1deg); }
  30% { transform: translateX(-50%) translate(3px, 2px) rotate(0deg); }
  40% { transform: translateX(-50%) translate(1px, -1px) rotate(1deg); }
  50% { transform: translateX(-50%) translate(-1px, 2px) rotate(-1deg); }
  60% { transform: translateX(-50%) translate(-3px, 1px) rotate(0deg); }
  70% { transform: translateX(-50%) translate(3px, 1px) rotate(-1deg); }
  80% { transform: translateX(-50%) translate(-1px, -1px) rotate(1deg); }
  90% { transform: translateX(-50%) translate(1px, 2px) rotate(0deg); }
  100% { transform: translateX(-50%) translate(1px, -2px) rotate(-1deg); }
}

/* Scene 4: Trials */
.trial-path {
  display: flex; gap: 50px; margin-bottom: 50px;
}
.trial-node {
  width: 80px; height: 80px; border: 1px solid #555; border-radius: 50%;
  display: flex; flex-direction: column; justify-content: center; align-items: center;
  opacity: 0.5; transition: all 0.3s;
}
.trial-node.active { opacity: 1; border-color: #ffd700; transform: scale(1.1); box-shadow: 0 0 20px rgba(255,215,0,0.3); }
.trial-node.completed { opacity: 1; background: #ffd700; color: #000; }
.node-icon { font-size: 2rem; }
.dialogue-box {
  background: rgba(0,0,0,0.8); border: 1px solid #ffd700; padding: 30px;
  text-align: center; max-width: 600px;
}
.dialogue-text { font-size: 1.5rem; margin-bottom: 30px; }
.choices { display: flex; gap: 20px; justify-content: center; }
.choices button {
  background: transparent; border: 1px solid #ffd700; color: #ffd700;
  padding: 10px 20px; cursor: pointer; font-family: 'Noto Serif SC';
  transition: all 0.3s;
}
.choices button:hover { background: #ffd700; color: #000; }

/* Scene 5: Enlightenment */
.scene-enlightenment {
  text-align: center;
}
.scripture-rain {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none; opacity: 0.1;
}
.rain-col {
  position: absolute; top: -50%; writing-mode: vertical-rl;
  font-family: 'Kaiti SC', serif; font-size: 1.5rem; color: #d4af37;
  animation: rain 20s linear infinite;
}
.final-text h1 { font-family: 'Ma Shan Zheng'; font-size: 5rem; margin-bottom: 20px; color: #d4af37; }
.final-text p { font-size: 1.5rem; color: #666; margin-bottom: 40px; }
.restart-btn {
  padding: 15px 40px; border: 2px solid #d4af37; background: transparent;
  color: #d4af37; font-size: 1.2rem; cursor: pointer; transition: all 0.3s;
}
.restart-btn:hover { background: #d4af37; color: #fff; }

@keyframes rain {
  to { transform: translateY(150vh); }
}

@keyframes shimmer {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100%); }
}
@keyframes pulse {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}
</style>