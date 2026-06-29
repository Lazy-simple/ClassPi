<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎语 -->
    <header class="page-header">
      <div>
        <!-- 动态显示用户名，如果没有则显示“老师” -->
        <h1 class="title">早安，{{ username }} 👋</h1>
        <p class="subtitle">这是您班级的今日概况，保持专注！</p>
      </div>
      <div class="date-badge">
        📅 {{ currentDate }}
      </div>
    </header>

    <!-- 核心数据卡片区域 (已改为可点击跳转) -->
    <section class="stats-grid">
      <!-- 卡片 1：班级人数 -> 点击跳转课程列表 -->
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

      <!-- 卡片 2：待批阅作业 -> 点击跳转作业批改 -->
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

      <!-- 卡片 3：平均活跃度 -> 点击跳转成绩管理 -->
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
import { useRouter } from 'vue-router';
import request from '@/utils/request'; // 引入你的 axios 封装
import { User, DocumentChecked, TrendCharts, ArrowRight } from '@element-plus/icons-vue';

const router = useRouter();

// 1. 定义响应式数据
// 尝试从 localStorage 获取真实用户名，如果获取不到则默认为“老师”
let storedUser = {};
try {
  const userStr = localStorage.getItem('userInfo');
  if (userStr) storedUser = JSON.parse(userStr);
} catch (e) {
  console.error('解析用户信息失败', e);
}
const username = ref(storedUser.username || storedUser.name || '老师');
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

    // 根据你的后端返回结构调整
    if (res.code === 200 || res.status === 200) {
      // 这里做一个简单的兼容，防止后端字段名不一样导致页面显示 undefined
      const data = res.data || {};
      stats.value = {
        studentCount: data.studentCount || 0,
        pendingHomework: data.pendingHomework || 0,
        avgScore: data.avgScore || 0
      };
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
    // 开发阶段为了看效果，如果接口报错，可以先用假数据兜底
    // 如果你不需要假数据，可以把下面这行注释掉
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
  transition: all 0.3s ease;
  cursor: pointer; /* 鼠标变成手型 */
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

/* 新增：鼠标悬停时的提示文字 */
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