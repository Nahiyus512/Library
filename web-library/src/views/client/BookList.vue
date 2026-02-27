<template>
  <div class="page-container">
    <div class="top-bar">
      <div class="search-wrapper">
        <el-input
          v-model="pageInfo.bookName"
          placeholder="搜索书名..."
          class="search-input"
          @keyup.enter="handleSearch"
          clearable
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button color="#000" class="search-btn" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <div class="scroll-container">
      <div class="books-grid">
        <div 
          class="book-card" 
          v-for="book in tableData" 
          :key="book.bookId" 
          @click="clickBook(book)"
          @mousemove="handleMouseMove"
          @mouseleave="handleMouseLeave"
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
        class="minimal-pagination"
      />
    </div>

    <!-- Book Details Dialog -->
    <BookDetailModal v-model="centerDialogVisible" :book="bookData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search } from '@element-plus/icons-vue'
import myAxios from "@/api/index";
import { useCookies } from '@vueuse/integrations/useCookies'
import { useRoute } from 'vue-router'
import BookDetailModal from '@/components/business/BookDetailModal.vue'

const cookie = useCookies()
const route = useRoute()

const likeLevel = ref(-1)

const ratingData = reactive({
  rating: 0,
  userId: 0,
  bookId: 0,
  bookName: '',
  ratingTime: ''
})

const centerDialogVisible = ref(false)

const pageInfo = reactive({
  pageSize: 24,
  pageNum: 1,
  allNum: 20,
  bookName: ''
})

const handleSearch = () => {
  pageInfo.pageNum = 1
  getBook()
}

const handleMouseMove = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const wrapper = card.querySelector('.book-cover-wrapper') as HTMLElement
  
  if (!wrapper) return

  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  // Calculate percentage from center (-1 to 1)
  const xPct = (x / rect.width - 0.5) * 2
  const yPct = (y / rect.height - 0.5) * 2
  
  // Max rotation angles
  const maxRotateX = 15
  const maxRotateY = 15
  
  const rotateX = -yPct * maxRotateX
  const rotateY = xPct * maxRotateY
  
  wrapper.style.transition = 'transform 0.1s ease-out'
  wrapper.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.08, 1.08, 1.08)`
}

const handleMouseLeave = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const wrapper = card.querySelector('.book-cover-wrapper') as HTMLElement
  
  if (wrapper) {
    wrapper.style.transition = 'transform 0.5s ease'
    wrapper.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) scale3d(1, 1, 1)'
  }
}

const bookData = reactive({
  bookId: '',
  bookName: '',
  bookPublic: '',
  bookClassify: '',
  bookImage: '',
  bookImge: '',
  bookDesc: '',
})

const tableData = ref([])

onMounted(async () => {
  if (route.query.q) {
    pageInfo.bookName = route.query.q as string
  }
  getBook()
  let username = cookie.get('username')
  
  const userId = cookie.get('userId')
  if (userId) {
    ratingData.userId = userId
  } else if (username) {
    await getUserByName(username)
  }
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
  bookData.bookAuthor = book.bookAuthor || 'Unknown' // Pass author
  centerDialogVisible.value = true

  // No need to call findRating/getLikeStatus here as component handles it
}

// Removed duplicate logic that is now in BookDetailModal
// kept required data loading logic for the page


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
    ElMessage.error("鍊熶功澶辫触")
  }
}

const rate = async (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for')!.slice(-1), 10);
    // If clicking same star, could optionally clear it, but usually just updates.
    // Here we just update.
    ratingData.rating = starIndex;
    
    // Call inputScore directly
    await inputScore();
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

.top-bar {
  padding: 20px 20px 8px;
  display: flex;
  justify-content: center;
}

.search-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
  max-width: 560px;
}

.search-input {
  flex: 1;
  --el-input-height: 32px;
}

.search-input :deep(.el-input__wrapper) {
  min-height: 32px !important;
  padding: 0 12px !important;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: box-shadow 0.2s ease;
}

.search-input :deep(.el-input__inner) {
  height: 32px;
  line-height: 32px;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

.search-btn {
  padding: 6px 18px;
  border-radius: 10px;
  font-weight: 600;
  background: #000;
  border-color: #000;
  color: #fff;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.search-btn:hover,
.search-btn:focus-visible {
  background: #fff;
  border-color: #000;
  color: #000;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.search-btn:active {
  transform: translateY(0);
}

.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.books-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 20px;
  padding-bottom: 20px;
}

.book-card {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.3s ease;
  height: 100%;
  min-height: 0;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover-wrapper {
  width: 100%;
  aspect-ratio: 2/3;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  transition: box-shadow 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  transform-style: preserve-3d;
  will-change: transform;
}

.book-card:hover .book-cover-wrapper {
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
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
  background: transparent;
}

.minimal-pagination :deep(.btn-prev),
.minimal-pagination :deep(.btn-next),
.minimal-pagination :deep(.el-pager li) {
  background: transparent !important;
  color: #000 !important;
  border: none !important;
  box-shadow: none !important;
}

.minimal-pagination :deep(.el-pager li.is-active) {
  color: #000 !important;
  font-weight: 700;
}

.minimal-pagination :deep(.el-pager li:hover),
.minimal-pagination :deep(.btn-prev:hover),
.minimal-pagination :deep(.btn-next:hover) {
  color: #000 !important;
}

.minimal-pagination :deep(.btn-prev:disabled),
.minimal-pagination :deep(.btn-next:disabled) {
  color: #909399 !important;
}

/* Dialog Styles Removed as they are now in the component */
</style>


