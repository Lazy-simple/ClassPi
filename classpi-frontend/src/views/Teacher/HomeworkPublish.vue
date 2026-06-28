<template>
  <div class="publish-homework-page">
    <el-card shadow="hover" class="form-card">
      <template #header><span class="card-title">📝 发布新作业</span></template>
      <el-form :model="form" label-width="100px" size="large">

        <!-- ========== 新增：选择课程 ========== -->
        <el-form-item label="所属课程" required>
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width:100%">
            <el-option
                v-for="course in courseList"
                :key="course.id"
                :label="course.name"
                :value="course.id"
            />
          </el-select>
        </el-form-item>

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
import { ref, onMounted } from 'vue';
import { publishHomework } from '@/api/homework';
import { getTeacherCourses } from '@/api/course';
import UploadFile from '@/components/UploadFile.vue';
import { ElMessage } from 'element-plus';
import { Promotion } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';

const userStore = useUserStore();
const submitting = ref(false);
const courseList = ref([]);

const form = ref({
  courseId: '',
  title: '',
  content: '',
  deadline: '',
  files: []
});

const loadCourses = async () => {
  try {
    const res = await getTeacherCourses(userStore.userInfo?.id);
    if (res.code === 200) {
      courseList.value = res.data || [];
    }
  } catch (error) {
    console.error('加载课程失败:', error);
  }
};

const handleFile = (files) => form.value.files = files;

const submit = async () => {
  if (!form.value.courseId) {
    ElMessage.warning('请选择所属课程');
    return;
  }
  if (!form.value.title || !form.value.deadline) {
    ElMessage.warning('请填写完整的作业标题和截止时间');
    return;
  }

  submitting.value = true;
  try {
    // 组装提交数据
    const submitData = {
      courseId: form.value.courseId,
      title: form.value.title,
      content: form.value.content,
      deadline: form.value.deadline,
      fileUrl: form.value.files?.[0]?.url || '',
      fileName: form.value.files?.[0]?.name || '',
      fileType: form.value.files?.[0]?.type || ''
    };

    const res = await publishHomework(submitData);
    if (res.code === 200) {
      ElMessage.success('作业发布成功！');
      form.value = { courseId: '', title: '', content: '', deadline: '', files: [] };
    } else {
      ElMessage.error(res.msg || res.message || '发布失败');
    }
  } catch (error) {
    console.error('发布失败:', error);
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  loadCourses();
});
</script>

<style scoped>
.publish-homework-page { padding: 20px 40px; max-width: 900px; margin: 0 auto; }
.form-card { border-radius: 12px; border: none; }
.card-title { font-size: 18px; font-weight: 600; color: #303133; }
.submit-btn { width: 100%; height: 48px; font-size: 16px; border-radius: 8px; margin-top: 10px; background: linear-gradient(90deg, #4f46e5, #818cf8); border: none; }
</style>