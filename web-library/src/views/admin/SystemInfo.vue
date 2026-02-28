<template>
  <div class="system-info-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">系统设置</h2>
        <span class="page-subtitle">管理系统配置和个人会话</span>
      </div>
    </div>

    <div class="content-card">
      <el-row :gutter="20">
        <el-col :span="12" :xs="24">
          <div class="setting-card">
            <div class="card-header">
              <h3>关于系统</h3>
            </div>
            <div class="card-body">
              <div class="info-item">
                <span class="label">系统名称</span>
                <span class="value">基于 AgenticAI 的人机协同阅读决策支持系统</span>
              </div>
              <div class="info-item">
                <span class="label">当前版本</span>
                <span class="value">v1.0.0</span>
              </div>
              <div class="info-item">
                <span class="label">开发状态</span>
                <span class="value"><el-tag class="running-tag" type="info" effect="plain">运行中</el-tag></span>
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
                <el-button class="logout-btn" @click="exit">
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

<style lang="scss" scoped>
.system-info-container {
  padding: 24px;
  background-color: #f5f7fa;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    flex-shrink: 0;

    .header-left {
      .page-title {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin: 0;
        margin-bottom: 8px;
      }

      .page-subtitle {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .content-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.05);
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
}

.content-card :deep(.el-row) {
  height: 100%;
}

.content-card :deep(.el-col) {
  display: flex;
}

.setting-card {
  background: #fff;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  margin-bottom: 0;
  width: 100%;
  height: 100%;
  transition: all 0.3s;
  
  &:hover {
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  }
  
  .card-header {
    padding: 16px 20px;
    border-bottom: 1px solid #ebeef5;
    
    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }
  
  .card-body {
    padding: 20px;
    
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .label {
        color: #606266;
      }
      
      .value {
        color: #303133;
        font-weight: 500;
      }
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
  }
}

.running-tag {
  color: #000 !important;
  border-color: #000 !important;
  height: 32px;
  line-height: 30px;
  padding: 0 14px;
  font-size: 14px;
}

.logout-btn {
  background: #fff;
  border-color: #000;
  color: #000;
}

.logout-btn:hover,
.logout-btn:focus {
  background: #f5f5f5;
  border-color: #000;
  color: #000;
}
</style>
