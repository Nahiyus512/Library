<template>
  <div class="page-container" :class="{ 'scan-active': scanAnimating }">
    <div class="header">
      <h2 class="page-title">关联规则推荐</h2>
      <p class="page-subtitle">基于借阅共现关系，识别更可能衔接你当前阅读路径的下一本书。</p>
    </div>

    <div class="content-wrapper">
      <div class="bundle-layout" v-if="books.length > 0">
        <div
          v-for="(book, index) in books"
          :key="book.bookId"
          class="bundle-card"
          @click="openDetail(book)"
        >
          <div class="bundle-visual">
            <div class="main-book">
              <CssBookCover
                :title="book.bookName"
                :author="book.bookAuthor || '未知作者'"
                :id="book.bookId"
              />
            </div>
            <div class="shadow-book"></div>
          </div>
          <div class="bundle-info">
            <span class="combo-tag">共借阅组合</span>
            <h3 class="bundle-title">{{ book.bookName }}</h3>
            <p class="bundle-hint">{{ buildAssociationReason(book, index) }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。请补充借阅与评分数据后再试。" />
    </div>

    <div class="insight-dock insight-dock-up" v-if="books.length > 0">
      <button class="reason-btn" @click="openReasonDialog">查看关联规则推荐依据</button>
    </div>

    <div v-if="showReasonDialog" class="assoc-modal-mask" @click.self="closeReasonDialog">
      <div class="assoc-modal">
        <button class="assoc-close-btn" @click="closeReasonDialog">&times;</button>
        <div class="assoc-modal-header">
          <h3>关联规则推荐解释</h3>
          <p>从共现借阅行为出发，筛选与当前阅读路径更连贯的候选书。</p>
        </div>
        <div class="assoc-modal-body">
          <div class="explain-flow">
            <div class="flow-item">
              <strong>1. 发现共现</strong>
              <span>统计借阅记录中，经常被同一批读者连续选择的图书组合。</span>
            </div>
            <div class="flow-item">
              <strong>2. 计算强度</strong>
              <span>根据出现频率与稳定性评估规则强度，过滤偶然噪声。</span>
            </div>
            <div class="flow-item">
              <strong>3. 输出候选</strong>
              <span>优先展示关联强度高、与当前阅读路径衔接更顺畅的书。</span>
            </div>
          </div>
          <div class="scan-grid">
            <div class="scan-beam"></div>
            <div class="evidence-row" v-for="row in associationRows" :key="row.bookId">
              <span class="book">{{ row.bookName }}</span>
              <span class="score">共现强度 {{ row.score }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

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
const scanAnimating = ref(false)

const associationRows = computed(() =>
  books.value.slice(0, 4).map((book, index) => ({
    bookId: book.bookId,
    bookName: book.bookName,
    score: 92 - index * 8
  }))
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
    const res = await myAxios.get('http://localhost:8080/bookScore/recommend', {
      params: {
        userId,
        strategy: 'association_rule'
      }
    })
    books.value = res.data.data || []
  } catch (e) {
    console.log(e)
  }
}

const buildAssociationReason = (book: BookItem, index: number) => {
  const rank = index + 1
  const classifyHint = book.bookClassify ? `主题：${book.bookClassify}。` : '主题信息待补充。'
  return `规则命中第 ${rank} 位。该书与您已读书在借阅行为上存在更稳定的共现关系。${classifyHint}`
}

const openReasonDialog = () => {
  showReasonDialog.value = true
  scanAnimating.value = true
  window.setTimeout(() => {
    scanAnimating.value = false
  }, 3600)
}

const closeReasonDialog = () => {
  showReasonDialog.value = false
}

const openDetail = (book: BookItem) => {
  selectedBook.value = book
  showDialog.value = true
}
</script>

<style scoped>
.page-container {
  --dock-lift: 42px;
  padding: 24px 24px 18px;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #001529;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  color: #fff;
  font-family: 'Consolas', 'Monaco', monospace;
  display: grid;
  grid-template-rows: auto 1fr auto;
  gap: 10px;
}

.header {
  text-align: center;
  border-bottom: 2px dashed #1890ff;
  padding-bottom: 16px;
}

.content-wrapper {
  min-height: 0;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px;
  color: #40a9ff;
  text-shadow: 0 0 10px rgba(24, 144, 255, 0.3);
}

.page-subtitle {
  color: #8cc8ff;
  font-size: 14px;
  margin: 0;
}

.bundle-layout {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  width: 100%;
  margin: 0 auto;
  padding: 8px 2px 10px;
  align-content: start;
}

.bundle-card {
  width: 100%;
  min-width: 0;
  background: rgba(24, 144, 255, 0.05);
  border: 1px solid #1890ff;
  padding: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
}

.bundle-card::before,
.bundle-card::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  border: 1px solid #1890ff;
  transition: all 0.3s;
}

.bundle-card::before {
  top: -1px;
  left: -1px;
  border-right: none;
  border-bottom: none;
}

.bundle-card::after {
  bottom: -1px;
  right: -1px;
  border-left: none;
  border-top: none;
}

.bundle-card:hover {
  transform: translateY(-5px);
  background: rgba(24, 144, 255, 0.15);
  box-shadow: 0 0 20px rgba(24, 144, 255, 0.2);
}

.scan-active .bundle-card {
  animation: borderScan 1.4s ease-in-out infinite;
}

@keyframes borderScan {
  0%, 100% { box-shadow: inset 0 0 0 rgba(64, 169, 255, 0); }
  50% { box-shadow: inset 0 0 24px rgba(64, 169, 255, 0.22); }
}

.bundle-visual {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 140%;
  margin-bottom: 12px;
}

.main-book {
  position: absolute;
  top: 0;
  left: 10%;
  width: 80%;
  height: 100%;
  z-index: 2;
  box-shadow: 5px 5px 0 rgba(24, 144, 255, 0.3);
  transition: transform 0.3s;
  border: 1px solid #40a9ff;
}

.bundle-card:hover .main-book {
  transform: translate(-5px, -5px);
}

.shadow-book {
  position: absolute;
  top: 10px;
  left: 20%;
  width: 80%;
  height: 100%;
  background: repeating-linear-gradient(
    45deg,
    rgba(24, 144, 255, 0.1),
    rgba(24, 144, 255, 0.1) 10px,
    rgba(24, 144, 255, 0.2) 10px,
    rgba(24, 144, 255, 0.2) 20px
  );
  z-index: 1;
  border: 1px dashed #1890ff;
}

.bundle-info {
  width: 100%;
  border-top: 1px dashed rgba(255, 255, 255, 0.2);
  padding-top: 12px;
}

.combo-tag {
  background: transparent;
  color: #40a9ff;
  border: 1px solid #40a9ff;
  font-size: 10px;
  padding: 2px 8px;
  font-weight: bold;
  margin-bottom: 8px;
  display: inline-block;
  letter-spacing: 1px;
}

.bundle-title {
  font-size: 16px;
  margin: 0 0 8px;
  font-weight: 400;
  color: #fff;
}

.bundle-hint {
  font-size: 12px;
  color: #8cc8ff;
  margin: 0;
  line-height: 1.5;
}

.insight-dock {
  width: min(920px, 100%);
  margin: 4px auto 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.insight-dock-up {
  position: relative;
  transform: translateY(calc(-1 * var(--dock-lift)));
}

.reason-btn {
  border: 1px solid rgba(177, 221, 255, 0.9);
  color: #ffffff;
  background: linear-gradient(135deg, #2d7be8, #1656b8);
  box-shadow: 0 10px 24px rgba(24, 105, 222, 0.35);
  border-radius: 999px;
  padding: 10px 20px;
  font-weight: 700;
  cursor: pointer;
}

.reason-btn:hover {
  transform: translateY(-1px);
}

.assoc-modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(5, 18, 34, 0.48);
  display: grid;
  place-items: center;
  z-index: 2100;
  padding: 16px;
}

.assoc-modal {
  width: min(760px, calc(100vw - 32px));
  border-radius: 16px;
  border: 1px solid #cfe2f7;
  background: #ffffff;
  color: #0f2f54;
  box-shadow: 0 18px 46px rgba(8, 23, 41, 0.3);
  position: relative;
  overflow: hidden;
}

.assoc-close-btn {
  position: absolute;
  top: 10px;
  right: 12px;
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 50%;
  background: #e6eff9;
  color: #0f2f54;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
}

.assoc-modal-header {
  padding: 18px 44px 12px 20px;
  border-bottom: 1px solid #dbe8f6;
  background: #f6f9fe;
}

.assoc-modal-header h3 {
  margin: 0;
  color: #123f6c;
  font-size: 23px;
  letter-spacing: 0.2px;
}

.assoc-modal-header p {
  margin: 8px 0 0;
  color: #2c5a89;
  font-size: 13px;
  line-height: 1.6;
}

.assoc-modal-body {
  padding: 16px 20px 20px;
  background: #ffffff;
}

.explain-flow {
  display: grid;
  gap: 10px;
  margin-bottom: 12px;
}

.flow-item {
  border: 1px solid #d7e6f6;
  background: #f8fbff;
  border-radius: 10px;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.flow-item strong {
  color: #123f6c;
  font-size: 13px;
}

.flow-item span {
  color: #355e89;
  font-size: 12px;
  line-height: 1.6;
}

.scan-grid {
  position: relative;
  margin-top: 8px;
  display: grid;
  gap: 6px;
  border: 1px dashed #bcd5ef;
  border-radius: 8px;
  padding: 12px;
  overflow: hidden;
  background: #f8fbff;
}

.scan-beam {
  position: absolute;
  left: 0;
  right: 0;
  height: 22px;
  background: linear-gradient(180deg, rgba(64, 139, 230, 0), rgba(64, 139, 230, 0.16), rgba(64, 139, 230, 0));
  animation: sweep 2s linear infinite;
}

.evidence-row {
  display: flex;
  justify-content: space-between;
  border: 1px dashed #bfd7f0;
  border-radius: 6px;
  padding: 6px 8px;
  font-size: 12px;
  background: #ffffff;
}

.book {
  color: #163f69;
}

.score {
  color: #2e6ba3;
}

@keyframes sweep {
  from { top: -24px; }
  to { top: 100%; }
}
</style>
