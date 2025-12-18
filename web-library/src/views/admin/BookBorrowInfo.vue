<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">借阅管理</h2>
        <span class="page-subtitle">查看所有图书的借阅记录</span>
      </div>
      <div class="header-right">
        <el-input 
          v-model="pageInfo.name" 
          placeholder="搜索借阅人..." 
          class="search-input"
          :prefix-icon="Search"
          clearable
          @clear="handleCurrentChange(1)"
          @keyup.enter="handleCurrentChange(1)"
        />
        <el-button color="#000" @click="handleCurrentChange(1)">查询</el-button>
      </div>
    </div>

    <div class="content-container">
      <el-table :data="resulData" style="width: 100%; flex: 1;" height="100%" stripe border>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="userName" label="借阅人" width="150" />
        <el-table-column prop="bookId" label="图书ID" width="100" align="center" />
        <el-table-column prop="bookName" label="书名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="borrowTime" label="借阅时长" width="120" align="center">
          <template #default="scope">
            {{ scope.row.borrowTime }} 天
          </template>
        </el-table-column>
        <el-table-column prop="beginTime" label="借阅日期" width="180" align="center" />
        <el-table-column prop="endTime" label="应还日期" width="180" align="center" />
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageInfo.pageNum"
          v-model:page-size="pageInfo.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.allNum"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search } from '@element-plus/icons-vue';
import myAxios from "@/api/index";

const pageInfo = reactive({
  pageSize: 10,
  pageNum: 1,
  allNum: 0,
  name: ''
})

const resulData = ref([])

onMounted(() => {
  getBorrowBook()
})

const handleCurrentChange = (val: number) => {
  pageInfo.pageNum = val;
  getBorrowBook()
}

const handleSizeChange = (val: number) => {
  pageInfo.pageSize = val;
  pageInfo.pageNum = 1; // Reset to first page when size changes
  getBorrowBook()
}

const getBorrowBook = async () => {
  try {
    let res = await myAxios.post('http://localhost:8080/bookBorrow/list', {
      pageSize: pageInfo.pageSize,
      pageNum: pageInfo.pageNum,
      nameInfo: pageInfo.name
    })
    if (res.data.code === 200) {
      resulData.value = res.data.data.borrows
      pageInfo.allNum = res.data.data.count
    } else {
      ElMessage.error(res.data.msg || '获取数据失败')
    }
  } catch (e) {
    console.log(e)
    ElMessage.error('网络请求失败')
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

.header-left {
  display: flex;
  flex-direction: column;
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

.header-right {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  width: 250px;
}

.content-container {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
