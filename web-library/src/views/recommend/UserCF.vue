<template>
  <div class="page-container" :class="{ 'pulse-active': pulseAnimating }">
    <div class="header">
      <h2 class="page-title">COMMUNITY // 用户协同</h2>
      <p class="page-subtitle">看看和你口味相近的读者最近在读什么。</p>
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
                :author="book.bookAuthor || '未知作者'"
                :id="book.bookId"
              />
            </div>
            <div class="info-area">
              <div class="match-badge">{{ formatMatch(book.matchPercent) }}</div>
              <h3 class="book-name">{{ book.bookName }}</h3>
              <p class="recommend-reason">{{ buildRecommendReason(book) }}</p>
              <div class="user-avatars">
                <div class="avatar" v-for="n in 3" :key="n" :style="{ backgroundColor: getRandomColor(index + n) }"></div>
                <span class="count">约 {{ book.readingCount || 0 }} 位相似读者在读</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请多给读过的书打分，让系统先找到和你口味接近的读者。" />
    </div>

    <div class="insight-dock" v-if="books.length > 0">
      <button class="reason-btn usercf-btn" @click="openReasonDialog">
        <span class="btn-dot"></span>
        <span>查看协同推荐依据</span>
      </button>
    </div>

    <el-dialog v-model="showReasonDialog" width="760px" class="reason-dialog usercf-dialog" :show-close="true">
      <template #header>
        <div class="dialog-title-wrap">
          <h3>UserCF 推荐解释</h3>
          <p>根据与你相似用户的评分行为，推断你可能喜欢的书。</p>
        </div>
      </template>
      <div class="dialog-body">
        <p>系统先计算与你在历史评分上的“相似用户集合”，再观察这批用户一致偏好的书，并结合匹配度和共读人数完成排序。</p>
        <p>所以这些书并不是随机热门，而是“与你相似的人已经验证有效”的候选，适合快速缩小选择范围。</p>
        <div class="community-graph">
          <div class="core-node">你</div>
          <div class="link-node" v-for="item in topCommunityBooks" :key="item.bookId">
            <span>{{ item.bookName }}</span>
            <em>{{ item.readingCount || 0 }} 人共读</em>
          </div>
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
  matchPercent?: number
  readingCount?: number
}

interface AgentRecommendItem {
  book?: {
    bookId?: number
  }
  score?: number
  hitStrategies?: string[]
}

interface BookLikeItem {
  bookId?: number
  likeLevel?: number
}

const cookie = useCookies()
const books = ref<BookItem[]>([])
const userName = reactive({
  userId: '',
  username: ''
})
const showDialog = ref(false)
const selectedBook = ref<BookItem | null>(null)
const matchPercentMap = ref<Record<number, number>>({})
const readingCountMap = ref<Record<number, number>>({})
const showReasonDialog = ref(false)
const pulseAnimating = ref(false)

const topCommunityBooks = computed(() =>
  [...books.value]
    .sort((a, b) => (b.readingCount || 0) - (a.readingCount || 0))
    .slice(0, 3)
)

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
    await Promise.all([loadMatchPercent(userId), loadReadingCount()])

    const res = await myAxios.get('http://localhost:8080/bookScore/recommend', {
      params: {
        userId,
        strategy: 'user_cf'
      }
    })

    const list = (res.data?.data || []) as BookItem[]
    books.value = list.map((book) => ({
      ...book,
      matchPercent: matchPercentMap.value[book.bookId],
      readingCount: readingCountMap.value[book.bookId] || 0
    }))
  } catch (e) {
    console.log(e)
  }
}

const loadMatchPercent = async (userId: any) => {
  try {
    const res = await myAxios.post('http://localhost:8080/agent/recommend', {
      userId: Number(userId),
      demand: 'goal_exploration;classify=all;hot=no;similar=yes;feedback=none',
      topN: 20
    })

    const rows = (res.data?.data?.recommendedBooks || []) as AgentRecommendItem[]
    const userCfRows = rows.filter((item) => (item.hitStrategies || []).includes('user_cf'))
    const sourceRows = userCfRows.length > 0 ? userCfRows : rows

    const map: Record<number, number> = {}
    sourceRows.forEach((item) => {
      const bookId = Number(item.book?.bookId || 0)
      if (!bookId) return
      map[bookId] = normalizeMatchPercent(Number(item.score || 0))
    })
    matchPercentMap.value = map
  } catch (e) {
    console.log(e)
    matchPercentMap.value = {}
  }
}

const loadReadingCount = async () => {
  try {
    const res = await myAxios.get('http://localhost:8080/bookLike/getAll')
    const rows = (res.data?.data || []) as BookLikeItem[]
    const map: Record<number, number> = {}

    rows.forEach((row) => {
      const bookId = Number(row.bookId || 0)
      if (!bookId) return
      if (Number(row.likeLevel) === 2) {
        map[bookId] = (map[bookId] || 0) + 1
      }
    })

    readingCountMap.value = map
  } catch (e) {
    console.log(e)
    readingCountMap.value = {}
  }
}

const normalizeMatchPercent = (score: number) => {
  return Math.max(0, Math.min(100, Math.round(score * 100)))
}

const formatMatch = (percent?: number) => {
  if (typeof percent !== 'number') return '匹配度计算中'
  return `${percent}% 匹配`
}

const buildRecommendReason = (book: BookItem) => {
  const percent = typeof book.matchPercent === 'number' ? `${book.matchPercent}%` : '较高'
  const readers = book.readingCount || 0
  return `你与相似读者的评分偏好重合度为 ${percent}，且已有 ${readers} 人明确喜欢这本书。`
}

const openReasonDialog = () => {
  showReasonDialog.value = true
  pulseAnimating.value = true
  window.setTimeout(() => {
    pulseAnimating.value = false
  }, 3600)
}

const openDetail = (book: BookItem) => {
  selectedBook.value = book
  showDialog.value = true
}

const getRandomColor = (seed: number) => {
  const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEEAD']
  return colors[seed % colors.length]
}
</script>

<style scoped>
.page-container {
  padding: 40px 40px 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff9f0;
  background-image: radial-gradient(#ffeaa7 20%, transparent 20%);
  background-size: 30px 30px;
  font-family: 'Varela Round', sans-serif;
}

.header {
  margin-bottom: 30px;
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
  gap: 24px;
  max-width: 100%;
  margin: 0 auto;
  overflow-x: auto;
  overflow-y: visible;
  padding: 24px 0 20px;
}

.user-card {
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(255, 159, 67, 0.15);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  height: 440px;
  width: 18%;
  min-width: 260px;
  flex-shrink: 0;
  border: 2px solid #fff;
  position: relative;
  z-index: 1;
}

.user-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(255, 159, 67, 0.25);
  border-color: #ff9f43;
  z-index: 5;
}

.pulse-active .user-card {
  animation: cardPulse 1.2s ease-in-out infinite;
}

@keyframes cardPulse {
  0% { box-shadow: 0 0 0 0 rgba(255, 159, 67, 0.35); }
  70% { box-shadow: 0 0 0 12px rgba(255, 159, 67, 0); }
  100% { box-shadow: 0 0 0 0 rgba(255, 159, 67, 0); }
}

.card-inner {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.cover-area {
  height: 56%;
  background: #fff5e6;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

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
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transform: rotate(-3deg);
  transition: transform 0.3s;
  z-index: 1;
}

.user-card:hover .cover-area :deep(.css-book-cover) {
  transform: rotate(0deg) scale(1.1);
}

.info-area {
  padding: 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
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
  margin: 0 0 8px 0;
  line-height: 1.3;
  color: #2d3436;
}

.recommend-reason {
  margin: 0 0 14px;
  font-size: 12px;
  line-height: 1.5;
  color: #5f6f7a;
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

.insight-dock {
  margin: 14px auto 0;
  width: min(900px, 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.reason-btn {
  border: none;
  border-radius: 999px;
  padding: 10px 22px;
  background: linear-gradient(135deg, #ff9f43, #ff7f50);
  color: #fff;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 10px 24px rgba(255, 127, 80, 0.28);
}

.reason-btn:hover {
  transform: translateY(-1px);
}

.usercf-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 22px;
  background: linear-gradient(135deg, #ff9f43, #ff7f50 55%, #ff6363);
  box-shadow: 0 12px 28px rgba(255, 127, 80, 0.34);
}

.btn-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 0 6px rgba(255, 255, 255, 0.2);
  animation: ping 1.5s ease-in-out infinite;
}

@keyframes ping {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.25); }
}

.dialog-title-wrap h3 {
  margin: 0;
  color: #da7a19;
  font-size: 24px;
  letter-spacing: 0.3px;
}

.dialog-title-wrap p {
  margin: 8px 0 0;
  font-size: 13px;
  color: #556474;
  line-height: 1.6;
}

.dialog-body {
  background: linear-gradient(180deg, #fffefb 0%, #fff7ea 100%);
  border: 1px solid #ffe2be;
  border-radius: 14px;
  padding: 16px;
}

.dialog-body p {
  margin: 0 0 10px;
  color: #526271;
  line-height: 1.7;
}

.community-graph {
  margin-top: 12px;
  border: 1px dashed #ffb56d;
  border-radius: 12px;
  background: #fffaf2;
  padding: 14px;
  display: grid;
  grid-template-columns: 72px 1fr;
  gap: 12px;
  align-items: start;
}

.core-node {
  align-self: start;
  justify-self: center;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #ff9f43;
  color: #fff;
  font-weight: 800;
}

.link-node {
  grid-column: 2;
  border: 1px solid #ffd5a6;
  border-radius: 10px;
  padding: 10px 12px;
  background: #fff;
  animation: riseIn 0.35s ease both;
}

.link-node + .link-node {
  margin-top: 0;
}

.link-node span {
  display: block;
  color: #3f4f5f;
  font-weight: 700;
}

.link-node em {
  display: block;
  font-style: normal;
  color: #8c5f33;
  font-size: 12px;
}

:deep(.usercf-dialog .el-dialog) {
  max-width: calc(100vw - 32px);
  border-radius: 16px;
  overflow: hidden;
}

:deep(.usercf-dialog .el-dialog__header) {
  padding: 18px 20px 10px;
  border-bottom: 1px solid #ffecd1;
  background: #fff9f0;
}

:deep(.usercf-dialog .el-dialog__body) {
  padding: 16px 20px 20px;
}

@keyframes riseIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
