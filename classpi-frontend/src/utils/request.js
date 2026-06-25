import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const service = axios.create({
  baseURL:'http://localhost:8080',
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
// request.js 响应拦截器
// request.js 响应拦截器 - 更安全的版本
service.interceptors.response.use(
    (res) => {
        const data = res.data;

        // 🔍 调试日志
        console.log('响应数据:', data);

        // 情况1：如果后端返回的是 HTTP 200，但 data 本身就是数据
        // 比如直接返回 { token, userInfo } 而没有 code 字段
        if (data.token && data.userInfo) {
            // 这是登录成功的响应，直接返回
            return { code: 200, data: data };
        }

        // 情况2：标准格式 { code: 200, data: {...}, msg: 'success' }
        // 使用宽松比较，支持数字和字符串
        const code = data.code;
        if (code == 200 || code == 0 || code === '200' || code === '0') {
            return data;  // 成功
        }

        // 情况3：如果 code 存在但不是成功状态码
        if (code !== undefined && code !== null) {
            return Promise.reject(data);  // 失败
        }

        // 情况4：没有 code 字段，但请求成功了，默认返回数据
        return data;
    },
    (err) => {
        console.error('请求失败:', err);
        ElMessage.error("网络连接失败");
        return Promise.reject(err);
    }
);

export default service