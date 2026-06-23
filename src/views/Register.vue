<template>
  <div class="register-container">
    <!-- 左侧展示区 -->
    <div class="left-panel">
      <img src="@/assets/image/login.png" alt="Welcome" class="welcome-img" />
      <div class="welcome-text">
        <h1>加入智慧课堂</h1>
        <p>选择你的身份，开启全新的教学体验</p>
      </div>
    </div>

    <!-- 右侧注册表单 -->
    <div class="right-panel">
      <el-card class="register-card" shadow="hover">
        <div class="logo-area">
          <img src="@/assets/image/logo.png" alt="Logo" height="40" />
          <h2>创建新账号</h2>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <!-- 核心亮点：可视化身份选择 -->
          <el-form-item label="选择身份" prop="role">
            <div class="role-selector">
              <!-- 学生卡片 -->
              <div class="role-card" :class="{ active: form.role === 'student' }" @click="form.role = 'student'">
                <img :src="studentAvatar" alt="Student" class="role-avatar" />
                <span class="role-title">我是学生</span>
                <span class="role-desc">加入课程，提交作业</span>
              </div>
              <!-- 教师卡片 -->
              <div class="role-card" :class="{ active: form.role === 'teacher' }" @click="form.role = 'teacher'">
                <img :src="teacherAvatar" alt="Teacher" class="role-avatar" />
                <span class="role-title">我是教师</span>
                <span class="role-desc">创建课堂，管理教学</span>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入真实姓名" clearable />
          </el-form-item>

          <el-form-item label="账号" prop="account">
            <el-input v-model="form.account" placeholder="请输入邮箱或手机号" clearable />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <!-- 核心亮点：密码可视切换 -->
            <el-input v-model="form.password" type="password" placeholder="设置密码（至少6位）" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPwd">
            <el-input v-model="form.confirmPwd" type="password" placeholder="再次输入密码" show-password />
          </el-form-item>

          <el-button type="primary" class="submit-btn" round :loading="isLoading" @click="handleRegister">
            立即注册
          </el-button>
        </el-form>

        <div class="extra-links">
          <span>已有账号？</span>
          <el-link href="/login" :underline="false" type="primary">去登录</el-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register as apiRegister } from '@/api/user' // 引入注册接口

// 👇 2. 导入你准备好的教师和学生头像图片
import teacherAvatar from '@/assets/image/teacher.png'
import studentAvatar from '@/assets/image/student.png'

const router = useRouter()
const formRef = ref(null)
const isLoading = ref(false)

const form = reactive({
  role: 'student', // 默认选中学生
  name: '',
  account: '',
  password: '',
  confirmPwd: ''
})

// 自定义校验：确认密码是否一致
const validateConfirmPwd = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  role: [{ required: true, message: '请选择你的身份', trigger: 'change' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      isLoading.value = true
      // 调用后端注册接口
      await apiRegister({
        role: form.role,
        name: form.name,
        username: form.account,
        password: form.password
      })

      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error) {
      ElMessage.error(error.message || '注册失败，请稍后重试')
    } finally {
      isLoading.value = false
    }
  })
}
</script>

<style scoped>
/* 整体布局与登录页保持一致 */
.register-container { display: flex; height: 100vh; background-color: #f0f2f5; }
.left-panel {
  flex: 1;
  background: url('@/assets/image/login.png') center / cover no-repeat;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  color: white; text-align: center; position: relative;
}
.left-panel::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.3);
}
.welcome-text {
  position: relative; z-index: 1;
  background: rgba(255, 255, 255, 0.1); backdrop-filter: blur(10px);
  padding: 30px 50px; border-radius: 16px; border: 1px solid rgba(255, 255, 255, 0.2);
}
.welcome-text h1 { font-size: 32px; margin-bottom: 10px; }
.welcome-text p { font-size: 16px; opacity: 0.9; }

.right-panel {
  width: 450px; display: flex; align-items: center; justify-content: center;
  background: #fff; box-shadow: -5px 0 15px rgba(0,0,0,0.05);
}
.register-card { width: 380px; border: none; padding: 20px; border-radius: 12px; }
.logo-area { text-align: center; margin-bottom: 20px; }
.logo-area h2 { margin: 15px 0 0; font-size: 24px; color: #333; }

/* 核心亮点：身份选择卡片样式 */
.role-selector { display: flex; gap: 15px; width: 100%; }
.role-card {
  flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 20px 15px; border-radius: 12px; cursor: pointer;
  border: 2px solid #ebeef5; transition: all 0.3s ease; background-color: #fafafa;
}
.role-avatar {
  width: 80px; height: 80px; object-fit: contain; margin-bottom: 10px; transition: transform 0.3s ease;
}
.role-title { font-size: 16px; font-weight: bold; color: #333; margin-bottom: 5px; }
.role-desc { font-size: 12px; color: #999; }

/* 选中状态 */
.role-card.active {
  border-color: #409eff; background-color: #ecf5ff;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.2);
}
.role-card:hover .role-avatar,
.role-card.active .role-avatar { transform: scale(1.1); }
.role-card.active .role-title { color: #409eff; }

.submit-btn { width: 100%; height: 45px; font-size: 16px; margin-top: 10px; font-weight: bold; letter-spacing: 2px; }
.extra-links { text-align: center; margin-top: 20px; font-size: 14px; }
</style>