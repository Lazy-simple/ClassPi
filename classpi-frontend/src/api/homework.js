import request from '@/utils/request'

// 发布作业
export function publishHomework(data) {
  return request({
    url: '/api/homework/publish',
    method: 'post',
    data
  })
}

// 学生提交作业
export function submitHomework(data) {
  console.log('========== 提交作业 ==========')
  console.log('请求路径: /api/student/homework/submit')
  console.log('提交数据:', data)

  return request({
    url: '/api/student/homework/submit',
    method: 'post',
    data
  })
}

// 获取作业列表（学生查看可提交的作业）
export function getHomeworkList(params) {
  return request({
    url: '/api/homework/list',
    method: 'get',
    params
  })
}

// 获取单个作业详情
export function getAssignmentDetail(id) {
  return request({
    url: `/api/homework/detail/${id}`,
    method: 'get'
  })
}

// 获取学生已提交的作业列表
export function getStudentHomework(studentId) {
  return request({
    url: `/api/student/homework/status/${studentId}`,
    method: 'get'
  })
}

// 教师批阅作业
export function checkHomework(data) {
  return request({
    url: '/api/homework/correct',
    method: 'post',
    data
  })
}

// 获取某次作业的所有提交记录（教师用）
export function getHomeworkSubmissions(homeworkId) {
  return request({
    url: `/api/homework/submit/list/${homeworkId}`,
    method: 'get'
  })
}

// 成绩列表
export function getScoreList(params) {
  return request({
    url: '/api/score/list',
    method: 'get',
    params
  })
}