<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎语 -->
    <header class="page-header">
      <div>
        <h1 class="title">早安，{{ username }} 👋</h1>
        <p class="subtitle">这是您班级的今日概况，保持专注！</p>
      </div>
      <div class="date-badge">
        📅 {{ currentDate }}
      </div>
    </header>

    <!-- 核心数据卡片区域 -->
    <section class="stats-grid">
      <!-- 卡片 1：班级人数 -->
      <div class="stat-card blue-theme">
        <div class="icon-box">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="info">
          <span class="label">班级总人数</span>
          <span class="value">{{ stats.studentCount }}</span>
        </div>
      </div>

      <!-- 卡片 2：待批阅作业 (动态数据) -->
      <div class="stat-card orange-theme">
        <div class="icon-box">
          <el-icon :size="24"><DocumentChecked /></el-icon>
        </div>
        <div class="info">
          <span class="label">待批阅作业</span>
          <span class="value">{{ stats.pendingHomework }} 份</span>
        </div>
      </div>

      <!-- 卡片 3：平均活跃度 (动态数据) -->
      <div class="stat-card green-theme">
        <div class="icon-box">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="info">
          <span class="label">平均活跃度</span>
          <span class="value">{{ stats.avgScore }}%</span>
        </div>
      </div>
    </section>

    <!-- 下方图表占位区 -->
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
import request from '@/utils/request'; // 引入你的 axios 封装
import { User, DocumentChecked, TrendCharts } from '@element-plus/icons-vue';

// 1. 定义响应式数据
const username = ref('老师'); // 实际应从 Store 或 LocalStorage 获取
const currentDate = ref(new Date().toLocaleDateString());

// 初始化统计数据（给默认值防止闪烁）
const stats = ref({
  studentCount: 0,
  pendingHomework: 0,
  avgScore: 0
});

// 2. 获取数据的函数
const fetchDashboardData = async () => {
  try {
    // 假设后端接口地址是 /api/teacher/stats
    const res = await request.get('/api/teacher/stats');

    // 根据你的后端返回结构调整，这里假设 res.data 就是我们要的数据对象
    if (res.code === 200 || res.status === 200) {
      stats.value = res.data;
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
    // 开发阶段为了看效果，如果接口报错，可以先用假数据兜底
    // stats.value = { studentCount: 45, pendingHomework: 12, avgScore: 88 };
  }
};

// 3. 页面加载时执行
onMounted(() => {
  fetchDashboardData();
});
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa; /* 浅灰背景 */
  min-height: 100vh;
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

/* 卡片网格布局 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.icon-box {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.blue-theme .icon-box { background: #ecf5ff; color: #409eff; }
.orange-theme .icon-box { background: #fdf6ec; color: #e6a23c; }
.green-theme .icon-box { background: #f0f9eb; color: #67c23a; }

.info {
  display: flex;
  flex-direction: column;
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