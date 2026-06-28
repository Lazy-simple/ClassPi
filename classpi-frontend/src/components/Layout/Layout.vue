<template>
  <div class="layout-container">
    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <!-- Logo区域 -->
      <div class="logo-box">
        <!-- 请确保你的 assets 路径正确，或者暂时注释掉 img 标签防止报错 -->
        <img src="@/assets/image/logo.png" alt="Logo" class="logo-img" onerror="this.style.display='none'" />
        <span class="logo-text">课堂派 ClassPi</span>
      </div>

      <!-- 菜单组件插槽 -->
      <div class="menu-area">
        <!-- ⚠️ 核心修改点：使用动态组件 -->
        <!-- 根据用户角色 role 的值，动态加载对应的菜单组件 -->
        <component :is="currentSidebar" />
      </div>
    </aside>

    <!-- 右侧主体内容 -->
    <div class="main-wrapper">
      <!-- 顶部导航栏 -->
      <header class="top-header">
        <Header />
      </header>

      <!-- 页面内容区域 -->
      <main class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
import Header from './Header.vue'

// 1. 引入两个不同的侧边栏组件
import SidebarTeacher from './SidebarTeacher.vue'
import SidebarStudent from './SidebarStudent.vue'

const userStore = useUserStore()

// 2. 定义计算属性：根据角色返回对应的组件对象
const currentSidebar = computed(() => {
  const identity = userStore.userInfo?.identity;
  console.log('=== Layout.vue 调试信息 ===')
  console.log('userStore:', userStore)
  console.log('userStore.userInfo:', userStore.userInfo)
  console.log('identity:', identity)
  
  if (identity === 'teacher' || identity === 'Teacher') {
    return SidebarTeacher
  } else if (identity === 'student' || identity === 'Student') {
    return SidebarStudent
  } else {
    console.warn('⚠️ 身份未知:', identity)
    return null
  }
})
</script>

<style scoped>
/* 整体容器：Flex布局，占满全屏 */
.layout-container {
  display: flex;
  width: 100%;
  height: 100vh; /* 强制占满视口高度 */
  background-color: #f0f2f5; /* 全局浅灰背景 */
  overflow: hidden; /* 防止出现双重滚动条 */
}

/* --- 左侧侧边栏样式 --- */
.sidebar {
  width: 220px;
  background-color: #304156; /* 经典的深色侧边栏颜色 */
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  background-color: #2b3a4d;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-img {
  height: 32px;
  margin-right: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  white-space: nowrap;
}

.menu-area {
  flex: 1;
  overflow-y: auto;
}

/* 隐藏侧边栏滚动条但保留功能 */
.menu-area::-webkit-scrollbar {
  width: 0;
}

/* --- 右侧主体样式 --- */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.top-header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
  display: flex;
  align-items: center;
}

.page-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* --- 路由切换动画 --- */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>