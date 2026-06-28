<template>
  <div class="course-detail-page">
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main/student-course' }">我的课程</el-breadcrumb-item>
        <el-breadcrumb-item>{{ courseInfo.courseName || courseInfo.name || '课程详情' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="course-header">
      <div class="cover-bg" :style="{ backgroundImage: `url(${defaultCover})` }"></div>
      <div class="cover-overlay"></div>
      <div class="course-info">
        <h1 class="course-title">{{ courseInfo.courseName || courseInfo.name || '课程名称' }}</h1>
        <p class="course-subtitle">{{ courseInfo.courseNo || '课程编号' }}</p>
        <div class="course-meta">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ courseInfo.teacherName || '未知教师' }}
          </span>
          <span class="meta-item">
            <el-icon><Collection /></el-icon>
            {{ courseInfo.credit || 0 }} 学分
          </span>
        </div>
      </div>
    </div>

    <div class="course-tabs">
      <div class="tabs-container">
        <div class="tab-item" :class="{ active: activeTab === 'homework' }" @click="switchTab('homework')">
          <el-icon><DocumentChecked /></el-icon>
          <span>作业</span>
        </div>
        <div class="tab-item" :class="{ active: activeTab === 'resource' }" @click="switchTab('resource')">
          <el-icon><Document /></el-icon>
          <span>资料</span>
        </div>
        <div class="tab-item" :class="{ active: activeTab === 'topic' }" @click="switchTab('topic')">
          <el-icon><ChatDotRound /></el-icon>
          <span>讨论</span>
        </div>
      </div>
    </div>

    <div class="tab-content">
      <div v-if="activeTab === 'homework'" class="homework-section">
        <div class="section-header">
          <h3>📋 作业列表</h3>
        </div>
        <div v-if="loading" class="loading-state">
          <span>加载中...</span>
        </div>
        <div v-else-if="homeworkList.length === 0" class="empty-state">
          <el-empty description="暂无作业" />
        </div>
        <div v-else class="homework-list">
          <div class="homework-card" v-for="hw in homeworkList" :key="hw.id">
            <div class="hw-header">
              <h4 class="hw-title">{{ hw.title }}</h4>
              <el-tag :type="getHomeworkStatus(hw).type">
                {{ getHomeworkStatus(hw).text }}
              </el-tag>
            </div>
            <p class="hw-desc">{{ hw.description || '暂无描述' }}</p>
            <div class="hw-footer">
              <span class="hw-deadline">
                <el-icon><Clock /></el-icon>
                截止时间: {{ hw.deadline || '--' }}
              </span>
              <el-button
                  v-if="getHomeworkStatus(hw).text === '未提交'"
                  type="primary"
                  size="small"
                  @click="goToSubmit(hw.id)"
              >
                <el-icon><UploadFilled /></el-icon> 提交作业
              </el-button>
              <el-button
                  v-else
                  type="success"
                  size="small"
                  disabled
              >
                <el-icon><Check /></el-icon> {{ getHomeworkStatus(hw).text }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'resource'" class="resource-section">
        <div class="section-header">
          <h3>📚 课程资料</h3>
        </div>
        <div v-if="loading" class="loading-state">
          <span>加载中...</span>
        </div>
        <div v-else-if="resourceList.length === 0" class="empty-state">
          <el-empty description="暂无资料" />
        </div>
        <div v-else class="resource-list">
          <div class="resource-item" v-for="item in resourceList" :key="item.id">
            <div class="resource-icon-wrap">
              <el-icon :size="32">
                <Folder v-if="item.isFolder === 1" />
                <Document v-else />
              </el-icon>
            </div>
            <div class="resource-info">
              <h4>{{ item.folderName || item.name }}</h4>
              <p>{{ item.uploaderName || '未知上传者' }}</p>
            </div>
            <div class="resource-actions">
              <span v-if="item.fileSize" class="file-size">{{ formatFileSize(item.fileSize) }}</span>
              <el-button
                  v-if="item.isFolder !== 1 && item.type !== 'link'"
                  type="primary"
                  size="small"
                  @click="downloadResource(item)"
              >
                <el-icon><Document /></el-icon> 下载
              </el-button>
              <el-button
                  v-if="item.type === 'link'"
                  type="success"
                  size="small"
                  @click="openLink(item)"
              >
                <el-icon><Link /></el-icon> 访问
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'topic'" class="topic-section">
        <div class="section-header">
          <h3>💬 课程讨论</h3>
          <el-button type="primary" size="small" @click="topicDialogVisible = true">
            <el-icon><Plus /></el-icon> 发起讨论
          </el-button>
        </div>
        <div v-if="loading" class="loading-state">
          <span>加载中...</span>
        </div>
        <div v-else-if="topicList.length === 0" class="empty-state">
          <el-empty description="暂无讨论" />
        </div>
        <div v-else class="topic-list">
          <div class="topic-card" v-for="topic in topicList" :key="topic.id">
            <div class="topic-header">
              <h4 class="topic-title">{{ topic.title }}</h4>
              <span class="topic-author">{{ topic.authorName || '匿名' }}</span>
            </div>
            <p class="topic-content">{{ topic.content || '暂无内容' }}</p>
            <div class="topic-footer">
              <span class="topic-time">{{ topic.createTime || '--' }}</span>
              <span class="topic-replies">
                <el-icon><ChatDotRound /></el-icon> {{ topic.replyCount || 0 }} 回复
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="topicDialogVisible" title="发起讨论" width="600px">
      <el-form :model="topicForm" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="topicForm.title" placeholder="请输入讨论标题" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="topicForm.content" type="textarea" :rows="4" placeholder="请输入讨论内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="topicDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="topicSubmitting" @click="submitTopic">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Collection, DocumentChecked, Document, ChatDotRound,
  Clock, UploadFilled, Check, Link, Folder, Plus
} from '@element-plus/icons-vue'
import { getStudentCourses } from '@/api/course'
import { getHomeworkByCourse } from '@/api/homework'
import { getResourceTree, downloadResource as downloadRes } from '@/api/resource'
import { getTopicList, publishTopic } from '@/api/topic'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const courseId = ref(Number(route.params.courseId))
const activeTab = ref('homework')
const loading = ref(false)

const courseInfo = ref({})
const homeworkList = ref([])
const resourceList = ref([])
const topicList = ref([])

const topicDialogVisible = ref(false)
const topicSubmitting = ref(false)

const topicForm = ref({
  title: '',
  content: ''
})

const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=education%20course%20abstract%20banner%20modern%20design&image_size=landscape_16_9'

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'homework') loadHomework()
  else if (tab === 'resource') loadResource()
  else if (tab === 'topic') loadTopic()
}

const loadCourseInfo = async () => {
  try {
    const studentId = userStore.userInfo?.id || localStorage.getItem('userId')
    const res = await getStudentCourses(studentId)
    if (res.code === 200 && res.data) {
      const course = res.data.find(c => (c.courseId || c.id) === courseId.value)
      if (course) {
        courseInfo.value = course
      }
    }
  } catch (error) {
    console.error('加载课程信息失败:', error)
  }
}

const loadHomework = async () => {
  loading.value = true
  try {
    const res = await getHomeworkByCourse(courseId.value)
    if (res.code === 200) {
      homeworkList.value = res.data || []
    }
  } catch (error) {
    console.error('加载作业失败:', error)
  } finally {
    loading.value = false
  }
}

const loadResource = async () => {
  loading.value = true
  try {
    const res = await getResourceTree(courseId.value)
    if (res.code === 200) {
      resourceList.value = res.data || []
    }
  } catch (error) {
    console.error('加载资料失败:', error)
  } finally {
    loading.value = false
  }
}

const loadTopic = async () => {
  loading.value = true
  try {
    const res = await getTopicList(courseId.value)
    if (res.code === 200) {
      topicList.value = res.data || []
    }
  } catch (error) {
    console.error('加载讨论失败:', error)
  } finally {
    loading.value = false
  }
}

const getHomeworkStatus = (hw) => {
  if (hw.submitted) {
    return { text: '已提交', type: 'success' }
  }
  return { text: '未提交', type: 'warning' }
}

const goToSubmit = (homeworkId) => {
  localStorage.setItem('currentCourseId', courseId.value)
  localStorage.setItem('currentHomeworkId', homeworkId)
  router.push('/main/submit-homework')
}

const downloadResource = (item) => {
  if (item.type === 'link') {
    window.open(item.path)
    return
  }
  downloadRes(item.id)
}

const openLink = (item) => {
  if (item.path) {
    window.open(item.path, '_blank')
  } else {
    ElMessage.warning('链接地址无效')
  }
}

const formatFileSize = (bytes) => {
  if (!bytes) return ''
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + sizes[i]
}

const submitTopic = async () => {
  if (!topicForm.value.title || !topicForm.value.content) {
    ElMessage.warning('请填写完整内容')
    return
  }
  topicSubmitting.value = true
  try {
    const userId = userStore.userInfo?.id || localStorage.getItem('userId')
    const userName = userStore.userInfo?.username || userStore.userInfo?.name || localStorage.getItem('userName') || '学生'
    const res = await publishTopic({
      courseId: courseId.value,
      title: topicForm.value.title,
      content: topicForm.value.content,
      authorId: userId,
      authorName: userName,
      authorType: 2
    })
    if (res.code === 200) {
      ElMessage.success('发布成功')
      topicDialogVisible.value = false
      topicForm.value = { title: '', content: '' }
      loadTopic()
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败')
  } finally {
    topicSubmitting.value = false
  }
}

onMounted(() => {
  loadCourseInfo()
  loadHomework()
})
</script>

<style scoped>
.course-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.breadcrumb {
  padding: 16px 40px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
}

.course-header {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.cover-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0,0,0,0.2), rgba(0,0,0,0.6));
}

.course-info {
  position: relative;
  z-index: 1;
  padding: 30px 40px;
  color: white;
}

.course-title {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 8px 0;
}

.course-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 16px 0;
}

.course-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  opacity: 0.9;
}

.meta-item .el-icon {
  font-size: 16px;
}

.course-tabs {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 40px;
}

.tabs-container {
  display: flex;
  gap: 0;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  font-size: 15px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.tab-item:hover {
  color: #4f46e5;
  background-color: #f5f7fa;
}

.tab-item.active {
  color: #4f46e5;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #4f46e5;
}

.tab-content {
  padding: 20px 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.loading-state,
.empty-state {
  background: white;
  padding: 40px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #909399;
}

.loading-state .el-icon {
  font-size: 32px;
  color: #4f46e5;
}

.homework-list, .resource-list, .topic-list {
  display: grid;
  gap: 16px;
}

.homework-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.hw-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.hw-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.hw-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.hw-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hw-deadline {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.resource-icon-wrap {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 10px;
}

.resource-icon-wrap .el-icon {
  color: #4f46e5;
}

.resource-info {
  flex: 1;
  min-width: 0;
}

.resource-info h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-info p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.resource-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.topic-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.3s;
}

.topic-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.topic-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.topic-author {
  font-size: 13px;
  color: #909399;
}

.topic-content {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.topic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topic-time {
  font-size: 13px;
  color: #909399;
}

.topic-replies {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
</style>