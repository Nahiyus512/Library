import { useCookies } from '@vueuse/integrations/useCookies'

const key = "token"
const cookie = useCookies()

//获取token
export function getToken() {
    return cookie.get(key)
}

//设置token
export function setToken(token) {
    return cookie.set(key,token)
}

//清除token
export function removeToken() {
    return cookie.remove(key)
}