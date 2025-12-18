<template>
  <div 
    class="category-nav" 
    :class="{ 'is-expanded': isExpanded }"
    @click="handleClick"
    ref="navRef"
  >
    <!-- Global Background Overlay -->
    <div 
      class="global-bg-overlay"
      :style="globalBgStyle"
    ></div>

    <button class="return-btn" v-if="isExpanded" @click.stop="emit('collapse')">⇐</button>

    <!-- Categories Grid / Flex -->
    <div class="categories-container" @mouseleave="hoveredIndex = -1">
      <div 
        v-for="(cat, index) in categories" 
        :key="index"
        class="category-block"
        :class="{ 
          'is-hovered': hoveredIndex === index,
          'is-expanded-mode': isExpanded,
          'is-dimmed': isExpanded && hoveredIndex !== -1 && hoveredIndex !== index
        }"
        @mouseenter="handleMouseEnter(index)"
        @mouseleave="handleMouseLeave(index)"
        @click.stop="handleBlockClick(cat, index)"
      >
        <!-- Local Immediate Background -->
        <div class="local-bg" :style="{ backgroundColor: cat.color }"></div>
        
        <div class="block-content">
          <div class="cat-icon" :style="{ color: getTextColor(cat, index) }">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path :d="cat.iconPath" />
            </svg>
          </div>
          <div class="cat-name" :style="{ color: getTextColor(cat, index) }">{{ cat.name }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const props = defineProps<{
  isExpanded: boolean;
}>();

const emit = defineEmits(['toggle-expand', 'select', 'collapse']);

const navRef = ref<HTMLElement | null>(null);
const hoveredIndex = ref(-1);

const categories = [
  { 
    name: '经典', 
    color: '#B8A99A',
    textColor: '#000',
    iconPath: 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z' 
  },
  { 
    name: '哲学', 
    color: '#4E5A6A',
    textColor: '#fff',
    iconPath: 'M6 4h12v2H6zm2 2h8v12H8zm-2 12h12v2H6z' 
  },
  { 
    name: '知识', 
    color: '#6C7A89',
    textColor: '#fff',
    iconPath: 'M9 18h6M10 22h4M12 2a7 7 0 0 0-7 7c0 2 2 3 2 5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2c0-2 2-3 2-5a7 7 0 0 0-7-7z' 
  },
  { 
    name: '历史', 
    color: '#8C6D62',
    textColor: '#fff',
    iconPath: 'M5 2h14v4L12 12l7 6v4H5v-4l7-6-7-6V2z' 
  },
  { 
    name: '生活', 
    color: '#8F9A8C',
    textColor: '#000',
    iconPath: 'M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10z' 
  },
  { 
    name: '科幻', 
    color: '#5C6473',
    textColor: '#fff',
    iconPath: 'M12 4l10 16H2L12 4z' 
  }
];

const globalBgStyle = computed(() => {
  if (!props.isExpanded) {
    return {
      backgroundColor: 'transparent',
      opacity: 0,
      transition: 'none'
    };
  }
  if (hoveredIndex.value !== -1) {
    const cat = categories[hoveredIndex.value];
    return {
      backgroundColor: cat.color,
      opacity: 1,
      transition: 'opacity 0.85s ease, background-color 0.85s ease'
    };
  }
  return {
    backgroundColor: 'transparent',
    opacity: 0,
    transition: 'opacity 0.6s ease, background-color 0.6s ease'
  };
});

const handleClick = () => {
  if (!props.isExpanded) {
    emit('toggle-expand');
  }
};

const handleBlockClick = (cat: any, index: number) => {
  if (!props.isExpanded) {
    emit('toggle-expand');
    hoveredIndex.value = index;
  } else {
    emit('select', { name: cat.name, color: cat.color });
  }
};

const handleMouseEnter = (index: number) => {
  hoveredIndex.value = index;
};

const handleMouseLeave = (index: number) => {
  if (hoveredIndex.value === index) {
    hoveredIndex.value = -1;
  }
};

// Logic for Text Color
const getTextColor = (cat: any, index: number) => {
  if (hoveredIndex.value !== -1) {
    if (props.isExpanded) {
      const activeCat = categories[hoveredIndex.value];
      return activeCat.textColor;
    }
    return hoveredIndex.value === index ? cat.textColor : '#333';
  }
  
  // Default state (no hover)
  return '#333';
};

</script>

<style scoped>
.category-nav {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  background: transparent;
  transition: all 0.5s ease;
  cursor: pointer;
}

/* Global Background Overlay */
.global-bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0; /* Behind content */
  pointer-events: none; /* Let clicks pass through */
  transition: background-color 0.4s ease, opacity 0.4s ease;
  opacity: 0;
}

.return-btn {
  width: 32px;
  height: 32px;
  background: #fff;
  color: #000;
  border: 0px solid #000;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.1s;
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 5;
}

.return-btn:active {
  transform: scale(0.95);
}

.categories-container {
  width: 100%;
  height: 100%;
  display: grid;
  position: relative;
  z-index: 1; /* Above global bg */
  /* Default Collapsed Grid */
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr 1fr;
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.is-expanded .categories-container {
  display: flex;
  flex-direction: row;
}

.category-block {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(0,0,0,0.05);
}

/* Local Background (Immediate Fill) */
.local-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 0;
  opacity: 0;
  transition: opacity 0.7s ease;
}

.category-block.is-hovered .local-bg {
  opacity: 1;
}

.is-expanded .local-bg {
  transition: opacity 0.55s ease;
}

/* When global hover is active, remove borders to make it look like a solid field */
.category-nav.is-expanded:hover .category-block {
  border-color: transparent;
}

.block-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 15px;
  transition: transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.cat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  line-height: 1;
  transition: font-size 0.45s ease, color 0.45s ease, transform 0.45s ease;
}

.cat-icon svg {
  width: 1em;
  height: 1em;
  transition: transform 0.45s ease, color 0.45s ease;
  transform-origin: 50% 50%;
}

.cat-name {
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 2px;
  transition: font-size 0.45s ease, font-weight 0.45s ease, color 0.45s ease;
}

/* Slower color transition for dimmed items */
.category-block.is-dimmed .cat-icon svg {
  transition: transform 0.45s ease, color 0.85s ease;
}

.category-block.is-dimmed .cat-name {
  transition: font-size 0.45s ease, font-weight 0.45s ease, color 0.85s ease;
}

/* ==========================================================================
   Collapsed State Interaction
   ========================================================================== */
.category-nav:not(.is-expanded) .category-block.is-hovered {
  z-index: 10;
}

.category-nav:not(.is-expanded) .category-block.is-hovered .cat-name {
  font-size: 24px;
  font-weight: 800;
}

.category-nav:not(.is-expanded) .category-block.is-hovered .cat-icon svg {
  transform: rotate(360deg) scale(2.0);
}

/* ==========================================================================
   Expanded State Layout & Interaction
   ========================================================================== */
.is-expanded .category-block {
  flex: 1;
  height: 100%;
  border: none;
  border-right: 1px solid rgba(0,0,0,0.1);
  justify-content: center;
}

.is-expanded .category-block:last-child {
  border-right: none;
}

.is-expanded .cat-name {
  writing-mode: vertical-rl;
  text-orientation: upright;
  font-size: 26px;
  line-height: 1;
}

.is-expanded .category-block .cat-icon {
  font-size: 26px;
  transform: translateX(var(--expanded-icon-x, 1.5px));
}

/* Hover in Expanded Mode */
.is-expanded .category-block.is-hovered {
  flex: 3; /* Expand width */
}

.is-expanded .category-block.is-hovered .cat-name {
  font-size: 80px;
  font-weight: 900;
}

.is-expanded .category-block.is-hovered .cat-icon {
  font-size: 80px;
  transform: translateX(var(--expanded-icon-x-hover, 5.5px));
}

.is-expanded .category-block.is-hovered .cat-icon svg {
  transform: rotate(360deg);
}


</style>
