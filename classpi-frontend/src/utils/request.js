import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const service = axios.create({
  // 【关键修改】直接指向后端地址
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 注意：这里最好用 Pinia 的 storeToRefs 或者确保 store 已初始化
    // 如果这里报错 "getActivePinia was called with no active Pinia"，请看下方的【重要提示】
    try {
      const userStore = useUserStore()
      if (userStore.token) {
        config.headers.Authorization = userStore.token
      }
    } catch (e) {
      console.warn('Store not ready in interceptor')
    }
    return config
  },
  (err) => Promise.reject(err)
)

// 响应拦截器
service.interceptors.response.use(
  (res) => {
    const data = res.data
    // 假设后端返回格式是 { code: 200, data: ..., msg: ... }
    if (data.code === 200) {
      return data
    } else {
      ElMessage.error(data.msg || '系统繁忙')
      return Promise.reject(new Error(data.msg || 'Error'))
    }
  },
  (err) => {
    // 处理 HTTP 错误状态码 (404, 500 等)
    if (err.response && err.response.status) {
      ElMessage.error(`请求失败: ${err.response.status}`)
    } else {
      ElMessage.error('网络连接异常')
    }
    return Promise.reject(err)
  }
)

export default service