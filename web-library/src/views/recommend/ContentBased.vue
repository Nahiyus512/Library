<template>
  <div class="page-container">
    <div class="header">
      <h2 class="page-title">TAGS // 内容推荐</h2>
      <p class="page-subtitle">根据你书架中书籍的作者、分类等属性进行推荐</p>
    </div>
    
    <div class="content-wrapper">
      <div class="compact-grid-layout" v-if="books && books.length > 0">
        <div 
          v-for="(book, index) in books" 
          :key="book.bookId" 
          class="compact-card"
          :class="{ 'centered-card': index === 4 }"
          @click="openDetail(book)"
        >
          <div class="card-left">
            <CssBookCover 
              :title="book.bookName" 
              :author="book.bookAuthor || '未知'" 
              :id="book.bookId"
            />
          </div>
          <div class="card-right">
            <div class="card-header">
              <div class="index-badge">0{{ index + 1 }}</div>
              <h3 class="compact-title">{{ book.bookName }}</h3>
            </div>
            <div class="tag-row">
              <span class="mini-tag category" v-if="book.bookClassify">{{ book.bookClassify }}</span>
              <span class="mini-tag author" v-if="book.bookAuthor">{{ book.bookAuthor }}</span>
            </div>
            <p class="compact-desc">{{ book.bookDescription || '暂无简介' }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请将喜欢的书加入书架，我们正在学习你的口味。" />
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
        strategy: 'content_based'
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
  overflow-y: auto;
  background-color: #f5f5f7; /* Archive/Library Grey */
  font-family: 'Georgia', serif;
}

.header {
  margin-bottom: 30px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 5px 0;
  color: #333;
  letter-spacing: 1px;
  border-bottom: 3px solid #ff4757;
  display: inline-block;
  padding-bottom: 5px;
}

.page-subtitle {
  color: #666;
  font-size: 14px;
  font-style: italic;
  margin-top: 10px;
}

.compact-grid-layout {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
  margin-top: 40px;
  width: 100%;
  padding: 0 20px;
}

.compact-card {
  background: #fff;
  border-radius: 2px;
  display: flex;
  height: 160px;
  overflow: visible; /* Allow badge to pop out */
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 1px 1px 0 rgba(0,0,0,0.1);
  border: 1px solid #e1e4e8;
  position: relative;
}

.compact-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #ff4757;
  transition: width 0.3s;
}

.centered-card {
  grid-column: 1 / -1;
  width: 60%;
  justify-self: center;
}

.compact-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.compact-card:hover::before {
  width: 8px;
}

.card-left {
  width: 110px;
  flex-shrink: 0;
  padding: 15px;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Ensure CssBookCover fits exactly */
.card-left :deep(.css-book-cover) {
  width: 90%;
  height: 90%;
  border-radius: 2px;
  box-shadow: 2px 2px 5px rgba(0,0,0,0.2);
}

.card-right {
  flex: 1;
  padding: 20px 25px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.index-badge {
  font-size: 14px;
  font-weight: 700;
  color: #e1e4e8;
  font-family: sans-serif;
}

.compact-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tag-row {
  display: flex;
  gap: 8px;
}

.mini-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: sans-serif;
  text-transform: uppercase;
}

.mini-tag.category {
  background: #e6f7ff;
  color: #0050b3;
}

.mini-tag.author {
  background: #fff1f0;
  color: #cf1322;
}

.compact-desc {
  font-size: 13px;
  color: #888;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0;
}
</style>