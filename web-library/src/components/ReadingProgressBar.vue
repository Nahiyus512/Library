<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps<{
  color: string;
  isActive: boolean;
}>();

const progress = ref(0);
const route = useRoute();
let scrollElement: HTMLElement | null = null;

const updateProgress = () => {
  if (!scrollElement) return;
  const { scrollTop, scrollHeight, clientHeight } = scrollElement;
  if (scrollHeight <= clientHeight) {
    progress.value = 0;
    return;
  }
  progress.value = (scrollTop / (scrollHeight - clientHeight)) * 100;
};

const attachListener = () => {
  // Clean up previous
  if (scrollElement) {
    scrollElement.removeEventListener('scroll', updateProgress);
    scrollElement = null;
  }
  
  if (!props.isActive) return;

  // We need to find the scrollable container.
  // Based on App.vue structure, the routed component is the first child of .main
  // However, due to transitions or other elements, we should be careful.
  // We'll try to find an element that looks like the page container.
  // Most book pages have a root class like .dune-page, .three-body-page etc.
  // But generally, it's the element rendered by RouterView.
  
  // Retry mechanism to ensure element is mounted
  const findAndAttach = (attempts = 0) => {
    const main = document.querySelector('.main');
    if (main) {
      const children = Array.from(main.children) as HTMLElement[];
      for (const child of children) {
        // Skip the progress bar itself and the transition overlay
        if (
          !child.classList.contains('reading-progress-bar') &&
          !child.classList.contains('transition-overlay')
        ) {
          scrollElement = child;
          scrollElement.addEventListener('scroll', updateProgress);
          updateProgress();
          return;
        }
      }
    }
    
    if (attempts < 5) {
      setTimeout(() => findAndAttach(attempts + 1), 200);
    }
  };

  findAndAttach();
};

watch(() => props.isActive, (newVal) => {
  if (newVal) {
    // Wait for transition/mount
    setTimeout(attachListener, 100); 
  } else {
    if (scrollElement) {
      scrollElement.removeEventListener('scroll', updateProgress);
      scrollElement = null;
    }
    progress.value = 0;
  }
});

watch(() => route.path, () => {
  if (props.isActive) {
    // Route changed but still active (e.g. navigating between books? unlikely but possible)
    // Or just to be safe re-attach
    setTimeout(attachListener, 500);
  }
});

onMounted(() => {
  if (props.isActive) {
    attachListener();
  }
});

onUnmounted(() => {
  if (scrollElement) {
    scrollElement.removeEventListener('scroll', updateProgress);
  }
});
</script>

<template>
  <div 
    v-if="isActive"
    class="reading-progress-bar" 
    :style="{ 
      width: `${progress}%`, 
      backgroundColor: color,
      opacity: progress > 0 ? 1 : 0 
    }"
  ></div>
</template>

<style scoped>
.reading-progress-bar {
  position: fixed;
  top: 0;
  left: 0;
  height: 4px;
  z-index: 99999; /* Ensure it is on top of everything including sticky navs */
  transition: width 0.1s ease-out, opacity 0.2s;
  pointer-events: none;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
</style>
