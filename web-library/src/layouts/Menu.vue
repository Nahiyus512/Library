<template>
  <div class="admin-menu">
    <div class="menu-header">
      <h3>系统管理</h3>
    </div>
    <el-menu
        class="minimal-menu"
        :default-active="activePath"
        router
    >
      <el-menu-item index="/userInfo">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      
      <el-menu-item index="/bookInfo">
        <el-icon><Notebook /></el-icon>
        <span>图书管理</span>
      </el-menu-item>
      
      <el-menu-item index="/bookClass">
        <el-icon><CollectionTag /></el-icon>
        <span>分类管理</span>
      </el-menu-item>
      
      <el-menu-item index="/bookLikeInfo">
        <el-icon><Star /></el-icon>
        <span>书架管理</span>
      </el-menu-item>
      
      <el-menu-item index="/advice">
        <el-icon><ChatDotRound /></el-icon>
        <span>意见反馈</span>
      </el-menu-item>
      
      <el-menu-item index="/sysInfo">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import {
  User,
  Notebook,
  CollectionTag,
  Tickets,
  ChatDotRound,
  Setting,
  Star,
} from '@element-plus/icons-vue'

const route = useRoute();
const activePath = computed(() => route.path);
</script>

<style scoped>
.admin-menu {
  height: 100%;
  background: #f5f5f5;
  border-right: 1px solid #eee;
  padding-top: 20px;
  box-sizing: border-box;
  overflow-y: auto;
}

/* Hide scrollbar for Chrome, Safari and Opera */
.admin-menu::-webkit-scrollbar {
  display: none;
}

/* Hide scrollbar for IE, Edge and Firefox */
.admin-menu {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

.menu-header {
  padding: 0 20px 30px;
  margin-bottom: 10px;
}

.menu-header h3 {
  font-family: 'Times New Roman', Times, serif;
  font-size: 24px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: #333;
  margin: 0;
  text-shadow: 1px 1px 0 rgba(0,0,0,0.1);
}

.minimal-menu {
  border-right: none !important;
  background: transparent !important;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  background: transparent !important;
}

:deep(.el-menu-item:hover) {
  color: #000 !important;
  background: rgba(0,0,0,0.03) !important;
}

:deep(.el-menu-item.is-active) {
  color: #000 !important;
  background: #fff !important;
  font-weight: 600;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

:deep(.el-icon) {
  font-size: 16px;
  margin-right: 10px;
}
</style>
