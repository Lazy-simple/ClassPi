<template>
  <div class="teacher-course-page">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <h2 class="page-title">📚 我的课程</h2>
      <div style="display:flex; align-items:center; gap:15px; flex-wrap:wrap;">
        <el-switch
            v-model="showArchived"
            active-text="显示归档"
            inactive-text="隐藏归档"
            @change="loadCourses"
        />
        <el-button type="primary" @click="openAddModal">
          <el-icon><Plus /></el-icon> 添加新课程
        </el-button>
      </div>
    </div>

    <!-- 课程网格 -->
    <div class="course-grid">
      <div class="course-card" v-for="course in courseList" :key="course.id">
        <div v-if="course.status === 1" class="card-tag tag-archived">已归档</div>
        <div class="course-info">
          <h3 class="course-name">{{ course.name }}</h3>
          <p class="course-no">课程号：{{ course.courseNo }}</p>
          <div class="info-row"><el-icon><Collection /></el-icon> 学分：{{ course.credit }}</div>
          <div class="info-row"><el-icon><User /></el-icon> 容量：{{ course.enrolledCount }}/{{ course.capacity }}</div>
          <div class="info-row"><el-icon><Location /></el-icon> 地点：{{ course.classroom || '待定' }}</div>
          <div class="info-row"><el-icon><Clock /></el-icon> 时间：{{ course.schedule || '待定' }}</div>
          <p class="course-desc">{{ course.description || '暂无课程描述' }}</p>
        </div>
        <div class="course-actions">
          <el-button type="primary" plain size="small" @click="editCourse(course)">编辑</el-button>
          <el-button type="danger" plain size="small" @click="handleDelete(course.id)">删除</el-button>
          <el-button
              :type="course.status === 1 ? 'success' : 'warning'"
              plain
              size="small"
              @click="handleArchive(course)"
          >
            {{ course.status === 1 ? '恢复' : '归档' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑 弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑课程' : '添加新课程'"
        width="600px"
        destroy-on-close
    >
      <!-- ... 弹窗内容不变 ... -->
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/store/user';
import { getTeacherCourses, createCourse, updateCourse, deleteCourse, archiveCourse } from '@/api/course';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Collection, User, Location, Clock } from '@element-plus/icons-vue';

const userStore = useUserStore();
const courseList = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editingCourseId = ref(null);

const defaultForm = { courseNo: '', name: '', credit: 3, capacity: 40, classroom: '', schedule: '', description: '' };
const courseForm = ref({ ...defaultForm });
const showArchived = ref(false);

const loadCourses = async () => {
  const res = await getTeacherCourses(userStore.userInfo.id, showArchived.value);
  if (res.code === 200) courseList.value = res.data;
};

const openAddModal = () => {
  isEdit.value = false;
  courseForm.value = { ...defaultForm };
  dialogVisible.value = true;
};

const editCourse = (course) => {
  isEdit.value = true;
  editingCourseId.value = course.id;
  courseForm.value = { ...course };
  dialogVisible.value = true;
};

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('删除后无法恢复，确定要删除这门课程吗？', '警告', { type: 'warning' });
    const res = await deleteCourse(id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      loadCourses();
    }
  } catch (e) { /* 用户取消 */ }
};

const submitCourse = async () => {
  submitting.value = true;
  try {
    const data = { ...courseForm.value, teacherId: userStore.userInfo.id };
    const res = isEdit.value
        ? await updateCourse(editingCourseId.value, data)
        : await createCourse(data);

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功');
      dialogVisible.value = false;
      loadCourses();
    } else {
      ElMessage.error(res.message || '保存失败');
    }
  } finally {
    submitting.value = false;
  }
};

// ✅ 归档/恢复课程
const handleArchive = async (course) => {
  const action = course.status === 1 ? '恢复' : '归档'
  try {
    await ElMessageBox.confirm(
        `确定要${action}课程「${course.name}」吗？${course.status !== 1 ? '归档后课程将不在列表中显示。' : ''}`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    const res = await archiveCourse(course.id, course.status !== 1)
    if (res.code === 200) {
      ElMessage.success(res.msg || `${action}成功`)
      loadCourses()
    } else {
      ElMessage.error(res.msg || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

onMounted(loadCourses);
</script>

<style scoped>
.teacher-course-page { padding: 20px 40px; background-color: #f5f7fa; min-height: 100vh; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.page-title { font-size: 24px; color: #303133; font-weight: 600; margin: 0; }
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px; }
.course-card { background: #fff; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); transition: all 0.3s; display: flex; flex-direction: column; justify-content: space-between; position: relative; }
.course-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(79, 70, 229, 0.15); }
.course-name { margin: 0 0 8px; font-size: 18px; color: #303133; }
.course-no { color: #909399; font-size: 13px; margin-bottom: 15px; }
.info-row { display: flex; align-items: center; color: #606266; font-size: 14px; margin-bottom: 8px; }
.info-row .el-icon { margin-right: 6px; color: #4f46e5; }
.course-desc { color: #909399; font-size: 13px; margin-top: 10px; line-height: 1.5; }
.course-actions { display: flex; gap: 10px; margin-top: 20px; border-top: 1px solid #ebeef5; padding-top: 15px; flex-wrap: wrap; }
.course-actions .el-button { flex: 1; min-width: 60px; }

/* ✅ 归档标签 */
.tag-archived {
  position: absolute;
  top: -10px;
  right: 20px;
  background: #909399;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  z-index: 1;
}
</style>