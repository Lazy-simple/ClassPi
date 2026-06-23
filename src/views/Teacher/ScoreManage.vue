<template>
<el-card title="成绩管理">
  <el-table :data="scoreList" border>
    <el-table-column label="学生姓名" prop="studentName" />
    <el-table-column label="作业名称" prop="homeworkName" />
    <el-table-column label="分数" prop="score" />
    <el-table-column label="教师评语" prop="comment" />
  </el-table>
</el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getScoreList } from '@/api/homework'
const scoreList = ref([])
const loadScore = async () => {
  const res = await getScoreList({})
  if(res.code === 200) score.value = res.data
}
onMounted(loadScore)
</script>