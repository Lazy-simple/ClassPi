import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api', // 配合 vite.config.js 的 proxy 使用，不要写死 localhost:8080
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.token) {
      config.headers['Authorization'] = 'Bearer ' + user.token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果后端返回 code 不为 200，视为业务错误
    if (res.code !== 200) {
      ElMessage.error(res.msg || '系统错误')
      // 401 token 过期处理
      if (res.code === 401) {
        localStorage.removeItem('user')
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error)
    ElMessage.error(error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export default service