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
            <span class="value highlight">{{ assignmentData?.courseName || '请先选择课程' }}</span>
          </div>
          <div class="info-item">
            <span class="label">作业标题：</span>
            <span class="value">{{ assignmentData?.title || '请先选择作业' }}</span>
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
          <!-- 选择课程 -->
          <el-form-item label="选择课程" required>
            <el-select
                v-model="form.courseId"
                placeholder="请选择课程"
                style="width:100%"
                @change="onCourseChange"
            >
              <el-option
                  v-for="course in courseList"
                  :key="course.id"
                  :label="course.courseName || course.name"
                  :value="course.courseId || course.id"
              />
            </el-select>
          </el-form-item>

          <!-- 选择作业 -->
          <el-form-item label="选择作业" required>
            <el-select
                v-model="form.homeworkId"
                placeholder="请先选择课程"
                style="width:100%"
                @change="onHomeworkChange"
            >
              <el-option
                  v-for="hw in homeworkList"
                  :key="hw.id"
                  :label="hw.title"
                  :value="hw.id"
              />
            </el-select>
          </el-form-item>

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
                :disabled="!hasFile || !form.courseId || !form.homeworkId"
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { submitHomework, getHomeworkByCourse } from '@/api/homework'
import { getStudentCourses } from '@/api/course'
import UploadFile from '@/components/UploadFile.vue'
import { ElMessage } from 'element-plus'
import { DocumentChecked, UploadFilled, Warning } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()
const submitting = ref(false)
const isSubmitted = ref(false)
const files = ref([])
const uploadComponentRef = ref(null)

// 课程列表
const courseList = ref([])
// 作业列表（根据选中的课程）
const homeworkList = ref([])

// 作业数据
const assignmentData = ref({
  id: '',
  courseName: '',
  title: '',
  deadline: ''
})

const form = ref({
  remark: '',
  courseId: '',
  homeworkId: '',
  studentId: localStorage.getItem('userId') || '1',
  studentName: localStorage.getItem('userName') || '学生',
  fileName: '',
  fileUrl: '',
  fileType: '',
  fileSize: 0,
  file: null
})

// 判断是否有文件
const hasFile = computed(() => {
  return files.value && files.value.length > 0
})

// 加载学生已选课程
const loadCourses = async () => {
  try {
    const studentId = userStore.userInfo?.id || localStorage.getItem('userId')
    const res = await getStudentCourses(studentId)
    if (res.code === 200) {
      courseList.value = res.data || []
      if (courseList.value.length === 0) {
        ElMessage.warning('你还没有选修任何课程，请先去选课')
      }
    }
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  }
}

// 选择课程后加载对应的作业
const onCourseChange = async (courseId) => {
  form.value.homeworkId = ''
  assignmentData.value = { id: '', courseName: '', title: '', deadline: '' }

  // 找到课程名
  const course = courseList.value.find(c => (c.courseId || c.id) === courseId)
  if (course) {
    assignmentData.value.courseName = course.courseName || course.name
  }

  try {
    const res = await getHomeworkByCourse(courseId)
    if (res.code === 200) {
      homeworkList.value = res.data || []
      if (homeworkList.value.length === 0) {
        ElMessage.info('该课程暂无作业')
      }
    }
  } catch (error) {
    console.error('加载作业失败:', error)
    ElMessage.error('加载作业失败')
  }
}

// 选择作业后更新显示
const onHomeworkChange = (homeworkId) => {
  const hw = homeworkList.value.find(h => h.id === homeworkId)
  if (hw) {
    assignmentData.value = {
      id: hw.id,
      courseName: assignmentData.value.courseName,
      title: hw.title,
      deadline: hw.deadline || '--'
    }
    form.value.homeworkId = homeworkId
  }
}

// 监听文件变化
const fileChange = (fileList) => {
  console.log('文件变化:', fileList)
  files.value = fileList || []

  if (files.value.length > 0) {
    const file = files.value[0]
    form.value.fileName = file.name || ''
    form.value.fileUrl = file.url || ''
    form.value.fileType = file.type || ''
    form.value.fileSize = file.size || 0
    if (file.raw) {
      form.value.file = file.raw
    }
    console.log('form.value.fileUrl:', form.value.fileUrl)
  } else {
    form.value.fileName = ''
    form.value.fileUrl = ''
    form.value.fileType = ''
    form.value.fileSize = 0
    form.value.file = null
  }
}

// 提交作业
const submitHomeworkHandler = async () => {
  if (!hasFile.value) {
    ElMessage.warning('请先上传作业附件')
    return
  }

  submitting.value = true
  try {
    const submitData = {
      homeworkId: form.value.homeworkId,
      submitContent: form.value.remark || '',
      fileUrl: form.value.fileUrl || '',
      fileName: form.value.fileName || '',
      fileType: form.value.fileType || ''
    }

    console.log('提交数据:', submitData)

    const res = await submitHomework(submitData)
    if (res.code === 200) {
      ElMessage.success('作业提交成功！')
      isSubmitted.value = true
      files.value = []
      form.value.remark = ''
      form.value.fileUrl = ''
      form.value.fileName = ''
      form.value.fileType = ''
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

onMounted(() => {
  loadCourses()
})
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
