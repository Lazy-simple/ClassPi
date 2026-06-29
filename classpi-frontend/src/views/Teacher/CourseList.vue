<template>
  <div class="teacher-course-page">
    <div class="page-header">
      <span class="page-title">全部课程</span>
      <div class="header-actions">
        <span class="sort-btn" @click="openSortDialog">
          <el-icon><Sort /></el-icon> 课程排序
        </span>
        <span class="archive-btn" @click="openArchiveDialog">
          <el-icon><Folder /></el-icon> 归档管理
        </span>
        <el-button type="primary" class="create-btn" @click="openAddModal">
          <el-icon><Plus /></el-icon> 创建/加入课程
        </el-button>
        <el-button type="primary" plain class="quick-btn">
          <el-icon><Promotion /></el-icon> 快速发布活动
        </el-button>
      </div>
    </div>

    <div class="course-grid">
      <div
        class="course-card"
        v-for="course in courseList"
        :key="course.id"
        @mouseenter="course.showActions = true"
        @mouseleave="course.showActions = false"
      >
        <div class="card-cover">
          <div class="cover-placeholder">
            <div class="cover-bg"></div>
            <div class="cover-text">{{ course.name }}</div>
            <div class="cover-dept">{{ course.department || '计算机学院' }}</div>
          </div>
          <div class="teacher-badge" v-if="course.role == 1">教</div>
          <div class="teacher-badge join-badge" v-else>助</div>
          <div class="more-actions" v-show="course.showActions">
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, course)">
              <span class="more-btn"><el-icon><MoreFilled /></el-icon></span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="delete">删除</el-dropdown-item>
                  <el-dropdown-item command="archive">归档</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="card-body">
          <div class="course-code">
            <el-icon><Unlock /></el-icon>
            加课码：{{ course.courseNo }}
            <el-icon class="copy-icon" @click.stop="copyCode(course.courseNo)"><CaretBottom /></el-icon>
          </div>
          <div class="homework-list">
            <div class="homework-title">近期作业</div>
            <div class="homework-item">范例_第一次作业</div>
          </div>
        </div>
        <div class="card-footer">
          <div class="member-count">
            <el-icon><User /></el-icon>
            成员{{ course.enrolledCount || 0 }}人
          </div>
          <div class="footer-right">
            <span class="top-btn">置顶</span>
            <span class="more-btn-bottom">更多<el-icon><CaretBottom /></el-icon></span>
          </div>
        </div>
      </div>

      <div class="course-card create-card" @click="openAddModal">
        <div class="create-inner">
          <div class="create-icon">
            <el-icon><Plus /></el-icon>
          </div>
          <div class="create-text">创建课程</div>
        </div>
      </div>
    </div>

    <el-dialog v-model="addDialogVisible" :title="addDialogTitle" width="500px" destroy-on-close>
      <el-tabs v-model="activeTab" class="add-tabs">
        <el-tab-pane label="创建课程" name="create">
          <el-form :model="courseForm" label-width="80px">
            <el-form-item label="课程名称">
              <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
            </el-form-item>
            <el-form-item label="课程号">
              <el-input v-model="courseForm.courseNo" placeholder="请输入课程号" />
            </el-form-item>
            <el-form-item label="学分">
              <el-input-number v-model="courseForm.credit" :min="0" :max="10" />
            </el-form-item>
            <el-form-item label="容量">
              <el-input-number v-model="courseForm.capacity" :min="1" :max="500" />
            </el-form-item>
            <el-form-item label="上课地点">
              <el-input v-model="courseForm.classroom" placeholder="请输入上课地点" />
            </el-form-item>
            <el-form-item label="上课时间">
              <el-input v-model="courseForm.schedule" placeholder="请输入上课时间" />
            </el-form-item>
            <el-form-item label="课程描述">
              <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
            </el-form-item>
          </el-form>
          <div class="dialog-footer">
            <el-button @click="addDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="submitCreate">创建</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="加入课程" name="join">
          <div class="join-tip">请输入课程号加入课程</div>
          <el-form label-width="80px">
            <el-form-item label="课程号">
              <el-input v-model="joinCourseNo" placeholder="请输入课程号" />
            </el-form-item>
          </el-form>
          <div class="dialog-footer">
            <el-button @click="addDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="submitJoin">加入</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog v-model="archiveDialogVisible" title="归档管理" width="700px" destroy-on-close>
      <el-tabs v-model="archiveTab" class="archive-tabs">
        <el-tab-pane label="课程排序" name="sort">
          <div class="sort-list">
            <div
              v-for="(course, index) in sortList"
              :key="course.id"
              class="sort-item"
              draggable="true"
              @dragstart="handleDragStart(index)"
              @dragover.prevent="handleDragOver(index)"
              @drop="handleDrop(index)"
              @dragend="handleDragEnd"
              :class="{ 'dragging': dragIndex === index, 'drag-over': dragOverIndex === index }"
            >
              <span class="drag-handle"><el-icon><Rank /></el-icon></span>
              <span class="sort-name">{{ course.name }} {{ course.department }}</span>
            </div>
          </div>
          <div class="dialog-footer" v-if="sortList.length > 0">
            <el-button type="primary" @click="saveSort">保存排序</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="归档管理" name="archive">
          <div class="archive-list" v-if="archivedList.length > 0">
            <div class="archive-card" v-for="course in archivedList" :key="course.id">
              <div class="archive-cover">
                <div class="archive-cover-bg"></div>
                <div class="archive-cover-text">{{ course.name }}</div>
              </div>
              <div class="archive-info">
                <div class="archive-name">{{ course.name }}</div>
                <div class="archive-role">角色：{{ course.role == 1 ? '教师' : '助教' }}</div>
              </div>
              <div class="archive-actions">
                <el-dropdown trigger="click" @command="(cmd) => handleArchiveCommand(cmd, course)">
                  <span class="archive-more"><el-icon><MoreFilled /></el-icon></span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="restore">恢复</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
          <div class="empty-tip" v-else>暂无归档课程</div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog v-model="archiveConfirmVisible" title="要归档此课程么？" width="420px" destroy-on-close>
      <div class="archive-confirm-content">
        <p>您可以在"课堂"-"归档管理"中查看此课程</p>
        <p class="archive-tip">【归档全部】：学生的课程也会一起被归档</p>
        <p class="archive-tip">【归档自己】：学生的课程不会被归档</p>
      </div>
      <template #footer>
        <el-button @click="archiveConfirmVisible = false">取消</el-button>
        <el-button @click="confirmArchive('all')">归档全部</el-button>
        <el-button type="primary" @click="confirmArchive('self')">归档自己</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import {
  getTeacherCourseList,
  getTeacherArchivedCourses,
  createCourse,
  updateCourse,
  deleteCourse,
  joinCourse,
  archiveSelf,
  archiveAll,
  updateCourseSort,
  restoreCourse,
  deleteArchivedCourse
} from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Sort, Folder, MoreFilled, Unlock, CaretBottom,
  User, Promotion, Rank
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const courseList = ref([])
const archivedList = ref([])
const sortList = ref([])

const addDialogVisible = ref(false)
const activeTab = ref('create')
const submitting = ref(false)
const isEdit = ref(false)
const editingCourseId = ref(null)
const joinCourseNo = ref('')

const archiveDialogVisible = ref(false)
const archiveTab = ref('sort')

const archiveConfirmVisible = ref(false)
const currentArchiveCourse = ref(null)

const dragIndex = ref(-1)
const dragOverIndex = ref(-1)

const defaultForm = {
  courseNo: '',
  name: '',
  credit: 3,
  capacity: 50,
  classroom: '',
  schedule: '',
  description: '',
  department: '计算机学院'
}
const courseForm = ref({ ...defaultForm })

const addDialogTitle = computed(() => isEdit.value ? '编辑课程' : '创建/加入课程')

const loadCourses = async () => {
  const res = await getTeacherCourseList(userStore.userInfo.id)
  if (res.code === 200) {
    const list = res.data || []
    courseList.value = list.filter(c => c.selfArchived === 0 && c.status === 0).map(c => ({
      ...c,
      showActions: false
    }))
  }
}

const loadArchivedCourses = async () => {
  const res = await getTeacherArchivedCourses(userStore.userInfo.id)
  if (res.code === 200) {
    archivedList.value = res.data || []
  }
}

const openAddModal = () => {
  isEdit.value = false
  activeTab.value = 'create'
  courseForm.value = { ...defaultForm }
  joinCourseNo.value = ''
  addDialogVisible.value = true
}

const openSortDialog = () => {
  sortList.value = [...courseList.value]
  archiveTab.value = 'sort'
  archiveDialogVisible.value = true
}

const openArchiveDialog = () => {
  loadArchivedCourses()
  archiveTab.value = 'archive'
  archiveDialogVisible.value = true
}

const handleCommand = (command, course) => {
  if (command === 'edit') {
    editCourse(course)
  } else if (command === 'delete') {
    handleDelete(course)
  } else if (command === 'archive') {
    handleArchive(course)
  }
}

const editCourse = (course) => {
  isEdit.value = true
  editingCourseId.value = course.id
  courseForm.value = { ...course }
  activeTab.value = 'create'
  addDialogVisible.value = true
}

const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm(
      course.role === 1
        ? '删除后无法恢复，确定要删除这门课程吗？'
        : '确定要退出这门课程吗？',
      '提示',
      { type: 'warning' }
    )
    const res = await deleteCourse(course.id, userStore.userInfo.id)
    if (res.code === 200) {
      ElMessage.success(res.message || '操作成功')
      loadCourses()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {}
}

const handleArchive = (course) => {
  currentArchiveCourse.value = course
  archiveConfirmVisible.value = true
}

const confirmArchive = async (type) => {
  if (!currentArchiveCourse.value) return
  const course = currentArchiveCourse.value
  try {
    let res
    if (type === 'all') {
      if (course.role !== 1) {
        ElMessage.error('只有课程创建者才能归档全部')
        return
      }
      res = await archiveAll(course.id, userStore.userInfo.id, true)
    } else {
      res = await archiveSelf(course.id, userStore.userInfo.id, true)
    }
    if (res.code === 200) {
      ElMessage.success(res.message || '归档成功')
      archiveConfirmVisible.value = false
      loadCourses()
    } else {
      ElMessage.error(res.message || '归档失败')
    }
  } catch (e) {
    console.error(e)
  }
}

const submitCreate = async () => {
  if (!courseForm.value.name) {
    ElMessage.warning('请输入课程名称')
    return
  }
  if (!courseForm.value.courseNo) {
    ElMessage.warning('请输入课程号')
    return
  }
  submitting.value = true
  try {
    const data = { ...courseForm.value, teacherId: userStore.userInfo.id, teacherName: userStore.userInfo.name }
    let res
    if (isEdit.value) {
      res = await updateCourse(editingCourseId.value, data)
    } else {
      res = await createCourse(data)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '创建成功')
      addDialogVisible.value = false
      loadCourses()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } finally {
    submitting.value = false
  }
}

const submitJoin = async () => {
  if (!joinCourseNo.value) {
    ElMessage.warning('请输入课程号')
    return
  }
  submitting.value = true
  try {
    const res = await joinCourse(
      userStore.userInfo.id,
      userStore.userInfo.name,
      joinCourseNo.value
    )
    if (res.code === 200) {
      ElMessage.success('加入成功')
      addDialogVisible.value = false
      loadCourses()
    } else {
      ElMessage.error(res.message || '加入失败')
    }
  } finally {
    submitting.value = false
  }
}

const copyCode = (code) => {
  navigator.clipboard.writeText(code)
  ElMessage.success('已复制加课码')
}

const handleDragStart = (index) => {
  dragIndex.value = index
}

const handleDragOver = (index) => {
  dragOverIndex.value = index
}

const handleDrop = (index) => {
  if (dragIndex.value === -1 || dragIndex.value === index) return
  const list = [...sortList.value]
  const item = list.splice(dragIndex.value, 1)[0]
  list.splice(index, 0, item)
  sortList.value = list
}

const handleDragEnd = () => {
  dragIndex.value = -1
  dragOverIndex.value = -1
}

const saveSort = async () => {
  const courseIds = sortList.value.map(c => c.id)
  const res = await updateCourseSort(userStore.userInfo.id, courseIds)
  if (res.code === 200) {
    ElMessage.success('排序保存成功')
    archiveDialogVisible.value = false
    loadCourses()
  } else {
    ElMessage.error(res.message || '保存失败')
  }
}

const handleArchiveCommand = (command, course) => {
  if (command === 'restore') {
    handleRestore(course)
  } else if (command === 'delete') {
    handleDeleteArchived(course)
  }
}

const handleRestore = async (course) => {
  try {
    await ElMessageBox.confirm('确定要恢复这门课程吗？', '提示', { type: 'info' })
    const res = await restoreCourse(course.id, userStore.userInfo.id)
    if (res.code === 200) {
      ElMessage.success('恢复成功')
      loadArchivedCourses()
      loadCourses()
    } else {
      ElMessage.error(res.message || '恢复失败')
    }
  } catch (e) {}
}

const handleDeleteArchived = async (course) => {
  try {
    await ElMessageBox.confirm('删除后无法恢复，确定要删除吗？', '警告', { type: 'warning' })
    const res = await deleteArchivedCourse(course.id, userStore.userInfo.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadArchivedCourses()
      loadCourses()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {}
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.teacher-course-page {
  padding: 20px 40px;
  background-color: #fff;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.page-title {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sort-btn,
.archive-btn {
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.sort-btn:hover,
.archive-btn:hover {
  color: #409eff;
}

.create-btn,
.quick-btn {
  padding: 8px 18px !important;
  font-size: 13px !important;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.course-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.course-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.card-cover {
  height: 120px;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.cover-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  opacity: 0.9;
}

.cover-text {
  position: absolute;
  top: 20px;
  left: 15px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.cover-dept {
  position: absolute;
  top: 45px;
  left: 15px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

.teacher-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #4f46e5;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.join-badge {
  background: rgba(255, 255, 255, 0.9);
  color: #67c23a;
}

.more-actions {
  position: absolute;
  top: 10px;
  right: 40px;
}

.more-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.3s;
}

.more-btn:hover {
  background: rgba(0, 0, 0, 0.5);
}

.card-body {
  padding: 12px 15px;
}

.course-code {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  gap: 4px;
}

.copy-icon {
  margin-left: auto;
  cursor: pointer;
  font-size: 14px;
}

.homework-list {
  min-height: 60px;
}

.homework-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.homework-item {
  font-size: 13px;
  color: #303133;
  line-height: 1.5;
}

.card-footer {
  padding: 10px 15px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.member-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.top-btn,
.more-btn-bottom {
  cursor: pointer;
  transition: color 0.3s;
  display: flex;
  align-items: center;
  gap: 2px;
}

.top-btn:hover,
.more-btn-bottom:hover {
  color: #409eff;
}

.create-card {
  border: 1px dashed #dcdfe6;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 280px;
}

.create-card:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.create-inner {
  text-align: center;
  color: #909399;
}

.create-icon {
  font-size: 36px;
  margin-bottom: 10px;
  color: #c0c4cc;
}

.create-text {
  font-size: 14px;
}

.add-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f2f5;
}

.join-tip {
  color: #909399;
  font-size: 13px;
  margin-bottom: 15px;
}

.sort-list {
  max-height: 400px;
  overflow-y: auto;
}

.sort-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f2f5;
  cursor: move;
  transition: background 0.2s;
  user-select: none;
}

.sort-item:hover {
  background: #f5f7fa;
}

.sort-item.dragging {
  opacity: 0.5;
  background: #ecf5ff;
}

.sort-item.drag-over {
  border-top: 2px solid #409eff;
}

.drag-handle {
  margin-right: 12px;
  color: #c0c4cc;
  font-size: 18px;
}

.sort-name {
  font-size: 14px;
  color: #303133;
}

.archive-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  max-height: 400px;
  overflow-y: auto;
}

.archive-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.archive-cover {
  height: 80px;
  position: relative;
}

.archive-cover-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.8;
}

.archive-cover-text {
  position: absolute;
  top: 15px;
  left: 12px;
  color: #fff;
  font-size: 14px;
  font-weight: bold;
}

.archive-info {
  padding: 10px 12px;
}

.archive-name {
  font-size: 13px;
  color: #303133;
  margin-bottom: 4px;
}

.archive-role {
  font-size: 12px;
  color: #909399;
}

.archive-actions {
  position: absolute;
  top: 10px;
  right: 10px;
}

.archive-more {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.archive-confirm-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

.archive-confirm-content p {
  margin: 0 0 8px;
}

.archive-tip {
  color: #909399;
  font-size: 12px;
}
</style>
