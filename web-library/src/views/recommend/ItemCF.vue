<template>
  <div class="page-container">
    <div class="header">
      <h2 class="page-title">FLOW // 物品协同</h2>
      <p class="page-subtitle">根据你评分高的书籍，推荐风格相似的其他好书</p>
    </div>
    
    <div class="content-wrapper">
      <div class="flow-layout" v-if="books.length > 0">
        <div class="timeline-line"></div>
        <div 
          v-for="(book, index) in books" 
          :key="book.bookId" 
          class="flow-item"
          :class="{ 'left': index % 2 === 0, 'right': index % 2 !== 0 }"
          @click="openDetail(book)"
        >
          <div class="connector-dot"></div>
          <div class="item-card">
            <div class="mini-cover">
              <CssBookCover 
                :title="book.bookName" 
                :author="book.bookAuthor || 'Nah'" 
                :id="book.bookId"
              />
            </div>
            <div class="item-info">
              <span class="reason">猜你喜欢</span>
              <h3 class="item-title">{{ book.bookName }}</h3>
              <p class="item-desc">{{ book.bookDescription ? book.bookDescription.substring(0, 50) + '...' : '暂无简介' }}</p>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请给更多喜欢的书打分（评分），我们将为您推荐相似书籍！" />
    </div>

    <BookDetailModal v-model="showDialog" :book="selectedBook" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import myAxios from "@/api/index"
import { useCookies } from '@vueuse/integrations/useCookies'
import CssBookCover from '@/components/common/CssBookCover.vue'
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
        strategy: 'item_cf'
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
  padding: 28px 24px;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #050510; /* Deep Space Background */
  background-image: 
    radial-gradient(circle at 50% 0%, #2a1b3d 0%, transparent 70%),
    linear-gradient(0deg, #050510 0%, #1a1a2e 100%);
  color: #fff;
  font-family: 'Montserrat', sans-serif;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 12px;
}

.header {
  margin-bottom: 0;
  text-align: center;
  position: relative;
  z-index: 2;
}
.content-wrapper {
  min-height: 0;
  overflow: hidden;
}

.page-title {
  font-size: 36px;
  font-weight: 200;
  letter-spacing: 4px;
  margin: 0 0 10px 0;
  color: #00f2ff;
  text-shadow: 0 0 20px rgba(0, 242, 255, 0.5);
  text-transform: uppercase;
}

.page-subtitle {
  color: #b8c1ec;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 1px;
}

.flow-layout {
  position: relative;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 14px;
  width: 100%;
  margin: 0 auto;
  overflow: hidden;
  padding: 8px 2px 24px;
  align-content: start;
}

/* The neon stream line */
.timeline-line {
  display: none;
}

.flow-item {
  position: relative;
  width: 100%;
  z-index: 1;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  min-width: 0;
}

.flow-item:hover {
  transform: scale(1.05);
  z-index: 10;
}

.connector-dot {
  display: none;
}

.flow-item:hover .connector-dot {
  background: #bc13fe;
  border-color: #fff;
  box-shadow: 0 0 20px #bc13fe;
}

/* Stagger items up and down */
.flow-item.left {
  transform: none;
}
.flow-item.right {
  transform: none;
}

.flow-item.left:hover { transform: scale(1.03); }
.flow-item.right:hover { transform: scale(1.03); }

/* Connecting lines to the main stream */
.flow-item::after {
  content: none;
}

.flow-item.left::after {
  top: 100%;
}

.flow-item.right::after {
  bottom: 100%;
  background: linear-gradient(to top, #bc13fe, rgba(188,19,254,0.1));
}

.item-card {
  background: rgba(20, 20, 40, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 15px;
  display: grid;
  grid-template-rows: 340px 1fr;
  gap: 12px;
  min-height: 600px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  overflow: hidden;
  position: relative;
}

.item-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255,255,255,0.05) 0%, transparent 100%);
  pointer-events: none;
}

.mini-cover {
  width: 100%;
  height: 340px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.5);
}

.mini-cover :deep(.css-book-cover) {
  width: 100%;
  height: 100%;
  border-radius: 4px;
}

.item-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.reason {
  font-size: 10px;
  color: #00f2ff;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 5px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #fff;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-desc {
  font-size: 12px;
  color: #b8c1ec;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
