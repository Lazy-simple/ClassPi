<template>
  <div class="page-wrap">
    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <h2>📚 课程资料</h2>
          <el-tag type="info">仅可下载</el-tag>
        </div>
      </template>

      <!-- 资源列表 -->
      <div v-if="treeData.length === 0">
        <el-empty description="暂无资料数据"></el-empty>
      </div>
      <div v-else class="resource-list">
        <div
            v-for="item in treeData"
            :key="item.id"
            class="resource-item"
            :class="{ 'is-folder': item.isFolder === 1 }"
        >
          <!-- 左侧：图标 + 名称 -->
          <div class="resource-info">
            <!-- 根据类型显示不同图标 -->
            <el-icon class="resource-icon" :size="24">
              <Folder v-if="item.isFolder === 1" />
              <Document v-else-if="item.type === 'file'" />
              <Link v-else-if="item.type === 'link'" />
              <DocumentCopy v-else />
            </el-icon>

            <span class="resource-name">{{ item.folderName || item.name }}</span>

            <!-- 文件类型标签 -->
            <el-tag v-if="item.type === 'link'" size="small" type="success">外链</el-tag>
            <el-tag v-else-if="item.type === 'file'" size="small" type="primary">
              {{ getFileType(item.name) }}
            </el-tag>
            <el-tag v-else-if="item.isFolder === 1" size="small" type="warning">文件夹</el-tag>

            <!-- 文件大小 -->
            <span v-if="item.fileSize" class="file-size">
              {{ formatFileSize(item.fileSize) }}
            </span>
          </div>

          <!-- 右侧：操作按钮 -->
          <div class="resource-actions">
            <!-- 上传人信息 -->
            <span class="uploader-info">
              <el-icon><User /></el-icon>
              {{ item.uploaderName || '未知' }}
            </span>

            <!-- 下载按钮（仅文件可下载） -->
            <el-button
                v-if="item.isFolder !== 1 && item.type !== 'link'"
                type="primary"
                size="small"
                @click="download(item)"
                :icon="Download"
            >
              下载
            </el-button>

            <!-- 外链跳转按钮 -->
            <el-button
                v-if="item.type === 'link'"
                type="success"
                size="small"
                @click="openLink(item)"
                :icon="Link"
            >
              访问链接
            </el-button>

            <!-- 文件夹展开提示 -->
            <el-tag v-if="item.isFolder === 1" size="small" type="info">
              <el-icon><ArrowRight /></el-icon> 点击展开
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Folder,
  Document,
  Link,
  DocumentCopy,
  User,
  Download,
  ArrowRight
} from '@element-plus/icons-vue'
import { getResourceTree, downloadResource } from '@/api/resource'

const route = useRoute()

// 获取 courseId
const getCourseId = () => {
  let id = route.query.courseId || localStorage.getItem('currentCourseId')
  if (!id) {
    console.warn('没有 courseId，使用默认值 1')
    return 1
  }
  return Number(id)
}

const courseId = ref(getCourseId())
const treeData = ref([])

// 加载资源列表
const loadTree = async () => {
  console.log('========== 加载资源树 ==========')
  console.log('courseId:', courseId.value)

  try {
    const res = await getResourceTree(courseId.value)
    console.log('返回数据:', res)

    if (res.code === 200) {
      treeData.value = res.data || []
      if (treeData.value.length === 0) {
        ElMessage.info('暂无资料数据')
      }
    } else {
      ElMessage.error(res.message || '获取资源失败')
    }
  } catch (error) {
    console.error('加载资源失败:', error)
    ElMessage.error('获取资源列表失败')
  }
}

// 下载文件
const download = (row) => {
  if (row.type === 'link') {
    window.open(row.path)
    return
  }
  downloadResource(row.id)
}

// 打开外链
const openLink = (item) => {
  if (item.path) {
    window.open(item.path, '_blank')
  } else {
    ElMessage.warning('链接地址无效')
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return ''
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + sizes[i]
}

// 获取文件类型
const getFileType = (fileName) => {
  if (!fileName) return '文件'
  const ext = fileName.split('.').pop().toLowerCase()
  const typeMap = {
    'pdf': 'PDF',
    'doc': 'Word',
    'docx': 'Word',
    'xls': 'Excel',
    'xlsx': 'Excel',
    'ppt': 'PPT',
    'pptx': 'PPT',
    'jpg': '图片',
    'jpeg': '图片',
    'png': '图片',
    'gif': '图片',
    'mp4': '视频',
    'avi': '视频',
    'zip': '压缩包',
    'rar': '压缩包',
    '7z': '压缩包'
  }
  return typeMap[ext] || ext.toUpperCase()
}

onMounted(loadTree)
</script>

<style scoped>
.page-wrap {
  padding: 20px 40px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resource-list {
  margin-top: 10px;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  background: #fff;
}

.resource-item:hover {
  background: #f5f7fa;
  border-color: #409eff;
  transform: translateX(4px);
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.resource-item.is-folder {
  background: #f8f9fa;
  border-color: #e6a23c;
}

.resource-item.is-folder:hover {
  background: #fdf6ec;
  border-color: #e6a23c;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.resource-icon {
  color: #409eff;
  flex-shrink: 0;
}

.resource-item.is-folder .resource-icon {
  color: #e6a23c;
}

.resource-name {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}

.resource-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.uploader-info {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-tag) {
  font-size: 12px;
}
</style>