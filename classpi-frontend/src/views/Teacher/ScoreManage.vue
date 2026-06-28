<template>
  <div class="score-manage-page">
    <el-card shadow="hover" style="border-radius:12px; border:none;">
      <template #header><span class="card-title">🏆 成绩管理</span></template>
      <el-table :data="scoreList" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="学生姓名" prop="studentName" width="150" />
        <el-table-column label="学号" prop="studentNo" width="150" />
        <el-table-column label="作业名称" prop="homeworkName" />
        <el-table-column label="分数" prop="score" width="100">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'" effect="dark">
              {{ row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="教师评语" prop="comment" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getScoreList } from '@/api/homework';
import { useUserStore } from '@/store/user';

const loading = ref(false);
const scoreList = ref([]);
const userStore = useUserStore();

const loadScore = async () => {
  loading.value = true;
  try {
    const teacherId = userStore.userInfo?.id;
    console.log('=== 加载成绩列表 ===');
    console.log('teacherId:', teacherId);
    const res = await getScoreList({ teacherId, page: 1, pageSize: 10 });
    console.log('响应数据:', res);
    if (res.code === 200) {
      scoreList.value = res.data?.records || [];
    }
  } catch (error) {
    console.error('加载成绩失败:', error);
    scoreList.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(loadScore);
</script>

<style scoped>
.score-manage-page { padding: 20px 40px; }
.card-title { font-size: 18px; font-weight: 600; color: #303133; }
</style>