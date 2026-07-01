import request from '@/utils/request'
import { ref } from 'vue'
// 获取全部课程（可选课程池）
export function getCourseList() {
  return request({ url: '/course/list', method: 'get' })
}

// 获取教师课程列表
export function getTeacherCourses(teacherId, includeArchived = false) {
  return request({
    url: `/course/teacher/${teacherId}`,
    method: 'get',
    params: { includeArchived }
  })
}

// 【核心修复】获取学生已选课程
// 后端定义：@GetMapping("/student/{studentId}")
// 必须使用模板字符串将 ID 拼接到 URL 路径中，而不是作为 params 传递
export function getStudentCourses(studentId) {
  return request({
    url: `/course/student/${studentId}`,
    method: 'get'
  })
}

// 创建课程
export function createCourse(data) {
  return request({ url: '/course/create', method: 'post', data }) // 注意：Controller里是 /create
}

// 修改课程
export function updateCourse(id, data) {
  return request({ url: `/course/${id}`, method: 'put', data }) // 注意：Controller里是 /{id}
}

// 删除课程
export function deleteCourse(id) {
  return request({ url: `/course/${id}`, method: 'delete' })
}

// 【核心修复】学生选课
// 后端定义：@PostMapping("/select") 且使用 @RequestParam
// 这里建议使用 params 传参，或者在 body 中传 JSON (取决于后端具体实现，通常 @RequestParam 对应 params)
export function selectCourse(data) {
  return request({
    url: '/course/select',
    method: 'post',
    params: data // 将 studentId, courseId 等作为查询参数发送
  })
}

// 根据课程id获取全部选课学生
export function getCourseAllStudent(courseId){
  return request({
    url:`/course/${courseId}/allStudent`,
    method:'get'
  })
}

const courseStudentList = ref([])
// 点击课程，加载所有选课人
const showAllMember = async (course) => {
  const res = await getCourseAll(course.id)
  if(res.code === 200){
    courseStudentList.value = res.data
    // 弹出弹窗展示列表
  }
}

// 【核心修复】学生退课
// 后端定义：@PostMapping("/drop")
export function dropCourse(data) {
  return request({
    url: '/course/drop',
    method: 'post',
    params: data
  })
}
// 【新增】根据课程ID获取课程详情
export function getCourseById(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

// 【新增】根据课程号获取课程详情（用于加入课程）
export function getCourseByNo(courseNo) {
  return request({
    url: `/course/no/${courseNo}`,
    method: 'get'
  })
}

// 归档/取消归档课程
export function archiveCourse(courseId, archived) {
  return request({
    url: `/course/${courseId}/archive`,
    method: 'put',
    params: { archived }
  })
}

// 【新增】获取某课程的讨论列表
export function getTopicList(courseId) {
  return request({
    url: `/topic/list/${courseId}`, // 假设后端路径，请根据实际调整
    method: 'get'
  })
}

// 【新增】发布新话题
export function createTopic(data) {
  return request({
    url: '/topic/create',
    method: 'post',
    data // data 应包含 { courseId, title, content }
  })
}

// 教师加入课程
export function teacherJoinCourse(teacherId, teacherName, courseNo) {
  return request({
    url: '/course/teacher/join',
    method: 'post',
    params: { teacherId, teacherName, courseNo }
  })
}