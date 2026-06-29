<template>
  <div class="teacher-top-nav">
    <!-- 左侧：返回按钮 + 当前页面标题 -->
    <div class="nav-left">
      <el-button link @click="$router.push('/main/dashboard')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回仪表盘</span>
      </el-button>

      <el-divider direction="vertical" />

      <!-- 根据当前路由自动显示中文标题 -->
      <span class="page-title">{{ currentTitle }}</span>
    </div>

    <!-- 右侧：横向功能菜单 -->
    <el-menu
      :default-active="activePath"
      router
      mode="horizontal"
      class="nav-menu"
      active-text-color="#409EFF"
    >
      <el-menu-item index="/main/teacher-course">
        <el-icon><Reading /></el-icon> 我的课程
      </el-menu-item>

      <el-menu-item index="/main/publish-homework">
        <el-icon><EditPen /></el-icon> 发布作业
      </el-menu-item>

      <el-menu-item index="/main/check-homework">
        <el-icon><DocumentChecked /></el-icon> 批改作业
      </el-menu-item>

      <el-menu-item index="/main/score">
        <el-icon><TrendCharts /></el-icon> 成绩管理
      </el-menu-item>

      <el-menu-item index="/main/preparation">
        <el-icon><FolderOpened /></el-icon> 备课区
      </el-menu-item>

      <el-menu-item index="/main/course-resource">
        <el-icon><FolderOpened /></el-icon> 教学资源
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { ArrowLeft, Reading, EditPen, DocumentChecked, TrendCharts, FolderOpened } from '@element-plus/icons-vue';

const route = useRoute();

// 获取当前激活的菜单路径
const activePath = computed(() => route.path);

// 路由路径与中文标题的映射表（方便以后扩展）
const titleMap = {
  '/main/teacher-course': '我的课程',
  '/main/preparation': '备课区',
  '/main/publish-homework': '发布新作业',
  '/main/check-homework': '作业批阅',
  '/main/score': '成绩统计分析',
  '/main/course-resource': '教学资源库'
};

// 动态计算当前页面的标题
const currentTitle = computed(() => {
  return titleMap[route.path] || '教师工作台';
});
</script>

<style scoped>
.teacher-top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 24px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.nav-left {
  display: flex;
  align-items: center;
}

.back-btn {
  font-size: 15px;
  color: #606266;
  padding: 0;
}

.back-btn:hover {
  color: #409EFF;
}

.page-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

/* 覆盖 Element Plus 菜单样式，使其更紧凑 */
.nav-menu {
  border-bottom: none !important;
  height: 60px;
}

.nav-menu .el-menu-item {
  height: 60px;
  line-height: 60px;
  font-size: 14px;
}
</style>