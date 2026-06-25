<template>
  <div class="page-wrap">
    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <h2>课程资料管理</h2>
          <div class="btn-group">
            <el-button type="primary" @click="openFolderDialog">新建文件夹</el-button>
            <el-button @click="openLinkDialog">添加外链资源</el-button>
          </div>
        </div>
      </template>

      <el-empty description="暂无资料数据"></el-empty>
    </el-card>

    <el-dialog v-model="folderDialog" title="新建文件夹" width="400">
      <el-input v-model="form.folderName" placeholder="文件夹名称" />
      <template #footer>
        <el-button @click="folderDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFolder">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="linkDialog" title="添加网络外链" width="500">
      <el-input v-model="form.linkName" placeholder="链接名称" />
      <el-input v-model="form.linkUrl" placeholder="https://xxx.com" style="margin-top:10px" />
      <template #footer>
        <el-button @click="linkDialog = false">取消</el-button>
        <el-button type="primary" @click="submitLink">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
const folderDialog = ref(false)
const linkDialog = ref(false)
const form = ref({
  folderName: '',
  linkName: '',
  linkUrl: ''
})

const openFolderDialog = () => {
  form.value.folderName = ''
  folderDialog.value = true
}
const submitFolder = () => {
  ElMessage.success('文件夹创建成功（接口未联调）')
  folderDialog.value = false
}
const openLinkDialog = () => {
  form.value.linkName = ''
  form.value.linkUrl = ''
  linkDialog.value = true
}
const submitLink = () => {
  ElMessage.success('外链添加成功（接口未联调）')
  linkDialog.value = false
}
</script>

<style scoped>
.page-wrap { padding: 20px 40px; }
.header-row { display: flex; justify-content: space-between; align-items: center; gap: 15px; }
.btn-group { display: flex; gap: 10px; }
</style>