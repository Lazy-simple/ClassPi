<template>
  <el-card title="作业提交" style="max-width:800px;margin:0 auto;">
    <el-form :model="form">
      <el-form-item label="作业附件">
        <UploadFile @change="fileChange" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import { submitHomework } from '@/api/homework'
import UploadFile from '@/components/UploadFile.vue'
import { ElMessage } from 'element-plus'
const form = ref({remark:''})
const files = ref([])
const fileChange = (list)=> files.value = list
const submit = async ()=>{
  await submitHomework(form.value)
  ElMessage.success('提交成功')
}
</script>