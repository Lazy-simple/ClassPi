import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/store/userStore'; // 导入 Pinia 存储
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import ForgotPassword from '@/components/ForgotPassword.vue';
import MainPage from '@/components/MainPage.vue';
import path from 'path';
import authService from '@/services/authService';


const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword
  },
  {
    path: '/main-page',
    name: 'MainPage',
    component:MainPage,
    meta: { requiresAuth: true } // 标记该路由需要身份验证
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const currentUser = authService.getCurrentUser();

  if (requiresAuth && !currentUser) {
    next('/login');
  } else if (to.path === '/login' && currentUser) {
    next('/main-page');
  } else {
    next();
  }
});

export default router;