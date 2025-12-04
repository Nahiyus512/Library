import { createApp } from 'vue'
import App from './App.vue'
import router from "./router";
import Element from 'element-plus'
import 'element-plus/dist/index.css'
import zhLocale from 'element-plus/es/locale/lang/zh-cn'

const app = createApp(App)
//app.use(ElementPlus)
app.use(Element,{locale: zhLocale})
app.use(router)
app.mount('#app')
