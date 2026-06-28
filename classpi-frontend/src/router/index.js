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
    component: () => import('@/views/Login/Register.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/Login/ForgotPassword.vue')
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
      { path: 'course-resource', component: () => import('@/views/Teacher/CourseResource.vue') },
      { path: 'course-topic', component: () => import('@/views/common/CourseTopic.vue') },
      // 学生路由
      { path: 'student-course', component: () => import('@/views/Student/CourseList.vue') },
      { path: 'submit-homework', component: () => import('@/views/Student/HomeworkSubmit.vue') },
      { path: 'student-resource', component: () => import('@/views/Student/StudentResource.vue') },
      { path: 'student-topic', component: () => import('@/views/common/CourseTopic.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', component: () => import('@/views/NotFound.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 修复后的完整路由守卫（合并重复逻辑、补全缺失的变量/逻辑）
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const userStore = useUserStore();
  
  // 获取身份：优先从store获取，其次从localStorage解析
  let identity = userStore.userInfo?.identity;
  if (!identity) {
    const storedInfo = localStorage.getItem('userInfo');
    if (storedInfo) {
      try {
        const userInfo = JSON.parse(storedInfo);
        identity = userInfo.identity;
      } catch (e) {
        console.error('解析用户信息失败', e);
      }
    }
  }
  
  console.log('=== 路由守卫 ===');
  console.log('token:', token ? '存在' : '不存在');
  console.log('identity:', identity);
  console.log('目标路径:', to.path);

  // 1. 无token跳登录
  // 1. 无token跳登录
  if (!token) {
    // ✅ 白名单：不需要登录就能访问的页面
    const whiteList = ['/login', '/register', '/forgot-password']
    if (whiteList.includes(to.path)) {
      next();
    } else {
      ElMessage.warning('请先登录');
      next("/login");
    }
    return;
  }

  // 2. 已登录禁止进登录页
  if (to.path === '/login') {
    if (identity === 'teacher') {
      next('/main/dashboard');
    } else if (identity === 'student') {
      next('/main/student-course');
    } else {
      ElMessage.error('身份验证失败，请重新登录');
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      next('/login');
    }
    return;
  }

  // 3. 身份为空直接重登
  if (!identity) {
    ElMessage.error('身份信息缺失，请重新登录');
    return next('/login');
  }

  // 页面权限分组不变
  const teacherPages = [
    '/main/dashboard',
    '/main/teacher-course',
    '/main/publish-homework',
    '/main/check-homework',
    '/main/score',
    '/main/course-resource'
  ];
  const studentPages = [
    '/main/student-course',
    '/main/submit-homework',
    '/main/student-resource',
    '/main/student-topic'
  ];

  // 教师不能进学生页面
  if (identity === 'teacher' && studentPages.includes(to.path)) {
    ElMessage.error('教师无权限访问学生页面');
    return next('/main/dashboard');
  }
  // 学生不能进教师页面
  if (identity === 'student' && teacherPages.includes(to.path)) {
    ElMessage.error('学生无权限访问教师后台');
    return next('/main/student-course');
  }

  next();
});

export default router