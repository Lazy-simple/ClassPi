<template>
  <el-upload
    action="#"
    :auto-upload="false"
    ref="uploadRef"
    v-model:file-list="fileList"
  >
    <el-button type="primary">点击上传文件</el-button>
  </el-upload>
</template>
<script setup>
import { ref, defineEmits } from 'vue'
const uploadRef = ref(null)
const fileList = ref([])
const emit = defineEmits(['change'])
function handleChange(file) {
  emit('change', fileList.value)
}
</script>