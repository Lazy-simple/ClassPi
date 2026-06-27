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

      <!-- 资源列表（暂无数据时显示空状态） -->
      <div v-if="resourceList.length === 0">
        <el-empty description="暂无资料数据"></el-empty>
      </div>
      <div v-else class="resource-list">
        <!-- 这里可扩展渲染资源树/列表，示例仅做占位 -->
        <div class="resource-item" v-for="item in resourceList" :key="item.id">
          {{ item.name }} - {{ item.type }}
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
// 引入接口方法
import { getResourceTree, createFolder, addLink } from '@/api/resource'

// 路由实例
const route = useRoute()
// 弹窗控制
const folderDialog = ref(false)
const linkDialog = ref(false)
// 表单数据
const form = ref({
  folderName: '',
  linkName: '',
  linkUrl: ''
})
// 资源列表
const resourceList = ref([])

// 获取课程ID（从路由参数/存储中获取，需根据实际业务调整）
const getCourseId = () => {
  // 从路由参数获取
  const courseId = route.query.courseId || localStorage.getItem('currentCourseId')

  console.log('========== 获取 courseId ==========')
  console.log('route.query.courseId:', route.query.courseId)
  console.log('localStorage currentCourseId:', localStorage.getItem('currentCourseId'))
  console.log('最终 courseId:', courseId)
  console.log('courseId 类型:', typeof courseId)

  // 如果获取到的是字符串，转为数字
  if (courseId) {
    const numId = Number(courseId)
    if (!isNaN(numId)) {
      return numId
    }
  }

  // 如果没有有效的 courseId，使用默认值或提示
  console.warn('没有有效的 courseId，使用默认值 1')
  return 1  // 或者弹窗提示用户选择课程
}

// 初始化：获取资源列表
const initResourceList = async () => {
  const courseId = getCourseId()
  console.log('========== 初始化资源列表 ==========')
  console.log('最终使用的 courseId:', courseId)

  if (!courseId || isNaN(courseId)) {
    ElMessage.warning('请先选择课程')
    return
  }

  try {
    // 确保 courseId 是数字
    const id = Number(courseId)
    console.log('请求参数 id:', id)

    const res = await getResourceTree(id, 1, 10)
    console.log('返回数据:', res)

    // 处理返回数据
    if (res && res.code === 200) {
      resourceList.value = res.data || []
    } else if (res && Array.isArray(res.data)) {
      resourceList.value = res.data
    } else {
      resourceList.value = []
    }

    if (resourceList.value.length === 0) {
      ElMessage.info('暂无资料数据')
    }
  } catch (error) {
    console.error('请求失败:', error)
    ElMessage.error('获取资源列表失败')
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
  const courseId = getCourseId()
  if (!courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  const folderName = form.value.folderName.trim()
  if (!folderName) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  // 补充后端要求的参数（需从用户信息/路由中获取，示例用localStorage）
  const courseNo = localStorage.getItem('currentCourseNo') // 课程编号
  const uploaderId = localStorage.getItem('userId') // 上传人ID
  const uploaderName = localStorage.getItem('userName') // 上传人姓名
  if (!courseNo || !uploaderId || !uploaderName) {
    ElMessage.warning('缺少课程编号/用户信息，请重新登录')
    return
  }
  try {
    const loading = ElMessage.loading({ message: '创建中...', duration: 0 })
    // 传所有后端要求的参数
    await createFolder({
      courseId,
      courseNo,
      folderName,
      parentId: '', // 根文件夹可传空，根据业务调整
      uploaderId,
      uploaderName
    })
    loading.close()
    ElMessage.success('文件夹创建成功')
    folderDialog.value = false
    form.value.folderName = ''
    initResourceList()
  } catch (error) {
    const errMsg = error.msg || error.message || '接口异常'
    ElMessage.error('创建文件夹失败：' + errMsg)
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
  const courseId = getCourseId()
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
  // 补充后端要求的参数
  const courseNo = localStorage.getItem('currentCourseNo')
  const uploaderId = localStorage.getItem('userId')
  const uploaderName = localStorage.getItem('userName')
  if (!courseNo || !uploaderId || !uploaderName) {
    ElMessage.warning('缺少课程编号/用户信息，请重新登录')
    return
  }
  try {
    await addLink({
      courseId,
      courseNo, // 补充courseNo
      name: linkName, // 匹配后端DTO的name字段（不是linkName）
      url: linkUrl, // 匹配后端DTO的url字段（不是linkUrl）
      parentId: '', // 根文件夹传空
      uploaderId,
      uploaderName
    })
    ElMessage.success('外链添加成功')
    linkDialog.value = false
    initResourceList()
  } catch (error) {
    ElMessage.error('添加外链失败：' + (error.msg || error.message || '接口异常'))
  }
}

// 新增删除方法
const deleteResourceItem = async (resId) => {
  try {
    await ElMessageBox.confirm('确定删除该资源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteResource(resId) // 调用resource.js的deleteResource
    ElMessage.success('删除成功')
    initResourceList()
  } catch (error) {
    if (error !== 'cancel') { // 排除取消弹窗的情况
      ElMessage.error('删除失败：' + (error.msg || error.message || '接口异常'))
    }
  }
}

// 页面挂载时初始化
onMounted(() => {
  initResourceList()
})
</script>

<style scoped>
.page-wrap { padding: 20px 40px; }
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
}
.btn-group { display: flex; gap: 10px; }
.resource-list {
  margin-top: 20px;
  padding: 10px;
}
.resource-item {
  padding: 8px 12px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  margin-bottom: 8px;
}
</style>