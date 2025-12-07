<template>
  <div class="book-info">
    <div class="header-info">
      输入查询的书名: <el-input v-model="headSearchInput" style="width: 240px" placeholder="Please input" />
      <el-button type="success" round style="margin-left: 10px" @click="findBook">查询</el-button>
      <el-button type="success" round style="margin-right: 20px" @click="dialogAddFormVisible = true">
        添加书本
      </el-button>
    </div>
    <div class="main-div">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column type="index" label="序号" width="80" fixed />
        <el-table-column prop="bookName" label="书名" width="150" />
        <el-table-column prop="bookPrice" label="价格" width="100" />
        <el-table-column prop="bookPublic" label="出版社" width="150" />
        <el-table-column prop="bookClassify" label="分类" width="120" />
        <el-table-column label="书本图片">
          <template #default="scope">
            <el-image :src="`http://localhost:8080/common/download?name=${scope.row.bookImge}`"
              style="width: 90px;height: 90px" />
          </template>
        </el-table-column>
        <el-table-column prop="bookDescription" label="描述" width="200" />
        <el-table-column prop="bookNum" label="数量" width="100" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="changeBook(scope.row)">
              修改
            </el-button>
            <el-button link type="primary" size="small" @click="deleteBook(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="page" v-model:current-page="dataInfo.pageNum" v-model:page-size="dataInfo.pageSize"
        :page-sizes="[3, 5, 10, 15]" :small="small" :disabled="disabled" :background="background"
        layout="total,sizes, prev, pager, next" :total="dataInfo.allNum" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>
  </div>

  <el-dialog v-model="dialogFormVisible" title="修改书本信息" width="500">
    <el-form :model="form">
      <el-form-item label="书名" :label-width="formLabelWidth">
        <el-input v-model="form.bookName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="价格" :label-width="formLabelWidth">
        <el-input v-model="form.bookPrice" autocomplete="off" />
      </el-form-item>
      <el-form-item label="出版社" :label-width="formLabelWidth">
        <el-input v-model="form.bookPublic" autocomplete="off" />
      </el-form-item>
      <el-form-item label="分类" :label-width="formLabelWidth">
        <!--        <el-input v-model="form.bookClassify" autocomplete="off" />-->
        <el-select v-model="form.bookClassify" placeholder="Select" style="width: 120px">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
            :disabled="item.disabled" />
        </el-select>
      </el-form-item>
      <el-form-item label="图片" :label-width="formLabelWidth">
        <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/common/upload?module=imageUrl"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleEditAvatarSuccess"
        >
          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon">
            <Plus />
          </el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="描述" :label-width="formLabelWidth">
        <el-input v-model="form.bookDesc" autocomplete="off" />
      </el-form-item>
      <el-form-item label="数量" :label-width="formLabelWidth">
        <el-input v-model="form.bookNum" autocomplete="off" />
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

  <el-dialog v-model="dialogAddFormVisible" title="添加书籍" width="500">
    <el-form :model="addForm">
      <el-form-item label="书名" :label-width="formLabelWidth">
        <el-input v-model="addForm.bookName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="价格" :label-width="formLabelWidth">
        <el-input v-model="addForm.bookPrice" autocomplete="off" />
      </el-form-item>
      <el-form-item label="出版社" :label-width="formLabelWidth">
        <el-input v-model="addForm.bookPublic" autocomplete="off" />
      </el-form-item>
      <el-form-item label="分类" :label-width="formLabelWidth">
        <!--        <el-input v-model="addForm.bookClassify" autocomplete="off" />-->
        <el-select v-model="addForm.bookClassify" placeholder="Select" style="width: 120px">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
            :disabled="item.disabled" />
        </el-select>
      </el-form-item>
      <el-form-item label="图片" :label-width="formLabelWidth">
        <el-upload class="avatar-uploader" action="http://localhost:8080/common/upload?module=imageUrl"
          :headers="headers" :show-file-list="false" :on-success="handleAvatarSuccess" ref="uploadRef">
          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon">
            <Plus />
          </el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="描述" :label-width="formLabelWidth">
        <el-input v-model="addForm.bookDesc" autocomplete="off" />
      </el-form-item>
      <el-form-item label="数量" :label-width="formLabelWidth">
        <el-input v-model="addForm.bookNum" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="clickAddOk">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="centerDialogVisible" title="提示" width="500" align-center>
    <span>确认删除此书本数据吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="clickDeleteBook">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

</template>

<script lang="ts" setup>
import { onMounted, ref, reactive } from "vue";
import myAxios from "../axios/index.js"
import { ElMessage, UploadInstance } from "element-plus";
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'
import { getCurrentInstance } from 'vue';
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()

const selectValue = ref('')
const options = [
  {
    value: '自然',
    label: '自然',
  },
  {
    value: '计算机',
    label: '计算机',
  },
  {
    value: '恐怖',
    label: '恐怖',
  },
  {
    value: '工业',
    label: '工业',
  },
  {
    value: '科普',
    label: '科普',
  },
  {
    value: '知识',
    label: '知识',
  }

]


const uploadRef = ref<UploadInstance>()
const instance = getCurrentInstance();
const dialogFormVisible = ref(false)
const dialogAddFormVisible = ref(false)
const centerDialogVisible = ref(false)
const formLabelWidth = '120px'
const small = ref(false)
const background = ref(false)
const disabled = ref(false)
const headSearchInput = ref('')
let imageUrl = ref('')

const headers = ref({ "token": setHeardToken() });

//书本数据表格
const form = reactive({
  bookId: '',
  bookName: '',
  bookPrice: '',
  bookPublic: '',
  bookClassify: '',
  bookImage: '',
  bookDesc: '',
  bookNum: '',
})

const addImgForm = reactive({
  image: ''
})


//添加书本数据
const addForm = reactive({
  bookId: '',
  bookName: '',
  bookPrice: '',
  bookPublic: '',
  bookClassify: '',
  bookImage: '',
  bookDesc: '',
  bookNum: ''
})

//分页数据
const dataInfo = reactive({
  allNum: 20,
  pageSize: 5,
  pageNum: 1,
  name: ''
})

onMounted(() => {
  //console.log("onmount")
  getBook();
})

//接收的书本数组
const tableData = ref([]);

const handleSizeChange = (val: number) => {
  getBook()
}
const handleCurrentChange = (val: number) => {
  getBook()
}


const clearData = () => {
  addForm.bookName = ''
  addForm.bookDesc = ''
  addForm.bookImage = ''
  addForm.bookPublic = ''
  addForm.bookPrice = ''
  addForm.bookClassify = ''
  addForm.bookNum = ''
}

const cancel = () => {
  clearData()
  uploadRef.value.clearFiles()	//该方法就是清理上传列表
  dialogAddFormVisible.value = false
}
const getBook = async () => {
  try {
    let res = await myAxios.post('http://localhost:8080/book/listPage', {
      pageSize: dataInfo.pageSize,
      pageNum: dataInfo.pageNum,
      nameInfo: dataInfo.name
    })
    //console.log(res)
    tableData.value = JSON.parse(JSON.stringify(res.data.data.bookList))
    //console.log(tableData)
    dataInfo.allNum = JSON.parse(JSON.stringify(res.data.data.count))
  } catch (e) {
    console.log(e)
  }
}

const changeBook = (row) => {
  dialogFormVisible.value = true
  form.bookId = row.bookId
  form.bookName = row.bookName
  form.bookPrice = row.bookPrice
  form.bookPublic = row.bookPublic
  form.bookClassify = row.bookClassify
  form.bookImage = row.bookImge
  form.bookDesc = row.bookDescription
  form.bookNum = row.bookNum
  // ✅【关键：这里必须同步设置 imageUrl 才能回显图片】
  if (row.bookImge) {
    imageUrl.value = `http://localhost:8080/common/download?name=${row.bookImge}`
  } else {
    imageUrl.value = ''
  }
}

const deleteBook = (row) => {
  centerDialogVisible.value = true
  //console.log(row)
  form.bookId = row.bookId
}

async function clickUpdateOk(row) {
  console.log("你点击了ok")
  try {
    let updateResult = await myAxios.put('http://localhost:8080/book/change', {
      bookId: form.bookId,
      bookName: form.bookName,
      bookPrice: form.bookPrice,
      bookPublic: form.bookPublic,
      bookClassify: form.bookClassify,
      bookImge: form.bookImage,
      bookDescription: form.bookDesc,
      bookNum: form.bookNum
    })
    //console.log(updateResult)
    if (updateResult.data.code === 200) {
      await getBook()
      ElMessage.success(updateResult.data.data)
    } else {
      ElMessage.error(updateResult.data.data)
    }
  } catch (e) {
    console.log(e)
  }
  dialogFormVisible.value = false
}

async function clickAddOk() {
  try {
    if (addForm.bookName == '') {
      ElMessage.error("书名不能为空")
      return;
    }
    let addResult = await myAxios.post('http://localhost:8080/book/add', {
      bookId: addForm.bookId,
      bookName: addForm.bookName,
      bookPrice: addForm.bookPrice,
      bookPublic: addForm.bookPublic,
      bookClassify: addForm.bookClassify,
      bookImge: addForm.bookImage,
      bookDescription: addForm.bookDesc,
      bookNum: addForm.bookNum
    })
    if (addResult.data.code === 200) {
      ElMessage.success("添加书本成功")
      await getBook();
    } else {
      ElMessage.error("添加书本失败")
    }
    uploadRef.value!.clearFiles()	//该方法就是清理上传列表
  } catch (e) {
    console.log(e)
  }
  clearData()
  dialogAddFormVisible.value = false
}

async function clickDeleteBook() {
  try {
    let deleteResult = await myAxios.delete('http://localhost:8080/book/del', {
      data: {
        bookId: form.bookId
      }
    })
    if (deleteResult.data.code === 200) {
      ElMessage.success("删除成功")
      await getBook();
    }
    else {
      ElMessage.error("删除失败")
    }
  } catch (e) {
    console.log(e)
  }
  centerDialogVisible.value = false
}

async function findBook() {
  dataInfo.name = headSearchInput.value
  await getBook()
}

function setHeardToken() {
  console.log("token===>", cookie.get('token'))
  return cookie.get('token')
}

//图片上传成功逻辑
const handleAvatarSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile
) => {
  console.log("resp==>", response)

  // 后端返回的是 objectName（如 "book/uuid.png"）
  // 存到 addForm.bookImage（这个会提交到后端 /book/add）
  addForm.bookImage = response.data

  // 用 /common/download 来预览图片
  imageUrl.value = `http://localhost:8080/common/download?name=${response.data}`

  console.log(imageUrl.value)
  instance?.proxy?.$forceUpdate()
}

// ✅ 修改书本时图片上传成功
const handleEditAvatarSuccess: UploadProps['onSuccess'] = (
    response,
    uploadFile
) => {
  // 保存给修改用
  form.bookImage = response.data

  // 预览用
  imageUrl.value = `http://localhost:8080/common/download?name=${response.data}`

  console.log("修改图片成功 =>", imageUrl.value)
}



</script>

<style lang="scss" scoped>
.book-info {
  .header-info {
    //height: 15vh;
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

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>


<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
