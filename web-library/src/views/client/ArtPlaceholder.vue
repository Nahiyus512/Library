<template>
  <div class="detail-container">
    <nav class="nav-bar">
      <button class="back-btn" @click="$router.back()">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <span class="nav-title">Prompt Details</span>
    </nav>

    <div class="content-wrapper" v-if="currentBook">
      <!-- Visual Side -->
      <div class="visual-section">
        <div class="art-display" :style="currentBook.visualStyle">
          <div class="visual-layer" :style="currentBook.layerStyle"></div>
        </div>
      </div>

      <!-- Details Side -->
      <div class="info-section">
        <div class="header">
          <div class="tags">
            <span class="tag model">{{ currentBook.model }}</span>
            <span class="tag category">{{ currentBook.category }}</span>
          </div>
          <h1 class="title">{{ currentBook.title }}</h1>
        </div>

        <div class="prompt-box">
          <h3 class="box-label">Prompt</h3>
          <p class="prompt-text">
            High-fidelity UI/UX design of a "Vibary" style digital art library website featuring an infinite masonry waterfall grid layout on a clean white background. 
            The grid is densely packed with vertical and horizontal art cards representing "{{ currentBook.title }}" with diverse visual styles.
            <br><br>
            <strong>--ar 16:9 --v 6.1 --stylize 250</strong>
          </p>
          <button class="copy-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
            </svg>
            Copy Prompt
          </button>
        </div>

        <div class="meta-grid">
          <div class="meta-item">
            <label>Dimensions</label>
            <span>16:9</span>
          </div>
          <div class="meta-item">
            <label>Seed</label>
            <span>29384710</span>
          </div>
          <div class="meta-item">
            <label>Sampler</label>
            <span>v6 (Default)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const currentBook = ref<any>(null);

const bookList = [
  { title: "三体", category: "Sci-Fi", model: "Midjourney v6", visual: "cosmic" },
  { title: "人类简史", category: "History", model: "DALL-E 3", visual: "earth" },
  { title: "银河系漫游指南", category: "Sci-Fi", model: "Niji 6", visual: "retro" },
  { title: "色彩互动学", category: "Design", model: "Stable Diffusion", visual: "color" },
  { title: "写给大家看的设计书", category: "Design", model: "Midjourney v5.2", visual: "typo" },
  { title: "平面设计中的网格系统", category: "Design", model: "Midjourney v6", visual: "grid" },
  { title: "沙丘", category: "Sci-Fi", model: "Midjourney v6", visual: "dune" },
  { title: "1984", category: "Dystopia", model: "DALL-E 3", visual: "eye" },
  { title: "美丽新世界", category: "Dystopia", model: "Stable Diffusion", visual: "neon" },
  { title: "禅与摩托车维修艺术", category: "Philosophy", model: "Midjourney v6", visual: "zen" },
  { title: "娱乐至死", category: "Sociology", model: "DALL-E 3", visual: "tv" },
  { title: "生命 3.0", category: "Future", model: "Niji 6", visual: "ai" }
];

const getVisualStyle = (type: string) => {
  switch(type) {
    case 'cosmic': return { background: 'linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)' };
    case 'earth': return { background: 'linear-gradient(to top, #e6e9f0 0%, #eef1f5 100%)' };
    case 'retro': return { background: 'linear-gradient(to top, #30cfd0 0%, #330867 100%)' };
    case 'color': return { background: '#ffffff' };
    case 'typo': return { background: '#f5f5f5' };
    case 'grid': return { background: '#e0e0e0' };
    case 'dune': return { background: 'linear-gradient(to right, #ffecd2 0%, #fcb69f 100%)' };
    case 'eye': return { background: 'linear-gradient(to top, #000000 0%, #434343 100%)' };
    case 'neon': return { background: 'linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%)' };
    case 'zen': return { background: 'linear-gradient(to top, #96fbc4 0%, #f9f586 100%)' };
    case 'tv': return { background: 'linear-gradient(to right, #868f96 0%, #596164 100%)' };
    case 'ai': return { background: 'linear-gradient(-20deg, #b721ff 0%, #21d4fd 100%)' };
    default: return { background: '#eee' };
  }
};

const getLayerStyle = (type: string) => {
  switch(type) {
    case 'cosmic': return { 
      width: '60%', height: '60%', borderRadius: '50%', 
      background: 'radial-gradient(circle at 30% 30%, rgba(255,255,255,0.8), transparent)',
      boxShadow: '0 0 30px rgba(255,255,255,0.2)',
      top: '20%', left: '20%'
    };
    case 'earth': return {
      width: '100%', height: '100%', 
      background: 'url("data:image/svg+xml;utf8,<svg xmlns=\'http://www.w3.org/2000/svg\' width=\'100\' height=\'100\'><path d=\'M0 100 Q 50 50 100 100\' stroke=\'%23ccc\' fill=\'none\'/></svg>")',
      opacity: 0.5
    };
    case 'color': return {
      width: '50%', height: '50%', background: '#ff4b1f',
      boxShadow: '20px 20px 0 #1fddff',
      top: '25%', left: '25%'
    };
    case 'grid': return {
      width: '100%', height: '100%',
      backgroundImage: 'linear-gradient(#999 1px, transparent 1px), linear-gradient(90deg, #999 1px, transparent 1px)',
      backgroundSize: '20px 20px',
      opacity: 0.3
    };
    case 'dune': return {
      width: '100%', height: '40%', bottom: 0,
      background: 'rgba(255,255,255,0.3)',
      clipPath: 'polygon(0 50%, 100% 0, 100% 100%, 0% 100%)'
    };
    case 'eye': return {
      width: '40px', height: '40px', borderRadius: '50%',
      background: '#fff', top: '50%', left: '50%', transform: 'translate(-50%, -50%)',
      boxShadow: '0 0 0 10px rgba(255,255,255,0.1)'
    };
    default: return {
      width: '80%', height: '2px', background: '#ccc', top: '50%', left: '10%'
    };
  }
};

onMounted(() => {
  const id = Number(route.params.id);
  const book = bookList[id - 1] || bookList[0];
  
  currentBook.value = {
    ...book,
    visualStyle: getVisualStyle(book.visual),
    layerStyle: getLayerStyle(book.visual)
  };
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&display=swap');

.detail-container {
  min-height: 100vh;
  background: #fff;
  font-family: 'Inter', sans-serif;
  color: #1a1a1a;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  padding: 20px 40px;
  display: flex;
  align-items: center;
  gap: 20px;
  position: sticky;
  top: 0;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(10px);
  z-index: 100;
}

.back-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background 0.2s;
  color: #333;
}

.back-btn:hover {
  background: #f0f0f0;
}

.nav-title {
  font-size: 14px;
  font-weight: 500;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.content-wrapper {
  flex: 1;
  display: flex;
  padding: 0 40px 40px;
  gap: 60px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.visual-section {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.art-display {
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
}

.visual-layer {
  position: absolute;
  transition: transform 10s ease-in-out;
}

.info-section {
  flex: 0.8;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 30px;
}

.header {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tags {
  display: flex;
  gap: 10px;
}

.tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.tag.model {
  background: #000;
  color: #fff;
}

.tag.category {
  background: #f0f0f0;
  color: #666;
}

.title {
  font-size: 42px;
  font-weight: 600;
  letter-spacing: -1px;
  margin: 0;
  line-height: 1.1;
}

.prompt-box {
  background: #f9f9f9;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #eee;
}

.box-label {
  font-size: 12px;
  text-transform: uppercase;
  color: #999;
  margin: 0 0 12px 0;
  letter-spacing: 1px;
}

.prompt-text {
  font-size: 14px;
  line-height: 1.6;
  color: #444;
  margin-bottom: 20px;
  font-family: 'Inter', sans-serif;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border: 1px solid #ddd;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.copy-btn:hover {
  border-color: #000;
  background: #000;
  color: #fff;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-item label {
  font-size: 11px;
  color: #999;
  text-transform: uppercase;
}

.meta-item span {
  font-size: 13px;
  font-weight: 500;
}

@media (max-width: 900px) {
  .content-wrapper {
    flex-direction: column;
  }
}
</style>