<template>
  <div class="page-container" :class="{ 'trail-active': trailAnimating }">
    <div class="header-wrap">
      <div class="header-content">
        <p class="kicker">基于物品的协同过滤</p>
        <h2 class="page-title">物品协同相似书推荐</h2>
        <p class="page-subtitle">从你已高分的书出发，优先推荐在读者评分行为上更接近的候选书。</p>
      </div>
    </div>

    <div class="content-wrapper">
      <div class="list-grid" v-if="books.length > 0">
        <article
          v-for="(book, index) in books"
          :key="book.bookId"
          class="book-card"
          :class="{ 'centered-card': index === 4 }"
          @click="openDetail(book)"
        >
          <div class="rank-pill">第 {{ index + 1 }} 名</div>

          <div class="cover-box">
            <CssBookCover
              :title="book.bookName"
              :author="book.bookAuthor || '未知作者'"
              :id="book.bookId"
            />
          </div>

          <div class="info-box">
            <span class="reason-chip">与已高分书共现较高</span>
            <h3 class="book-title">{{ book.bookName }}</h3>

            <div class="meta-row">
              <span class="meta-tag" v-if="book.bookAuthor">{{ book.bookAuthor }}</span>
              <span class="meta-tag classify" v-if="book.bookClassify">{{ book.bookClassify }}</span>
            </div>

            <p class="book-desc">{{ formatDesc(book.bookDescription) }}</p>
            <p class="book-reason">{{ buildSimilarityReason(book, index) }}</p>

            <div class="cta-row">
              <span>点击查看详情</span>
              <span class="arrow">→</span>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else description="暂无推荐。请补充评分数据后再试。" />
    </div>

    <div class="insight-dock insight-dock-up" v-if="books.length > 0">
      <button class="reason-btn" @click="openReasonDialog">查看物品协同推荐依据</button>
    </div>

    <el-dialog v-model="showReasonDialog" width="720px" class="reason-dialog itemcf-dialog" :show-close="true">
      <template #header>
        <div class="dialog-title-wrap">
          <h3>物品协同推荐原理说明</h3>
          <p>从你明确喜欢的书出发，找出被同类读者反复共同喜欢的候选书。</p>
        </div>
      </template>
      <div class="dialog-body">
        <p>系统先提取你的高分书作为“种子”，再统计哪些书与这些种子在同一批用户中经常被同时高分。共同高分越稳定，相似度越高。</p>
        <p>最终排序主要看三件事：共现强度、覆盖了多少本种子书、以及结果多样性。它反映的是“行为相似”，不是对内容质量的绝对判断。</p>
        <div class="diagram-track">
          <div class="seed-node">你的高分书</div>
          <div class="flow-line"></div>
          <div class="candidate-node">
            <span v-for="item in highlightedSignals.slice(0, 4)" :key="item">{{ item }}</span>
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
const trailAnimating = ref(false)

const highlightedSignals = computed(() => [
  '共现强度',
  '种子覆盖度',
  '相似读者权重',
  '多样性校正'
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
        strategy: 'item_cf'
      }
    })
    books.value = res.data.data || []
  } catch (e) {
    console.log(e)
  }
}

const formatDesc = (desc?: string) => {
  if (!desc) return '暂无简介'
  if (desc.length <= 86) return desc
  return `${desc.slice(0, 86)}...`
}

const buildSimilarityReason = (book: BookItem, index: number) => {
  const rank = index + 1
  const classifyHint = book.bookClassify ? `主题：${book.bookClassify}。` : '主题信息待补充。'
  const authorHint = book.bookAuthor ? `作者：${book.bookAuthor}。` : ''
  return `排序第 ${rank} 位。该书与您的高分书在读者评分行为上共现更稳定。${classifyHint}${authorHint}`
}

const openReasonDialog = () => {
  showReasonDialog.value = true
  trailAnimating.value = true
  window.setTimeout(() => {
    trailAnimating.value = false
  }, 3600)
}

const openDetail = (book: BookItem) => {
  selectedBook.value = book
  showDialog.value = true
}
</script>

<style scoped>
.page-container {
  --bg-main: #f5f0e6;
  --bg-soft: #fffdfa;
  --ink-main: #1f2a37;
  --ink-sub: #6f7a86;
  --accent: #cc7a36;
  --accent-soft: #f4d7b8;
  --dock-lift: 40px;

  height: 100%;
  overflow: hidden;
  padding: 14px 16px 12px;
  background:
    radial-gradient(1200px 520px at 92% -15%, #edd2b3 0%, transparent 65%),
    radial-gradient(800px 400px at -10% 10%, #dce7f1 0%, transparent 62%),
    var(--bg-main);
  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;
  gap: 14px;
}

.header-content {
  border: 1px solid rgba(24, 33, 46, 0.08);
  background: linear-gradient(145deg, rgba(255, 253, 250, 0.98), rgba(255, 247, 238, 0.96));
  border-radius: 14px;
  padding: 10px 14px;
  box-shadow: 0 10px 24px rgba(41, 35, 28, 0.08);
  text-align: center;
}

.kicker {
  margin: 0;
  color: var(--accent);
  letter-spacing: 1.4px;
  font-size: 11px;
  font-weight: 700;
}

.page-title {
  margin: 4px 0;
  color: var(--ink-main);
  font-size: 24px;
  line-height: 1.2;
  font-weight: 800;
}

.page-subtitle {
  margin: 0;
  color: var(--ink-sub);
  font-size: 12px;
}

.content-wrapper {
  min-height: 0;
  overflow: hidden;
}

.list-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  align-items: start;
  align-content: start;
  max-width: 1080px;
  margin: 6px auto 0;
  width: 100%;
}

.book-card {
  position: relative;
  border-radius: 12px;
  border: 1px solid rgba(31, 42, 55, 0.08);
  background: var(--bg-soft);
  box-shadow: 0 8px 20px rgba(31, 42, 55, 0.1);
  padding: 10px;
  display: grid;
  grid-template-columns: 84px minmax(0, 1fr);
  gap: 8px;
  cursor: pointer;
  overflow: hidden;
  min-height: 0;
  transition: transform 0.28s ease, box-shadow 0.28s ease, border-color 0.28s ease;
}

.centered-card {
  grid-column: 1 / -1;
  width: 54%;
  justify-self: center;
}

.book-card::before {
  content: '';
  position: absolute;
  inset: auto -40px -52px auto;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(204, 122, 54, 0.14) 0%, rgba(204, 122, 54, 0) 75%);
  pointer-events: none;
}

.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 22px 46px rgba(31, 42, 55, 0.16);
  border-color: rgba(204, 122, 54, 0.36);
}

.trail-active .book-card {
  animation: highlightCard 1.4s ease-in-out infinite;
}

@keyframes highlightCard {
  0%, 100% { border-color: rgba(204, 122, 54, 0.2); }
  50% { border-color: rgba(204, 122, 54, 0.55); }
}

.rank-pill {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 11px;
  letter-spacing: 0.7px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #cb7b37, #9c5e2a);
  border-radius: 999px;
  padding: 4px 10px;
  z-index: 2;
}

.cover-box {
  width: 84px;
  height: 132px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.2);
}

.cover-box :deep(.css-book-cover) {
  width: 84px;
  height: 132px;
}

.info-box {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reason-chip {
  align-self: flex-start;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 700;
  color: #9a5a25;
  background: var(--accent-soft);
  margin-bottom: 6px;
}

.book-title {
  margin: 0;
  color: var(--ink-main);
  font-size: 14px;
  line-height: 1.3;
  font-weight: 800;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 4px;
}

.meta-tag {
  border-radius: 8px;
  padding: 2px 6px;
  font-size: 10px;
  color: #334155;
  background: #e9eef4;
}

.meta-tag.classify {
  background: #fcefd9;
  color: #91531f;
}

.book-desc {
  margin: 0;
  color: #4a5868;
  font-size: 11px;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-reason {
  margin: 0;
  color: #6b7380;
  font-size: 11px;
  line-height: 1.35;
}

.cta-row {
  padding-top: 2px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #855022;
  font-size: 10px;
  font-weight: 700;
  border-top: 1px dashed rgba(133, 80, 34, 0.28);
}

.arrow {
  font-size: 16px;
  line-height: 1;
}

.insight-dock {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-bottom: 2px;
}

.insight-dock-up {
  position: relative;
  transform: translateY(calc(-1 * var(--dock-lift)));
}

.reason-btn {
  border: none;
  border-radius: 999px;
  padding: 10px 20px;
  color: #fff;
  background: linear-gradient(135deg, #c37535, #8d5328);
  font-weight: 700;
  cursor: pointer;
}

.dialog-title-wrap h3 {
  margin: 0;
  color: #8d5328;
}

.dialog-title-wrap p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #6c5a49;
}

.dialog-body p {
  margin: 0 0 10px;
  color: #4d5965;
  line-height: 1.7;
}

.diagram-track {
  margin-top: 8px;
  padding: 14px;
  border-radius: 10px;
  background: #fff9f1;
  border: 1px dashed #d7a97e;
  display: grid;
  grid-template-columns: 130px 1fr 1.2fr;
  gap: 10px;
  align-items: center;
}

.seed-node {
  padding: 8px;
  text-align: center;
  border-radius: 8px;
  background: #e6c8a8;
  color: #7a4c21;
  font-weight: 700;
}

.flow-line {
  height: 2px;
  background: linear-gradient(90deg, #c37535, #8d5328);
  animation: dashRun 1.4s linear infinite;
}

.candidate-node {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.candidate-node span {
  font-size: 12px;
  border-radius: 999px;
  padding: 4px 10px;
  background: #f5e1cb;
  color: #7a4c21;
  animation: chipFloat 1.3s ease-in-out infinite alternate;
}

@keyframes dashRun {
  from { background-position: 0 0; }
  to { background-position: 120px 0; }
}

@keyframes chipFloat {
  from { transform: translateY(0); }
  to { transform: translateY(-2px); }
}

:deep(.itemcf-dialog .el-dialog) {
  max-width: calc(100vw - 32px);
  border-radius: 16px;
  overflow: hidden;
}

:deep(.itemcf-dialog .el-dialog__header) {
  padding: 18px 20px 10px;
  border-bottom: 1px solid #f1dfcc;
  background: #fff8ef;
}

:deep(.itemcf-dialog .el-dialog__body) {
  padding: 16px 20px 20px;
}

.dock-reveal-enter-active,
.dock-reveal-leave-active {
  transition: all 0.25s ease;
}

.dock-reveal-enter-from,
.dock-reveal-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@media (max-width: 900px) {
  .page-container {
    padding: 16px 12px;
    overflow: auto;
    display: block;
  }

  .list-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .centered-card {
    width: 100%;
  }
}

@media (max-width: 600px) {
  .list-grid {
    grid-template-columns: 1fr;
  }

  .centered-card {
    grid-column: auto;
  }
}
</style>
