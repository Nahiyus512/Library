<script setup lang="ts">
import {RouterView} from "vue-router";
</script>

<template>
  <div class="main">
    <RouterView></RouterView>
  </div>
</template>

<style scoped>
.main {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
}
</style>
