<template>
  <div class="book-card-wrapper" @click="handleClick">
    <div class="book-shadow"></div>
    <div class="book-body unified-card" :class="{ 'expanded-mode': isExpanded }">
      <!-- Top Section (5%) -->
      <div class="card-top">
        <span class="book-index">{{ formattedId }}</span>
        <div class="archived-tag">
          <div class="black-square"></div>
          <span>ARCHIVED</span>
        </div>
      </div>

      <!-- Middle Section (55%) -->
      <div class="card-middle">
        <!-- Category Tag -->
        <div class="category-tag">{{ book.category }}</div>
        
        <!-- Three-Body Problem -->
        <ThreeBodyCover v-if="book.id === 1" />

        <!-- Sapiens -->
        <SapiensCover v-else-if="book.id === 2" />

        <!-- Hitchhiker -->
        <HitchhikersGuideCover v-else-if="book.id === 3" />

        <!-- Interaction of Color -->
        <InteractionOfColorCover v-else-if="book.id === 4" />

        <!-- The Non-Designer's Design Book -->
        <NonDesignersDesignBookCover v-else-if="book.id === 5" />

        <!-- Grid Systems -->
        <GridSystemsCover v-else-if="book.id === 6" />

        <!-- Dune -->
        <DuneCover v-else-if="book.id === 7" />

        <!-- 1984 -->
        <NineteenEightyFourCover v-else-if="book.id === 8" />

        <!-- Brave New World -->
        <BraveNewWorldCover v-else-if="book.id === 9" />

        <!-- Amusing Ourselves to Death -->
        <AmusingOurselvesToDeathCover v-else-if="book.id === 11" />

        <!-- Life 3.0 -->
        <Life30Cover v-else-if="book.id === 12" />

        <!-- Default -->
        <div v-else class="visual-default" :style="defaultStyle">
          <!-- Optional: Pattern or texture for default covers -->
        </div>
      </div>

      <!-- Bottom Section (30%) -->
      <div class="card-bottom">
        <div class="info-group">
          <h3 class="pixel-title">{{ book.titleCN }}</h3>
          <p class="book-quote">{{ book.quote }}</p>
          <p class="book-author">— {{ book.author }}</p>
        </div>
        <div class="card-footer">
          <div class="barcode">
            <div class="bar" v-for="n in 12" :key="n" :style="{ width: Math.random() * 3 + 1 + 'px' }"></div>
          </div>
          <button class="read-entry-btn">READ ENTRY</button>
        </div>
      </div>
    </div>

    <!-- Expansion Animation Overlay -->
    <Teleport to="body">
      <div v-if="isNavigating" class="expansion-overlay" :style="overlayStyle">
        <div class="expansion-content" :style="getExpansionStyle(book)">
           
           <!-- Three-Body Transition -->
           <div v-if="book.id === 1" class="transition-three-body">
              <!-- Pure color block, no internal elements as requested -->
           </div>

           <!-- Amusing Ourselves Transition (White Noise) -->
           <div v-else-if="book.id === 11" class="transition-amusing">
              <!-- White Noise handled by background or CSS -->
           </div>

           <!-- Simplified Content for Transition -->
           <h1 v-else class="expansion-title">{{ book.titleCN }}</h1>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, nextTick, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import ThreeBodyCover from './covers/ThreeBodyCover.vue';
import SapiensCover from './covers/SapiensCover.vue';
import NineteenEightyFourCover from './covers/NineteenEightyFourCover.vue';
import HitchhikersGuideCover from './covers/HitchhikersGuideCover.vue';
import InteractionOfColorCover from './covers/InteractionOfColorCover.vue';
import NonDesignersDesignBookCover from './covers/NonDesignersDesignBookCover.vue';
import GridSystemsCover from './covers/GridSystemsCover.vue';
import DuneCover from './covers/DuneCover.vue';
import BraveNewWorldCover from './covers/BraveNewWorldCover.vue';
import Life30Cover from './covers/Life30Cover.vue';
import AmusingOurselvesToDeathCover from './covers/AmusingOurselvesToDeathCover.vue';

const router = useRouter();

// --- Countdown Logic ---
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
});

onUnmounted(() => {
  clearInterval(timer);
});
// --- End Countdown Logic ---

interface BookItem {
  id: number;
  title: string;
  titleCN: string;
  author: string;
  quote: string;
  category: string;
  colorTheme: string;
  isBorder?: boolean;
}

const props = defineProps<{
  book: BookItem;
  isExpanded?: boolean;
}>();

const isNavigating = ref(false);
const overlayStyle = ref({});

const formattedId = computed(() => {
  return props.book.id < 10 ? `0${props.book.id}` : `${props.book.id}`;
});

const defaultStyle = computed(() => {
  const style: any = { background: props.book.colorTheme };
  if (props.book.isBorder) {
    style.border = '4px solid #000';
    style.color = '#000';
  }
  return style;
});

const getExpansionStyle = (book: BookItem) => {
  if (book.id === 1) return { background: '#000' };
  if (book.id === 11) return { background: '#fff' }; // White flash for Amusing Ourselves
  return { background: book.colorTheme };
};

const handleClick = (event: MouseEvent) => {
  if (!props.isExpanded) {
    // If not expanded, do nothing here and let the event bubble up to the parent
    // The parent (MainPage) handles the expansion logic via @click on the container
    return;
  }

  // If already expanded, stop propagation and navigate
  event.stopPropagation();
  triggerNavigation(event);
};

const triggerNavigation = async (event: MouseEvent) => {
  // 1. Calculate start position
  const target = event.currentTarget as HTMLElement;
  const rect = target.getBoundingClientRect();

  // 2. Set initial style for the overlay (matching the card's position)
  overlayStyle.value = {
    top: `${rect.top}px`,
    left: `${rect.left}px`,
    width: `${rect.width}px`,
    height: `${rect.height}px`,
    opacity: 1,
    transform: 'none',
    borderRadius: '0px' // Assuming card has 0 border radius or match it
  };

  isNavigating.value = true;

  // 3. Force reflow/wait for render
  await nextTick();
  
  // Small delay to ensure the initial state is rendered before transitioning
  setTimeout(() => {
    // 4. Set final style (full screen)
    overlayStyle.value = {
      top: '0px',
      left: '0px',
      width: '100vw',
      height: '100vh',
      opacity: 1,
      transform: 'scale(1)', // Just to be explicit
      borderRadius: '0px'
    };
  }, 20);

  // 5. Navigate after animation
  setTimeout(() => {
    const routeMap: Record<number, string> = {
      1: 'ThreeBodyProblem',
      2: 'Sapiens',
      3: 'HitchhikersGuide',
      4: 'InteractionOfColor',
      5: 'NonDesignersDesignBook',
      6: 'GridSystems',
      7: 'Dune',
      8: 'Book1984',
      9: 'BraveNewWorld',
      10: 'ZenAndMotorcycle',
      11: 'AmusingOurselvesToDeath',
      12: 'Life30',
    };
    const routeName = routeMap[props.book.id];
    if (routeName) {
      router.push({ name: routeName });
    }
    // Note: We don't set isNavigating to false here immediately to prevent flicker
    // Ideally, the new page loads on top. 
    // If we want to clean up, we can do it after a delay or let the component unmount.
    // However, since this component might stay mounted (if cached) or unmounted, 
    // it's safer to rely on the route change.
  }, 600); // Match transition duration
};
</script>

<style scoped>
/* ... existing styles ... */
/* Layout Structure */
.book-card-wrapper {
  margin-bottom: 40px;
  opacity: 0;
  animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  position: relative;
  aspect-ratio: 0.72;
  cursor: pointer;
}

.book-shadow {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-color: #000;
  z-index: 0;
  transition: transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform: translate(4px, 4px);
}

.book-body {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 1;
  background-color: #fff;
  border: 1px solid #000;
  transition: transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transform: translate(-4px, -4px);
}

.unified-card {
  display: flex;
  flex-direction: column;
}

/* Hover Effects */
.book-card-wrapper:hover .book-shadow {
  transform: translate(8px, 8px);
}
.book-card-wrapper:hover .book-body {
  transform: translate(-6px, -6px);
}

/* Sections */
.card-top {
  height: 5%;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  border-bottom: 1px solid #eee;
}

.book-index {
  font-family: monospace;
  font-size: 7px;
  font-weight: 700;
  color: #000;
}

.archived-tag {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 6px;
  font-weight: 700;
  letter-spacing: 1px;
}

.black-square {
  width: 4px; height: 4px;
  background: #000;
}

.card-middle {
  height: 55%;
  position: relative;
}

/* ... existing styles ... */

.expansion-overlay {
  position: fixed;
  z-index: 9999;
  transition: all 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: #fff;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  pointer-events: none; /* Prevent clicks during animation */
}

.expansion-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  /* Add a gradient overlay effect */
  background-image: linear-gradient(135deg, rgba(255,255,255,0) 0%, rgba(255,255,255,0.8) 100%);
}

.expansion-title {
  font-size: 3rem;
  font-weight: 900;
  color: #000;
  opacity: 0;
  animation: fadeIn 0.5s ease 0.2s forwards;
  text-align: center;
  padding: 20px;
}

@keyframes fadeIn {
  to { opacity: 1; }
}

.category-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #000;
  color: #fff;
  padding: 3px 6px;
  font-size: 6px;
  font-weight: 700;
  text-transform: uppercase;
  z-index: 10;
  transform: rotate(5deg);
  transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  letter-spacing: 0.5px;
}

.book-card-wrapper:hover .category-tag {
  transform: rotate(15deg) scale(1.1);
}

.card-bottom {
  height: 40%;
  background: #fff;
  padding: 12px 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* Bottom Section Content */
.info-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pixel-title {
  margin: 0;
  font-family: 'Courier New', Courier, monospace; /* Fallback for pixel feel */
  font-size: 18px;
  font-weight: 900;
  color: #000;
  line-height: 1.1;
  letter-spacing: -1px;
}

.book-quote {
  margin: 0;
  font-size: 10px;
  color: #666;
  font-style: italic;
  line-height: 1.4;
}

.book-author {
  margin: 0;
  font-size: 8px;
  color: #000;
  font-weight: 700;
  text-transform: uppercase;
  width: 100%;
  text-align: right;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.barcode {
  display: flex;
  gap: 1px;
  height: 10px;
  align-items: flex-end;
}

.bar {
  width: 1.5px;
  background: #000;
  height: 100%;
}

.read-entry-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 3px 6px;
  font-size: 6px;
  font-weight: 700;
  cursor: pointer;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Animations CSS Copied */
@keyframes fadeUp { from { opacity: 0; transform: translateY(50px); } to { opacity: 1; transform: translateY(0); } }

/* --- Sapiens --- */
.visual-sapiens {
  width: 100%; height: 100%;
  background: linear-gradient(to top, #d6d3d1 0%, #f3f4f6 100%);
  position: relative; overflow: hidden;
}
.time-layers {
  position: absolute; width: 100%; height: 100%;
  display: flex; flex-direction: column-reverse;
}
.layer {
  flex: 1; position: relative;
  transition: all 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  border-top: 1px solid rgba(0,0,0,0.05);
}
.layer-primitive { background-color: #e7e5e4; }
.layer-agriculture { background-color: #f5f5f4; }
.layer-scientific { background-color: #fff; }

.symbol-bone, .symbol-wheat, .symbol-atom {
  position: absolute; right: 20px; bottom: 10px;
  width: 30px; height: 30px;
  opacity: 0.3;
  transition: all 0.6s ease;
}
.symbol-bone { background: #a8a29e; clip-path: polygon(20% 0%, 80% 0%, 100% 20%, 100% 80%, 80% 100%, 20% 100%, 0% 80%, 0% 20%); }
.symbol-wheat { background: #d97706; border-radius: 50% 50% 0 0; }
.symbol-atom { border: 2px solid #3b82f6; border-radius: 50%; }
.grid-lines {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background-image: linear-gradient(#ccc 1px, transparent 1px), linear-gradient(90deg, #ccc 1px, transparent 1px);
  background-size: 20px 20px; opacity: 0.1;
}
.abstract-data {
  position: absolute; top: 10px; left: 10px;
  font-family: monospace; font-size: 8px; color: #999;
  content: "010101";
}

.book-card-wrapper:hover .layer-primitive { flex: 0.5; filter: grayscale(100%); }
.book-card-wrapper:hover .layer-agriculture { flex: 0.8; }
.book-card-wrapper:hover .layer-scientific { flex: 2; }
.book-card-wrapper:hover .symbol-bone { transform: translateX(-50px) rotate(-20deg); opacity: 0; }
.book-card-wrapper:hover .symbol-wheat { transform: scale(1.2); opacity: 0.5; }
.book-card-wrapper:hover .symbol-atom { transform: scale(2) rotate(180deg); opacity: 1; border-color: #2563eb; box-shadow: 0 0 15px rgba(37,99,235,0.3); }

/* --- Hitchhiker --- */
.visual-hitchhiker {
  width: 100%; height: 100%;
  background: #2563eb;
  position: relative; overflow: hidden;
}
.absurd-container { width: 100%; height: 100%; position: relative; }
.float-item {
  position: absolute; font-weight: 900; color: #fff;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.whale { font-size: 30px; top: 10%; left: 10%; animation: float 6s ease-in-out infinite; }
.pot { font-size: 24px; top: 20%; right: 20%; animation: float 8s ease-in-out infinite reverse; }
.number-42 { font-size: 80px; top: 40%; left: 50%; transform: translate(-50%, -50%); opacity: 0.2; }
.towel { font-size: 12px; bottom: 40px; right: 10px; background: #fff; color: #2563eb; padding: 2px 6px; transform: rotate(-5deg); }
.geometry-chaos {
  position: absolute; border: 2px solid rgba(255,255,255,0.2);
  transition: all 0.3s ease;
}
.geo-1 { width: 50px; height: 50px; top: 60%; left: 20%; border-radius: 50%; }
.geo-2 { width: 40px; height: 40px; bottom: 10%; right: 40%; transform: rotate(45deg); }

.book-card-wrapper:hover .visual-hitchhiker { background: #4f46e5; }
.book-card-wrapper:hover .whale { top: 50%; left: 60%; transform: scale(1.5) rotate(20deg); }
.book-card-wrapper:hover .pot { top: 80%; right: 10%; transform: rotate(180deg); }
.book-card-wrapper:hover .number-42 { opacity: 0.8; transform: translate(-50%, -50%) scale(1.2) rotate(10deg); text-shadow: 4px 4px 0px #000; }
.book-card-wrapper:hover .towel { transform: rotate(5deg) scale(1.1); background: #facc15; color: #000; }
.book-card-wrapper:hover .geo-1 { border-radius: 0; border-color: #fff; transform: rotate(90deg); }

@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }

.visual-default {
  width: 100%;
  height: 100%;
  position: relative;
}
/* Expanded Mode Scaling */
.expanded-mode .book-index { font-size: 11px; }
.expanded-mode .archived-tag { font-size: 9px; gap: 4px; }
.expanded-mode .black-square { width: 6px; height: 6px; }
.expanded-mode .pixel-title { font-size: 32px; }
.expanded-mode .book-quote { font-size: 16px; }
.expanded-mode .book-author { font-size: 12px; }
.expanded-mode .barcode { height: 18px; gap: 2px; }
.expanded-mode .bar { width: 2.5px; }
.expanded-mode .read-entry-btn { font-size: 10px; padding: 6px 10px; }
/* --- Transition Styles --- */
.transition-three-body {
  width: 100%; height: 100%;
  position: relative;
  overflow: hidden;
  background-color: #000; /* Ensure transition bg matches */
}

.antenna-hero-transition {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 60vh;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  animation: slideUpHero 0.5s ease-out forwards;
}

.antenna-dish-large {
  width: 300px; height: 300px;
  border-bottom: 10px solid #222;
  border-left: 5px solid #222;
  border-radius: 0 0 0 100%;
  transform: rotate(-45deg) translate(50px, 50px);
  background: linear-gradient(45deg, #111 0%, #333 100%);
  position: relative;
  z-index: 2;
}

.antenna-tip-large {
  position: absolute; top: 20px; right: 20px; width: 8px; height: 8px;
  background: #ff0000; border-radius: 50%; box-shadow: 0 0 20px #ff0000;
  animation: pulse 1s infinite;
}

.antenna-base-large {
  width: 80px; height: 150px;
  background: #111;
  position: absolute;
  bottom: 0;
  clip-path: polygon(20% 0%, 80% 0%, 100% 100%, 0% 100%);
}

@keyframes slideUpHero {
  from { transform: translateY(100px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.transition-amusing {
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='1'/%3E%3C/svg%3E");
  opacity: 0.5;
  animation: noise 0.2s steps(4) infinite;
}

@keyframes noise {
  0% { transform: translate(0,0); }
  10% { transform: translate(-5%,-5%); }
  20% { transform: translate(-10%,5%); }
  30% { transform: translate(5%,-10%); }
  40% { transform: translate(-5%,15%); }
  50% { transform: translate(-10%,5%); }
  60% { transform: translate(15%,0); }
  70% { transform: translate(0,10%); }
  80% { transform: translate(-15%,0); }
  90% { transform: translate(10%,5%); }
  100% { transform: translate(5%,0); }
}
</style>
