<template>
<div class="suggest">
  <div class="suggest-header">
    <span>留言建议</span>
  </div>
  <div class="suggest-body">
    <div class="suggest-contain">
      <div class="suggest-item" v-for="(item, index) in suggestions" :key="index">
        <div class="right-item">
          <span>{{item.userName}}: </span>
          <span class="span-info">{{item.info}} 。</span>
          <span>{{item.infoTime}}</span>
        </div>
        <div class="left-item" v-if="item.reply">
          <span class="span-user">admin: </span>
          <span class="span-reply">{{item.reply}}</span>
          <span >{{ item.replyTime }}</span>
        </div>
        <div class="div-line"></div>
      </div>
    </div>
  </div>
  <div class="suggest-footer">
    <input class="input" v-model="inputValue" placeholder="text">
    <button @click="inputBtn">
      <span> 提交</span>
    </button>
  </div>
</div>
</template>

<script setup lang="ts">

import {onMounted, reactive, ref} from 'vue'
import myAxios from '../axios/index.js'
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()

const inputValue = ref('')
const userName = ref('')

const suggestions = ref([]);

onMounted(() => {
  userName.value = cookie.get('username')
  getUserAdvises()
})

const inputBtn = () => {
  postSuggest()
  inputValue.value = ''
}

async function getUserAdvises() {
  try {
    let res = await myAxios.get('http://localhost:8080/advice/get?userName=' + userName.value)
    suggestions.value = res.data.data
    console.log(res)
  }catch (error) {
    console.log(error)
  }
}

async function postSuggest() {
  try {
    let res = await myAxios.post('http://localhost:8080/advice/input', {
      info: inputValue.value,
      userName: userName.value
    })
    console.log(res)
    await getUserAdvises()
  }catch (error) {
    console.log(error)
  }
}

</script>

<style scoped>

.suggest {
  width: 100%;
  height: 550px;
  display: flex;
  flex-direction: column;
  background-color: #8bb451;
}

.suggest-header {
  width: 100%;
  height: 80px;
  background-color: #71886d;
  display: flex;
  justify-content: center;
  align-items: center;
  font-style: italic;
}
.suggest-header span {
  font:oblique bold 25px "楷体";
}

.suggest-body {
  width: 100%;
  height: 450px;
  background-color: #64967E;
  display: flex;
  justify-content: center;
}

.suggest-contain {
  width: 80%;
  height: 100%;
  background-color: #e3e0e5;
  justify-content: center;
  overflow-y: scroll; /* 添加此属性以启用滚动条 */
}

/* 如果需要的话，你还可以为滚动条添加自定义样式 */
::-webkit-scrollbar {
  width: 8px; /* 滚动条宽度 */
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2); /* 滚动条颜色 */
  border-radius: 5px; /* 圆角 */
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1); /* 滚动条轨道的颜色 */
}


.suggest-item {
  font:oblique bold 18px "楷体";
  margin-top: 10px;
}
.left-item {
  display: flex;
  flex-direction: row;
  justify-content: left; /* 或者使用 "flex-end" 如果你想让所有项目都贴紧右边 */
  padding-left: 20px;
  flex-wrap: wrap;
}

.left-item span {
  margin-right: 5px;
  flex-wrap: wrap;
}

.right-item {
  display: flex;
  flex-direction: row;
  justify-content: right; /* 或者使用 "flex-end" 如果你想让所有项目都贴紧右边 */
  padding-right: 20px;
  flex-wrap: wrap;
}
.right-item span {
  margin-left: 5px;
}

.span-info{
  max-width: 80%;
  word-wrap: break-word;
  flex-wrap: wrap;
}

.div-line {
  display: flex;
  width: 100%;
  height: 1px;
  background-color: #7c7373;
  margin-top: 10px;
}

.suggest-footer {
  width: 100%;
  height: 120px;
  background-color: #71886d;
  display: flex;
  justify-content: center;
  align-items: center;
}

.input {
  border: 2px solid transparent;
  width: 30em;
  height: 2.5em;
  padding-left: 0.8em;
  outline: none;
  overflow: hidden;
  background-color: #F3F3F3;
  border-radius: 10px;
  transition: all 0.5s;
}

.input:hover,
.input:focus {
  border: 2px solid #4A9DEC;
  box-shadow: 0px 0px 0px 7px rgb(74, 157, 236, 20%);
  background-color: white;
}

button {
  position: relative;
  height: 2.5em;
  padding: 0 30px;
  border: 2px solid #000;
  background: #e8e8e8;
  user-select: none;
  white-space: nowrap;
  transition: all .05s linear;
  font-family: inherit;
  margin-left: 20px;
}

button:before, button:after {
  content: "";
  position: absolute;
  background: #e8e8e8;
  transition: all .2s linear;
}

button:before {
  width: calc(100% + 6px);
  height: calc(100% - 16px);
  top: 8px;
  left: -3px;
}

button:after {
  width: calc(100% - 16px);
  height: calc(100% + 6px);
  top: -3px;
  left: 8px;
}

button:hover {
  cursor: crosshair;
}

button:active {
  transform: scale(0.95);
}

button:hover:before {
  height: calc(100% - 32px);
  top: 16px;
}

button:hover:after {
  width: calc(100% - 32px);
  left: 16px;
}

button span {
  z-index: 3;
  position: relative;
  font:oblique bold 15px "楷体";
}

</style>