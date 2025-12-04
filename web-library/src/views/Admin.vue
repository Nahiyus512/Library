<template>
  <div class="common-layout">
    <el-container class="container">
      <el-header class="header">
        图书推荐系统
      </el-header>
      <div class="header-left">
        <span class="username-span">欢迎你: {{username}}</span>
      </div>
      <el-container>
        <el-aside class="aside">
          <Menu></Menu>
        </el-aside>
        <el-main class="el-main">
          <div class="main-view">
            <RouterView></RouterView>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<script lang="ts" setup>
import {RouterView, useRouter} from "vue-router";
const router = useRouter();
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()
import Menu from './Menu.vue'
import myAxios from "../axios/index.js"
import {onMounted, ref} from "vue";

const username=ref('')

onMounted(()=>{
  // test()
  let pass = cookie.get('adminId')
  username.value=cookie.get('username')
  //console.log(pass)
  if(pass == null) {
    router.push('/login')
  }
})

async function test() {
  let res = await myAxios.get("http://localhost:8080/book/home")
  console.log("res===>",res)
}


</script>

<style lang="scss">

.common-layout {
  height: 100vh;
  .container {
    height: 100vh;
    .header {
      height: 12vh;
      font-size: 20px;
      font-weight: bold;
      font-style: italic;
      justify-content: center;
      display: flex;
      background-color: gray;
      padding-top: 40px;
    }
    .header-left {
      display: flex;
      justify-content: flex-end;
      width: 100%;
      height: 50px;
      align-items: center;
      background-color: gray;
      .username-span {
        margin-right: 30px;
      }
    }
    .aside{
      width:250px;
      background-color: #545c64;
    }
    .el-main{
      padding: 0;
      .page {
        justify-content: center;
        display: flex;
      }
    }
  }
}



</style>