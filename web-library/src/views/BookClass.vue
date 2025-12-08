<template>
  <div class="book-info">
    <!-- 头部 -->
    <div class="header-info">
      输入查询的分类名:
      <el-input v-model="headSearchInput" style="width: 240px" placeholder="请输入分类" />
      <el-button type="success" round style="margin-left: 10px" @click="findClass">查询</el-button>
      <el-button type="success" round style="margin-right: 20px" @click="dialogAddFormVisible = true">
        添加分类
      </el-button>
    </div>

    <!-- 表格 -->
    <div class="main-div">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="classify" label="分类名称" width="200" />
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="changeClass(scope.row)">修改</el-button>
            <el-button link type="danger" size="small" @click="deleteClass(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          class="page"
          v-model:current-page="dataInfo.pageNum"
          v-model:page-size="dataInfo.pageSize"
          :page-sizes="[5, 10, 15]"
          layout="total, sizes, prev, pager, next"
          :total="dataInfo.allNum"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>

  <!-- 修改分类 -->
  <el-dialog v-model="dialogFormVisible" title="修改分类" width="400">
    <el-form :model="form">
      <el-form-item label="分类名称" label-width="100px">
        <el-input v-model="form.classify" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogFormVisible = false">取消</el-button>
      <el-button type="primary" @click="clickUpdateOk">确定</el-button>
    </template>
  </el-dialog>

  <!-- 添加分类 -->
  <el-dialog v-model="dialogAddFormVisible" title="添加分类" width="400">
    <el-form :model="addForm">
      <el-form-item label="分类名称" label-width="100px">
        <el-input v-model="addForm.classify" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogAddFormVisible = false">取消</el-button>
      <el-button type="primary" @click="clickAddOk">确定</el-button>
    </template>
  </el-dialog>

  <!-- 删除确认 -->
  <el-dialog v-model="centerDialogVisible" title="提示" width="400" align-center>
    <span>确认删除该分类吗？</span>
    <template #footer>
      <el-button @click="centerDialogVisible = false">取消</el-button>
      <el-button type="danger" @click="clickDeleteClass">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import myAxios from "../axios/index.js";
import { ElMessage } from "element-plus";

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
  pageSize: 5,
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

<style scoped>
.book-info {
  .header-info {
    background-color: #68cbc1;
    padding-top: 40px;
    padding-left: 20px;
    padding-bottom: 40px;
  }

  .main-div {
    .page {
      margin-top: 5px;
      justify-content: center;
      display: flex;
    }
  }
}
</style>
