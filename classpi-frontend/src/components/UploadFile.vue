<template>
  <el-upload
      action="#"
      :auto-upload="false"
      ref="uploadRef"
      v-model:file-list="fileList"
      :on-change="handleChange"
      :on-remove="handleRemove"
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

// 文件变化时触发
const handleChange = (file, fileListData) => {
  fileList.value = fileListData
  emit('change', fileList.value)
}

// 文件移除时触发
const handleRemove = (file, fileListData) => {
  fileList.value = fileListData
  emit('change', fileList.value)
}

// 超出限制时触发
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件')
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