<template>
  <div class="book-comments">
    <h3>书友评论</h3>
    <div class="messages-container">
      <div v-if="suggestions.length === 0" class="empty-state">
        <el-empty description="暂无评论，快来抢沙发吧！" :image-size="60" />
      </div>
      
      <div v-else class="message-list">
        <div v-for="(item, index) in suggestions" :key="index" class="message-card">
          <div class="message-header">
            <span class="username">{{ item.userName }}</span>
            <span class="time">{{ item.infoTime }}</span>
          </div>
          <div class="message-content">
            {{ item.info }}
          </div>
          
          <div v-if="item.reply" class="reply-section">
            <div class="reply-header">
              <span class="admin-badge">管理员回复</span>
              <span class="time">{{ item.replyTime }}</span>
            </div>
            <div class="reply-content">
              {{ item.reply }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="input-section">
      <el-input
        v-model="inputValue"
        placeholder="发表对此书的评论..."
        type="textarea"
        :rows="2"
        resize="none"
      />
      <div class="button-wrapper">
        <el-button type="primary" size="small" @click="inputBtn" :disabled="!inputValue.trim()">
          发表评论
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import myAxios from '@/api/index'
import { useCookies } from '@vueuse/integrations/useCookies'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  bookId: number | string
}>()

const cookie = useCookies()
const inputValue = ref('')
const userName = ref('')
const suggestions = ref([])

onMounted(() => {
  userName.value = cookie.get('username')
  if (props.bookId) {
    getBookComments()
  }
})

watch(() => props.bookId, (newVal) => {
  if (newVal) {
    getBookComments()
  }
})

const inputBtn = async () => {
  if (!inputValue.value.trim()) return
  await postComment()
  inputValue.value = ''
}

async function getBookComments() {
  try {
    let res = await myAxios.get(`http://localhost:8080/advice/getBookAdvice?bookId=${props.bookId}`)
    suggestions.value = res.data.data
  } catch (error) {
    console.error(error)
    ElMessage.error('获取评论失败')
  }
}

async function postComment() {
  try {
    let res = await myAxios.post('http://localhost:8080/advice/input', {
      info: inputValue.value,
      userName: userName.value,
      bookId: props.bookId
    })
    if (res.data.code === 200) {
      ElMessage.success('评论成功')
      await getBookComments()
    } else {
      ElMessage.error(res.data.msg || '评论失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误')
  }
}
</script>

<style scoped>
.book-comments {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}
.book-comments h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}
.messages-container {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 15px;
}
.message-card {
  background: #FFFFFF;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}
.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}
.username {
  color: #409EFF;
  font-weight: bold;
}
.message-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
}
.input-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.button-wrapper {
  text-align: right;
}
.reply-section {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}
.reply-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}
.admin-badge {
  background: #f0f9eb;
  color: #67c23a;
  padding: 0 4px;
  border-radius: 2px;
}
</style>
