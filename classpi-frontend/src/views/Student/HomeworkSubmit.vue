<template>
  <div class="homework-submit-page">
    <div class="submit-container">
      <!-- 作业信息卡片 -->
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

      <!-- 提交表单 -->
      <el-card shadow="hover" class="form-card">
        <template #header>
          <div class="card-header">
            <span>📤 提交作业</span>
            <el-tag v-if="isSubmitted" type="success" effect="dark">已提交</el-tag>
          </div>
        </template>

        <el-form :model="form" label-position="top" size="large">
          <!-- 附件上传 -->
          <el-form-item label="上传附件" required>
            <div class="upload-area">
              <UploadFile @change="fileChange" ref="uploadComponentRef" />
            </div>
          </el-form-item>

          <!-- 备注 -->
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
                :disabled="!hasFile"
                @click="submitHomeworkHandler"
                class="submit-btn"
            >
              <el-icon style="margin-right: 5px;"><UploadFilled /></el-icon>
              {{ isSubmitted ? '重新提交' : '确认提交' }}
            </el-button>
            <div v-if="!hasFile" class="tip-text">
              <el-icon><Warning /></el-icon> 请先上传作业文件
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { submitHomework, getAssignmentDetail } from '@/api/homework'
import UploadFile from '@/components/UploadFile.vue'
import { ElMessage } from 'element-plus'
import { DocumentChecked, UploadFilled, Warning } from '@element-plus/icons-vue'

const route = useRoute()
const submitting = ref(false)
const isSubmitted = ref(false)
const files = ref([])
const uploadComponentRef = ref(null)

// 作业数据
const assignmentData = ref({
  id: route.query.id || 1,
  courseName: route.query.courseName || 'Web前端开发技术',
  title: route.query.title || '第三次平时作业：Vue组件化开发实践',
  deadline: route.query.deadline || '2026-07-01 23:59'
})

const form = ref({
  remark: '',
  homeworkId: Number(route.query.id) || 1,
  studentId: localStorage.getItem('userId') || '1',
  studentName: localStorage.getItem('userName') || '学生'
})

// 判断是否有文件
const hasFile = computed(() => {
  return files.value && files.value.length > 0
})

// 监听文件变化
const fileChange = (fileList) => {
  console.log('文件变化:', fileList)
  files.value = fileList || []

  if (files.value.length > 0) {
    const file = files.value[0]
    form.value.fileName = file.name
    form.value.fileSize = file.size
    // 如果是自定义上传，保存文件对象
    if (file.raw) {
      form.value.file = file.raw
    }
  } else {
    form.value.fileName = ''
    form.value.fileSize = 0
    form.value.file = null
  }
}

// 提交作业
// 提交作业
const submitHomeworkHandler = async () => {
  if (!hasFile.value) {
    ElMessage.warning('请先上传作业附件')
    return
  }

  submitting.value = true
  try {
    const file = files.value[0]

    const submitData = {
      homeworkId: form.value.homeworkId,
      studentId: form.value.studentId,
      studentName: form.value.studentName,
      fileName: file?.name || '',
      fileSize: file?.size || 0,
      submitContent: form.value.remark || ''
    }

    console.log('提交数据:', submitData)

    const res = await submitHomework(submitData)
    if (res.code === 200) {
      ElMessage.success('作业提交成功！')
      isSubmitted.value = true

      // 简单处理：清空文件列表和重置状态
      files.value = []
      form.value.remark = ''

      // 重新加载页面数据
      // 或者直接刷新页面
      // window.location.reload()
    } else {
      ElMessage.error(res.msg || '提交失败，请重试')
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.response?.data?.message?.includes('重复提交')) {
      ElMessage.warning('您已提交过该作业')
    } else {
      ElMessage.error('网络异常，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}
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

.tip-text {
  color: #e6a23c;
  font-size: 13px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
