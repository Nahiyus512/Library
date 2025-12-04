import axios from "axios";
import {getToken} from "../utils/token.js";
import router from '@/router';


const myAxios = axios.create()



//请求拦截器
myAxios.interceptors.request.use(function (config){
    //在发送请求之前做些什么
    //登录、请求验证码才设置token
    let currentUrl = config.url
    if(currentUrl != 'http://localhost:8080/code/captcha' && currentUrl != 'http://localhost:8080/user/login' && currentUrl != 'http://localhost:8080/user/logon') {
        console.log("设置token")
        let token = getToken();
        config.headers.token = token
    }
   // console.log("请求拦截器:",config)
    return config;
},function (error){
    //对请求错误做些什么
    return Promise.reject(error)
});

//响应拦截器
myAxios.interceptors.response.use(function (response){
    //对响应数据做些什么
    //console.log("响应拦截器:",response)
    if(response.data.code === 200) {
        if(response.data.data.code === '401') {
            console.log("跳转...")
            router.push("login")
        }
    }
    return response;
},function (error) {
    //对响应错误做些什么
    return Promise.reject(error)
})

export default myAxios