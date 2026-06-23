<template>
  <div class="publish-homework-page">
    <el-card shadow="hover" class="form-card">
      <template #header><span class="card-title">📝 发布新作业</span></template>
      <el-form :model="form" label-width="100px" size="large">
        <el-form-item label="作业标题" required>
          <el-input v-model="form.title" placeholder="例如：第三章课后习题" />
        </el-form-item>
        <el-form-item label="作业内容" required>
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入作业的具体要求..." />
        </el-form-item>
        <el-form-item label="截止时间" required>
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择截止日期和时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="附件">
          <UploadFile @change="handleFile" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit" class="submit-btn">
            <el-icon><Promotion /></el-icon> 发布作业
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { publishHomework } from '@/api/homework';
import UploadFile from '@/components/UploadFile.vue';
import { ElMessage } from 'element-plus';
import { Promotion } from '@element-plus/icons-vue';

const submitting = ref(false);
const form = ref({ title: '', content: '', deadline: '', files: [] });

const handleFile = (files) => form.value.files = files;

const submit = async () => {
  if (!form.value.title || !form.value.deadline) {
    ElMessage.warning('请填写完整的作业标题和截止时间');
    return;
  }
  submitting.value = true;
  try {
    const res = await publishHomework(form.value);
    if (res.code === 200) {
      ElMessage.success('作业发布成功！');
      form.value = { title: '', content: '', deadline: '', files: [] };
    } else {
      ElMessage.error(res.msg);
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.publish-homework-page { padding: 20px 40px; max-width: 900px; margin: 0 auto; }
.form-card { border-radius: 12px; border: none; }
.card-title { font-size: 18px; font-weight: 600; color: #303133; }
.submit-btn { width: 100%; height: 48px; font-size: 16px; border-radius: 8px; margin-top: 10px; background: linear-gradient(90deg, #4f46e5, #818cf8); border: none; }
</style>