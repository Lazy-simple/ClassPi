<template>
  <div class="course-detail-page">
    <div class="course-header-card">
      <div class="header-content">
        <div class="back-link" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回我的课程</span>
        </div>
        <h1 class="course-title">{{ courseInfo.courseName || courseInfo.name || '课程名称' }}</h1>
        <p class="sub-info">
          课程号：{{ courseInfo.courseNo || '课程编号' }} | 教师：{{ courseInfo.teacherName || '未知教师' }} | 学分：{{ courseInfo.credit || 0 }}
        </p>
      </div>
      <div class="header-bg"></div>
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

              <!-- ✅ 用 v-if/v-else-if 而不是多个 v-else -->
              <template v-if="getHomeworkStatus(hw).text === '未提交'">
                <el-button
                    type="primary"
                    size="small"
                    @click="goToSubmit(hw.id)"
                >
                  <el-icon><UploadFilled /></el-icon> 提交作业
                </el-button>
              </template>
              <template v-else>
                <el-button
                    type="warning"
                    size="small"
                    @click="goToSubmit(hw.id)"
                >
                  <el-icon><RefreshRight /></el-icon> 重新提交
                </el-button>
              </template>
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
          <el-button type="primary" size="small" @click="showPublishDialog = true">
            <el-icon><Plus /></el-icon> 发起讨论
          </el-button>
        </div>

        <div v-if="selectedTopic" class="topic-detail">
          <div class="topic-detail-header" @click="selectedTopic = null">
            <el-button text type="primary">
              <el-icon><ArrowLeft /></el-icon> 返回话题列表
            </el-button>
          </div>
          <div class="topic-detail-card">
            <h3 class="topic-detail-title">{{ selectedTopic.title }}</h3>
            <div class="topic-detail-meta">
              <span>{{ selectedTopic.authorName || '匿名' }}</span>
              <span>{{ selectedTopic.createTime || '--' }}</span>
            </div>
            <p class="topic-detail-content">{{ selectedTopic.content || '暂无内容' }}</p>
          </div>

          <div class="comment-section">
            <div class="comment-input-area">
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="3"
                placeholder="发表你的评论..."
                maxlength="500"
                show-word-limit
              />
              <div class="comment-actions">
                <el-checkbox v-model="commentAnonymous">匿名评论</el-checkbox>
                <el-button type="primary" size="small" :loading="commentSubmitting" @click="submitComment">
                  发表评论
                </el-button>
              </div>
            </div>

            <div class="comment-list">
              <div v-if="commentLoading" class="loading-state">
                <span>加载评论中...</span>
              </div>
              <div v-else-if="commentList.length === 0" class="empty-state">
                <el-empty description="暂无评论，快来发表第一条评论吧" />
              </div>
              <div v-else class="comment-item" v-for="comment in commentList" :key="comment.id">
                <div class="comment-avatar">
                  <el-avatar :size="40">
                    {{ (comment.authorName || '匿').charAt(0) }}
                  </el-avatar>
                </div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.authorName || '匿名用户' }}</span>
                    <span class="comment-time">{{ comment.createTime || '--' }}</span>
                  </div>
                  <p class="comment-content">{{ comment.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else>
          <div v-if="loading" class="loading-state">
            <span>加载中...</span>
          </div>
          <div v-else-if="topicList.length === 0" class="empty-state">
            <el-empty description="暂无讨论" />
          </div>
          <div v-else class="topic-list">
            <div class="topic-card" v-for="topic in topicList" :key="topic.id">
              <div class="topic-card-header" style="display:flex; justify-content:space-between; align-items:center;">
                <h4 class="topic-card-title" @click="openTopic(topic)" style="cursor:pointer; flex:1;">
                  {{ topic.title }}
                  <!-- ✅ 添加标签 -->
                  <el-tag v-if="topic.isTop == 1" type="danger" size="small" style="margin-left:8px;">置顶</el-tag>
                  <el-tag v-if="topic.allowComment == 0" type="warning" size="small" style="margin-left:8px;">🔇 已禁言</el-tag>
                </h4>
                <!-- 操作按钮 -->
                <div class="topic-actions">
                  <el-button
                      v-if="String(userStore.userInfo?.id) === String(topic.authorId) || userStore.userInfo?.identity === 'teacher'"
                      size="small"
                      type="primary"
                      text
                      @click.stop="openEditTopic(topic)"
                  >编辑</el-button>
                  <el-button
                      v-if="String(userStore.userInfo?.id) === String(topic.authorId) || userStore.userInfo?.identity === 'teacher'"
                      size="small"
                      type="danger"
                      text
                      @click.stop="handleDeleteTopic(topic)"
                  >删除</el-button>
                </div>
              </div>
              <p class="topic-card-content" @click="openTopic(topic)" style="cursor:pointer;">{{ topic.content || '暂无内容' }}</p>
              <div class="topic-card-footer">
                    <span class="topic-card-author">
                      <el-icon><User /></el-icon>
                      {{ topic.authorName || '匿名' }}
                    </span>
                <span class="topic-card-time">{{ topic.createTime || '--' }}</span>
                <span class="topic-card-reply">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ topic.replyCount || topic.replyNum || 0 }} 回复
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showPublishDialog" title="发起讨论" width="600px">
      <el-form :model="topicForm" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="topicForm.title" placeholder="请输入讨论标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="topicForm.content" type="textarea" :rows="5" placeholder="请输入讨论内容" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="topicForm.isAnonymous">匿名发布</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" :loading="topicSubmitting" @click="submitTopic">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage,ElMessageBox } from 'element-plus'
import {
  User, Collection, DocumentChecked, Document, ChatDotRound, Clock,
  UploadFilled, Check, Link, Folder, Plus, ArrowLeft, RefreshRight
} from '@element-plus/icons-vue'
import { getStudentCourses } from '@/api/course'
import { getHomeworkByCourse,getStudentHomeworkStatus } from '@/api/homework'
import { getResourceTree, downloadResource as downloadRes } from '@/api/resource'
import { getTopicList, publishTopic, getCommentList, addComment,deleteTopic,editTopic } from '@/api/topic'
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
const selectedTopic = ref(null)
const commentList = ref([])
const commentLoading = ref(false)
const showPublishDialog = ref(false)
const topicSubmitting = ref(false)
const commentSubmitting = ref(false)
const newComment = ref('')
const commentAnonymous = ref(false)
const editingTopicId = ref(null)


const topicForm = ref({
  title: '',
  content: '',
  isAnonymous: false
})

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/main/student-course')
  }
}

const switchTab = (tab) => {
  activeTab.value = tab
  selectedTopic.value = null
  if (tab === 'homework') loadHomework()
  else if (tab === 'resource') loadResource()
  else if (tab === 'topic') loadTopics()
}

const loadCourseInfo = async () => {
  try {
    const studentId = userStore.userInfo?.id || localStorage.getItem('userId')
    const res = await getStudentCourses(studentId)
    if (res.code === 200 && res.data) {
      const course = res.data.find(c => (c.courseId || c.id) === courseId.value)
      if (course) {
        courseInfo.value = course
        // ✅ 保存 courseNo 到 localStorage
        if (course.courseNo) {
          localStorage.setItem('currentCourseNo', course.courseNo)
        }
        console.log('课程信息加载成功:', course)
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
      const homeworkData = res.data || []

      // ✅ 检查每个作业是否已提交（只需要传 homeworkId）
      for (const hw of homeworkData) {
        try {
          const statusRes = await getStudentHomeworkStatus(hw.id)
          if (statusRes.code === 200 && statusRes.data) {
            hw.submitted = true
          } else {
            hw.submitted = false
          }
        } catch (e) {
          hw.submitted = false
        }
      }

      homeworkList.value = homeworkData
    }
  } catch (error) {
    console.error('加载作业失败:', error)
    ElMessage.error('加载作业失败')
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

const loadTopics = async () => {
  // ✅ 确保 courseNo 保存到 localStorage
  if (route.query.courseNo) {
    localStorage.setItem('currentCourseNo', route.query.courseNo)
  } else if (courseInfo.value?.courseNo) {
    localStorage.setItem('currentCourseNo', courseInfo.value.courseNo)
  }

  loading.value = true
  try {
    const res = await getTopicList(courseId.value)
    if (res.code === 200) {
      // ✅ 处理 allowComment 默认值
      topicList.value = (res.data || []).map(topic => ({
        ...topic,
        allowComment: topic.allowComment !== undefined ? topic.allowComment : 1
      }))
    }
  } catch (error) {
    console.error('加载讨论列表失败:', error)
  } finally {
    loading.value = false
  }
}

const openTopic = async (topic) => {
  selectedTopic.value = topic
  commentList.value = []
  commentLoading.value = true
  try {
    const res = await getCommentList(topic.id)
    if (res.code === 200) {
      commentList.value = res.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    commentLoading.value = false
  }
}

const submitTopic = async () => {
  if (!topicForm.value.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!topicForm.value.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  const courseNo = localStorage.getItem('currentCourseNo') || ''
  if (!courseNo) {
    ElMessage.error('课程编号缺失，请重新进入课程')
    return
  }

  topicSubmitting.value = true
  try {
    const userId = userStore.userInfo?.id || localStorage.getItem('userId')
    const userName = userStore.userInfo?.username || userStore.userInfo?.name || localStorage.getItem('userName') || '学生'

    // ✅ 如果是编辑模式
    if (editingTopicId.value) {
      const res = await editTopic({
        id: editingTopicId.value,
        title: topicForm.value.title,
        content: topicForm.value.content,
        authorId: userId,
        identity: userStore.userInfo?.identity || 'student'
      })
      if (res.code === 200) {
        ElMessage.success('修改成功')
        showPublishDialog.value = false
        editingTopicId.value = null
        topicForm.value = { title: '', content: '', isAnonymous: false }
        loadTopics()
      } else {
        ElMessage.error(res.msg || '修改失败')
      }
    } else {
      // 发布新话题
      const res = await publishTopic({
        courseId: courseId.value,
        courseNo: courseNo,
        title: topicForm.value.title,
        content: topicForm.value.content,
        authorId: userId,
        authorName: topicForm.value.isAnonymous ? '匿名用户' : userName,
        authorType: 2,
        isAnonymous: topicForm.value.isAnonymous ? 1 : 0
      })
      if (res.code === 200) {
        ElMessage.success('发布成功')
        showPublishDialog.value = false
        topicForm.value = { title: '', content: '', isAnonymous: false }
        loadTopics()
      } else {
        ElMessage.error(res.msg || '发布失败')
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    topicSubmitting.value = false
  }
}

// 编辑话题
const openEditTopic = (topic) => {
  // 弹出编辑对话框
  topicForm.value.title = topic.title
  topicForm.value.content = topic.content
  topicForm.value.isAnonymous = topic.isAnonymous === 1
  // 保存要编辑的话题ID
  editingTopicId.value = topic.id
  showPublishDialog.value = true
}

// 删除话题
const handleDeleteTopic = async (topic) => {
  try {
    await ElMessageBox.confirm(`确定要删除话题「${topic.title}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteTopic(
        topic.id,
        String(userStore.userInfo?.id),  // ✅ 传当前用户ID
        userStore.userInfo?.identity || 'student'
    )
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadTopics()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除话题失败:', error)
    }
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentSubmitting.value = true
  try {
    const userId = userStore.userInfo?.id || localStorage.getItem('userId')
    const userName = userStore.userInfo?.username || userStore.userInfo?.name || localStorage.getItem('userName') || '学生'
    const res = await addComment({
      topicId: selectedTopic.value.id,
      content: newComment.value,
      authorId: userId,
      authorName: commentAnonymous.value ? '匿名用户' : userName,
      authorType: 2,
      isAnonymous: commentAnonymous.value ? 1 : 0
    })

    // ✅ 后端返回的 res 中 code 可能是 200 或 500
    if (res.code === 200) {
      ElMessage.success('评论成功')
      newComment.value = ''
      commentAnonymous.value = false
      openTopic(selectedTopic.value)
    } else {
      // ✅ 直接显示 res.message
      const msg = res.message || res.msg || '评论失败'
      if (msg.includes('禁言')) {
        ElMessage.warning('该话题已被教师禁言，无法发表评论')
      } else {
        ElMessage.error(msg)
      }
    }
  } catch (error) {
    console.error('评论失败:', error)
    // ✅ 从 error.response 取错误信息
    const data = error.response?.data
    if (data) {
      const msg = data.message || data.msg || ''
      if (msg.includes('禁言')) {
        ElMessage.warning('该话题已被教师禁言，无法发表评论')
      } else {
        ElMessage.error(msg || '评论失败')
      }
    } else {
      ElMessage.error('评论失败，请稍后重试')
    }
  } finally {
    commentSubmitting.value = false
  }
}

onMounted(() => {
  loadCourseInfo()
  loadHomework()
})
</script>

<style scoped>
.course-detail-page {
  padding: 20px 40px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.course-header-card {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  padding: 30px 30px 20px 30px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(118, 75, 162, 0.2);
}

.header-content {
  position: relative;
  z-index: 2;
}

.back-link {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.back-link:hover {
  opacity: 1;
  transform: translateX(-5px);
}

.back-link .el-icon {
  margin-right: 4px;
}

.course-title {
  margin: 0 0 10px;
  font-size: 36px;
  font-weight: 700;
}

.sub-info {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.course-tabs {
  background: #fff;
  border-radius: 8px 8px 0 0;
  padding: 0 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  margin-bottom: 0;
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
  background: #fff;
  padding: 25px;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  min-height: 500px;
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

.homework-list, .resource-list {
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

.topic-section {
  padding: 0;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.topic-card {
  background: white;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.3s;
}

.topic-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.topic-card-header {
  margin-bottom: 10px;
}

.topic-card-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
}

.topic-card-content {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.topic-card-footer {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.topic-card-footer > span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.topic-card-reply {
  margin-left: auto !important;
  color: #409eff !important;
}

.topic-detail {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.topic-detail-header {
  margin-bottom: 16px;
}

.topic-detail-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  margin-bottom: 20px;
}

.topic-detail-title {
  margin: 0 0 12px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.topic-detail-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.topic-detail-content {
  margin: 0;
  font-size: 15px;
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
}

.comment-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.comment-input-area {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f7fa;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>