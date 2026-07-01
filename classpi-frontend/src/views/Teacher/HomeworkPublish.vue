<template>
  <div class="publish-homework-page">
    <el-card shadow="hover" class="form-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属课程" required>
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width:100%">
            <el-option
                v-for="course in courseList"
                :key="course.id"
                :label="course.name"
                :value="course.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="作业名称" required>
          <el-input v-model="form.title" placeholder="作业名称" style="width:100%" />
        </el-form-item>

        <el-form-item label="作业名称">
          <el-input v-model="form.subtitle" placeholder="作业名称" style="width:100%" />
        </el-form-item>

        <el-form-item label="作业简介">
          <div class="editor-wrapper">
            <div class="editor-toolbar">
              <span class="toolbar-item" @mousedown.prevent="formatText('bold')" title="加粗">B</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('italic')" title="斜体">I</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('underline')" title="下划线">U</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item" @mousedown.prevent="formatText('strikeThrough')" title="删除线">S</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item" @mousedown.prevent="formatText('h1')" title="标题1">H1</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('h2')" title="标题2">H2</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('h3')" title="标题3">H3</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item" @mousedown.prevent="formatText('insertUnorderedList')" title="无序列表">☰</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('insertOrderedList')" title="有序列表">☷</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('justifyLeft')" title="左对齐">↔</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('justifyCenter')" title="居中">↕</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('justifyRight')" title="右对齐">↔</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item" @mousedown.prevent="formatText('insertParagraphBefore')" title="上方插入段落">⊕</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('insertParagraphAfter')" title="下方插入段落">⊖</span>
              <span class="toolbar-divider"></span>
              <span class="toolbar-item" @mousedown.prevent="formatText('undo')" title="撤销">☝</span>
              <span class="toolbar-item" @mousedown.prevent="formatText('redo')" title="重做">✎</span>
            </div>
            <div 
              ref="editorRef"
              contenteditable="true" 
              class="editor-content"
              placeholder="作业简介，作业格式要求,温馨提醒：word,pdf,txt等文字性的文档，都可以进行查重"
              @input="handleEditorInput"
              @keyup="saveSelection"
              @mouseup="saveSelection"
              @focus="editorFocused = true"
              @blur="editorFocused = false"
            ></div>
          </div>
        </el-form-item>

        <el-form-item label="截止日期">
          <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择截止日期和时间" style="width:100%" />
        </el-form-item>

        <el-form-item label="满分值">
          <el-input v-model.number="form.fullScore" type="number" placeholder="满分值" style="width:200px" />
        </el-form-item>

        <el-form-item label="是否查重">
          <div class="check-item">
            <el-switch v-model="form.enableCheck" />
            <span class="check-label">查重阈值:</span>
            <el-input v-model.number="form.checkThreshold" type="number" style="width:80px" />
            <span class="check-unit">%</span>
          </div>
        </el-form-item>

        <el-form-item>
          <div class="check-item">
            <el-checkbox v-model="form.autoReject" />
            <span class="check-label">查重率高于</span>
            <el-input v-model.number="form.rejectThreshold" type="number" style="width:80px" />
            <span class="check-unit">%自动打回</span>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="showImportDialog = true" class="import-btn">
            <el-icon><Upload /></el-icon> 导入作业
          </el-button>
          <el-button @click="handleCancel" class="cancel-btn">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submit" class="submit-btn">
            发布个人作业
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="showImportDialog" title="导入作业" width="800px" top="5vh">
      <div class="import-container">
        <el-tabs v-model="importTab" class="import-tabs">
          <el-tab-pane label="从备课区导入" name="preparation">
            <div class="import-content">
              <div class="preparation-sidebar">
                <div class="sidebar-item active">
                  <el-icon><FolderOpened /></el-icon>
                  <span>我的备课区</span>
                </div>
              </div>
              <div class="preparation-main">
                <div 
                  v-for="item in preparationList" 
                  :key="item.id" 
                  class="preparation-item"
                  :class="{ selected: selectedPreparationId === item.id }"
                  @click="selectPreparation(item)"
                >
                  <input type="radio" :value="item.id" v-model="selectedPreparationId" />
                  <div class="preparation-info">
                    <div class="preparation-title">{{ item.title }}</div>
                    <div class="preparation-desc">{{ item.content }}</div>
                    <div class="preparation-time">编辑于 {{ formatTime(item.updateTime) }}</div>
                  </div>
                </div>
                <div v-if="preparationList.length === 0" class="empty-tip">
                  暂无备课内容，请先在备课区创建
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="从课程导入" name="course">
            <div class="import-content">
              <el-select v-model="importCourseId" placeholder="请选择课程" style="width:100%; margin-bottom: 15px;">
                <el-option
                    v-for="course in courseList"
                    :key="course.id"
                    :label="course.name"
                    :value="course.id"
                />
              </el-select>
              <div class="preparation-main">
                <div 
                  v-for="item in courseHomeworkList" 
                  :key="item.id" 
                  class="preparation-item"
                  :class="{ selected: selectedCourseHomeworkId === item.id }"
                  @click="selectCourseHomework(item)"
                >
                  <input type="radio" :value="item.id" v-model="selectedCourseHomeworkId" />
                  <div class="preparation-info">
                    <div class="preparation-title">{{ item.title }}</div>
                    <div class="preparation-desc">{{ item.content }}</div>
                    <div class="preparation-time">编辑于 {{ formatTime(item.updateTime) }}</div>
                  </div>
                </div>
                <div v-if="courseHomeworkList.length === 0" class="empty-tip">
                  该课程暂无已发布的作业
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <el-button @click="showImportDialog = false" class="dialog-cancel">取消</el-button>
        <el-button type="primary" :loading="importing" @click="submitImport" class="dialog-import">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { publishHomework, getHomeworkList } from '@/api/homework';
import { getTeacherCourses } from '@/api/course';
import { getTeacherPreparationsByType, getTeacherPreparations } from '@/api/preparation';
import { ElMessage } from 'element-plus';
import { Upload, FolderOpened } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';

const userStore = useUserStore();
const submitting = ref(false);
const importing = ref(false);
const courseList = ref([]);
const editorRef = ref(null);
const editorFocused = ref(false);
const savedRange = ref(null);

const form = ref({
  courseId: '',
  title: '',
  subtitle: '',
  content: '',
  deadline: '',
  fullScore: 100,
  enableCheck: false,
  checkThreshold: 50,
  autoReject: false,
  rejectThreshold: 50,
  files: []
});

const showImportDialog = ref(false);
const importTab = ref('preparation');
const preparationList = ref([]);
const selectedPreparationId = ref(null);
const courseHomeworkList = ref([]);
const importCourseId = ref(null);
const selectedCourseHomeworkId = ref(null);

const loadPreparationList = async () => {
  try {
    const res = await getTeacherPreparations(userStore.userInfo?.id, 1, 50);
    if (res.code === 200) {
      preparationList.value = res.data?.records || [];
    }
  } catch (error) {
    console.error('加载备课内容失败:', error);
  }
};

const loadCourses = async () => {
  try {
    const res = await getTeacherCourses(userStore.userInfo?.id);
    if (res.code === 200) {
      const data = res.data || [];
      const uniqueMap = new Map();
      data.forEach(course => {
        if (!uniqueMap.has(course.id) && course.name) {
          uniqueMap.set(course.id, JSON.parse(JSON.stringify(course)));
        }
      });
      courseList.value = Array.from(uniqueMap.values());
    }
  } catch (error) {
    console.error('加载课程失败:', error);
  }
};

const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString('zh-CN');
};

const formatText = (command) => {
  if (!editorRef.value) return;
  
  restoreSelection();
  const selection = window.getSelection();
  
  switch (command) {
    case 'insertParagraphBefore':
      {
        const range = selection.getRangeAt(0);
        const br = document.createElement('br');
        range.insertNode(br);
        const newRange = document.createRange();
        newRange.setStartBefore(br);
        newRange.collapse(true);
        selection.removeAllRanges();
        selection.addRange(newRange);
        savedRange.value = newRange.cloneRange();
      }
      break;
    case 'insertParagraphAfter':
      {
        const range = selection.getRangeAt(0);
        const br = document.createElement('br');
        range.insertNode(br);
        const newRange = document.createRange();
        newRange.setStartAfter(br);
        newRange.collapse(true);
        selection.removeAllRanges();
        selection.addRange(newRange);
        savedRange.value = newRange.cloneRange();
      }
      break;
    case 'redo':
      editorRef.value.innerHTML = '';
      form.value.content = '';
      savedRange.value = null;
      break;
    case 'h1':
      document.execCommand('formatBlock', false, '<h1>');
      saveSelection();
      break;
    case 'h2':
      document.execCommand('formatBlock', false, '<h2>');
      saveSelection();
      break;
    case 'h3':
      document.execCommand('formatBlock', false, '<h3>');
      saveSelection();
      break;
    default:
      document.execCommand(command, false, null);
      saveSelection();
      break;
  }
  
  handleEditorInput();
};

const saveSelection = () => {
  const selection = window.getSelection();
  if (selection.rangeCount > 0 && editorRef.value) {
    const range = selection.getRangeAt(0);
    if (editorRef.value.contains(range.commonAncestorContainer)) {
      savedRange.value = range.cloneRange();
    }
  }
};

const restoreSelection = () => {
  if (!editorRef.value) return;
  
  editorRef.value.focus();
  const selection = window.getSelection();
  
  if (savedRange.value) {
    selection.removeAllRanges();
    selection.addRange(savedRange.value);
  } else {
    const range = document.createRange();
    range.selectNodeContents(editorRef.value);
    range.collapse(true);
    selection.removeAllRanges();
    selection.addRange(range);
  }
};

const handleEditorInput = () => {
  if (editorRef.value) {
    form.value.content = editorRef.value.innerHTML;
  }
};

watch(showImportDialog, (newVal) => {
  if (newVal) {
    loadPreparationList();
    loadCourses();
  } else {
    selectedPreparationId.value = null;
    selectedCourseHomeworkId.value = null;
    importCourseId.value = null;
    courseHomeworkList.value = [];
  }
});

const handleImportTabChange = () => {
  if (importTab.value === 'preparation') {
    loadPreparationList();
  } else {
    courseHomeworkList.value = [];
    selectedCourseHomeworkId.value = null;
    importCourseId.value = null;
  }
};

const selectPreparation = (item) => {
  selectedPreparationId.value = item.id;
};

const selectCourseHomework = (item) => {
  selectedCourseHomeworkId.value = item.id;
};

const loadCourseHomeworkList = async () => {
  if (!importCourseId.value) return;
  try {
    const res = await getHomeworkList({ courseId: importCourseId.value, page: 1, pageSize: 50 });
    if (res.code === 200) {
      courseHomeworkList.value = res.data?.records || [];
    }
  } catch (error) {
    console.error('加载课程作业失败:', error);
  }
};

const submitImport = async () => {
  if (importTab.value === 'preparation') {
    if (!selectedPreparationId.value) {
      ElMessage.warning('请选择要导入的备课内容');
      return;
    }
    const item = preparationList.value.find(p => p.id === selectedPreparationId.value);
    if (item) {
      form.value.title = item.title;
      form.value.content = item.content;
      if (editorRef.value) {
        editorRef.value.innerHTML = item.content || '';
      }
      if (item.courseId) {
        form.value.courseId = Number(item.courseId);
      }
    }
  } else {
    if (!selectedCourseHomeworkId.value) {
      ElMessage.warning('请选择要导入的作业');
      return;
    }
    const item = courseHomeworkList.value.find(h => h.id === selectedCourseHomeworkId.value);
    if (item) {
      form.value.title = item.title;
      form.value.content = item.content;
      if (editorRef.value) {
        editorRef.value.innerHTML = item.content || '';
      }
      if (item.courseId) {
        form.value.courseId = Number(item.courseId);
      }
    }
  }
  ElMessage.success('导入成功');
  showImportDialog.value = false;
};

const handleCancel = () => {
  form.value = {
    courseId: '',
    title: '',
    subtitle: '',
    content: '',
    deadline: '',
    fullScore: 100,
    enableCheck: false,
    checkThreshold: 50,
    autoReject: false,
    rejectThreshold: 50,
    files: []
  };
  if (editorRef.value) {
    editorRef.value.innerHTML = '';
  }
};

const submit = async () => {
  if (!form.value.courseId) {
    ElMessage.warning('请选择所属课程');
    return;
  }
  if (!form.value.title || !form.value.deadline) {
    ElMessage.warning('请填写完整的作业标题和截止时间');
    return;
  }

  submitting.value = true;
  try {
    const submitData = {
      courseId: form.value.courseId,
      title: form.value.title,
      content: form.value.content,
      deadline: form.value.deadline,
      fullScore: form.value.fullScore,
      enableCheck: form.value.enableCheck ? 1 : 0,
      checkThreshold: form.value.checkThreshold,
      autoReject: form.value.autoReject ? 1 : 0,
      rejectThreshold: form.value.rejectThreshold,
      fileUrl: form.value.files?.[0]?.url || '',
      fileName: form.value.files?.[0]?.name || '',
      fileType: form.value.files?.[0]?.type || ''
    };

    const res = await publishHomework(submitData);
    if (res.code === 200) {
      ElMessage.success('作业发布成功！');
      handleCancel();
    } else {
      ElMessage.error(res.msg || res.message || '发布失败');
    }
  } catch (error) {
    console.error('发布失败:', error);
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  loadCourses();
});

watch(importCourseId, (newVal) => {
  if (newVal) {
    loadCourseHomeworkList();
  }
});

watch(importTab, handleImportTabChange);
</script>

<style scoped>
.publish-homework-page { padding: 20px; }

.form-card {
  border-radius: 0;
  border: 1px solid #e4e7ed;
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
  transition: all 0.2s;
}

.toolbar-item:hover {
  background-color: #e4e7ed;
  color: #409eff;
}

.toolbar-item:active {
  background-color: #dcdfe6;
  transform: scale(0.95);
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background-color: #e4e7ed;
  margin: 0 5px;
}

.editor-content {
  min-height: 150px;
  padding: 15px;
  outline: none;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  background-color: #fff;
}

.editor-content:focus {
  outline: none;
}

.editor-content:empty:before {
  content: attr(placeholder);
  color: #909399;
  pointer-events: none;
}

.editor-content h1 {
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0;
  color: #303133;
}

.editor-content h2 {
  font-size: 18px;
  font-weight: bold;
  margin: 8px 0;
  color: #303133;
}

.editor-content h3 {
  font-size: 16px;
  font-weight: bold;
  margin: 6px 0;
  color: #303133;
}

.editor-content ul, .editor-content ol {
  padding-left: 24px;
  margin: 8px 0;
}

.editor-content li {
  margin: 4px 0;
}

.editor-content p {
  margin: 6px 0;
}

.check-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.check-label {
  font-size: 14px;
  color: #606266;
}

.check-unit {
  font-size: 14px;
  color: #606266;
}

.import-btn {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}

.cancel-btn {
  margin-left: 10px;
}

.submit-btn {
  margin-left: 10px;
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}

.import-container {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.import-tabs {
  flex: 1;
}

.import-content {
  display: flex;
  height: 100%;
}

.preparation-sidebar {
  width: 150px;
  border-right: 1px solid #e4e7ed;
  padding: 10px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  color: #606266;
}

.sidebar-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.preparation-main {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.preparation-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 10px;
  border: 1px solid transparent;
}

.preparation-item:hover {
  background-color: #f5f7fa;
}

.preparation-item.selected {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.preparation-item input[type="radio"] {
  margin-top: 5px;
  margin-right: 12px;
}

.preparation-info {
  flex: 1;
}

.preparation-info .preparation-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.preparation-info .preparation-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 5px;
  line-height: 1.5;
}

.preparation-info .preparation-time {
  font-size: 12px;
  color: #909399;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.dialog-cancel {
  margin-right: 10px;
}

.dialog-import {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}
</style>