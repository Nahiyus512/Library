<template>
  <div class="page-container">
    <div class="scroll-container">
      <div class="books-grid">
        <div 
          class="book-card" 
          v-for="book in tableData" 
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
    </div>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        layout="prev, pager, next"
        :total="pageInfo.allNum"
        @current-change="handleCurrentChange"
        background
        class="minimal-pagination"
      />
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
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import myAxios from "../axios/index.js";
import { useCookies } from '@vueuse/integrations/useCookies'
import { useRoute } from 'vue-router'

const cookie = useCookies()
const route = useRoute()

const ratingData = reactive({
  rating: 0,
  userId: 0,
  bookId: 0,
  bookName: '',
  ratingTime: ''
})

const centerDialogVisible = ref(false)

const pageInfo = reactive({
  pageSize: 12,
  pageNum: 1,
  allNum: 20,
  bookName: ''
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

const borrowData = reactive({
  userName: '',
  bookId: '',
  bookName: '',
  borrowTime: '',
  beginTime: '',
  endTime: ''
})

const tableData = ref([])

onMounted(() => {
  if (route.query.q) {
    pageInfo.bookName = route.query.q as string
  }
  getBook()
  let username = cookie.get('username')
  borrowData.userName = username
  getUserByName(username)
})

const getTime = new Date().getTime();
const lastTime = new Date(getTime + 432000000);
const time = new Date(getTime);

function nowDate(time: Date) {
  const year = time.getFullYear();
  const month = (time.getMonth() + 1).toString().padStart(2, '0');
  const date = time.getDate().toString().padStart(2, '0');
  const hour = time.getHours().toString().padStart(2, '0');
  const minute = time.getMinutes().toString().padStart(2, '0');
  const second = time.getSeconds().toString().padStart(2, '0');
  return (
    year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second
  )
}

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

const handleCurrentChange = (val: number) => {
  getBook()
}

const getUserByName = async (username: string) => {
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName', {
      params: { name: username }
    })
    ratingData.userId = res.data.data.id
  } catch (e) {
    console.log(e)
  }
}

const getBook = async () => {
  try {
    let res = await myAxios.post('http://localhost:8080/book/listPage', {
      pageSize: pageInfo.pageSize,
      pageNum: pageInfo.pageNum,
      nameInfo: pageInfo.bookName
    })
    tableData.value = JSON.parse(JSON.stringify(res.data.data.bookList))
    pageInfo.allNum = JSON.parse(JSON.stringify(res.data.data.count))
  } catch (e) {
    console.log(e)
  }
}

const borrow = async () => {
  if (bookData.bookNum > 0) {
    try {
      let res = await myAxios.put('http://localhost:8080/bookBorrow/borrow', {
        userName: borrowData.userName,
        bookId: bookData.bookId,
        bookName: bookData.bookName,
        borrowTime: '5',
        beginTime: nowDate(time),
        endTime: nowDate(lastTime)
      })
      ElMessage.success(res.data.data)
      centerDialogVisible.value = false
      await getBook()
    } catch (e) {
      console.log(e)
    }
  } else {
    ElMessage.error("借书失败")
  }
}

const rate = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for')!.slice(-1), 10);
    ratingData.rating = starIndex;
  }
};

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
  overflow: hidden;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.books-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  overflow: hidden;
}

.book-card {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.3s ease;
  height: 100%;
  min-height: 0;
  justify-content: center;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover-wrapper {
  flex: 1;
  width: 100%;
  background: transparent;
  overflow: hidden;
  margin-bottom: 10px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  transition: box-shadow 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-card:hover .book-cover-wrapper {
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.book-cover {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
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
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px;
  flex-shrink: 0;
  border-top: 1px solid rgba(0,0,0,0.05);
  background-color: #f9f9f9; /* Match app background */
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

/* Override Element Plus Pagination */
:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #000;
  color: #fff;
}
</style>
