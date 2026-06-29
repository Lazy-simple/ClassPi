<template>
  <div class="dashboard-container">
    <!-- 1. 顶部欢迎语区域 -->
    <header class="page-header">
      <div>
        <h1 class="title">早安，{{ username }} 👋</h1>
        <p class="subtitle">这是您班级的今日概况，保持专注！</p>
      </div>
      <div class="date-badge">
        📅 {{ currentDate }}
      </div>
    </header>

    <!-- 2. 【新增】核心引导区：进入工作台 -->
    <!-- 放在最显眼的位置，解决“登录后不知道去哪”的问题 -->
    <section class="welcome-action-section">
      <div class="action-card" @click="goToWorkbench">
        <div class="action-icon-wrapper">
          <el-icon :size="32"><Monitor /></el-icon>
        </div>
        <div class="action-content">
          <h3>进入教师工作台</h3>
          <p>开始备课、发布作业或管理成绩</p>
        </div>
        <el-button type="primary" round size="large" class="enter-btn">
          立即进入 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </section>

    <!-- 3. 核心数据卡片区域 (保留原图标与逻辑) -->
    <section class="stats-grid">
      <!-- 卡片 1：班级人数 -->
      <div class="stat-card blue-theme" @click="$router.push('/main/teacher-course')">
        <div class="icon-box">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="info">
          <span class="label">班级总人数</span>
          <span class="value">{{ stats.studentCount }}</span>
        </div>
        <div class="hover-hint">点击查看课程 <el-icon><ArrowRight /></el-icon></div>
      </div>

      <!-- 卡片 2：待批阅作业 -->
      <div class="stat-card orange-theme" @click="$router.push('/main/check-homework')">
        <div class="icon-box">
          <el-icon :size="24"><DocumentChecked /></el-icon>
        </div>
        <div class="info">
          <span class="label">待批阅作业</span>
          <span class="value">{{ stats.pendingHomework }} 份</span>
        </div>
        <div class="hover-hint">立即去批阅 <el-icon><ArrowRight /></el-icon></div>
      </div>

      <!-- 卡片 3：平均活跃度 -->
      <div class="stat-card green-theme" @click="$router.push('/main/score')">
        <div class="icon-box">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="info">
          <span class="label">平均活跃度</span>
          <span class="value">{{ stats.avgScore }}%</span>
        </div>
        <div class="hover-hint">查看详细成绩 <el-icon><ArrowRight /></el-icon></div>
      </div>
    </section>

    <!-- 4. 下方图表占位区 -->
    <section class="chart-section">
      <div class="card-header">
        <h3>📊 班级学习活跃度热力图</h3>
        <el-button size="small" plain>最近7天</el-button>
      </div>
      <div class="chart-placeholder">
        <el-empty description="暂无数据，请等待后端接入图表组件" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
// ✅ 保留你原来的所有图标，新增 Monitor 用于工作台按钮
import { User, DocumentChecked, TrendCharts, ArrowRight, Monitor } from '@element-plus/icons-vue';

const router = useRouter();

// 1. 定义响应式数据
let storedUser = {};
try {
  const userStr = localStorage.getItem('userInfo');
  if (userStr) storedUser = JSON.parse(userStr);
} catch (e) {
  console.error('解析用户信息失败', e);
}
const username = ref(storedUser.username || storedUser.name || '老师');
const currentDate = ref(new Date().toLocaleDateString());

const stats = ref({
  studentCount: 0,
  pendingHomework: 0,
  avgScore: 0
});

// 2. 获取数据的函数
const fetchDashboardData = async () => {
  try {
    const res = await request.get('/api/teacher/stats');
    if (res.code === 200 || res.status === 200) {
      const data = res.data || {};
      stats.value = {
        studentCount: data.studentCount || 0,
        pendingHomework: data.pendingHomework || 0,
        avgScore: data.avgScore || 0
      };
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
  }
};

// ✅ 新增：跳转到工作台的函数
// 这里指向 /main/teacher-course 是因为通常工作台默认展示课程列表
const goToWorkbench = () => {
  router.push('/main/teacher-course');
};

onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
  max-width: 1400px; /* 增加最大宽度限制，防止在大屏上太散 */
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #909399;
  margin: 0;
}

.date-badge {
  background: #fff;
  padding: 8px 16px;
  border-radius: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  font-weight: bold;
  color: #606266;
}

/* ✅ 新增：工作台引导卡片样式 */
.welcome-action-section {
  margin-bottom: 24px;
}

.action-card {
  background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.4);
}

.action-icon-wrapper {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24px;
  backdrop-filter: blur(4px);
}

.action-content h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
}

.action-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.enter-btn {
  background: #fff;
  color: #409EFF;
  border: none;
  font-weight: bold;
  padding: 12px 32px;
}

.enter-btn:hover {
  background: #ecf5ff;
}

/* 原有卡片样式保持不变 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border: 1px solid transparent;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border-color: #dcdfe6;
}

.hover-hint {
  position: absolute;
  bottom: -20px;
  right: 20px;
  font-size: 12px;
  color: #909399;
  opacity: 0;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-card:hover .hover-hint {
  bottom: 10px;
  opacity: 1;
}

.icon-box {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.blue-theme .icon-box { background: #ecf5ff; color: #409eff; }
.orange-theme .icon-box { background: #fdf6ec; color: #e6a23c; }
.green-theme .icon-box { background: #f0f9eb; color: #67c23a; }

.info {
  display: flex;
  flex-direction: column;
  z-index: 1;
}

.label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 300px;
  background: #fafafa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
}
</style>