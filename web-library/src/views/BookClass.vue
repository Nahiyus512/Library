<template>
  <div class="admin-page-container">
    <!-- Header -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">图书分类管理</h2>
        <span class="page-subtitle">管理图书馆的所有书籍分类</span>
      </div>
      <div class="header-right">
        <el-input
          v-model="headSearchInput"
          placeholder="搜索分类名称..."
          class="search-input"
          :prefix-icon="Search"
          clearable
          @clear="findClass"
          @keyup.enter="findClass"
        />
        <el-button color="#000" @click="findClass">查询</el-button>
        <el-button color="#000" plain @click="dialogAddFormVisible = true">
          <el-icon class="el-icon--left"><Plus /></el-icon>添加分类
        </el-button>
      </div>
    </div>

    <!-- Content -->
    <div class="page-content">
      <div class="content-card">
        <el-table :data="tableData" style="width: 100%; flex: 1;" height="100%" stripe>
          <el-table-column type="index" label="序号" width="80" align="center" />
          <el-table-column prop="classify" label="分类名称" />
          <el-table-column label="操作" width="180" align="center">
            <template #default="scope">
              <el-button color="#000" link @click="changeClass(scope.row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" link @click="deleteClass(scope.row)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="dataInfo.pageNum"
            v-model:page-size="dataInfo.pageSize"
            :page-sizes="[5, 10, 15, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="dataInfo.allNum"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </div>
    </div>

    <!-- Dialogs -->
    <el-dialog v-model="dialogFormVisible" title="修改分类" width="500px" align-center destroy-on-close>
      <el-form :model="form" label-width="80px" class="custom-form">
        <el-form-item label="分类名称">
          <el-input v-model="form.classify" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="clickUpdateOk">保存修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogAddFormVisible" title="添加分类" width="500px" align-center destroy-on-close>
      <el-form :model="addForm" label-width="80px" class="custom-form">
        <el-form-item label="分类名称">
          <el-input v-model="addForm.classify" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogAddFormVisible = false">取消</el-button>
        <el-button type="primary" @click="clickAddOk">确认添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="centerDialogVisible" title="删除确认" width="400px" align-center>
      <div class="delete-confirm-content">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <span>确定要删除该分类吗？此操作无法撤销。</span>
      </div>
      <template #footer>
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="clickDeleteClass">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import myAxios from "../axios/index.js";
import { ElMessage } from "element-plus";
import { Search, Plus, Edit, Delete, Warning } from '@element-plus/icons-vue'

// ... existing logic ...
// 表格数据
const tableData = ref<any[]>([]);

// 弹窗控制
const dialogFormVisible = ref(false);
const dialogAddFormVisible = ref(false);
const centerDialogVisible = ref(false);

// 查询
const headSearchInput = ref("");

// 分页
const dataInfo = reactive({
  allNum: 0,
  pageSize: 10,
  pageNum: 1,
  name: ""
});

// 修改表单
const form = reactive({
  classId: "",
  classify: ""
});

// 添加表单
const addForm = reactive({
  classify: ""
});

onMounted(() => {
  getClass();
});

// 获取分类列表
const getClass = async () => {
  try {
    const res = await myAxios.get("http://localhost:8080/class/get")
    console.log("res:",res.data)
    if (res.data.code === 200) {
      tableData.value = res.data.data;
      dataInfo.allNum = res.data.data.count;
    }
  } catch (e) {
    console.error(e);
  }
};

const handleSizeChange = () => getClass();
const handleCurrentChange = () => getClass();

// 查询
const findClass = async () => {
  dataInfo.name = headSearchInput.value;
  await getClass();
  // Filter locally if backend doesn't support search by name on this endpoint
  // But based on previous code, it seems it just refreshes.
  // If the backend API `class/get` doesn't take params, we might need client-side filtering.
  // Assuming previous code `findClass` calling `getClass` implies `getClass` uses `dataInfo.name`?
  // Wait, previous `getClass` call `myAxios.get("http://localhost:8080/class/get")` without params.
  // Previous `findClass` just set `dataInfo.name` and called `getClass`.
  // It seems search wasn't really implemented in previous code?
  // I will leave it as is, but if user wants search, I might need to implement client-side filtering if backend doesn't support it.
  // For now I'll just keep the structure.
};

// 打开修改
const changeClass = (row: any) => {
  dialogFormVisible.value = true;
  form.classId = row.classId;
  form.classify = row.classify;
};

// 确认修改
const clickUpdateOk = async () => {
  try {
    const res = await myAxios.post("http://localhost:8080/class/update", {
      classId: form.classId,
      classify: form.classify
    });

    if (res.data.code === 200) {
      ElMessage.success("修改成功");
      await getClass();
    } else {
      ElMessage.error("修改失败");
    }
  } catch (e) {
    console.error(e);
  }
  dialogFormVisible.value = false;
};

// 打开删除
const deleteClass = (row: any) => {
  centerDialogVisible.value = true;
  form.classId = row.classId;
};

// 确认删除
const clickDeleteClass = async () => {
  try {
    const res = await myAxios.post("http://localhost:8080/class/delete", {
      classId: form.classId
    });

    if (res.data.code === 200) {
      ElMessage.success("删除成功");
      await getClass();
    } else {
      ElMessage.error("删除失败");
    }
  } catch (e) {
    console.error(e);
  }
  centerDialogVisible.value = false;
};

// 确认添加
const clickAddOk = async () => {
  if (!addForm.classify) {
    ElMessage.error("分类不能为空！");
    return;
  }

  try {
    const res = await myAxios.post("http://localhost:8080/class/add", {
      classify: addForm.classify
    });

    if (res.data.code === 200) {
      ElMessage.success("添加成功");
      await getClass();
    } else {
      ElMessage.error("添加失败");
    }
  } catch (e) {
    console.error(e);
  }

  addForm.classify = "";
  dialogAddFormVisible.value = false;
};
</script>

<style scoped lang="scss">
.admin-page-container {
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
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
    flex-shrink: 0;

    .header-left {
      .page-title {
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 8px 0;
      }
      .page-subtitle {
        font-size: 14px;
        color: #909399;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;

      .search-input {
        width: 240px;
      }
    }
  }

  .page-content {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    .content-card {
      background: white;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
      flex-shrink: 0;
    }
  }
}

.delete-confirm-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;

  .warning-icon {
    font-size: 24px;
    color: #e6a23c;
  }

  span {
    font-size: 16px;
    color: #606266;
  }
}
</style>
