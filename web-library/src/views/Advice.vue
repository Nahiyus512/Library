<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">意见反馈</h2>
        <span class="page-subtitle">查看并回复用户的建议与反馈</span>
      </div>
      <div class="header-right">
        <el-button color="#000" @click="getAdvice">
          <el-icon class="el-icon--left"><Refresh /></el-icon>刷新列表
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <el-table :data="adviceData" style="width: 100%; flex: 1;" height="100%" stripe border>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="info" label="建议内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="infoTime" label="建议时间" width="180" align="center" />
        <el-table-column prop="reply" label="回复内容" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.reply">{{ scope.row.reply }}</span>
            <el-tag v-else type="info" effect="plain" style="color: #000; border-color: #000;" size="small">暂无回复</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="replyTime" label="回复时间" width="180" align="center" />
        <el-table-column fixed="right" label="操作" width="100" align="center">
          <template #default="scope">
            <el-button color="#000" link @click="refMsg(scope.row)">
              <el-icon><EditPen /></el-icon> 回复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog 
      v-model="dialogReply" 
      title="回复建议" 
      width="500px" 
      destroy-on-close
    >
      <div class="reply-form">
        <div class="user-suggestion">
          <p class="label">用户 {{ rowData.userName }} 的建议：</p>
          <div class="suggestion-content">
            {{ rowData.info }}
          </div>
        </div>
        
        <div class="admin-reply">
          <p class="label">回复内容：</p>
          <el-input
            v-model="rowData.reply"
            type="textarea"
            :rows="4"
            placeholder="请输入您的回复..."
          />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogReply = false">取消</el-button>
          <el-button color="#000" @click="clickOk">确定回复</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import myAxios from "../axios/index.js";
import { ElMessage } from "element-plus";
import { Refresh, EditPen } from '@element-plus/icons-vue';

const adviceData = ref([])
const dialogReply = ref(false)

const rowData = reactive({
  id: '',
  userName: '',
  info: '',
  infoTime: '',
  reply: '',
  replyTime: ''
})

onMounted(() => {
  getAdvice()
})

const refMsg = (row: any) => {
  dialogReply.value = true
  rowData.id = row.id
  rowData.userName = row.userName
  rowData.info = row.info
  rowData.infoTime = row.infoTime
  rowData.reply = row.reply || '' // Default to empty string if null
}

async function getAdvice() {
  try {
    let res = await myAxios.get('http://localhost:8080/advice/getAll')
    adviceData.value = res.data.data
  } catch (e) {
    console.log(e)
    ElMessage.error('获取数据失败')
  }
}

async function clickOk() {
  if (!rowData.reply) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  
  try {
    let res = await myAxios.post('http://localhost:8080/advice/reply', {
      id: rowData.id,
      reply: rowData.reply,
    })
    if (res.data.code === 200) {
      await getAdvice();
      dialogReply.value = false
      ElMessage.success('回复成功')
    } else {
      ElMessage.error('回复失败')
    }
  } catch (e) {
    console.log(e)
    ElMessage.error('系统错误')
  }
}
</script>

<style scoped>
.page-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  box-sizing: border-box;
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
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.user-suggestion {
  margin-bottom: 20px;
}

.label {
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.suggestion-content {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  color: #303133;
  line-height: 1.5;
}
</style>
