import request from '@/utils/request'

// 获取教师的备课列表（用于备课区页面）
export function getTeacherPreparations(teacherId, page, pageSize) {
  return request({
    url: '/api/preparation/teacher/list',
    method: 'get',
    params: { teacherId, page, pageSize }
  })
}

// 根据类型获取备课列表
export function getTeacherPreparationsByType(teacherId, type, page, pageSize) {
  return request({
    url: '/api/preparation/teacher/list/type',
    method: 'get',
    params: { teacherId, type, page, pageSize }
  })
}

// 新增备课内容
export function addPreparation(data) {
  return request({
    url: '/api/preparation',
    method: 'post',
    data
  })
}

// 更新备课内容
export function updatePreparation(id, data) {
  return request({
    url: `/api/preparation/${id}`,
    method: 'put',
    data
  })
}

// 删除备课内容
export function deletePreparation(id, teacherId, identity) {
  return request({
    url: `/api/preparation/${id}`,
    method: 'delete',
    params: { teacherId, identity }
  })
}

// 分配备课内容到课程
export function assignToCourse(id, courseId, courseNo, teacherId, identity) {
  return request({
    url: '/api/preparation/assign',
    method: 'post',
    data: { id, courseId, courseNo, teacherId, identity }
  })
}

// 【核心修复】获取未分配的备课内容（用于发布作业时导入）
// 这个函数就是 HomeworkPublish.vue 报错时寻找的
export function getUnassignedPreparations(teacherId, page, pageSize) {
  return request({
    url: '/api/preparation/unassigned',
    method: 'get',
    params: { teacherId, page, pageSize }
  })
}