import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '', // 从本地存储读取 Token
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null // 用户信息
  }),
  actions: {
    // 存储 Token 和用户信息
    setUser(token, userInfo, remember) {
      this.token = token;
      this.userInfo = userInfo;
      // 根据 "记住密码" 选择存储方式
      if (remember) {
        localStorage.setItem('token', token);
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
      } else {
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
      }
    },
    // 清除登录状态（退出登录）
    logout() {
      this.token = '';
      this.userInfo = null;
      localStorage.removeItem('token');
      sessionStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      sessionStorage.removeItem('userInfo');
    }
  }
});