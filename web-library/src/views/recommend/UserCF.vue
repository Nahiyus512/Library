<template>
  <div class="page-container">
    <div class="header">
      <h2 class="page-title">COMMUNITY // 用户协同</h2>
      <p class="page-subtitle">看看和你口味相似的书友都在读什么</p>
    </div>
    
    <div class="content-wrapper">
      <div class="grid-layout" v-if="books.length > 0">
        <div 
          v-for="(book, index) in books" 
          :key="book.bookId" 
          class="user-card"
          @click="openDetail(book)"
        >
          <div class="card-inner">
            <div class="cover-area">
              <CssBookCover 
                :title="book.bookName" 
                :author="book.bookAuthor || 'Unknown'" 
                :id="book.bookId"
              />
            </div>
            <div class="info-area">
              <div class="match-badge">9{{ 9 - index }}% 匹配</div>
              <h3 class="book-name">{{ book.bookName }}</h3>
              <div class="user-avatars">
                <!-- Fake user avatars to simulate community -->
                <div class="avatar" v-for="n in 3" :key="n" :style="{ backgroundColor: getRandomColor(index + n) }"></div>
                <span class="count">+{{ (index + 1) * 12 }} 人在读</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请多给看过的书打分，让我们找到你的书友！" />
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
        strategy: 'user_cf'
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

const getRandomColor = (seed: number) => {
  const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEEAD'];
  return colors[seed % colors.length];
}
</script>

<style scoped>
.page-container {
  padding: 40px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff9f0; /* Warm/Social background */
  background-image: radial-gradient(#ffeaa7 20%, transparent 20%);
  background-size: 30px 30px;
  font-family: 'Varela Round', sans-serif; /* Rounded/Friendly font */
}

.header {
  margin-bottom: 40px;
  text-align: center;
  background: #fff;
  padding: 20px;
  border-radius: 20px;
  display: inline-block;
  box-shadow: 0 4px 15px rgba(255, 165, 2, 0.1);
  position: relative;
  left: 50%;
  transform: translateX(-50%);
}

.page-title {
  font-size: 32px;
  font-weight: 900;
  margin: 0 0 5px 0;
  letter-spacing: -1px;
  color: #ff9f43;
}

.page-subtitle {
  color: #8395a7;
  font-size: 14px;
}

.grid-layout {
  display: flex;
  justify-content: center;
  flex-wrap: nowrap;
  gap: 30px;
  max-width: 100%;
  margin: 0 auto;
  overflow-x: auto;
  padding: 20px 0;
}

.user-card {
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(255, 159, 67, 0.15);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  height: 420px;
  width: 22%;
  min-width: 260px;
  flex-shrink: 0;
  border: 2px solid #fff;
}

.user-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(255, 159, 67, 0.25);
  border-color: #ff9f43;
}

.card-inner {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.cover-area {
  height: 60%;
  background: #fff5e6;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* Decorative circles in background */
.cover-area::after {
  content: '';
  position: absolute;
  width: 150%;
  height: 150%;
  background: #fff;
  border-radius: 50%;
  top: 60%;
  left: -25%;
  opacity: 0.5;
}

.cover-area :deep(.css-book-cover) {
  width: 140px;
  height: 200px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  transform: rotate(-3deg);
  transition: transform 0.3s;
  z-index: 1;
}

.user-card:hover .cover-area :deep(.css-book-cover) {
  transform: rotate(0deg) scale(1.1);
}

.info-area {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: #fff;
  position: relative;
  z-index: 2;
}

.match-badge {
  display: inline-flex;
  align-items: center;
  background: #ff9f43;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 100px;
  align-self: flex-start;
  margin-bottom: 10px;
  box-shadow: 0 2px 5px rgba(255, 159, 67, 0.4);
}

.book-name {
  font-size: 18px;
  font-weight: 800;
  margin: 0 0 10px 0;
  line-height: 1.3;
  color: #2d3436;
}

.user-avatars {
  display: flex;
  align-items: center;
  margin-top: auto;
  background: #f7f1e3;
  padding: 8px 12px;
  border-radius: 12px;
}

.avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid #fff;
  margin-right: -8px;
  position: relative;
  z-index: 1;
}

.count {
  margin-left: 15px;
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 600;
}
</style>