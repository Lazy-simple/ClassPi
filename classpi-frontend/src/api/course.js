import request from '@/utils/request'

export function getCourseList() {
  return request({ url: '/course/list', method: 'get' })
}

export function getTeacherCourses(teacherId, includeArchived = false) {
  return request({
    url: `/course/teacher/${teacherId}`,
    method: 'get',
    params: { includeArchived }
  })
}

export function getTeacherCourseList(teacherId) {
  return request({
    url: `/course/teacher/${teacherId}/list`,
    method: 'get'
  })
}

export function getTeacherArchivedCourses(teacherId) {
  return request({
    url: `/course/teacher/${teacherId}/archived`,
    method: 'get'
  })
}

export function getStudentCourses(studentId) {
  return request({
    url: `/course/student/${studentId}`,
    method: 'get'
  })
}

export function createCourse(data) {
  return request({ url: '/course/create', method: 'post', data })
}

export function updateCourse(id, data) {
  return request({ url: `/course/${id}`, method: 'put', data })
}

export function deleteCourse(id, teacherId) {
  return request({
    url: `/course/${id}`,
    method: 'delete',
    params: { teacherId }
  })
}

export function selectCourse(data) {
  return request({
    url: '/course/select',
    method: 'post',
    params: data
  })
}

export function getCourseAllStudent(courseId) {
  return request({
    url: `/course/${courseId}/allStudent`,
    method: 'get'
  })
}

export function dropCourse(data) {
  return request({
    url: '/course/drop',
    method: 'post',
    params: data
  })
}

export function archiveCourse(courseId, archived) {
  return request({
    url: `/course/${courseId}/archive`,
    method: 'put',
    params: { archived }
  })
}

export function joinCourse(teacherId, teacherName, courseNo) {
  return request({
    url: '/course/join',
    method: 'post',
    params: { teacherId, teacherName, courseNo }
  })
}

export function archiveSelf(courseId, teacherId, archived) {
  return request({
    url: `/course/${courseId}/archive-self`,
    method: 'put',
    params: { teacherId, archived }
  })
}

export function archiveAll(courseId, teacherId, archived) {
  return request({
    url: `/course/${courseId}/archive-all`,
    method: 'put',
    params: { teacherId, archived }
  })
}

export function updateCourseSort(teacherId, courseIds) {
  return request({
    url: `/course/teacher/${teacherId}/sort`,
    method: 'put',
    data: courseIds
  })
}

export function restoreCourse(courseId, teacherId) {
  return request({
    url: `/course/${courseId}/restore`,
    method: 'put',
    params: { teacherId }
  })
}

export function deleteArchivedCourse(courseId, teacherId) {
  return request({
    url: `/course/${courseId}/archived`,
    method: 'delete',
    params: { teacherId }
  })
}
