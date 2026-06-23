<template>
  <div class="course-container">
    <!-- 头部区域 -->
    <div class="page-header">
      <div class="header-title">
        <h2>我的课程</h2>
        <p class="subtitle">拖动卡片可自定义课程顺序</p>
      </div>
      <el-button type="primary" round @click="handleCreate">
        <el-icon style="margin-right: 5px;"><Plus /></el-icon> 加入新课程
      </el-button>
    </div>

    <!-- 核心区域：拖拽列表 -->
    <!-- v-model="courses" 双向绑定数据 -->
    <!-- item-key="id" 唯一标识 -->
    <draggable
      v-model="courses"
      item-key="id"
      class="course-grid"
      ghost-class="ghost-card"
      :animation="300"
    >
      <template #item="{ element }">
        <!-- 玻璃拟态卡片 -->
        <div class="glass-card" @click="goToCourse(element.id)">
          <div class="card-cover">
            <!-- 这里的图片地址如果是本地资源，记得用 import 或者放在 public 文件夹 -->
            <img :src="element.coverImage || defaultCover" alt="cover" />
            <div class="overlay"></div>
          </div>

          <div class="card-content">
            <div class="tag">{{ element.semester }}</div>
            <h3>{{ element.name }}</h3>
            <div class="actions">
              <el-button text size="small">进入学习</el-button>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </template>
    </draggable>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore'
import draggable from 'vuedraggable' // 引入拖拽组件
import { Plus, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 使用计算属性获取 store 中的课程，并支持修改（为了拖拽生效）
const courses = computed({
  get: () => userStore.courses,
  set: (val) => {
    // 这里通常需要 dispatch 一个 action 来更新 store，
    // 简单演示可以直接赋值，实际项目中建议通过 mutation/action 更新
    userStore.courses = val
  }
})

// 默认封面图（如果没有图片时显示）
const defaultCover = 'https://via.placeholder.com/300x150/409EFF/ffffff?text=Course'

const handleCreate = () => {
  router.push('/course/create')
}

const goToCourse = (id) => {
  console.log('进入课程 ID:', id)
  // router.push(`/course/detail/${id}`)
}
</script>

<style scoped>
.course-container {
  padding: 20px;
  background-color: #f5f7fa; /* 浅灰背景更能衬托玻璃效果 */
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-title h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 5px 0 0;
  font-size: 13px;
  color: #909399;
}

/* 网格布局 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

/* --- 核心：玻璃拟态卡片样式 --- */
.glass-card {
  background: rgba(255, 255, 255, 0.7); /* 半透明白 */
  backdrop-filter: blur(10px); /* 毛玻璃模糊效果 */
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.glass-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(64, 158, 255, 0.15);
  border-color: #409eff;
}

.card-cover {
  height: 140px;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.glass-card:hover .card-cover img {
  transform: scale(1.1);
}

.card-content {
  padding: 16px;
}

.tag {
  display: inline-block;
  background: #ecf5ff;
  color: #409eff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-bottom: 8px;
}

.card-content h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #303133;
  line-height: 1.4;
}

.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #409eff;
  font-weight: 500;
}

/* 拖拽时的幽灵样式 */
.ghost-card {
  opacity: 0.5;
  background: #c8ebfb;
  border: 2px dashed #409eff;
}
</style>