<template>
  <div class="course-select-page">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <h2 class="page-title">🎓 选课中心</h2>
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或编号..."
          prefix-icon="Search"
          clearable
          @keyup.enter="loadCourses"
          style="width: 300px;"
        />
        <el-button type="primary" :loading="loading" @click="loadCourses" style="margin-left: 10px;">刷新列表</el-button>
      </div>
    </div>

    <!-- 第一部分：我的课表 (已选) -->
    <div class="section-block">
      <div class="section-title">
        <span class="title-dot"></span>
        <h3>我的课表 ({{ selectedCourses.length }})</h3>
      </div>

      <div v-if="selectedCourses.length === 0" class="empty-tip">
        <el-empty description="暂无已选课程，快去下方挑选吧！" />
      </div>

      <div v-else class="course-grid">
        <!-- 注意：key 使用 sc.id 或 sc.courseId，取决于后端返回的主键 -->
        <div class="course-card selected-card" v-for="sc in selectedCourses" :key="sc.id || sc.courseId">
          <div class="card-tag tag-selected">已选</div>
          <div class="card-content">
            <!-- 兼容处理：如果后端返回 name，就用 name；如果有 courseName 就用 courseName -->
            <h4 class="course-name">{{ sc.courseName || sc.name }}</h4>
            <p class="course-no">编号：{{ sc.courseNo }}</p>
            <div class="info-row">
              <el-icon><Collection /></el-icon> <span>{{ sc.credit || 0 }} 学分</span>
            </div>
            <div class="info-row">
              <el-icon><Clock /></el-icon> <span>{{ sc.schedule || '待定' }}</span>
            </div>
          </div>
          <div class="card-action">
            <el-button type="danger" plain size="small" :loading="loading" @click="handleDrop(sc)">退选</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二部分：可选课程池 -->
    <div class="section-block">
      <div class="section-title">
        <span class="title-dot primary"></span>
        <h3>可选课程</h3>
      </div>

      <div class="course-grid">
        <div class="course-card" v-for="course in availableCourses" :key="course.id">
          <div class="card-content">
            <h4 class="course-name">{{ course.name }}</h4>
            <p class="course-no">编号：{{ course.courseNo }}</p>
            <div class="info-row">
              <el-icon><User /></el-icon> <span>教师：{{ course.teacherName || '未知' }}</span>
            </div>
            <div class="info-row">
              <el-icon><Clock /></el-icon> <span>{{ course.schedule || '待定' }}</span>
            </div>
            <div class="capacity-info">
              <span>余量：</span>
              <!-- 防止除以0错误 -->
              <el-progress
                v-if="course.capacity > 0"
                :percentage="(course.enrolledCount / course.capacity) * 100"
                :format="() => `${course.enrolledCount || 0}/${course.capacity}`"
                :stroke-width="8"
                :status="course.enrolledCount >= course.capacity ? 'exception' : ''"
              />
              <span v-else>未设置容量</span>
            </div>
          </div>
          <div class="card-action">
            <el-button
              v-if="isSelected(course.id)"
              disabled
              type="success"
              plain
            >
              <el-icon><Check /></el-icon> 已选
            </el-button>
            <el-button
              v-else-if="course.enrolledCount >= course.capacity"
              disabled
              type="info"
            >
              已满
            </el-button>
            <el-button
              v-else
              type="primary"
              :loading="loading"
              @click="handleSelect(course)"
            >
              立即选课
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/store/user';
import { getCourseList, getStudentCourses, selectCourse, dropCourse } from '@/api/course';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Clock, User, Collection, Check } from '@element-plus/icons-vue';

const userStore = useUserStore();
const loading = ref(false);
const searchKeyword = ref('');
const allCourses = ref([]);
const selectedCourses = ref([]);

// 计算属性：过滤出未选的课程，并根据关键词搜索
const availableCourses = computed(() => {
  let courses = allCourses.value.filter(c => !selectedCourses.value.some(sc => sc.courseId === c.id));
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase();
    courses = courses.filter(c =>
      (c.courseNo && c.courseNo.toLowerCase().includes(kw)) ||
      (c.name && c.name.toLowerCase().includes(kw))
    );
  }
  return courses;
});

const isSelected = (cid) => selectedCourses.value.some(sc => sc.courseId === cid);

// 加载数据
const loadCourses = async () => {
  // 安全检查：确保用户信息已加载
  if (!userStore.userInfo || !userStore.userInfo.id) {
    console.warn("用户信息尚未加载，跳过课程请求");
    return;
  }

  loading.value = true;
  try {
    // 并行请求：获取所有课程 和 当前学生的已选课程
    const [allRes, selRes] = await Promise.all([
      getCourseList(),
      getStudentCourses(userStore.userInfo.id) // 此时 API 会自动拼接 ID 到 URL
    ]);

    // 假设后端返回格式为 { code: 200, data: [...] }
    if (allRes.code === 200) allCourses.value = allRes.data || [];
    if (selRes.code === 200) selectedCourses.value = selRes.data || [];

  } catch (error) {
    console.error("加载课程失败:", error);
    ElMessage.error('加载课程数据失败，请检查网络或联系管理员');
  } finally {
    loading.value = false;
  }
};

// 选课逻辑
const handleSelect = async (course) => {
  try {
    const res = await selectCourse({
      studentId: userStore.userInfo.id,
      studentName: userStore.userInfo.username || userStore.userInfo.name, // 兼容不同字段名
      courseId: course.id,
      courseNo: course.courseNo,
      courseName: course.name
    });

    if (res.code === 200) {
      ElMessage.success(`成功选修：${course.name}`);
      loadCourses(); // 重新加载以更新状态
    } else {
      ElMessage.error(res.msg || '选课失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('网络请求异常');
  }
};

// 退课逻辑
const handleDrop = async (sc) => {
  try {
    await ElMessageBox.confirm(
      `确定要退选《${sc.courseName || sc.name}》吗？`,
      '退选确认',
      { confirmButtonText: '确定退选', cancelButtonText: '取消', type: 'warning' }
    );

    const res = await dropCourse({
      studentId: userStore.userInfo.id,
      courseId: sc.courseId || sc.id // 兼容 ID 字段
    });

    if (res.code === 200) {
      ElMessage.success('退选成功');
      loadCourses();
    } else {
      ElMessage.error(res.msg);
    }
  } catch (e) {
    // 用户点击取消，不做处理
  }
};

onMounted(loadCourses);
</script>

<style scoped>
/* 保持你原有的样式不变，这里省略以节省篇幅 */
.course-select-page { padding: 20px 40px; background-color: #f5f7fa; min-height: 100vh; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.page-title { font-size: 24px; color: #303133; font-weight: 600; }
.section-block { margin-bottom: 40px; }
.section-title { display: flex; align-items: center; margin-bottom: 20px; }
.title-dot { width: 4px; height: 18px; background-color: #909399; border-radius: 2px; margin-right: 10px; }
.title-dot.primary { background: linear-gradient(135deg, #4f46e5, #818cf8); }
.section-title h3 { margin: 0; font-size: 18px; color: #303133; }
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px; }
.course-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); transition: all 0.3s ease; position: relative; border: 1px solid transparent; display: flex; flex-direction: column; justify-content: space-between; }
.course-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(79, 70, 229, 0.15); }
.selected-card { border-color: #e0eaff; background: linear-gradient(to bottom right, #ffffff, #f9fbff); }
.tag-selected { position: absolute; top: -10px; right: 20px; background: #4f46e5; color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: bold; box-shadow: 0 2px 6px rgba(79, 70, 229, 0.4); }
.course-name { margin: 0 0 10px 0; font-size: 18px; color: #303133; line-height: 1.4; }
.course-no { color: #909399; font-size: 13px; margin-bottom: 15px; }
.info-row { display: flex; align-items: center; color: #606266; font-size: 14px; margin-bottom: 8px; }
.info-row .el-icon { margin-right: 6px; color: #4f46e5; }
.capacity-info { margin-top: 15px; font-size: 12px; color: #606266; }
.capacity-info span { margin-right: 5px; }
.card-action { margin-top: 20px; border-top: 1px solid #ebeef5; padding-top: 15px; text-align: right; }
.card-action .el-button { width: 100%; border-radius: 6px; }
.empty-tip { background: #fff; padding: 40px; border-radius: 12px; text-align: center; }
</style>