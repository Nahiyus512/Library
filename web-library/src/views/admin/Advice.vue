<template>
  <div class="advice-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">意见反馈</h2>
        <span class="page-subtitle">查看并回复用户建议与图书评论</span>
      </div>
      <div class="header-right">
        <el-button color="#000" @click="refreshData">
          <el-icon class="el-icon--left"><Refresh /></el-icon>刷新列表
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="留言建议" name="suggestion">
          <el-table :data="suggestionData" style="width: 100%" height="100%" stripe border>
            <el-table-column type="expand">
              <template #default="props">
                <div style="padding: 10px 20px">
                  <el-table :data="props.row.items" border>
                    <el-table-column prop="id" label="ID" width="80" align="center" />
                    <el-table-column prop="info" label="建议内容" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="infoTime" label="建议时间" width="180" align="center" />
                    <el-table-column prop="reply" label="回复内容" min-width="200" show-overflow-tooltip>
                      <template #default="scope">
                        <span v-if="scope.row.reply">{{ scope.row.reply }}</span>
                        <el-tag v-else type="info" effect="plain" style="color: #000; border-color: #000" size="small">暂无回复</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="replyTime" label="回复时间" width="180" align="center" />
                    <el-table-column label="操作" width="100" align="center">
                      <template #default="scope">
                        <el-button color="#000" link size="small" @click="openReplyDialog(scope.row)">
                          <el-icon><EditPen /></el-icon>回复
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户" />
            <el-table-column label="留言数量" width="120" align="center">
              <template #default="scope">
                <el-tag effect="plain" round type="info" style="color: #000; border-color: #000">{{ scope.row.items.length }} 条</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="图书评论" name="review">
          <el-table :data="reviewData" style="width: 100%" height="100%" stripe border>
            <el-table-column type="expand">
              <template #default="props">
                <div style="padding: 10px 20px">
                  <el-table :data="props.row.items" border>
                    <el-table-column prop="id" label="ID" width="80" align="center" />
                    <el-table-column prop="bookName" label="图书名称" width="150" show-overflow-tooltip />
                    <el-table-column prop="info" label="评论内容" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="infoTime" label="评论时间" width="180" align="center" />
                    <el-table-column prop="reply" label="回复内容" min-width="200" show-overflow-tooltip>
                      <template #default="scope">
                        <span v-if="scope.row.reply">{{ scope.row.reply }}</span>
                        <el-tag v-else type="info" effect="plain" style="color: #000; border-color: #000" size="small">暂无回复</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="replyTime" label="回复时间" width="180" align="center" />
                    <el-table-column label="操作" width="100" align="center">
                      <template #default="scope">
                        <el-button color="#000" link size="small" @click="openReplyDialog(scope.row)">
                          <el-icon><EditPen /></el-icon>回复
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户" />
            <el-table-column label="评论数量" width="120" align="center">
              <template #default="scope">
                <el-tag effect="plain" round type="info" style="color: #000; border-color: #000">{{ scope.row.items.length }} 条</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="dialogReply" title="回复反馈" width="500px" destroy-on-close>
      <div class="reply-form">
        <div class="user-suggestion">
          <p class="label">用户 {{ rowData.userName }} 的内容：</p>
          <div class="suggestion-content">
            {{ rowData.info }}
          </div>
        </div>

        <div class="admin-reply">
          <p class="label">回复内容：</p>
          <el-input v-model="rowData.reply" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogReply = false">取消</el-button>
          <el-button color="#000" @click="submitReply">确认回复</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import myAxios from '@/api/index'
import { ElMessage } from 'element-plus'
import { EditPen, Refresh } from '@element-plus/icons-vue'

interface AdviceItem {
  id: number | string
  userName: string
  info: string
  infoTime: string
  reply?: string
  replyTime?: string
  bookName?: string
}

interface AdviceGroup {
  userName: string
  items: AdviceItem[]
}

interface ApiResp<T> {
  code: number
  msg?: string
  data: T
}

const suggestionData = ref<AdviceGroup[]>([])
const reviewData = ref<AdviceGroup[]>([])
const dialogReply = ref(false)
const activeTab = ref('suggestion')

const rowData = reactive({
  id: '',
  userName: '',
  info: '',
  infoTime: '',
  reply: '',
  replyTime: ''
})

const groupByUserName = (rows: AdviceItem[]) => {
  const map = new Map<string, AdviceGroup>()
  rows.forEach((item) => {
    const key = item.userName || '未知用户'
    if (!map.has(key)) {
      map.set(key, { userName: key, items: [] })
    }
    map.get(key)!.items.push(item)
  })
  return Array.from(map.values())
}

const refreshData = async () => {
  await Promise.all([getSuggestions(), getReviews()])
}

const openReplyDialog = (row: AdviceItem) => {
  dialogReply.value = true
  rowData.id = String(row.id || '')
  rowData.userName = row.userName || ''
  rowData.info = row.info || ''
  rowData.infoTime = row.infoTime || ''
  rowData.reply = row.reply || ''
  rowData.replyTime = row.replyTime || ''
}

async function getSuggestions() {
  try {
    const res = await myAxios.get<ApiResp<AdviceItem[]>>('/advice/getAll')
    if (res.data.code !== 200) {
      throw new Error(res.data.msg || '获取留言建议失败')
    }
    const rows = res.data.data || []
    suggestionData.value = groupByUserName(rows)
  } catch (e) {
    console.error(e)
    suggestionData.value = []
    ElMessage.error('获取留言建议失败')
  }
}

async function getReviews() {
  try {
    const res = await myAxios.get<ApiResp<AdviceItem[]>>('/advice/getAllBookReviews')
    if (res.data.code !== 200) {
      throw new Error(res.data.msg || '获取图书评论失败')
    }
    const rows = res.data.data || []
    reviewData.value = groupByUserName(rows)
  } catch (e) {
    console.error(e)
    reviewData.value = []
    ElMessage.error('获取图书评论失败')
  }
}

async function submitReply() {
  if (!rowData.reply.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    const res = await myAxios.post<ApiResp<string>>('/advice/reply', {
      id: rowData.id,
      reply: rowData.reply
    })
    if (res.data.code === 200) {
      await refreshData()
      dialogReply.value = false
      ElMessage.success('回复成功')
      return
    }
    ElMessage.error(res.data.msg || '回复失败')
  } catch (e) {
    console.error(e)
    ElMessage.error('系统错误，回复失败')
  }
}

onMounted(() => {
  refreshData()
})
</script>

<style lang="scss" scoped>
.advice-container {
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
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
}

.custom-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
  padding-top: 10px;
}

:deep(.el-tab-pane) {
  height: 100%;
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

.admin-reply {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
