<template>
  <div class="teacher-course-detail">
    <!--
      【一级视图：课程上下文】
      这里展示当前是在哪个课程里。
      注意：顶部的全局导航（我的课程/备课区）属于 Layout 层，不需要在这里写，
      只要确保它们不会被这个页面的 z-index 遮挡即可。
    -->
    <div class="course-header-card">
      <div class="header-content">
        <!-- 返回按钮：这是最重要的导航，带用户回到列表 -->
        <div class="back-link" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回我的课程</span>
        </div>

        <h1 class="course-title">{{ courseInfo.name }}</h1>
        <p class="sub-info">
          课程号：{{ courseInfo.courseNo }} | 学分：{{ courseInfo.credit }}
        </p>
      </div>
      <!-- 装饰背景 -->
      <div class="header-bg"></div>
    </div>

    <!--
      【二级视图：课程内功能】
      这是当前页面的核心操作区。
      为了不和顶部导航冲突，这里的 Tabs 样式要做得明显一些。
    -->
    <div class="course-tabs-container">
      <el-tabs v-model="activeTab" class="course-tabs" @tab-click="handleTabClick">
        <!-- 这里的图标保留，增加辨识度 -->
        <el-tab-pane label="📝 发布作业" name="publish-homework"></el-tab-pane>
        <el-tab-pane label="✅ 批改作业" name="check-homework"></el-tab-pane>
        <el-tab-pane label="📊 成绩管理" name="score"></el-tab-pane>
        <el-tab-pane label="📂 教学资源" name="resource"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 【内容区域】 -->
    <div class="tab-content-area">
      <keep-alive>
        <component :is="currentComponent" :course-id="courseId" />
      </keep-alive>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router'; // 引入 useRouter
import { ArrowLeft } from '@element-plus/icons-vue'; // 确保引入图标

// 引入子组件
import HomeworkPublish from './HomeworkPublish.vue';
import HomeworkCheck from './HomeworkCheck.vue';
import ScoreManage from './ScoreManage.vue';
import CourseResource from './CourseResource.vue';
import { getCourseById } from '@/api/course';

const route = useRoute();
const router = useRouter(); // 初始化 router
const courseId = route.params.courseId;
const activeTab = ref('publish-homework');
const courseInfo = ref({});

const componentMap = {
  'publish-homework': HomeworkPublish,
  'check-homework': HomeworkCheck,
  'score': ScoreManage,
  'resource': CourseResource
};

const currentComponent = computed(() => componentMap[activeTab.value]);

onMounted(async () => {
  const res = await getCourseById(courseId);
  if (res.code === 200) {
    courseInfo.value = res.data;
  }
});

// 返回上一级逻辑
const goBack = () => {
  // 这里的逻辑是：如果有历史记录就回退，没有就强制去课程列表页
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/main/teacher-course'); // 你的课程列表页路由
  }
};

const handleTabClick = (tab) => {
  console.log('切换到了:', tab.props.name);
};
</script>

<style scoped>
.teacher-course-detail {
  padding: 20px 40px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 头部卡片 */
.course-header-card {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  padding: 30px 30px 20px 30px; /* 调整内边距 */
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(118, 75, 162, 0.2);
}

.header-content {
  position: relative;
  z-index: 2;
}

/* 返回链接样式优化 */
.back-link {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 15px;
  transition: all 0.3s;
}
.back-link:hover {
  opacity: 1;
  transform: translateX(-5px); /* 悬停时向左移动一点，增加动感 */
}
.back-link .el-icon {
  margin-right: 4px;
}

.course-title {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 600;
}

.sub-info {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

/* Tabs 容器 */
.course-tabs-container {
  background: #fff;
  border-radius: 8px 8px 0 0;
  padding: 0 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  margin-bottom: 0; /* 紧贴下方内容 */
}

.course-tabs {
  border-bottom: none;
}

/* 深度选择器调整 Tabs 样式，使其更显眼 */
:deep(.el-tabs__item) {
  font-size: 16px;
  height: 50px;
  line-height: 50px;
  font-weight: 500;
  color: #606266;
}
:deep(.el-tabs__item.is-active) {
  color: #764ba2; /* 激活颜色与头部呼应 */
  font-weight: bold;
}
:deep(.el-tabs__active-bar) {
  background-color: #764ba2;
  height: 3px; /* 加粗下划线 */
}

/* 内容区域 */
.tab-content-area {
  background: #fff;
  padding: 25px;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  min-height: 500px;
}
</style>