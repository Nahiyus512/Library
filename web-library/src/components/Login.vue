<template>
  <div class="swiss-login-container">
    <div class="swiss-grid-bg"></div>
    <canvas id="particle-canvas" class="particle-bg"></canvas>
    
    <div class="main-card">
      <!-- Left Panel: Brand / Typography -->
      <div class="brand-panel">
        <div class="brand-content">
          <h1 class="hero-text">
            <span class="highlight">图书馆</span>
            <span class="highlight">Library</span>
          </h1>
          <p class="brand-desc">万卷云霞静候君，此间灯火照书深</p>
          
          <div class="brand-footer">
            <span>2025</span>
            <div class="line"></div>
          </div>
        </div>
      </div>

      <!-- Right Panel: Form -->
      <div class="form-panel">
        <div class="form-header">
          <div class="tabs">
            <span :class="['tab', active === 1 ? 'active' : '']" @click="switchTab(1)">登录</span>
            <span :class="['tab', active === 2 ? 'active' : '']" @click="switchTab(2)">注册</span>
          </div>
        </div>

        <div class="form-content-wrapper">
          <transition name="fade-slide" mode="out-in">
            <!-- Login Form -->
            <div v-if="active === 1" key="login" class="form-content">
              <div class="input-group">
                <input type="text" v-model="info.username" @input="handleInput" placeholder=" " id="username"/>
                <label for="username">用户名</label>
                <span class="border-animation"></span>
                <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
              </div>

              <div class="input-group">
                <input type="password" v-model="info.password" placeholder=" " id="password"/>
                <label for="password">密码</label>
                <span class="border-animation"></span>
              </div>

              <div class="input-group captcha-group">
                <div class="captcha-wrapper">
                  <input type="text" v-model="info.captcha" placeholder=" " id="captcha"/>
                  <label for="captcha">验证码</label>
                  <span class="border-animation"></span>
                </div>
                <img :src="captchaImg" @click="getCaptcha" alt="captcha" class="captcha-img"/>
              </div>

              <button class="cta-btn" @click="loginClick">
                <span class="btn-text">登录</span>
                <span class="btn-icon">↩︎</span>
              </button>
            </div>

            <!-- Register Form -->
            <div v-else key="register" class="form-content">
              <div class="input-group">
                <input type="text" v-model="info.newName" @input="countInput" placeholder=" " id="reg-user"/>
                <label for="reg-user">新用户名</label>
                <span class="border-animation"></span>
                <p v-if="countError" class="error-msg">{{ countError }}</p>
              </div>

              <div class="input-group">
                <input type="password" v-model="info.firstPassword" @input="passInput" placeholder=" " id="reg-pass"/>
                <label for="reg-pass">密码</label>
                <span class="border-animation"></span>
                <p v-if="passwordError" class="error-msg">{{ passwordError }}</p>
              </div>

              <div class="input-group">
                <input type="password" v-model="info.secondPassword" placeholder=" " id="reg-confirm"/>
                <label for="reg-confirm">确认密码</label>
                <span class="border-animation"></span>
              </div>

              <button class="cta-btn" @click="logonClick">
                <span class="btn-text">注册</span>
                <span class="btn-icon">+</span>
              </button>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from "vue";
import {useRouter} from "vue-router";
const router = useRouter();
import myAxios from "../axios/index.js"
import {setToken} from "../utils/token.js";
import {ElMessage} from "element-plus";
import { useCookies } from '@vueuse/integrations/useCookies'
const cookie = useCookies()

const errorMessage = ref('');
const countError = ref('');
const passwordError = ref('')
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

const regex = /^[A-Za-z0-9]{4,15}$/;

onMounted(()=>{
  getCaptcha()
  initParticles()
})

const initParticles = () => {
  const canvas = document.getElementById('particle-canvas');
  const ctx = canvas.getContext('2d');
  
  let width, height;
  let particles = [];
  
  // Configuration
  const particleCount = 60;
  const connectionDistance = 150;
  const mouseDistance = 200;

  const resize = () => {
    width = canvas.width = window.innerWidth;
    height = canvas.height = window.innerHeight;
  };
  
  window.addEventListener('resize', resize);
  resize();

  class Particle {
    constructor() {
      this.x = Math.random() * width;
      this.y = Math.random() * height;
      this.vx = (Math.random() - 0.5) * 0.5;
      this.vy = (Math.random() - 0.5) * 0.5;
      this.size = Math.random() * 2 + 1;
    }

    update() {
      this.x += this.vx;
      this.y += this.vy;

      if (this.x < 0 || this.x > width) this.vx *= -1;
      if (this.y < 0 || this.y > height) this.vy *= -1;
    }

    draw() {
      ctx.fillStyle = '#000000'; // Black particles
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
      ctx.fill();
    }
  }

  for (let i = 0; i < particleCount; i++) {
    particles.push(new Particle());
  }

  const animate = () => {
    ctx.clearRect(0, 0, width, height);
    
    particles.forEach((p, index) => {
      p.update();
      p.draw();

      // Connect particles
      for (let j = index + 1; j < particles.length; j++) {
        const p2 = particles[j];
        const dx = p.x - p2.x;
        const dy = p.y - p2.y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < connectionDistance) {
          ctx.beginPath();
          ctx.strokeStyle = `rgba(0, 0, 0, ${1 - distance / connectionDistance})`; // Black lines with fade
          ctx.lineWidth = 0.5;
          ctx.moveTo(p.x, p.y);
          ctx.lineTo(p2.x, p2.y);
          ctx.stroke();
        }
      }
    });

    requestAnimationFrame(animate);
  };

  animate();
};

const switchTab = (tab) => {
  active.value = tab;
}

const handleInput = (event) => {
  const value = event.target.value;
  if (regex.test(value)) {
    errorMessage.value = '';
  } else {
    errorMessage.value = '4-15个字符，仅限字母数字';
  }
};

const countInput = (event) => {
  const value = event.target.value;
  if (regex.test(value)) {
    countError.value = '';
  } else {
    countError.value = '4-15个字符，仅限字母数字';
  }
};

const passInput = (event) => {
  const value = event.target.value;
  if (regex.test(value)) {
    passwordError.value = '';
  } else {
    passwordError.value = '4-15个字符，仅限字母数字';
  }
};

if (info.username) {
  handleInput({ target: { value: info.username } });
}

async function getCaptcha() {
  try {
    let res = await myAxios.get("http://localhost:8080/code/captcha")
    captchaImg.value = "data:image/png;base64," + res.data.data.imageBase64
    info.uuid = res.data.data.uuid
  } catch (e) {
    console.log(e)
  }
}

async function loginClick() {
  try {
    if(info.username === '') {
      ElMessage.error("请输入用户名")
      return;
    }
    if(info.password === '') {
      ElMessage.error("请输入密码");
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
      cookie.set('username',info.username)
      if(info.username === 'admin') {
        ElMessage.success("欢迎，管理员")
        setTimeout(()=>{
          router.push("/admin")
          cookie.set('adminId','pass')
        },1000)
      } else {
        ElMessage.success("欢迎回来")
        setTimeout(()=>{
          router.push("/home")
        },1000)
      }
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    console.log("Login failed")
  }
}

async function logonClick() {
  try {
    if(info.firstPassword != info.secondPassword) {
      ElMessage.error("两次输入的密码不一致")
      return;
    }
    if(countError.value !== '') {
      ElMessage.error("用户名格式无效")
      return;
    }
    if(passwordError.value !== '') {
      ElMessage.error("密码格式无效")
      return;
    }
    let res = await myAxios.post("http://localhost:8080/user/logon",{
      name:info.newName,
      password:info.firstPassword
    })
    if(res.data.code === 200){
      ElMessage.success("注册成功")
      active.value = 1; // Switch to login
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    console.log(e)
  }
  info.newName=''
  info.firstPassword=''
  info.secondPassword=''
}
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;900&display=swap');

/* Reset & Base */
* {
  box-sizing: border-box;
}

.swiss-login-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  font-family: 'Inter', sans-serif;
  color: #000;
  position: relative;
  overflow: hidden;
}

/* Swiss Grid Background */
.swiss-grid-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(#e0e0e0 1px, transparent 1px),
    linear-gradient(90deg, #e0e0e0 1px, transparent 1px);
  background-size: 40px 40px;
  z-index: 0;
  opacity: 0.5;
}

.particle-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1; /* Above grid, below card */
  pointer-events: none;
}

.main-card {
  position: relative;
  z-index: 10;
  width: 900px;
  height: 600px;
  background: #fff;
  display: flex;
  box-shadow: 20px 20px 0px #000000; /* Hard shadow */
  border: 3px solid #000;
  animation: cardEntry 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* Left Panel */
.brand-panel {
  flex: 1;
  background: #000;
  color: #fff;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border-right: 3px solid #000;
  position: relative;
  overflow: hidden;
}

.hero-text {
  font-size: 64px;
  font-weight: 900;
  line-height: 0.9;
  letter-spacing: -2px;
  display: flex;
  flex-direction: column;
  
  span {
    display: block;
    opacity: 0;
    transform: translateY(20px);
    animation: textSlideUp 0.6s ease forwards;
    
    &:nth-child(1) { animation-delay: 0.4s; }
    &:nth-child(2) { animation-delay: 0.6s; }
  }

  .highlight {
    -webkit-text-stroke: 1px #fff;
    color: transparent; /* Outline style */
  }
}

.brand-desc {
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 1px;
  opacity: 0.7;
  margin-top: 20px;
}

.brand-footer {
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  
  .line {
    height: 1px;
    background: #fff;
    flex: 1;
    opacity: 0.3;
  }
}

/* Right Panel */
.form-panel {
  flex: 1.2;
  padding: 60px;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.form-header {
  margin-bottom: 40px;
}

.tabs {
  display: flex;
  gap: 30px;
  
  .tab {
    font-size: 14px;
    font-weight: 600;
    text-transform: uppercase;
    cursor: pointer;
    opacity: 0.4;
    transition: all 0.3s ease;
    position: relative;
    padding-bottom: 5px;

    &.active {
      opacity: 1;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        height: 2px;
        background: #000;
      }
    }

    &:hover {
      opacity: 0.8;
    }
  }
}

.form-content-wrapper {
  flex: 1;
  position: relative;
}

.input-group {
  position: relative;
  margin-bottom: 35px;
  
  input {
    width: 100%;
    border: none;
    border-bottom: 1px solid #ddd;
    padding: 10px 0;
    font-size: 16px;
    font-family: 'Inter', sans-serif;
    color: #000;
    background: transparent;
    transition: border-color 0.3s;
    border-radius: 0; /* No rounding */

    &:focus {
      outline: none;
    }
    
    &:focus ~ label,
    &:not(:placeholder-shown) ~ label {
      top: -20px;
      font-size: 12px;
      color: #000;
      font-weight: 700;
    }

    &:focus ~ .border-animation {
      width: 100%;
    }
  }

  label {
    position: absolute;
    top: 10px;
    left: 0;
    font-size: 16px;
    color: #999;
    pointer-events: none;
    transition: all 0.3s ease;
  }

  .border-animation {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 0;
    height: 2px;
    background: #000;
    transition: width 0.3s ease;
  }

  .error-msg {
    position: absolute;
    bottom: -20px;
    left: 0;
    font-size: 11px;
    color: #000;
    font-weight: 600;
    background: #000;
    color: #fff;
    padding: 2px 4px;
  }
}

.captcha-group {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  
  .captcha-wrapper {
    flex: 1;
    position: relative;
  }
  
  .captcha-img {
    height: 40px;
    cursor: pointer;
    border: 1px solid #ddd;
    transition: filter 0.3s;
    
    &:hover {
      filter: contrast(1.2);
    }
  }
}

.cta-btn {
  width: 100%;
  padding: 18px;
  background: #000;
  color: #fff;
  border: none;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  transition: all 0.3s ease;
  border: 1px solid #000;

  &:hover {
    background: #fff;
    color: #000;
    
    .btn-icon {
      transform: translateX(5px);
    }
  }
  
  .btn-icon {
    font-size: 18px;
    transition: transform 0.3s ease;
  }
}

/* Animations */
@keyframes cardEntry {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes textSlideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Vue Transitions */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>