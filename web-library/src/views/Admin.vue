<template>
  <div class="admin-layout">
    <el-container class="main-container">
      <el-aside width="240px" class="admin-aside">
        <Menu />
      </el-aside>
      
      <el-container>
        <el-header class="admin-header">
          <div class="header-content">
            <span class="page-title">后台管理系统</span>
            <div class="user-info">
              <span class="user-name" v-if="username">{{ username }}</span>
              <span class="user-role">管理员</span>
            </div>
          </div>
        </el-header>
        
        <el-main class="admin-content">
          <RouterView v-slot="{ Component }">
            <component :is="Component" :key="$route.fullPath" />
          </RouterView>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { RouterView, useRouter } from "vue-router";
import { useCookies } from '@vueuse/integrations/useCookies'
import Menu from './Menu.vue'
import { onMounted, ref } from "vue";

const router = useRouter();
const cookie = useCookies()
const username = ref('')

onMounted(() => {
  let pass = cookie.get('adminId')
  username.value = cookie.get('username')
  if (pass == null) {
    router.push('/login')
  }
})
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&display=swap');

.admin-layout {
  height: 100vh;
  width: 100vw;
  font-family: 'Inter', sans-serif;
  background-color: #fff;
  overflow: hidden;
}

.main-container {
  height: 100%;
}

.admin-aside {
  background-color: #f5f5f5;
  border-right: 1px solid #eee;
  overflow: hidden;
}

.admin-content {
  padding: 0;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.admin-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 0 40px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #000;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.user-name {
  font-weight: 500;
  color: #000;
}

.user-role {
  color: #999;
  font-size: 12px;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 12px;
}

.admin-content {
  background: #fff;
  padding: 0; /* Remove padding to let children control layout */
  overflow: hidden; /* Prevent window scroll */
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
