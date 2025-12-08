<template>
  <el-row class="tac">
    <el-col :span="24">
      <h3 class="mb-2">欢迎使用图书推荐系统</h3>
      <el-menu
          active-text-color="#ffd04b"
          background-color="#545c64"
          class="el-menu"
          default-active="2"
          text-color="#fff"
      >
        <el-menu-item index="1">
          <el-icon><icon-menu /></el-icon>
          <span>
            <RouterLink :to="{path:'/userInfo'}" active-class="active">用户信息管理</RouterLink>
          </span>
        </el-menu-item>
        <el-menu-item index="2">
          <el-icon><icon-menu /></el-icon>
          <span>
            <RouterLink :to="{path:'/bookInfo'}" active-class="active">书本信息管理</RouterLink>
          </span>
        </el-menu-item>
        <el-menu-item index="3">
          <el-icon><icon-menu /></el-icon>
          <span>
            <RouterLink :to="{path:'/bookClass'}" active-class="active">书本分类管理</RouterLink>
          </span>
        </el-menu-item>
        <el-menu-item index=4>
          <el-icon><document /></el-icon>
          <span>
            <RouterLink :to="{path:'/bookBorrowInfo'}" active-class="active">书本借阅管理</RouterLink>
          </span>
        </el-menu-item>
        <el-menu-item index="5">
          <el-icon><document /></el-icon>
          <span>
            <RouterLink :to="{path:'/advice'}" active-class="active">用户建议</RouterLink>
          </span>
        </el-menu-item>
        <el-menu-item index="6">
          <el-icon><setting /></el-icon>
          <span>
            <RouterLink :to="{path:'/sysInfo'}" active-class="active">系统信息管理</RouterLink>
          </span>
        </el-menu-item>
      </el-menu>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {RouterView,RouterLink} from "vue-router";
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'

</script>

<style>
.mb-2{
  justify-content: center;
  display: flex;
  font-weight: 300;
  font-style: italic;
  font-family: 楷体;
  font-size: 1.2em;
}

.el-menu{
  width: 100%;
}

span a {
  display: block;
  text-align: center;
  line-height: 40px;
  text-decoration: none;
  color: white;
}
</style>
