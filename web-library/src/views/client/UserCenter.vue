<template>
  <div class="page-container">
    <div class="user-layout">
      <!-- User Profile Card -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-placeholder">
            {{ String(userInfo.userName ?? '').charAt(0).toUpperCase() }}

          </div>
          <h2 class="username">{{ userInfo.userName }}</h2>
          <p class="user-role">读者</p>
        </div>

        <div class="profile-details">
          <div class="detail-item">
            <span class="label">账号</span>
            <span class="value">{{ userInfo.userName }}</span>
          </div>
          <div class="detail-item">
            <span class="label">年龄</span>
            <span class="value">{{ userInfo.age }}</span>
          </div>
          <div class="detail-item">
            <span class="label">地址</span>
            <span class="value">{{ userInfo.address }}</span>
          </div>
        </div>

        <div class="profile-actions">
          <button class="action-btn outline" @click="openEditDialog">
            修改信息
          </button>
          <button class="action-btn danger" @click="logout">
            退出登录
          </button>
        </div>
      </div>

      <!-- Borrowed Books Section -->
      <div class="borrow-section">
        <div class="section-header">
          <h3 class="section-title">借阅记录</h3>
          <span class="section-subtitle">管理您的借阅书籍和归还时间</span>
        </div>

        <div class="table-container">
          <el-table ref="tableRef" :data="borrowData" row-key="id" style="width: 100%"
            :header-cell-style="{ background: '#f9f9f9', color: '#666', fontWeight: '500' }">

            <el-table-column prop="bookName" label="书名" min-width="150" />
            <el-table-column prop="borrowTime" label="借阅天数" width="100" align="center" />
            <el-table-column prop="beginTime" label="借阅时间" width="180" />
            <el-table-column prop="endTime" label="应还时间" width="180" />
            <el-table-column fixed="right" label="操作" width="150" align="center">
              <template #default="scope">
                <el-button link type="primary" @click="changeBorrow(scope.row)">
                  续借
                </el-button>
                <el-button link type="danger" @click="changeBack(scope.row)">
                  归还
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- Edit Profile Dialog -->
    <el-dialog v-model="editDialogVisible" title="修改个人信息" width="400px" align-center class="custom-dialog">
      <div class="form-container">
        <div class="form-group">
          <label>账号</label>
          <el-input v-model="inputValue.userName" disabled />
        </div>
        <div class="form-group">
          <label>密码</label>
          <el-input v-model="inputValue.password" type="password" show-password />
        </div>
        <div class="form-group">
          <label>年龄</label>
          <el-input v-model="inputValue.age" />
        </div>
        <div class="form-group">
          <label>地址</label>
          <el-input v-model="inputValue.address" />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateUser">保存修改</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Renew Dialog -->
    <el-dialog v-model="borrowDialogVisible" title="续借书籍" width="400px" align-center class="custom-dialog">
      <p class="dialog-desc">请输入您想要续借的天数。</p>
      <div class="form-group">
        <el-input v-model.number="borrowDay" placeholder="请输入天数" type="number" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="borrowDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="borrow">确认续借</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Return Dialog -->
    <el-dialog v-model="backDialogVisible" title="归还书籍" width="400px" align-center class="custom-dialog">
      <p class="dialog-desc">确认归还书籍 <strong>{{ borrowSum.bookName }}</strong> 吗？</p>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="backDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="back">确认归还</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watchEffect, nextTick } from 'vue'
import { useRouter } from "vue-router";
import { useCookies } from '@vueuse/integrations/useCookies'
import myAxios from "@/api/index";


import { ElMessage } from "element-plus";

const router = useRouter();
const cookie = useCookies()

const editDialogVisible = ref(false)
const borrowDialogVisible = ref(false)
const backDialogVisible = ref(false)

const inputValue = reactive({
  userName: '',
  password: '',
  age: '',
  address: ''
})

const userInfo = reactive({
  userName: '',
  password: '',
  age: '',
  address: ''
})

const borrowData = ref([])
const tableRef = ref<any>(null)


watchEffect(() => {
  console.log("表格绑定 borrowData =", borrowData.value, Array.isArray(borrowData.value))
})

const borrowTime = ref<number>(0)
const borrowDay = ref<number>(0)
const borrowDays = ref(0);
const borrowSum = reactive({
  userName: '',
  bookName: '',
  beginTime: '',
  endTime: '',
})

onMounted(async () => {
  const uname = cookie.get('username') || ''
  inputValue.userName = uname
  userInfo.userName = uname

  await getUserInfo()     // 这里会把 userInfo.userName 更新为后端的 name
  await getBorrowInfo()   // 用更新后的 userInfo.userName 去查借阅
})


const openEditDialog = () => {
  editDialogVisible.value = true
  // Sync input values with current info
  inputValue.password = userInfo.password
  inputValue.age = userInfo.age
  inputValue.address = userInfo.address
}

const changeBorrow = (row: any) => {
  borrowDialogVisible.value = true
  borrowSum.userName = row.userName
  borrowSum.bookName = row.bookName
  borrowTime.value = parseInt(row.borrowTime, 10)
  borrowSum.beginTime = row.beginTime
  borrowSum.endTime = row.endTime
}

const changeBack = (row: any) => {
  backDialogVisible.value = true
  borrowSum.userName = row.userName
  borrowSum.bookName = row.bookName
  borrowTime.value = parseInt(row.borrowTime, 10)
  borrowSum.beginTime = row.beginTime
  borrowSum.endTime = row.endTime
}

// Get User Info
const getUserInfo = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName?name=' + userInfo.userName)
    if (res.data.code == 200) {
      userInfo.userName = res.data.data.name
      userInfo.password = res.data.data.password
      userInfo.age = res.data.data.age
      userInfo.address = res.data.data.address

      inputValue.userName = res.data.data.name
      inputValue.password = res.data.data.password
      inputValue.age = res.data.data.age
      inputValue.address = res.data.data.address
    }
  } catch (e) {
    console.log(e)
  }
}

// Update User Info
const updateUser = async () => {
  try {
    let res = await myAxios.post('http://localhost:8080/user/updateByName', {
      name: inputValue.userName,
      password: inputValue.password,
      age: inputValue.age,
      address: inputValue.address
    })
    if (res.data.code == 200) {
      ElMessage.success(res.data.msg)
      await getUserInfo()
      editDialogVisible.value = false
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    console.log(e)
  }
}

// Get Borrow Info
const getBorrowInfo = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookBorrow/getBorrowInfo?username=' + userInfo.userName)
    console.log('借阅接口返回：', res.data)
    if (res.data.code == 200) {
      borrowData.value = res.data.data
      await nextTick()
      tableRef.value?.doLayout?.()
    }
  } catch (e) {
    console.log(e)
  }
}

// Renew
const borrow = async () => {
  borrowDays.value = borrowDay.value + borrowTime.value
  try {
    let res = await myAxios.post('http://localhost:8080/bookBorrow/borrowTime', {
      userName: borrowSum.userName,
      bookName: borrowSum.bookName,
      borrowTime: borrowDays.value,
      beginTime: borrowSum.beginTime,
    })
    if (res.data.code == 200) {
      ElMessage.success(res.data.msg)
      await getBorrowInfo()
      borrowDialogVisible.value = false
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    console.log(e)
  }
}

// Return
const back = async () => {
  try {
    let res = await myAxios.post('http://localhost:8080/bookBorrow/back', {
      userName: borrowSum.userName,
      bookName: borrowSum.bookName,
      beginTime: borrowSum.beginTime,
    })
    if (res.data.code == 200) {
      ElMessage.success(res.data.msg)
      await getBorrowInfo()
      backDialogVisible.value = false
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    console.log(e)
  }
}

const logout = () => {
  cookie.remove('token')
  router.push('/login')
}
</script>

<style scoped>
.page-container {
  padding-top: 20px;
}

.user-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 40px;
  align-items: start;
}

/* Profile Card */
.profile-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  position: sticky;
  top: 20px;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  background: #000;
  color: #fff;
  font-size: 32px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  margin: 0 auto 20px;
}

.username {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 5px 0;
  color: #1a1a1a;
}

.user-role {
  color: #888;
  font-size: 14px;
  margin: 0 0 30px 0;
}

.profile-details {
  text-align: left;
  margin-bottom: 30px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.detail-item .label {
  color: #888;
}

.detail-item .value {
  color: #333;
  font-weight: 500;
}

.profile-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-btn {
  width: 100%;
  padding: 12px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn.outline {
  background: transparent;
  border: 1px solid #ddd;
  color: #333;
}

.action-btn.outline:hover {
  border-color: #000;
}

.action-btn.danger {
  background: transparent;
  border: 1px solid #ff4d4f;
  color: #ff4d4f;
}

.action-btn.danger:hover {
  background: #fff1f0;
}

/* Borrow Section */
.borrow-section {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

.section-header {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 5px 0;
  color: #1a1a1a;
}

.section-subtitle {
  color: #888;
  font-size: 14px;
}

/* Form Styles */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.dialog-desc {
  color: #666;
  margin-bottom: 20px;
  font-size: 14px;
}

/* Responsive */
@media (max-width: 900px) {
  .user-layout {
    grid-template-columns: 1fr;
  }

  .profile-card {
    position: static;
  }
}
</style>