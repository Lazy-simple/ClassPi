import request from '@/utils/request'

// AI作业评价
export function aiEvaluate(data) {
  return request({
    url: '/api/ai/evaluate',
    method: 'post',
    data
  })
}

// 新增：获取作业列表
export function getHomeworkList(params) {
  return request({
    url: '/homework/list',
    method: 'get',
    params
  })
}