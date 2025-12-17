<template>
  <div class="suggest-container">
    <div class="suggest-layout">
      <div class="header-section">
        <h2>留言建议</h2>
        <p class="subtitle">您的反馈是我们进步的动力</p>
      </div>

      <div class="messages-container" ref="messagesContainer">
        <div v-if="suggestions.length === 0" class="empty-state">
          <el-empty description="暂无留言" />
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
          placeholder="请输入您的建议..."
          type="textarea"
          :rows="3"
          resize="none"
          class="custom-textarea"
        />
        <div class="button-wrapper">
          <el-button color="#000" @click="inputBtn" :disabled="!inputValue.trim()">
            提交建议
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick } from 'vue'
import myAxios from '@/api/index'
import { useCookies } from '@vueuse/integrations/useCookies'
import { ElMessage } from 'element-plus'

const cookie = useCookies()
const inputValue = ref('')
const userName = ref('')
const suggestions = ref([])
const messagesContainer = ref(null)

onMounted(() => {
  userName.value = cookie.get('username')
  getUserAdvises()
})

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const inputBtn = async () => {
  if (!inputValue.value.trim()) return
  await postSuggest()
  inputValue.value = ''
}

async function getUserAdvises() {
  try {
    let res = await myAxios.get('http://localhost:8080/advice/get?userName=' + userName.value)
    suggestions.value = res.data.data
    scrollToBottom()
  } catch (error) {
    console.error(error)
    ElMessage.error('获取留言失败')
  }
}

async function postSuggest() {
  try {
    let res = await myAxios.post('http://localhost:8080/advice/input', {
      info: inputValue.value,
      userName: userName.value
    })
    if (res.data.code === 200) {
      ElMessage.success('提交成功')
      await getUserAdvises()
    } else {
      ElMessage.error(res.data.msg || '提交失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误')
  }
}
</script>

<style scoped>
.suggest-container {
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  box-sizing: border-box;
}

.suggest-layout {
  width: 100%;
  max-width: 800px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.header-section {
  padding: 20px 30px;
  border-bottom: 1px solid #eee;
  background: #fff;
}

.header-section h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 5px 0 0;
  font-size: 14px;
  color: #909399;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f9f9f9;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  border: 1px solid #ebeef5;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.username {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.time {
  font-size: 12px;
  color: #909399;
}

.message-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

.reply-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  background: #fdfdfd;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.admin-badge {
  background: #f4f4f5;
  color: #909399;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #e9e9eb;
}

.reply-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.input-section {
  padding: 20px;
  background: #fff;
  border-top: 1px solid #eee;
}

.button-wrapper {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

/* Scrollbar styling */
.messages-container::-webkit-scrollbar {
  width: 6px;
}
.messages-container::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}
.messages-container::-webkit-scrollbar-track {
  background: transparent;
}
</style>