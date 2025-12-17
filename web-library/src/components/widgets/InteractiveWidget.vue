<template>
  <div class="book-card-wrapper widget-wrapper">
    <div class="book-shadow"></div>
    <div class="book-body unified-card widget-body">
      <BookshelfWidget v-if="type === 'bookshelf'" />
      <RadioWidget v-else-if="type === 'radio'" />
    </div>
  </div>
</template>

<script setup lang="ts">
import BookshelfWidget from './BookshelfWidget.vue';
import RadioWidget from './RadioWidget.vue';

defineProps<{
  type: 'bookshelf' | 'radio';
}>();
</script>

<style scoped>
/* Copied from BookCard.vue to ensure exact match */
.book-card-wrapper {
  margin-bottom: 40px;
  opacity: 0;
  animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  position: relative;
  aspect-ratio: 0.72;
  cursor: default; /* Widgets might handle their own cursor */
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

@keyframes fadeUp { from { opacity: 0; transform: translateY(50px); } to { opacity: 1; transform: translateY(0); } }

/* Widget Specific */
.widget-body {
  padding: 0; /* Let widgets handle padding */
  background: #fff;
}
</style>
