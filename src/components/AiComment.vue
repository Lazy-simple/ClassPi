<template>
  <div class="ai-eval-page">
    <el-card shadow="hover" style="border-radius:12px; border:none;">
      <template #header><span class="card-title">🤖 AI 智能作业评价</span></template>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="学生姓名" prop="studentName" width="150" />
        <el-table-column label="作业文件" prop="fileName">
          <template #default="{ row }">
            <el-link type="primary" :underline="false">{{ row.fileName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" prop="submitTime" width="180" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="aiEval(row)" :loading="row.evalLoading">
              ✨ AI 评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- AI 评语弹窗 -->
    <el-dialog v-model="dialogVisible" title="AI 智能评语" width="600px">
      <div class="ai-content-box">
        <AiComment :ai-content="aiText" />
      </div>
      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getHomeworkList, aiEvaluate } from '@/api/ai'; // 假设获取列表的接口在这里
import AiComment from '@/components/AiComment.vue';
import { ElMessage } from 'element-plus';

const loading = ref(false);
const tableData = ref([]);
const dialogVisible = ref(false);
const aiText = ref('');

const loadList = async () => {
  loading.value = true;
  try {
    const res = await getHomeworkList(); // 需根据实际API调整
    if (res.code === 200) {
      tableData.value = res.data.map(item => ({ ...item, evalLoading: false }));
    }
  } finally {
    loading.value = false;
  }
};

const aiEval = async (row) => {
  row.evalLoading = true;
  try {
    const res = await aiEvaluate({
      content: row.content || row.fileName,
      prompt: '请从完成度、格式规范、内容深度三个方面给出客观评价和修改建议'
    });
    if (res.code === 200) {
      aiText.value = res.data;
      dialogVisible.value = true;
    }
  } catch (e) {
    ElMessage.error('AI 评价请求失败');
  } finally {
    row.evalLoading = false;
  }
};

onMounted(loadList);
</script>

<style scoped>
.ai-eval-page { padding: 20px 40px; }
.card-title { font-size: 18px; font-weight: 600; color: #303133; }
.ai-content-box { max-height: 400px; overflow-y: auto; padding: 10px; background: #f9fafb; border-radius: 8px; }
</style>