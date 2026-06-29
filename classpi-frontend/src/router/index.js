import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

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
      {
              path: 'teacher-course-detail/:courseId',
              component: () => import('@/views/Teacher/CourseDetail.vue')
            },
      { path: 'preparation', component: () => import('@/views/Teacher/Preparation.vue') },
      // 学生路由
      { path: 'student-course', component: () => import('@/views/Student/CourseList.vue') },
      { path: 'course-detail/:courseId', component: () => import('@/views/Student/CourseDetail.vue') },
      { path: 'submit-homework', component: () => import('@/views/Student/HomeworkSubmit.vue') },
      { path: 'student-resource', component: () => import('@/views/Student/StudentResource.vue') },
      { path: 'student-topic', component: () => import('@/views/common/CourseTopic.vue') }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ================= 核心路由守卫 =================
router.beforeEach((to, from, next) => {
  // 1. 纯原生方式获取 Token，不依赖 Pinia Store，防止初始化时序问题
  const token = localStorage.getItem('token');

  // 2. 定义不需要登录就能访问的白名单
  const whiteList = ['/login', '/register', '/forgot-password'];
  const isWhiteListed = whiteList.includes(to.path);

  // --- 场景 A：没有 Token ---
  if (!token) {
    if (isWhiteListed) {
      return next(); // 在白名单内，直接放行
    }
    // 不在白名单且无 Token，强制跳回登录页
    ElMessage.warning('请先登录');
    return next('/login');
  }

  // --- 场景 B：有 Token，但试图访问登录/注册页 ---
  if (isWhiteListed) {
    // 如果已经登录，就不允许再进登录页了，直接踢到首页
    return next('/main/dashboard');
  }

  // --- 场景 C：有 Token，需要校验身份信息 ---
  let identity = null;
  try {
    const storedInfo = localStorage.getItem('userInfo');
    if (storedInfo) {
      const userInfo = JSON.parse(storedInfo);
      identity = userInfo.identity;
    }
  } catch (e) {
    console.error('解析用户信息失败', e);
  }

  // 如果有 Token 但没有身份信息（比如数据损坏），清除并踢回登录
  if (!identity) {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    ElMessage.error('登录状态异常，请重新登录');
    return next('/login');
  }

  // --- 场景 D：身份与页面的权限校验 ---
  // 教师专属页面特征
  const teacherPaths = ['/main/dashboard', '/main/teacher-course', '/main/publish-homework', '/main/check-homework', '/main/score', '/main/course-resource', '/main/preparation'];
  // 学生专属页面特征
  const studentPaths = ['/main/student-course', '/main/submit-homework', '/main/student-resource', '/main/student-topic'];

  const isTeacherPage = teacherPaths.includes(to.path);
  const isStudentPage = studentPaths.includes(to.path) || to.path.startsWith('/main/course-detail/');

  // 教师访问学生页面 -> 拦截
  if (identity === 'teacher' && isStudentPage) {
    ElMessage.error('教师无权限访问学生页面');
    return next('/main/dashboard');
  }

  // 学生访问教师页面 -> 拦截
  if (identity === 'student' && isTeacherPage) {
    ElMessage.error('学生无权限访问教师后台');
    return next('/main/student-course');
  }

  // --- 场景 E：一切正常，放行 ---
  next();
});

export default router