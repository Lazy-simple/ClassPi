// src/services/registerService.js
import axios from 'axios';

// 后端接口地址（根据你的实际地址调整）
const baseUrl = 'http://localhost:8080';

export default {
    async register(account, password, identity, name, school, studentId) {
        const registerDTO = {
            username: account, // 账号对应username
            password: password,
            identity: identity, // 老师/学生
            name: name,
            school: school,
            studentId: studentId || '', // 学生学号，非必填
            phone: account, // 如果账号是手机号，可赋值给phone（根据你的逻辑调整）
        };
        const response = await axios.post(`${baseUrl}/api/user/register`, registerDTO);
        return response.data;
    }
};