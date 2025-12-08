<template>
  <div class="book-main">
    <div class="book-info" v-for="book in recommendBook" :key="book.bookId" @click="clickBook(book)">
      <div class="book-image">
        <img :src= "'http://localhost:8080/common/download?name=' +book.bookImge" alt="image" width="160px" height="170px">
      </div>
      <span style="display: flex;justify-content: center">{{book.bookName}}</span>
    </div>
  </div>
  <div class="foot-div">
    <div class="foot-contain">
      <span>基于协同过滤的图书推荐系统，通过对用户阅读行为的分析，
        巧妙利用用户或书籍之间的相似性进行个性化推荐。用户基于的方法聚焦于寻找兴趣相似的用户群，
        以此预测并推荐未知书籍；而物品基于的方法则着重分析书籍间的关联，推荐与用户喜好匹配的书籍。
        该系统有效应对信息过载，提升阅读体验，虽面临冷启动与数据稀疏挑战，
        但通过混合策略、矩阵分解及深度学习等技术不断优化，日益精准高效，
        为读者开启了一扇扇通往新知与兴趣领域的大门。</span>
    </div>
  </div>

  <el-dialog
      v-model="centerDialogVisible"
      width="360"
      align-center
      style="height:540px;background-color: #d6e5e4"
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
import myAxios from "../axios/index.js"
import {ref, reactive, onMounted} from "vue";
import { useCookies } from '@vueuse/integrations/useCookies'
import {ElMessage} from "element-plus";
const cookie = useCookies()

const recommendBook = ref([]);
const centerDialogVisible = ref(false)
const userName=reactive({
  userId:'',
  username:'',
})

//评分数据
const ratingData = reactive({
  rating:0,
  userId:0,
  bookId:0,
  bookName:'',
  ratingTime:''
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


onMounted(()=>{

  userName.username=cookie.get('username')
  getUserByName()
})

//点击书籍
const clickBook = (book)=>{
  // ElMessage.info(book)
  bookData.bookId = book.bookId
  bookData.bookImage = book.bookImge
  bookData.bookName = book.bookName
  bookData.bookPublic = book.bookPublic
  bookData.bookClassify = book.bookClassify
  bookData.bookNum = book.bookNum
  centerDialogVisible.value=true
  ratingData.bookId=book.bookId
  ratingData.bookName = book.bookName
  findRating()
}

//查找用户通过用户名
const getUserByName = async ()=>{
  try {
    let res = await myAxios.get('http://localhost:8080/user/getUserByName',{
      params:{
        name:userName.username
      }
    })
    userName.userId=res.data.data.id
    ratingData.userId=res.data.data.id
    await getRecommendBook(userName.userId)
  }catch (e) {
    console.log(e)
  }
}

//获取推荐书籍
const getRecommendBook = async (userId)=> {
  try {
    let res = await myAxios.get('http://localhost:8080/bookScore/recommend',{
      params:{
        userId:userId
      }
    })
    recommendBook.value=res.data.data
  }catch (e) {
    console.log(e)
  }
}

//评分
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

//获取星星评分
const rate = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for').slice(-1), 10);
    ratingData.rating = starIndex;
  }
};

//借书
const borrow =async ()=>{
  if(bookData.bookNum > 0){
    //ElMessage.success("借书成功")
    try {
      let res = await myAxios.put('http://localhost:8080/bookBorrow/borrow',{
        userName:userName.username,
        bookId:bookData.bookId,
        bookName:bookData.bookName,
        borrowTime:'5',
      })
      ElMessage.success(res.data.data)
      centerDialogVisible.value=false

    }catch (e) {
      console.log(e)
    }
  }else{
    ElMessage.error("借书失败")
  }
}

//查找评分
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


</script>

<style scoped>


.book-main {
  width: 100%;
  height: 350px;
  background-color: #c0d5d3;
  display:flex;
  flex-flow: row wrap;
  justify-content: center;
}

.foot-div {
  width: 100%;
  height: 200px;
  background-color: #779d99;
  display: flex;
}

.foot-contain {
  width: 100%;
  height: 100%;
  margin-left: 100px;
  margin-right: 100px;
  align-items: center;
  display: flex;
  justify-content: center;
}

.foot-contain span {
  font:oblique bold 18px "楷体";
  text-indent: 2em;
}


.book-info {
  display: flow;
  width: 160px;
  height: 200px;
  margin-right: 20px;
  margin-top: 30px;
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
