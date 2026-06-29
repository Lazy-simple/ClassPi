<template>
  <div class="teacher-top-nav">
    <!-- 【修改处】左侧：返回按钮 + 面包屑/标题 -->
    <div class="nav-left">
      <!-- 1. 新增返回仪表盘按钮 -->
      <el-tooltip content="返回仪表盘" placement="bottom">
        <el-button
          link
          @click="$router.push('/main/dashboard')"
          class="back-btn"
        >
          <el-icon :size="20"><HomeFilled /></el-icon>
        </el-button>
      </el-tooltip>

      <!-- 2. 原有的标题 -->
      <span class="page-title">{{ currentTitle }}</span>
    </div>

    <!-- 中间：横向菜单 (保持不变) -->
    <el-menu
      :default-active="activePath"
      router
      mode="horizontal"
      class="nav-menu"
      active-text-color="#409EFF"
    >
      <el-menu-item index="/main/teacher-course">
        <el-icon><Reading /></el-icon> <span>我的课程</span>
      </el-menu-item>

      <el-menu-item index="/main/publish-homework">
        <el-icon><EditPen /></el-icon> <span>发布作业</span>
      </el-menu-item>

      <el-menu-item index="/main/check-homework">
        <el-icon><DocumentChecked /></el-icon> <span>批改作业</span>
      </el-menu-item>

      <el-menu-item index="/main/score">
        <el-icon><TrendCharts /></el-icon> <span>成绩管理</span>
      </el-menu-item>

      <el-menu-item index="/main/preparation">
        <el-icon><FolderOpened /></el-icon> <span>备课区</span>
      </el-menu-item>

      <el-menu-item index="/main/course-resource">
        <el-icon><Files /></el-icon> <span>教学资源</span>
      </el-menu-item>
    </el-menu>

    <!-- 右侧：留空 -->
    <div class="nav-right"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router'; // 确保引入了 useRouter
import {
  Reading, EditPen, DocumentChecked, TrendCharts, FolderOpened, Files,
  HomeFilled // 【新增】引入房子图标作为返回Dashboard的标志
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

// 动态高亮当前菜单
const activePath = computed(() => route.path);

// 路由标题映射
const titleMap = {
  '/main/teacher-course': '课程管理中心',
  '/main/preparation': '在线备课室',
  '/main/publish-homework': '发布新作业',
  '/main/check-homework': '作业批阅台',
  '/main/score': '学情分析报表',
  '/main/course-resource': '教学资源库'
};

const currentTitle = computed(() => titleMap[route.path] || '教师工作台');
</script>

<style scoped>
.teacher-top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
  padding: 0 40px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

/* 【修改处】调整左侧布局，让按钮和标题挨得近一点 */
.nav-left {
  min-width: 180px; /*稍微加宽一点容纳按钮*/
  display: flex;
  align-items: center;
  gap: 12px; /* 按钮和文字的间距 */
}

/* 返回按钮样式微调 */
.back-btn {
  color: #606266;
  padding: 0;
  transition: all 0.3s;
}
.back-btn:hover {
  color: #409EFF;
  transform: translateX(-2px); /* 悬停时微微向左动，暗示“回去” */
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 0.5px;
}

/* ...其余样式保持不变... */
.nav-menu {
  border-bottom: none !important;
  height: 50px;
  flex: 1;
  justify-content: center;
}
.nav-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  padding: 0 20px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}
.nav-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409EFF;
  border-bottom-color: #409EFF;
  border-radius: 4px 4px 0 0;
}
.nav-menu .el-menu-item:hover {
  background-color: #f5f7fa;
  color: #409EFF;
}
.nav-right {
  min-width: 150px;
}
</style>