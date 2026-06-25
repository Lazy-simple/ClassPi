import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 初始化时统一读取本地存储，同时处理 userInfo
    token: localStorage.getItem('token') || sessionStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.userInfo?.role || null
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

      localStorage.setItem('token', this.token);
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
    },

    /**
     * 单独更新用户信息（例如登录后获取详细信息）
     * @param {Object} info
     */
    setUserInfo(info) {
      this.userInfo = info;
      localStorage.setItem('userInfo', JSON.stringify(info));
    },

    /**
     * 退出登录，清除所有状态和本地存储
     */
    logout() {
      this.token = '';
      this.userInfo = null;

      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('userInfo');
    },

    /**
     * 初始化检查：读取本地存储恢复用户信息，防止刷新丢失
     */
    initFromStorage() {
      // 优先读 localStorage，兼容 sessionStorage（可选）
      const storedToken = localStorage.getItem('token') || sessionStorage.getItem('token');
      const storedInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo');

      if (storedToken) {
        this.token = storedToken;
      }
      if (storedInfo) {
        try {
          this.userInfo = JSON.parse(storedInfo);
        } catch (e) {
          console.error('解析用户信息失败', e);
          this.logout(); // 数据损坏则强制登出
        }
      }
    }
  }
})

