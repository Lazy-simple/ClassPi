import request from '@/utils/request'

// 获取全部课程
export function getCourseList() {
  return request({ url: '/course/list', method: 'get' })
}

// 获取教师课程
export function getTeacherCourses(teacherId) {
  return request({ url: '/course/teacher', method: 'get', params: { teacherId } })
}

// 获取学生已选课程
export function getStudentCourses(studentId) {
  return request({ url: '/course/student', method: 'get', params: { studentId } })
}

// 创建课程
export function createCourse(data) {
  return request({ url: '/course/add', method: 'post', data })
}

// 修改课程
export function updateCourse(id, data) {
  return request({ url: `/course/update/${id}`, method: 'put', data })
}

// 删除课程
export function deleteCourse(id) {
  return request({ url: `/course/delete/${id}`, method: 'delete' })
}

// 学生选课
export function selectCourse(data) {
  return request({ url: '/student/course/add', method: 'post', data })
}

// 学生退课
export function dropCourse(data) {
  return request({ url: '/student/course/del', method: 'post', data })
}