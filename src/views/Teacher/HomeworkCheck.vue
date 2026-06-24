<template>
  <div class="check-wrap">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>📝 作业批阅列表</span>
          <el-input placeholder="搜索学生姓名" prefix-icon="Search" style="width: 200px;" />
        </div>
      </template>

      <el-table :data="tableData" border stripe style="width: 100%" v-loading="listLoading">
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="fileName" label="作业附件">
          <template #default="scope">
            <el-link type="primary" :underline="false" icon="Paperclip">{{ scope.row.fileName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button size="small" plain>查看详情</el-button>
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { aiEvaluate } from '@/api/ai'
import AiComment from '@/components/AiComment.vue'
import { ElMessage } from 'element-plus'

// 模拟数据，实际请替换为你的API获取逻辑
const tableData = ref([
  { id: 1, studentName: '张三', fileName: 'Java实验报告.docx', submitTime: '2023-10-24 10:00', content: '这是张三的作业内容...' },
  { id: 2, studentName: '李四', fileName: 'Web前端设计.pdf', submitTime: '2023-10-24 11:30', content: '这是李四的作业内容...' },
])

const listLoading = ref(false) // 列表加载状态
const dialogVisible = ref(false)
const aiText = ref('')

const aiEval = async (row) => {
  // 设置当前行的 loading 状态
  row.aiLoading = true
  try {
    const res = await aiEvaluate({
      content: row.content, // 确保后端能接收到作业内容
      prompt: '请从完成度、格式规范、代码质量三个维度给出修改建议，语气要鼓励为主。'
    })
    if (res.code === 200) {
      aiText.value = res.data
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || 'AI 生成失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后再试')
  } finally {
    row.aiLoading = false
  }
}
</script>

<style scoped>
.check-wrap { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.ai-result-box { background: #f9fafb; padding: 20px; border-radius: 8px; min-height: 100px; }
</style>