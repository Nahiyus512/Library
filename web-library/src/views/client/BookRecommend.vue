<template>
  <div class="page-container">
    <div class="scroll-container">
      <div class="header-section">
        <h2 class="page-title">为您推荐</h2>
        <p class="page-subtitle">根据您的阅读历史和偏好精心挑选。</p>
      </div>

      <div class="books-grid">
        <div 
          class="book-card" 
          v-for="book in recommendBook" 
          :key="book.bookId" 
          @click="clickBook(book)"
        >
          <div class="book-cover-wrapper">
            <img 
              :src="'http://localhost:8080/common/download?name=' + book.bookImge" 
              alt="cover" 
              class="book-cover"
              loading="lazy"
            />
          </div>
          <div class="book-info-preview">
            <h3 class="book-title">{{ book.bookName }}</h3>
          </div>
        </div>
      </div>
      
      <div class="info-section">
        <p class="info-text">
          我们的智能推荐系统会分析您的阅读模式，发现更多好书。
          通过连接相似的用户兴趣和书籍特征，为您打开通往新知识的大门。
        </p>
      </div>
    </div>

    <!-- Book Details Dialog -->
    <el-dialog
      v-model="centerDialogVisible"
      width="600px"
      align-center
      class="book-dialog"
      :show-close="false"
    >
      <div class="dialog-content">
        <div class="dialog-cover">
          <img :src="'http://localhost:8080/common/download?name=' + bookData.bookImage" alt="cover">
        </div>
        
        <div class="dialog-details">
          <h2 class="detail-title">{{ bookData.bookName }}</h2>
          
          <div class="detail-meta">
            <div class="meta-item">
              <span class="label">出版社</span>
              <span class="value">{{ bookData.bookPublic }}</span>
            </div>
            <div class="meta-item">
              <span class="label">分类</span>
              <span class="value">{{ bookData.bookClassify }}</span>
            </div>
            <div class="meta-item">
              <span class="label">库存</span>
              <span class="value">{{ bookData.bookNum }}</span>
            </div>
          </div>

          <div class="rating-section">
            <span class="section-label">您的评分</span>
            <div class="stars" @click="rate($event)">
              <input
                v-for="n in 5"
                :key="n"
                type="radio"
                :id="'star' + n"
                :value="n"
                hidden
              />
              <label
                v-for="n in 5"
                :key="n"
                :for="'star' + n"
                :class="{ filled: n <= ratingData.rating }"
              >
                ★
              </label>
            </div>
          </div>

          <div class="action-buttons">
            <button class="action-btn outline" @click="inputScore">
              评分
            </button>
            <button class="action-btn primary" @click="borrow" :disabled="bookData.bookNum <= 0">
              {{ bookData.bookNum > 0 ? '借阅' : '暂无库存' }}
            </button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import myAxios from "@/api/index"
import { ref, reactive, onMounted } from "vue";
import { useCookies } from '@vueuse/integrations/useCookies'
import { ElMessage } from "element-plus";

const cookie = useCookies()

const recommendBook = ref([]);
const centerDialogVisible = ref(false)
const userName = reactive({
  userId: '',
  username: '',
})

const ratingData = reactive({
  rating: 0,
  userId: 0,
  bookId: 0,
  bookName: '',
  ratingTime: ''
})

const bookData = reactive({
  bookId: '',
  bookName: '',
  bookPrice: '',
  bookPublic: '',
  bookClassify: '',
  bookImage: '',
  bookDesc: '',
  bookNum: 0
})

onMounted(() => {
  userName.username = cookie.get('username')
  getUserByName()
})

const clickBook = (book: any) => {
  bookData.bookId = book.bookId
  bookData.bookImage = book.bookImge
  bookData.bookName = book.bookName
  bookData.bookPublic = book.bookPublic
  bookData.bookClassify = book.bookClassify
  bookData.bookNum = book.bookNum
  centerDialogVisible.value = true
  ratingData.bookId = book.bookId
  ratingData.bookName = book.bookName
  findRating()
}

const getUserByName = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName', {
      params: {
        name: userName.username
      }
    })
    userName.userId = res.data.data.id
    ratingData.userId = res.data.data.id
    await getRecommendBook(userName.userId)
  } catch (e) {
    console.log(e)
  }
}

const getRecommendBook = async (userId: any) => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookScore/recommend', {
      params: {
        userId: userId
      }
    })
    recommendBook.value = res.data.data
  } catch (e) {
    console.log(e)
  }
}

const inputScore = async () => {
  try {
    let rs = await myAxios.put('http://localhost:8080/bookScore/updateScore', {
      userId: ratingData.userId,
      bookId: ratingData.bookId,
      score: ratingData.rating
    })
    ElMessage.success(rs.data.data)
  } catch (e) {
    console.log(e)
  }
}

const rate = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for')!.slice(-1), 10);
    ratingData.rating = starIndex;
  }
};

const borrow = async () => {
  if (bookData.bookNum > 0) {
    try {
      let res = await myAxios.put('http://localhost:8080/bookBorrow/borrow', {
        userName: userName.username,
        bookId: bookData.bookId,
        bookName: bookData.bookName,
        borrowTime: '5',
      })
      ElMessage.success(res.data.data)
      centerDialogVisible.value = false

    } catch (e) {
      console.log(e)
    }
  } else {
    ElMessage.error("借书失败")
  }
}

const findRating = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookScore/findScore', {
      params: {
        userId: ratingData.userId,
        bookId: ratingData.bookId
      }
    });
    ratingData.rating = res.data.data
  } catch (e) {
    console.log(e)
  }
}
</script>

<style scoped>
.page-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
}

.scroll-container::-webkit-scrollbar {
  width: 6px;
}
.scroll-container::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}
.scroll-container::-webkit-scrollbar-track {
  background: transparent;
}

.header-section {
  margin-bottom: 40px;
  text-align: left;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 300;
  margin: 0 0 10px 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  color: #888;
  font-size: 14px;
  margin: 0;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 80px;
  padding: 0 20px;
}

.book-card {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.book-card:hover {
  transform: translateY(-8px);
}

.book-cover-wrapper {
  width: 100%;
  aspect-ratio: 2/3;
  background: #f0f0f0;
  overflow: hidden;
  margin-bottom: 15px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  transition: box-shadow 0.3s ease;
}

.book-card:hover .book-cover-wrapper {
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.book-card:hover .book-cover {
  transform: scale(1.05);
}

.book-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-section {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
  color: #999;
  font-size: 13px;
  line-height: 1.6;
}

/* Dialog Styles */
.dialog-content {
  display: flex;
  gap: 30px;
}

.dialog-cover {
  width: 180px;
  flex-shrink: 0;
}

.dialog-cover img {
  width: 100%;
  border-radius: 4px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.dialog-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 20px 0;
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.meta-item .label {
  color: #999;
}

.meta-item .value {
  font-weight: 500;
}

.rating-section {
  margin-bottom: 30px;
}

.section-label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stars {
  display: flex;
  gap: 5px;
  cursor: pointer;
}

.stars label {
  font-size: 24px;
  color: #eee;
  cursor: pointer;
  transition: color 0.2s;
}

.stars label:hover,
.stars label:hover ~ label {
  color: #ddd;
}

.stars label.filled {
  color: #000;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: auto;
}

.action-btn {
  flex: 1;
  padding: 12px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn.outline {
  background: transparent;
  border: 1px solid #ddd;
  color: #333;
}

.action-btn.outline:hover {
  border-color: #000;
}

.action-btn.primary {
  background: #000;
  color: #fff;
}

.action-btn.primary:hover {
  background: #333;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
