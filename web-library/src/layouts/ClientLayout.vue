  <template>
  <div class="app-container">
    <header class="app-header" v-if="!hideNav">
      <div class="brand-group">
        <div class="brand">
          <span class="brand-text">图书馆</span>
          <span class="user-greeting" v-if="username"> / {{ username }}</span>
        </div>
      </div>
      
      <div class="nav-center">
        <nav class="app-nav">
          <RouterLink to="/main" class="nav-item" active-class="active">首页</RouterLink>
          <RouterLink to="/userIndex" class="nav-item" active-class="active">仪表盘</RouterLink>
          <RouterLink to="/book" class="nav-item" active-class="active">图书库</RouterLink>
          
          <!-- AI Assistant Button Centered in Nav -->
          <button class="ai-nav-btn" @click="showAiChat = true">
            <svg class="ai-icon-svg" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L14.4 7.2L20 9.6L14.4 12L12 17.2L9.6 12L4 9.6L9.6 7.2L12 2Z" fill="currentColor"/>
              <path d="M19 15L20.2 17.4L23 18.6L20.2 19.8L19 22.2L17.8 19.8L15 18.6L17.8 17.4L19 15Z" fill="currentColor"/>
              <path d="M5 16L5.8 17.6L7.4 18.4L5.8 19.2L5 20.8L4.2 19.2L2.6 18.4L4.2 17.6L5 16Z" fill="currentColor"/>
            </svg>
            <span class="ai-text">AI 助手</span>
          </button>
          
          <RouterLink to="/bookshelf" class="nav-item" active-class="active">我的书架</RouterLink>
          <RouterLink to="/bookRecommend" class="nav-item" active-class="active">推荐</RouterLink>
          <RouterLink to="/suggest" class="nav-item" active-class="active">建议</RouterLink>
          <RouterLink to="/user" class="nav-item" active-class="active">个人中心</RouterLink>
        </nav>
      </div>

      <div class="header-right">
         <!-- Right placeholder -->
      </div>
    </header>

    <main class="app-main">
      <RouterView v-slot="{ Component }">
        <component :is="Component" :key="$route.fullPath" />
      </RouterView>
    </main>
    
    <AiChatDialog :visible="showAiChat" @close="showAiChat = false" />
  </div>
</template>

<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from "vue-router";
import { onMounted, ref, computed } from "vue";
import bus from "@/bus/index";
import { ElMessage } from "element-plus";
import { useCookies } from '@vueuse/integrations/useCookies'
import AiChatDialog from '@/components/common/AiChatDialog.vue'

const cookie = useCookies()
const username = ref('')
const route = useRoute();
const showAiChat = ref(false)

const hideNav = computed(() => {
  return route.path.startsWith('/category/');
});

onMounted(() => {
  username.value = cookie.get('username')
  bus.on('msg', (val: any) => { ElMessage.info('val==>', val.username) })
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&display=swap');

.app-container {
  height: 100vh;
  background-color: #f9f9f9;
  font-family: 'Inter', sans-serif;
  color: #1a1a1a;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-header {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  padding: 20px 40px;
  background: transparent;
  flex-shrink: 0;
}

.brand-group {
  display: flex;
  align-items: center;
  justify-self: start;
}

.nav-center {
  justify-self: center;
}

.header-right {
  justify-self: end;
}

.brand {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1px;
}

.ai-nav-btn {
  background: #000;
  border: 1px solid #000;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  font-family: 'Inter', sans-serif;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  padding: 6px 16px;
  border-radius: 20px;
  margin: 0 10px;
  position: relative;
  overflow: hidden;
}

.ai-nav-btn:hover {
  background: #fff;
  color: #000;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.ai-nav-btn:active {
  transform: translateY(0);
}

.ai-icon-svg {
  width: 14px;
  height: 14px;
  transition: transform 0.4s ease;
}

.ai-nav-btn:hover .ai-icon-svg {
  transform: rotate(15deg) scale(1.1);
}

.ai-text {
  letter-spacing: 0.5px;
}

.user-greeting {
  font-weight: 400;
  color: #888;
}

.app-nav {
  display: flex;
  gap: 30px;
  align-items: center;
}

.nav-item {
  text-decoration: none;
  color: #888;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover {
  color: #000;
}

.nav-item.active {
  color: #000;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 100%;
  height: 1px;
  background-color: #000;
}

.app-main {
  flex: 1;
  padding: 0 0px 0px 0px;
  width: 100%;
  margin: 0 auto;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
