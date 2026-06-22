<template>
    <div>
        <img src="@/assets/image/logo.png">
    </div>
    <div>
        <button @click="logout">退出登录</button>
        <h1>欢迎回来，{{ userInfo?.name }}！</h1>
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