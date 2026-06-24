import request from '@/utils/request'

// 活跃度热力图数据
export function getHeatData(courseId) {
  return request({
    url: '/statistics/heat',
    method: 'get',
    params: { courseId }
  })
}

// 课程统计总数据
export function getCourseStat(courseId) {
  return request({
    url: '/statistics/course',
    method: 'get',
    params: { courseId }
  })
}