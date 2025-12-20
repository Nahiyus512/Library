<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">书架管理</h2>
        <span class="page-subtitle">管理用户收藏的图书信息</span>
      </div>
      <div class="header-right">
        <el-button color="#000" @click="fetchData">
          <el-icon class="el-icon--left"><Refresh /></el-icon>刷新列表
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <el-table :data="tableData" style="width: 100%; flex: 1;" height="100%" stripe border>
        <el-table-column type="expand">
          <template #default="props">
            <div style="padding: 10px 20px;">
              <el-table :data="props.row.books" border>
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="bookName" label="图书名称" min-width="200" />
                <el-table-column prop="likeLevel" label="状态" width="120" align="center">
                  <template #default="scope">
                    <el-tag v-if="scope.row.likeLevel === 2" type="success">喜欢💗</el-tag>
                    <el-tag v-else-if="scope.row.likeLevel === 1" type="warning">还行👌</el-tag>
                    <el-tag v-else type="info">不行🙅‍♀️</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120" align="center">
                  <template #default="scope">
                    <el-popconfirm title="确定要删除这条记录吗？" @confirm="handleDelete(scope.row)">
                      <template #reference>
                        <el-button type="danger" link>
                          <el-icon><Delete /></el-icon> 删除
                        </el-button>
                      </template>
                    </el-popconfirm>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" />
        <el-table-column label="收藏数量" width="120" align="center">
          <template #default="scope">
            <el-tag effect="plain" round>{{ scope.row.books.length }} 本</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import myAxios from "@/api/index"
import { ElMessage } from "element-plus"
import { Refresh, Delete } from '@element-plus/icons-vue'

const tableData = ref([])

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  try {
    const res = await myAxios.get('http://localhost:8080/bookLike/getAll')
    if (res.data.code === 200) {
      // Group by userName
      const rawData = res.data.data
      const groupedMap = new Map()
      
      rawData.forEach((item: any) => {
        if (!groupedMap.has(item.userName)) {
          groupedMap.set(item.userName, {
            userName: item.userName,
            books: []
          })
        }
        groupedMap.get(item.userName).books.push(item)
      })
      
      tableData.value = Array.from(groupedMap.values())
    } else {
      ElMessage.error(res.data.msg || '获取数据失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('网络错误')
  }
}

const handleDelete = async (row: any) => {
  try {
    const res = await myAxios.delete(`http://localhost:8080/bookLike/delete?id=${row.id}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(res.data.msg || '删除失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('网络错误')
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
</style>
