<template>
  <div class="page-container" :class="{ 'evidence-active': evidenceAnimating }">
    <div class="header">
      <h2 class="page-title">TAGS // 内容推荐</h2>
      <p class="page-subtitle">根据你已收藏图书的作者、分类与主题标签，筛选最贴近兴趣画像的候选书。</p>
    </div>

    <div class="content-wrapper">
      <div class="compact-grid-layout" v-if="books.length > 0">
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
              :author="book.bookAuthor || '未知作者'"
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
            <p class="compact-desc">{{ formatDesc(book.bookDescription) }}</p>
            <p class="reason-text">{{ buildContentReason(book) }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请将喜欢的书加入书架，我们正在学习你的口味。" />
    </div>

    <div class="insight-dock" v-if="books.length > 0">
      <button class="reason-btn" @click="openReasonDialog">展开标签证据</button>
    </div>

    <el-dialog v-model="showReasonDialog" width="740px" class="reason-dialog content-dialog" :show-close="true">
      <template #header>
        <div class="dialog-title-wrap">
          <h3>内容推荐解释</h3>
          <p>基于标签重合度与特征相似度进行排序。</p>
        </div>
      </template>
      <div class="dialog-body">
        <p>系统会抽取你历史偏好中的高频标签（分类、作者、主题关键词），再计算每本候选书与这些标签的重合程度。</p>
        <p>标签重合越多、重要标签命中越高，这本书在结果中的优先级就越靠前。</p>
        <div class="tag-cloud">
          <span v-for="tag in evidenceTags" :key="tag">{{ tag }}</span>
        </div>
      </div>
    </el-dialog>

    <BookDetailModal v-model="showDialog" :book="selectedBook" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import myAxios from '@/api/index'
import { useCookies } from '@vueuse/integrations/useCookies'
import CssBookCover from '@/components/common/CssBookCover.vue'
import BookDetailModal from '@/components/business/BookDetailModal.vue'

interface BookItem {
  bookId: number
  bookName: string
  bookAuthor?: string
  bookDescription?: string
  bookClassify?: string
}

const cookie = useCookies()
const books = ref<BookItem[]>([])
const userName = reactive({
  userId: '',
  username: ''
})
const showDialog = ref(false)
const selectedBook = ref<BookItem | null>(null)
const showReasonDialog = ref(false)
const evidenceAnimating = ref(false)

const evidenceTags = computed(() => {
  const rawTags = books.value.flatMap((book) => [book.bookClassify, book.bookAuthor]).filter((x): x is string => !!x)
  return Array.from(new Set(rawTags)).slice(0, 8)
})

onMounted(() => {
  userName.username = cookie.get('username')
  getUserByName()
})

const getUserByName = async () => {
  try {
    const res = await myAxios.get('http://localhost:8080/user/getUserByName', {
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
    const res = await myAxios.get('http://localhost:8080/bookScore/recommend', {
      params: {
        userId,
        strategy: 'content_based'
      }
    })
    books.value = res.data.data || []
  } catch (e) {
    console.log(e)
  }
}

const formatDesc = (desc?: string) => {
  if (!desc) return '暂无简介'
  if (desc.length <= 68) return desc
  return `${desc.slice(0, 68)}...`
}

const buildContentReason = (book: BookItem) => {
  const classifyText = book.bookClassify ? `命中你常读的「${book.bookClassify}」分类` : '命中你的高频主题标签'
  const authorText = book.bookAuthor ? `，并与 ${book.bookAuthor} 相关作品偏好接近` : ''
  return `${classifyText}${authorText}，所以该书被提升到前列。`
}

const openReasonDialog = () => {
  showReasonDialog.value = true
  evidenceAnimating.value = true
  window.setTimeout(() => {
    evidenceAnimating.value = false
  }, 3600)
}

const openDetail = (book: BookItem) => {
  selectedBook.value = book
  showDialog.value = true
}
</script>

<style scoped>
.page-container {
  padding: 34px 34px 22px;
  height: 100%;
  overflow-y: auto;
  background-color: #f5f5f7;
  font-family: 'Georgia', serif;
}

.header {
  margin-bottom: 20px;
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
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 10px;
}

.compact-card {
  background: #fff;
  border-radius: 2px;
  display: flex;
  min-height: 172px;
  overflow: visible;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 1px 1px 0 rgba(0, 0, 0, 0.1);
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
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.compact-card:hover::before {
  width: 8px;
}

.evidence-active .compact-card {
  box-shadow: 0 0 0 1px #ff6b78, 0 6px 18px rgba(255, 71, 87, 0.2);
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

.card-left :deep(.css-book-cover) {
  width: 90%;
  height: 90%;
  border-radius: 2px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.card-right {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
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

.reason-text {
  margin: 0;
  font-size: 12px;
  line-height: 1.45;
  color: #4b5967;
}

.insight-dock {
  margin: 18px auto 0;
  width: min(900px, 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.reason-btn {
  border: 1px solid #d93c4b;
  background: #ff4757;
  color: #fff;
  font-weight: 700;
  border-radius: 999px;
  padding: 9px 18px;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(255, 71, 87, 0.25);
}

.dialog-title-wrap h3 {
  margin: 0;
  color: #d93c4b;
}

.dialog-title-wrap p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #6a7784;
}

.dialog-body p {
  margin: 0 0 10px;
  line-height: 1.7;
  color: #4f5d6a;
}

.tag-cloud {
  margin-top: 10px;
  border: 1px dashed #ffd1d6;
  border-radius: 10px;
  background: #fff8f8;
  padding: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-cloud span {
  background: #ffe7ea;
  color: #b4303d;
  border-radius: 999px;
  font-size: 11px;
  padding: 4px 10px;
  animation: fadeInUp 0.3s ease both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.24s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 900px) {
  .compact-grid-layout {
    grid-template-columns: 1fr;
  }

  .centered-card {
    width: 100%;
    grid-column: auto;
  }
}
</style>
