import axios from 'axios';

// 修改为正确的后端地址
const API_URL = 'http://localhost:8080/user';  // 改成 /user 而不是 /auth

class AuthService {
    login(username, password) {
        return axios.post(API_URL + '/login', {
            username,
            password
        })
            .then(response => {
                // 检查后端返回的数据结构
                console.log('登录响应:', response.data);

                // 后端返回格式: { code: 200, msg: "登录成功", data: { id, username, name, ... } }
                if (response.data.code === 200) {
                    // 生成一个模拟 token（后端暂时没有实现 JWT）
                    const token = 'user-token-' + Date.now();
                    const userData = response.data.data;

                    // 存储用户信息
                    localStorage.setItem('user', JSON.stringify({
                        token: token,
                        user: userData
                    }));

                    return {
                        token: token,
                        user: userData
                    };
                } else {
                    throw new Error(response.data.msg || '登录失败');
                }
            });
    }

    logout() {
        localStorage.removeItem('user');
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
    }
}

export default new AuthService();