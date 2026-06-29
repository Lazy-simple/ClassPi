<template>
  <div class="page-wrap">
    <!-- 发布话题：所有人都可见 -->
    <div class="publishing-box">
      <el-input v-model="topicForm.title" placeholder="输入话题标题" />
      <el-input
          v-model="topicForm.content"
          type="textarea"
          :rows="4"
          placeholder="输入话题内容"
          style="margin-top:10px"
      />
      <el-button type="primary" class="mt-3" :loading="submitting" @click="handlePublishTopic">发布话题</el-button>
    </div>

    <div v-loading="loading" class="topic-list">
      <div v-if="topicList.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无话题，快来发布第一个话题吧！" />
      </div>
      <div class="topic-item" v-for="topic in topicList" :key="topic.id">
        <div class="topic-head">
          <h3>
            {{ topic.title }}
            <el-tag v-if="topic.isTop === 1" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="topic.allowComment === 0" type="warning" size="small">🔇 已禁言</el-tag>
          </h3>
          <div class="topic-operate">
            <el-button
                v-if="String(userStore.userInfo?.id) === String(topic.authorId) || userStore.userInfo?.identity === 'teacher'"
                size="small"
                @click="openEdit(topic)"
            >编辑</el-button>
            <el-button
                v-if="String(userStore.userInfo?.id) === String(topic.authorId) || userStore.userInfo?.identity === 'teacher'"
                size="small"
                type="danger"
                @click="handleDeleteTopic(topic)"
            >删除</el-button>
            <el-button
                v-if="userStore.userInfo?.identity === 'teacher'"
                size="small"
                type="warning"
                @click="handleToggleTop(topic)"
            >{{ topic.isTop === 1 ? '取消置顶' : '置顶' }}</el-button>
            <el-button
                v-if="userStore.userInfo?.identity === 'teacher'"
                size="small"
                :type="topic.allowComment === 1 ? 'danger' : 'success'"
                @click="handleToggleComment(topic)"
            >{{ topic.allowComment === 1 ? '🔇 禁言' : '🔊 解禁' }}</el-button>
          </div>
        </div>
        <div class="topic-content">{{ topic.content }}</div>
        <div class="topic-meta">
          <span>发布人：{{ topic.authorType === 1 ? '教师' : '学生' }}</span>
          <span style="margin-left:12px">{{ formatTime(topic.createTime) }}</span>
          <span style="margin-left:12px">回复数：{{ topic.replyCount || 0 }}</span>
        </div>

        <div class="comment-wrap">
          <h4>评论区</h4>
          <div v-if="topic.allowComment !== 0" class="comment-input">
            <el-input
                v-model="commentForm[topic.id].content"
                placeholder="输入评论内容"
                style="flex:1"
            />
            <el-checkbox v-model="commentForm[topic.id].isAnonymous" class="ml-2">匿名</el-checkbox>
            <el-button
                type="primary"
                size="small"
                class="ml-2"
                :loading="commentLoading"
                @click="handleSubmitComment(topic.id)"
            >提交</el-button>
          </div>
          <div v-else class="disabled-comment">
            <el-alert type="warning" :closable="false" show-icon>
              该话题已被教师禁言，无法发表评论
            </el-alert>
          </div>
          <div v-if="!commentMap[topic.id] || commentMap[topic.id].length === 0" class="empty-comment">
            暂无评论，快来抢沙发！
          </div>
          <div class="comment-item" v-for="comment in commentMap[topic.id] || []" :key="comment.id">
            <span>
              <template v-if="comment.isAnonymous === 1">
                匿名用户
              </template>
              <template v-else>
                <span v-if="comment.authorType === 1" style="color:#409eff;">👨‍🏫 {{ comment.authorName }}</span>
                <span v-else>{{ comment.authorName || '用户' }}</span>
              </template>
            </span>
            <span>{{ comment.content }}</span>
            <span style="margin-left:12px;font-size:12px;color:#999">{{ formatTime(comment.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editDialog" title="编辑话题" width="500">
      <el-input v-model="editForm.title" placeholder="标题" />
      <el-input v-model="editForm.content" type="textarea" :rows="4" style="margin-top:10px" />
      <template #footer>
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSaveEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  getTopicList as fetchTopicList,
  publishTopic as apiPublishTopic,
  editTopic as apiEditTopic,
  deleteTopic as apiDeleteTopic,
  setTopicTop as apiSetTopicTop,
  getCommentList as apiGetCommentList,
  addComment as apiAddComment,
  disableComment as apiDisableComment,
  enableComment as apiEnableComment
} from '@/api/topic'

const route = useRoute()
const userStore = useUserStore()

const props = defineProps({
  courseId: {
    type: Number,
    default: null
  }
})

const courseId = ref(Number(props.courseId) || Number(route.query.courseId) || Number(localStorage.getItem('currentCourseId')) || 1)

const loading = ref(false)
const submitting = ref(false)
const commentLoading = ref(false)

const topicList = ref([])
const commentMap = reactive({})

const topicForm = ref({ title: '', content: '' })
const commentForm = reactive({})
const editDialog = ref(false)
const editForm = ref({ topicId: null, title: '', content: '' })

const initCommentForm = (topicId) => {
  if (!commentForm[topicId]) {
    commentForm[topicId] = {
      content: '',
      isAnonymous: 0
    }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadTopics = async () => {
  if (route.query.courseNo) {
    localStorage.setItem('currentCourseNo', route.query.courseNo)
  }
  if (route.query.courseId) {
    localStorage.setItem('currentCourseId', route.query.courseId)
  }

  loading.value = true
  try {
    const res = await fetchTopicList(courseId.value)
    if (res.code === 200) {
      topicList.value = (res.data || []).map(topic => ({
        ...topic,
        allowComment: topic.allowComment !== undefined ? topic.allowComment : 1
      }))
      for (const topic of topicList.value) {
        initCommentForm(topic.id)
        await loadReplies(topic.id)
      }
    } else {
      ElMessage.error(res.msg || '加载话题失败')
    }
  } catch (error) {
    console.error('加载话题失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadReplies = async (topicId) => {
  try {
    const res = await apiGetCommentList(topicId)
    if (res.code === 200) {
      commentMap[topicId] = res.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    commentMap[topicId] = []
  }
}

const handlePublishTopic = async () => {
  if (!topicForm.value.title || !topicForm.value.content) {
    return ElMessage.warning('标题和内容不能为空')
  }

  const userInfo = userStore.userInfo
  if (!userInfo || !userInfo.id) {
    return ElMessage.warning('请先登录')
  }

  submitting.value = true
  try {
    const courseNo = localStorage.getItem('currentCourseNo') || 'C001'

    const data = {
      courseId: courseId.value,
      courseNo: courseNo,
      title: topicForm.value.title,
      content: topicForm.value.content,
      authorId: String(userInfo.id),  // ✅ 加回来
      authorName: userInfo.name || userInfo.username || '用户',
      authorType: userInfo.identity === 'teacher' ? 1 : 2,
      isAnonymous: 0
    }

    console.log('发布话题数据:', data)

    const res = await apiPublishTopic(data)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      topicForm.value = { title: '', content: '' }
      await loadTopics()
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (error) {
    console.error('发布话题失败:', error)
    if (error.response?.data) {
      ElMessage.error(error.response.data.message || '发布失败')
    } else {
      ElMessage.error('网络异常，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

const handleDeleteTopic = async (topic) => {
  try {
    await ElMessageBox.confirm(`确定要删除话题「${topic.title}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await apiDeleteTopic(
        topic.id,
        topic.authorId,
        userStore.userInfo?.identity || 'student'
    )
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await loadTopics()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除话题失败:', error)
      ElMessage.error('网络异常，请稍后重试')
    }
  }
}

const handleToggleTop = async (topic) => {
  try {
    const newTop = topic.isTop === 1 ? 0 : 1
    const res = await apiSetTopicTop(topic.id, newTop)
    if (res.code === 200) {
      ElMessage.success(newTop === 1 ? '置顶成功' : '取消置顶成功')
      await loadTopics()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('置顶操作失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  }
}

const openEdit = (topic) => {
  editForm.value = {
    topicId: topic.id,
    title: topic.title,
    content: topic.content
  }
  editDialog.value = true
}

const handleSaveEdit = async () => {
  if (!editForm.value.title || !editForm.value.content) {
    return ElMessage.warning('标题和内容不能为空')
  }

  const userInfo = userStore.userInfo
  if (!userInfo || !userInfo.id) {
    return ElMessage.warning('请先登录')
  }

  submitting.value = true
  try {
    const data = {
      id: editForm.value.topicId,
      title: editForm.value.title,
      content: editForm.value.content,
      authorId: String(userInfo.id),
      identity: userInfo.identity || 'student'
    }
    const res = await apiEditTopic(data)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      editDialog.value = false
      await loadTopics()
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (error) {
    console.error('编辑话题失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const handleToggleComment = async (topic) => {
  const isDisabled = topic.allowComment === 0
  const action = isDisabled ? '解禁' : '禁言'

  try {
    await ElMessageBox.confirm(
        `确定要${action}话题「${topic.title}」吗？${!isDisabled ? '禁言后学生将无法发表评论。' : ''}`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = isDisabled
        ? await apiEnableComment(topic.id)
        : await apiDisableComment(topic.id)

    if (res.code === 200) {
      ElMessage.success(res.msg)
      await loadTopics()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('网络异常，请稍后重试')
    }
  }
}

const handleSubmitComment = async (topicId) => {
  const form = commentForm[topicId]
  if (!form || !form.content) {
    return ElMessage.warning('评论不能为空')
  }

  const userInfo = userStore.userInfo
  if (!userInfo || !userInfo.id) {
    return ElMessage.warning('请先登录')
  }

  commentLoading.value = true
  try {
    const data = {
      topicId: Number(topicId),
      content: form.content.trim(),
      authorId: String(userInfo.id),
      authorName: userInfo.name || userInfo.username || '用户',
      authorType: userInfo.identity === 'teacher' ? 1 : 2,
      isAnonymous: form.isAnonymous ? 1 : 0
    }

    const res = await apiAddComment(data)
    if (res.code === 200) {
      ElMessage.success('评论成功')
      commentForm[topicId] = {
        content: '',
        isAnonymous: 0
      }
      await loadReplies(topicId)
      await loadTopics()
    } else {
      ElMessage.error(res.msg || res.message || '评论失败')
    }
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    commentLoading.value = false
  }
}

onMounted(() => {
  loadTopics()
})
</script>

<style scoped>
.page-wrap { padding: 20px 40px; }

.publishing-box {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  background: #fafafa;
}

.topic-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 15px;
  background: #fff;
}

.topic-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.topic-head h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
}

.topic-head .el-tag {
  margin-left: 6px;
}

.topic-content {
  margin: 10px 0;
  color: #333;
  font-size: 14px;
}

.topic-meta {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.comment-wrap {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.comment-wrap h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.comment-input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0;
  flex-wrap: wrap;
}

.comment-item {
  padding: 6px 0;
  font-size: 14px;
  border-bottom: 1px solid #f5f5f5;
}

.empty-comment {
  color: #999;
  font-size: 13px;
  padding: 8px 0;
}

.disabled-comment {
  margin: 10px 0;
}

.ml-2 { margin-left: 8px; }
.mt-3 { margin-top: 12px; }
.empty-tip { padding: 40px 0; }

.topic-operate {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
</style>