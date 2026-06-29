<template>
  <div class="preparation-page">
    <el-tabs v-model="activeType" @tab-change="handleTypeChange" class="type-tabs">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="作业" name="homework" />
      <el-tab-pane label="话题" name="topic" />
      <el-tab-pane label="资料" name="resource" />
    </el-tabs>

    <el-card shadow="hover" class="form-card">
      <div class="card-header">
        <span class="card-title">我的备课区</span>
        <el-button type="primary" @click="showAddDialog = true">新增备课内容</el-button>
      </div>

      <div class="content-list">
        <div 
          v-for="item in tableData" 
          :key="item.id" 
          class="content-item"
        >
          <div class="content-info">
            <div class="content-title">{{ item.title }}</div>
            <div class="content-desc">{{ item.content }}</div>
            <div class="content-meta">
              <span>{{ formatTime(item.updateTime) }}</span>
              <span class="meta-divider">|</span>
              <span>{{ item.courseNo ? '已分配至 ' + item.courseNo : '未分配' }}</span>
            </div>
          </div>
          <div class="content-actions">
            <el-button size="small" @click="editPreparation(item)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
            <el-button 
              v-if="!item.courseId" 
              size="small" 
              type="primary" 
              @click="openAssignDialog(item)"
            >分配课程</el-button>
          </div>
        </div>
        <div v-if="tableData.length === 0" class="empty-tip">
          暂无备课内容，请点击右上角"新增备课内容"
        </div>
      </div>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top:20px;text-align:right;"
      />
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增备课内容" width="600px" top="5vh">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="类型" required>
          <el-select v-model="addForm.type" placeholder="请选择类型">
            <el-option label="作业" value="homework" />
            <el-option label="资源" value="resource" />
            <el-option label="话题" value="topic" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="addForm.title" placeholder="请输入标题" style="width:100%" />
        </el-form-item>
        <el-form-item label="内容" required>
          <div class="editor-wrapper">
            <div class="editor-toolbar">
              <span class="toolbar-item">B</span>
              <span class="toolbar-item">I</span>
              <span class="toolbar-item">U</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">S</span>
              <span class="toolbar-item">66</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">H1</span>
              <span class="toolbar-item">H2</span>
              <span class="toolbar-item">H3</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">☰</span>
              <span class="toolbar-item">☷</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">↔</span>
              <span class="toolbar-item">↕</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">⊕</span>
              <span class="toolbar-item">⊖</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">📷</span>
              <span class="toolbar-item">🔗</span>
            </div>
            <el-input 
              v-model="addForm.content" 
              type="textarea" 
              :rows="6" 
              placeholder="请输入内容..."
              class="editor-textarea"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEditDialog" title="编辑备课内容" width="600px" top="5vh">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="类型">
          <el-select v-model="editForm.type" disabled>
            <el-option label="作业" value="homework" />
            <el-option label="资源" value="resource" />
            <el-option label="话题" value="topic" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="editForm.title" placeholder="请输入标题" style="width:100%" />
        </el-form-item>
        <el-form-item label="内容" required>
          <div class="editor-wrapper">
            <div class="editor-toolbar">
              <span class="toolbar-item">B</span>
              <span class="toolbar-item">I</span>
              <span class="toolbar-item">U</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">S</span>
              <span class="toolbar-item">66</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">H1</span>
              <span class="toolbar-item">H2</span>
              <span class="toolbar-item">H3</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">☰</span>
              <span class="toolbar-item">☷</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">↔</span>
              <span class="toolbar-item">↕</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">⊕</span>
              <span class="toolbar-item">⊖</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item">📷</span>
              <span class="toolbar-item">🔗</span>
            </div>
            <el-input 
              v-model="editForm.content" 
              type="textarea" 
              :rows="6" 
              placeholder="请输入内容..."
              class="editor-textarea"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAssignDialog" title="分配课程" width="500px" top="5vh">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="所属课程" required>
          <el-select v-model="assignForm.courseId" placeholder="请选择课程" style="width:100%">
            <el-option 
              v-for="course in courseList" 
              :key="course.id" 
              :label="course.name" 
              :value="course.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/store/user';
import { getTeacherPreparations, getTeacherPreparationsByType, addPreparation, updatePreparation, deletePreparation, assignToCourse } from '@/api/preparation';
import { getTeacherCourses } from '@/api/course';

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const activeType = ref('all');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const tableData = ref([]);

const showAddDialog = ref(false);
const showEditDialog = ref(false);
const showAssignDialog = ref(false);

const courseList = ref([]);
const editRow = ref(null);
const assignRow = ref(null);

const addForm = reactive({ type: '', title: '', content: '' });
const editForm = reactive({ id: '', type: '', title: '', content: '' });
const assignForm = reactive({ courseId: '', courseNo: '' });

const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString('zh-CN');
};

const loadData = async () => {
  loading.value = true;
  try {
    const teacherId = userStore.userInfo?.id;
    if (!teacherId) {
      ElMessage.error('请先登录');
      return;
    }
    let res;
    if (activeType.value === 'all') {
      res = await getTeacherPreparations(teacherId, currentPage.value, pageSize.value);
    } else {
      res = await getTeacherPreparationsByType(teacherId, activeType.value, currentPage.value, pageSize.value);
    }
    if (res.code === 200) {
      tableData.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  } catch (error) {
    console.error('加载失败:', error);
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
  }
};

const handleTypeChange = () => {
  currentPage.value = 1;
  loadData();
};

const submitAdd = async () => {
  if (!addForm.type || !addForm.title || !addForm.content) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  submitting.value = true;
  try {
    const res = await addPreparation({
      teacherId: userStore.userInfo?.id,
      teacherName: userStore.userInfo?.name,
      type: addForm.type,
      title: addForm.title,
      content: addForm.content
    });
    if (res.code === 200) {
      ElMessage.success('新增成功');
      showAddDialog.value = false;
      addForm.type = '';
      addForm.title = '';
      addForm.content = '';
      loadData();
    } else {
      ElMessage.error(res.message || '新增失败');
    }
  } catch (error) {
    console.error('新增失败:', error);
    ElMessage.error('新增失败');
  } finally {
    submitting.value = false;
  }
};

const editPreparation = (row) => {
  editRow.value = row;
  editForm.id = row.id;
  editForm.type = row.type;
  editForm.title = row.title;
  editForm.content = row.content;
  showEditDialog.value = true;
};

const submitEdit = async () => {
  if (!editForm.title || !editForm.content) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  submitting.value = true;
  try {
    const res = await updatePreparation(editForm.id, {
      title: editForm.title,
      content: editForm.content,
      teacherId: userStore.userInfo?.id,
      identity: userStore.userInfo?.identity
    });
    if (res.code === 200) {
      ElMessage.success('修改成功');
      showEditDialog.value = false;
      loadData();
    } else {
      ElMessage.error(res.message || '修改失败');
    }
  } catch (error) {
    console.error('修改失败:', error);
    ElMessage.error('修改失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' });
    const res = await deletePreparation(row.id, userStore.userInfo?.id, userStore.userInfo?.identity);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      loadData();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败');
  }
};

const openAssignDialog = async (row) => {
  assignRow.value = row;
  await loadCourses();
  showAssignDialog.value = true;
};

const loadCourses = async () => {
  try {
    const res = await getTeacherCourses(userStore.userInfo?.id);
    if (res.code === 200) courseList.value = res.data || [];
  } catch (error) {
    console.error('加载课程失败:', error);
  }
};

const submitAssign = async () => {
  if (!assignForm.courseId) {
    ElMessage.warning('请选择课程');
    return;
  }
  const course = courseList.value.find(c => c.id === assignForm.courseId);
  const courseNo = course?.courseNo || '';
  submitting.value = true;
  try {
    const res = await assignToCourse(assignRow.value.id, assignForm.courseId, courseNo, userStore.userInfo?.id, userStore.userInfo?.identity);
    if (res.code === 200) {
      ElMessage.success('分配成功');
      showAssignDialog.value = false;
      loadData();
    } else {
      ElMessage.error(res.message || '分配失败');
    }
  } catch (error) {
    console.error('分配失败:', error);
    ElMessage.error('分配失败');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => loadData());
</script>

<style scoped>
.preparation-page { padding: 20px; }

.type-tabs {
  margin-bottom: 20px;
}

.type-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.type-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 10px 30px;
}

.type-tabs :deep(.el-tabs__item.is-active) {
  color: #409eff;
}

.type-tabs :deep(.el-tabs__active-bar) {
  background-color: #409eff;
}

.form-card {
  border-radius: 0;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 15px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.content-list {
  padding: 10px 0;
}

.content-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  margin-bottom: 10px;
  transition: all 0.2s;
}

.content-item:hover {
  border-color: #409eff;
  background-color: #fafafa;
}

.content-info {
  flex: 1;
}

.content-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.content-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.content-meta {
  font-size: 12px;
  color: #909399;
}

.meta-divider {
  margin: 0 8px;
  color: #e4e7ed;
}

.content-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-left: 20px;
}

.content-actions .el-button {
  padding: 4px 12px;
  font-size: 12px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.editor-wrapper {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  flex-wrap: wrap;
  gap: 2px;
}

.toolbar-item {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-radius: 4px;
}

.toolbar-item:hover {
  background-color: #e4e7ed;
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background-color: #e4e7ed;
  margin: 0 5px;
}

.editor-textarea {
  border: none;
  resize: none;
  padding: 15px;
}

.editor-textarea:focus {
  outline: none;
}
</style>