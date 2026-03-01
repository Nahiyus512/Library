<template>
  <div v-if="visible" class="ai-chat-overlay" @click.self="close">
    <div class="book-card-wrapper chat-window">
      <div class="book-shadow"></div>
      <div class="book-body unified-card">
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

        <div class="chat-main">
          <div class="chat-header">
            <div class="header-left">
              <span class="icon">AI</span>
              <span class="title">Polaris</span>
            </div>
            <button class="close-btn" @click="close">×</button>
          </div>

          <div class="feature-toolbar">
            <button
              v-for="item in featureOptions"
              :key="item.id"
              class="feature-btn"
              :class="{ active: pendingFeature === item.id }"
              :disabled="isLoading"
              @click="onFeatureClick(item.id)"
            >
              {{ item.label }}
            </button>
          </div>

          <div class="chat-content" ref="chatContentRef" @click="handleBubbleClick">
            <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
              <div class="avatar">{{ msg.role === 'system' ? '🌟' : 'User' }}</div>
              <div class="bubble" v-html="renderMarkdown(msg.content)"></div>
            </div>
            <div v-if="isLoading" class="message system">
              <div class="avatar">AI</div>
              <div class="bubble">正在处理中...</div>
            </div>
          </div>

          <div class="bottom-controls">
            <div class="chat-input-area">
              <input
                v-model="inputMessage"
                type="text"
                class="chat-input"
                :disabled="isLoading"
                placeholder="输入你的需求，例如：推荐特色页面 / 检索系统图书库 三体"
                @keyup.enter="sendMessage"
              />
              <button class="send-btn" :disabled="isLoading" @click="sendMessage">
                {{ isLoading ? '发送中' : '发送' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
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
  content: string;
}

type FeatureId =
  | 'featured_recommend'
  | 'system_search'
  | 'user_data_query'
  | 'preference_manage'
  | 'score_manage'
  | 'book_suggestion'
  | 'feedback_advice';

interface FeatureOption {
  id: FeatureId;
  label: string;
}

const router = useRouter();
const cookie = useCookies();

const featureOptions: FeatureOption[] = [
  { id: 'featured_recommend', label: '特色推荐' },
  { id: 'system_search', label: '系统检索' },
  { id: 'user_data_query', label: '用户数据' },
  { id: 'preference_manage', label: '喜好管理' },
  { id: 'score_manage', label: '评分管理' },
  { id: 'book_suggestion', label: '图书建议' },
  { id: 'feedback_advice', label: '反馈建议' }
];

const messages = ref<Message[]>([{ role: 'system', content: buildWelcomeMessage() }]);
const historyList = ref<ChatSession[]>([]);
const inputMessage = ref('');
const isLoading = ref(false);
const chatContentRef = ref<HTMLElement | null>(null);
const userId = ref<string>('');
const memoryId = ref<number>(Date.now());
const pendingFeature = ref<FeatureId | null>(null);

function buildWelcomeMessage(): string {
  return [
    '你好，我是 Polaris，图书馆 AI 助手。',
    '我负责协助你完成检索、推荐、评分/喜好管理以及建议反馈。',
    '请点击上方功能按钮，我会先引导你补充信息，再执行对应功能。'
  ].join('\n');
}

function renderMarkdown(text: string): string {
  return marked.parse(text || '', { async: false }) as string;
}

async function scrollToBottom(): Promise<void> {
  await nextTick();
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight;
  }
}

function close(): void {
  emit('close');
}

function pushSystemMessage(content: string): void {
  messages.value.push({ role: 'system', content });
  void scrollToBottom();
}

function onFeatureClick(featureId: FeatureId): void {
  if (isLoading.value) return;

  if (featureId === 'featured_recommend') {
    pendingFeature.value = null;
    messages.value.push({ role: 'user', content: '特色推荐' });
    void scrollToBottom();
    void requestChat('特色推荐');
    return;
  }

  pendingFeature.value = featureId;
  if (featureId === 'system_search') {
    pushSystemMessage('已选择【系统图书检索】。请提供书名或关键词，例如：三体。');
    return;
  }
  if (featureId === 'user_data_query') {
    pushSystemMessage('已选择【用户数据查询】。请描述要查的内容，例如：我的喜好和评分。');
    return;
  }
  if (featureId === 'preference_manage') {
    pushSystemMessage('已选择【喜好管理】。请输入：书名 + 喜好（想看/还行/不想看），例如：三体 想看。');
    return;
  }
  if (featureId === 'score_manage') {
    pushSystemMessage('已选择【评分管理】。请输入：书名 + 分数(1-5)，例如：三体 5。');
    return;
  }
  if (featureId === 'book_suggestion') {
    pushSystemMessage('已选择【图书建议】。请输入建议内容，例如：图书建议《三体》建议补充新版。');
    return;
  }
  pushSystemMessage('已选择【反馈建议】。请输入建议内容，例如：希望增加按作者筛选。');
}

function getHistoryTitle(session: ChatSession): string {
  try {
    if (!session.content) return `对话 ${new Date(session.memoryId).toLocaleDateString()}`;
    const raw = typeof session.content === 'string' ? JSON.parse(session.content) : session.content;
    if (!Array.isArray(raw)) return `对话 ${new Date(session.memoryId).toLocaleDateString()}`;
    const firstUserMsg = raw.find((m: { type?: string }) => (m.type || '').toUpperCase() === 'USER');
    if (!firstUserMsg) return `对话 ${new Date(session.memoryId).toLocaleDateString()}`;
    const text = extractMessageText(firstUserMsg);
    return text.length > 12 ? `${text.slice(0, 12)}...` : text;
  } catch {
    return `对话 ${session.memoryId}`;
  }
}

function extractMessageText(raw: {
  contents?: Array<{ type?: string; text?: string }>;
  text?: string;
  content?: string;
}): string {
  if (raw.contents && Array.isArray(raw.contents)) {
    const txt = raw.contents.find((c) => (c.type || '').toUpperCase() === 'TEXT');
    if (txt?.text) return txt.text;
  }
  return raw.text || raw.content || '';
}

function handleBubbleClick(event: MouseEvent): void {
  const target = event.target as HTMLElement | null;
  if (!target) return;
  const anchor = target.closest('a') as HTMLAnchorElement | null;
  if (!anchor) return;
  const href = anchor.getAttribute('href') || '';
  if (!href.startsWith('/')) return;
  event.preventDefault();
  emit('close');
  void router.push(href);
}

function loadSession(session: ChatSession): void {
  if (isLoading.value) return;
  memoryId.value = session.memoryId;
  pendingFeature.value = null;
  try {
    if (!session.content) {
      messages.value = [{ role: 'system', content: buildWelcomeMessage() }];
      return;
    }
    const history = typeof session.content === 'string' ? JSON.parse(session.content) : session.content;
    if (!Array.isArray(history)) {
      messages.value = [{ role: 'system', content: buildWelcomeMessage() }];
      return;
    }
    messages.value = history.map((m: { type?: string; contents?: Array<{ type?: string; text?: string }>; text?: string; content?: string }) => {
      const role: Message['role'] = ['AI', 'SYSTEM'].includes((m.type || '').toUpperCase()) ? 'system' : 'user';
      return { role, content: extractMessageText(m) };
    });
    void scrollToBottom();
  } catch {
    messages.value = [{ role: 'system', content: buildWelcomeMessage() }];
  }
}

function startNewChat(): void {
  if (isLoading.value) return;
  memoryId.value = Date.now();
  pendingFeature.value = null;
  messages.value = [{ role: 'system', content: buildWelcomeMessage() }];
}

async function fetchHistory(): Promise<void> {
  userId.value = cookie.get('userId') || '10001';
  try {
    const res = await fetch(`http://localhost:8080/polaris/history?userId=${userId.value}`);
    const data = await res.json();
    if (data.code === 200 && Array.isArray(data.data)) {
      historyList.value = data.data.sort((a: ChatSession, b: ChatSession) => b.memoryId - a.memoryId);
    }
  } catch {
    historyList.value = [];
  }
}

function stripQuotes(text: string): string {
  return text.replace(/[《》"'“”]/g, '').trim();
}

function parsePreferenceInput(text: string): { bookName: string; levelText: string } | null {
  const match = text.trim().match(/^(.+?)\s*(想看|还行|一般|不想看|喜欢|0|1|2)$/);
  if (!match) return null;
  const levelMap: Record<string, string> = {
    想看: '想看',
    喜欢: '想看',
    还行: '还行',
    一般: '还行',
    不想看: '不想看',
    '0': '不想看',
    '1': '还行',
    '2': '想看'
  };
  const bookName = stripQuotes(match[1]);
  if (!bookName) return null;
  return { bookName, levelText: levelMap[match[2]] };
}

function parseScoreInput(text: string): { bookName: string; score: number } | null {
  const simple = text.trim().match(/^(.+?)\s*([1-5])\s*分?$/);
  if (simple) {
    const bookName = stripQuotes(simple[1]);
    if (!bookName) return null;
    return { bookName, score: Number(simple[2]) };
  }

  const natural = text.trim().match(/给?\s*《?(.+?)》?\s*(?:评分|打分|评为?)\s*([1-5])\s*分?/);
  if (!natural) return null;
  const bookName = stripQuotes(natural[1]);
  if (!bookName) return null;
  return { bookName, score: Number(natural[2]) };
}

function resolvePendingCommand(rawText: string): { command: string | null; error?: string } {
  const text = rawText.trim();
  if (!pendingFeature.value) return { command: text };
  if (!text) return { command: null, error: '请输入内容，或输入“取消”退出当前功能。' };
  if (text === '取消') {
    pendingFeature.value = null;
    return { command: null, error: '已取消当前功能选择。' };
  }

  if (pendingFeature.value === 'featured_recommend') {
    pendingFeature.value = null;
    if (text.includes('换一批') || text.includes('再来一批')) return { command: '换一批' };
    if (/(特色推荐|特色页面|推荐书|推荐几本|给我推荐)/.test(text)) return { command: text };
    return { command: `推荐几本书：${text}` };
  }

  if (pendingFeature.value === 'system_search') {
    pendingFeature.value = null;
    if (/(系统图书库|系统书库|检索|搜索|查找)/.test(text) && text.includes('书')) return { command: text };
    return { command: `检索系统图书库《${stripQuotes(text)}》` };
  }

  if (pendingFeature.value === 'user_data_query') {
    pendingFeature.value = null;
    if (/(我的数据|用户数据|喜好表|评分表|我的喜好|我的评分|我的书库)/.test(text)) return { command: text };
    return { command: `查询我的喜好表和评分表：${text}` };
  }

  if (pendingFeature.value === 'preference_manage') {
    const parsed = parsePreferenceInput(text);
    if (!parsed) {
      return { command: null, error: '格式不正确，请输入：书名 + 喜好（想看/还行/不想看），例如：三体 想看。' };
    }
    pendingFeature.value = null;
    return { command: `把《${parsed.bookName}》设为${parsed.levelText}` };
  }

  if (pendingFeature.value === 'score_manage') {
    const parsed = parseScoreInput(text);
    if (!parsed) {
      return { command: null, error: '格式不正确，请输入：书名 + 1-5 分，例如：三体 5。' };
    }
    pendingFeature.value = null;
    return { command: `给《${parsed.bookName}》评分${parsed.score}分` };
  }

  if (pendingFeature.value === 'book_suggestion') {
    pendingFeature.value = null;
    if (/(图书建议|缺书建议|缺书|补书|上架建议|建议补充)/.test(text)) return { command: text };
    return { command: `图书建议：${text}` };
  }

  pendingFeature.value = null;
  if (/(反馈建议|提出建议|系统建议|产品建议)/.test(text)) return { command: text };
  return { command: `反馈建议：${text}` };
}

async function requestChat(command: string): Promise<void> {
  isLoading.value = true;
  const aiMessageIndex = messages.value.push({ role: 'system', content: '' }) - 1;
  let currentResponse = '';
  const controller = new AbortController();

  try {
    await fetchEventSource('http://localhost:8080/polaris/chat', {
      signal: controller.signal,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream'
      },
      body: JSON.stringify({
        memoryId: memoryId.value,
        message: command,
        userId: userId.value
      }),
      onmessage(msg) {
        if (!msg.data) return;
        currentResponse += msg.data;
        messages.value[aiMessageIndex].content = currentResponse;
        void scrollToBottom();
      },
      onclose() {
        isLoading.value = false;
        controller.abort();
        void fetchHistory();
      },
      onerror(err) {
        messages.value[aiMessageIndex].content += '\n[连接出错，请稍后重试]';
        isLoading.value = false;
        throw err;
      }
    });
  } catch {
    isLoading.value = false;
  }
}

async function sendMessage(): Promise<void> {
  const content = inputMessage.value.trim();
  if (!content || isLoading.value) return;

  messages.value.push({ role: 'user', content });
  inputMessage.value = '';
  await scrollToBottom();

  const resolved = resolvePendingCommand(content);
  if (!resolved.command) {
    if (resolved.error) pushSystemMessage(resolved.error);
    return;
  }

  await requestChat(resolved.command);
}

watch(
  () => props.visible,
  (newVal) => {
    if (!newVal) return;
    void fetchHistory();
    void scrollToBottom();
  }
);

onMounted(() => {
  if (props.visible) {
    void fetchHistory();
  }
});
</script>

<style scoped>
.book-card-wrapper {
  position: relative;
  width: 900px;
  height: 800px;
  max-width: 95vw;
  max-height: 90vh;
  cursor: default;
  animation: popIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes popIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.book-shadow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #000;
  z-index: 0;
  transform: translate(8px, 8px);
  border-radius: 2px;
}

.book-body {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  background-color: #faf8f5;
  border: 2px solid #000;
  display: flex;
  flex-direction: row;
  transform: translate(-4px, -4px);
  border-radius: 2px;
}

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

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.ai-chat-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(2px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

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
  font-size: 13px;
  padding: 2px 8px;
  border: 1px solid #000;
  border-radius: 10px;
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

.feature-toolbar {
  padding: 10px 12px;
  border-bottom: 1px solid #000;
  background: #f6f6f6;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.feature-btn {
  border: 1px solid #000;
  background: #fff;
  color: #000;
  border-radius: 14px;
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
}

.feature-btn:hover {
  background: #000;
  color: #fff;
}

.feature-btn.active {
  background: #000;
  color: #fff;
}

.feature-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #faf8f5;
  background-image: linear-gradient(#e5e5e5 1px, transparent 1px),
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
  font-size: 12px;
  flex-shrink: 0;
}

.message .bubble {
  background: #fff;
  padding: 12px 16px;
  border: 1px solid #000;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 2px 2px 0 rgba(0, 0, 0, 0.1);
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

.bubble :deep(a) {
  color: #1f6feb;
  text-decoration: underline;
  cursor: pointer;
}

.bottom-controls {
  background: #fff;
  border-top: 1px solid #000;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 12px 0 12px;
}

.quick-action-btn {
  border: 1px solid #000;
  background: #fff;
  color: #000;
  border-radius: 16px;
  font-size: 12px;
  padding: 5px 10px;
  cursor: pointer;
}

.quick-action-btn:hover {
  background: #000;
  color: #fff;
}

.quick-action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.chat-input-area {
  padding: 10px 15px 15px 15px;
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
  box-shadow: 2px 2px 0 rgba(0, 0, 0, 0.1);
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

