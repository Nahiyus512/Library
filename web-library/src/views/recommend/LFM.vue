<template>
  <div class="page-container dark-theme">
    <div class="header">
      <h2 class="page-title glitch" data-text="MATRIX // 隐语义">MATRIX // 隐语义</h2>
      <p class="page-subtitle">Latent Factor Model: 通过算法挖掘你可能感兴趣的潜在好书</p>
    </div>
    
    <div class="content-wrapper">
      <div class="matrix-layout" v-if="books.length > 0">
        <div 
          v-for="(book, index) in books" 
          :key="book.bookId" 
          class="matrix-card"
          @click="openDetail(book)"
        >
          <div class="score-ring">
            <svg viewBox="0 0 36 36" class="circular-chart">
              <path class="circle-bg"
                d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <path class="circle"
                :stroke-dasharray="(95 - index * 5) + ', 100'"
                d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <text x="18" y="20.35" class="percentage">{{ 95 - index * 5 }}%</text>
            </svg>
          </div>
          <div class="matrix-info">
            <h3 class="matrix-title">{{ book.bookName }}</h3>
            <p class="matrix-author">{{ book.bookAuthor }}</p>
          </div>
          <div class="visual-bar"></div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请多给书打分（评分），让算法更懂你！" />
    </div>

    <BookDetailModal v-model="showDialog" :book="selectedBook" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import myAxios from "@/api/index"
import { useCookies } from '@vueuse/integrations/useCookies'
import BookDetailModal from '@/components/business/BookDetailModal.vue'

const cookie = useCookies()
const books = ref([])
const userName = reactive({
  userId: '',
  username: '',
})
const showDialog = ref(false)
const selectedBook = ref(null)

onMounted(() => {
  userName.username = cookie.get('username')
  getUserByName()
})

const getUserByName = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName', {
      params: { name: userName.username }
    })
    userName.userId = res.data.data.id
    getRecommendBook(userName.userId)
  } catch (e) {
    console.log(e)
  }
}

const getRecommendBook = async (userId: any) => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookScore/recommend', {
      params: {
        userId: userId,
        strategy: 'lfm'
      }
    })
    books.value = res.data.data
  } catch (e) {
    console.log(e)
  }
}

const openDetail = (book: any) => {
  selectedBook.value = book
  showDialog.value = true
}
</script>

<style scoped>
.page-container {
  padding: 40px;
  height: 100%;
  overflow-y: hidden; /* Prevent scroll */
  background-color: #0f0f0f;
  color: #00ff41;
  font-family: 'Courier New', Courier, monospace;
  display: flex;
  flex-direction: column;
}

.header {
  margin-bottom: 40px;
  border-bottom: 1px solid #00ff41;
  padding-bottom: 20px;
  text-align: center;
  flex-shrink: 0;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center; /* Center content vertically */
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px 0;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.page-subtitle {
  color: #008f11;
  font-size: 14px;
}

.matrix-layout {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 40px; /* Increased gap */
  max-width: 1400px; /* Increased max-width */
  margin: 0 auto;
  width: 100%;
}

.matrix-card {
  background: rgba(0, 255, 65, 0.05);
  border: 1px solid #003b00;
  padding: 40px; /* Increased padding */
  display: flex;
  align-items: center;
  gap: 30px; /* Increased gap */
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  width: 45%; /* Use percentage width to fill space */
  min-width: 400px;
  height: 180px; /* Increased height */
  flex-shrink: 0;
}

.matrix-card:hover {
  background: rgba(0, 255, 65, 0.15);
  border-color: #00ff41;
  box-shadow: 0 0 15px rgba(0, 255, 65, 0.3);
}

.score-ring {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.circular-chart {
  display: block;
  margin: 0 auto;
  max-width: 100%;
  max-height: 100%;
}

.circle-bg {
  fill: none;
  stroke: #003b00;
  stroke-width: 2.5;
}

.circle {
  fill: none;
  stroke: #00ff41;
  stroke-width: 2.5;
  stroke-linecap: round;
  animation: progress 1s ease-out forwards;
}

@keyframes progress {
  0% { stroke-dasharray: 0 100; }
}

.percentage {
  fill: #00ff41;
  font-family: monospace;
  font-weight: bold;
  font-size: 0.5em;
  text-anchor: middle;
}

.matrix-info {
  flex: 1;
}

.matrix-title {
  font-size: 20px;
  margin: 0 0 8px 0;
  color: #fff;
  font-weight: 700;
}

.matrix-author {
  font-size: 14px;
  color: #008f11;
}

.visual-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  background: #00ff41;
  width: 0%;
  transition: width 0.3s;
}

.matrix-card:hover .visual-bar {
  width: 100%;
}

/* Glitch Effect */
.glitch {
  position: relative;
}
.glitch::before,
.glitch::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #0f0f0f;
}
.glitch::before {
  left: 2px;
  text-shadow: -1px 0 red;
  clip: rect(24px, 550px, 90px, 0);
  animation: glitch-anim-2 3s infinite linear alternate-reverse;
}
.glitch::after {
  left: -2px;
  text-shadow: -1px 0 blue;
  clip: rect(85px, 550px, 140px, 0);
  animation: glitch-anim 2.5s infinite linear alternate-reverse;
}
@keyframes glitch-anim {
  0% { clip: rect(14px, 9999px, 127px, 0); }
  20% { clip: rect(80px, 9999px, 34px, 0); }
  40% { clip: rect(60px, 9999px, 50px, 0); }
  60% { clip: rect(3px, 9999px, 92px, 0); }
  80% { clip: rect(110px, 9999px, 120px, 0); }
  100% { clip: rect(40px, 9999px, 10px, 0); }
}
@keyframes glitch-anim-2 {
  0% { clip: rect(65px, 9999px, 100px, 0); }
  20% { clip: rect(10px, 9999px, 70px, 0); }
  40% { clip: rect(120px, 9999px, 20px, 0); }
  60% { clip: rect(5px, 9999px, 140px, 0); }
  80% { clip: rect(50px, 9999px, 40px, 0); }
  100% { clip: rect(90px, 9999px, 60px, 0); }
}
</style>