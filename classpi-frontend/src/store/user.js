import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 初始化时尝试从本地存储获取，如果失败则默认为空
    token: localStorage.getItem('token') || sessionStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    // 增加一个判断是否已登录的快捷属性
    isLoggedIn: (state) => !!state.token,
    // 增加一个获取角色的快捷属性
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

      // 将数据存入 localStorage (长期保存)
      // 如果你想区分“记住我”，可以在这里加判断，目前默认都存 localStorage
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

      // 清除所有相关的本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('userInfo');
    },

    /**
     * 初始化检查（可选）：在 App.vue 中调用，防止刷新页面后 userInfo 丢失
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