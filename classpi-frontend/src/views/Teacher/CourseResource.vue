<template>
  <div class="page-wrap">
    <el-card shadow="hover">
      <template #header>
        <div class="header-row">
          <h2>课程资料管理</h2>
          <div class="course-selector">
            <el-select v-model="selectedCourseId" placeholder="请选择课程" @change="onCourseChange">
              <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
            </el-select>
          </div>
          <div class="btn-group">
            <el-button type="primary" @click="openFolderDialog" :disabled="!selectedCourseId">新建文件夹</el-button>
            <el-button type="success" @click="openUploadDialog" :disabled="!selectedCourseId">上传文件</el-button>
            <el-button @click="openLinkDialog" :disabled="!selectedCourseId">添加外链资源</el-button>
            <el-button type="warning" @click="openPreparationDialog" :disabled="!selectedCourseId">
              从备课区导入
            </el-button>
          </div>
        </div>
      </template>

      <!-- 面包屑导航 -->
      <div class="breadcrumb" v-if="breadcrumbs.length > 0">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item @click="goHome">根目录</el-breadcrumb-item>
          <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.id" @click="goToFolder(item)">
            {{ item.name || item.folderName }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 资源列表（暂无数据时显示空状态） -->
      <div v-if="resourceList.length === 0">
        <el-empty description="暂无资料数据"></el-empty>
      </div>
      <div v-else class="resource-list">
        <!-- 资源列表 -->
        <div class="resource-item" v-for="item in resourceList" :key="item.id">
          <div class="resource-icon">
            <el-icon v-if="isFolder(item)" :size="24" color="#409EFF">
              <Folder />
            </el-icon>
            <el-icon v-else-if="item.type === 'link'" :size="24" color="#67C23A">
              <Link />
            </el-icon>
            <el-icon v-else :size="24" color="#909399">
              <Document />
            </el-icon>
          </div>
          <div class="resource-info">
            <div class="resource-name">{{ item.name || item.folderName || '未命名' }}</div>
            <div class="resource-meta">
              <span>{{ getResourceTypeText(item) }}</span>
              <span v-if="item.fileSize">{{ formatFileSize(item.fileSize) }}</span>
              <span>{{ item.uploaderName }}</span>
            </div>
          </div>
          <div class="resource-actions">
            <el-button size="small" v-if="isFolder(item)" @click="openFolder(item)">打开</el-button>
            <el-button size="small" v-if="item.type === 'link'" @click="openLink(item)">访问</el-button>
            <el-button size="small" v-if="isFile(item)" @click="downloadFile(item)">下载</el-button>
            <el-button size="small" type="danger" @click="deleteResourceItem(item.id)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新建文件夹弹窗 -->
    <el-dialog v-model="folderDialog" title="新建文件夹" width="400">
      <el-input
          v-model="form.folderName"
          placeholder="文件夹名称"
          @input="validateFolderName"
      />
      <template #footer>
        <el-button @click="folderDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFolder" :disabled="!form.folderName">确认</el-button>
      </template>
    </el-dialog>

    <!-- 添加外链弹窗 -->
    <el-dialog v-model="linkDialog" title="添加网络外链" width="500">
      <el-input
          v-model="form.linkName"
          placeholder="链接名称"
          @input="validateLinkName"
      />
      <el-input
          v-model="form.linkUrl"
          placeholder="https://xxx.com"
          style="margin-top:10px"
          @input="validateLinkUrl"
      />
      <template #footer>
        <el-button @click="linkDialog = false">取消</el-button>
        <el-button
            type="primary"
            @click="submitLink"
            :disabled="!form.linkName || !form.linkUrl"
        >
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- 上传文件弹窗 -->
    <el-dialog v-model="uploadDialog" title="上传文件" width="500">
      <el-upload
          ref="uploadRef"
          :action="uploadAction"
          :data="uploadData"
          :headers="uploadHeaders"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeUpload"
          :limit="1"
          v-model:file-list="fileList"
          :auto-upload="false"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
            支持 Word、PDF、PPT、Excel、图片等文件，单个文件不超过 50MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="uploadDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :disabled="fileList.length === 0">
          开始上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 从备课区导入弹窗 -->
    <el-dialog v-model="preparationDialog" title="从备课区导入" width="700px" top="5vh">
      <div class="preparation-selector">
        <el-tabs v-model="prepActiveType" @tab-change="loadPreparations">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="作业" name="homework" />
          <el-tab-pane label="话题" name="topic" />
          <el-tab-pane label="资料" name="resource" />
        </el-tabs>

        <el-table :data="prepList" v-loading="prepLoading" border max-height="350">
          <el-table-column label="标题" prop="title" min-width="150" />
          <el-table-column label="类型" prop="type" width="80">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.type)" size="small">
                {{ getTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="内容" prop="content" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="importPreparation(row)" :loading="row.importing">
                导入
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
            v-model:current-page="prepPage"
            v-model:page-size="prepPageSize"
            :total="prepTotal"
            layout="total, prev, pager, next"
            @current-change="loadPreparations"
            style="margin-top:10px;text-align:right;"
        />
      </div>
      <template #footer>
        <el-button @click="preparationDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Folder, Link, Document } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
// 引入接口方法
import { getResourceTree, createFolder, addLink, downloadResource, deleteResource } from '@/api/resource'
import request from '@/utils/request'

// 路由实例
const route = useRoute()
// 弹窗控制
const folderDialog = ref(false)
const linkDialog = ref(false)
const uploadDialog = ref(false)
const uploadRef = ref()
// 表单数据
const form = ref({
  folderName: '',
  linkName: '',
  linkUrl: ''
})
// 资源列表
const resourceList = ref([])
// 课程列表
const courseList = ref([])
// 选中的课程ID
const selectedCourseId = ref(null)
// 当前父文件夹ID
const currentParentId = ref('0')
// 面包屑导航
const breadcrumbs = ref([])
// 文件上传
const fileList = ref([])
const uploadAction = ref('http://localhost:8080/resource/upload')
const uploadData = ref({})
const uploadHeaders = ref({})
const preparationDialog = ref(false)
const prepActiveType = ref('all')
const prepList = ref([])
const prepLoading = ref(false)
const prepPage = ref(1)
const prepPageSize = ref(10)
const prepTotal = ref(0)

// 获取课程ID（从路由参数/存储中获取，需根据实际业务调整）
const getCourseId = () => {
  // 从路由参数获取
  let courseId = route.query.courseId || localStorage.getItem('currentCourseId')

  console.log('========== 获取 courseId ==========')
  console.log('route.query.courseId:', route.query.courseId)
  console.log('localStorage currentCourseId:', localStorage.getItem('currentCourseId'))

  // 如果获取到的是字符串，转为数字
  if (courseId) {
    const numId = Number(courseId)
    if (!isNaN(numId) && numId > 0) {
      console.log('最终 courseId:', numId)
      return numId
    }
  }

  // 如果没有有效的 courseId，尝试从用户已创建的课程中选择第一个
  console.warn('没有有效的 courseId，需要用户选择课程')
  return null
}

// 获取用户信息
const getUserInfo = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      return JSON.parse(userInfoStr)
    }
  } catch (e) {
    console.error('解析用户信息失败', e)
  }
  return null
}

// 获取教师课程列表
const getTeacherCourses = async () => {
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.id) {
    return []
  }
  try {
    const res = await fetch(`http://localhost:8080/course/teacher/${userInfo.id}`)
    const data = await res.json()
    if (data.code === 200 && data.data) {
      return data.data
    }
  } catch (e) {
    console.error('获取课程列表失败', e)
  }
  return []
}

// 初始化：获取资源列表
const initResourceList = async () => {
  // 确保 parentId 有默认值
  if (!currentParentId.value) {
    currentParentId.value = '0'
  }
  
  const courseId = selectedCourseId.value || getCourseId()
  console.log('========== 初始化资源列表 ==========')
  console.log('最终使用的 courseId:', courseId)
  console.log('当前 parentId:', currentParentId.value)

  if (!courseId || isNaN(courseId)) {
    resourceList.value = []
    return
  }

  try {
    const id = Number(courseId)
    const parentId = currentParentId.value || '0'
    console.log('请求参数 id:', id, 'parentId:', parentId)

    const res = await getResourceTree(id, 1, 10, parentId)
    console.log('返回数据:', res)

    if (res && res.code === 200) {
      resourceList.value = res.data || []
    } else if (res && Array.isArray(res.data)) {
      resourceList.value = res.data
    } else {
      resourceList.value = []
    }

    if (resourceList.value.length === 0) {
      console.log('暂无资料数据，当前目录:', parentId)
    }
  } catch (error) {
    console.error('请求失败:', error)
    ElMessage.error('获取资源列表失败')
  }
}

// 课程选择变化
const onCourseChange = (courseId) => {
  console.log('========== 课程选择变化 ==========')
  console.log('选中的 courseId:', courseId)
  
  if (courseId) {
    // 重置文件夹导航
    currentParentId.value = '0'
    breadcrumbs.value = []
    // 保存到 localStorage
    localStorage.setItem('currentCourseId', courseId)
    // 获取课程号
    const course = courseList.value.find(c => c.id === courseId)
    if (course) {
      localStorage.setItem('currentCourseNo', course.courseNo)
      console.log('保存课程号:', course.courseNo)
    }
    // 刷新资源列表
    initResourceList()
  }
}

// 加载课程列表
const loadCourseList = async () => {
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.id) {
    ElMessage.warning('请重新登录')
    return
  }
  try {
    const res = await fetch(`http://localhost:8080/course/teacher/${userInfo.id}`)
    const data = await res.json()
    if (data.code === 200 && data.data) {
      courseList.value = data.data
      console.log('========== 加载课程列表 ==========')
      console.log('课程数量:', courseList.value.length)
      
      // 如果只有一门课程，自动选中
      if (courseList.value.length === 1) {
        selectedCourseId.value = courseList.value[0].id
        onCourseChange(selectedCourseId.value)
      }
    }
  } catch (e) {
    console.error('加载课程列表失败', e)
    ElMessage.error('加载课程列表失败')
  }
}

// 打开新建文件夹弹窗
const openFolderDialog = () => {
  form.value.folderName = ''
  folderDialog.value = true
}

// 校验文件夹名称
const validateFolderName = () => {
  form.value.folderName = form.value.folderName.trim()
}

// 提交新建文件夹
const submitFolder = async () => {
  const courseId = selectedCourseId.value || getCourseId()
  if (!courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  const folderName = form.value.folderName.trim()
  if (!folderName) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.id) {
    ElMessage.warning('请重新登录')
    return
  }
  
  // 获取当前课程的课程号
  const courseNo = localStorage.getItem('currentCourseNo')
  if (!courseNo) {
    ElMessage.warning('请先选择课程')
    return
  }
  
  const loading = ElMessage({ message: '创建中...', type: 'warning', duration: 0 })
  
  try {
    console.log('========== 创建文件夹 ==========')
    console.log('courseId:', courseId, 'type:', typeof courseId)
    console.log('courseNo:', courseNo)
    console.log('folderName:', folderName)
    console.log('uploaderId:', userInfo.id, 'type:', typeof userInfo.id)
    console.log('uploaderName:', userInfo.name || userInfo.username)
    
    const res = await createFolder({
      courseId: Number(courseId),
      courseNo: String(courseNo),
      folderName: String(folderName),
      parentId: '',
      uploaderId: String(userInfo.id),
      uploaderName: String(userInfo.name || userInfo.username)
    })
    console.log('创建结果:', res)
    
    loading.close()
    if (res && res.code === 200) {
      ElMessage.success('文件夹创建成功')
      folderDialog.value = false
      form.value.folderName = ''
      initResourceList()
    } else {
      ElMessage.error(res?.message || '创建文件夹失败')
    }
  } catch (error) {
    loading.close()
    console.error('创建文件夹失败:', error)
    ElMessage.error('创建文件夹失败：' + (error.message || '接口异常'))
  }
}

// 打开添加外链弹窗
const openLinkDialog = () => {
  form.value.linkName = ''
  form.value.linkUrl = ''
  linkDialog.value = true
}

// 校验链接名称
const validateLinkName = () => {
  form.value.linkName = form.value.linkName.trim()
}

// 校验链接URL
const validateLinkUrl = () => {
  form.value.linkUrl = form.value.linkUrl.trim()
}

// 提交添加外链
const submitLink = async () => {
  const courseId = selectedCourseId.value || getCourseId()
  if (!courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  const linkName = form.value.linkName.trim()
  const linkUrl = form.value.linkUrl.trim()
  if (!linkName || !linkUrl) {
    ElMessage.warning('请填写链接名称和地址')
    return
  }
  // 校验URL
  try {
    new URL(linkUrl)
  } catch (e) {
    ElMessage.warning('请输入有效的HTTPS/HTTP链接')
    return
  }
  
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.id) {
    ElMessage.warning('请重新登录')
    return
  }
  
  // 获取当前课程的课程号
  const courseNo = localStorage.getItem('currentCourseNo')
  if (!courseNo) {
    ElMessage.warning('请先选择课程')
    return
  }
  
  const loading = ElMessage({ message: '添加中...', type: 'warning', duration: 0 })
  
  try {
    console.log('========== 添加外链 ==========')
    const res = await addLink({
      courseId: Number(courseId),
      courseNo: String(courseNo),
      name: String(linkName),
      url: String(linkUrl),
      parentId: '',
      uploaderId: String(userInfo.id),
      uploaderName: String(userInfo.name || userInfo.username)
    })
    console.log('添加结果:', res)
    
    loading.close()
    if (res && res.code === 200) {
      ElMessage.success('外链添加成功')
      linkDialog.value = false
      initResourceList()
    } else {
      ElMessage.error(res?.message || '添加外链失败')
    }
  } catch (error) {
    loading.close()
    console.error('添加外链失败:', error)
    ElMessage.error('添加外链失败：' + (error.message || '接口异常'))
  }
}

// 新增删除方法
const deleteResourceItem = async (resId) => {
  console.log('========== 删除资源 ==========')
  console.log('资源ID:', resId)
  
  try {
    await ElMessageBox.confirm('确定删除该资源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    console.log('用户确认删除，准备发送请求')
    const res = await deleteResource(resId)
    console.log('删除响应:', res)
    
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      // 返回根目录并刷新
      goHome()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除错误:', error)
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.msg || error.message || '接口异常'))
    }
  }
}

// 打开文件夹
const openFolder = (item) => {
  console.log('打开文件夹:', item)
  if (item.id) {
    currentParentId.value = String(item.id)
    breadcrumbs.value.push(item)
    initResourceList()
  }
}

// 返回根目录
const goHome = () => {
  currentParentId.value = '0'
  breadcrumbs.value = []
  initResourceList()
}

// 跳转到指定文件夹
const goToFolder = (item) => {
  const index = breadcrumbs.value.findIndex(b => b.id === item.id)
  if (index !== -1) {
    breadcrumbs.value = breadcrumbs.value.slice(0, index + 1)
    currentParentId.value = String(item.id)
    initResourceList()
  }
}

// 访问外链
const openLink = (item) => {
  if (item.url) {
    window.open(item.url, '_blank')
  } else {
    ElMessage.warning('链接地址无效')
  }
}

// 下载文件
const downloadFile = (item) => {
  if (item.id) {
    downloadResource(item.id)
  } else {
    ElMessage.warning('文件ID无效')
  }
}

// 判断是否是文件夹
const isFolder = (item) => {
  if (item.type === 'folder') return true
  if (item.isFolder === 1) return true
  if (item.folderName && !item.type) return true
  return false
}

// 判断是否是文件
const isFile = (item) => {
  if (item.type === 'file') return true
  if (item.isFolder === 0 && item.type !== 'link') return true
  return false
}

// 获取资源类型文本
const getResourceTypeText = (item) => {
  if (isFolder(item)) return '文件夹'
  if (item.type === 'link') return '外链'
  if (isFile(item)) return '文件'
  return '未知'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 打开上传弹窗
const openUploadDialog = () => {
  const userInfo = getUserInfo()
  const courseNo = localStorage.getItem('currentCourseNo')
  
  if (!userInfo || !userInfo.id) {
    ElMessage.warning('请重新登录')
    return
  }
  
  if (!selectedCourseId.value || !courseNo) {
    ElMessage.warning('请先选择课程')
    return
  }
  
  // 设置上传请求头
  uploadHeaders.value = {
    Authorization: localStorage.getItem('token') || '',
    userId: String(userInfo.id)
  }
  
  fileList.value = []
  uploadDialog.value = true
}

// 上传前校验
const beforeUpload = (file) => {
  const maxSize = 50 * 1024 * 1024 // 50MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

// 提交上传
const submitUpload = async () => {
  const userInfo = getUserInfo()
  const courseNo = localStorage.getItem('currentCourseNo')
  
  uploadData.value = {
    courseId: Number(selectedCourseId.value),
    courseNo: String(courseNo),
    parentId: currentParentId.value,
    uploaderId: String(userInfo.id),
    uploaderName: String(userInfo.name || userInfo.username)
  }
  
  console.log('========== 上传文件 ==========')
  console.log('上传参数:', uploadData.value)
  console.log('当前目录:', currentParentId.value)
  
  uploadRef.value.submit()
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  console.log('上传成功:', response)
  if (response.code === 200) {
    ElMessage.success('文件上传成功')
    uploadDialog.value = false
    fileList.value = []
    initResourceList()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = (error, file) => {
  console.error('上传失败:', error)
  ElMessage.error('文件上传失败')
}

// 获取备课区内容列表
const getPreparations = async (type = 'all', page = 1, pageSize = 10) => {
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.id) return

  const url = type === 'all'
      ? `http://localhost:8080/preparation/teacher/${userInfo.id}`
      : `http://localhost:8080/preparation/teacher/${userInfo.id}/type/${type}`

  try {
    const res = await fetch(`${url}?page=${page}&pageSize=${pageSize}`)
    const data = await res.json()
    if (data.code === 200 && data.data) {
      prepList.value = data.data.records || []
      prepTotal.value = data.data.total || 0
    }
  } catch (e) {
    console.error('加载备课区失败:', e)
  }
}

// 加载备课区
const loadPreparations = () => {
  prepLoading.value = true
  getPreparations(prepActiveType.value, prepPage.value, prepPageSize.value).finally(() => {
    prepLoading.value = false
  })
}

// 打开备课区导入弹窗
const openPreparationDialog = () => {
  prepPage.value = 1
  prepActiveType.value = 'all'
  prepList.value = []
  prepTotal.value = 0
  preparationDialog.value = true
  loadPreparations()
}

// 获取类型标签颜色
const getTypeTag = (type) => {
  const map = { homework: 'primary', topic: 'warning', resource: 'success' }
  return map[type] || 'info'
}

// 获取类型显示文字
const getTypeLabel = (type) => {
  const map = { homework: '作业', topic: '话题', resource: '资料' }
  return map[type] || type
}

// 导入备课内容到课程资源
const importPreparation = async (row) => {
  row.importing = true
  try {
    const userInfo = getUserInfo()
    const courseNo = localStorage.getItem('currentCourseNo')
    const parentId = currentParentId.value || '0'  // ✅ 取当前目录

    let resData
    if (row.type === 'homework') {
      resData = await request({
        url: '/preparation/import/homework',
        method: 'post',
        data: {
          preparationId: row.id,
          courseId: selectedCourseId.value,
          courseNo: courseNo,
          parentId: parentId,  // ✅ 加这行
          uploaderId: String(userInfo.id),
          uploaderName: String(userInfo.name || userInfo.username)
        }
      })
    } else if (row.type === 'resource') {
      resData = await request({
        url: '/preparation/import/resource',
        method: 'post',
        data: {
          preparationId: row.id,
          courseId: selectedCourseId.value,
          courseNo: courseNo,
          parentId: parentId,  // ✅ 加这行
          uploaderId: String(userInfo.id),
          uploaderName: String(userInfo.name || userInfo.username)
        }
      })
    } else if (row.type === 'topic') {
      resData = await request({
        url: '/preparation/import/topic',
        method: 'post',
        data: {
          preparationId: row.id,
          courseId: selectedCourseId.value,
          courseNo: courseNo,
          parentId: parentId,  // ✅ 加这行
          uploaderId: String(userInfo.id),
          uploaderName: String(userInfo.name || userInfo.username)
        }
      })
    }

    if (resData && resData.code === 200) {
      ElMessage.success('导入成功')
      initResourceList()
      loadPreparations()
    } else {
      ElMessage.error(resData?.message || '导入失败')
    }
  } catch (e) {
    console.error('导入失败:', e)
    ElMessage.error('导入失败')
  } finally {
    row.importing = false
  }
}

// 页面挂载时初始化
onMounted(() => {
  loadCourseList()
})
</script>

<style scoped>
.page-wrap { padding: 20px 40px; }
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}
.course-selector {
  min-width: 200px;
}
.btn-group { display: flex; gap: 10px; }
.resource-list {
  margin-top: 20px;
  padding: 10px;
}
.resource-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  margin-bottom: 8px;
  transition: background-color 0.2s;
}
.resource-item:hover {
  background-color: #f5f7fa;
}
.resource-icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
}
.resource-info {
  flex: 1;
}
.resource-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
.resource-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.resource-meta span {
  margin-right: 8px;
}
.resource-actions {
  display: flex;
  gap: 8px;
}

.preparation-selector {
  padding: 5px 0;
}
</style>