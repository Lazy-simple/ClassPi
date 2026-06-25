<template>
  <div class="page-wrap">
    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <h2>课程讨论区</h2>
          <!-- 空值判断：?. 避免userInfo为null报错 -->
          <el-switch
              v-if="userStore.userInfo?.identity === 'teacher'"
              v-model="allowDiscuss"
              active-text="允许讨论"
              inactive-text="关闭讨论"
          />
        </div>
      </template>

      <el-alert v-if="!allowDiscuss" type="warning" title="教师已关闭本课程讨论，无法发布话题和评论" />

      <div v-if="allowDiscuss" class="publishing-box">
        <el-input v-model="topicForm.title" placeholder="输入话题标题" />
        <el-input
            v-model="topicForm.content"
            type="textarea"
            :rows="4"
            placeholder="输入话题内容"
            style="margin-top:10px"
        />
        <el-button type="primary" class="mt-3" @click="publishTopic">发布话题</el-button>
      </div>

      <div class="topic-list">
        <div class="topic-item" v-for="topic in topicList" :key="topic.id">
          <div class="topic-head">
            <h3>
              {{ topic.title }}
              <el-tag v-if="topic.isTop === 1" type="danger" size="small">置顶</el-tag>
            </h3>
            <div class="topic-operate">
              <!-- 全部加上可选链 ?. 防止null报错 -->
              <el-button
                  v-if="userStore.userInfo?.identity === 'teacher' || userStore.userInfo?.id === topic.userId"
                  size="small"
                  @click="openEdit(topic)"
              >编辑</el-button>
              <el-button
                  v-if="userStore.userInfo?.identity === 'teacher' || userStore.userInfo?.id === topic.userId"
                  size="small"
                  type="danger"
                  @click="delTopic(topic.id)"
              >删除</el-button>
              <el-button
                  v-if="userStore.userInfo?.identity === 'teacher'"
                  size="small"
                  type="warning"
                  @click="topTopic(topic)"
              >{{ topic.isTop ? '取消置顶' : '置顶' }}</el-button>
            </div>
          </div>
          <div class="topic-content">{{ topic.content }}</div>
          <div class="topic-meta">
            <span>发布人：{{ topic.identity === 'teacher' ? '教师' : '学生' }}</span>
            <span style="margin-left:12px">{{ topic.createTime }}</span>
          </div>

          <div class="comment-wrap">
            <h4>评论区</h4>
            <div v-if="allowDiscuss" class="comment-input">
              <el-input v-model="commentForm.content" placeholder="输入评论内容" />
              <el-checkbox v-model="commentForm.isAnonymous" class="ml-2">匿名发布</el-checkbox>
              <el-button type="primary" size="small" class="ml-2" @click="submitComment(topic.id)">提交评论</el-button>
            </div>
            <div class="comment-item" v-for="comment in commentMap[topic.id]" :key="comment.id">
              <span>
                {{ comment.isAnonymous ? '匿名用户' : (comment.identity === 'teacher' ? '教师' : '用户') }}：
              </span>
              <span>{{ comment.content }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="editDialog" title="编辑话题" width="500">
      <el-input v-model="editForm.title" placeholder="标题" />
      <el-input v-model="editForm.content" type="textarea" :rows="4" style="margin-top:10px" />
      <template #footer>
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
// 注释接口请求，避免后端未启动阻塞页面
// import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
// import { getTopicList, publishTopic, editTopic, deleteTopic, setTopicTop, addComment, getCommentList, switchDiscuss } from '@/api/topic'

// const route = useRoute()
const userStore = useUserStore()
// const courseId = ref(Number(route.query.courseId))
const allowDiscuss = ref(1)
// 模拟测试数据，无需后端接口
const topicList = ref([
  {
    id: 1,
    title: '测试讨论话题',
    content: '这是一条测试话题内容',
    isTop: 1,
    userId: 1,
    identity: 'teacher',
    createTime: '2026-06-24 10:00'
  }
])
const commentMap = reactive({
  1: [
    { id: 1, content: '测试评论', isAnonymous: 0, identity: 'student' }
  ]
})
const topicForm = ref({ title: '', content: '' })
const commentForm = ref({ content: '', isAnonymous: 0 })
const editDialog = ref(false)
const editForm = ref({ topicId: null, title: '', content: '' })

const publishingTopic = () => {
  if (!topicForm.value.title || !topicForm.value.content) {
    return ElMessage.warning('标题和内容不能为空')
  }
  ElMessage.success('发布成功（接口未联调）')
  topicForm.value = { title: '', content: '' }
}
const topTopic = () => {
  ElMessage.success('置顶操作成功')
}
const delTopic = () => {
  ElMessage.success('删除成功')
}
const openEdit = (topic) => {
  editForm.value = {
    topicId: topic.id,
    title: topic.title,
    content: topic.content
  }
  editDialog.value = true
}
const saveEdit = () => {
  editDialog.value = false
  ElMessage.success('修改成功')
}
const submitComment = () => {
  if (!commentForm.value.content) return ElMessage.warning('评论不能为空')
  commentForm.content = ''
  ElMessage.success('评论成功')
}
const changeDiscussSwitch = () => {
  ElMessage.success('讨论状态已切换')
}
</script>

<style scoped>
.page-wrap { padding: 20px 40px; }
.header-row { display: flex; justify-content: space-between; align-items: center; }
.publishing-box { padding: 15px; border: 1px solid #eee; border-radius: 8px; margin-bottom: 20px; }
.topic-item { border: 1px solid #eee; border-radius: 8px; padding: 16px; margin-bottom: 15px; }
.topic-head { display: flex; justify-content: space-between; align-items: center; }
.topic-content { margin: 10px 0; color: #333; }
.topic-meta { font-size: 13px; color: #999; }
.comment-wrap { margin-top: 12px; padding-top: 12px; border-top: 1px solid #eee; }
.comment-input { display: flex; align-items: center; margin:10px 0; }
.comment-item { padding:6px 0; font-size:14px; }
.ml-2 { margin-left: 8px; }
.mt-3 { margin-top: 12px; }
</style>