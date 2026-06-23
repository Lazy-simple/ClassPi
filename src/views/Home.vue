<template>
  <div class="home-layout">
    <!-- 顶部导航 -->
    <header class="top-header">
      <div class="logo-box">
        <img src="@/assets/image/logo.png" height="32" alt="Logo" />
        <span class="title">智慧教学平台</span>
      </div>

      <div class="user-box">
        <el-dropdown @command="handleCommand">
          <div class="user-info">
            <!-- 1. 动态绑定头像，如果没有则显示默认图 -->
            <el-avatar :size="32" :src="userStore.userInfo?.avatar || defaultAvatar" />
            <!-- 2. 动态绑定用户名 -->
            <span class="username">{{ userStore.userInfo?.name || '加载中...' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="setting">设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          @select="handleMenuSelect"
          background-color="#fff"
          text-color="#333"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/home/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>工作台</span>
          </el-menu-item>
          <el-menu-item index="/home/course">
            <el-icon><Notebook /></el-icon>
            <span>我的课程</span>
          </el-menu-item>
          <el-menu-item index="/home/homework">
            <el-icon><Document /></el-icon>
            <span>作业批改</span>
          </el-menu-item>
          <el-menu-item index="/home/user">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 内容区域 -->
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/userStore' // 引入 Store

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 默认头像，可以使用你提供的链接
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 根据当前路由高亮菜单
const activeMenu = computed(() => route.path)

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    // 3. 调用 Store 的 logout 方法清除状态
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/home/user')
  }
}

// 可选：可以在这里添加逻辑，如果用户信息为空，则请求一次用户详情接口
onMounted(() => {
  if (!userStore.userInfo) {
    // fetchUserInfo()
  }
})
</script>

<style scoped>
/* ... 你的样式保持不变 ... */
.home-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.top-header {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  z-index: 10;
}
.logo-box {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}
.logo-box img { margin-right: 10px; }
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background 0.3s;
}
.user-info:hover { background: #f5f7fa; }
.username { margin: 0 8px; font-size: 14px; color: #606266; }
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  background-color: #f0f2f5;
}
.sidebar {
  width: 220px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  overflow-y: auto;
}
.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>