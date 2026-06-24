<template>
  <el-menu
    :default-active="activePath"
    router
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
    class="sidebar-menu"
  >
    <!-- 老师的仪表盘 -->
    <el-menu-item index="/main/dashboard">
      <el-icon><DataLine /></el-icon>
      <span>数据驾驶舱</span>
    </el-menu-item>

    <!-- 课程管理 -->
    <el-menu-item index="/main/teacher-course">
      <el-icon><Reading /></el-icon>
      <span>课程管理</span>
    </el-menu-item>

    <!-- 作业发布 -->
    <el-menu-item index="/main/publish-homework">
      <el-icon><EditPen /></el-icon>
      <span>发布作业</span>
    </el-menu-item>

    <!-- 作业批阅 -->
    <el-menu-item index="/main/check-homework">
      <el-icon><DocumentChecked /></el-icon>
      <span>批阅作业</span>
    </el-menu-item>

    <!-- 成绩管理 -->
    <el-menu-item index="/main/score">
      <el-icon><TrendCharts /></el-icon>
      <span>成绩管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
// 引入需要的图标（确保你已经安装了 @element-plus/icons-vue）
import { DataLine, Reading, EditPen, DocumentChecked, TrendCharts } from '@element-plus/icons-vue';

const route = useRoute();
// 计算当前激活的菜单项
const activePath = computed(() => route.path);
</script>

<style scoped>
/* 去掉默认的右边框，让侧边栏更好看 */
.sidebar-menu {
  border-right: none;
}
</style>