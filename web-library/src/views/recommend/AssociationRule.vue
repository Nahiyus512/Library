<template>
  <div class="page-container">
    <div class="header">
      <h2 class="page-title">BUNDLES // 关联规则</h2>
      <p class="page-subtitle">"啤酒与尿布"：大家经常把这些书一起带回家</p>
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
                :author="book.bookAuthor || 'Unknown'" 
                :id="book.bookId"
              />
            </div>
            <!-- Decorative 'behind' book to suggest bundle -->
            <div class="shadow-book"></div>
          </div>
          <div class="bundle-info">
            <span class="combo-tag">最佳组合</span>
            <h3 class="bundle-title">{{ book.bookName }}</h3>
            <p class="bundle-hint">常与你已加入书架的书一起被借阅</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐。丰富你的书架，让我们发现更多关联！" />
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
        strategy: 'association_rule'
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
  background-color: #001529; /* Dark Blue Blueprint Background */
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  color: #fff;
  font-family: 'Consolas', 'Monaco', monospace; /* Technical font */
}

.header {
  margin-bottom: 40px;
  text-align: center;
  border-bottom: 2px dashed #1890ff;
  padding-bottom: 20px;
  display: inline-block;
  width: 100%;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-transform: uppercase;
  color: #40a9ff;
  text-shadow: 0 0 10px rgba(24, 144, 255, 0.3);
}

.page-subtitle {
  color: #8cc8ff;
  font-size: 14px;
}

.bundle-layout {
  display: flex;
  flex-wrap: nowrap;
  gap: 30px;
  justify-content: center;
  max-width: 100%;
  margin: 0 auto;
  overflow-x: auto;
  padding: 20px 0;
}

.bundle-card {
  width: 20%;
  min-width: 240px;
  background: rgba(24, 144, 255, 0.05);
  border: 1px solid #1890ff;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  flex-shrink: 0;
  position: relative;
}

.bundle-card::before, .bundle-card::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  border: 1px solid #1890ff;
  transition: all 0.3s;
}

.bundle-card::before { top: -1px; left: -1px; border-right: none; border-bottom: none; }
.bundle-card::after { bottom: -1px; right: -1px; border-left: none; border-top: none; }

.bundle-card:hover {
  transform: translateY(-5px);
  background: rgba(24, 144, 255, 0.15);
  box-shadow: 0 0 20px rgba(24, 144, 255, 0.2);
}

.bundle-visual {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 140%;
  margin-bottom: 20px;
}

.main-book {
  position: absolute;
  top: 0;
  left: 10%;
  width: 80%;
  height: 100%;
  z-index: 2;
  box-shadow: 5px 5px 0px rgba(24, 144, 255, 0.3);
  transition: transform 0.3s;
  border: 1px solid #40a9ff;
}

.bundle-card:hover .main-book {
  transform: translate(-5px, -5px);
  box-shadow: 10px 10px 0px rgba(24, 144, 255, 0.3);
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
  border-top: 1px dashed rgba(255,255,255,0.2);
  padding-top: 15px;
}

.combo-tag {
  background: transparent;
  color: #40a9ff;
  border: 1px solid #40a9ff;
  font-size: 10px;
  padding: 2px 8px;
  font-weight: bold;
  margin-bottom: 10px;
  display: inline-block;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.bundle-title {
  font-size: 16px;
  margin: 0 0 8px 0;
  font-weight: 400;
  color: #fff;
  letter-spacing: 1px;
}

.bundle-hint {
  font-size: 12px;
  color: #8cc8ff;
  font-style: normal;
}
</style>