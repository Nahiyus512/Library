<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">系统设置</h2>
        <span class="page-subtitle">管理系统配置和个人会话</span>
      </div>
    </div>

    <div class="content-container">
      <el-row :gutter="20">
        <el-col :span="12" :xs="24">
          <div class="setting-card">
            <div class="card-header">
              <h3>关于系统</h3>
            </div>
            <div class="card-body">
              <div class="info-item">
                <span class="label">系统名称</span>
                <span class="value">图书馆管理系统</span>
              </div>
              <div class="info-item">
                <span class="label">当前版本</span>
                <span class="value">v1.0.0</span>
              </div>
              <div class="info-item">
                <span class="label">开发状态</span>
                <span class="value"><el-tag type="info" effect="plain" style="color: #000; border-color: #000;" size="small">运行中</el-tag></span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="12" :xs="24">
          <div class="setting-card">
            <div class="card-header">
              <h3>会话管理</h3>
            </div>
            <div class="card-body">
              <p class="description">退出当前登录的管理员账号，返回登录页面。</p>
              <div class="action-area">
                <el-button color="#000" plain @click="exit">
                  <el-icon class="el-icon--left"><SwitchButton /></el-icon>退出登录
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useCookies } from "@vueuse/integrations/useCookies";
import { useRouter } from "vue-router";
import { SwitchButton } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';

const cookie = useCookies()
const router = useRouter();

const exit = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      cookie.remove('adminId')
      cookie.remove('token')
      ElMessage.success('已安全退出');
      setTimeout(() => {
        router.push('/login')
      }, 500);
    })
    .catch(() => {
      // cancel
    })
}
</script>

<style scoped>
.page-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  display: block;
  margin-top: 5px;
  color: #909399;
  font-size: 14px;
}

.content-container {
  flex: 1;
}

.setting-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  overflow: hidden;
}

.card-header {
  padding: 15px 20px;
  border-bottom: 1px solid #EBEEF5;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-body {
  padding: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #606266;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
}

.description {
  color: #606266;
  margin-bottom: 20px;
  line-height: 1.5;
}

.action-area {
  display: flex;
  justify-content: flex-end;
}
</style>
