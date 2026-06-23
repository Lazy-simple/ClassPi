import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  { path: '/forgot-password', name: 'ForgotPassword', component: () => import('@/views/ForgotPassword.vue') },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }, // 标记需要登录
    children: [
      { path: '', redirect: '/home/dashboard' },
      { path: 'dashboard', component: () => import('@/views/dashboard/Index.vue') },
      { path: 'course', component: () => import('@/views/course/Index.vue') },
      { path: 'homework', component: () => import('@/views/homework/Index.vue') },
      { path: 'user', component: () => import('@/views/user/Index.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (to.meta.requiresAuth && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router