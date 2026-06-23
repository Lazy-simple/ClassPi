<template>
  <div class="homework-container">
    <!-- 页面头部：标题 + 搜索框 -->
    <div class="page-header">
      <h2 class="page-title">作业管理</h2>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索作业名称或课程..."
        style="width: 300px;"
        clearable
        prefix-icon="Search"
      />
    </div>

    <!-- 作业列表表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="filteredHomeworkList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="作业名称" min-width="180" />
        <el-table-column prop="courseName" label="所属课程" min-width="150" />
        <el-table-column prop="submitInfo" label="提交人数" width="120" align="center" />

        <!-- 状态列：使用不同颜色的 Tag -->
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === '已批改' ? 'success' : row.status === '待批改' ? 'warning' : 'info'"
              effect="light"
              round
            >
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleGrade(row)">
              <el-icon><Edit /></el-icon> 批改
            </el-button>
            <el-button size="small" type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon> 详情
            </el-button>
          </template>
        </el-table-column>

        <!-- 空状态提示 -->
        <template #empty>
          <el-empty description="暂无作业数据" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore'

const router = useRouter()
const userStore = useUserStore()

// 搜索关键字
const searchKeyword = ref('')
// 模拟加载状态
const loading = ref(false)

// 从 Store 中获取作业列表
const homeworkList = computed(() => userStore.homeworkList || [])

// 根据搜索关键字过滤列表
const filteredHomeworkList = computed(() => {
  if (!searchKeyword.value) return homeworkList.value
  const keyword = searchKeyword.value.toLowerCase()
  return homeworkList.value.filter(item =>
    item.title.toLowerCase().includes(keyword) ||
    item.courseName.toLowerCase().includes(keyword)
  )
})

// 批改作业
const handleGrade = (row) => {
  router.push(`/homework/grade/${row.id}`)
}

// 查看详情
const handleView = (row) => {
  console.log('查看作业详情:', row)
  // router.push(`/homework/detail/${row.id}`)
}
</script>

<style scoped>
.homework-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.table-card {
  border-radius: 8px;
}
</style>