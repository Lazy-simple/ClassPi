<template>
  <el-menu
      :default-active="activePath"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      class="sidebar-menu"
  >
    <el-menu-item index="/main/student-dashboard">
      <el-icon><HomeFilled /></el-icon>
      <span>我的主页</span>
    </el-menu-item>
    <el-menu-item index="/main/student-course">
      <el-icon><Collection /></el-icon>
      <span>选课中心</span>
    </el-menu-item>
    <el-menu-item index="/main/student-resource">
      <el-icon><Document /></el-icon>
      <span>课程资料</span>
    </el-menu-item>
    <el-menu-item index="/main/student-topic">
      <el-icon><ChatDotRound /></el-icon>
      <span>课程讨论</span>
    </el-menu-item>
    <el-menu-item index="/main/submit-homework">
      <el-icon><UploadFilled /></el-icon>
      <span>提交作业</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router'
import { HomeFilled, Collection, Document, ChatDotRound, UploadFilled } from '@element-plus/icons-vue';
const route = useRoute();
const activePath = computed(() => route.path);
</script>

<style scoped>
.sidebar-menu {
  border-right: none;
}
</style>