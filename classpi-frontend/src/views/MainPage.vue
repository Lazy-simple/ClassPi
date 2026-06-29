<template>
  <div class="main-container">
    <!-- 智能判断：非仪表盘页面才显示顶部导航 -->
    <TeacherTopNav v-if="!isDashboard" />

    <!-- 页面主体内容区 -->
    <main class="content-area">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import TeacherTopNav from './Teacher/TeacherTopNav.vue'; // 引入刚才新建的组件

const route = useRoute();

// 判断当前是否是仪表盘首页
const isDashboard = computed(() => {
  return route.path === '/main/dashboard';
});
</script>

<style scoped>
.main-container {
  min-height: 100vh;
  background-color: #f5f7fa; /* 全局浅灰背景 */
  display: flex;
  flex-direction: column;
}

.content-area {
  flex: 1;
  /* 如果需要在非仪表盘页面加内边距，可以在这里控制 */
}
</style>