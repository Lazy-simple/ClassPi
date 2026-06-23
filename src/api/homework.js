import request from '@/utils/request'

// 发布作业
export function publishHomework(data) {
  return request({ url: '/homework/publish', method: 'post', data })
}

// 学生提交作业
export function submitHomework(data) {
  return request({ url: '/homework/submit', method: 'post', data })
}

// 获取作业列表
export function getHomeworkList(params) {
  return request({ url: '/homework/list', method: 'get', params })
}

// 教师批阅作业录入成绩
export function checkHomework(data) {
  return request({ url: '/homework/check', method: 'post', data })
}

// 成绩列表
export function getScoreList(params) {
  return request({ url: '/score/list', method: 'get', params })
}