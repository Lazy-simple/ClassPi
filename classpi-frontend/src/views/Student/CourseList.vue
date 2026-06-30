<template>
  <div class="student-course-page">
    <div class="page-header">
      <span class="page-label">全部课程</span>
      <div class="header-actions">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名称或编号..."
            prefix-icon="Search"
            clearable
            @keyup.enter="loadCourses"
            style="width: 280px;"
            size="default"
        />
        <el-button type="primary" :loading="loading" @click="loadCourses">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <!-- 置顶课程 -->
    <div v-if="pinnedCourses.length > 0" class="section-block">
      <div class="section-title">
        <span class="title-dot warning"></span>
        <h3>📌 置顶课程 ({{ pinnedCourses.length }})</h3>
      </div>
      <div class="course-grid">
        <div class="course-card pinned-card" v-for="sc in pinnedCourses" :key="'pin-' + (sc.id || sc.courseId)" @click="goToCourseDetail(sc)">
          <div class="course-cover">
            <div class="cover-bg" :style="{ background: getCoverColor(sc.id || sc.courseId) }"></div>
            <div class="cover-content">
              <div class="course-title">{{ sc.courseName || sc.name }}</div>
              <div class="course-subtitle">{{ sc.teacherName || '未知教师' }}</div>
            </div>
            <div class="pinned-badge">置顶</div>
            <el-dropdown
              class="more-dropdown"
              trigger="click"
              @command="handleCommand"
              @click.stop
            >
              <div class="more-btn" @click.stop>
                <el-icon><MoreFilled /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="{ action: 'unpin', course: sc }">取消置顶</el-dropdown-item>
                  <el-dropdown-item :command="{ action: 'drop', course: sc }">退选</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="course-body">
            <div class="course-code-row">
              <el-icon><Grid /></el-icon>
              <span>课程号：</span>
              <span class="course-code">{{ sc.courseNo }}</span>
            </div>
            <div class="info-section">
              <div class="info-item">
                <el-icon><CreditCard /></el-icon>
                <span>{{ sc.credit || 0 }} 学分</span>
              </div>
            </div>
          </div>
          <div class="course-footer">
            <div class="member-info">
              <el-icon><User /></el-icon>
              <span>{{ sc.teacherName || '未知教师' }}</span>
            </div>
            <el-button type="primary" size="small" @click.stop="goToCourseDetail(sc)">
              进入课程
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 已选课程（可拖拽部分） -->
    <div class="section-block">
      <div class="section-title">
        <span class="title-dot"></span>
        <h3>已选课程 ({{ unpinnedList.length }})</h3>
      </div>
      <div v-if="unpinnedList.length === 0" class="empty-tip">
        <el-empty description="暂无已选课程，快去下方挑选吧！" />
      </div>
      <draggable
        v-else
        v-model="unpinnedList"
        item-key="id"
        ghost-class="drag-ghost"
        chosen-class="drag-chosen"
        drag-class="drag-active"
        @change="onSortChange"
        class="course-grid"
      >
        <template #item="{ element }">
          <div class="course-card" :key="element.id || element.courseId" @click="goToCourseDetail(element)">
            <div class="course-cover">
              <div class="cover-bg" :style="{ background: getCoverColor(element.id || element.courseId) }"></div>
              <div class="cover-content">
                <div class="course-title">{{ element.courseName || element.name }}</div>
                <div class="course-subtitle">{{ element.teacherName || '未知教师' }}</div>
              </div>
              <el-dropdown
                class="more-dropdown"
                trigger="click"
                @command="handleCommand"
                @click.stop
              >
                <div class="more-btn" @click.stop>
                  <el-icon><MoreFilled /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="{ action: 'pin', course: element }">置顶</el-dropdown-item>
                    <el-dropdown-item :command="{ action: 'drop', course: element }">退选</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div class="course-body">
              <div class="course-code-row">
                <el-icon><Grid /></el-icon>
                <span>课程号：</span>
                <span class="course-code">{{ element.courseNo }}</span>
              </div>
              <div class="info-section">
                <div class="info-item">
                  <el-icon><CreditCard /></el-icon>
                  <span>{{ element.credit || 0 }} 学分</span>
                </div>
              </div>
            </div>
            <div class="course-footer">
              <div class="member-info">
                <el-icon><User /></el-icon>
                <span>{{ element.teacherName || '未知教师' }}</span>
              </div>
              <el-button type="primary" size="small" @click.stop="goToCourseDetail(element)">
                进入课程
              </el-button>
            </div>
          </div>
        </template>
      </draggable>
    </div>

    <!-- 可选课程 -->
    <div class="section-block">
      <div class="section-title">
        <span class="title-dot primary"></span>
        <h3>可选课程</h3>
      </div>
      <div class="course-grid">
        <div class="course-card available-card" v-for="course in availableCourses" :key="course.id">
          <div class="course-cover">
            <div class="cover-bg" :style="{ background: getCoverColor(course.id) }"></div>
            <div class="cover-content">
              <div class="course-title">{{ course.name }}</div>
              <div class="course-subtitle">{{ course.teacherName || '未知教师' }}</div>
            </div>
          </div>
          <div class="course-body">
            <div class="course-code-row">
              <el-icon><Grid /></el-icon>
              <span>课程号：</span>
              <span class="course-code">{{ course.courseNo }}</span>
            </div>
            <div class="info-section">
              <div class="info-item">
                <el-icon><Clock /></el-icon>
                <span>{{ course.schedule || '待定' }}</span>
              </div>
              <div class="info-item">
                <el-icon><CreditCard /></el-icon>
                <span>{{ course.credit || 0 }} 学分</span>
              </div>
            </div>
            <div class="capacity-info">
              <span class="capacity-label">选课人数：</span>
              <el-progress
                  v-if="course.capacity > 0"
                  :percentage="(course.enrolledCount / course.capacity) * 100"
                  :format="() => `${course.enrolledCount || 0}/${course.capacity}`"
                  :stroke-width="6"
                  :status="course.enrolledCount >= course.capacity ? 'exception' : ''"
              />
              <span v-else>未设置容量</span>
            </div>
          </div>
          <div class="course-footer">
            <div class="member-info">
              <el-icon><User /></el-icon>
              <span>{{ course.teacherName || '未知' }}</span>
            </div>
            <div class="footer-actions">
              <el-button size="small" text @click="openStudentDialog(course)">
                查看学生
              </el-button>
              <el-button
                  v-if="isSelected(course.id)"
                  disabled
                  type="success"
                  size="small"
              >
                已选
              </el-button>
              <el-button
                  v-else-if="course.enrolledCount >= course.capacity"
                  disabled
                  type="info"
                  size="small"
              >
                已满
              </el-button>
              <el-button
                  v-else
                  type="primary"
                  size="small"
                  :loading="loading"
                  @click.stop="handleSelect(course)"
              >
                立即选课
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="studentDialogVisible" width="650" title="课程选课学生列表">
      <el-table :data="studentList" border>
        <el-table-column label="学生ID" prop="studentId" />
        <el-table-column label="学生姓名" prop="studentName" />
        <el-table-column label="选课状态" prop="status" :formatter="formatStatus"/>
      </el-table>
      <template #footer>
        <el-button @click="studentDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { getCourseList, getStudentCourses, selectCourse, dropCourse, getCourseAllStudent } from '@/api/course';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Clock, User, MoreFilled, Grid, CreditCard, Refresh } from '@element-plus/icons-vue';
import draggable from 'vuedraggable';

const userStore = useUserStore();
const router = useRouter();
const loading = ref(false);
const searchKeyword = ref('');
const allCourses = ref([]);
const selectedCourses = ref([]);
const pinnedCourseIds = ref([]);

const PIN_KEY = 'student_pinned_courses';

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

const getCoverColor = (id) => {
  const idx = typeof id === 'number' ? id % coverColors.length : 0;
  return coverColors[idx];
};

const handleCommand = async ({ action, course }) => {
  switch (action) {
    case 'pin':
      togglePin(course);
      break;
    case 'unpin':
      togglePin(course);
      break;
    case 'drop':
      handleDrop(course);
      break;
  }
};

// 【关键修复】用 ref 来存可拖拽列表，而不是 computed
const unpinnedList = ref([]);

const loadPinnedCourses = () => {
  try {
    const saved = localStorage.getItem(PIN_KEY);
    if (saved) {
      pinnedCourseIds.value = JSON.parse(saved);
    }
  } catch (e) {
    console.error('读取置顶课程失败:', e);
  }
};

const savePinnedCourses = () => {
  try {
    localStorage.setItem(PIN_KEY, JSON.stringify(pinnedCourseIds.value));
  } catch (e) {
    console.error('保存置顶课程失败:', e);
  }
};

const isPinned = (course) => {
  const id = course.courseId || course.id;
  return pinnedCourseIds.value.includes(id);
};

const togglePin = (course) => {
  const id = course.courseId || course.id;
  const index = pinnedCourseIds.value.indexOf(id);
  if (index > -1) {
    pinnedCourseIds.value.splice(index, 1);
    ElMessage.success('已取消置顶');
  } else {
    pinnedCourseIds.value.unshift(id);
    ElMessage.success('已置顶');
  }
  savePinnedCourses();
  // 置顶后，更新列表
  updateUnpinnedList();
};

const pinnedCourses = computed(() => {
  let courses = selectedCourses.value.filter(sc => isPinned(sc));
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase();
    courses = courses.filter(c =>
        (c.courseNo && c.courseNo.toLowerCase().includes(kw)) ||
        (c.courseName && c.courseName.toLowerCase().includes(kw)) ||
        (c.name && c.name.toLowerCase().includes(kw))
    );
  }
  return courses.sort((a, b) => {
    const aId = a.courseId || a.id;
    const bId = b.courseId || b.id;
    return pinnedCourseIds.value.indexOf(aId) - pinnedCourseIds.value.indexOf(bId);
  });
});

// 【新增】根据 selectedCourses 和置顶状态，更新 unpinnedList
const updateUnpinnedList = () => {
  let courses = selectedCourses.value.filter(sc => !isPinned(sc));
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase();
    courses = courses.filter(c =>
        (c.courseNo && c.courseNo.toLowerCase().includes(kw)) ||
        (c.courseName && c.courseName.toLowerCase().includes(kw)) ||
        (c.name && c.name.toLowerCase().includes(kw))
    );
  }
  unpinnedList.value = courses;
};

// 监听 selectedCourses 变化，更新 unpinnedList
watch([selectedCourses, searchKeyword, pinnedCourseIds], () => {
  updateUnpinnedList();
}, { deep: true });

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
const loadCourses = async () => {
  if (!userStore.userInfo || !userStore.userInfo.id) {
    console.warn("用户信息尚未加载，跳过课程请求");
    return;
  }
  loading.value = true;
  try {
    const [allRes, selRes] = await Promise.all([
      getCourseList(),
      getStudentCourses(userStore.userInfo.id)
    ]);
    if (allRes.code === 200) allCourses.value = allRes.data || [];
    if (selRes.code === 200) selectedCourses.value = selRes.data || [];
    // 加载课程后，更新列表
    updateUnpinnedList();
    loadCourseOrder();
  } catch (error) {
    console.error("加载课程失败:", error);
    ElMessage.error('加载课程数据失败，请检查网络或联系管理员');
  } finally {
    loading.value = false;
  }
};
const handleSelect = async (course) => {
  try {
    const res = await selectCourse({
      studentId: userStore.userInfo.id,
      studentName: userStore.userInfo.username || userStore.userInfo.name,
      courseId: course.id,
      courseNo: course.courseNo,
      courseName: course.name
    });
    if (res.code === 200) {
      ElMessage.success(`成功选修：${course.name}`);
      loadCourses();
    } else {
      ElMessage.error(res.msg || '选课失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('网络请求异常');
  }
};
const handleDrop = async (sc) => {
  try {
    await ElMessageBox.confirm(
        `确定要退选《${sc.courseName || sc.name}》吗？`,
        '退选确认',
        { confirmButtonText: '确定退选', cancelButtonText: '取消', type: 'warning' }
    );
    const res = await dropCourse({
      studentId: userStore.userInfo.id,
      courseId: sc.courseId || sc.id
    });
    if (res.code === 200) {
      ElMessage.success('退选成功');
      loadCourses();
    } else {
      ElMessage.error(res.msg);
    }
  } catch (e) {
    // 用户取消不处理
  }
};
const goToCourseDetail = (sc) => {
  const realCourseId = sc.courseId || sc.id;
  const courseNo = sc.courseNo || '';

  localStorage.setItem('currentCourseId', realCourseId)
  localStorage.setItem('currentCourseNo', courseNo)

  router.push({
    path: `/main/course-detail/${realCourseId}`,
    query: {
      courseId: realCourseId,
      courseNo: courseNo
    }
  });
};

// 【新增】拖拽排序回调
const saveCourseOrder = () => {
  try {
    const orderIds = unpinnedList.value.map(c => c.courseId || c.id);
    localStorage.setItem('student_course_order', JSON.stringify(orderIds));
  } catch (e) {
    console.error('保存课程排序失败:', e);
  }
};

const loadCourseOrder = () => {
  try {
    const saved = localStorage.getItem('student_course_order');
    if (saved) {
      const orderIds = JSON.parse(saved);
      unpinnedList.value.sort((a, b) => {
        const aId = a.courseId || a.id;
        const bId = b.courseId || b.id;
        return orderIds.indexOf(aId) - orderIds.indexOf(bId);
      });
    }
  } catch (e) {
    console.error('读取课程排序失败:', e);
  }
};

const onSortChange = () => {
  saveCourseOrder();
  ElMessage.success('课程排序已更新');
};

onMounted(() => {
  loadPinnedCourses();
  loadCourses();
});

const studentDialogVisible = ref(false)
const studentList = ref([])

const openStudentDialog = async (item) => {
  studentDialogVisible.value = true
  studentList.value = []
  const realCourseId = item.courseId || item.id
  try {
    const res = await getCourseAllStudent(realCourseId)
    if (res.code === 200) {
      studentList.value = res.data
    } else {
      ElMessage.error(res.message || '获取选课学生失败')
    }
  } catch (err) {
    ElMessage.error('获取选课学生失败')
    studentList.value = []
  }
}
const formatStatus = (row) => {
  return row.status === 1 ? "正常选课" : "已退选"
}
</script>

<style scoped>
.student-course-page {
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

.section-block {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.title-dot {
  width: 4px;
  height: 18px;
  background-color: #909399;
  border-radius: 2px;
  margin-right: 10px;
}

.title-dot.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.title-dot.warning {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.section-title h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 500;
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

.pinned-card {
  border-color: #fa709a;
}

.available-card {
  cursor: default;
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-subtitle {
  font-size: 12px;
  opacity: 0.9;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pinned-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.9);
  color: #e6a23c;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
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

.info-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  gap: 4px;
}

.capacity-info {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.capacity-label {
  margin-right: 4px;
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 50%;
}

.footer-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.empty-tip {
  background: #fff;
  padding: 40px;
  border-radius: 6px;
  text-align: center;
  border: 1px solid #ebeef5;
}

/* 拖拽状态样式 */
.drag-ghost {
  opacity: 0.4;
  background: #ecf5ff;
}
.drag-chosen {
  background: #f5f7fa;
}
.drag-active {
  transform: scale(1.02);
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.2);
}
</style>