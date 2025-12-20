<template>
  <div v-if="visible" class="ai-chat-overlay" @click.self="close">
    <div class="book-card-wrapper chat-window">
      <div class="book-shadow"></div>
      <div class="book-body unified-card">
        <!-- Header -->
        <div class="chat-header">
          <div class="header-left">
            <span class="icon">🤖</span>
            <span class="title">图书小智</span>
          </div>
          <button class="close-btn" @click="close">×</button>
        </div>
        
        <!-- Content -->
        <div class="chat-content" ref="chatContentRef">
          <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
            <div class="avatar">{{ msg.role === 'system' ? '🤖' : '👤' }}</div>
            <div class="bubble" v-html="renderMarkdown(msg.content)"></div>
          </div>
          <div v-if="isLoading" class="message system">
             <div class="avatar">🤖</div>
             <div class="bubble">正在思考...</div>
          </div>
        </div>

        <!-- Input Area -->
        <div class="chat-input-area">
          <input 
            type="text" 
            v-model="inputMessage" 
            @keyup.enter="sendMessage"
            placeholder="输入你的问题..." 
            class="chat-input" 
            :disabled="isLoading"
          />
          <button class="send-btn" @click="sendMessage" :disabled="isLoading">
            {{ isLoading ? '发送中' : '发送' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { marked } from 'marked';
import { useCookies } from '@vueuse/integrations/useCookies';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

interface Message {
  role: 'system' | 'user';
  content: string;
}

const messages = ref<Message[]>([
  {
    role: 'system',
    content: '你好！我是图书小智，你的智能图书助理。<br>我可以帮你找书、推荐书，或者聊聊你感兴趣的话题。'
  }
]);

const inputMessage = ref('');
const isLoading = ref(false);
const chatContentRef = ref<HTMLElement | null>(null);
const cookie = useCookies();

// 简单的 Markdown 渲染
const renderMarkdown = (text: string) => {
  return marked(text);
};

const scrollToBottom = async () => {
  await nextTick();
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight;
  }
};

const close = () => {
  emit('close');
};

const sendMessage = async () => {
  const content = inputMessage.value.trim();
  if (!content || isLoading.value) return;

  // 添加用户消息
  messages.value.push({ role: 'user', content });
  inputMessage.value = '';
  isLoading.value = true;
  await scrollToBottom();

  // 准备接收 AI 回复
  const aiMessageIndex = messages.value.push({ role: 'system', content: '' }) - 1;
  let currentResponse = '';

  try {
    const memoryId = cookie.get('userId') || '10001'; // 默认 fallback
    
    await fetchEventSource('http://localhost:8088/xiaozhi/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        memoryId: memoryId,
        message: content
      }),
      onmessage(msg) {
        if (msg.data) {
           currentResponse += msg.data;
           messages.value[aiMessageIndex].content = currentResponse;
           scrollToBottom();
        }
      },
      onclose() {
        isLoading.value = false;
      },
      onerror(err) {
        console.error('Chat error:', err);
        messages.value[aiMessageIndex].content += '\n[连接出错，请稍后重试]';
        isLoading.value = false;
        throw err; // 停止重试
      }
    });
  } catch (error) {
    isLoading.value = false;
  }
};

// 监听可见性变化，自动滚动到底部
watch(() => props.visible, (newVal) => {
  if (newVal) {
    scrollToBottom();
  }
});
</script>

<style scoped>
/* 复用 InteractiveWidget.vue 的风格 */
.book-card-wrapper {
  position: relative;
  width: 450px;
  height: 600px;
  max-width: 90vw;
  max-height: 80vh;
  cursor: default;
  /* 初始动画 */
  animation: popIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes popIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

.book-shadow {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-color: #000;
  z-index: 0;
  transform: translate(8px, 8px);
  border-radius: 2px;
}

.book-body {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 1;
  background-color: #FAF8F5; /* 更新背景色 */
  border: 2px solid #000;
  display: flex;
  flex-direction: column;
  transform: translate(-4px, -4px);
  border-radius: 2px;
}

/* 覆盖层样式 */
.ai-chat-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.1); /* 极其轻微的遮罩，或者完全透明 */
  backdrop-filter: blur(2px); /* 轻微模糊背景 */
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 内部布局 */
.chat-header {
  padding: 15px 20px;
  border-bottom: 2px solid #000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-left .icon {
  font-size: 20px;
}

.header-left .title {
  font-weight: 700;
  font-size: 16px;
  letter-spacing: 0.5px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  line-height: 1;
  padding: 0 5px;
  transition: transform 0.2s;
}

.close-btn:hover {
  transform: scale(1.1);
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  /* 网格背景 */
  background-color: #FAF8F5;
  background-image: 
    linear-gradient(#e5e5e5 1px, transparent 1px),
    linear-gradient(90deg, #e5e5e5 1px, transparent 1px);
  background-size: 20px 20px;
  
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.message.system {
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message .avatar {
  width: 32px;
  height: 32px;
  background: #fff;
  border: 1px solid #000;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message .bubble {
  background: #fff;
  padding: 12px 16px;
  border: 1px solid #000;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 2px 2px 0 rgba(0,0,0,0.1);
  word-wrap: break-word;
}

.message.system .bubble {
  border-top-left-radius: 0;
}

.message.user .bubble {
  border-top-right-radius: 0;
  background: #000;
  color: #fff;
}

.chat-input-area {
  padding: 15px;
  border-top: 2px solid #000;
  display: flex;
  gap: 10px;
  background: #fff;
}

.chat-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #000;
  border-radius: 4px;
  outline: none;
  font-family: inherit;
  font-size: 14px;
  transition: all 0.3s;
}

.chat-input:focus {
  box-shadow: 2px 2px 0 rgba(0,0,0,0.1);
}

.send-btn {
  padding: 0 25px;
  background: #000;
  color: #fff;
  border: 1px solid #000;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s;
}

.send-btn:hover {
  background: #333;
  transform: translateY(-1px);
}

.send-btn:active {
  transform: translateY(1px);
}

.send-btn:disabled {
  background: #ccc;
  border-color: #ccc;
  cursor: not-allowed;
  transform: none;
}
</style>
