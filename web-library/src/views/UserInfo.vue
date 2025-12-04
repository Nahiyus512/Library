<template>
  <div class="user-info">
      <div class="header-info">
        输入查询的用户名:  <el-input v-model="headSearchInput" style="width: 240px;margin-left: 10px" placeholder="Please input"/>
        <el-button type="success" round style="margin-left: 10px" @click="findUser">查询</el-button>
        <el-button type="success" round style="margin-right: 20px" @click="dialogAddFormVisible = true">
          添加用户
        </el-button>
      </div>
      <div class="main-div">
        <el-table :data="tableData" style="width: 100%">
          <el-table-column fixed prop="id" label="用户ID" width="150" />
          <el-table-column prop="name" label="用户名" width="150" />
          <el-table-column prop="password" label="密码" width="180" />
          <el-table-column prop="age" label="年龄" width="120" />
          <el-table-column prop="address" label="地址" width="480" />
          <el-table-column fixed="right" label="操作" width="140">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="changeUser(scope.row)">
                修改
              </el-button>
              <el-button link type="primary" size="small" @click="deleteUser(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="page-el"
                       v-model:current-page="dataInfo.pageNum"
                       v-model:page-size="dataInfo.pageSize"
                       :page-sizes="[3, 5, 8, 12]"
                       :small="small"
                       :disabled="disabled"
                       :background="background"
                       layout="total, prev, pager, next"
                       :total="dataInfo.allNum"
                       @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"
        />
      </div>
  </div>

  <el-dialog v-model="dialogFormVisible" title="修改用户信息" width="500">
    <el-form :model="form">
      <el-form-item label="用户ID" :label-width="formLabelWidth" >
        <el-input v-model="form.userId" autocomplete="off" disabled/>
      </el-form-item>
      <el-form-item label="用户名" :label-width="formLabelWidth">
        <el-input v-model="form.userName" autocomplete="off" disabled/>
      </el-form-item>
      <el-form-item label="密码" :label-width="formLabelWidth">
        <el-input v-model="form.password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="年龄" :label-width="formLabelWidth">
        <el-input v-model="form.age" autocomplete="off" />
      </el-form-item>
      <el-form-item label="地址" :label-width="formLabelWidth">
        <el-input v-model="form.address" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="clickUpdateOk">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="dialogAddFormVisible" title="添加用户" width="500">
    <el-form :model="addForm">
      <el-form-item label="用户名" :label-width="formLabelWidth">
        <el-input v-model="addForm.userName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码" :label-width="formLabelWidth">
        <el-input v-model="addForm.password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="年龄" :label-width="formLabelWidth">
        <el-input v-model="addForm.age" autocomplete="off" />
      </el-form-item>
      <el-form-item label="地址" :label-width="formLabelWidth">
        <el-input v-model="addForm.address" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogAddFormVisible = false">取消</el-button>
        <el-button type="primary" @click="clickAddUser">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
      v-model="centerDialogVisible"
      title="提示"
      width="500"
      align-center
  >
    <span>确认删除此用户数据吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="clickDeleteUser">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

</template>

<script lang="ts" setup>
import {onMounted} from "vue";
import {useRouter} from "vue-router";
import {reactive,ref} from "vue";
import myAxios from "../axios";
import {ElMessage} from "element-plus";

const router = useRouter()

onMounted(()=>{
  //console.log("onmount")
  getUser()
})

const dialogFormVisible = ref(false)
const dialogAddFormVisible = ref(false)
const centerDialogVisible = ref(false)
const formLabelWidth = '140px'
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

<style lang="scss">


 .user-info {
   .header-info {
     //height: 20vh;
     background-color: #e5cdb5;
     justify-content: center;
     display: flex;
     padding-top: 40px;
     padding-bottom: 40px;
   }
   .main-div {
     //max-height: 60vh;
     .page-el {
       margin-top: 5px;
       justify-content: center;
       display: flex;
     }
   }

 }
</style>