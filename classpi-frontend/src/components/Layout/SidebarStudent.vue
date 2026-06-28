<template>
  <el-menu
      :default-active="activePath"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      class="sidebar-menu"
  >
    <el-menu-item index="/main/student-course">
      <el-icon><Collection /></el-icon>
      <span>我的课程</span>
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