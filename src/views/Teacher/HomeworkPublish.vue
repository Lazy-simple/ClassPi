<template>
  <el-card title="发布新作业" style="max-width:900px;margin:0 auto;">
    <el-form :model="form" label-width="100px">
      <el-form-item label="作业标题">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="作业内容">
        <el-input v-model="form.content" type="textarea" rows="6"></el-input>
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker v-model="form.deadline" type="datetime"></el-date-picker>
      </el-form-item>
      <el-form-item label="附件">
        <UploadFile @change="handleFile"></UploadFile>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布作业</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import { publishHomework } from '@/api/homework'
import UploadFile from '@/components/UploadFile.vue'
import { ElMessage } from 'element-plus'
const form = ref({title:'',content:'',deadline:''})
const fileList = ref([])
const handleFile = (files)=> fileList.value = files
const submit = async ()=>{
  await publishHomework(form.value)
  ElMessage.success('发布成功')
}
</script>