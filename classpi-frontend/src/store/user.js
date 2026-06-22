import { defineStore } from 'pinia'
import { login, register, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  
  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      if (res.code === 200) {
        this.token = res.data.token || 'mock-token'
        localStorage.setItem('token', this.token)
        return true
      }
      return false
    },
    
    async register(registerForm) {
      const res = await register(registerForm)
      return res.code === 200
    },
    
    async getUserInfo() {
      const res = await getUserInfo()
      if (res.code === 200) {
        this.userInfo = res.data
      }
    },
    
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})