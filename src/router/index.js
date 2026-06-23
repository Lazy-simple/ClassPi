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
/*
// 修复后的路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const role = userStore.userInfo?.role

  if (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password') {
    next()
    return
  }
  if (!token) {
    next('/login')
    return
  }
  next()
})
*/
export default router