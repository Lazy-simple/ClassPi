<template>
  <div class="page-wrap">
    <el-card shadow="hover">
      <template #header>
        <h2>课程资料（仅可下载）</h2>
      </template>
      <el-tree :data="treeData" node-key="id" default-expand-all>
        <template #default="{ node }">
          <span>{{ node.data.resourceName }}</span>
          <el-button
              v-if="node.data.resourceType !== 1"
              size="small"
              type="primary"
              link
              @click="download(node.data)"
          >下载</el-button>
          <el-tag v-if="node.data.resourceType === 3" size="small" type="info">外链</el-tag>
        </template>
      </el-tree>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getResourceTree, downloadResource } from '@/api/resource'
const route = useRoute()
const courseId = ref(Number(route.query.courseId))
const treeData = ref([])
const loadTree = async () => {
  const res = await getResourceTree(courseId.value)
  if (res.code === 200) treeData.value = res.data
}
const download = (row) => {
  downloadResource(row.id)
}
onMounted(loadTree)
</script>

<style scoped>
.page-wrap { padding: 20px 40px; }
</style>