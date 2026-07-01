<template>
  <div class="check-wrap">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>📝 作业批阅列表</span>
          <div style="display:flex; gap:10px; flex-wrap:wrap;">
            <!-- 新增：选择作业下拉框 -->
            <el-select
                v-model="selectedHomeworkId"
                placeholder="请选择作业"
                style="width:250px;"
                @change="loadSubmissions"
            >
              <el-option
                  v-for="hw in homeworkList"
                  :key="hw.id"
                  :label="hw.title"
                  :value="hw.id"
              />
            </el-select>
            <el-input
                placeholder="搜索学生姓名"
                prefix-icon="Search"
                style="width: 200px;"
                v-model="searchKeyword"
                @input="filterList"
            />
          </div>
        </div>
      </template>

      <el-table :data="filteredTableData" border stripe style="width: 100%" v-loading="listLoading">
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="fileName" label="作业附件" width="180">
          <template #default="scope">
            <template v-if="scope.row.fileUrl && scope.row.fileUrl !== ''">
              <el-link type="primary" :underline="false" @click="downloadFile(scope.row.fileUrl)">
                {{ scope.row.fileName || '下载附件' }}
              </el-link>
            </template>
            <template v-else>
              <el-tag size="small" type="info">无附件</el-tag>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="170" />
        <el-table-column label="批阅状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.corrected === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.corrected === 1 ? '已批阅' : '待批阅' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="scope">
            <el-button size="small" plain @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button
                size="small"
                type="primary"
                plain
                :loading="scope.row.aiLoading"
                @click="aiEval(scope.row)"
            >
              ✨ AI评价
            </el-button>
            <el-button
                v-if="scope.row.corrected !== 1"
                size="small"
                type="success"
                plain
                @click="openCorrectDialog(scope.row)"
            >
              📝 批阅
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 批阅弹窗 -->
    <el-dialog v-model="correctDialogVisible" title="批阅作业" width="550">
      <el-form :model="correctForm" label-width="80px">
        <el-form-item label="学生">
          <span>{{ correctForm.studentName }}</span>
        </el-form-item>
        <el-form-item label="作业内容">
          <div class="content-box">{{ correctForm.submitContent || '无' }}</div>
        </el-form-item>
        <el-form-item label="附件">
          <el-link type="primary" @click="downloadFile(correctForm.fileUrl)">{{ correctForm.fileName || '无附件' }}</el-link>
        </el-form-item>
        <el-form-item label="分数" required>
          <el-input-number v-model="correctForm.score" :min="0" :max="100" style="width:120px" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="correctForm.correctionContent" type="textarea" :rows="3" placeholder="请输入评语..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="correctDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="correctLoading" @click="submitCorrect">确认批阅</el-button>
      </template>
    </el-dialog>

    <!-- AI评价弹窗 -->
    <el-dialog v-model="dialogVisible" title="AI 助教评语" width="50%">
      <div class="ai-result-box">
        <AiComment :ai-content="aiText" />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="applyAiComment">采纳评语</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="作业详情" width="600">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生姓名">{{ detailData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailData.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="提交内容">{{ detailData.submitContent || '无' }}</el-descriptions-item>
        <el-descriptions-item label="附件">
          <el-link type="primary" @click="downloadFile(detailData.fileUrl)">{{ detailData.fileName || '无附件' }}</el-link>
        </el-descriptions-item>
        <el-descriptions-item label="批阅状态">
          <el-tag :type="detailData.corrected === 1 ? 'success' : 'warning'">
            {{ detailData.corrected === 1 ? '已批阅' : '待批阅' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.corrected === 1" label="分数">
          {{ detailData.score || '未打分' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.corrected === 1" label="评语">
          {{ detailData.correctionContent || '无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { ref, computed, onMounted } from 'vue'
import { aiEvaluate } from '@/api/ai'
import AiComment from '@/components/AiComment.vue'
import { ElMessage } from 'element-plus'
import { getHomeworkSubmissions, checkHomework, getTeacherHomeworkList } from '@/api/homework'
import { useRoute } from 'vue-router'

const props = defineProps({
  courseId: {
    type: [Number, String],
    default: ''
  }
})

const route = useRoute()
const userStore = useUserStore()
const listLoading = ref(false)
const correctLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const correctDialogVisible = ref(false)
const aiText = ref('')
const searchKeyword = ref('')
const tableData = ref([])
const detailData = ref({})
const currentAiRow = ref(null)

// ========== 新增：作业列表 ==========
const homeworkList = ref([])
const selectedHomeworkId = ref(null)

// 批阅表单
const correctForm = ref({
  submitId: null,
  studentName: '',
  submitContent: '',
  fileUrl: '',
  fileName: '',
  score: 60,
  correctionContent: ''
})

// ========== 新增：加载教师的所有作业，按当前课程过滤 ==========
const loadHomeworkList = async () => {
  try {
    const teacherId = userStore.userInfo?.id
    const res = await getTeacherHomeworkList(teacherId)
    if (res.code === 200) {
      const allList = res.data || []
      if (props.courseId) {
        homeworkList.value = allList.filter(hw =>
          String(hw.courseId) === String(props.courseId)
        )
      } else {
        homeworkList.value = allList
      }
      if (homeworkList.value.length > 0) {
        selectedHomeworkId.value = homeworkList.value[0].id
        await loadSubmissions()
      } else {
        ElMessage.info('暂无已发布的作业')
      }
    }
  } catch (error) {
    console.error('加载作业列表失败:', error)
    ElMessage.error('加载作业列表失败')
  }
}

// 过滤后的数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) return tableData.value
  return tableData.value.filter(item =>
      item.studentName?.includes(searchKeyword.value)
  )
})

// 加载提交记录
// 加载提交记录
const loadSubmissions = async () => {
  // ✅ 用选中的作业ID，而不是 route.query
  if (!selectedHomeworkId.value) {
    ElMessage.warning('请选择作业')
    return
  }

  listLoading.value = true
  try {
    const res = await getHomeworkSubmissions(selectedHomeworkId.value)  // 改这里！
    if (res.code === 200) {
      tableData.value = (res.data || []).map(item => ({
        id: item.id,
        studentName: item.studentName || '未知学生',
        fileName: item.fileName || '无附件',
        fileUrl: item.fileUrl || '',
        submitTime: item.submitTime || '',
        submitContent: item.submitContent || '',
        content: item.submitContent || '',
        corrected: item.corrected || 0,
        correctionContent: item.correctionContent || '',
        score: item.score || 0,
        aiLoading: false
      }))
      if (tableData.value.length === 0) {
        ElMessage.info('该作业暂无学生提交')
      }
    } else {
      ElMessage.error(res.msg || '加载提交记录失败')
    }
  } catch (error) {
    console.error('加载提交记录失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    listLoading.value = false
  }
}

// 打开批阅弹窗
const openCorrectDialog = (row) => {
  correctForm.value = {
    submitId: row.id,
    studentName: row.studentName,
    submitContent: row.submitContent || '',
    fileUrl: row.fileUrl || '',
    fileName: row.fileName || '',
    score: row.score || 60,
    correctionContent: row.correctionContent || ''
  }
  correctDialogVisible.value = true
}

// 提交批阅
const submitCorrect = async () => {
  if (correctForm.value.score === undefined || correctForm.value.score === null) {
    ElMessage.warning('请输入分数')
    return
  }

  correctLoading.value = true
  try {
    const data = {
      submitId: correctForm.value.submitId,
      score: correctForm.value.score,
      correctionContent: correctForm.value.correctionContent || '无评语'  // ✅ 只传评语，不加分数
    }
    console.log('批阅提交数据:', data)

    const res = await checkHomework(data)
    if (res.code === 200) {
      ElMessage.success('批阅成功！')
      correctDialogVisible.value = false
      await loadSubmissions()
    } else {
      ElMessage.error(res.msg || '批阅失败')
    }
  } catch (error) {
    console.error('批阅失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    correctLoading.value = false
  }
}

// 查看详情
const viewDetail = (row) => {
  detailData.value = { ...row }
  detailDialogVisible.value = true
}

// 下载文件
const downloadFile = (url) => {
  if (!url) {
    ElMessage.warning('无附件可下载')
    return
  }
  window.open(url, '_blank')
}

// 过滤列表
const filterList = () => {
  // computed 自动处理
}

// AI评价
const aiEval = async (row) => {
  if (!row.content) {
    ElMessage.warning('没有作业内容，无法进行AI评价')
    return
  }

  currentAiRow.value = row
  row.aiLoading = true
  try {
    const res = await aiEvaluate({
      content: row.content,
      prompt: '请从完成度、格式规范、代码质量三个维度给出修改建议，并给出一个建议分数（满分100分），语气要鼓励为主。'
    })
    if (res.code === 200) {
      aiText.value = res.data
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || 'AI 生成失败')
    }
  } catch (error) {
    console.error('AI评价失败:', error)
    ElMessage.error('网络异常，请稍后再试')
  } finally {
    row.aiLoading = false
  }
}

// 采纳AI评语
const applyAiComment = () => {
  if (currentAiRow.value) {
    // 解析AI返回的内容，提取分数和评语
    const text = aiText.value
    // 简单提取：尝试匹配数字分数
    const scoreMatch = text.match(/(\d+)\s*分/)
    const score = scoreMatch ? parseInt(scoreMatch[1]) : 60
    const comment = text.replace(/分数[：:]\s*\d+\s*分[，,、\s]*/, '').trim()

    correctForm.value = {
      submitId: currentAiRow.value.id,
      studentName: currentAiRow.value.studentName,
      submitContent: currentAiRow.value.submitContent || '',
      fileUrl: currentAiRow.value.fileUrl || '',
      fileName: currentAiRow.value.fileName || '',
      score: Math.min(Math.max(score, 0), 100),
      correctionContent: comment || text
    }
    correctDialogVisible.value = true
    dialogVisible.value = false
    ElMessage.success('已采纳AI评语，请确认后提交')
  }
}

onMounted(() => {
  loadHomeworkList()
})
</script>

<style scoped>
.check-wrap { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; }
.ai-result-box { background: #f9fafb; padding: 20px; border-radius: 8px; min-height: 100px; white-space: pre-wrap; }
.content-box { background: #f5f7fa; padding: 12px; border-radius: 8px; min-height: 50px; white-space: pre-wrap; word-break: break-all; }
</style>