<template>
  <!-- 使用 Element Plus 的布局容器 -->
  <el-container style="height: 100vh; background-color: #f3f4f6;">

    <!-- 1. 左侧侧边栏 -->
    <!-- 根据用户角色，动态加载不同的侧边栏组件 -->
    <el-aside v-if="userStore.userInfo?.role === 1" width="220px" style="background-color: #304156;">
      <SidebarTeacher />
    </el-aside>
    <el-aside v-else width="220px" style="background-color: #304156;">
      <SidebarStudent />
    </el-aside>

    <el-container>
      <!-- 2. 顶部 Header -->
      <el-header style="padding: 0; height: 60px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <Header />
      </el-header>

      <!-- 3. 核心内容区 -->
      <el-main style="padding: 20px;">
        <!-- 这里会显示具体的页面内容 -->
        <router-view />
      </el-main>
    </el-container>

  </el-container>
</template>

<script setup>
import { useUserStore } from '@/store/user'
// 引入老师和学生各自的侧边栏组件
import Header from '@/components/Layout/Header.vue'
import SidebarTeacher from '@/components/Layout/SidebarTeacher.vue'
import SidebarStudent from '@/components/Layout/SidebarStudent.vue'

const userStore = useUserStore()
</script>