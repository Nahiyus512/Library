<script setup lang="ts">
import {RouterView} from "vue-router";
import TransitionOverlay from "./components/TransitionOverlay.vue";
</script>

<template>
  <div class="main">
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
