import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 优先从 localStorage 取，如果没有再从 sessionStorage 取
    // 这样无论用户选没选“记住我”，刷新页面都能保持登录状态
    token: localStorage.getItem('token') || sessionStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')) || null
  }),

  getters: {
    // 提供一个便捷的方法判断是否已登录
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    /**
     * 设置用户信息
     * @param {String} token - 令牌
     * @param {Object} userInfo - 用户对象
     * @param {Boolean} remember - 是否记住密码/自动登录
     */
    setUser(token, userInfo, remember = false) {
      this.token = token
      this.userInfo = userInfo

      if (remember) {
        // 存入持久化存储（关闭浏览器还在）
        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        // 清除 session 中的旧数据，避免冲突
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('userInfo')
      } else {
        // 存入会话存储（关闭浏览器即失效）
        sessionStorage.setItem('token', token)
        sessionStorage.setItem('userInfo', JSON.stringify(userInfo))
        // 清除 local 中的旧数据
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    },

    /**
     * 退出登录
     */
    logout() {
      this.token = ''
      this.userInfo = null

      // 同时清除两种存储，确保彻底退出
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('userInfo')
    }
  }
})