<template>
  <div class="login-container">
    <div class="slider">
      <div :class="active=== 1 ? 'form' : 'form hidden'">
        <div class="title">欢迎 <b>回来</b></div>
        <div class="subtitle">登录你的账户</div>
        <div class="input-form">
          <input type="text" v-model="info.username" @input="handleInput" placeholder="账号"/>
          <span class="lable">账号</span>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        </div>
        <div class="input-form">
          <input type="password" v-model="info.password" placeholder="密码"/>
          <span class="lable">密码</span>
        </div>
        <div class="input-code">
          <input type="text" class="text-code" v-model="info.captcha" placeholder="验证码"/>
          <img :src=captchaImg @click="getCaptcha">
        </div>
        <button @click="loginClick">登录</button>
      </div>
      <div :class="active=== 2 ? 'form' : 'form hidden'">
        <div class="title">开始</div>
        <div class="subtitle">注册你的账户</div>
        <div class="input-form">
          <input type="text" v-model="info.newName" @input="countInput" placeholder="账号"/>
          <span class="lable">账号</span>
          <p v-if="countError" class="error-message">{{ countError }}</p>
        </div>
        <div class="input-form">
          <input type="text" v-model="info.firstPassword" @input="passInput" placeholder="密码"/>
          <span class="lable">密码</span>
          <p v-if="passwordError" class="error-message">{{ passwordError }}</p>
        </div>
        <div class="input-form">
          <input type="text" v-model="info.secondPassword"  placeholder="确认密码"/>
          <span class="lable">确认密码</span>
        </div>
        <button @click="logonClick">注册</button>
      </div>
      <div :class="active===1 ? 'card' : 'card active'">
        <div class="head">
          <div class="name">图书推荐系统</div>
        </div>
        <div class="desc">图书推荐系统是一种利用信息技术为用户提供个性化的图书推荐的系统。图书推荐系统可以根据用户的阅读历史、评价和偏好，分析用户的兴趣和需求，从海量的图书和文献中筛选出适合用户的阅读材料。图书推荐系统可以提高用户的阅读体验，节省时间，并促进知识的传播。图书推荐系统的实现方法有多种，常见的有基于协同过滤的推荐算法。</div>
        <div class="btn">
          {{ active === 1 ? '新用户 ?' : '已有账号' }}
          <button @click="active = (active === 1) ? 2 : 1">
            {{ active === 1 ? '去注册' : '去登陆' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

import {ref, reactive, computed, onMounted} from "vue";
import {useRouter} from "vue-router";
const router = useRouter();
import myAxios from "../axios/index.js"
import {setToken} from "../utils/token.js";
import {ElMessage} from "element-plus";
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()
import bus from "../bus/index"


const errorMessage = ref('');
const countError = ref('');
const passwordError=ref('')
const active = ref(1);
const captchaImg = ref('')
const info = reactive({
  username:'',
  password:'',
  captcha:'',
  newName:'',
  firstPassword:'',
  secondPassword:'',
  uuid:'',
})

//定义登录校验规则
const regex = /^[A-Za-z0-9]{4,15}$/;
onMounted(()=>{
  console.log("sss")
  getCaptcha()
})

//校验账号输入规则
const handleInput = (event) => {
  const value = event.target.value;
  // 检查输入值是否符合正则表达式
  if (regex.test(value)) {
    errorMessage.value = ''; // 符合时清除错误信息
  } else {
    errorMessage.value = '输入内容不正确，请输入长度为4-15位的数字和字母'; // 不符合时设置错误信息
  }
  // 注意：这里没有直接修改event.target.value来阻止输入，而是通过errorMessage提示用户
  // 若要直接限制输入，可参考之前示例中的逻辑，但通常更好的做法是给予用户反馈而非直接阻止
};

//校验注册账号输入规则
const countInput = (event) => {
  const value = event.target.value;
  // 检查输入值是否符合正则表达式
  if (regex.test(value)) {
    countError.value = ''; // 符合时清除错误信息
  } else {
    countError.value = '输入内容不正确，请输入长度为4-15位的数字和字母'; // 不符合时设置错误信息
  }
  // 注意：这里没有直接修改event.target.value来阻止输入，而是通过errorMessage提示用户
  // 若要直接限制输入，可参考之前示例中的逻辑，但通常更好的做法是给予用户反馈而非直接阻止
};

//校验注册账号输入规则
const passInput = (event) => {
  const value = event.target.value;
  // 检查输入值是否符合正则表达式
  if (regex.test(value)) {
    passwordError.value = ''; // 符合时清除错误信息
  } else {
    passwordError.value = '输入内容不正确，请输入长度为4-15位的数字和字母'; // 不符合时设置错误信息
  }
  // 注意：这里没有直接修改event.target.value来阻止输入，而是通过errorMessage提示用户
  // 若要直接限制输入，可参考之前示例中的逻辑，但通常更好的做法是给予用户反馈而非直接阻止
};

// 初始化时也可以检查一次输入值
if (info.username) {
  handleInput({ target: { value: info.username } });
}

//获取验证码
async function getCaptcha() {
  try {
    let res = await myAxios.get("http://localhost:8080/code/captcha")
    console.log(res)
    captchaImg.value = "data:image/png;base64," + res.data.data.imageBase64
    info.uuid = res.data.data.uuid
  } catch (e) {
    console.log(e)
  }
}


async function loginClick() {
  try {
    if(info.username === '')
    {
      ElMessage.error("账号不能为空")
      return;
    }
    if(info.password === '')
    {
      ElMessage.error("密码不能为空");
      return;
    }
    let res = await myAxios.post("http://localhost:8080/user/login",{
      username:info.username,
      password:info.password,
      uuid:info.uuid,
      captcha:info.captcha
    })
    if(res.data.code === 200) {
      setToken(res.data.data.token)
      console.log('传递用户名')
      cookie.set('username',info.username)
      //alert("登录成功")
      if(info.username === 'admin') {
        ElMessage.success("管理员登录成功")
        setTimeout(()=>{
          router.push("/admin")
          cookie.set('adminId','pass')
        },1000)
      }else{
        ElMessage.success("用户登录成功")
        setTimeout(()=>{
          router.push("/home")
        },1000)
      }
    }
    else {
      ElMessage.error(res.data.msg)
    }
  }catch (e) {
    console.log("登录失败")
  }
}

async function logonClick() {
  try {
    if(info.firstPassword != info.secondPassword)
    {
      ElMessage.error("两次密码不一致")
      return;
    }
    if(countError.value !== '') {
      ElMessage.error("账号不符合规则")
      return;
    }
    if(passwordError.value !== '') {
      ElMessage.error("密码不符合规则")
      return;
    }
    let res = await myAxios.post("http://localhost:8080/user/logon",{
      name:info.newName,
      password:info.firstPassword
    })
    console.log(res)
    if(res.data.code === 200){
      ElMessage.success("用户注册成功")
    }else {
      ElMessage.error(res.data.msg)
    }
  }catch (e) {
    console.log(e)
  }
  info.newName=''
  info.firstPassword=''
  info.secondPassword=''
}

</script>

<style lang="scss">

.login-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url("../assets/bg01.jpg") no-repeat center center;
  background-size: 100% 100%;
  -webkit-filter: opacity(70%);
  filter: opacity(70%);

  .slider {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    .form {
      width: 400px;
      height: 500px;
      background: rgb(17,25,40,0.75);
      //backdrop-filter: blur(16px) saturate(0);
      border-radius: 10px;
      border: 1px solid rgba(255,255,255,0.15);
      padding: 40px 60px;
      box-shadow: rgba(50,50,93,0.25) 50px 50px 100px -20px,
      rgba(0,0,0,0.5) 30px 30px 600px -30px,
      rgba(212,217,222,0.35) 2px -2px 6px 0px inset;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: flex-start;
      margin: 0 2px;
      z-index: 3;
      transition: 0.5s ease-in-out;

      &.hidden {
        height: 395px;
        box-shadow: none;
        z-index: 1;
      }

      .title {
        font-size: 18px;
        color: white;
        letter-spacing: 1px;
        font-weight: 300;
      }

      .subtitle {
        font-size: 11px;
        color: rgb(246,240,255);
        letter-spacing: 1px;
        margin-bottom: 35px;
      }

      .input-form {
        width: 100%;
        position: relative;
        margin-bottom: 20px;

        input {
          width: 100%;
          height: 35px;
          border: none;
          outline: 1.5px solid rgb(200,200,220);
          background: transparent;
          border-radius: 8px;
          font-size: 12px;
          padding: 0 15px;
          color: rgb(246,259,255);
          &::placeholder {
            color: rgb(175,180,190);
          }
          &:focus {
            outline: 1.5px solid rgb(224,229,240);
            &::placeholder{
              opacity: 0;
            }
            & + .lable {
              opacity: 1;
              top: -20px
            }
          }
          &:not(:placeholder-shown) + .lable {
            opacity: 1;
            top: -20px;
          }
        }

        .lable {
          position: absolute;
          top: 0;
          left: 0;
          color: rgb(246,259,255);
          font-size: 11px;
          font-weight: bold;
          transition: 0.25s ease-out;
          opacity: 0;
        }
        .error-message{
          height: 10px;
          color: red;
          font-size: 10px;
        }

      }
      .input-code {
        width: 100%;
        margin-bottom: 20px;
        display: flex;
        //height: 35px;
        .text-code {
          width: 50%;
          height: 32px;
          margin-right: 10px;
          border-radius: 5px;
          background-color: transparent;
          border: none;
          outline: 1.5px solid rgb(200,200,220);
          font-size: 12px;
          padding: 0 15px;
        }
        img {
          width: 120px;
          height: 35px;
          padding: 0 15px;
        }
      }
      button {
        width: 107%;
        height: 35px;
        background: rgb(36,217,127);
        color: #ffffff;
        border: none;
        outline: none;
        border-radius: 5px;
        font-weight: bold;
        font-size: 12px;
        cursor: pointer;
        margin-top: 5px;
      }
    }

    .card {
      position: absolute;
      right: 0px;
      top: 50%;
      transform: translate(0,-50%);
      width: 448px;
      height: 400px;
      background: url("../assets/bg03.png");
      background-size: 100% 100%;
      padding: 40px;
      border-radius: 0 10px 10px 0;
      transition: 0.5s ease-in-out;
      z-index: 2;
      &.active {
        right: calc(100% - 530px);
        border-radius: 10px 0 0 10px;
      }

      .head {
        font-size: 34px;
        margin-bottom: 35px;
        .name {
          font-weight: 300;
          font-style: italic;
        }
      }

      .desc {
        font-size: 14px;
        text-align: justify;
        margin-bottom: 35px;
        font-weight: bold;
      }

      .btn {
        font-size: 14px;
        button {
          background: rgb(36,217,127);
          margin-top: 100px;
          font-size: 14px;
          padding: 5px 15px;
          border: none;
          outline: none;
          border-radius: 5px;
          cursor: pointer;
          margin-left: 10px;
        }
      }
    }
  }
}

</style>
