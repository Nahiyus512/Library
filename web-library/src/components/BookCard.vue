<template>
  <div class="book-card-wrapper">
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
        <div v-if="book.id === 1" class="visual-three-body">
          <div class="universe-depth"></div>
          <div class="three-body-system">
            <div class="orbit-ring ring-1"></div>
            <div class="orbit-ring ring-2"></div>
            <div class="orbit-ring ring-3"></div>
            <div class="sun sun-1"></div>
            <div class="sun sun-2"></div>
            <div class="sun sun-3"></div>
          </div>
          <div class="chaos-overlay"></div>
        </div>

        <!-- Sapiens -->
        <div v-else-if="book.id === 2" class="visual-sapiens">
          <div class="time-layers">
            <div class="layer layer-primitive">
              <div class="texture-noise"></div>
              <div class="symbol-bone"></div>
            </div>
            <div class="layer layer-agriculture">
              <div class="grid-lines"></div>
              <div class="symbol-wheat"></div>
            </div>
            <div class="layer layer-scientific">
              <div class="abstract-data"></div>
              <div class="symbol-atom"></div>
            </div>
          </div>
        </div>

        <!-- Hitchhiker -->
        <div v-else-if="book.id === 3" class="visual-hitchhiker">
          <div class="absurd-container">
            <div class="float-item whale">🐋</div>
            <div class="float-item pot">🪴</div>
            <div class="float-item number-42">42</div>
            <div class="float-item towel">DON'T PANIC</div>
            <div class="geometry-chaos geo-1"></div>
            <div class="geometry-chaos geo-2"></div>
          </div>
        </div>

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
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

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
</script>

<style scoped>
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
  overflow: hidden;
  border-bottom: 1px solid #000;
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

/* --- Three-Body Problem --- */
.visual-three-body {
  width: 100%; height: 100%;
  background: #000;
  position: relative; overflow: hidden;
  perspective: 600px;
}
.universe-depth {
  position: absolute; width: 200%; height: 200%;
  top: -50%; left: -50%;
  background-image: radial-gradient(white, rgba(255,255,255,.1) 1px, transparent 2px), radial-gradient(white, rgba(255,255,255,.05) 1px, transparent 1.5px);
  background-size: 100px 100px, 60px 60px;
  animation: universeRotate 60s linear infinite;
  opacity: 0.5;
}
.three-body-system {
  position: absolute; top: 50%; left: 50%;
  width: 100%; height: 100%;
  transform: translate(-50%, -50%) scale(0.6);
  transition: transform 1.2s cubic-bezier(0.16, 1, 0.3, 1);
}
.orbit-ring {
  position: absolute; top: 50%; left: 50%;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.8s ease;
}
.ring-1 { width: 60px; height: 60px; border-color: rgba(255,255,255,0.3); animation: orbitRotate 10s linear infinite reverse; }
.ring-2 { width: 120px; height: 120px; animation: orbitRotate 15s linear infinite; }
.ring-3 { width: 180px; height: 180px; animation: orbitRotate 20s linear infinite reverse; }

.sun {
  position: absolute; border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #fff, #a855f7, #000);
  box-shadow: 0 0 10px rgba(168, 85, 247, 0.5);
  transition: all 0.8s ease;
}
.sun-1 { width: 12px; height: 12px; top: 0; left: 50%; margin-left: -6px; animation: threeBodyChaos 4s ease-in-out infinite alternate; }
.sun-2 { width: 8px; height: 8px; bottom: 20px; right: 20px; animation: threeBodyChaos 6s ease-in-out infinite alternate-reverse; }
.sun-3 { width: 16px; height: 16px; top: 40%; left: 10%; background: radial-gradient(circle at 30% 30%, #fff, #ef4444, #000); animation: threeBodyChaos 5s ease-in-out infinite; }

.chaos-overlay {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(circle at center, transparent 30%, #000 90%);
  pointer-events: none;
}

.book-card-wrapper:hover .three-body-system { transform: translate(-50%, -50%) scale(0.8) rotate(10deg); }
.book-card-wrapper:hover .ring-1 { width: 70px; height: 90px; border-color: rgba(168, 85, 247, 0.6); }
.book-card-wrapper:hover .ring-2 { border-style: dashed; animation-duration: 2s; }
.book-card-wrapper:hover .sun-1 { box-shadow: 0 0 30px #a855f7; transform: scale(1.5); }
.book-card-wrapper:hover .sun-3 { box-shadow: 0 0 40px #ef4444; transform: scale(1.2); }

@keyframes universeRotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@keyframes orbitRotate { from { transform: translate(-50%, -50%) rotate(0deg); } to { transform: translate(-50%, -50%) rotate(360deg); } }
@keyframes threeBodyChaos { 0% { transform: translate(0,0); } 100% { transform: translate(10px, -10px); } }

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
</style>
