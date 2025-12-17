  <template>
  <div class="app-container">
    <header class="app-header">
      <div class="brand">
        <span class="brand-text">图书馆</span>
        <span class="user-greeting" v-if="username"> / {{ username }}</span>
      </div>
      
      <nav class="app-nav">
        <RouterLink to="/main" class="nav-item" active-class="active">首页</RouterLink>
        <RouterLink to="/userIndex" class="nav-item" active-class="active">仪表盘</RouterLink>
        <RouterLink to="/book" class="nav-item" active-class="active">图书库</RouterLink>
        <RouterLink to="/bookRecommend" class="nav-item" active-class="active">推荐</RouterLink>
        <RouterLink to="/suggest" class="nav-item" active-class="active">建议</RouterLink>
        <RouterLink to="/user" class="nav-item" active-class="active">个人中心</RouterLink>
      </nav>
    </header>

    <main class="app-main">
      <RouterView v-slot="{ Component }">
        <component :is="Component" :key="$route.fullPath" />
      </RouterView>
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterView, RouterLink } from "vue-router";
import { onMounted, ref } from "vue";
import bus from "../bus/index";
import { ElMessage } from "element-plus";
import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()
const username = ref('')

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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  background: transparent;
  flex-shrink: 0;
}

.brand {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1px;
}

.user-greeting {
  font-weight: 400;
  color: #888;
}

.app-nav {
  display: flex;
  gap: 40px;
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
