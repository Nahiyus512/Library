<template>
  <div v-if="visible" class="ai-chat-overlay" @click.self="close">
    <div class="book-card-wrapper chat-window">
      <div class="book-shadow"></div>
      <div class="book-body unified-card">
        <!-- Sidebar -->
        <div class="chat-sidebar">
          <div class="new-chat-btn" @click="startNewChat">
            <span>+ 新对话</span>
          </div>
          <div class="history-list">
             <div 
               v-for="item in historyList" 
               :key="item.memoryId"
               :class="['history-item', { active: item.memoryId === memoryId }]"
               @click="loadSession(item)"
             >
               <span class="history-title">{{ getHistoryTitle(item) }}</span>
             </div>
          </div>
        </div>

        <!-- Main Chat Area -->
        <div class="chat-main">
          <!-- Header -->
          <div class="chat-header">
            <div class="header-left">
              <span class="icon">🌟</span>
              <span class="title">Polaris</span>
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
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch, onMounted } from 'vue';
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

interface ChatSession {
  memoryId: number;
  userId: string;
  content: string; // JSON string
}

const messages = ref<Message[]>([
  {
    role: 'system',
    content: '你好！我是 Polaris，你的智能图书助理。<br>我可以帮你找书、推荐书，或者聊聊你感兴趣的话题。'
  }
]);

const historyList = ref<ChatSession[]>([]);
const inputMessage = ref('');
const isLoading = ref(false);
const chatContentRef = ref<HTMLElement | null>(null);
const cookie = useCookies();
const userId = ref<string>('');
const memoryId = ref<number>(new Date().getTime());

// 简单的 Markdown 渲染
const renderMarkdown = (text: string) => {
  return marked(text || '');
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

const getHistoryTitle = (session: ChatSession) => {
  try {
    if (!session.content) return `对话 ${new Date(session.memoryId).toLocaleDateString()}`;
    let msgs;
    if (typeof session.content === 'string') {
        msgs = JSON.parse(session.content);
    } else {
        msgs = session.content;
    }
    
    if (!Array.isArray(msgs)) return `对话 ${new Date(session.memoryId).toLocaleDateString()}`;

    // 找到第一条用户消息作为标题
    const firstUserMsg = msgs.find((m: any) => {
      const type = m.type ? m.type.toUpperCase() : '';
      return type === 'USER';
    });
    
    if (firstUserMsg) {
       let text = '';
       if (firstUserMsg.contents && Array.isArray(firstUserMsg.contents)) {
          const textContent = firstUserMsg.contents.find((c: any) => c.type === 'TEXT');
          if (textContent) text = textContent.text;
       } else {
          text = firstUserMsg.text || firstUserMsg.content || '';
       }
       return text.length > 10 ? text.substring(0, 10) + '...' : text;
    }
    return `对话 ${new Date(session.memoryId).toLocaleDateString()}`;
  } catch (e) {
    console.warn("Parse session content failed", e);
    return `对话 ${session.memoryId}`;
  }
};

const loadSession = (session: ChatSession) => {
  if (isLoading.value) return;
  memoryId.value = session.memoryId;
  try {
    if (!session.content) {
       messages.value = [];
       return;
    }
    let history;
    if (typeof session.content === 'string') {
        history = JSON.parse(session.content);
    } else {
        history = session.content;
    }
    console.log("Loading history:", history);
    
    if (!Array.isArray(history)) {
       messages.value = [];
       return;
    }

    messages.value = history.map((m: any) => {
      const type = m.type ? m.type.toUpperCase() : '';
      const role = (type === 'AI' || type === 'SYSTEM') ? 'system' : 'user';
      
      let content = '';
      if (m.contents && Array.isArray(m.contents)) {
          const textContent = m.contents.find((c: any) => c.type === 'TEXT');
          if (textContent) content = textContent.text;
      } else {
          content = m.text || m.content || '';
      }
      return { role, content };
    });
    scrollToBottom();
  } catch (e) {
    console.error("Parse session failed", e);
  }
};

const startNewChat = () => {
  if (isLoading.value) return;
  memoryId.value = new Date().getTime();
  messages.value = [
    {
      role: 'system',
      content: '你好！我是 Polaris，你的智能图书助理。<br>我可以帮你找书、推荐书，或者聊聊你感兴趣的话题。'
    }
  ];
};

const fetchHistory = async () => {
  userId.value = cookie.get('userId') || '10001';
  try {
    const res = await fetch(`http://localhost:8080/polaris/history?userId=${userId.value}`);
    const data = await res.json();
    if (data.code === 200 && data.data) {
       // Ensure data.data is an array
       if (Array.isArray(data.data)) {
         historyList.value = data.data.sort((a: ChatSession, b: ChatSession) => b.memoryId - a.memoryId);
         
         // 如果有历史记录且当前是初始状态，加载最近一次对话
         if (historyList.value.length > 0 && messages.value.length === 1) {
            // loadSession(historyList.value[0]);
         }
       } else {
         console.warn("Expected history data to be an array, but got:", data.data);
         // If it's a single object (legacy support or bug), wrap it
         if (typeof data.data === 'object') {
            historyList.value = [data.data];
         }
       }
    }
  } catch (e) {
    console.error("Fetch history failed", e);
  }
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
    await fetchEventSource('http://localhost:8080/polaris/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify({
        memoryId: memoryId.value,
        message: content,
        userId: userId.value
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
        // 刷新历史列表以显示新对话
        fetchHistory();
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

// 监听可见性变化，自动滚动到底部并获取历史
watch(() => props.visible, (newVal) => {
  if (newVal) {
    fetchHistory();
    scrollToBottom();
  }
});

onMounted(() => {
  if (props.visible) {
    fetchHistory();
  }
});
</script>

<style scoped>
/* 复用 InteractiveWidget.vue 的风格 */
.book-card-wrapper {
  position: relative;
  width: 900px;
  height: 800px;
  max-width: 95vw;
  max-height: 90vh;
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
  flex-direction: row; /* 改为横向布局 */
  transform: translate(-4px, -4px);
  border-radius: 2px;
}

/* Sidebar Styles */
.chat-sidebar {
  width: 200px;
  border-right: 2px solid #000;
  display: flex;
  flex-direction: column;
  background: #f0f0f0;
}

.new-chat-btn {
  padding: 17.5px;
  text-align: center;
  border-bottom: 2px solid #000;
  cursor: pointer;
  background: #fff;
  font-weight: bold;
  transition: background 0.2s;
}

.new-chat-btn:hover {
  background: #e0e0e0;
}

.history-list {
  flex: 1;
  overflow-y: auto;
}

.history-item {
  padding: 12px 15px;
  border-bottom: 1px solid #ccc;
  cursor: pointer;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: background 0.2s;
}

.history-item:hover {
  background: #e8e8e8;
}

.history-item.active {
  background: #fff;
  font-weight: bold;
  border-left: 4px solid #000;
}

/* Main Chat Area Styles */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0; /* 防止子元素溢出 */
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
