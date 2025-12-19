<template>
  <div class="split-layout">
    <!-- Left Panel: Algorithm Navigation -->
    <div 
      class="panel left-panel" 
      :class="{ 'expanded': activeSide === 'left', 'shrunk': activeSide === 'right' }"
    >
      <AlgorithmNav 
        :is-expanded="activeSide === 'left'"
        @toggle-expand="expandLeftPanel"
        @collapse="collapseLeftPanel"
        @select="navigateToAlgorithm"
      />
    </div>

    <!-- Right Panel: Recommendation Content -->
    <div 
      class="panel right-panel" 
      :class="{ 'expanded': activeSide === 'right', 'shrunk': activeSide === 'left' }"
    >
      <div class="layout-container is-expanded-mode">
        
        <!-- Left Sidebar (Always present) -->
        <div class="sidebar left-sidebar">
          <div class="watermark-vertical">LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY</div>
        </div>

        <!-- Main Content Area -->
        <div class="content-area beige-grid">
          <!-- Return Button for Right Panel -->
          <button class="return-btn" @click="expandLeftPanel">⇐</button>

          <div class="bg-watermark">RECOMMEND</div>
          
          <div class="scroll-container expanded-padding">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </div>

        <!-- Right Sidebar (Always present) -->
        <div class="sidebar right-sidebar">
          <div class="watermark-vertical">LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY // LIBRARY</div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import AlgorithmNav from '@/components/business/AlgorithmNav.vue';
import { transitionState } from '@/store/transitionStore';

const router = useRouter();
const route = useRoute();
const activeSide = ref<string | null>('left'); // Default to left panel expanded

const expandLeftPanel = () => {
  activeSide.value = 'left';
};

const collapseLeftPanel = () => {
  activeSide.value = null; // Or handle as needed, but for this view we likely want left or right
};

const navigateToAlgorithm = (payload: { name: string, color: string, path: string }) => {
  activeSide.value = 'left'; // Keep left active during transition animation
  
  // Trigger transition animation (similar to CategoryNav)
  transitionState.startAnimation(payload.color, payload.name, '#fff');

  setTimeout(() => {
    // Switch to right panel expanded view
    activeSide.value = 'right';
    // The router push is handled inside AlgorithmNav or here if needed, 
    // but AlgorithmNav emits select, so we can handle logic here if we want to sync
    // AlgorithmNav already did router.push, but let's ensure we are in sync
    
    setTimeout(() => {
        transitionState.endAnimation();
    }, 100);
  }, 800);
};

// Check current route on mount to decide initial state
onMounted(() => {
  // If we are on a specific algorithm sub-route, maybe show right panel?
  // But user request says "Default direct display is expanded state" -> implying left panel expanded for selection?
  // "default direct display is expanded state" usually refers to the nav being full screen.
  // Let's stick to activeSide = 'left' initially so user sees the full screen nav.
  if (route.path === '/bookRecommend' || route.path === '/bookRecommend/') {
      activeSide.value = 'left';
  } else {
      // If direct link to a sub-route, show content
      activeSide.value = 'right';
  }
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.split-layout {
  height: 100%;
  width: 100%;
  display: flex;
  overflow: hidden;
  font-family: 'Inter', sans-serif;
  background: #fff;
}

.panel {
  height: 100%;
  transition: all 0.6s cubic-bezier(0.65, 0, 0.35, 1);
  overflow: hidden;
  position: relative;
}

/* Ensure padding is zero or controlled */
.panel * {
  box-sizing: border-box;
}

.left-panel {
  width: 50%;
  background-color: #fff;
  border-right: 1px solid rgba(0,0,0,0.05);
  z-index: 2;
}

.right-panel {
  width: 50%;
  background-color: #fcfcfc;
  z-index: 1;
  transition: width 0.6s cubic-bezier(0.65, 0, 0.35, 1);
}

.panel.expanded { width: 100% !important; }
.panel.shrunk { width: 0% !important; opacity: 0; pointer-events: none; }

.layout-container {
  width: 100%;
  height: 100%;
  display: flex;
}

.sidebar {
  width: 60px;
  background: #fff;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  padding-top: 20px;
  z-index: 10;
  border-left: 1px solid #000;
  border-right: 1px solid #000;
  overflow: hidden;
}

.left-sidebar { border-left: none; }
.right-sidebar { border-right: none; }

.content-area {
  flex: 1;
  height: 100%;
  position: relative;
  overflow: hidden;
  background-color: #faf8f5;
  background-image: 
    linear-gradient(#e5e5e5 1px, transparent 1px),
    linear-gradient(90deg, #e5e5e5 1px, transparent 1px);
  background-size: 125px 125px;
}

.scroll-container {
  height: 100%;
  width: 100%;
  overflow-y: auto;
  box-sizing: border-box;
  scrollbar-width: none;
  position: relative;
  z-index: 5;
}
.scroll-container.expanded-padding {
  padding: 0; 
}
.scroll-container::-webkit-scrollbar { display: none; }

.bg-watermark {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-family: 'Inter', sans-serif;
  font-weight: 900;
  font-size: 120px;
  color: rgba(0,0,0,0.03);
  pointer-events: none;
  z-index: 0;
  letter-spacing: 20px;
  white-space: nowrap;
}

.watermark-vertical {
  writing-mode: vertical-rl;
  transform: rotate(180deg);
  font-family: 'Inter', sans-serif;
  font-weight: 900;
  font-size: 24px;
  color: rgba(0,0,0,0.1);
  letter-spacing: 4px;
  white-space: nowrap;
  padding: 20px 0;
  margin: 0;
}

/* Return Button in Right Panel Content */
.return-btn {
  width: 32px; height: 32px;
  background: #000;
  color: #fff;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.1s;
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 20;
}
.return-btn:active {
  transform: scale(0.95);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>