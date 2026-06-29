<template>
  <el-container style="height: 100vh; background-color: #f5f7fa;">
    <!-- 1. 顶部紫色 Header (始终显示) -->
    <el-header style="padding: 0; height: 60px; z-index: 1000;">
      <Header />
    </el-header>

    <!--
      2. 【核心逻辑】教师专属白色导航栏
      判断条件：
      1. 用户身份是 teacher
      2. 当前路由【不是】 /main/dashboard (即不在仪表盘时才显示)
    -->
    <div
      v-if="userStore.userInfo?.identity === 'teacher' && route.path !== '/main/dashboard'"
      class="teacher-nav-bar"
    >
      <TeacherTopNav />
    </div>

    <!-- 3. 主体内容区 -->
    <el-main style="padding: 0; overflow-y: auto; flex: 1;">
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useRoute } from 'vue-router'; // ✅ 引入 useRoute 获取当前路径
import Header from '@/components/Layout/Header.vue';
import TeacherTopNav from '@/views/Teacher/TeacherTopNav.vue';
import { useUserStore } from '@/store/user';

const route = useRoute();
const userStore = useUserStore();

// 初始化用户信息
onMounted(() => {
  if (!userStore.token) {
    userStore.initFromStorage();
  }
});
</script>

<style scoped>
/* 给导航栏加一个阴影，让它和下面内容区分开 */
.teacher-nav-bar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  background: #fff;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.2s;
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(10px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>