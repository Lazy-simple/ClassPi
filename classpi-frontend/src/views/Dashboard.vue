<template>
  <div class="dashboard-container">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1>课堂派</h1>
          <div class="user-info">
            <span>欢迎，{{ userStore.userInfo?.username || '用户' }}</span>
            <el-button type="danger" size="small" @click="handleLogout">
              退出登录
            </el-button>
          </div>
        </div>
      </el-header>
      
      <el-main class="main">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="menu-card">
              <template #header>
                <div class="card-header">
                  <span>功能菜单</span>
                </div>
              </template>
              <el-menu
                default-active="1"
                class="el-menu-vertical-demo"
              >
                <el-menu-item index="1">
                  <el-icon><House /></el-icon>
                  <span>首页</span>
                </el-menu-item>
                <el-menu-item index="2">
                  <el-icon><Document /></el-icon>
                  <span>课程管理</span>
                </el-menu-item>
                <el-menu-item index="3">
                  <el-icon><User /></el-icon>
                  <span>学生管理</span>
                </el-menu-item>
                <el-menu-item index="4">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>成绩管理</span>
                </el-menu-item>
              </el-menu>
            </el-card>
          </el-col>
          
          <el-col :span="18">
            <el-card class="content-card">
              <template #header>
                <div class="card-header">
                  <span>欢迎使用课堂派</span>
                </div>
              </template>
              <div class="welcome-content">
                <h2>欢迎来到课堂派系统</h2>
                <p>这是一个基于 Spring Boot + Vue + Element Plus 的课堂管理系统</p>
                <el-divider />
                <h3>系统功能</h3>
                <ul>
                  <li>用户登录注册</li>
                  <li>课程管理</li>
                  <li>学生管理</li>
                  <li>成绩管理</li>
                  <li>作业管理</li>
                </ul>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main {
  padding: 20px;
}

.menu-card {
  height: 100%;
}

.content-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content {
  padding: 20px;
}

.welcome-content h2 {
  color: #333;
  margin-bottom: 10px;
}

.welcome-content p {
  color: #666;
  margin-bottom: 20px;
}

.welcome-content h3 {
  color: #333;
  margin-bottom: 15px;
}

.welcome-content ul {
  list-style: none;
  padding: 0;
}

.welcome-content li {
  padding: 8px 0;
  color: #666;
  border-bottom: 1px solid #eee;
}

.welcome-content li:last-child {
  border-bottom: none;
}
</style>