<template>
  <el-menu
      :default-active="activePath"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      class="sidebar-menu"
  >
    <el-menu-item index="/main/dashboard">
      <el-icon><DataLine /></el-icon>
      <span>数据驾驶舱</span>
    </el-menu-item>
    <el-menu-item index="/main/teacher-course">
      <el-icon><Reading /></el-icon>
      <span>课程管理</span>
    </el-menu-item>
    <el-menu-item index="/main/course-resource">
      <el-icon><Document /></el-icon>
      <span>课程资料</span>
    </el-menu-item>
    <el-menu-item index="/main/course-topic">
      <el-icon><ChatDotRound /></el-icon>
      <span>课程讨论</span>
    </el-menu-item>
    <el-menu-item index="/main/preparation">
      <el-icon><FolderOpened /></el-icon>
      <span>备课区</span>
    </el-menu-item>
    <el-menu-item index="/main/publish-homework">
      <el-icon><EditPen /></el-icon>
      <span>发布作业</span>
    </el-menu-item>
    <el-menu-item index="/main/check-homework">
      <el-icon><DocumentChecked /></el-icon>
      <span>批阅作业</span>
    </el-menu-item>
    <el-menu-item index="/main/score">
      <el-icon><TrendCharts /></el-icon>
      <span>成绩管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { DataLine, Reading, Document, ChatDotRound, EditPen, DocumentChecked, TrendCharts, FolderOpened } from '@element-plus/icons-vue';
const route = useRoute();
const activePath = computed(() => route.path);
</script>

<style scoped>
.sidebar-menu {
  border-right: none;
}
</style>