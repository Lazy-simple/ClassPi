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
  const token = userStore.token // 假设 token 也保存在 store 里
  const role = userStore.userInfo?.role

  // 1. 白名单：登录、注册等页面不需要权限，直接放行
  const whiteList = ['/login', '/register', '/forgot-password']
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 2. 检查是否已登录 (是否有 token)
  if (!token) {
    next('/login') // 没登录就跳去登录页
    return
  }

  // 3. 权限校验
  // 如果访问的是老师页面，但角色不是老师
  if (to.path.startsWith('/main') && to.path !== '/main/student-course' && role !== 1) {
     ElMessage.error('您没有权限访问该页面')
     next('/main/student-course') // 踢回学生页面
     return
  }

  // 如果访问的是学生页面，但角色不是学生
  if (to.path === '/main/student-course' && role !== 2) {
     ElMessage.error('您没有权限访问该页面')
     next('/main/dashboard') // 踢回老师页面
     return
  }

  // 4. 其他情况，正常放行
  next()
})

export default router