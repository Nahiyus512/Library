import axios from 'axios'
import { getToken } from '../utils/token'

const myAxios = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

myAxios.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

myAxios.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default myAxios
