import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000
});

// 无需认证的接口列表
const publicPaths = ['/api/auth/register', '/api/auth/login'];

// 添加请求拦截器
instance.interceptors.request.use(
    config => {
        // 检查请求路径是否需要认证
        const isPublicPath = publicPaths.some(path => config.url.includes(path));
        if (isPublicPath) {
            // 公开接口不添加令牌
            return config;
        }

        const user = JSON.parse(localStorage.getItem('user'));
        if (user && user.token) {
            config.headers['Authorization'] = 'Bearer ' + user.token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 添加响应拦截器
instance.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 未授权，跳转到登录页
                    localStorage.removeItem('user');
                    window.location.href = '/login';
                    break;
                case 403:
                    // 权限不足，显示提示
                    alert('您没有权限访问该资源');
                    break;
            }
        }
    }
);

export default instance;