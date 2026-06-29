<template>
  <div class="teacher-course-detail">
    <!-- 【一级视图：课程上下文】 -->
    <div class="course-header-card">
      <div class="header-content">
        <!-- 返回按钮 -->
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

    <!-- 【二级视图：课程内功能】 -->
    <div class="course-tabs-container">
      <el-tabs v-model="activeTab" class="course-tabs" @tab-click="handleTabClick">
        <el-tab-pane label="📝 发布作业" name="publish-homework"></el-tab-pane>
        <el-tab-pane label="✅ 批改作业" name="check-homework"></el-tab-pane>
        <el-tab-pane label="📊 成绩管理" name="score"></el-tab-pane>
        <el-tab-pane label="📂 教学资源" name="resource"></el-tab-pane>
        <!-- ✅ 新增：讨论区页签 -->
        <el-tab-pane label="💬 课程讨论" name="topic"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 【内容区域】 -->
    <div class="tab-content-area">
      <!-- ✅ 修复：去掉了注释，确保 KeepAlive 只有一个子元素 -->
      <keep-alive>
        <component :is="currentComponent" :course-id="courseId" />
      </keep-alive>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';

// 引入子组件 (注意路径)
import HomeworkPublish from './HomeworkPublish.vue';
import HomeworkCheck from './HomeworkCheck.vue';
import ScoreManage from './ScoreManage.vue';
import CourseResource from './CourseResource.vue';
// ✅ 修改：从 common 目录引入 CourseTopic
import CourseTopic from '../common/CourseTopic.vue';

import { getCourseById } from '@/api/course'; // 假设你的 api 路径是这个

const route = useRoute();
const router = useRouter();
const courseId = route.params.courseId;
const activeTab = ref('publish-homework');
const courseInfo = ref({});

// ✅ 修改：注册新的组件映射
const componentMap = {
  'publish-homework': HomeworkPublish,
  'check-homework': HomeworkCheck,
  'score': ScoreManage,
  'resource': CourseResource,
  'topic': CourseTopic // 对应上面 el-tab-pane 的 name="topic"
};

const currentComponent = computed(() => componentMap[activeTab.value]);

onMounted(async () => {
  const res = await getCourseById(courseId);
  if (res.code === 200) {
    courseInfo.value = res.data;
  }
});

const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/main/teacher-course');
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
  padding: 30px 30px 20px 30px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(118, 75, 162, 0.2);
}

.header-content {
  position: relative;
  z-index: 2;
}

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
  transform: translateX(-5px);
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
  margin-bottom: 0;
}

.course-tabs {
  border-bottom: none;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  height: 50px;
  line-height: 50px;
  font-weight: 500;
  color: #606266;
}
:deep(.el-tabs__item.is-active) {
  color: #764ba2;
  font-weight: bold;
}
:deep(.el-tabs__active-bar) {
  background-color: #764ba2;
  height: 3px;
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