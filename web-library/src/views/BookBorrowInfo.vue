<template>
  <div class="borrow-info">
    <div class="main-div">
      <el-table :data="resulData" style="width: 100%">
        <el-table-column fixed prop="userName" label="用户名" width="160" />
        <el-table-column prop="bookId" label="书本ID" width="160" />
        <el-table-column prop="bookName" label="书名" width="240" />
        <el-table-column prop="borrowTime" label="借阅天数" width="170" />
        <el-table-column prop="beginTime" label="起始时间" width="250" />
        <el-table-column prop="endTime" label="结束时间" width="250" />
      </el-table>
      <el-pagination class="page"
                     v-model:current-page="pageInfo.pageNum"
                     v-model:page-size="pageInfo.pageSize"
                     :small="small"
                     :disabled="disabled"
                     :background="background"
                     layout="total, prev, pager, next"
                     :total="pageInfo.allNum"
                     @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import {ElMessage} from "element-plus";
import myAxios from "../axios/index.js";
import {onMounted} from "vue";
import {ref,reactive} from "vue";

const small = ref(false)
const background = ref(false)
const disabled = ref(false)
const pageInfo = reactive({
  pageSize:5,
  pageNum:1,
  allNum:10,
  name:''
})
const resulData = ref([])
const borrowInfo = reactive({
  userName:'',
  bookId:'',
  bookName:'',
  borrowTime:'',
  beginTime:'',
  endTime:''
})

onMounted(()=>{
  getBorrowBook()
})


const handleCurrentChange = (val: number) => {
  getBorrowBook()
}

const getBorrowBook = async ()=>{
  try {
    let res = await myAxios.post('http://localhost:8080/bookBorrow/list',{
      pageSize: pageInfo.pageSize,
      pageNum: pageInfo.pageNum,
      nameInfo: pageInfo.name
    })
    resulData.value=res.data.data.borrows
    console.log(resulData.value)
    pageInfo.allNum = res.data.data.count
  }catch (e) {
    console.log(e)
  }
}

</script>

<style scoped lang="scss">

.div-card {
  display: flex;
  .car-img {
    margin:auto;
    width: 200px;
    height: 220px;
  }
}

</style>`