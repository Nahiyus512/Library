<template>
  <div class="book-main">
    <div class="book-contain" style="background-color: #c0d5d3">
      <div class="book-info" v-for="book in tableData" :key="book.bookId" @click="clickBook(book)">
       <div class="book-image">
          <img :src="'http://localhost:8080/common/download?name=' +book.bookImge" alt="image" width="160px" height="170px">
       </div>
       <span style="display: flex;justify-content: center">{{book.bookName}}</span>
      </div>
    </div>
    <div class="page-div">
      <el-pagination
          v-model:current-page="pageInfo.pageNum"
          v-model:page-size="pageInfo.pageSize"
          layout="prev, pager, next"
          :total="pageInfo.allNum"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
  <el-dialog
      v-model="centerDialogVisible"
      width="360"
      align-center
      style="height:540px;background-color: #d6e5e4;border-radius: 10px;"
  >
    <div class="image-div" style="display: flex;justify-content: left"><img :src="bookData.bookImage" style="width: 300px;height: 320px" > </div>
    <span>书名:{{bookData.bookName}}</span>
    <span>出版社:{{bookData.bookPublic}}</span>
    <span>分类:{{bookData.bookClassify}}</span>
    <span>数量:{{bookData.bookNum}}</span>
    <div class="rating" @click="rate($event)" style="width: 100%;height: auto">
      点击评分:<input
          v-for="n in 5"
          :key="n"
          type="radio"
          :id="'star' + n"
          :value="n"
          hidden
      />
      <label
          v-for="n in 5"
          :key="n"
          :for="'star' + n"
          :class="{ filled: n <= ratingData.rating }"
      >
        &#9733;
      </label>
      <span>评分: {{ ratingData.rating }}</span>
    </div>
    <div class="contain-div">
      <div class="inputScore-div">
        <button>
          <span @click="inputScore">评 分</span>
        </button>
      </div>
      <div class="borrow-div">
        <button>
          <span @click="borrow">借 阅</span>
        </button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted} from "vue";
import {ElMessage} from "element-plus";
import myAxios from "../axios/index.js";
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()

// 使用ref定义响应式变量

const ratingData = reactive({
  rating:0,
  userId:0,
  bookId:0,
  bookName:'',
  ratingTime:''
})

const centerDialogVisible = ref(false)

const pageInfo = reactive({
  pageSize:12,
  pageNum:1,
  allNum:20,
  bookName:''
})

//添加书本数据
const bookData = reactive({
  bookId:'',
  bookName: '',
  bookPrice: '',
  bookPublic: '',
  bookClassify: '',
  bookImage:'',
  bookDesc:'',
  bookNum:''
})

//借阅书本数据
const borrowData = reactive({
  userName:'',
  bookId:'',
  bookName:'',
  borrowTime:'',
  beginTime:'',
  endTime:''
})

onMounted(()=>{
  getBook()
  let username = cookie.get('username')
  borrowData.userName = username
  //console.log(nowDate(time))
  console.log(nowDate(lastTime))
  getUserByName(username)
})

const tableData = ref([])

const getTime = new Date().getTime(); //获取到当前时间戳
const lastTime = new Date(getTime+432000000);
const time = new Date(getTime); //创建一个日期对象
function nowDate(time) {
  const year = time.getFullYear(); // 年
  const month = (time.getMonth() + 1).toString().padStart(2, '0'); // 月
  const date = time.getDate().toString().padStart(2, '0'); // 日
  const hour = time.getHours().toString().padStart(2, '0'); // 时
  const minute = time.getMinutes().toString().padStart(2, '0'); // 分
  const second = time.getSeconds().toString().padStart(2, '0'); // 秒
  return (
      year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second
  )
}



//点击书本事件
const clickBook = (book)=>{
 // ElMessage.info(book)
  bookData.bookId = book.bookId
  bookData.bookImage = book.bookImge
  bookData.bookName = book.bookName
  bookData.bookPublic = book.bookPublic
  bookData.bookClassify = book.bookClassify
  bookData.bookNum = book.bookNum
  centerDialogVisible.value=true

  //查看用户对书本是否进行过评分
  ratingData.bookId=book.bookId
  ratingData.bookName = book.bookName
  findRating()

}


const handleCurrentChange = (val: number) => {
  getBook()
}


const getUserByName = async (username)=>{
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName',{
      params:{
        name:username
      }
    })
    ratingData.userId = res.data.data.id
  }catch (e) {
    console.log(e)
  }
}

const getBook = async ()=> {
  try {
    let res = await myAxios.post('http://localhost:8080/book/listPage', {
      pageSize: pageInfo.pageSize,
      pageNum: pageInfo.pageNum,
      nameInfo: pageInfo.bookName
    })
    //console.log(res)
    tableData.value = JSON.parse(JSON.stringify(res.data.data.bookList))
    //console.log(tableData)
    pageInfo.allNum = JSON.parse(JSON.stringify(res.data.data.count))
  } catch (e) {
    console.log(e)
  }
}

const borrow =async ()=>{
  if(bookData.bookNum > 0){
    //ElMessage.success("借书成功")
    try {
      let res = await myAxios.put('http://localhost:8080/bookBorrow/borrow',{
        userName:borrowData.userName,
        bookId:bookData.bookId,
        bookName:bookData.bookName,
        borrowTime:'5',
        beginTime:nowDate(time),
        endTime:nowDate(lastTime)
      })
      ElMessage.success(res.data.data)
      centerDialogVisible.value=false
      await getBook()
    }catch (e) {
      console.log(e)
    }
  }else{
    ElMessage.error("借书失败")
  }
}

const rate = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for').slice(-1), 10);
    ratingData.rating = starIndex;
  }
};

const findRating = async ()=>{
  try {
    let res = await myAxios.get('http://localhost:8080/bookScore/findScore', {
      params: {
        userId: ratingData.userId,
        bookId: ratingData.bookId
      }
    });
    ratingData.rating = res.data.data
  }catch (e) {
    console.log(e)
  }
}

const inputScore = async ()=>{
  try {
    let rs = await myAxios.put('http://localhost:8080/bookScore/updateScore',{
      userId:ratingData.userId,
      bookId:ratingData.bookId,
      score:ratingData.rating
    })
    ElMessage.success(rs.data.data)
  }catch (e) {
    console.log(e)
  }
}

</script>

<style scoped>

.book-main {
  width: 100%;
  height: 515px;
  display:flex;
  flex-flow: row wrap;
  justify-content: center;
}

.book-contain{
  width: 100%;
  height: 513px;
  flex-wrap: wrap; /* 允许子元素换行 */
  display: flex;
  justify-content: center;
}


.book-info {
  /*display: flow;*/
  width: 160px;
  height: 200px;
  margin-right: 20px;
  margin-top: 10px;
  border-radius: 30px;
  background: #e0e0e0;
  box-shadow: 5px 5px 10px #bebebe,
  -10px -10px 20px #ffffff;
}

.book-info:hover{
  transform: scale(1.1);
  box-shadow: 0 25px 40px rgba(0,0,0,0.5);
}

.book-image {
  border-radius: 30px 30px 0 0;
  width: 100%;
  height:170px;
  overflow: hidden;
  position: relative;
}


.page-div {
  position: center;
  clear: both;
  display: flex;
  justify-content: center;
}

span {
  display: flex;
  margin-top: 4px;
  justify-content: left;
  font:oblique bold 16px "楷体";
}

.contain-div {
  display: flex;
}
/*提交评分样式*/
.inputScore-div {
  margin-top: 10px;
  margin-right: 40px;
  display: flex;
  justify-content: left;
}


/* From uiverse.io by @Ali-Tahmazi99 */
.inputScore-div button {
  display: inline-block;
  width: 100px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid #03045e;
  position: relative;
  overflow: hidden;
  transition: all 0.5s ease-in;
  z-index: 1;
}

.inputScore-div button::before,
.inputScore-div button::after {
  content: '';
  position: absolute;
  top: 0;
  width: 0;
  height: 100%;
  transform: skew(15deg);
  transition: all 0.5s;
  overflow: hidden;
  z-index: -1;
}

.inputScore-div button::before {
  left: -10px;
  background: #240046;
}

.inputScore-div button::after {
  right: -10px;
  background: #5a189a;
}

.inputScore-div button:hover::before,
.inputScore-div button:hover::after {
  width: 58%;
}

.inputScore-div button:hover span {
  color: #e0aaff;
  transition: 0.3s;
}

.inputScore-div button span {
  color: #03045e;
  font-size: 16px;
  transition: all 0.3s ease-in;
  display: flex;
  justify-content: center;
}



/*借阅书本样式*/
.borrow-div {
  margin-top: 10px;
  margin-left: 40px;
  display: flex;
  justify-content: right;
}


/* From uiverse.io by @Ali-Tahmazi99 */
.borrow-div button {
  display: inline-block;
  width: 100px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid #03045e;
  position: relative;
  overflow: hidden;
  transition: all 0.5s ease-in;
  z-index: 1;
}

.borrow-div button::before,
.borrow-div button::after {
  content: '';
  position: absolute;
  top: 0;
  width: 0;
  height: 100%;
  transform: skew(15deg);
  transition: all 0.5s;
  overflow: hidden;
  z-index: -1;
}

.borrow-div button::before {
  left: -10px;
  background: #240046;
}

.borrow-div button::after {
  right: -10px;
  background: #5a189a;
}

.borrow-div button:hover::before,
.borrow-div button:hover::after {
  width: 58%;
}

.borrow-div button:hover span {
  color: #e0aaff;
  transition: 0.3s;
}

.borrow-div button span {
  color: #03045e;
  font-size: 16px;
  transition: all 0.3s ease-in;
  display: flex;
  justify-content: center;
}


.rating {
  display: inline-block;
  margin-top: 4px;
  font:oblique bold 16px "楷体";
  cursor: pointer;
}

.rating label {
  cursor: pointer;
}

.filled {
  color: #ffcc00;
}

.rating label:hover,
.rating label:hover ~ label {
  color: #ffcc00;
}

</style>
