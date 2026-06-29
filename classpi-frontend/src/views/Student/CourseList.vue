<template>
  <div class="course-select-page">
    <div class="page-header">
      <h2 class="page-title">🎓 我的课程</h2>
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

    <!-- 置顶课程 - 保持原样 -->
    <div v-if="pinnedCourses.length > 0" class="section-block">
      <div class="section-title">
        <span class="title-dot warning"></span>
        <h3>📌 置顶课程 ({{ pinnedCourses.length }})</h3>
      </div>
      <div class="course-grid">
        <div class="course-card selected-card pinned-card" v-for="sc in pinnedCourses" :key="'pin-' + (sc.id || sc.courseId)">
          <div class="card-tag tag-pinned">置顶</div>
          <div class="card-content" @click="goToCourseDetail(sc)">
            <h4 class="course-name">{{ sc.courseName || sc.name }}</h4>
            <p class="course-no">编号：{{ sc.courseNo }}</p>
            <div class="info-row">
              <el-icon><User /></el-icon> <span>{{ sc.teacherName || '未知教师' }}</span>
            </div>
            <div class="info-row">
              <el-icon><CreditCard /></el-icon> <span>{{ sc.credit || 0 }} 学分</span>
            </div>
          </div>
          <div class="card-action">
            <el-button type="primary" size="small" @click="goToCourseDetail(sc)">
              <el-icon><ArrowRight /></el-icon> 进入课程
            </el-button>
            <div class="card-action-bottom">
              <el-button size="small" text type="warning" @click.stop="togglePin(sc)">
                取消置顶
              </el-button>
              <el-button size="small" text type="danger" @click.stop="handleDrop(sc)">
                退选
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 已选课程（可拖拽部分，已修复） -->
    <div class="section-block">
      <div class="section-title">
        <span class="title-dot"></span>
        <h3>已选课程 ({{ unpinnedList.length }})</h3>
      </div>
      <div v-if="unpinnedList.length === 0" class="empty-tip">
        <el-empty description="暂无已选课程，快去下方挑选吧！" />
      </div>
      <!-- 【关键修复】
        1. 用 v-model 绑定 ref 变量 unpinnedList，而非 computed
        2. 添加 itemKey 绑定，用课程ID作为唯一标识
      -->
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
          <div class="course-card selected-card" :key="element.id || element.courseId">
            <div class="card-tag tag-selected">已选</div>
            <div class="card-content" @click="goToCourseDetail(element)">
              <h4 class="course-name">{{ element.courseName || element.name }}</h4>
              <p class="course-no">编号：{{ element.courseNo }}</p>
              <div class="info-row">
                <el-icon><User /></el-icon> <span>{{ element.teacherName || '未知教师' }}</span>
              </div>
              <div class="info-row">
                <el-icon><CreditCard /></el-icon> <span>{{ element.credit || 0 }} 学分</span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="primary" size="small" @click="goToCourseDetail(element)">
                <el-icon><ArrowRight /></el-icon> 进入课程
              </el-button>
              <div class="card-action-bottom">
                <el-button size="small" text type="primary" @click.stop="togglePin(element)">
                  置顶
                </el-button>
                <el-button size="small" text type="danger" @click.stop="handleDrop(element)">
                  退选
                </el-button>
              </div>
            </div>
          </div>
        </template>
      </draggable>
    </div>

    <!-- 可选课程部分保持原样 -->
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
            <el-button style="margin-top:8px;" plain size="small" @click="openStudentDialog(course)">
              查看选课学生
            </el-button>
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
import { Search, Clock, User, Collection, Check, UserFilled, CreditCard, ArrowRight } from '@element-plus/icons-vue';
import draggable from 'vuedraggable';

const userStore = useUserStore();
const router = useRouter();
const loading = ref(false);
const searchKeyword = ref('');
const allCourses = ref([]);
const selectedCourses = ref([]);
const pinnedCourseIds = ref([]);

const PIN_KEY = 'student_pinned_courses';

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
/* 原有样式保持不变，只添加拖拽状态样式 */
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
.card-content { cursor: pointer; }
.card-content:hover { background-color: rgba(79, 70, 229, 0.03); border-radius: 8px; }

.title-dot.warning { background: linear-gradient(135deg, #ff9800, #ffb74d); }

.tag-pinned { position: absolute; top: -10px; right: 20px; background: linear-gradient(135deg, #ff9800, #ffb74d); color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: bold; box-shadow: 0 2px 6px rgba(255, 152, 0, 0.4); }

.pinned-card { border-color: #fff0e0; background: linear-gradient(to bottom right, #ffffff, #fffaf0); }

.card-action-bottom {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}

.card-action-bottom .el-button {
  width: auto;
  padding: 4px 8px;
}

/* 拖拽状态样式 */
.drag-ghost {
  opacity: 0.4;
  background: #e0eaff;
  border: 2px dashed #4f46e5;
}
.drag-chosen {
  box-shadow: 0 10px 30px rgba(79, 70, 229, 0.3);
}
.drag-active {
  transform: scale(1.02);
  box-shadow: 0 12px 35px rgba(79, 70, 229, 0.4);
}
</style>