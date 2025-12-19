<template>
  <div class="css-book-cover" :style="coverStyle">
    <div class="book-spine"></div>
    <div class="cover-content">
      <div class="title-section">
        <h3 class="cover-title" :class="{ 'long-title': title.length > 10 }">{{ title }}</h3>
      </div>
      <div class="author-section">
        <span class="cover-author">{{ author }}</span>
      </div>
      <div class="decorative-element"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  title: string;
  author: string;
  id: number | string;
}>();

const colors = [
  '#E0BBE4', '#957DAD', '#D291BC', '#FEC8D8', '#FFDFD3',
  '#F0E6EF', '#93B7BE', '#554D56', '#967AA1', '#192A51',
  '#D5C6E0', '#AAA1C8', '#967AA1', '#192A51', '#F5E6CC',
  '#A9B7C0', '#C2D4D8', '#DBE9EE', '#F0F5F9', '#FFFFFF',
  '#2C3E50', '#E74C3C', '#ECF0F1', '#3498DB', '#2980B9'
];

const patterns = [
  'radial-gradient(circle at 10% 20%, rgba(255,255,255,0.1) 0%, transparent 20%)',
  'linear-gradient(45deg, rgba(0,0,0,0.05) 25%, transparent 25%, transparent 50%, rgba(0,0,0,0.05) 50%, rgba(0,0,0,0.05) 75%, transparent 75%, transparent)',
  'repeating-linear-gradient(0deg, transparent, transparent 19px, rgba(0,0,0,0.05) 20px)',
  'none'
];

const generateStyle = (id: string | number, str: string) => {
  const seed = (typeof id === 'number' ? id : parseInt(id as string) || 0) + str.length;
  const colorIndex = seed % colors.length;
  const patternIndex = seed % patterns.length;
  const textColor = isLight(colors[colorIndex]) ? '#000' : '#fff';
  
  return {
    backgroundColor: colors[colorIndex],
    backgroundImage: patterns[patternIndex],
    color: textColor
  };
};

const isLight = (color: string) => {
  // Very basic check, can be improved
  if (color === '#2C3E50' || color === '#192A51' || color === '#554D56') return false;
  return true;
};

const coverStyle = computed(() => generateStyle(props.id, props.title));
</script>

<style scoped>
.css-book-cover {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  box-shadow: inset 2px 0 5px rgba(0,0,0,0.1); /* Spine shadow effect */
  padding: 15px;
  box-sizing: border-box;
}

.book-spine {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: rgba(0,0,0,0.15);
  z-index: 2;
}

.cover-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  text-align: center;
  z-index: 1;
  border: 1px solid rgba(0,0,0,0.1);
  padding: 10px;
}

.title-section {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-title {
  font-family: 'Georgia', serif;
  font-size: 16px;
  font-weight: bold;
  line-height: 1.2;
  margin: 0;
  word-wrap: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.cover-title.long-title {
  font-size: 13px;
}

.cover-title.very-long-title {
  font-size: 11px;
}

.author-section {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  max-height: 20px;
  overflow: hidden;
}

.cover-author {
  font-family: sans-serif;
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
  opacity: 0.8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.decorative-element {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 60%;
  border: 1px solid currentColor;
  opacity: 0.2;
  pointer-events: none;
}
</style>