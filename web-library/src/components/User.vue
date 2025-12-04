<template>
  <div class="user-main">
    <div class="left-contain">
      <span class="span-title">用户信息:</span>
      <div class="user-info">
        <div class="flip-card">
          <div class="flip-card-inner">
            <div class="flip-card-front">
<!--              <p class="title">FLIP CARD</p>-->
<!--              <p>Hover Me</p>-->
              <p>账  号:{{ userInfo.userName }}</p>
              <p>密  码:{{ userInfo.password }}</p>
              <p>年  龄:{{ userInfo.age }}</p>
              <p>地  址:{{ userInfo.address }}</p>
            </div>
            <div class="flip-card-back">
              <span class="span-back-title">修改信息: </span><br/>
              <span class="span-back">账  号:<input type="text" v-model="inputValue.userName" disabled style="background-color: #f8eec8"/></span><br/>
              <span class="span-back">密  码:<input type="text" v-model="inputValue.password"/></span><br/>
              <span class="span-back">年  龄:<input type="text" v-model="inputValue.age"/></span><br/>
              <span class="span-back">地  址:<input type="text" v-model="inputValue.address"/></span><br/>
              <span class="span-back-btn"><button @click="updateUser">确认修改</button></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="center-contain">
      <span class="span-title">借阅书本信息:</span>
      <div class="book-borrow-info">
        <el-table :data="borrowData"  class="col-table-el" >
          <el-table-column prop="bookName" label="书名" width="170" />
          <el-table-column prop="borrowTime" label="借阅天数" width="80" />
          <el-table-column prop="beginTime" label="起始时间" width="175" />
          <el-table-column prop="endTime" label="结束时间" width="175" />
          <el-table-column fixed="right" label="操作" width="143">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="changeBorrow(scope.row)">
                续借
              </el-button>
              <el-button link type="primary" size="small" @click="changeBack(scope.row)">
                归还
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="right-contain">
      <div class="right-contain-center">
        <button class="btn" @click="logout">
          <div class="wrapper">
            <p class="text">退出系统</p>

            <div class="flower flower1">
              <div class="petal one"></div>
              <div class="petal two"></div>
              <div class="petal three"></div>
              <div class="petal four"></div>
            </div>
            <div class="flower flower2">
              <div class="petal one"></div>
              <div class="petal two"></div>
              <div class="petal three"></div>
              <div class="petal four"></div>
            </div>
            <div class="flower flower3">
              <div class="petal one"></div>
              <div class="petal two"></div>
              <div class="petal three"></div>
              <div class="petal four"></div>
            </div>
            <div class="flower flower4">
              <div class="petal one"></div>
              <div class="petal two"></div>
              <div class="petal three"></div>
              <div class="petal four"></div>
            </div>
            <div class="flower flower5">
              <div class="petal one"></div>
              <div class="petal two"></div>
              <div class="petal three"></div>
              <div class="petal four"></div>
            </div>
            <div class="flower flower6">
              <div class="petal one"></div>
              <div class="petal two"></div>
              <div class="petal three"></div>
              <div class="petal four"></div>
            </div>
          </div>
        </button>

      </div>
    </div>
  </div>

  <el-dialog
      v-model="borrowDialogVisible"
      title="续借"
      width="400"
      align-center
  >
    <span>请输入续借时长:</span><br/>
    <el-input v-model.number="borrowDay" placeholder="请输入续借时长/天" ></el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="borrow">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>


  <el-dialog
      v-model="backDialogVisible"
      title="归还书本"
      width="400"
      align-center
  >
    <span>确认归还书本:{{borrowSum.bookName}}吗?</span><br/>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="backDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="back">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

</template>

<script setup lang="ts">
import router from "../router/index.ts";
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()
import {ref, reactive, onMounted, computed} from 'vue'
import myAxios from "../axios/index.js";
import {ElMessage} from "element-plus";

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
const borrowDialogVisible = ref(false)
const backDialogVisible = ref(false)
const borrowTime = ref<number>(0)
const borrowDay = ref<number>(0)
const borrowDays = ref(0);
const borrowSum = reactive({
  userName: '',
  bookName: '',
  beginTime: '',
  endTime:  '',
})


onMounted(()=>{
  inputValue.userName = cookie.get('username')
  userInfo.userName = cookie.get('username')
  getUserInfo()
  getBorrowInfo()
})

const changeBorrow = (row)=>{
  borrowDialogVisible.value = true
  borrowSum.userName = row.userName
  borrowSum.bookName = row.bookName
  borrowTime.value = parseInt(row.borrowTime,10)
  borrowSum.beginTime = row.beginTime
  borrowSum.endTime = row.endTime
}

const changeBack = (row)=>{
  backDialogVisible.value = true
  borrowSum.userName = row.userName
  borrowSum.bookName = row.bookName
  borrowTime.value = parseInt(row.borrowTime,10)
  borrowSum.beginTime = row.beginTime
  borrowSum.endTime = row.endTime
}

//获取用户信息
const getUserInfo = async ()=>{
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName?name='+userInfo.userName)
    if(res.data.code == 200){
      userInfo.password = res.data.data.password
      userInfo.age = res.data.data.age
      userInfo.address = res.data.data.address
      inputValue.password = res.data.data.password
      inputValue.age = res.data.data.age
      inputValue.address = res.data.data.address
    }
  }catch (e) {
    console.log(e)
  }
}

//修改用户信息
const updateUser = async ()=>{
  try {
    let res = await myAxios.post('http://localhost:8080/user/updateByName', {
      name: inputValue.userName,
      password:inputValue.password,
      age:inputValue.age,
      address:inputValue.address
    })
   if(res.data.code == 200){
     ElMessage.success(res.data.data)
    await getUserInfo()
   }else {
     ElMessage.error(res.data.data)
   }
  }catch (e) {
    console.log(e)
  }
}

//获取书本借阅数据
const getBorrowInfo = async ()=>{
  try {
    let res = await myAxios.get('http://localhost:8080/bookBorrow/getBorrowInfo?username='+userInfo.userName)
    if(res.data.code == 200){
      borrowData.value = res.data.data
    }
  }catch (e) {
    console.log(e)
  }
}

//续借
const borrow = async ()=>{
  borrowDays.value = borrowDay.value + borrowTime.value
  try {
    let res = await myAxios.post('http://localhost:8080/bookBorrow/borrowTime', {
      userName: borrowSum.userName,
      bookName: borrowSum.bookName,
      borrowTime: borrowDays.value,
      beginTime: borrowSum.beginTime,
    })
    if(res.data.code == 200){
      ElMessage.success(res.data.data)
      await getBorrowInfo()
      borrowDialogVisible.value = false
    }else {
      ElMessage.error(res.data.msg)
    }
  }catch (e) {
    console.log(e)
  }
}

//归还
const back = async ()=>{
  try {
    let res = await myAxios.post('http://localhost:8080/bookBorrow/back', {
      userName: borrowSum.userName,
      bookName: borrowSum.bookName,
      beginTime: borrowSum.beginTime,
    })
    if(res.data.code == 200){
      ElMessage.success(res.data.data)
      await getBorrowInfo()
      backDialogVisible.value = false
    }else {
      ElMessage.error(res.data.msg)
    }
  }catch (e) {
    console.log(e)
  }
}

const logout = ()=>{
  cookie.remove('token')
  router.push('/login')
}




</script>

<style scoped>

.user-main{
  width: 100%;
  height: 550px;
  display: flex;
}

.left-contain{
  width: 30%;
  height: 100%;
  display: block;
  background: rgb(253, 198, 176);
}

.span-title{
  font:oblique bold 26px "楷体";
  color: rgb(164, 59, 21);
}

.user-info{
  width: 300px;
  height: 400px;
  display: block;
  margin-top: 60px;
  margin-left: 80px;
}

.flip-card {
  background-color: transparent;
  width: 290px;
  height: 354px;
  perspective: 1000px;
  font-family: sans-serif;
}


.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.8s;
  transform-style: preserve-3d;
}

.flip-card:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front{
  box-shadow: 0 8px 14px 0 rgba(0,0,0,0.2);
  position: absolute;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  height: 100%;
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  border: 1px solid coral;
  border-radius: 1rem;
}

.flip-card-back {
  box-shadow: 0 8px 14px 0 rgba(0,0,0,0.2);
  position: absolute;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  height: 100%;
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  border: 1px solid coral;
  border-radius: 1rem;
}

.span-back-title {
  font:oblique bold 26px "楷体";
}

.span-back>input{
  width: 150px;
  height: 20px;
  border-radius: 8px;
  margin-left: 10px;
  border: 1px solid rgb(253, 198, 176);
  color: rgb(208, 68, 12);
  background-color: rgb(253, 198, 176);
}

.span-back-btn>button{
  width: 100px;
  height: 30px;
  border-radius: 10px;
  border: 1px solid rgb(253, 198, 176);
  background-color: rgb(253, 198, 176);
  font:oblique bold 16px "楷体";
}

.flip-card-front {
  background: linear-gradient(120deg, bisque 60%, rgb(255, 231, 222) 88%,
  rgb(255, 211, 195) 40%, rgba(255, 127, 80, 0.603) 48%);
  color: coral;
}

.flip-card-back {
  background: linear-gradient(120deg, rgb(255, 174, 145) 30%, coral 88%,
  bisque 40%, rgb(255, 185, 160) 78%);
  color: white;
  transform: rotateY(180deg);
}



.center-contain {
  width: 50%;
  height: 100%;
  background-color: #d58869;
}

.book-borrow-info {
  width: 100%;
  height: 520px;
  display: flex;
}

.right-contain{
  width: 20%;
  height: 100%;
  background-color: rgb(253, 198, 176);
  display: flex;
  align-items: center;
  justify-content: center;
}

.col-table-el {
  width: 100%;
  font-family: 楷体;
  font-size: 14px;
  background-color: #e0be94;
}

.right-contain-center {
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}


.btn {
  height: 4em;
  width: 12em;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 0px solid black;
  cursor: pointer;
}

.wrapper {
  height: 2em;
  width: 8em;
  position: relative;
  background: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
}

.text {
  font-size: 18px;
  z-index: 1;
  color: #000;
  padding: 4px 12px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.7);
  transition: all 0.5s ease;
  font-family: 楷体;
}

.flower {
  display: grid;
  grid-template-columns: 1em 1em;
  position: absolute;
  transition: grid-template-columns 0.8s ease;
}

.flower1 {
  top: -12px;
  left: -13px;
  transform: rotate(5deg);
}

.flower2 {
  bottom: -5px;
  left: 8px;
  transform: rotate(35deg);
}

.flower3 {
  bottom: -15px;
  transform: rotate(0deg);
}

.flower4 {
  top: -14px;
  transform: rotate(15deg);
}

.flower5 {
  right: 11px;
  top: -3px;
  transform: rotate(25deg);
}

.flower6 {
  right: -15px;
  bottom: -15px;
  transform: rotate(30deg);
}

.petal {
  height: 1em;
  width: 1em;
  border-radius: 40% 70% / 7% 90%;
  background: linear-gradient(#07a6d7, #93e0ee);
  border: 0.5px solid #96d1ec;
  z-index: 0;
  transition: width 0.8s ease, height 0.8s ease;
}

.two {
  transform: rotate(90deg);
}

.three {
  transform: rotate(270deg);
}

.four {
  transform: rotate(180deg);
}

.btn:hover .petal {
  background: linear-gradient(#0761d7, #93bdee);
  border: 0.5px solid #96b4ec;
}

.btn:hover .flower {
  grid-template-columns: 1.5em 1.5em;
}

.btn:hover .flower .petal {
  width: 1.5em;
  height: 1.5em;
}

.btn:hover .text {
  background: rgba(255, 255, 255, 0.4);
}

.btn:hover div.flower1 {
  animation: 15s linear 0s normal none infinite running flower1;
}

@keyframes flower1 {
  0% {
    transform: rotate(5deg);
  }

  100% {
    transform: rotate(365deg);
  }
}

.btn:hover div.flower2 {
  animation: 13s linear 1s normal none infinite running flower2;
}

@keyframes flower2 {
  0% {
    transform: rotate(35deg);
  }

  100% {
    transform: rotate(-325deg);
  }
}

.btn:hover div.flower3 {
  animation: 16s linear 1s normal none infinite running flower3;
}

@keyframes flower3 {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.btn:hover div.flower4 {
  animation: 17s linear 1s normal none infinite running flower4;
}

@keyframes flower4 {
  0% {
    transform: rotate(15deg);
  }

  100% {
    transform: rotate(375deg);
  }
}

.btn:hover div.flower5 {
  animation: 20s linear 1s normal none infinite running flower5;
}

@keyframes flower5 {
  0% {
    transform: rotate(25deg);
  }

  100% {
    transform: rotate(-335deg);
  }
}

.btn:hover div.flower6 {
  animation: 15s linear 1s normal none infinite running flower6;
}

@keyframes flower6 {
  0% {
    transform: rotate(30deg);
  }

  100% {
    transform: rotate(390deg);
  }
}



</style>