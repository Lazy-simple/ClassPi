import request from '@/utils/request'

// 登录接口
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 注册接口
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 忘记密码 (新增)
export function forgotPassword(data) {
  return request({
    url: '/user/forgot-password', // 假设后端是这个路径，根据实际调整
    method: 'post',
    data
  })
}