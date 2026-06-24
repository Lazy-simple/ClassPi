import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => {
      // 初始化时尝试从本地存储获取
      const token = localStorage.getItem('token') || sessionStorage.getItem('token') || '';

      // ⚠️ 【核心修复】：从本地存储读取 userInfo，并安全解析
      let userInfo = null;
      const storedInfo = localStorage.getItem('userInfo');
      if (storedInfo) {
        try {
          userInfo = JSON.parse(storedInfo);
        } catch (e) {
          console.error('解析本地用户信息失败', e);
        }
      }

      return {
        token,
        userInfo
      };
    },

  actions: {
    /**
     * 设置用户信息并持久化存储
     * @param {Object} data - 包含 token 和 userInfo 的对象
     */
    setUser(data) {
      if (!data) return;

      this.token = data.token;
      this.userInfo = data.userInfo;

      // 将数据存入 localStorage
      localStorage.setItem('token', this.token);
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
    },

    /**
     * 退出登录，清除所有状态和本地存储
     */
    logout() {
      this.token = '';
      this.userInfo = null;

      // 清除所有相关的本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('userInfo');
    },

    /**
     * 初始化检查：在 App.vue 中调用，防止刷新页面后 userInfo 丢失
     */
    initFromStorage() {
      const storedToken = localStorage.getItem('token');
      const storedInfo = localStorage.getItem('userInfo');

      if (storedToken) {
        this.token = storedToken;
      }
      if (storedInfo) {
        try {
          this.userInfo = JSON.parse(storedInfo);
        } catch (e) {
          console.error('解析用户信息失败', e);
          this.logout(); // 如果解析失败，说明数据损坏，强制登出
        }
      }
    }
  }
})