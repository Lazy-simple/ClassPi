import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 发送重置验证码（验证账号是否存在）
export function sendResetCode(data) {
  return request({
    url: '/user/sendResetCode',
    method: 'post',
    data
  })
}

// ✅ 重置密码
export function resetPassword(data) {
  return request({
    url: '/user/resetPassword',
    method: 'post',
    data
  })
}