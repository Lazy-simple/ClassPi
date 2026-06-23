<template>
  <div class="course-select-container">
    <div class="header">
      <h2>选课中心</h2>
      <div class="search-box">
        <input v-model="searchKeyword" placeholder="搜索课程号或课程名称" @keyup.enter="searchCourse">
        <button @click="searchCourse">搜索</button>
      </div>
    </div>
    <div class="section">
      <h3>已选课程 ({{ selectedCourses.length }}门)</h3>
      <div v-if="selectedCourses.length === 0" class="empty-state">暂无已选课程</div>
      <div class="course-grid" v-else>
        <div class="course-card selected" v-for="sc in selectedCourses" :key="sc.id">
          <div class="course-badge">已选</div>
          <h4>{{ sc.courseName }}</h4>
          <p>课程号：{{ sc.courseNo }}</p>
          <p>学分：{{ sc.credit }} 学分</p>
          <button class="btn-drop" @click="dropCourse(sc)">退选</button>
        </div>
      </div>
    </div>
    <div class="section">
      <h3>可选课程</h3>
      <div class="course-grid">
        <div class="course-card" v-for="course in availableCourses" :key="course.id">
          <h4>{{ course.name }}</h4>
          <p>课程号：{{ course.courseNo }}</p>
          <p>学分：{{ course.credit }} 学分</p>
          <p>容量：{{ course.enrolledCount }}/{{ course.capacity }}</p>
          <p>上课时间：{{ course.schedule }}</p>
          <p class="teacher">教师：{{ course.teacherName }}</p>
          <button
            class="btn-select"
            :disabled="course.enrolledCount >= course.capacity || isSelected(course.id)"
            @click="selectCourse(course)"
          >
            {{ course.enrolledCount >= course.capacity ? '已满' : isSelected(course.id) ? '已选' : '选课' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/store/user';
import { getCourseList, getStudentCourses, selectCourse, dropCourse } from '@/api/course';
import { ElMessage } from 'element-plus'
const userStore = useUserStore();
const searchKeyword = ref('');
const allCourses = ref([]);
const selectedCourses = ref([]);
const availableCourses = computed(() => {
  let courses = allCourses.value.filter(c => !selectedCourses.value.some(sc => sc.courseId === c.id))
  if(searchKeyword.value){
    const kw = searchKeyword.value.toLowerCase()
    courses = courses.filter(c=>c.courseNo.toLowerCase().includes(kw)||c.name.toLowerCase().includes(kw))
  }
  return courses
});
const isSelected = (cid) => selectedCourses.value.some(sc=>sc.courseId === cid)
const loadCourses = async () => {
  const [allRes, selRes] = await Promise.all([getCourseList(), getStudentCourses(userStore.userInfo.id)])
  if(allRes.code ===200) allCourses.value = allRes.data
  if(selRes.code ===200) selectedCourses.value = selRes.data
}
const searchCourse = ()=>{}
// 重命名函数，不和导入API重名
const handleSelect = async (course) => {
  const res = await selectCourse({
    studentId: userStore.userInfo.id,
    studentName: userStore.userInfo.username,
    courseId: course.id,
    courseNo: course.courseNo,
    courseName: course.name
  })
  if(res.code === 200){
    ElMessage.success('选课成功')
    loadCourses()
  }else ElMessage.error(res.msg)
}
const handleDrop = async (sc) => {
  if(!confirm('确定退课？')) return
  const res = await dropCourse({studentId:userStore.userInfo.id,courseId:sc.courseId})
  if(res.code ===200){
    ElMessage.success('退课成功')
    loadCourses()
  }
}
onMounted(loadCourses)
</script>
<style scoped>
.course-select-container {padding:20px;max-width:1200px;margin:0 auto;}
.header {display:flex;justify-content:space-between;align-items:center;margin-bottom:30px;}
.search-box {display:flex;gap:10px;}
.search-box input {padding:10px;width:250px;border:1px solid #ddd;border-radius:6px;}
.search-box button {background:#4f46e5;color:white;border:none;padding:10px 20px;border-radius:6px;cursor:pointer;}
.section {margin-bottom:40px;}
.empty-state {padding:40px;text-align:center;background:#f9fafb;border-radius:10px;color:#999;}
.course-grid {display:grid;grid-template-columns: repeat(auto-fill, minmax(280px,1fr));gap:20px;}
.course-card {background:white;padding:20px;border-radius:10px;box-shadow:0 2px 8px #00000010;position:relative;}
.course-card.selected {border:2px solid #4f46e5;}
.course-badge {position:absolute;top:10px;right:10;background:#4f46e5;color:white;padding:4px 10;border-radius:20px;font-size:12px;}
.btn-select {width:100%;background:#10b981;color:white;border:none;padding:10px;border-radius:6px;margin-top:10px;cursor:pointer;}
.btn-select:disabled {background:#d1d5db;cursor:not-allowed;}
.btn-drop {width:100%;background:#ef4444;color:white;border:none;padding:10px;border-radius:6px;margin-top:10px;cursor:pointer;}
</style>