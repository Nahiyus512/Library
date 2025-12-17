<template>
  <div 
    class="transition-overlay" 
    :class="{ 'active': transitionState.isActive }"
    :style="{ background: transitionState.color }"
  >
    <h1 
      class="transition-title" 
      :class="{ 'show': showTitle }" 
      :style="{ color: transitionState.textColor }"
    >
      {{ transitionState.title }}
    </h1>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { transitionState } from '@/store/transitionStore';

const showTitle = ref(false);

watch(() => transitionState.isActive, (newVal) => {
  if (newVal) {
    // Delay title appearance slightly for effect
    setTimeout(() => {
      showTitle.value = true;
    }, 50);
  } else {
    showTitle.value = false;
  }
});
</script>

<style scoped>
.transition-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.transition-overlay.active {
  pointer-events: auto;
  opacity: 1;
}

.transition-title {
  color: #fff;
  font-size: 12rem;
  font-weight: 900;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  text-shadow: 0 4px 10px rgba(0,0,0,0.2);
}

.transition-title.show {
  opacity: 1;
  transform: translateY(0);
}
</style>
