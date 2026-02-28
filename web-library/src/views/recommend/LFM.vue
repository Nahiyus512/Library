<template>
  <div class="page-container dark-theme" :class="{ 'decode-active': decodeAnimating }">
    <div class="header">
      <h2 class="page-title glitch" data-text="MATRIX // 隐语书单">MATRIX // 隐语书单</h2>
      <p class="page-subtitle">Latent Factor Model：通过潜在偏好向量，挖掘你尚未明确表达但可能喜欢的书。</p>
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
              <path
                class="circle-bg"
                d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <path
                class="circle"
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
            <p class="matrix-author">{{ book.bookAuthor || '未知作者' }}</p>
            <p class="matrix-reason">{{ buildLatentReason(book, index) }}</p>
          </div>
          <div class="visual-bar"></div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请多给图书评分，让潜在因子模型更懂你。" />
    </div>

    <div class="insight-dock insight-dock-up" v-if="books.length > 0">
      <button class="reason-btn" @click="openReasonDialog">解码潜在因子</button>
    </div>

    <el-dialog v-model="showReasonDialog" width="760px" class="reason-dialog lfm-dialog" modal-class="lfm-dialog-overlay" :show-close="true">
      <template #header>
        <div class="dialog-title-wrap">
          <h3>LFM 推荐解释</h3>
          <p>通过潜在因子向量，捕捉你未显式表达的阅读偏好。</p>
        </div>
      </template>
      <div class="dialog-body">
        <p>LFM 不只看你“明确喜欢过什么”，还会从评分行为中学习隐藏偏好，例如节奏、世界观密度、写作风格和难度容忍度。</p>
        <p>当前推荐在潜在向量空间距离你最近，因此适合“想换一本，但仍保持熟悉感”的阅读场景。</p>
        <div class="factor-bars">
          <div class="factor" v-for="factor in factors" :key="factor.name">
            <span>{{ factor.name }}</span>
            <div class="bar"><i :style="{ width: factor.value + '%' }"></i></div>
            <em>{{ factor.value }}%</em>
          </div>
        </div>
      </div>
    </el-dialog>

    <BookDetailModal v-model="showDialog" :book="selectedBook" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import myAxios from '@/api/index'
import { useCookies } from '@vueuse/integrations/useCookies'
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
const decodeAnimating = ref(false)

const factors = ref([
  { name: '主题强度', value: 88 },
  { name: '叙事风格', value: 81 },
  { name: '阅读难度', value: 73 }
])

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
        strategy: 'lfm'
      }
    })
    books.value = res.data.data || []
  } catch (e) {
    console.log(e)
  }
}

const buildLatentReason = (book: BookItem, index: number) => {
  const classify = book.bookClassify ? `与「${book.bookClassify}」潜在偏好吻合` : '与潜在兴趣向量吻合'
  return `因子匹配排名第 ${index + 1}：${classify}，且在隐式反馈中得分较高。`
}

const openReasonDialog = () => {
  showReasonDialog.value = true
  decodeAnimating.value = true
  window.setTimeout(() => {
    decodeAnimating.value = false
  }, 3800)
}

const openDetail = (book: BookItem) => {
  selectedBook.value = book
  showDialog.value = true
}
</script>

<style scoped>
.page-container {
  padding: 24px 28px 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #0f0f0f;
  color: #00ff41;
  font-family: 'Courier New', Courier, monospace;
  display: grid;
  grid-template-rows: auto 1fr auto;
  gap: 10px;
}

.header {
  border-bottom: 1px solid #00ff41;
  padding-bottom: 14px;
  text-align: center;
}

.content-wrapper {
  min-height: 0;
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.page-subtitle {
  color: #008f11;
  font-size: 14px;
  margin: 0;
}

.matrix-layout {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 18px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.matrix-card {
  background: rgba(0, 255, 65, 0.05);
  border: 1px solid #003b00;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 18px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  width: 46%;
  min-width: 360px;
}

.matrix-card:hover {
  background: rgba(0, 255, 65, 0.12);
  border-color: #00ff41;
  box-shadow: 0 0 15px rgba(0, 255, 65, 0.3);
}

.decode-active .matrix-card {
  animation: neonPulse 1.2s ease-in-out infinite;
}

@keyframes neonPulse {
  0%, 100% { box-shadow: 0 0 0 rgba(0, 255, 65, 0); }
  50% { box-shadow: 0 0 18px rgba(0, 255, 65, 0.25); }
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
  font-size: 18px;
  margin: 0 0 6px;
  color: #fff;
  font-weight: 700;
}

.matrix-author {
  font-size: 13px;
  color: #00b936;
  margin: 0 0 6px;
}

.matrix-reason {
  margin: 0;
  color: #8cf5a8;
  font-size: 12px;
  line-height: 1.45;
}

.visual-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  background: #00ff41;
  width: 0;
  transition: width 0.3s;
}

.matrix-card:hover .visual-bar {
  width: 100%;
}

.insight-dock {
  width: min(920px, 100%);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.insight-dock-up {
  margin-top: -24px;
  margin-bottom: 24px;
}

.reason-btn {
  border: 1px solid #00ff41;
  background: #0b2011;
  color: #77ff99;
  border-radius: 999px;
  padding: 8px 18px;
  font-weight: 700;
  cursor: pointer;
}

.dialog-title-wrap h3 {
  margin: 0;
  color: #71ff95;
}

.dialog-title-wrap p {
  margin: 4px 0 0;
  color: #8edaa2;
  font-size: 12px;
}

.dialog-body p {
  margin: 0 0 10px;
  color: #a7e8b8;
  line-height: 1.7;
}

.factor-bars {
  margin-top: 8px;
  display: grid;
  gap: 8px;
}

.factor {
  display: grid;
  grid-template-columns: 70px 1fr 44px;
  gap: 8px;
  align-items: center;
  font-size: 12px;
}

.bar {
  background: rgba(0, 255, 65, 0.1);
  border-radius: 999px;
  height: 8px;
  overflow: hidden;
}

.bar i {
  display: block;
  height: 100%;
  background: linear-gradient(90deg, #00aa2f, #00ff41);
  animation: growBar 0.8s ease;
}

@keyframes growBar {
  from { width: 0; }
}

.factor em {
  font-style: normal;
  color: #9ceeb3;
}

:deep(.lfm-dialog .el-dialog) {
  background: #060d09;
  border: 1px solid #00ff41;
  box-shadow: 0 0 24px rgba(0, 255, 65, 0.22);
}

:deep(.lfm-dialog .el-dialog__header) {
  border-bottom: 1px solid rgba(0, 255, 65, 0.3);
  padding-bottom: 12px;
}

:deep(.lfm-dialog .el-dialog__title),
:deep(.lfm-dialog .el-dialog__header .dialog-title-wrap h3),
:deep(.lfm-dialog .el-dialog__header .dialog-title-wrap p),
:deep(.lfm-dialog .el-dialog__body),
:deep(.lfm-dialog .el-dialog__body p),
:deep(.lfm-dialog .factor span),
:deep(.lfm-dialog .factor em) {
  color: #7bff9f !important;
}

:deep(.lfm-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: #3ce977;
}

:deep(.lfm-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #9cffb6;
}

.matrix-reveal-enter-active,
.matrix-reveal-leave-active {
  transition: all 0.25s ease;
}

.matrix-reveal-enter-from,
.matrix-reveal-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

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

@media (max-width: 900px) {
  .matrix-card {
    width: 100%;
    min-width: 0;
  }
}
</style>

<style>
/* Element Plus dialog may be teleported to body; use global selectors to ensure skin applies */
.lfm-dialog.el-dialog,
.lfm-dialog .el-dialog {
  background: #060d09 !important;
  border: 1px solid #00ff41 !important;
  box-shadow: 0 0 24px rgba(0, 255, 65, 0.22) !important;
}

.lfm-dialog.el-dialog .el-dialog__header,
.lfm-dialog .el-dialog__header {
  border-bottom: 1px solid rgba(0, 255, 65, 0.3) !important;
}

.lfm-dialog.el-dialog .el-dialog__title,
.lfm-dialog .el-dialog__title,
.lfm-dialog.el-dialog .el-dialog__header .dialog-title-wrap h3,
.lfm-dialog .el-dialog__header .dialog-title-wrap h3,
.lfm-dialog.el-dialog .el-dialog__header .dialog-title-wrap p,
.lfm-dialog .el-dialog__header .dialog-title-wrap p,
.lfm-dialog.el-dialog .el-dialog__body,
.lfm-dialog .el-dialog__body,
.lfm-dialog.el-dialog .el-dialog__body p,
.lfm-dialog .el-dialog__body p,
.lfm-dialog.el-dialog .factor span,
.lfm-dialog .factor span,
.lfm-dialog.el-dialog .factor em,
.lfm-dialog .factor em {
  color: #7bff9f !important;
}

.lfm-dialog.el-dialog .el-dialog__headerbtn .el-dialog__close,
.lfm-dialog .el-dialog__headerbtn .el-dialog__close {
  color: #3ce977 !important;
}

.lfm-dialog.el-dialog .el-dialog__headerbtn:hover .el-dialog__close,
.lfm-dialog .el-dialog__headerbtn:hover .el-dialog__close {
  color: #9cffb6 !important;
}
</style>
