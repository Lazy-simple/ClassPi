<template>
  <div class="header-wrap">
    <div class="logo">
      <img src="@/assets/image/logo.png" width="40" />
      <span>课堂派ClassPi</span>
    </div>
    <div class="user-info">
      <span>{{ userStore.userInfo?.username }}</span>
      <el-button text @click="logout">退出登录</el-button>
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
.header-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background: #4f46e5;
  color: #fff;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
}
.user-info {
  display: flex;
  gap: 15px;
  align-items: center;
}
</style>