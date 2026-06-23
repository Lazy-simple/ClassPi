<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 课程统计 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>我的课程</template>
          <!-- 1. 动态绑定课程数量 -->
          <div class="stat-num">{{ stats.courseCount }}</div>
          <div class="stat-desc">本学期正在教授/学习</div>
        </el-card>
      </el-col>

      <!-- 2. 根据角色显示不同的卡片 -->
      <template v-if="userRole === 'teacher'">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>待批作业</template>
            <div class="stat-num">{{ stats.pendingHomework }}</div>
            <div class="stat-desc">份作业等待处理</div>
          </el-card>
        </el-col>
      </template>
      <template v-else>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>待交作业</template>
            <div class="stat-num">{{ stats.pendingSubmission }}</div>
            <div class="stat-desc">份作业等待提交</div>
          </el-card>
        </el-col>
      </template>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>班级通知</template>
          <div class="stat-num">{{ stats.unreadNotice }}</div>
          <div class="stat-desc">条未读消息</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome-card" shadow="hover">
      <template #header>系统公告</template>
      <p>欢迎使用智慧教学管理平台！在这里您可以轻松管理课程、发布作业并与学生互动。</p>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/userStore'

const userStore = useUserStore()

// 从 Store 中获取用户角色和统计数据
const userRole = computed(() => userStore.userInfo.role)
const stats = computed(() => userStore.dashboardStats)
</script>

<style scoped>
.dashboard-container { padding: 20px; }
.stat-num { font-size: 36px; font-weight: bold; color: #409EFF; margin: 10px 0; }
.stat-desc { font-size: 14px; color: #909399; }
.welcome-card { margin-top: 20px; }
</style>