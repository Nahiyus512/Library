<template>
  <div 
    class="tk-cover-container" 
    :class="[mode === 'hero' ? 'mode-hero' : 'mode-card']"
    @mousemove="handleMouseMove" 
    @mouseleave="resetState"
    ref="containerRef"
  >
    <!-- 1. 背景与纹理 -->
    <div class="bg-layer paper-texture"></div>
    <div class="bg-layer vignette"></div>
    
    <!-- 2. 装饰层：花瓣 (前景) -->
    <div class="petals-layer">
      <div v-for="n in 6" :key="n" class="petal" :style="getPetalStyle(n)"></div>
    </div>

    <!-- 3. 内容主容器 (用于整体定位) -->
    <div class="content-wrapper">
      
      <!-- A. 左侧：标题组 -->
      <div class="title-section" :style="parallaxStyle(0.05)">
        <div class="main-title">三国</div>
        <div class="seal-container">
          <div class="seal-box">演义</div>
        </div>
      </div>

      <!-- B. 右侧：旗帜组 -->
      <div class="flags-section" :style="parallaxStyle(0.12)">
        
        <!-- 魏 (Wei) -->
        <div class="flag-wrapper flag-wei">
          <div class="flag-pole"></div>
          <div class="flag-cloth">
            <span class="flag-text">魏</span>
          </div>
        </div>

        <!-- 蜀 (Shu) -->
        <div class="flag-wrapper flag-shu">
          <div class="flag-pole"></div>
          <div class="flag-cloth">
            <span class="flag-text">蜀</span>
          </div>
        </div>

        <!-- 吴 (Wu) -->
        <div class="flag-wrapper flag-wu">
          <div class="flag-pole"></div>
          <div class="flag-cloth">
            <span class="flag-text">吴</span>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = withDefaults(defineProps<{
  mode?: 'card' | 'hero'
}>(), {
  mode: 'card'
});

const containerRef = ref<HTMLElement | null>(null);
const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e: MouseEvent) => {
  if (!containerRef.value) return;
  const rect = containerRef.value.getBoundingClientRect();
  mouseX.value = ((e.clientX - rect.left) / rect.width - 0.5) * 2;
  mouseY.value = ((e.clientY - rect.top) / rect.height - 0.5) * 2;
};

const resetState = () => {
  mouseX.value = 0;
  mouseY.value = 0;
};

// 视差效果：让整个组轻微移动，不影响内部hover
const parallaxStyle = (factor: number) => {
  // 仅在展开模式或鼠标移动幅度大时启用明显视差
  const moveX = mouseX.value * factor * 30;
  const moveY = mouseY.value * factor * 30;
  return {
    transform: `translate3d(${moveX}px, ${moveY}px, 0)`
  };
};

const getPetalStyle = (n: number) => {
  const delay = Math.random() * 5;
  const duration = 5 + Math.random() * 5;
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `-${delay}s`,
    animationDuration: `${duration}s`,
    opacity: 0.4 + Math.random() * 0.4
  };
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&family=Noto+Serif+SC:wght@900&display=swap');

/* --- 基础容器 --- */
.tk-cover-container {
  width: 100%;
  aspect-ratio: 1 / 1;
  position: relative;
  overflow: hidden;
  background-color: #e6dac8;
  font-family: 'Ma Shan Zheng', cursive;
  user-select: none;
  cursor: default;
}

.bg-layer {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.paper-texture {
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.7' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.1'/%3E%3C/svg%3E");
  mix-blend-mode: multiply;
}
.vignette {
  background: radial-gradient(circle at 40% 50%, transparent 50%, rgba(56, 46, 36, 0.4) 100%);
}

/* --- 核心布局层 --- */
.content-wrapper {
  position: absolute;
  top: 42%; /* 整体上移 (原50%) */
  left: 8%; /* 整体左侧定位 */
  right: 5%;
  transform: translateY(-50%);
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: flex-start;
  height: 60%; /* 限制高度区域 */
}

/* --- 左侧：标题部分 --- */
.title-section {
  display: flex;
  align-items: flex-end; /* 底部对齐，使印章靠近“国”字 */
  z-index: 10;
  transition: transform 0.1s ease-out;
  flex-shrink: 0; /* 防止被挤压 */
}

.main-title {
  writing-mode: vertical-rl; /* 改为竖向排版 */
  text-orientation: upright;
  font-weight: 800;
  color: #1a1a1a;
  line-height: 1;
  letter-spacing: 0.05em;
  text-shadow: 2px 2px 0px rgba(255, 255, 255, 0.4);
}

.seal-container {
  margin-left: 3px;
  display: flex;
  align-items: flex-end;
  padding-bottom: 2px;
}

.seal-box {
  border: 1.2px solid #b22222;
  color: #b22222;
  writing-mode: vertical-rl; /* 印章内文字竖排 */
  font-family: 'Noto Serif SC', serif;
  font-weight: bold;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.5);
  box-shadow: 1px 1px 2px rgba(0,0,0,0.2);
  padding: 4px 1px; /* 左右宽度缩小 */
}

/* --- 右侧：旗帜部分 --- */
.flags-section {
  position: relative;
  flex-grow: 1;
  height: 100%;
  margin-left: 8%; /* 与标题的间距 */
  pointer-events: auto; /* 允许交互 */
  top: -30px; /* 整体上移 */
  transition: transform 0.3s ease;
}

.flag-wrapper {
  position: absolute;
  top: 50%;
  transform-origin: bottom center;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); /* 弹性过渡 */
  cursor: pointer;
  filter: drop-shadow(2px 4px 6px rgba(0,0,0,0.4));
}

/* 旗帜的具体画法 */
.flag-pole {
  width: 4px;
  height: 120%;
  background: #2c2c2c;
  position: absolute;
  left: -4px;
  top: 0;
  border-radius: 2px;
}

.flag-cloth {
  width: 50px;
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  clip-path: polygon(0% 0%, 100% 0%, 100% 100%, 50% 80%, 0% 100%);
  position: relative;
  overflow: hidden;
}

/* 旗帜纹理 */
.flag-cloth::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, transparent 50%, rgba(0,0,0,0.2) 100%);
  z-index: 1;
}

.flag-text {
  font-size: 1.8rem;
  font-weight: bold;
  z-index: 2;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

/* 各国旗帜定位与颜色 */

/* 魏：左后方，高冷 */
.flag-wei {
  left: 0%;
  margin-top: -40px;
  z-index: 1;
  transform: rotate(-10deg) scale(0.9);
}
.flag-wei .flag-cloth { background: linear-gradient(to bottom, #2c3e50, #000); border-top: 3px solid #34495e; }

/* 蜀：右后方，赤诚 */
.flag-shu {
  left: 45%;
  margin-top: -20px;
  z-index: 2;
  transform: rotate(5deg) scale(0.95);
}
.flag-shu .flag-cloth { background: linear-gradient(to bottom, #c0392b, #581212); border-top: 3px solid #e74c3c; }

/* 吴：正前方，辉煌 */
.flag-wu {
  left: 20%;
  margin-top: 10px;
  z-index: 3;
  transform: rotate(-2deg) scale(1);
}
.flag-wu .flag-cloth { background: linear-gradient(to bottom, #d4ac0d, #574603); border-top: 3px solid #f1c40f; }


/* --- 关键交互：旗帜悬浮动画 --- */
.flag-wrapper:hover {
  z-index: 100 !important; /* 悬浮时层级最高 */
  transform: translateY(-15px) scale(1.15) rotate(0deg) !important; /* 修正角度并放大 */
  filter: drop-shadow(4px 8px 12px rgba(0,0,0,0.6)) brightness(1.1);
}

.flag-wrapper:hover .flag-text {
  color: #fff;
  text-shadow: 0 0 8px rgba(255,255,255,0.8);
}

/* --- 状态适配 --- */

/* CARD Mode (小卡片) */
.mode-card .main-title {
  font-size: 3rem;
}
.mode-card .seal-box {
  font-size: 0.6rem;
  padding: 2px 10px;
}
.mode-card .flag-wrapper {
  transform: scale(0.6) rotate(var(--r, 0deg)); /* 整体缩小 */
}
/* 卡片模式下重置一下旗帜位置，使其更紧凑 */
.mode-card .flag-wei { left: -10px; margin-top: -20px; transform: rotate(-10deg) scale(0.7); }
.mode-card .flag-shu { left: 40px; margin-top: -10px; transform: rotate(5deg) scale(0.7); }
.mode-card .flag-wu { left: 15px; margin-top: 10px; transform: rotate(-2deg) scale(0.75); }


/* HERO Mode (展开/详情) */
.mode-hero .main-title {
  font-size: 5.5rem; /* 大字 */
}
.mode-hero .seal-box {
  font-size: 1.2rem;
  padding: 6px 8px;
  background: #b22222; /* 实心红 */
  color: #fff;
  border: none;
  transform: rotate(3deg); /* 盖章倾斜感 */
}
.mode-hero .flag-cloth {
  width: 70px; /* 更大的旗帜 */
  height: 100px;
}
.mode-hero .flag-text {
  font-size: 2.5rem;
}
/* 展开模式下旗帜散开一点 */
.mode-hero .flag-wei { left: 10px; margin-top: -50px; }
.mode-hero .flag-shu { left: 90px; margin-top: -30px; }
.mode-hero .flag-wu { left: 50px; margin-top: 20px; }

/* 展开模式下整体左移 */
.mode-hero .flags-section {
  transform: translateX(-30px);
}


/* --- 装饰：花瓣 --- */
.petals-layer {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 20;
}
.petal {
  position: absolute;
  top: -20px;
  width: 8px;
  height: 12px;
  background: #ffb7b2;
  border-radius: 100% 0 100% 0;
  animation: fall linear infinite;
}
@keyframes fall {
  to { transform: translate(40px, 400px) rotate(360deg); opacity: 0; }
}
</style>