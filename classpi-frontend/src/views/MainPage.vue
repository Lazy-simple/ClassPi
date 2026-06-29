<template>
  <el-container class="app-layout">
    <!-- 顶部固定 -->
    <el-header height="64px" class="app-header">
      <Header />
    </el-header>

    <el-container class="main-body">
      <!-- 【核心逻辑】只有当身份是 teacher 时，才显示左侧侧边栏 -->
      <el-aside v-if="isTeacher" width="220px" class="sidebar-container">
        <SidebarTeacher />
      </el-aside>

      <!-- 内容区域 -->
      <el-main class="app-main">
        <!-- 给学生端加一个最大宽度限制，防止在大屏幕上太散，老师端通常全屏 -->
        <div :class="['content-wrapper', { 'student-limit': !isTeacher }]">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useUserStore } from '@/store/user';
import Header from '@/components/Layout/Header.vue';
import SidebarTeacher from '@/components/Layout/SidebarTeacher.vue';

const userStore = useUserStore();

// 计算属性：判断是否为老师
const isTeacher = computed(() => {
  return userStore.userInfo?.identity === 'teacher';
});
</script>

<style scoped>
.app-layout {
  height: 100vh;
  background-color: #f5f7fa; /* 浅灰背景，护眼且高级 */
}

.app-header {
  padding: 0;
  background-color: #fff;
  z-index: 10;
}

.main-body {
  height: calc(100vh - 64px); /* 减去头部高度 */
}

.sidebar-container {
  background-color: #304156; /* 深色侧边栏 */
  overflow-y: auto;
  transition: width 0.3s;
}

/* 隐藏滚动条但保留功能 */
.sidebar-container::-webkit-scrollbar {
  width: 0px;
}

.app-main {
  padding: 20px;
  overflow-y: auto;
}

.content-wrapper {
  /* 默认全屏 */
  width: 100%;
  height: 100%;
}

/* 针对学生端的优化：限制最大宽度并居中，像选课中心那种卡片流布局在超宽屏下会更好看 */
.student-limit {
  max-width: 1400px;
  margin: 0 auto;
}
</style>