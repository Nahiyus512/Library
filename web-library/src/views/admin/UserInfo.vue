<template>
  <div class="user-info-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">用户管理</h2>
        <span class="page-subtitle">管理系统所有用户</span>
      </div>
      <div class="header-right">
        <el-input 
          v-model="headSearchInput" 
          placeholder="搜索用户名..." 
          class="search-input"
          :prefix-icon="Search"
          clearable
          @clear="findUser"
          @keyup.enter="findUser"
        />
        <el-button color="#000" @click="findUser">查询</el-button>
        <el-button color="#000" plain @click="dialogAddFormVisible = true">
          <el-icon class="el-icon--left"><Plus /></el-icon>添加用户
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table :data="tableData" style="width: 100%; flex: 1;" height="100%" stripe>
        <el-table-column fixed prop="id" label="用户ID" width="150" align="center" />
        <el-table-column prop="name" label="用户名" width="150" />
        <el-table-column prop="password" label="密码" width="180" />
        <el-table-column prop="age" label="年龄" width="120" align="center" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column fixed="right" label="操作" width="180" align="center">
          <template #default="scope">
            <el-button link color="#000" size="small" @click="changeUser(scope.row)">
              <el-icon><Edit /></el-icon> 修改
            </el-button>
            <el-button link type="danger" size="small" @click="deleteUser(scope.row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination 
          v-model:current-page="dataInfo.pageNum"
          v-model:page-size="dataInfo.pageSize"
          :page-sizes="[5, 10, 20]"
          :disabled="disabled"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="dataInfo.allNum"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>

  <el-dialog v-model="dialogFormVisible" title="修改用户信息" width="500px" destroy-on-close>
    <el-form :model="form" label-position="top">
      <el-form-item label="用户ID">
        <el-input v-model="form.userId" autocomplete="off" disabled/>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="form.userName" autocomplete="off" disabled/>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" autocomplete="off" show-password />
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="form.age" autocomplete="off" />
      </el-form-item>
      <el-form-item label="地址">
        <el-input v-model="form.address" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="clickUpdateOk">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="dialogAddFormVisible" title="添加用户" width="500px" destroy-on-close>
    <el-form :model="addForm" label-position="top">
      <el-form-item label="用户名">
        <el-input v-model="addForm.userName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="addForm.password" autocomplete="off" show-password />
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="addForm.age" autocomplete="off" />
      </el-form-item>
      <el-form-item label="地址">
        <el-input v-model="addForm.address" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogAddFormVisible = false">取消</el-button>
        <el-button type="primary" @click="clickAddUser">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
      v-model="centerDialogVisible"
      title="警告"
      width="400px"
      align-center
      destroy-on-close
  >
    <div class="warning-content">
      <el-icon class="warning-icon"><WarningFilled /></el-icon>
      <span>确认删除此用户数据吗? 此操作无法恢复。</span>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="clickDeleteUser">删除</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import {onMounted} from "vue";
import {useRouter} from "vue-router";
import {reactive,ref} from "vue";
import myAxios from "@/api/index";
import {ElMessage} from "element-plus";
import { Search, Plus, Edit, Delete, WarningFilled } from '@element-plus/icons-vue'

const router = useRouter()

onMounted(()=>{
  //console.log("onmount")
  getUser()
})

const dialogFormVisible = ref(false)
const dialogAddFormVisible = ref(false)
const centerDialogVisible = ref(false)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)
const headSearchInput = ref('')
const tableData = ref([])

const addForm = reactive({
  userName:'',
  password:'',
  age:'',
  address:''
})

const form = reactive({
  userId:'',
  userName:'',
  password:'',
  age:'',
  address:''
})

//分页数据
const dataInfo = reactive({
  allNum:20,
  pageSize:10,
  pageNum:1,
  name:''
})


function goto() {
  router.push("/login")
}

const handleSizeChange = (val: number) => {
  getUser()
}
const handleCurrentChange = (val: number) => {
  getUser()
}

const changeUser = (row)=>{
  dialogFormVisible.value = true
  //console.log(row)
  form.userId=row.id
  form.userName=row.name
  form.password=row.password
  form.age=row.age
  form.address=row.address
}

const deleteUser = (row)=>{
  centerDialogVisible.value = true
  form.userName=row.name
}

const getUser = async ()=>{
  try {
    let res = await myAxios.post('http://localhost:8080/user/listPage',{
      pageSize:dataInfo.pageSize,
      pageNum:dataInfo.pageNum,
      nameInfo:dataInfo.name
    })
    tableData.value=JSON.parse(JSON.stringify(res.data.data.userList))
    dataInfo.allNum=JSON.parse(JSON.stringify(res.data.data.count))
  }catch (e) {
    console.log(e)
  }
}

async function clickUpdateOk(){
  try {
    let updateResult = await myAxios.put('http://localhost:8080/user/change',{
      id:form.userId,
      name:form.userName,
      password:form.password,
      age:form.age,
      address:form.address,
    })
    //console.log(updateResult)
    if(updateResult.data.code === 200) {
      await getUser()
      ElMessage.success(updateResult.data.data)
    } else {
      ElMessage.error(updateResult.data.data)
    }
  }catch (e) {
    console.log(e)
  }
  dialogFormVisible.value = false
}

async function clickDeleteUser() {
  try {
    let deleteResult = await myAxios.delete('http://localhost:8080/user/del',{
      data:{
        username:form.userName
      }
    })
    if(deleteResult.data.code === 200)
    {
      ElMessage.success("删除成功")
      await getUser();
    }
    else {
      ElMessage.error("删除失败")
    }
  } catch (e) {
    console.log(e)
  }
  centerDialogVisible.value = false
}

async function findUser(){
  dataInfo.name = headSearchInput.value
  await getUser()
}

async function clickAddUser() {
  try {
    if(addForm.userName == ''){
      ElMessage.error("用户名不能为空")
      return;
    }
    if(addForm.password == ''){
      ElMessage.error("用户密码不能为空")
      return;
    }
    let res = await myAxios.post("http://localhost:8080/user/logon",{
      name:addForm.userName,
      password:addForm.password,
      age:addForm.age,
      address:addForm.address
    })
    console.log(res)
    if(res.data.code === 200){
      ElMessage.success("用户注册成功")
      await getUser();
    }else {
      ElMessage.error(res.data.msg)
    }
  }catch (e) {
    console.log(e)
  }
  addForm.age=''
  addForm.userName=''
  addForm.password=''
  addForm.address=''
  dialogAddFormVisible.value = false
}

</script>

<style lang="scss" scoped>
.user-info-container {
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

    .header-right {
      display: flex;
      gap: 12px;
      align-items: center;

      .search-input {
        width: 240px;
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

    .pagination-container {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
      flex-shrink: 0;
    }
  }
}

.warning-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 0;
  
  .warning-icon {
    font-size: 24px;
    color: #e6a23c;
  }
  
  span {
    font-size: 16px;
    color: #606266;
  }
}

@media (max-width: 768px) {
  .user-info-container {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      
      .header-right {
        width: 100%;
        flex-wrap: wrap;
        
        .search-input {
          flex: 1;
          min-width: 200px;
        }
      }
    }
  }
}
</style>