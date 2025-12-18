<template>
  <div 
    class="bookshelf-widget"
    @dragover.prevent
    @drop="$emit('drop-on-shelf', $event)"
  >
    <!-- Books Row -->
    <div class="books-container">
      <div 
        v-for="(book, index) in books" 
        :key="book.bookId"
        class="book-spine"
        :style="getBookStyle(book, index)"
        draggable="true"
        @dragstart="$emit('dragstart', $event, book)"
        @mouseenter="hoveredBook = book"
        @mouseleave="hoveredBook = null"
        @click.stop="$emit('click-book', book)"
      >
        <!-- Hover Title above the book -->
        <transition name="fade">
          <div v-if="hoveredBook === book" class="hover-book-title">
            {{ book.bookName }}
          </div>
        </transition>

        <div class="spine-content">
          <span class="spine-title">{{ book.bookName }}</span>
        </div>
      </div>
    </div>

    <!-- Wood Shelf -->
    <div class="wood-shelf"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';

const props = defineProps<{
  books: any[];
}>();

defineEmits(['dragstart', 'click-book', 'drop-on-shelf']);

const hoveredBook = ref<any | null>(null);

// Visual State Management
interface BookVisual {
  heightVal: number; // Store numeric value for logic
  height: string;
  backgroundColor: string;
  color: string;
  tiltAngle: string;
  transformOrigin: string;
  zIndex: number;
  marginLeft: string;
  marginRight: string;
}

const bookVisuals = ref<Map<any, BookVisual>>(new Map());

// Color Palette Definition
const colorPalette = [
  { bg: '#1a1a1a', text: '#ffffff' }, // Classic Black
  { bg: '#f9f9f9', text: '#333333' }, // Off White
  { bg: '#2c3e50', text: '#ecf0f1' }, // Midnight Blue
  { bg: '#8e44ad', text: '#ffffff' }, // Royal Purple
  { bg: '#27ae60', text: '#ffffff' }, // Emerald Green
  { bg: '#d35400', text: '#ffffff' }, // Pumpkin Orange
  { bg: '#c0392b', text: '#ffffff' }, // Pomegranate Red
  { bg: '#7f8c8d', text: '#ffffff' }, // Concrete Gray
  { bg: '#e0e0e0', text: '#000000' }, // Light Gray
  { bg: '#34495e', text: '#ffffff' }, // Wet Asphalt
  { bg: '#16a085', text: '#ffffff' }, // Green Sea
  { bg: '#8B4513', text: '#FFF8DC' }, // Saddle Brown
  { bg: '#556B2F', text: '#FFFFF0' }, // Dark Olive Green
  { bg: '#483D8B', text: '#E6E6FA' }, // Dark Slate Blue
  { bg: '#A0522D', text: '#FFFFFF' }, // Sienna
];

const generateVisuals = () => {
  const newVisuals = new Map();
  const tempVisuals: any[] = [];

  // Pass 1: Generate basic visuals (Height, Color)
  props.books.forEach((book) => {
    // Random Height (75% - 95%)
    const heightVal = Math.random() * 20 + 75;
    const height = `${heightVal}%`;

    // Random Color from Palette
    const theme = colorPalette[Math.floor(Math.random() * colorPalette.length)];

    tempVisuals.push({
      bookId: book.bookId,
      heightVal,
      height,
      backgroundColor: theme.bg,
      color: theme.text,
      // Defaults
      tiltAngle: '0deg',
      transformOrigin: 'bottom center',
      zIndex: 1,
      marginLeft: '0px',
      marginRight: '0px'
    });
  });

  // Pass 2: Calculate Tilt based on Neighbors
  let lastWasTilted = false;

  tempVisuals.forEach((visual, index) => {
    // If previous was tilted, force this one straight to avoid collision chains
    if (lastWasTilted) {
      lastWasTilted = false;
      newVisuals.set(visual.bookId, visual);
      return;
    }

    // 30% chance to attempt tilt
    if (Math.random() < 0.3) {
      const leftNeighbor = index > 0 ? tempVisuals[index - 1] : null;
      const rightNeighbor = index < tempVisuals.length - 1 ? tempVisuals[index + 1] : null;

      // Check height constraint: Neighbor must be taller (or at least close/same) to lean on
      const canTiltLeft = leftNeighbor && leftNeighbor.heightVal > visual.heightVal;
      const canTiltRight = rightNeighbor && rightNeighbor.heightVal > visual.heightVal;

      let tiltDir = ''; // 'left' | 'right' | ''

      if (canTiltLeft && canTiltRight) {
        tiltDir = Math.random() < 0.5 ? 'left' : 'right';
      } else if (canTiltLeft) {
        tiltDir = 'left';
      } else if (canTiltRight) {
        tiltDir = 'right';
      }

      if (tiltDir) {
        lastWasTilted = true;

        // Calculate Physically Accurate Angle
        // 1. Generate random bottom gap (the distance the base slides out)
        // Range: 15px to 30px (slightly larger minimum to be noticeable)
        const bottomGap = Math.random() * 15 + 15;
        
        // 2. Convert percentage height to approx pixels (Shelf row is ~220px)
        const bookHeightPx = (visual.heightVal / 100) * 220;

        // 3. Adjust for CSS Flex Gap (2px) and slight overlap (1px)
        // The total horizontal distance the top needs to cover is:
        // Margin (bottomGap) + FlexGap (2px) + Overlap (1px)
        // Also account for the slight width loss from rotation: W * (1 - cos(a)) ~ 1px
        const totalProtrusion = bottomGap + 2 + 1 + 1;

        // 4. Calculate angle: sin(theta) = Protrusion / Height
        const angleRad = Math.asin(Math.min(totalProtrusion / bookHeightPx, 0.9)); 
        const angleDeg = angleRad * (180 / Math.PI);

        if (tiltDir === 'left') {
           visual.tiltAngle = `-${angleDeg.toFixed(2)}deg`;
           visual.transformOrigin = 'bottom right';
           visual.marginLeft = `${bottomGap}px`; 
           visual.zIndex = 2;
        } else {
           visual.tiltAngle = `${angleDeg.toFixed(2)}deg`;
           visual.transformOrigin = 'bottom left';
           visual.marginRight = `${bottomGap}px`;
           visual.zIndex = 2;
        }
      }
    }

    newVisuals.set(visual.bookId, visual);
  });

  bookVisuals.value = newVisuals;
};


onMounted(() => {
  generateVisuals();
});

// Regenerate if books prop changes significantly (optional, depending on use case)
watch(() => props.books, () => {
  generateVisuals();
}, { deep: true });

const getBookStyle = (book: any, index: number) => {
  const visual = bookVisuals.value.get(book.bookId);
  
  // Fallback if visual not ready
  if (!visual) return {};

  return {
    backgroundColor: visual.backgroundColor,
    color: visual.color,
    borderColor: 'transparent',
    height: visual.height,
    transformOrigin: visual.transformOrigin,
    zIndex: visual.zIndex,
    marginLeft: visual.marginLeft,
    marginRight: visual.marginRight,
    '--tilt-rotate': visual.tiltAngle
  };
};
</script>

<style scoped>
.bookshelf-widget {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 20px 10px 0;
  box-sizing: border-box;
  background: #f9f9f9; /* Updated background */
  position: relative;
  overflow: visible; /* Allow hover title to overflow */
}

.shelf-display {
  position: absolute;
  top: 15px;
  left: 0;
  width: 100%;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: #333;
  height: 20px;
  z-index: 10;
}

.display-text {
  display: block;
}

.books-container {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 2px; /* Reduced gap slightly */
  flex: 1;
  padding-bottom: 12px;
  perspective: 500px;
}

.book-spine {
  width: 7%; /* Reduced from 14% to 7% (1/2 thickness) */
  border: 1px solid rgba(0,0,0,0.1);
  border-radius: 1px;
  cursor: pointer;
  /* Apply static tilt via CSS variable */
  transform: rotate(var(--tilt-rotate, 0deg));
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), margin 0.3s ease;
  position: relative;
  box-shadow: 1px 0 3px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-spine:hover {
  /* Combine hover lift/scale with the existing tilt */
  transform: translateY(-10px) scale(1.05) rotate(var(--tilt-rotate, 0deg));
  z-index: 20 !important; /* Ensure hovered book is always on top */
  box-shadow: 2px 2px 5px rgba(0,0,0,0.2);
}

.hover-book-title {
  position: absolute;
  bottom: 110%; /* Above the book */
  left: 50%;
  /* Counter-rotate the title so it stays horizontal even if book is tilted */
  transform: translateX(-50%) rotate(calc(var(--tilt-rotate, 0deg) * -1));
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  pointer-events: none;
  z-index: 30;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

.spine-content {
  writing-mode: vertical-rl;
  text-orientation: upright;
  font-size: 12px; /* Slightly smaller font for thinner books */
  font-weight: 600;
  letter-spacing: 1px;
  overflow: hidden;
  white-space: nowrap;
  max-height: 90%;
  opacity: 0.9;
}

.wood-shelf {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 12px;
  background: #d4a373;
  border-top: 2px solid #a98467;
  box-shadow: 0 -2px 5px rgba(0,0,0,0.1);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
