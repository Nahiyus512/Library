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

      <!-- Content Section (Likes & Comments) -->
      <div class="content-section">
        <el-row :gutter="24">
          <!-- Left: Liked Books -->
          <el-col :span="12">
            <div class="panel">
              <div class="panel-header">
                <h3 class="panel-title">我的收藏</h3>
                <span class="panel-subtitle">共 {{ likeData.length }} 本</span>
              </div>
              <div class="table-container">
                <el-table ref="tableRef" :data="likeData" row-key="id" style="width: 100%" height="calc(100vh - 250px)"
                  :header-cell-style="{ background: '#f9f9f9', color: '#666', fontWeight: '500' }">
                  <el-table-column prop="bookName" label="书名" min-width="120" show-overflow-tooltip />
                  <el-table-column prop="bookAuthor" label="作者" width="100" show-overflow-tooltip />
                  <el-table-column prop="likeLevel" label="喜爱" width="80" align="center">
                    <template #default="scope">
                      <el-tag :type="scope.row.likeLevel === 2 ? 'danger' : 'warning'" effect="light" round size="small">
                        {{ scope.row.likeLevel === 2 ? '想看' : '还行' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="80" align="center">
                    <template #default="scope">
                      <el-popconfirm title="移除?" @confirm="unlikeBook(scope.row)">
                        <template #reference>
                          <el-button link type="danger" size="small">移除</el-button>
                        </template>
                      </el-popconfirm>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-col>

          <!-- Right: My Comments -->
          <el-col :span="12">
            <div class="panel">
              <div class="panel-header">
                <h3 class="panel-title">我的评论</h3>
                <span class="panel-subtitle">共 {{ commentData.length }} 条</span>
              </div>
              <div class="table-container">
                <el-table :data="commentData" style="width: 100%" height="calc(100vh - 250px)"
                  :header-cell-style="{ background: '#f9f9f9', color: '#666', fontWeight: '500' }">
                  <el-table-column prop="bookName" label="评论书籍" width="120" show-overflow-tooltip />
                  <el-table-column prop="info" label="评论内容" min-width="150" show-overflow-tooltip />
                  <el-table-column prop="infoTime" label="时间" width="100" show-overflow-tooltip />
                </el-table>
              </div>
            </div>
          </el-col>
        </el-row>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watchEffect, nextTick, computed } from 'vue'
import { useRouter } from "vue-router";
import { useCookies } from '@vueuse/integrations/useCookies'
import myAxios from "@/api/index";


import { ElMessage } from "element-plus";

const router = useRouter();
const cookie = useCookies()

const editDialogVisible = ref(false)

const inputValue = reactive({
  userName: '',
  password: '',
  age: '',
  address: ''
})

const userInfo = reactive({
  id: 0,
  userName: '',
  password: '',
  age: '',
  address: ''
})

const likeData = ref([])
const commentData = ref<any[]>([])
const tableRef = ref<any>(null)

watchEffect(() => {
  console.log("表格绑定 likeData =", likeData.value, Array.isArray(likeData.value))
})

onMounted(async () => {
  const uname = cookie.get('username') || ''
  inputValue.userName = uname
  userInfo.userName = uname

  await getUserInfo()     // 这里会把 userInfo.userName 更新为后端的 name
  if (userInfo.id) {
    await getLikeInfo()   // 用更新后的 userInfo.id 去查收藏
  }
  await getCommentInfo()
})


const openEditDialog = () => {
  editDialogVisible.value = true
  // Sync input values with current info
  inputValue.password = userInfo.password
  inputValue.age = userInfo.age
  inputValue.address = userInfo.address
}

const unlikeBook = async (row: any) => {
  // Assuming row has 'id' which is the primary key of BookLike record
  // Or we need to use delete endpoint with ID
  // Based on SearchCodebase, delete is /bookLike/delete?id=...
  // We need to know if row has 'id' (BookLike ID) or just bookId.
  // The /bookLike/list returns map with 'bookId', 'bookName'... does it return BookLike ID?
  // Let's re-check the Controller code in SearchCodebase.
  // BookLikeController.getBookshelf returns map with book info + likeLevel. It does NOT seem to put 'id' (BookLike ID) in the map!
  // It puts 'bookId'.
  // But delete endpoint requires BookLike ID.
  // OR we can use the 'like' endpoint with level 0? No, that updates level.
  // If we can't get BookLike ID, we might need to modify backend or find another way.
  // Wait, let's look at BookLikeController again.
  
  // "List<Map<String, Object>> result = new ArrayList<>(); for (BookLike like : likes) { ... map.put("bookId", book.getBookId()); ... }"
  // It misses putting the BookLike ID!
  
  // Alternative: Call /bookLike/like with likeLevel = 0?
  // Controller: "if(exist != null) { exist.setLikeLevel(bookLike.getLikeLevel()); return this.updateById(exist); }"
  // So if we set level to 0, it updates it. Does it delete it?
  // Requirement says "0-不想看".
  // So maybe we just set it to 0. But then it might still show up if we filter >= 1.
  // The /bookLike/list code: "likeWrapper.eq(BookLike::getUserId, userId);" - it gets ALL likes for user.
  // BUT the controller code I saw earlier:
  // "List<BookLike> likes = bookLikeService.list(likeWrapper);" - No filter on level?
  // Wait, "likeWrapper.eq(BookLike::getUserId, userId);" - yes no level filter.
  // So if I set it to 0, it will still appear in the list?
  // Let's check getBookshelf again.
  // It iterates and returns them.
  // If I want to "remove" it, I should probably delete the record.
  // But I don't have the ID.
  
  // Strategy: Update backend or use what I have.
  // Since I can't easily change backend structure without seeing it all (I can, but...), 
  // let's try to see if I can use another endpoint or if I missed something.
  // Ah, I can use the `like` endpoint to set it to 0. 
  // And then in the frontend, I filter out 0s? 
  // Or maybe I should modify the backend to return the ID.
  // The user prompt says "modify system dashboard... modify personal center". I can modify backend.
  
  // Let's modify BookLikeController.java to include 'id' (BookLike ID) in the map.
  
  try {
    // We will use the BookLike ID if available. If not, we might need to fix backend.
    // For now, let's assume we will fix the backend to return 'id'.
    // If we use 'delete', we need the ID.
    
    // Let's try to use the delete endpoint if we have ID.
    // If not, we try to set level to 0.
    
    // Actually, I'll update the backend controller first to ensure 'id' is returned.
    
    // But first, let's write the frontend code assuming 'id' will be there.
    if (row.id) {
         let res = await myAxios.delete(`http://localhost:8080/bookLike/delete?id=${row.id}`)
         if (res.data.code == 200) {
            ElMessage.success('已移除')
            await getLikeInfo()
         } else {
            ElMessage.error(res.data.msg)
         }
    } else {
        // Fallback: try to set level to 0 (if that's how we "remove" from view, but usually we want to delete)
        // Or finding the ID by userId + bookId is hard without an endpoint.
        // I will modify the backend to return 'id'.
        ElMessage.error("无法获取记录ID")
    }
  } catch (e) {
    console.log(e)
  }
}

// Get User Info
const getUserInfo = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName?name=' + userInfo.userName)
    if (res.data.code == 200) {
      userInfo.id = res.data.data.id
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

// Get Like Info
const getLikeInfo = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookLike/list?userId=' + userInfo.id)
    console.log('收藏接口返回：', res.data)
    if (res.data.code == 200) {
      // Sort: 2 (Love) first, then 1 (Like)
      let data = res.data.data || []
      data.sort((a: any, b: any) => b.likeLevel - a.likeLevel)
      likeData.value = data
      
      await nextTick()
      tableRef.value?.doLayout?.()
    }
  } catch (e) {
    console.log(e)
  }
}

const getCommentInfo = async () => {
  try {
    // type=1 for book reviews (has bookId)
    const res = await myAxios.get(`/advice/get?userName=${userInfo.userName}&type=1`)
    if (res.data.code === 200) {
      const comments = res.data.data || [];
      
      if (comments.length > 0 && !comments[0].bookName && comments[0].bookId) {
          const booksRes = await myAxios.get('/book/all');
          if (booksRes.data.code === 200) {
              const bookMap = new Map();
              booksRes.data.data.forEach((b: any) => bookMap.set(String(b.bookId), b.bookName));
              comments.forEach((c: any) => {
                  if (c.bookId) c.bookName = bookMap.get(String(c.bookId)) || '未知书籍';
              });
          }
      }
      
      commentData.value = comments;
    }
  } catch (e) {
    console.error(e)
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

/* Content Section */
.content-section {
  width: 100%;
}

.panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.panel-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #1a1a1a;
}

.panel-subtitle {
  color: #999;
  font-size: 14px;
}

/* Content Section */
.content-section {
  width: 100%;
  height: calc(100vh - 120px);
  padding-bottom: 20px;
}
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