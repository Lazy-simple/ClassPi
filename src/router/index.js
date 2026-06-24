import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue')
  },
  {
    path: '/main',
    component: () => import('@/views/MainPage.vue'),
    redirect: '/main/dashboard',
    children: [
      // 教师路由
      { path: 'dashboard', component: () => import('@/views/Teacher/Dashboard.vue') },
      { path: 'teacher-course', component: () => import('@/views/Teacher/CourseList.vue') },
      { path: 'publish-homework', component: () => import('@/views/Teacher/HomeworkPublish.vue') },
      { path: 'check-homework', component: () => import('@/views/Teacher/HomeworkCheck.vue') },
      { path: 'score', component: () => import('@/views/Teacher/ScoreManage.vue') },
      { path: 'course-resource', component: () => import('@/views/teacher/CourseResource.vue') },
      { path: 'course-topic', component: () => import('@/views/common/CourseTopic.vue') },
      // 学生路由
      { path: 'student-course', component: () => import('@/views/Student/CourseList.vue') },
      { path: 'submit-homework', component: () => import('@/views/Student/HomeworkSubmit.vue') },
      { path: 'student-resource', component: () => import('@/views/student/StudentResource.vue') },
      { path: 'student-topic', component: () => import('@/views/common/CourseTopic.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', component: () => import('@/views/NotFound.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 注释路由守卫，跳过登录校验，直接访问页面
// router.beforeEach((to, from, next) => {
//   const userStore = useUserStore()
//   const token = userStore.token
//   const role = userStore.userInfo?.role
//   const whiteList = ['/login', '/register', '/forgot-password']
//   if (whiteList.includes(to.path)) {
//     next()
//     return
//   }
//   if (!token) {
//     next('/login')
//     return
//   }
//   // 教师页面权限拦截
//   if (to.path.startsWith('/main')
//     && to.path !== '/main/student-course'
//     && to.path !== '/main/student-resource'
//     && to.path !== '/main/student-topic'
//     && role !== 1) {
//     ElMessage.error('您没有权限访问该页面')
//     next('/main/student-course')
//     return
//   }
//   // 学生页面权限拦截
//   if ((to.path === '/main/student-course' || to.path === '/main/student-resource' || to.path === '/main/student-topic') && role !== 2) {
//     ElMessage.error('您没有权限访问该页面')
//     next('/main/dashboard')
//     return
//   }
//   next()
// })

export default router