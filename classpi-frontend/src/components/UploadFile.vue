<template>
  <el-upload
      action="http://localhost:8080/api/file/upload"
      :auto-upload="true"
      ref="uploadRef"
      v-model:file-list="fileList"
      :on-success="handleSuccess"
      :on-error="handleError"
      :limit="1"
      :on-exceed="handleExceed"
  >
    <el-button type="primary">
      <el-icon><Upload /></el-icon> 选择文件
    </el-button>
    <template #tip>
      <div class="el-upload__tip">
        支持 PDF、Word、Zip 格式，文件大小不超过 50MB
      </div>
    </template>
  </el-upload>
</template>

<script setup>
import { ref, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const uploadRef = ref(null)
const fileList = ref([])
const emit = defineEmits(['change'])

// 上传成功
const handleSuccess = (response, file) => {
  console.log('上传成功:', response)
  if (response.code === 200) {
    // 构建文件对象，包含 url
    const fileData = {
      name: response.data.fileName || file.name,
      url: response.data.fileUrl || '',
      type: response.data.fileType || '',
      size: file.size,
      raw: file.raw
    }
    fileList.value = [fileData]
    emit('change', fileList.value)
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传失败
const handleError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('文件上传失败，请重试')
}

// 超出限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}
</script>

<style scoped>
:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
}

:deep(.el-upload__tip) {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style>