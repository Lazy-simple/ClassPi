<template>
  <div class="user-container">
    <el-card style="max-width: 600px;">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>

      <el-form :model="form" label-width="80px" ref="formRef">
        <!-- 头像展示区域 -->
        <el-form-item label="头像">
          <div class="avatar-wrapper">
            <!-- 1. 动态绑定头像：根据角色显示对应的图片 -->
            <img
              :src="currentAvatar"
              alt="用户头像"
              class="avatar-img"
            />
            <!-- 可选：点击更换头像的按钮 -->
            <el-button size="small" type="primary" plain style="margin-left: 15px;">更换头像</el-button>
          </div>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="身份">
          <!-- 身份通常不可编辑，仅展示 -->
          <el-tag :type="form.role === 'teacher' ? 'success' : 'primary'" size="large">
            {{ form.role === 'teacher' ? '教师' : '学生' }}
          </el-tag>
        </el-form-item>

        <el-form-item label="学校">
          <el-input v-model="form.school" placeholder="请输入学校名称" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入联系邮箱" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { useUserStore } from '@/store/userStore'
import { ElMessage } from 'element-plus'

// 2. 导入你准备好的教师和学生头像图片
import teacherAvatar from '@/assets/image/teacher.png'
import studentAvatar from '@/assets/image/student.png'

const userStore = useUserStore()
const saving = ref(false)
const formRef = ref(null)

// 3. 定义响应式的表单数据对象
const form = reactive({
  name: '',
  role: '',
  school: '',
  email: ''
})

// 4. 计算当前应该显示的头像
const currentAvatar = computed(() => {
  return form.role === 'teacher' ? teacherAvatar : studentAvatar
})

// 5. 监听 Store 变化，初始化表单数据
watch(
  () => userStore.userInfo,
  (newVal) => {
    if (newVal) {
      Object.assign(form, newVal)
    }
  },
  { immediate: true } // 立即执行一次，防止刷新页面后表单为空
)

// 6. 保存逻辑
const handleSave = async () => {
  saving.value = true
  try {
    // TODO: 调用后端 API 更新用户信息
    // await updateUserApi(form)

    // 模拟请求延迟
    setTimeout(() => {
      // 更新本地 Store
      userStore.setUserInfo(form)
      ElMessage.success('个人信息更新成功')
      saving.value = false
    }, 1000)
  } catch (error) {
    console.error(error)
    ElMessage.error('更新失败，请稍后重试')
    saving.value = false
  }
}

// 重置功能
const resetForm = () => {
  if (userStore.userInfo) {
    Object.assign(form, userStore.userInfo)
  }
}
</script>

<style scoped>
.user-container {
  padding: 20px;
  display: flex;
  justify-content: center; /* 让卡片居中 */
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

/* 头像样式 */
.avatar-wrapper {
  display: flex;
  align-items: center;
}

.avatar-img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ebeef5;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>