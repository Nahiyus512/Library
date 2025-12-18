<template>
  <div class="zen-cover" :class="{ 'card-mode': mode === 'card', hero: mode === 'hero' }">
    <div class="cover-content">
      <!-- Road Perspective -->
      <div class="road-scene">
        <div class="sky"></div>
        <div class="ground">
          <div class="road">
            <div class="road-lines"></div>
          </div>
        </div>
      </div>

      <!-- Title -->
      <div class="title-layer" :class="mode === 'hero' ? 'state-expanded' : 'state-collapsed'">
        <h1 class="zen-char">禅</h1>
        <div class="subtitle-group">
          <span>ZEN AND THE ART OF</span>
          <span>MOTORCYCLE MAINTENANCE</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  mode?: 'card' | 'hero'
}>();
</script>

<style scoped>
.zen-cover {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  background-color: #f3f4f6;
  font-family: 'Microsoft YaHei', sans-serif;
}

.cover-content {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Road Scene */
.road-scene {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  display: flex;
  flex-direction: column;
}

.sky {
  flex: 1;
  background: linear-gradient(to bottom, #87CEEB 0%, #e0f2fe 100%);
}

.ground {
  flex: 1;
  background: #047857; /* Zen Green */
  position: relative;
  overflow: hidden;
  perspective: 100px;
}

.road {
  position: absolute;
  top: 0; left: 50%;
  width: 60%;
  height: 100%;
  background: #333;
  transform: translateX(-50%) rotateX(30deg) scaleY(2);
  transform-origin: top center;
  box-shadow: 0 0 20px rgba(0,0,0,0.3);
}

.road-lines {
  position: absolute;
  top: 0; left: 50%;
  width: 4px;
  height: 100%;
  background: repeating-linear-gradient(
    to bottom,
    #fff 0%,
    #fff 20%,
    transparent 20%,
    transparent 40%
  );
  transform: translateX(-50%);
  animation: moveRoad 1s linear infinite;
}

@keyframes moveRoad {
  from { background-position: 0 0; }
  to { background-position: 0 20px; } /* Adjust based on pattern size */
}

/* Title Layer */
.title-layer {
  z-index: 10;
  text-align: center;
  color: #111;
  mix-blend-mode: multiply; /* Blend with background */
}

/* 状态：未展开 */
.state-collapsed .zen-char {
  font-size: 8rem;
  line-height: 1;
  font-weight: 900;
  margin: 0;
  opacity: 0.8;
  text-shadow: 0 10px 20px rgba(0,0,0,0.2);
  transition: all 0.3s ease;
}

.state-collapsed .subtitle-group {
  display: flex;
  flex-direction: column;
  font-size: 0.6rem;
  font-weight: 700;
  margin-top: 10px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

/* 状态：已展开 */
.state-expanded .zen-char {
  font-size: 12rem;
  line-height: 1;
  font-weight: 900;
  margin: 0;
  opacity: 0.8;
  text-shadow: 0 10px 20px rgba(0,0,0,0.2);
  transition: all 0.3s ease;
}

.state-expanded .subtitle-group {
  display: flex;
  flex-direction: column;
  font-size: 0.8rem;
  font-weight: 700;
  margin-top: 10px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}
</style>
