<template>
  <el-container style="height: 100vh; background-color: #f3f4f6;">
    <!--
      修改点 1: height 改为 64px 以适配你的 Header.vue
      padding: 0 是必须的，否则 Element Plus 会自带 20px 内边距导致错位
    -->
    <el-header style="padding: 0; height: 64px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
      <!-- 这里导入你的 Header 组件 -->
      <Header />
    </el-header>

    <!-- 主体内容区 -->
    <el-main style="padding: 20px; overflow-y: auto;">
      <!-- 建议加上 v-slot 写法以支持过渡动画，如果不需要可改回简单的 <router-view /> -->
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
// 确保路径与你实际的文件位置一致
// 如果报错找不到，请检查 src/components/Layout/Header.vue 是否存在
import Header from '@/components/Layout/Header.vue'
</script>

<style scoped>
/* 可选：添加一点过渡动画效果 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.2s;
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