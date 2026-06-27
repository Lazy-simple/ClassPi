import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(config => {
    console.log('========== 请求拦截器 ==========')
    console.log('URL:', config.url)
    console.log('Method:', config.method)
    console.log('Data:', config.data)

    const token = localStorage.getItem('token')
    const userInfoStr = localStorage.getItem('userInfo')

    console.log('token:', token ? '存在' : '不存在')
    console.log('userInfoStr:', userInfoStr)

    // 从 userInfo 中解析 userId
    let userId = null
    if (userInfoStr) {
        try {
            const userInfo = JSON.parse(userInfoStr)
            userId = userInfo.id || userInfo.userId
            console.log('从 userInfo 解析 userId:', userId)
        } catch (e) {
            console.error('解析 userInfo 失败:', e)
        }
    }

    // 如果 localStorage 中没有 userId，尝试从 localStorage 直接获取
    if (!userId) {
        userId = localStorage.getItem('userId')
        console.log('从 localStorage 直接获取 userId:', userId)
    }

    // 如果还是获取不到，使用默认值 1（临时测试）
    if (!userId) {
        console.warn('⚠️ userId 获取失败，使用默认值 1')
        userId = '1'
    }

    if (token) {
        config.headers.Authorization = token
    }
    if (userId) {
        config.headers.userId = String(userId)  // 确保是字符串
        console.log('设置 userId header:', userId)
    }

    console.log('最终 Headers:', config.headers)
    return config
}, err => Promise.reject(err))

// 响应拦截器
service.interceptors.response.use(
    (response) => {
        console.log('========== 响应成功 ==========')
        console.log('状态码:', response.status)
        console.log('数据:', response.data)
        return response.data
    },
    (error) => {
        console.error('========== 请求失败 ==========')
        console.error('错误状态:', error.response?.status)
        console.error('错误数据:', error.response?.data)
        console.error('错误信息:', error.message)

        ElMessage.error('请求失败，请稍后重试')
        return Promise.reject(error)
    }
)

export default service