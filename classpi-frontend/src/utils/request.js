import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器携带token
service.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = userStore.token
  }
  return config
}, err => Promise.reject(err))

// 响应拦截器统一处理返回格式
service.interceptors.response.use(res => {
  const data = res.data
  if (data.code !== 200) {
    ElMessage.error(data.msg || '请求失败')
    return Promise.reject(data)
  }
  return data
}, err => {
  ElMessage.error('网络连接失败')
  return Promise.reject(err)
})

export default service