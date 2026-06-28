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
import { ElMessage } from 'element-plus';

const loading = ref(false);
const scoreList = ref([]);
const userStore = useUserStore();

const loadScore = async () => {
  loading.value = true;
  try {
    const teacherId = userStore.userInfo?.id;
    const res = await getScoreList({ teacherId, page: 1, pageSize: 20 });
    if (res.code === 200) {
      const records = res.data?.records || [];
      // 后端返回的数据直接映射到表格
      scoreList.value = records.map(item => ({
        studentName: item.studentName || '未知学生',
        studentNo: item.studentUsername || '--',  // 如果后端有学号字段就用
        homeworkName: item.homeworkTitle || '未命名作业',
        score: item.score || 0,
        comment: item.correctionContent || ''
      }));
    } else {
      ElMessage.error(res.msg || '加载成绩失败');
    }
  } catch (error) {
    console.error('加载成绩失败:', error);
    ElMessage.error('网络异常，请稍后重试');
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