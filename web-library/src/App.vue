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

.bw-exit-confirm {
  border-radius: 0 !important;
  border: 2px solid #000 !important;
  box-shadow: none !important;
}

.bw-exit-confirm .el-message-box__header {
  border-bottom: 1px solid #000;
  padding-bottom: 12px;
}

.bw-exit-confirm .el-message-box__title {
  color: #000;
  font-weight: 700;
}

.bw-exit-confirm .el-message-box__message p {
  color: #000;
  line-height: 1.6;
}

.bw-exit-confirm .el-message-box__btns {
  display: flex;
  gap: 10px;
}

.bw-exit-confirm .bw-exit-cancel-btn {
  border: 1px solid #000 !important;
  background: #fff !important;
  color: #000 !important;
}

.bw-exit-confirm .bw-exit-confirm-btn {
  border: 1px solid #000 !important;
  background: #000 !important;
  color: #fff !important;
}

.bw-exit-confirm .bw-exit-cancel-btn:hover,
.bw-exit-confirm .bw-exit-confirm-btn:hover {
  opacity: 0.9;
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
