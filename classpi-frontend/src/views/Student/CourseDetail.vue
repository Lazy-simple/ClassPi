<template>
  <div class="course-detail-page">
    <div class="course-header">
      <div class="cover-bg" :style="{ backgroundImage: `url(${courseInfo.coverUrl || defaultCover})` }"></div>
      <div class="cover-overlay"></div>
      <div class="course-info">
        <h1 class="course-title">{{ courseInfo.name || '课程名称' }}</h1>
        <p class="course-subtitle">{{ courseInfo.className || '班级名称' }}</p>
        <div class="course-meta">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ courseInfo.teacherName || '未知教师' }}
          </span>
          <span class="meta-item">
            <el-icon><Users /></el-icon>
            {{ courseInfo.studentCount || 0 }}人已加入
          </span>
          <span class="meta-item">
            <el-icon><CreditCard /></el-icon>
            加课码: {{ courseInfo.courseNo || '--' }}
          </span>
        </div>
      </div>
    </div>

    <div class="course-tabs">
      <div class="tabs-container">
        <div class="tab-item" :class="{ active: activeTab === 'homework' }" @click="switchTab('homework')">
          <el-icon><DocumentChecked /></el-icon>
          <span>作业</span>
          <el-badge v-if="pendingHomeworkCount > 0" :value="pendingHomeworkCount" class="badge" />
        </div>
        <div class="tab-item" :class="{ active: activeTab === 'resource' }" @click="switchTab('resource')">
          <el-icon><Document /></el-icon>
          <span>资料</span>
        </div>
        <div class="tab-item" :class="{ active: activeTab === 'topic' }" @click="switchTab('topic')">
          <el-icon><ChatDotRound /></el-icon>
          <span>讨论</span>
        </div>
        <div class="tab-item" :class="{ active: activeTab === 'score' }" @click="switchTab('score')">
          <el-icon><TrendCharts /></el-icon>
          <span>成绩</span>
        </div>
      </div>
    </div>

    <div class="tab-content">
      <div v-if="activeTab === 'homework'" class="homework-section">
        <div class="section-header">
          <h3>📋 作业列表</h3>
        </div>
        <div v-if="homeworkList.length === 0" class="empty-state">
          <el-empty description="暂无作业" />
        </div>
        <div v-else class="homework-list">
          <div class="homework-card" v-for="hw in homeworkList" :key="hw.id">
            <div class="hw-header">
              <h4 class="hw-title">{{ hw.title }}</h4>
              <el-tag :type="getHomeworkStatus(hw).type" :effect="getHomeworkStatus(hw).effect">
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
                  v-else-if="getHomeworkStatus(hw).text === '已提交'"
                  type="success"
                  size="small"
                  disabled
              >
                <el-icon><Check /></el-icon> 已提交
              </el-button>
              <el-button
                  v-else-if="getHomeworkStatus(hw).text === '已批改'"
                  type="info"
                  size="small"
                  @click="viewScore(hw)"
              >
                <el-icon><Eye /></el-icon> 查看成绩
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'resource'" class="resource-section">
        <div class="section-header">
          <h3>📚 课程资料</h3>
        </div>
        <div v-if="resourceList.length === 0" class="empty-state">
          <el-empty description="暂无资料" />
        </div>
        <div v-else class="resource-list">
          <div class="resource-item" v-for="item in resourceList" :key="item.id">
            <div class="resource-icon-wrap">
              <el-icon :size="32">
                <Folder v-if="item.isFolder === 1" />
                <Document v-else-if="item.type === 'file'" />
                <Link v-else-if="item.type === 'link'" />
                <DocumentCopy v-else />
              </el-icon>
            </div>
            <div class="resource-info">
              <h4>{{ item.folderName || item.name }}</h4>
              <p>{{ item.uploaderName || '未知上传者' }} · {{ item.createTime || '--' }}</p>
            </div>
            <div class="resource-actions">
              <span v-if="item.fileSize" class="file-size">{{ formatFileSize(item.fileSize) }}</span>
              <el-button
                  v-if="item.isFolder !== 1 && item.type !== 'link'"
                  type="primary"
                  size="small"
                  @click="downloadResource(item)"
              >
                <el-icon><Download /></el-icon> 下载
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
          <el-button type="primary" size="small" @click="openCreateTopicDialog">
            <el-icon><Plus /></el-icon> 发起讨论
          </el-button>
        </div>
        <div v-if="topicList.length === 0" class="empty-state">
          <el-empty description="暂无讨论" />
        </div>
        <div v-else class="topic-list">
          <div class="topic-card" v-for="topic in topicList" :key="topic.id" @click="openTopicDetail(topic)">
            <div class="topic-header">
              <h4 class="topic-title">{{ topic.title }}</h4>
              <span class="topic-author">{{ topic.authorName || '匿名' }}</span>
            </div>
            <p class="topic-content">{{ topic.content || '暂无内容' }}</p>
            <div class="topic-footer">
              <span class="topic-time">{{ topic.createTime || '--' }}</span>
              <span class="topic-replies">
                <el-icon><ChatDotSquare /></el-icon> {{ topic.replyCount || 0 }} 回复
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'score'" class="score-section">
        <div class="section-header">
          <h3>📊 我的成绩</h3>
        </div>
        <div v-if="scoreList.length === 0" class="empty-state">
          <el-empty description="暂无成绩记录" />
        </div>
        <div v-else class="score-list">
          <div class="score-card" v-for="score in scoreList" :key="score.id">
            <div class="score-info">
              <h4>{{ score.homeworkTitle || '作业' }}</h4>
              <p>{{ score.submitTime || '--' }}提交</p>
            </div>
            <div class="score-value">
              <span :class="{ 'high': score.score >= 80, 'medium': score.score >= 60 && score.score < 80, 'low': score.score < 60 }">
                {{ score.score || '--' }}
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

    <el-dialog v-model="topicDetailVisible" title="讨论详情" width="700px">
      <div v-if="currentTopic" class="topic-detail">
        <h3>{{ currentTopic.title }}</h3>
        <p class="topic-meta">{{ currentTopic.authorName || '匿名' }} · {{ currentTopic.createTime || '--' }}</p>
        <div class="topic-body">{{ currentTopic.content }}</div>
        <div class="reply-section">
          <h4>回复 ({{ currentTopic.replies?.length || 0 }})</h4>
          <div v-if="currentTopic.replies?.length === 0" class="no-replies">暂无回复</div>
          <div v-else class="reply-list">
            <div class="reply-item" v-for="reply in currentTopic.replies" :key="reply.id">
              <span class="reply-author">{{ reply.authorName || '匿名' }}</span>
              <span class="reply-content">{{ reply.content }}</span>
              <span class="reply-time">{{ reply.createTime || '--' }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-input v-model="replyContent" placeholder="输入回复内容" @keyup.enter="submitReply" />
        <el-button type="primary" @click="submitReply">回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Users, CreditCard, DocumentChecked, Document, ChatDotRound,
  TrendCharts, Clock, UploadFilled, Check, Eye, Download, Link,
  Folder, DocumentCopy, Plus, ChatDotSquare
} from '@element-plus/icons-vue'
import { getCourseById, getStudentCourses } from '@/api/course'
import { getHomeworkByCourse } from '@/api/homework'
import { getResourceTree } from '@/api/resource'
import { getTopicList, createTopic, getTopicDetail, createTopicReply } from '@/api/topic'
import { getStudentScores } from '@/api/statistics'

const route = useRoute()
const router = useRouter()
const courseId = ref(Number(route.params.courseId))
const activeTab = ref('homework')

const courseInfo = ref({})
const homeworkList = ref([])
const resourceList = ref([])
const topicList = ref([])
const scoreList = ref([])

const topicDialogVisible = ref(false)
const topicDetailVisible = ref(false)
const topicSubmitting = ref(false)
const currentTopic = ref(null)
const replyContent = ref('')

const topicForm = ref({
  title: '',
  content: ''
})

const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=education%20course%20abstract%20banner%20modern%20design&image_size=landscape_16_9'

const pendingHomeworkCount = computed(() => {
  return homeworkList.value.filter(hw => !hw.submitted).length
})

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'homework') loadHomework()
  else if (tab === 'resource') loadResource()
  else if (tab === 'topic') loadTopic()
  else if (tab === 'score') loadScore()
}

const loadCourseInfo = async () => {
  try {
    const res = await getCourseById(courseId.value)
    if (res.code === 200) {
      courseInfo.value = res.data || {}
    }
  } catch (error) {
    console.error('加载课程信息失败:', error)
  }
}

const loadHomework = async () => {
  try {
    const res = await getHomeworkByCourse(courseId.value)
    if (res.code === 200) {
      homeworkList.value = res.data || []
    }
  } catch (error) {
    console.error('加载作业失败:', error)
  }
}

const loadResource = async () => {
  try {
    const res = await getResourceTree(courseId.value)
    if (res.code === 200) {
      resourceList.value = res.data || []
    }
  } catch (error) {
    console.error('加载资料失败:', error)
  }
}

const loadTopic = async () => {
  try {
    const res = await getTopicList(courseId.value)
    if (res.code === 200) {
      topicList.value = res.data || []
    }
  } catch (error) {
    console.error('加载讨论失败:', error)
  }
}

const loadScore = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await getStudentScores(userId)
    if (res.code === 200) {
      scoreList.value = res.data || []
    }
  } catch (error) {
    console.error('加载成绩失败:', error)
  }
}

const getHomeworkStatus = (hw) => {
  if (hw.submitted && hw.score !== undefined && hw.score !== null) {
    return { text: '已批改', type: 'success', effect: 'dark' }
  } else if (hw.submitted) {
    return { text: '已提交', type: 'info', effect: 'dark' }
  }
  return { text: '未提交', type: 'warning', effect: '' }
}

const goToSubmit = (homeworkId) => {
  localStorage.setItem('currentCourseId', courseId.value)
  localStorage.setItem('currentHomeworkId', homeworkId)
  router.push('/main/submit-homework')
}

const viewScore = (hw) => {
  ElMessage.info(`作业: ${hw.title}，成绩: ${hw.score || '--'}`)
}

const downloadResource = (item) => {
  if (item.type === 'link') {
    window.open(item.path)
    return
  }
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

const openCreateTopicDialog = () => {
  topicForm.value = { title: '', content: '' }
  topicDialogVisible.value = true
}

const submitTopic = async () => {
  if (!topicForm.value.title || !topicForm.value.content) {
    ElMessage.warning('请填写完整内容')
    return
  }
  topicSubmitting.value = true
  try {
    const res = await createTopic({
      courseId: courseId.value,
      title: topicForm.value.title,
      content: topicForm.value.content
    })
    if (res.code === 200) {
      ElMessage.success('发布成功')
      topicDialogVisible.value = false
      loadTopic()
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (error) {
    ElMessage.error('发布失败')
  } finally {
    topicSubmitting.value = false
  }
}

const openTopicDetail = async (topic) => {
  try {
    const res = await getTopicDetail(topic.id)
    if (res.code === 200) {
      currentTopic.value = res.data
    }
  } catch (error) {
    currentTopic.value = topic
  }
  topicDetailVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    const res = await createTopicReply({
      topicId: currentTopic.value.id,
      content: replyContent.value
    })
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyContent.value = ''
      openTopicDetail(currentTopic.value)
    } else {
      ElMessage.error(res.msg || '回复失败')
    }
  } catch (error) {
    ElMessage.error('回复失败')
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

.course-header {
  position: relative;
  height: 280px;
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
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.6));
}

.course-info {
  position: relative;
  z-index: 1;
  padding: 40px;
  color: white;
}

.course-title {
  font-size: 32px;
  font-weight: bold;
  margin: 0 0 10px 0;
}

.course-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 20px 0;
}

.course-meta {
  display: flex;
  gap: 30px;
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
  padding: 16px 24px;
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

.badge {
  margin-left: 4px;
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

.empty-state {
  background: white;
  padding: 40px;
  border-radius: 12px;
}

.homework-list, .resource-list, .topic-list, .score-list {
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

.score-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.score-info h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #303133;
}

.score-info p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.score-value span {
  font-size: 28px;
  font-weight: bold;
}

.score-value .high {
  color: #67c23a;
}

.score-value .medium {
  color: #e6a23c;
}

.score-value .low {
  color: #f56c6c;
}

.topic-detail {
  padding: 10px 0;
}

.topic-detail h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
}

.topic-meta {
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
}

.topic-body {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.reply-section h4 {
  margin: 0 0 12px 0;
  font-size: 15px;
}

.no-replies {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.reply-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.reply-author {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.reply-content {
  font-size: 14px;
  color: #606266;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}
</style>