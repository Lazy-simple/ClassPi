<template>
  <div class="teacher-top-nav">
    <!-- 左侧：返回仪表盘 + 标题 -->
    <div class="nav-left">
      <el-tooltip content="返回仪表盘" placement="bottom">
        <el-button link @click="$router.push('/main/dashboard')" class="back-btn">
          <el-icon :size="20"><HomeFilled /></el-icon>
        </el-button>
      </el-tooltip>
      <span class="page-title">{{ currentTitle }}</span>
    </div>

    <!-- 中间：仅保留“我的课程”和“备课区” -->
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

      <el-menu-item index="/main/preparation">
        <el-icon><FolderOpened /></el-icon> <span>备课区</span>
      </el-menu-item>
    </el-menu>

    <div class="nav-right"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { Reading, FolderOpened, HomeFilled } from '@element-plus/icons-vue';

const route = useRoute();

// 动态高亮当前菜单
// 使用 startsWith 确保在课程详情页(/main/teacher-course-detail/...)时，“我的课程”依然高亮
const activePath = computed(() => {
  if (route.path.startsWith('/main/teacher-course')) return '/main/teacher-course';
  return route.path;
});

// 路由标题映射
const titleMap = {
  '/main/teacher-course': '课程管理中心',
  '/main/preparation': '在线备课室',
  // 如果需要在详情页显示特定标题，可以在组件内单独设置，或者在这里加逻辑
};

const currentTitle = computed(() => titleMap[route.path] || '教师工作台');
</script>

<style scoped>
/* 样式保持你之前的即可 */
.teacher-top-nav { display: flex; justify-content: space-between; align-items: center; height: 50px; padding: 0 40px; background-color: #fff; border-bottom: 1px solid #ebeef5; }
.nav-left { min-width: 180px; display: flex; align-items: center; gap: 12px; }
.back-btn { color: #606266; padding: 0; transition: all 0.3s; }
.back-btn:hover { color: #409EFF; transform: translateX(-2px); }
.page-title { font-size: 16px; font-weight: 600; color: #303133; letter-spacing: 0.5px; }
.nav-menu { border-bottom: none !important; height: 50px; flex: 1; justify-content: center; }
.nav-menu .el-menu-item { height: 50px; line-height: 50px; font-size: 14px; padding: 0 20px; border-bottom: 2px solid transparent; transition: all 0.3s; }
.nav-menu .el-menu-item.is-active { background-color: #ecf5ff; color: #409EFF; border-bottom-color: #409EFF; border-radius: 4px 4px 0 0; }
.nav-right { min-width: 150px; }
</style>