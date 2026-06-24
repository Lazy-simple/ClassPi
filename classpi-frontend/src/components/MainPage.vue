<template>
  <div>
    <img src="@/assets/image/logo.png">
  </div>
  <div>
    <button @click="logout">退出登录</button>
    <h1>欢迎回来，{{ userStore.userInfo?.name || userStore.userInfo?.username || '用户' }}！</h1>
    <!-- 显示更多用户信息方便调试 -->
    <p v-if="userStore.userInfo">
      身份: {{ userStore.userInfo.identity }} |
      学校: {{ userStore.userInfo.school }}
    </p>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/userStore';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

// 退出登录
const logout = () => {
  userStore.logout();
  router.push('/login');
};
</script>
<style>

</style>