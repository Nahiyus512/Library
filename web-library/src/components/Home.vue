<template>
<div class="header-div" style="width: 100%;height: 200px;">
  <div class="header-label">
    <div class="title-div">图书推荐系统</div>
    <span class="username-span">欢迎您:  {{username}}  .</span>
  </div>
  <div class="navigate" style="height: 120px">
    <RouterLink :to="{path:'/userIndex'}" active-class="active">首页</RouterLink>
    <RouterLink :to="{path:'/book'}" active-class="active">图书信息</RouterLink>
    <RouterLink :to="{path:'/bookRecommend'}" active-class="active">好书推荐</RouterLink>
    <RouterLink :to="{path:'/suggest'}" active-class="active">留言建议</RouterLink>
    <RouterLink :to="{path:'/user'}" active-class="active">个人中心</RouterLink>
  </div>
</div>
  <div class="home-main" style="width: 100%;height: 515px;">
    <RouterView></RouterView>
  </div>
</template>

<script setup lang="ts">
import {RouterView,RouterLink} from "vue-router";
import {onMounted,ref} from "vue";
import myAxios from "../axios/index.js";
import bus from "../bus/index";
import {ElMessage} from "element-plus";
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()

const username = ref('')
onMounted(()=>{
  username.value = cookie.get('username')
  // test()
  bus.on('msg',(val)=>{ElMessage.info('val==>',val.username)})
})


async function test() {
  let res = await myAxios.get("http://localhost:8080/book/home")
 // console.log("res===>",res)
}

</script>

<style scoped>

.header-label {
  width: 100%;
  height: 80px;
  display: block;
  margin: 0 auto;
  background-image: url("../assets/homebg02.png");
  background-size: cover;
}

.title-div {
  width: 100%;
  height: 60px;
  font-size: 23px;
  font-weight: 900;
  font-style: italic;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 楷体;
}

.username-span {
  width: 100%;
  font-size: 16px;
  display: block;
  text-align: right;
}

.navigate {
  display: flex;
  justify-content: center;
  margin: 0 auto;
  background-color: #8c939d;
}

.navigate a {
  display: block;
  text-align: center;
  margin-right: 10px;
  margin-top: 40px;
  width: 90px;
  height: 40px;
  line-height: 40px;
  border-radius: 10px;
  background-color: gray;
  text-decoration: none;
  color: white;
  font-size: 10px;
  letter-spacing: 5px;
}

.navigate a.active {
  background-color: #64967E;
  color: #ffc268;
  font-weight: 900;
  text-shadow: 0 0 1px black;
  font-family: 微软雅黑;
}

</style>