<template>
  <div class="bookshelf-page">
    <div class="header-section">
      <h1 class="page-title">我的书架</h1>
      <p class="page-subtitle">收藏您喜爱的图书，拖动可移动位置或移除</p>
    </div>

    <div class="shelves-layout">
      <!-- Left Side: Liked Books (2 rows) -->
      <div class="shelf-column left-column">
        <h2 class="column-title">想看</h2>
        <div class="shelf-row" v-for="(books, rowIndex) in shelfState.liked" :key="'liked-'+rowIndex">
          <BookShelfItem 
            :books="books"
            @dragstart="dragStart"
            @click-book="clickBook"
            @drop-on-shelf="(e) => dropOnShelf(e, 'liked', rowIndex)"
          />
        </div>
      </div>

      <!-- Center: Trash Can -->
      <div class="center-column">
        <div 
          class="trash-container"
          :class="{ 'drag-over': isDragOver }"
          @dragover.prevent
          @dragenter.prevent="isDragOver = true"
          @dragleave.prevent="isDragOver = false"
          @drop="dropToTrash"
        >
          <div class="trash-icon">🗑️</div>
          <div class="trash-text">移除</div>
        </div>
      </div>

      <!-- Right Side: So-so Books (2 rows) -->
      <div class="shelf-column right-column">
        <h2 class="column-title">待看</h2>
        <div class="shelf-row" v-for="(books, rowIndex) in shelfState.soso" :key="'soso-'+rowIndex">
          <BookShelfItem 
            :books="books"
            @dragstart="dragStart"
            @click-book="clickBook"
            @drop-on-shelf="(e) => dropOnShelf(e, 'soso', rowIndex)"
          />
        </div>
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
      <div class="dialog-content-wrapper">
        <div class="dialog-top-section">
          <div class="dialog-cover">
            <img :src="'http://localhost:8080/common/download?name=' + bookData.bookImage" alt="cover">
          </div>
          
          <div class="dialog-details">
            <h2 class="detail-title">{{ bookData.bookName }}</h2>
            
            <div class="detail-meta">
              <div class="meta-item">
                <span class="label">作者</span>
                <span class="value">{{ bookData.bookAuthor }}</span>
              </div>
            </div>

            <div class="rating-action-row">
              <div class="rating-group">
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
            </div>
          </div>
        </div>
        <BookComments :bookId="bookData.bookId" v-if="centerDialogVisible" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue'
import myAxios from "@/api/index"
import { useCookies } from '@vueuse/integrations/useCookies'
import { ElMessage } from "element-plus"
import BookShelfItem from '@/components/business/BookShelfItem.vue'
import BookComments from '@/components/business/BookComments.vue'

const cookie = useCookies()
const allBooks = ref<any[]>([])
const isDragOver = ref(false)

// Manual Shelf State Management
const shelfState = reactive({
  liked: [[], []] as any[][], // 2 rows
  soso: [[], []] as any[][]   // 2 rows
})

// Dialog State
const centerDialogVisible = ref(false)
const bookData = reactive({
  bookId: '',
  bookName: '',
  bookImage: '',
  bookAuthor: '',
})
const ratingData = reactive({
  rating: 0,
  userId: 0,
  bookId: 0,
})

const likedBooks = computed(() => allBooks.value.filter(b => b.likeLevel === 2))
const sosoBooks = computed(() => allBooks.value.filter(b => b.likeLevel === 1))

// Distribute books randomly between 2 rows based on bookId hash
const distributeBooksRandomly = (books: any[], rowCount: number) => {
  const rows = Array.from({ length: rowCount }, () => [] as any[]);
  books.forEach(book => {
    // Simple deterministic hash based on bookId
    const idStr = String(book.bookId || book.bookName || '');
    const hash = idStr.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    
    // Assign to a row based on hash
    const rowIndex = hash % rowCount;
    rows[rowIndex].push(book);
  });
  return rows;
};

// Initialize shelf state from fetched books
const initializeShelves = () => {
  const likedRows = distributeBooksRandomly(likedBooks.value, 2);
  shelfState.liked[0] = likedRows[0];
  shelfState.liked[1] = likedRows[1];

  const sosoRows = distributeBooksRandomly(sosoBooks.value, 2);
  shelfState.soso[0] = sosoRows[0];
  shelfState.soso[1] = sosoRows[1];
};

onMounted(() => {
  fetchBookshelf()
  const username = cookie.get('username')
  if (username) getUserByName(username)
})

const fetchBookshelf = async () => {
  const username = cookie.get('username')
  if (!username) return
  
  try {
    const res = await myAxios.get('http://localhost:8080/bookLike/list', {
      params: { userName: username }
    })
    if (res.data.code === 200) {
      allBooks.value = res.data.data
      initializeShelves(); // Populate shelves after fetch
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取书架数据失败')
  }
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

const dropOnShelf = async (event: DragEvent, targetSection: 'liked' | 'soso', targetRowIndex: number) => {
  const bookId = event.dataTransfer?.getData('bookId');
  if (!bookId) return;

  // 1. Find source book and location
  let sourceBook: any = null;
  let sourceSection: 'liked' | 'soso' | null = null;
  let sourceRowIndex = -1;
  let sourceIndex = -1;

  // Search in liked
  for (let r = 0; r < shelfState.liked.length; r++) {
    const idx = shelfState.liked[r].findIndex(b => b.bookId == bookId);
    if (idx !== -1) {
      sourceBook = shelfState.liked[r][idx];
      sourceSection = 'liked';
      sourceRowIndex = r;
      sourceIndex = idx;
      break;
    }
  }

  // Search in soso if not found
  if (!sourceBook) {
    for (let r = 0; r < shelfState.soso.length; r++) {
      const idx = shelfState.soso[r].findIndex(b => b.bookId == bookId);
      if (idx !== -1) {
        sourceBook = shelfState.soso[r][idx];
        sourceSection = 'soso';
        sourceRowIndex = r;
        sourceIndex = idx;
        break;
      }
    }
  }

  if (!sourceBook) return;

  // 2. Optimistic Update (Move locally)
  // Remove from source
  if (sourceSection === 'liked') {
    shelfState.liked[sourceRowIndex].splice(sourceIndex, 1);
  } else {
    shelfState.soso[sourceRowIndex].splice(sourceIndex, 1);
  }

  // Add to target
  if (targetSection === 'liked') {
    shelfState.liked[targetRowIndex].push(sourceBook);
    sourceBook.likeLevel = 2; // Update local state
  } else {
    shelfState.soso[targetRowIndex].push(sourceBook);
    sourceBook.likeLevel = 1; // Update local state
  }

  // 3. Backend Update (if section changed)
  // Even if section didn't change (just moved rows), we might want to persist? 
  // Current backend only supports likeLevel, so row position is transient unless we save it.
  // The user requirement implies moving between sections updates the status.
  
  if (sourceSection !== targetSection) {
    try {
      const username = cookie.get('username');
      const targetLikeLevel = targetSection === 'liked' ? 2 : 1;
      
      await myAxios.put('http://localhost:8080/bookLike/like', {
        userName: username,
        bookId: sourceBook.bookId,
        bookName: sourceBook.bookName,
        likeLevel: targetLikeLevel
      });
      ElMessage.success('已更新整理');
    } catch (e) {
      console.error(e);
      ElMessage.error('更新失败');
      // Revert logic could be added here if critical
    }
  }
};

const dragStart = (event: DragEvent, book: any) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('bookId', book.bookId)
    event.dataTransfer.effectAllowed = 'move'
  }
}

const dropToTrash = async (event: DragEvent) => {
  isDragOver.value = false
  const bookId = event.dataTransfer?.getData('bookId')
  if (!bookId) return

  const book = allBooks.value.find(b => b.bookId == bookId)
  if (!book) return

  try {
    const username = cookie.get('username')
    const res = await myAxios.put('http://localhost:8080/bookLike/like', {
      userName: username,
      bookId: book.bookId,
      bookName: book.bookName,
      likeLevel: 0
    })
    
    if (res.data.code === 200) {
      ElMessage.success(`已移除《${book.bookName}》`)
      fetchBookshelf()
    } else {
      ElMessage.error('移除失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('操作失败')
  }
}

const clickBook = (book: any) => {
  bookData.bookId = book.bookId
  bookData.bookName = book.bookName
  bookData.bookImage = book.bookImge
  bookData.bookAuthor = book.bookAuthor
  
  ratingData.bookId = book.bookId
  findRating()
  
  centerDialogVisible.value = true
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

const rate = async (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for')!.slice(-1), 10);
    ratingData.rating = starIndex;
    await inputScore();
  }
};

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
.bookshelf-page {
  padding: 30px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f9f9f9;
  box-sizing: border-box;
  overflow: hidden; /* No scrollbar */
}

.header-section {
  margin-bottom: 20px; /* Reduced margin */
  text-align: center;
  flex-shrink: 0;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin: 0 0 5px 0;
  font-family: 'Times New Roman', serif;
}

.page-subtitle {
  color: #7f8c8d;
  font-size: 16px;
}

.shelves-layout {
  flex: 1;
  display: flex;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.shelf-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 30px; /* Space between shelves vertically */
}

.column-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 10px 0;
  text-align: center;
  font-family: 'Times New Roman', serif;
}

.left-column {
  /* 3 rows */
}

.right-column {
  /* 2 rows */
}

.center-column {
  width: 120px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end; /* Trash at bottom */
  padding-bottom: 20px;
}

.shelf-row {
  height: 220px;
  width: 100%;
}

.trash-container {
  height: 120px;
  border: 2px dashed #bdc3c7;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #95a5a6;
  transition: all 0.3s;
  background: rgba(0,0,0,0.02);
}

.trash-container.drag-over {
  border-color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  transform: scale(1.05);
}

.trash-icon {
  font-size: 32px;
  margin-bottom: 5px;
}

.trash-text {
  font-size: 12px;
  font-weight: 500;
}

/* Dialog Styles */
.dialog-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dialog-top-section {
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

.rating-action-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: auto;
}

.rating-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.section-label {
  display: block;
  font-size: 12px;
  color: #999;
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
</style>