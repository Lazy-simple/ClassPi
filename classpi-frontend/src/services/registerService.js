import axios from './axios';

const API_URL = 'http://localhost:8080/api/auth';

class RegisterService {
    register(account, password, identity, name, school, studentId) {
    return axios.post('/api/auth/register', {
        account: account,  // 确保与后端User类字段名一致
        password: password, // 明文发送，后端加密
        character: identity, // 对应后端的character字段
        name: name,
        school: school,
        studentId: identity === 'student' ? studentId : null
    }).then(response => {
        return response.data; // 返回成功响应
    }).catch(error => {
        // 明确显示后端返回的错误信息
        throw new Error(error.response?.data?.message || "注册失败，请重试");
    }); 
    }
}

export default new RegisterService();