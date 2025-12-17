<script setup lang="ts">
import { computed } from "vue";
import { RouterView, useRoute } from "vue-router";
import TransitionOverlay from "@/components/common/TransitionOverlay.vue";
import ReadingProgressBar from "@/components/common/ReadingProgressBar.vue";
import { books } from "@/data/books";

const route = useRoute();

const currentBook = computed(() => {
  return books.find(b => b.path === route.path);
});

const isBookPage = computed(() => !!currentBook.value);
const bookColor = computed(() => currentBook.value?.colorTheme || '#000000');
</script>

<template>
  <div class="main">
    <ReadingProgressBar 
      :is-active="isBookPage"
      :color="bookColor"
    />
    <RouterView></RouterView>
    <TransitionOverlay />
  </div>
</template>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #fff; /* Ensure white background */
}
</style>

<style scoped>
.main {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #fff;
  overflow: hidden;
}
</style>
