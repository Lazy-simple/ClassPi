import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 页面懒加载
const Login = () => import('@/views/Login/index.vue')
const Register = () => import('@/views/Login/Register.vue')
const ForgotPassword = () => import('@/views/Login/ForgotPassword.vue')
const MainPage = () => import('@/views/MainPage.vue')
const NotFound = () => import('@/views/NotFound.vue')

// 教师页面
const TeacherDashboard = () => import('@/views/Teacher/Dashboard.vue')
const TeacherCourse = () => import('@/views/Teacher/CourseList.vue')
const TeacherHomeworkPublish = () => import('@/views/Teacher/HomeworkPublish.vue')
const TeacherHomeworkCheck = () => import('@/views/Teacher/HomeworkCheck.vue')
const TeacherScore = () => import('@/views/Teacher/ScoreManage.vue')

// 学生页面
const StudentCourse = () => import('@/views/Student/CourseList.vue')
const StudentHomeworkSubmit = () => import('@/views/Student/HomeworkSubmit.vue')

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/forgot-password', component: ForgotPassword },
  {
    path: '/main',
    component: MainPage,
    redirect: '/main/dashboard',
    children: [
      // 教师子路由
      { path: 'dashboard', component: TeacherDashboard },
      { path: 'teacher-course', component: TeacherCourse },
      { path: 'publish-homework', component: TeacherHomeworkPublish },
      { path: 'check-homework', component: TeacherHomeworkCheck },
      { path: 'score', component: TeacherScore },
      // 学生子路由
      { path: 'student-course', component: StudentCourse },
      { path: 'submit-homework', component: StudentHomeworkSubmit }
    ]
  },
  { path: '/:pathMatch(.*)*', component: NotFound }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
// 修复并完善后的路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  // 获取用户身份，确保转为小写以防万一，例如 'Student' -> 'student'
  const identity = userStore.userInfo?.identity?.toLowerCase()
  console.log('🚀 路由守卫检查 -> Token:', !!token, '| 身份:', identity, '| 目标路径:', to.path)
  // 1. 白名单：登录、注册等页面不需要权限，直接放行
  const whiteList = ['/login', '/register', '/forgot-password']
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 2. 检查是否已登录
  if (!token) {
    next('/login')
    return
  }

  // 3. 权限校验（基于字符串 identity）
  // 规则：访问 /main 下的所有页面都需要特定身份

  // 情况 A：如果是学生身份 ('student')
  if (identity === 'student') {
    // 如果学生试图访问非学生页面（比如 dashboard, teacher-course 等），强制跳回学生课程页
    if (to.path !== '/main/student-course' && to.path !== '/main/submit-homework') {
      // ElMessage.warning('您是学生账号，已自动跳转至学生页面')
      next('/main/student-course')
      return
    }
  }

  // 情况 B：如果是老师身份 ('teacher')
  else if (identity === 'teacher') {
    // 如果老师试图访问学生专属页面，强制跳回老师仪表盘
    if (to.path === '/main/student-course' || to.path === '/main/submit-homework') {
      // ElMessage.warning('您是教师账号，已自动跳转至教师页面')
      next('/main/dashboard')
      return
    }
  }

  // 4. 其他情况（身份未知或匹配成功），正常放行
  next()
})

export default router