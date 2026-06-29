<template>
  <div class="header-wrap">
    <!-- 左侧：Logo + 品牌名 -->
    <div class="logo-section">
      <div class="logo-box">
        <!-- 这里的 src 请确保路径正确，如果还是小图，可能是原图分辨率太低 -->
        <img src="@/assets/image/logo.png" alt="Logo" />
      </div>
      <span class="brand-name">课堂派 ClassPi</span>
    </div>

    <!-- 右侧：用户名 + 退出按钮 -->
    <div class="user-section">
      <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>

      <!-- 按钮样式重构：去掉了 type="danger"，改用 CSS 控制 hover 变红 -->
      <el-button
        round
        size="small"
        @click="logout"
        class="custom-logout-btn"
      >
        <span class="btn-text">退出登录</span>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
/* 1. 顶部栏整体容器 */
.header-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px; /* 增加高度，显得大气 */
  padding: 0 24px;
  background-color: #4f46e5; /* 你的主色调 */
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
  z-index: 10;
}

/* 2. 左侧 Logo 区域优化 */
.logo-section {
  display: flex;
  align-items: center;
  gap: 12px; /* 图片和文字的间距 */
}

.logo-box {
  width: 45px;  /* 限制图片容器大小，防止原图过大撑开 */
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-box img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持图片比例 */
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1)); /* 给Logo加一点点立体感 */
}

.brand-name {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

/* 3. 右侧用户区域优化 */
.user-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  font-weight: 500;
}

/* 4. 核心修改：退出按钮样式 */
.custom-logout-btn {
  /* 默认状态：白色边框，半透明背景，白色文字 */
  border: 1px solid rgba(255, 255, 255, 0.3);
  background-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  padding: 6px 16px;
  font-size: 13px;
  transition: all 0.3s ease;
}

/* 悬停状态：变红，提示危险操作 */
.custom-logout-btn:hover {
  background-color: #ff4d4f; /* 鲜艳的红色 */
  border-color: #ff4d4f;
  color: #fff;
  transform: translateY(-1px); /* 微微上浮，增加点击欲望 */
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.4);
}

/* 适配 Element Plus 的一些默认覆盖 */
.custom-logout-btn :deep(.el-icon) {
  margin-right: 4px;
}
</style>