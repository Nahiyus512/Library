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
            <div class="quick-actions">
              <button
                v-for="option in featureOptions"
                :key="option.id"
                class="quick-action-btn"
                :disabled="isLoading"
                @click="onOptionClick(option.command)"
              >
                {{ option.label }}
              </button>
            </div>

            <div class="chat-input-area">
              <input
                v-model="inputMessage"
                type="text"
                class="chat-input"
                :disabled="isLoading"
                placeholder="输入问题或数字选项"
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
import myAxios from '@/api/index';
import { books, type BookItem } from '@/data/books';

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

interface BackendBook {
  bookId: number;
  bookName: string;
  bookAuthor: string;
}

interface FeatureDashboardBookItem {
  bookId?: number;
  bookName: string;
  enterUv?: number;
  completeRate?: number;
  like2Count?: number;
  acceptRate?: number;
}

interface FeatureOption {
  id: string;
  label: string;
  command: string;
}

type PendingAction = 'search' | 'advice' | 'score' | 'like' | null;

type ConfirmContext =
  | { type: 'advice'; info: string }
  | { type: 'score'; keyword: string; score: number }
  | { type: 'like'; keyword: string; likeLevel: number; likeLabel: string };

const router = useRouter();
const cookie = useCookies();

const featureOptions: FeatureOption[] = [
  { id: 'recommend', label: '1 推荐特色页面', command: '1' },
  { id: 'search', label: '2 检索图书', command: '2' },
  { id: 'chat', label: '3 自由聊天', command: '3' },
  { id: 'advice', label: '4 缺书建议', command: '4' },
  { id: 'score', label: '5 图书评分', command: '5' },
  { id: 'like', label: '6 喜好调整', command: '6' }
];

const messages = ref<Message[]>([{ role: 'system', content: buildWelcomeMessage() }]);
const historyList = ref<ChatSession[]>([]);
const inputMessage = ref('');
const isLoading = ref(false);
const chatContentRef = ref<HTMLElement | null>(null);
const userId = ref<string>('');
const userName = ref<string>('');
const memoryId = ref<number>(Date.now());
const pendingAction = ref<PendingAction>(null);
const confirmContext = ref<ConfirmContext | null>(null);

function buildWelcomeMessage(): string {
  return [
    '你好，我是 Polaris 图书助手。',
    '',
    '你可以选择下列功能或提出问题：',
    '1. 推荐有特色页面的书（可点击跳转）',
    '2. 检索系统里有什么书',
    '3. 自由聊天',
    '4. 提交缺书/特色页面建议（入库）',
    '5. 给书评分（1-5分）',
    '6. 调整喜好（不想看/一般/想看）',
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

function pushSystemMessage(content: string): void {
  messages.value.push({ role: 'system', content });
}

function close(): void {
  emit('close');
}

function onOptionClick(command: string): void {
  if (isLoading.value) return;
  inputMessage.value = command;
  void sendMessage();
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
  pendingAction.value = null;
  confirmContext.value = null;
  messages.value = [{ role: 'system', content: buildWelcomeMessage() }];
}

async function fetchHistory(): Promise<void> {
  userId.value = cookie.get('userId') || '10001';
  userName.value = cookie.get('username') || `user_${userId.value}`;
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

function normalizeName(name: string): string {
  return name.toLowerCase().replace(/\s+/g, '');
}

function formatDate(date: Date): string {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

function findLocalFeatureBookByName(name: string): BookItem | null {
  const n = normalizeName(name);
  const hit = books.find((b) => {
    const cn = normalizeName(b.titleCN || '');
    const en = normalizeName(b.title || '');
    return cn === n || en === n || cn.includes(n) || en.includes(n) || n.includes(cn) || n.includes(en);
  });
  return hit || null;
}

async function fetchFeatureRankedBooks(): Promise<Array<{ book: BookItem; metric?: FeatureDashboardBookItem }>> {
  const today = new Date();
  const fromDate = new Date(today);
  fromDate.setDate(today.getDate() - 29);

  try {
    const res = await myAxios.get('/feature/dashboard/books', {
      params: {
        from: formatDate(fromDate),
        to: formatDate(today),
        sort: 'acceptRate',
        limit: 100
      }
    });
    const rows = (res?.data?.data || []) as FeatureDashboardBookItem[];
    const ranked: Array<{ book: BookItem; metric?: FeatureDashboardBookItem }> = [];
    const used = new Set<string>();

    rows.forEach((row) => {
      const local = findLocalFeatureBookByName(String(row.bookName || '').trim());
      if (!local || used.has(local.path)) return;
      used.add(local.path);
      ranked.push({ book: local, metric: row });
    });

    books.forEach((book) => {
      if (used.has(book.path)) return;
      ranked.push({ book });
    });

    return ranked;
  } catch {
    return books.map((book) => ({ book }));
  }
}

function sanitizeKeyword(raw: string): string {
  return raw.replace(/[《》"'“”]/g, '').trim();
}

function findFeaturePath(bookName: string): string | null {
  const n = normalizeName(bookName);
  const local = books.find((b) => {
    const cn = normalizeName(b.titleCN || '');
    const en = normalizeName(b.title || '');
    return cn.includes(n) || en.includes(n) || n.includes(cn) || n.includes(en);
  });
  return local?.path || null;
}

function getCurrentUserId(): number {
  const parsed = Number(userId.value || cookie.get('userId') || 10001);
  return Number.isNaN(parsed) ? 10001 : parsed;
}

function parseLikeLevel(raw: string): number | null {
  const map: Record<string, number> = {
    '0': 0,
    '1': 1,
    '2': 2,
    不想看: 0,
    一般: 1,
    还行: 1,
    想看: 2,
    喜欢: 2
  };
  return map[raw.trim()] ?? null;
}

function likeLabel(level: number): string {
  if (level === 2) return '想看';
  if (level === 1) return '一般';
  return '不想看';
}

function isConfirmYes(text: string): boolean {
  const t = text.trim().toLowerCase();
  return ['1', '是', '确认', '确定', '好', '好的', 'yes', 'y'].includes(t);
}

function isConfirmNo(text: string): boolean {
  const t = text.trim().toLowerCase();
  return ['2', '否', '取消', '不用', 'no', 'n'].includes(t);
}

function parseScoreInput(raw: string): { keyword: string; score: number } | null {
  const match = raw.match(/^(.+?)\s*([1-5])\s*分?$/);
  if (!match) return null;
  return { keyword: sanitizeKeyword(match[1]), score: Number(match[2]) };
}

function parseLikeInput(raw: string): { keyword: string; likeLevel: number; likeLabel: string } | null {
  const m = raw.match(/^(.+?)(想看|不想看|一般|还行|喜欢|[012])$/);
  if (!m) return null;
  const keyword = sanitizeKeyword(m[1]);
  const lvl = parseLikeLevel(m[2]);
  if (lvl === null) return null;
  return { keyword, likeLevel: lvl, likeLabel: likeLabel(lvl) };
}

function parseNaturalAdvice(raw: string): string | null {
  const t = raw.trim();
  if (!t) return null;
  const hasAdviceVerb = /(补充|增加|新增|缺少|没有|想要|补上|加入)/.test(t);
  const hasBookHint = /(书|图书|页面|特色|数据)/.test(t);
  if (hasAdviceVerb && hasBookHint) return t;
  if (/^补充.+/.test(t)) return t;
  return null;
}

function extractBookFromAdviceText(raw: string): string | null {
  const text = raw.trim();
  const wrapped = text.match(/《([^》]+)》/);
  if (wrapped?.[1]) return wrapped[1].trim();

  const compact = text.replace(/\s+/g, '');
  if (/^[A-Za-z0-9\u4e00-\u9fa5]{2,30}$/.test(compact)) {
    return compact;
  }

  const verbLead = compact.match(/^(补充|增加|新增|补上|想要|加入|添加)([A-Za-z0-9\u4e00-\u9fa5]{2,30})$/);
  if (verbLead?.[2]) return verbLead[2].trim();

  return null;
}

function normalizeAdviceInfo(raw: string): string {
  const text = raw.trim();
  if (!text) return '';

  const book = extractBookFromAdviceText(text);
  const hasPageHint = /(特色页面|页面)/.test(text);
  const hasDataHint = /(图书数据|数据|图书)/.test(text);
  const hasAdviceVerb = /(补充|增加|新增|缺少|没有|想要|补上|加入|添加)/.test(text);

  if (book && !hasAdviceVerb && !hasPageHint && !hasDataHint) {
    return `建议补充《${book}》的图书数据与特色页面。`;
  }
  if (book && hasAdviceVerb && !hasPageHint && !hasDataHint) {
    return `建议补充《${book}》的图书数据与特色页面。`;
  }
  if (book && hasPageHint && !hasDataHint) {
    return `建议补充《${book}》的特色页面。`;
  }
  if (book && hasDataHint && !hasPageHint) {
    return `建议补充《${book}》的图书数据。`;
  }
  if (!/^建议[:：]?/.test(text)) {
    return `建议：${text}`;
  }
  return text;
}

function askConfirmAdvice(info: string): void {
  const normalized = normalizeAdviceInfo(info);
  confirmContext.value = { type: 'advice', info: normalized };
  pushSystemMessage(`是否要提交这条建议并入库？\n> ${normalized}\n\n1. 是，提交\n2. 否，取消`);
}

function askConfirmScore(keyword: string, score: number): void {
  confirmContext.value = { type: 'score', keyword, score };
  pushSystemMessage(`是否给《${keyword}》评分 ${score} 分？\n\n1. 是，提交评分\n2. 否，取消`);
}

function askConfirmLike(keyword: string, level: number, label: string): void {
  confirmContext.value = { type: 'like', keyword, likeLevel: level, likeLabel: label };
  pushSystemMessage(`是否将《${keyword}》设置为“${label}”？\n\n1. 是，更新喜好\n2. 否，取消`);
}

async function queryBookByName(keyword: string): Promise<BackendBook[]> {
  const res = await myAxios.get('/book/get', { params: { name: keyword } });
  const list = res?.data?.data;
  return Array.isArray(list) ? (list as BackendBook[]) : [];
}

async function handleFeatureRecommend(): Promise<void> {
  const picks = await fetchFeatureRankedBooks();
  const lines = picks.slice(0, 6).map(({ book, metric }) => {
    if (!metric) {
      return `- [${book.titleCN || book.title}](${book.path}) · ${book.author}`;
    }
    const completeRate = `${Math.round((Number(metric.completeRate || 0) || 0) * 100)}%`;
    const acceptRate = `${Math.round((Number(metric.acceptRate || 0) || 0) * 100)}%`;
    return `- [${book.titleCN || book.title}](${book.path}) · ${book.author}（完成率 ${completeRate}，接受率 ${acceptRate}）`;
  });
  pushSystemMessage([
    '这些书有特色页面，已按“特色页分析高表现”优先排序；该信号也会同步用于阅读决策推荐加权：',
    '',
    ...lines,
    '',
    '- [进入阅读决策](/readingDecision)'
  ].join('\n'));
}

async function handleBookSearch(keyword: string): Promise<void> {
  if (!keyword) {
    pushSystemMessage('请输入检索关键词。');
    return;
  }
  isLoading.value = true;
  try {
    const list = await queryBookByName(keyword);
    if (!list.length) {
      pushSystemMessage(`未检索到“${keyword}”。你可以输入“补充${keyword}”提交建议。`);
      return;
    }
    const lines = list.slice(0, 8).map((b) => {
      const path = findFeaturePath(b.bookName);
      const jump = path ? ` | [特色页面](${path})` : '';
      return `- ${b.bookName} · ${b.bookAuthor || '未知作者'}${jump}`;
    });
    pushSystemMessage([`检索到 ${list.length} 本相关图书：`, '', ...lines].join('\n'));
  } catch {
    pushSystemMessage('检索失败，请稍后再试。');
  } finally {
    isLoading.value = false;
  }
}

async function executeAdvice(info: string): Promise<void> {
  isLoading.value = true;
  try {
    const res = await myAxios.post('/advice/input', {
      info,
      userName: userName.value || cookie.get('username') || `user_${getCurrentUserId()}`
    });
    if (res?.data?.code === 200) {
      pushSystemMessage('建议已记录到系统数据库。');
    } else {
      pushSystemMessage(`建议提交失败：${res?.data?.msg || '未知错误'}`);
    }
  } catch {
    pushSystemMessage('建议提交失败，请稍后重试。');
  } finally {
    isLoading.value = false;
  }
}

async function executeScore(keyword: string, score: number): Promise<void> {
  isLoading.value = true;
  try {
    const booksList = await queryBookByName(keyword);
    const matched = booksList[0];
    if (!matched) {
      pushSystemMessage(`未找到“${keyword}”，无法评分。`);
      return;
    }
    const res = await myAxios.put('/bookScore/updateScore', {
      userId: getCurrentUserId(),
      bookId: matched.bookId,
      score
    });
    if (res?.data?.code === 200) {
      pushSystemMessage(`已为《${matched.bookName}》打分：${score} 分。`);
    } else {
      pushSystemMessage(`评分失败：${res?.data?.msg || '未知错误'}`);
    }
  } catch {
    pushSystemMessage('评分失败，请稍后重试。');
  } finally {
    isLoading.value = false;
  }
}

async function executeLike(keyword: string, level: number, label: string): Promise<void> {
  isLoading.value = true;
  try {
    const booksList = await queryBookByName(keyword);
    const matched = booksList[0];
    if (!matched) {
      pushSystemMessage(`未找到“${keyword}”，无法更新喜好。`);
      return;
    }
    const res = await myAxios.put('/bookLike/like', {
      userId: getCurrentUserId(),
      userName: userName.value || cookie.get('username') || `user_${getCurrentUserId()}`,
      bookId: matched.bookId,
      bookName: matched.bookName,
      likeLevel: level
    });
    if (res?.data?.code === 200) {
      pushSystemMessage(`已将《${matched.bookName}》设置为“${label}”。`);
    } else {
      pushSystemMessage(`喜好更新失败：${res?.data?.msg || '未知错误'}`);
    }
  } catch {
    pushSystemMessage('喜好更新失败，请稍后重试。');
  } finally {
    isLoading.value = false;
  }
}

async function handlePendingConfirm(text: string): Promise<boolean> {
  if (!confirmContext.value) return false;
  if (!isConfirmYes(text) && !isConfirmNo(text)) {
    pushSystemMessage('请回复 `1`（确认）或 `2`（取消）。');
    return true;
  }

  const context = confirmContext.value;
  confirmContext.value = null;
  if (isConfirmNo(text)) {
    pushSystemMessage('已取消。你可以继续输入其他需求。');
    return true;
  }

  if (context.type === 'advice') {
    await executeAdvice(context.info);
    return true;
  }
  if (context.type === 'score') {
    await executeScore(context.keyword, context.score);
    return true;
  }
  await executeLike(context.keyword, context.likeLevel, context.likeLabel);
  return true;
}

async function handleFeatureInput(content: string): Promise<boolean> {
  const text = content.trim();
  if (!text) return true;

  if (await handlePendingConfirm(text)) {
    return true;
  }

  const normalized = text.replace(/\s+/g, '').toLowerCase();
  if (['菜单', 'help', '功能', '选项', '0'].includes(normalized)) {
    pendingAction.value = null;
    confirmContext.value = null;
    pushSystemMessage(buildWelcomeMessage());
    return true;
  }

  if (pendingAction.value === 'search') {
    pendingAction.value = null;
    await handleBookSearch(text);
    return true;
  }
  if (pendingAction.value === 'advice') {
    pendingAction.value = null;
    askConfirmAdvice(text);
    return true;
  }
  if (pendingAction.value === 'score') {
    pendingAction.value = null;
    const parsed = parseScoreInput(text);
    if (!parsed) {
      pushSystemMessage('格式示例：`三体 5分`。');
      return true;
    }
    askConfirmScore(parsed.keyword, parsed.score);
    return true;
  }
  if (pendingAction.value === 'like') {
    pendingAction.value = null;
    const parsed = parseLikeInput(text);
    if (!parsed) {
      pushSystemMessage('格式示例：`三体想看` 或 `三体 一般`。');
      return true;
    }
    askConfirmLike(parsed.keyword, parsed.likeLevel, parsed.likeLabel);
    return true;
  }

  if (text === '1' || text === '推荐') {
    await handleFeatureRecommend();
    return true;
  }
  if (text === '2' || text.startsWith('2 ')) {
    const keyword = text.slice(1).trim();
    if (!keyword) {
      pendingAction.value = 'search';
      pushSystemMessage('请输入要检索的书名关键字。');
    } else {
      await handleBookSearch(keyword);
    }
    return true;
  }
  if (text === '3' || text === '自由聊天' || text === '系统交互') {
    pushSystemMessage('已进入自由聊天模式。你可以直接提问。');
    return true;
  }
  if (text === '4' || text.startsWith('4 ')) {
    const adviceText = text.slice(1).trim();
    if (!adviceText) {
      pendingAction.value = 'advice';
      pushSystemMessage('请说出你想补充的书籍或特色页面需求。');
    } else {
      askConfirmAdvice(adviceText);
    }
    return true;
  }
  if (text === '5' || text.startsWith('5 ')) {
    const payload = text.slice(1).trim();
    if (!payload) {
      pendingAction.value = 'score';
      pushSystemMessage('请输入评分，例如：`三体 5分`。');
    } else {
      const parsed = parseScoreInput(payload);
      if (!parsed) {
        pushSystemMessage('评分格式错误，请用：`书名 分数`。');
      } else {
        askConfirmScore(parsed.keyword, parsed.score);
      }
    }
    return true;
  }
  if (text === '6' || text.startsWith('6 ')) {
    const payload = text.slice(1).trim();
    if (!payload) {
      pendingAction.value = 'like';
      pushSystemMessage('请输入喜好，例如：`三体想看`、`三体一般`、`三体不想看`。');
    } else {
      const parsed = parseLikeInput(payload);
      if (!parsed) {
        pushSystemMessage('喜好格式错误，请用：`书名 + 想看/一般/不想看`。');
      } else {
        askConfirmLike(parsed.keyword, parsed.likeLevel, parsed.likeLabel);
      }
    }
    return true;
  }

  if (/^检索\s+/.test(text)) {
    await handleBookSearch(text.replace(/^检索\s+/, '').trim());
    return true;
  }
  if (/^建议\s+/.test(text)) {
    askConfirmAdvice(text.replace(/^建议\s+/, '').trim());
    return true;
  }

  const scoreNatural = parseScoreInput(text);
  if (scoreNatural) {
    askConfirmScore(scoreNatural.keyword, scoreNatural.score);
    return true;
  }

  const likeNatural = parseLikeInput(text);
  if (likeNatural) {
    askConfirmLike(likeNatural.keyword, likeNatural.likeLevel, likeNatural.likeLabel);
    return true;
  }

  const adviceNatural = parseNaturalAdvice(text);
  if (adviceNatural) {
    askConfirmAdvice(adviceNatural);
    return true;
  }

  return false;
}

async function sendMessage(): Promise<void> {
  const content = inputMessage.value.trim();
  if (!content || isLoading.value) return;

  messages.value.push({ role: 'user', content });
  inputMessage.value = '';
  await scrollToBottom();

  const handled = await handleFeatureInput(content);
  if (handled) {
    await scrollToBottom();
    return;
  }

  isLoading.value = true;
  const aiMessageIndex = messages.value.push({ role: 'system', content: '' }) - 1;
  let currentResponse = '';

  try {
    await fetchEventSource('http://localhost:8080/polaris/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream'
      },
      body: JSON.stringify({
        memoryId: memoryId.value,
        message: content,
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
