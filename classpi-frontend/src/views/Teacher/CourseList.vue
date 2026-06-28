<template>
  <div class="teacher-course-page">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <h2 class="page-title">📚 我的课程</h2>
      <el-button type="primary" @click="openAddModal">
        <el-icon><Plus /></el-icon> 添加新课程
      </el-button>
    </div>

    <!-- 课程网格 -->
    <div class="course-grid">
      <div class="course-card" v-for="course in courseList" :key="course.id">
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
      <el-form :model="courseForm" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程号" required>
              <el-input v-model="courseForm.courseNo" placeholder="如：CS101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学分" required>
              <el-input-number v-model="courseForm.credit" :min="1" :max="10" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程名称" required>
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="容量">
              <el-input-number v-model="courseForm.capacity" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课地点">
              <el-input v-model="courseForm.classroom" placeholder="如：教学楼A-301" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="上课时间">
          <el-input v-model="courseForm.schedule" placeholder="如：周一 1-2节" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="简要介绍课程内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCourse">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/store/user';
import { getTeacherCourses, createCourse, updateCourse, deleteCourse } from '@/api/course';
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

const loadCourses = async () => {
  const res = await getTeacherCourses(userStore.userInfo.id);
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

onMounted(loadCourses);
</script>

<style scoped>
.teacher-course-page { padding: 20px 40px; background-color: #f5f7fa; min-height: 100vh; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.page-title { font-size: 24px; color: #303133; font-weight: 600; margin: 0; }
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px; }
.course-card { background: #fff; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); transition: all 0.3s; display: flex; flex-direction: column; justify-content: space-between; }
.course-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(79, 70, 229, 0.15); }
.course-name { margin: 0 0 8px; font-size: 18px; color: #303133; }
.course-no { color: #909399; font-size: 13px; margin-bottom: 15px; }
.info-row { display: flex; align-items: center; color: #606266; font-size: 14px; margin-bottom: 8px; }
.info-row .el-icon { margin-right: 6px; color: #4f46e5; }
.course-desc { color: #909399; font-size: 13px; margin-top: 10px; line-height: 1.5; }
.course-actions { display: flex; gap: 10px; margin-top: 20px; border-top: 1px solid #ebeef5; padding-top: 15px; }
.course-actions .el-button { flex: 1; }
</style>