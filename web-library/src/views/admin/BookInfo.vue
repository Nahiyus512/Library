<template>
  <div class="book-info-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">图书管理</h2>
        <span class="page-subtitle">管理馆藏图书信息</span>
      </div>
      <div class="header-right">
        <el-input 
          v-model="headSearchInput" 
          placeholder="搜索书名..." 
          class="search-input"
          :prefix-icon="Search"
          clearable
          @clear="findBook"
          @keyup.enter="findBook"
        />
        <el-button color="#000" @click="findBook">查询</el-button>
        <el-button color="#000" plain @click="dialogAddFormVisible = true">
          <el-icon class="el-icon--left"><Plus /></el-icon>添加书本
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table :data="tableData" style="width: 100%; flex: 1;" height="100%" stripe>
        <el-table-column type="index" label="序号" width="80" fixed align="center" />
        <el-table-column prop="bookName" label="书名" min-width="150" />
        <el-table-column prop="bookPrice" label="价格" width="100" align="center">
          <template #default="scope">
            ¥{{ scope.row.bookPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="bookPublic" label="出版社" width="150" show-overflow-tooltip />
        <el-table-column prop="bookClassify" label="分类" width="120" align="center">
          <template #default="scope">
            <el-tag size="small" type="info" effect="plain" style="color: #000; border-color: #000;">{{ scope.row.bookClassify }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="封面" width="120" align="center">
          <template #default="scope">
            <el-image 
              :src="`http://localhost:8080/common/download?name=${scope.row.bookImge}`"
              style="width: 60px; height: 80px; border-radius: 4px;"
              fit="cover"
              :preview-src-list="[`http://localhost:8080/common/download?name=${scope.row.bookImge}`]"
              preview-teleported
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="bookDescription" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="bookNum" label="库存" width="100" align="center" />
        <el-table-column fixed="right" label="操作" width="180" align="center">
          <template #default="scope">
            <el-button link style="color: #000" size="small" @click="changeBook(scope.row)">
              <el-icon><Edit /></el-icon> 修改
            </el-button>
            <el-button link style="color: #666" size="small" @click="deleteBook(scope.row)">
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

  <el-dialog v-model="dialogFormVisible" title="修改书本信息" width="600px" destroy-on-close>
    <el-form :model="form" label-position="top">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="书名">
            <el-input v-model="form.bookName" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格">
            <el-input v-model="form.bookPrice" autocomplete="off" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出版社">
            <el-input v-model="form.bookPublic" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="分类">
            <el-select v-model="form.bookClassify" placeholder="选择分类" style="width: 100%">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
                :disabled="item.disabled" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="封面图片">
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

      <el-form-item label="描述">
        <el-input v-model="form.bookDesc" type="textarea" :rows="3" autocomplete="off" />
      </el-form-item>
      
      <el-form-item label="库存数量">
        <el-input-number v-model="form.bookNum" :min="0" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button color="#000" @click="clickUpdateOk">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="dialogAddFormVisible" title="添加书籍" width="600px" destroy-on-close>
    <el-form :model="addForm" label-position="top">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="书名">
            <el-input v-model="addForm.bookName" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格">
            <el-input v-model="addForm.bookPrice" autocomplete="off" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出版社">
            <el-input v-model="addForm.bookPublic" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="分类">
            <el-select v-model="addForm.bookClassify" placeholder="选择分类" style="width: 100%">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
                :disabled="item.disabled" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="封面图片">
        <el-upload class="avatar-uploader" action="http://localhost:8080/common/upload?module=imageUrl"
          :headers="headers" :show-file-list="false" :on-success="handleAvatarSuccess" ref="uploadRef">
          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon">
            <Plus />
          </el-icon>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="描述">
        <el-input v-model="addForm.bookDesc" type="textarea" :rows="3" autocomplete="off" />
      </el-form-item>
      
      <el-form-item label="库存数量">
        <el-input-number v-model="addForm.bookNum" :min="0" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button color="#000" @click="clickAddOk">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="centerDialogVisible" title="警告" width="400px" align-center destroy-on-close>
    <div class="warning-content">
      <el-icon class="warning-icon"><WarningFilled /></el-icon>
      <span>确认删除此书本数据吗? 此操作无法恢复。</span>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button color="#000" plain @click="clickDeleteBook">删除</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { onMounted, ref, reactive } from "vue";
import myAxios from "@/api/index"
import { ElMessage, UploadInstance } from "element-plus";
import { Plus, Search, Edit, Delete, WarningFilled, Picture } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'
import { getCurrentInstance } from 'vue';
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()

const selectValue = ref('')
const options = ref<any[]>([])

const uploadRef = ref<UploadInstance>()
const instance = getCurrentInstance();
const dialogFormVisible = ref(false)
const dialogAddFormVisible = ref(false)
const centerDialogVisible = ref(false)
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
  getBook()
  getBookClass()
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
  imageUrl.value = ''
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
    tableData.value = JSON.parse(JSON.stringify(res.data.data.bookList))
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
  form.bookId = row.bookId
}

async function clickUpdateOk(row) {
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
  addForm.bookImage = response.data
  imageUrl.value = `http://localhost:8080/common/download?name=${response.data}`
  instance?.proxy?.$forceUpdate()
}

// ✅ 修改书本时图片上传成功
const handleEditAvatarSuccess: UploadProps['onSuccess'] = (
    response,
    uploadFile
) => {
  form.bookImage = response.data
  imageUrl.value = `http://localhost:8080/common/download?name=${response.data}`
}

const getBookClass = async () => {
  try {
    const res = await myAxios.get('http://localhost:8080/class/get')
    if (res.data.code === 200) {
      options.value = res.data.data.map((item: any) => ({
        value: item.classify,
        label: item.classify
      }))
    }
  } catch (error) {
    console.error("获取分类失败：", error)
  }
}
</script>

<style lang="scss" scoped>
.book-info-container {
  padding: 24px;
  background-color: #f5f7fa;
  height: 100%; /* Change min-height to height for fixed layout */
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

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 178px;
  height: 178px;
  display: flex;
  justify-content: center;
  align-items: center;
  
  &:hover {
    border-color: #000;
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 20px;
}

@media (max-width: 768px) {
  .book-info-container {
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
