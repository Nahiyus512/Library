<template>
  <div class="advice">
    <div class="advice-contain">
      <el-table :data="adviceData" style="width: 100%">
        <el-table-column fixed prop="id" label="id" width="50" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="info" label="建议内容" width="280" />
        <el-table-column prop="infoTime" label="建议时间" width="200" />
        <el-table-column prop="reply" label="回复内容" width="280" />
        <el-table-column prop="replyTime" label="回复时间" width="200" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="refMsg(scope.row)">
              回复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
<!--      <el-pagination class="page"-->
<!--                     v-model:current-page="pageInfo.pageNum"-->
<!--                     v-model:page-size="pageInfo.pageSize"-->
<!--                     :small="small"-->
<!--                     :disabled="disabled"-->
<!--                     :background="background"-->
<!--                     layout="total, prev, pager, next"-->
<!--                     :total="pageInfo.allNum"-->
<!--                     @size-change="handleSizeChange"-->
<!--                     @current-change="handleCurrentChange"-->
<!--      />-->
    </div>
  </div>
  <el-dialog v-model="dialogReply" title="回复建议" width="500" style="background-color: #f3f5f8">
    <span>用户{{rowData.userName}}建议:</span>
    <div class="infoMsg">
      {{rowData.info}}
    </div>
    <span>回复内容:</span><br>
    <textarea v-model="rowData.reply" rows="10" cols="50" ></textarea>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogReply = false">取消</el-button>
        <el-button type="primary" @click="clickOk">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import myAxios from "../axios/index.js";
import  {ElMessage} from "element-plus";

const small = ref(false)
const background = ref(false)
const disabled = ref(false)
const adviceData = ref([])
const dialogReply = ref(false)

const pageInfo = reactive({
  pageSize:5,
  pageNum:1,
  allNum:10,
  name:''
})

const rowData = reactive({
  id:'',
  userName:'',
  info:'',
  infoTime:'',
  reply:'',
  replyTime:''
})

onMounted(() => {
  getAdvice()
})

const refMsg = (row) => {
  dialogReply.value = true
  rowData.id = row.id
  rowData.userName = row.userName
  rowData.info = row.info
  rowData.infoTime = row.infoTime
  rowData.reply = row.reply
  //console.log(row)
}


async function getAdvice(){
  try {
    let res = await myAxios.get('http://localhost:8080/advice/getAll')
    adviceData.value = res.data.data
    //console.log(adviceData.value)
  }catch (e) {
    console.log(e)
  }
}

async function clickOk(){
  try {
    let res = await myAxios.post('http://localhost:8080/advice/reply',{
      id:rowData.id,
      reply:rowData.reply,
    })
    if(res.data.code === 200){
      await getAdvice();
      dialogReply.value = false
      ElMessage.success('回复成功')
    }else {
      ElMessage.error('回复失败')
    }
  }catch (e) {
    console.log(e)
  }
}

</script>

<style scoped>

.infoMsg {
  width: 420px;
  height: 200px;
  border: 1px solid #000;
  font:oblique bold 16px "楷体";
}

span {
  font:oblique bold 18px "楷体";
}

textarea {
  font:oblique bold 16px "楷体";
}

</style>