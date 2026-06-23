<template>
  <div class="homework-submit-page">
    <div class="submit-container">
      <!-- 左侧/顶部：作业信息概览 -->
      <div class="assignment-info-card">
        <div class="info-header">
          <el-icon size="24" color="#4f46e5"><DocumentChecked /></el-icon>
          <h3>当前作业任务</h3>
        </div>
        <div class="info-body">
          <div class="info-item">
            <span class="label">所属课程：</span>
            <span class="value highlight">{{ assignmentData?.courseName || '加载中...' }}</span>
          </div>
          <div class="info-item">
            <span class="label">作业标题：</span>
            <span class="value">{{ assignmentData?.title || '请选择要提交的作业' }}</span>
          </div>
          <div class="info-item">
            <span class="label">截止时间：</span>
            <span class="value deadline">{{ assignmentData?.deadline || '--' }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧/底部：提交表单 -->
      <el-card shadow="hover" class="form-card">
        <template #header>
          <div class="card-header">
            <span>📤 提交作业</span>
            <el-tag v-if="isSubmitted" type="success" effect="dark">已提交</el-tag>
          </div>
        </template>

        <el-form :model="form" label-position="top" size="large">
          <!-- 附件上传区域 -->
          <el-form-item label="上传附件">
            <div class="upload-area">
              <UploadFile @change="fileChange" />
              <p class="upload-tip">支持 PDF, Word, Zip 格式，文件大小不超过 50MB</p>
            </div>
          </el-form-item>

          <!-- 备注输入 -->
          <el-form-item label="留言/备注 (选填)">
            <el-input
              v-model="form.remark"
              type="textarea"
              :rows="4"
              placeholder="如果有特殊情况需要说明，请在这里留言..."
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              :disabled="files.length === 0"
              @click="submitHomeworkHandler"
              class="submit-btn"
            >
              <el-icon style="margin-right: 5px;"><UploadFilled /></el-icon>
              {{ isSubmitted ? '重新提交' : '确认提交' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { submitHomework, getAssignmentDetail } from '@/api/homework'; // 假设你有获取详情的接口
import UploadFile from '@/components/UploadFile.vue';
import { ElMessage } from 'element-plus';
import { DocumentChecked, UploadFilled } from '@element-plus/icons-vue';

const route = useRoute();
const submitting = ref(false);
const isSubmitted = ref(false); // 模拟是否已提交状态
const files = ref([]);

// 作业数据（实际开发中应从路由参数或API获取）
const assignmentData = ref({
  id: route.query.id || 1,
  courseName: 'Web前端开发技术',
  title: '第三次平时作业：Vue组件化开发实践',
  deadline: '2023-12-31 23:59'
});

const form = ref({
  remark: '',
  assignmentId: assignmentData.value.id,
  fileUrl: '' // 用于存储上传后的文件地址
});

// 监听文件变化
const fileChange = (list) => {
  files.value = list;
  if (list.length > 0) {
    form.value.fileUrl = list[0].url; // 假设你的 UploadFile 组件返回的格式包含 url
  }
};

// 提交作业
const submitHomeworkHandler = async () => {
  if (files.value.length === 0) {
    ElMessage.warning('请先上传作业附件');
    return;
  }

  submitting.value = true;
  try {
    const res = await submitHomework(form.value);
    if (res.code === 200) {
      ElMessage.success('作业提交成功！');
      isSubmitted.value = true;
    } else {
      ElMessage.error(res.msg || '提交失败');
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

// 页面加载时获取作业详情
onMounted(async () => {
  // 实际开发中取消注释
  // const res = await getAssignmentDetail(assignmentData.value.id);
  // if (res.code === 200) assignmentData.value = res.data;
});
</script>

<style scoped>
.homework-submit-page {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.submit-container {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 作业信息卡片 */
.assignment-info-card {
  background: #fff;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-left: 5px solid #4f46e5;
}
.info-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.info-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}
.info-body {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-item .label {
  font-size: 13px;
  color: #909399;
}
.info-item .value {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}
.info-item .highlight {
  color: #4f46e5;
}
.info-item .deadline {
  color: #e6a23c;
}

/* 表单卡片 */
.form-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.upload-area {
  width: 100%;
}
.upload-tip {
  margin: 8px 0 0;
  font-size: 12px;
  color: #909399;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(90deg, #4f46e5, #818cf8);
  border: none;
  margin-top: 10px;
}
.submit-btn:hover {
  opacity: 0.9;
}
</style
