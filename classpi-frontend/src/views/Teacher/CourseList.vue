<template>
  <div class="teacher-course-page">
    <div class="page-header">
      <span class="page-label">全部课程</span>
      <div class="header-actions">
        <el-button link class="sort-btn" @click="openSortDialog">
          <el-icon><Sort /></el-icon> 课程排序
        </el-button>
        <el-button link class="archive-btn" @click="openArchiveDialog">
          <el-icon><Box /></el-icon> 归档管理
        </el-button>
        <el-button type="primary" class="create-btn" @click="openAddModal">
          <el-icon><Plus /></el-icon> 创建/加入课程
        </el-button>
        <el-button type="primary" class="quick-btn">
          <el-icon><Promotion /></el-icon> 快速发布活动
        </el-button>
      </div>
    </div>

    <div class="course-grid">
      <div
        class="course-card"
        v-for="(course, index) in displayCourses"
        :key="course.id"
        :class="{ 'archived-card': isCourseArchived(course) }"
        @click="enterCourse(course)"
      >
        <div class="course-cover">
          <div class="cover-bg" :style="{ background: getCoverColor(index) }"></div>
          <div class="cover-content">
            <div class="course-title">{{ course.name }}</div>
            <div class="course-subtitle" v-if="course.department">{{ course.department }}</div>
          </div>
          <div class="teacher-badge" v-if="isTeacherOfCourse(course)">教</div>
          <el-dropdown
            class="more-dropdown"
            trigger="click"
            @command="handleCommand"
            @visible-change="(v) => onDropdownVisible(v, course)"
          >
            <div class="more-btn" @click.stop>
              <el-icon><MoreFilled /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="{ action: 'edit', course }">编辑</el-dropdown-item>
                <el-dropdown-item :command="{ action: 'delete', course }">删除</el-dropdown-item>
                <el-dropdown-item :command="{ action: 'archive', course }" v-if="!isCourseArchived(course)">归档</el-dropdown-item>
                <el-dropdown-item :command="{ action: 'restore', course }" v-if="isCourseArchived(course)">恢复课程</el-dropdown-item>
                <el-dropdown-item :command="{ action: 'download', course }">打包下载</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="course-body">
          <div class="course-code-row">
            <el-icon><Grid /></el-icon>
            <span>加课码：</span>
            <span class="course-code">{{ course.courseNo }}</span>
            <el-icon class="arrow-down"><ArrowDown /></el-icon>
          </div>

          <div class="homework-section">
            <div class="section-label">近期作业</div>
            <div class="homework-list">
              <div class="homework-item" v-for="(hw, i) in getRecentHomework(course)" :key="i">
                {{ hw }}
              </div>
            </div>
          </div>
        </div>

        <div class="course-footer">
          <div class="member-info">
            <el-icon><Avatar /></el-icon>
            <span>成员{{ course.enrolledCount || 0 }}人</span>
          </div>
          <div class="footer-actions">
            <el-button link size="small" class="top-btn">置顶</el-button>
            <el-button link size="small" class="more-btn-link" @click.stop="openMoreMenu(course)">
              更多 <el-icon><ArrowDown /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <div class="course-card create-card" @click="openAddModal">
        <div class="create-inner">
          <el-icon class="create-icon"><Plus /></el-icon>
          <div class="create-text">创建课程</div>
        </div>
      </div>
    </div>

    <el-dialog v-model="addDialogVisible" :title="isJoinMode ? '加入课程' : '创建课程'" width="500px" destroy-on-close>
      <div class="dialog-tabs">
        <div class="tab-item" :class="{ active: !isJoinMode }" @click="isJoinMode = false">创建课程</div>
        <div class="tab-item" :class="{ active: isJoinMode }" @click="isJoinMode = true">加入课程</div>
      </div>

      <el-form :model="courseForm" label-width="80px" v-if="!isJoinMode">
        <el-form-item label="课程名称"><el-input v-model="courseForm.name" placeholder="请输入课程名称" /></el-form-item>
        <el-form-item label="课程号"><el-input v-model="courseForm.courseNo" placeholder="请输入课程号" /></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="courseForm.credit" :min="0" :max="10" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="courseForm.capacity" :min="1" :max="500" /></el-form-item>
        <el-form-item label="上课地点"><el-input v-model="courseForm.classroom" placeholder="请输入上课地点" /></el-form-item>
        <el-form-item label="上课时间"><el-input v-model="courseForm.schedule" placeholder="请输入上课时间" /></el-form-item>
        <el-form-item label="课程描述"><el-input type="textarea" v-model="courseForm.description" :rows="3" placeholder="请输入课程描述" /></el-form-item>
      </el-form>

      <el-form v-else label-width="80px">
        <el-form-item label="加课码">
          <el-input v-model="joinCode" placeholder="请输入加课码（课程号）" />
        </el-form-item>
        <div class="join-tip">输入教师分享的加课码，即可加入课程共同管理</div>
      </el-form>

      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCourse" :loading="submitting">
          {{ isJoinMode ? '加入' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="archiveDialogVisible" title="归档管理" width="700px" destroy-on-close>
      <el-tabs v-model="activeTab" class="archive-tabs">
        <el-tab-pane label="课程排序" name="sort">
          <div class="sort-list" v-if="sortedCourses.length > 0">
            <div
              class="sort-item"
              v-for="(course, index) in sortedCourses"
              :key="course.id"
              draggable="true"
              @dragstart="onDragStart(index)"
              @dragover.prevent
              @drop="onDrop(index)"
              :class="{ dragging: dragIndex === index }"
            >
              <el-icon class="drag-handle"><Rank /></el-icon>
              <el-radio v-model="currentSortIndex" :label="index" class="sort-radio"></el-radio>
              <span class="sort-course-name">{{ course.name }}</span>
              <span class="sort-course-sub" v-if="course.department">{{ course.department }}</span>
            </div>
          </div>
          <div v-else class="empty-tip">暂无课程</div>
        </el-tab-pane>

        <el-tab-pane label="归档管理" name="archive">
          <div class="archive-grid" v-if="archivedCourses.length > 0">
            <div class="archive-card" v-for="course in archivedCourses" :key="course.id">
              <div class="archive-cover" :style="{ background: getCoverColor(course.id) }">
                <div class="archive-title">{{ course.name }}</div>
                <el-dropdown
                  class="archive-more"
                  trigger="click"
                  @command="handleArchiveCommand"
                >
                  <div class="more-btn" @click.stop>
                    <el-icon><MoreFilled /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="{ action: 'restore', course }">恢复</el-dropdown-item>
                      <el-dropdown-item :command="{ action: 'delete', course }">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
              <div class="archive-role">角色：老师</div>
            </div>
          </div>
          <div v-else class="empty-tip">暂无归档课程</div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog v-model="confirmArchiveVisible" title="要归档此课程么？" width="420px" destroy-on-close>
      <div class="archive-confirm-content">
        <p>您可以在"课堂"-"归档管理"中查看此课程</p>
        <p class="tip-line">【归档全部】，学生的课程也会一起被归档</p>
        <p class="tip-line">【归档自己】，学生的课程不会被归档</p>
      </div>
      <template #footer>
        <el-button @click="confirmArchiveVisible = false">取消</el-button>
        <el-button @click="doArchive('all')" class="archive-all-btn">归档全部</el-button>
        <el-button type="primary" @click="doArchive('self')">归档自己</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import {
  getTeacherCourses, createCourse, updateCourse, deleteCourse, archiveCourse, getCourseByNo
} from '@/api/course';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Plus, Sort, Box, Promotion, MoreFilled, Grid, ArrowDown, Avatar, Rank
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const courseList = ref([]);
const addDialogVisible = ref(false);
const archiveDialogVisible = ref(false);
const confirmArchiveVisible = ref(false);
const isJoinMode = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editingCourseId = ref(null);
const joinCode = ref('');
const activeTab = ref('sort');
const currentArchiveCourse = ref(null);
const dragIndex = ref(-1);
const currentSortIndex = ref(0);

const defaultForm = {
  courseNo: '', name: '', credit: 3, capacity: 40,
  classroom: '', schedule: '', description: '', department: '计算机学院'
};
const courseForm = ref({ ...defaultForm });

const coverColors = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
  'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
  'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)'
];

const getExtraKey = () => `teacher_course_extra_${userStore.userInfo?.id || 'default'}`;

const getExtraData = () => {
  try {
    const data = localStorage.getItem(getExtraKey());
    return data ? JSON.parse(data) : {};
  } catch (e) {
    return {};
  }
};

const saveExtraData = (data) => {
  localStorage.setItem(getExtraKey(), JSON.stringify(data));
};

const getCourseExtra = (courseId) => {
  const extra = getExtraData();
  return extra[courseId] || { sortOrder: 9999, archiveSelf: false };
};

const setCourseExtra = (courseId, key, value) => {
  const extra = getExtraData();
  if (!extra[courseId]) extra[courseId] = { sortOrder: 9999, archiveSelf: false };
  extra[courseId][key] = value;
  saveExtraData(extra);
};

const isCourseArchived = (course) => {
  if (course.status === 1) return true;
  const extra = getCourseExtra(course.id);
  return extra.archiveSelf === true;
};

const isTeacherOfCourse = (course) => {
  return course.teacherId === userStore.userInfo?.id;
};

const getCoverColor = (index) => {
  const idx = typeof index === 'number' ? index % coverColors.length : 0;
  return coverColors[idx];
};

const getRecentHomework = (course) => {
  const homeworks = ['小组作业', 'JavaEE小作业', '范例_第一次作业', '期中作业', '期末大作业'];
  return homeworks.slice(0, 2 + (course.id % 2));
};

const displayCourses = computed(() => {
  const active = courseList.value.filter(c => !isCourseArchived(c));
  const extra = getExtraData();
  return active.sort((a, b) => {
    const orderA = extra[a.id]?.sortOrder ?? 9999;
    const orderB = extra[b.id]?.sortOrder ?? 9999;
    if (orderA !== orderB) return orderA - orderB;
    return b.id - a.id;
  });
});

const sortedCourses = computed(() => {
  return displayCourses.value;
});

const archivedCourses = computed(() => {
  return courseList.value.filter(c => isCourseArchived(c));
});

const loadCourses = async () => {
  try {
    const teacherId = userStore.userInfo?.id;
    if (!teacherId) {
      console.warn('用户未登录或用户信息缺失');
      return;
    }
    const res = await getTeacherCourses(teacherId, true);
    if (res.code === 200) {
      courseList.value = res.data || [];
      initSortOrder();
    }
  } catch (e) {
    console.error('加载课程列表失败:', e);
  }
};

const initSortOrder = () => {
  const extra = getExtraData();
  let changed = false;
  courseList.value.forEach((course, idx) => {
    if (!extra[course.id]) {
      extra[course.id] = { sortOrder: idx, archiveSelf: false };
      changed = true;
    }
  });
  if (changed) saveExtraData(extra);
};

const openAddModal = () => {
  isEdit.value = false;
  isJoinMode.value = false;
  courseForm.value = { ...defaultForm };
  joinCode.value = '';
  addDialogVisible.value = true;
};

const openSortDialog = () => {
  activeTab.value = 'sort';
  archiveDialogVisible.value = true;
};

const openArchiveDialog = () => {
  activeTab.value = 'archive';
  archiveDialogVisible.value = true;
};

const openMoreMenu = (course) => {
};

const onDropdownVisible = (visible, course) => {
};

const enterCourse = (course) => {
  router.push(`/main/teacher-course-detail/${course.id}`);
};

const handleCommand = async ({ action, course }) => {
  switch (action) {
    case 'edit':
      editCourse(course);
      break;
    case 'delete':
      handleDelete(course);
      break;
    case 'archive':
      openArchiveConfirm(course);
      break;
    case 'restore':
      handleRestore(course);
      break;
    case 'download':
      ElMessage.info('打包下载功能开发中');
      break;
  }
};

const handleArchiveCommand = async ({ action, course }) => {
  switch (action) {
    case 'restore':
      handleRestore(course);
      break;
    case 'delete':
      handleDelete(course);
      break;
  }
};

const editCourse = (course) => {
  isEdit.value = true;
  isJoinMode.value = false;
  editingCourseId.value = course.id;
  courseForm.value = { ...defaultForm, ...course };
  addDialogVisible.value = true;
};

const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm('删除后无法恢复，确定要删除这门课程吗？', '警告', { type: 'warning' });
    const res = await deleteCourse(course.id);
    if (res.code === 200) {
      const extra = getExtraData();
      delete extra[course.id];
      saveExtraData(extra);
      ElMessage.success('删除成功');
      loadCourses();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (e) { }
};

const openArchiveConfirm = (course) => {
  currentArchiveCourse.value = course;
  confirmArchiveVisible.value = true;
};

const doArchive = async (type) => {
  const course = currentArchiveCourse.value;
  if (!course) return;

  try {
    if (type === 'all') {
      const res = await archiveCourse(course.id, true);
      if (res.code === 200) {
        ElMessage.success('归档成功');
        confirmArchiveVisible.value = false;
        loadCourses();
      } else {
        ElMessage.error(res.message || '归档失败');
      }
    } else {
      setCourseExtra(course.id, 'archiveSelf', true);
      ElMessage.success('归档成功');
      confirmArchiveVisible.value = false;
      loadCourses();
    }
  } catch (e) {
    console.error(e);
  }
};

const handleRestore = async (course) => {
  try {
    const extra = getCourseExtra(course.id);
    if (extra.archiveSelf) {
      setCourseExtra(course.id, 'archiveSelf', false);
      ElMessage.success('恢复成功');
      loadCourses();
    } else {
      const res = await archiveCourse(course.id, false);
      if (res.code === 200) {
        ElMessage.success('恢复成功');
        loadCourses();
      } else {
        ElMessage.error(res.message || '恢复失败');
      }
    }
  } catch (e) { }
};

const submitCourse = async () => {
  submitting.value = true;
  try {
    if (isJoinMode.value) {
      const res = await getCourseByNo(joinCode.value);
      if (res.code === 200 && res.data) {
        ElMessage.success('加入成功');
        addDialogVisible.value = false;
        loadCourses();
      } else {
        ElMessage.error('未找到该课程，请检查加课码');
      }
    } else {
      const data = {
        ...courseForm.value,
        teacherId: userStore.userInfo.id,
        teacherName: userStore.userInfo.name || '教师'
      };
      const res = isEdit.value
        ? await updateCourse(editingCourseId.value, data)
        : await createCourse(data);

      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '修改成功' : '创建成功');
        addDialogVisible.value = false;
        loadCourses();
      } else {
        ElMessage.error(res.message || '保存失败');
      }
    }
  } finally {
    submitting.value = false;
  }
};

const onDragStart = (index) => {
  dragIndex.value = index;
};

const onDrop = (targetIndex) => {
  if (dragIndex.value === -1 || dragIndex.value === targetIndex) {
    dragIndex.value = -1;
    return;
  }

  const list = [...sortedCourses.value];
  const [moved] = list.splice(dragIndex.value, 1);
  list.splice(targetIndex, 0, moved);

  const extra = getExtraData();
  list.forEach((course, idx) => {
    if (!extra[course.id]) extra[course.id] = { sortOrder: idx, archiveSelf: false };
    extra[course.id].sortOrder = idx;
  });
  saveExtraData(extra);

  dragIndex.value = -1;
  currentSortIndex.value = targetIndex;
  loadCourses();
};

onMounted(loadCourses);
</script>

<style scoped>
.teacher-course-page {
  padding: 20px 40px;
  background-color: #fff;
  min-height: calc(100vh - 50px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.page-label {
  font-size: 14px;
  color: #606266;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-btn,
.archive-btn {
  color: #606266 !important;
  font-size: 13px;
}

.sort-btn:hover,
.archive-btn:hover {
  color: #409eff !important;
}

.create-btn,
.quick-btn {
  padding: 8px 16px;
  font-size: 13px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.course-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.course-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.archived-card {
  opacity: 0.6;
}

.course-cover {
  position: relative;
  height: 120px;
  overflow: hidden;
}

.cover-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.cover-bg::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 60%;
  height: 100%;
  background: radial-gradient(circle at 70% 30%, rgba(255,255,255,0.3) 0%, transparent 60%);
}

.cover-content {
  position: relative;
  z-index: 1;
  padding: 16px;
  color: #fff;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.course-subtitle {
  font-size: 12px;
  opacity: 0.9;
}

.teacher-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.9);
  color: #67c23a;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  z-index: 2;
}

.more-dropdown {
  position: absolute;
  bottom: 8px;
  right: 8px;
  z-index: 10;
}

.more-btn {
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
}

.more-btn:hover {
  background: #fff;
  color: #409eff;
}

.course-body {
  padding: 12px 16px;
}

.course-code-row {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
  gap: 4px;
}

.course-code {
  color: #606266;
  font-weight: 500;
}

.arrow-down {
  margin-left: auto;
  font-size: 12px;
}

.homework-section {
  margin-bottom: 8px;
}

.section-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.homework-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.homework-item {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  border-top: 1px solid #f2f6fc;
}

.member-info {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  gap: 4px;
}

.footer-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-btn,
.more-btn-link {
  font-size: 12px !important;
  padding: 0 !important;
}

.create-card {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 280px;
  border: 1px dashed #dcdfe6;
  background: #fafafa;
}

.create-card:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.create-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #909399;
}

.create-icon {
  font-size: 28px;
}

.create-text {
  font-size: 14px;
}

.dialog-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.tab-item {
  padding: 10px 24px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}

.tab-item.active {
  color: #409eff;
  border-bottom-color: #409eff;
  font-weight: 500;
}

.join-tip {
  font-size: 12px;
  color: #909399;
  margin-top: -10px;
  padding-left: 80px;
}

.archive-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.sort-list {
  display: flex;
  flex-direction: column;
}

.sort-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f2f6fc;
  cursor: move;
  transition: background 0.2s;
}

.sort-item:hover {
  background: #f5f7fa;
}

.sort-item.dragging {
  opacity: 0.5;
  background: #ecf5ff;
}

.drag-handle {
  color: #c0c4cc;
  margin-right: 12px;
  font-size: 16px;
}

.sort-radio {
  margin-right: 12px;
}

.sort-course-name {
  font-size: 14px;
  color: #303133;
  flex: 1;
}

.sort-course-sub {
  font-size: 13px;
  color: #909399;
}

.archive-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.archive-card {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  overflow: hidden;
}

.archive-cover {
  position: relative;
  height: 80px;
  padding: 12px;
  color: #fff;
}

.archive-title {
  font-size: 14px;
  font-weight: 600;
}

.archive-more {
  position: absolute;
  bottom: 6px;
  right: 6px;
}

.archive-role {
  padding: 8px 12px;
  font-size: 12px;
  color: #909399;
}

.archive-confirm-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

.archive-confirm-content p {
  margin: 0;
}

.tip-line {
  color: #909399;
  font-size: 12px;
}

.archive-all-btn {
  color: #606266 !important;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: #909399;
  font-size: 14px;
}
</style>
