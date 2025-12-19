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

    <!-- Algorithms Flex Container -->
    <div class="categories-container" @mouseleave="hoveredIndex = -1">
      <div 
        v-for="(algo, index) in algorithms" 
        :key="index"
        class="category-block"
        :class="{ 
          'is-hovered': hoveredIndex === index,
          'is-expanded-mode': isExpanded,
          'is-dimmed': isExpanded && hoveredIndex !== -1 && hoveredIndex !== index
        }"
        @mouseenter="handleMouseEnter(index)"
        @mouseleave="handleMouseLeave(index)"
        @click.stop="handleBlockClick(algo, index)"
      >
        <!-- Local Immediate Background -->
        <div class="local-bg" :style="{ backgroundColor: algo.color }"></div>
        
        <div class="block-content">
          <div class="cat-icon" :style="{ color: getTextColor(algo, index) }">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path :d="algo.iconPath" />
            </svg>
          </div>
          <div class="cat-name" :style="{ color: getTextColor(algo, index) }">{{ algo.name }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps<{
  isExpanded: boolean;
}>();

const emit = defineEmits(['toggle-expand', 'select', 'collapse']);
const router = useRouter();

const navRef = ref<HTMLElement | null>(null);
const hoveredIndex = ref(-1);

const algorithms = [
  { 
    name: '用户协同', 
    path: 'user_cf',
    color: '#B8A99A',
    textColor: '#000',
    iconPath: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75' 
  },
  { 
    name: '物品协同', 
    path: 'item_cf',
    color: '#4E5A6A',
    textColor: '#fff',
    iconPath: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' 
  },
  { 
    name: '内容推荐', 
    path: 'content_based',
    color: '#6C7A89',
    textColor: '#fff',
    iconPath: 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z' 
  },
  { 
    name: '关联规则', 
    path: 'association_rule',
    color: '#8C6D62',
    textColor: '#fff',
    iconPath: 'M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71 M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71' 
  },
  { 
    name: '隐语义', 
    path: 'lfm',
    color: '#5C6473',
    textColor: '#fff',
    iconPath: 'M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z M3.27 6.96L12 12.01l8.73-5.05 M12 22.08V12' 
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
    const algo = algorithms[hoveredIndex.value];
    return {
      backgroundColor: algo.color,
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

const handleBlockClick = (algo: any, index: number) => {
  if (!props.isExpanded) {
    emit('toggle-expand');
    hoveredIndex.value = index;
  } else {
    emit('select', { name: algo.name, color: algo.color, path: algo.path });
    router.push(`/bookRecommend/${algo.path}`);
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

const getTextColor = (algo: any, index: number) => {
  if (hoveredIndex.value !== -1) {
    if (props.isExpanded) {
      const activeAlgo = algorithms[hoveredIndex.value];
      return activeAlgo.textColor;
    }
    return hoveredIndex.value === index ? algo.textColor : '#333';
  }
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
  z-index: 0;
  pointer-events: none;
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
  z-index: 1;
  /* Default Collapsed Grid (2 columns for 5 items) */
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