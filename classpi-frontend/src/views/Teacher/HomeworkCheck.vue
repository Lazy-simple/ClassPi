<template>
  <div class="check-wrap">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>📝 作业批阅列表</span>
          <el-input placeholder="搜索学生姓名" prefix-icon="Search" style="width: 200px;" v-model="searchKeyword" @input="filterList" />
        </div>
      </template>

      <el-table :data="filteredTableData" border stripe style="width: 100%" v-loading="listLoading">
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="fileName" label="作业附件">
          <template #default="scope">
            <el-link type="primary" :underline="false" @click="downloadFile(scope.row.fileUrl)">
              {{ scope.row.fileName || '无附件' }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button size="small" plain @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button
                size="small"
                type="primary"
                plain
                :loading="scope.row.aiLoading"
                @click="aiEval(scope.row)"
            >
              ✨ AI 智能评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- AI 评价弹窗 -->
    <el-dialog v-model="dialogVisible" title="AI 助教评语" width="50%">
      <div class="ai-result-box">
        <AiComment :ai-content="aiText" />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="dialogVisible = false">采纳评语</el-button>
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
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { aiEvaluate } from '@/api/ai'
import AiComment from '@/components/AiComment.vue'
import { ElMessage } from 'element-plus'
import { getHomeworkSubmissions } from '@/api/homework'
import { useRoute } from 'vue-router'

const route = useRoute()
const listLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const aiText = ref('')
const searchKeyword = ref('')
const tableData = ref([])
const detailData = ref({})

// 过滤后的数据
const filteredTableData = computed(() => {
  if (!searchKeyword.value) return tableData.value
  return tableData.value.filter(item =>
      item.studentName?.includes(searchKeyword.value)
  )
})

// 加载提交记录 - 使用固定的 homeworkId
const loadSubmissions = async () => {
  // 从路由参数获取 homeworkId，如果没有则使用默认值 1
  const homeworkId = route.query.homeworkId || 1

  listLoading.value = true
  try {
    const res = await getHomeworkSubmissions(homeworkId)
    if (res.code === 200) {
      tableData.value = (res.data || []).map(item => ({
        id: item.id,
        studentName: item.studentName || '未知学生',
        fileName: item.fileName || '无附件',
        fileUrl: item.fileUrl || '',
        submitTime: item.submitTime || '',
        submitContent: item.submitContent || '',
        content: item.submitContent || '',
        aiLoading: false,
        corrected: item.corrected || 0
      }))
      if (tableData.value.length === 0) {
        ElMessage.info('暂无学生提交作业')
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

// 查看详情
const viewDetail = (row) => {
  detailData.value = row
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

const aiEval = async (row) => {
  if (!row.content) {
    ElMessage.warning('没有作业内容，无法进行AI评价')
    return
  }

  row.aiLoading = true
  try {
    const res = await aiEvaluate({
      content: row.content,
      prompt: '请从完成度、格式规范、代码质量三个维度给出修改建议，语气要鼓励为主。'
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

onMounted(() => {
  loadSubmissions()
})
</script>

<style scoped>
.check-wrap { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.ai-result-box { background: #f9fafb; padding: 20px; border-radius: 8px; min-height: 100px; }
</style>