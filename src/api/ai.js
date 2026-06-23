import request from '@/utils/request'

// AI作业评价
export function aiEvaluate(data) {
  return request({
    url: '/ai/evaluate',
    method: 'post',
    data
  })
}