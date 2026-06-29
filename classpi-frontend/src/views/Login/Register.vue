<template>
  <div class="register-page">
    <!-- 背景层保持不动 -->
    <div class="aurora-bg"></div>

    <div class="register-card-container">
      <!-- 头部标题 -->
      <div class="card-header">
        <h2 class="title">注册新账号</h2>
        <p class="subtitle">创建您的 ClassPi 账户</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="default"
        class="custom-form"
      >
        <!-- 账号/手机号 -->
        <el-form-item label="账号/手机号" prop="account">
          <el-input v-model="form.account" placeholder="请输入手机号或邮箱" clearable prefix-icon="User" />
        </el-form-item>

        <!-- 密码与确认密码 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="6-16位字符" show-password prefix-icon="Lock" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPwd">
              <el-input v-model="form.confirmPwd" type="password" placeholder="再次输入" show-password prefix-icon="Lock" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 身份选择 -->
        <el-form-item label="身份选择" class="identity-item">
          <el-radio-group v-model="form.identity" size="default" class="custom-radio-group">
            <el-radio-button label="teacher">👨‍🏫 教师</el-radio-button>
            <el-radio-button label="student">👨‍🎓 学生</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 姓名 -->
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="真实姓名" prefix-icon="Avatar" />
        </el-form-item>

        <!-- 学校/机构 -->
        <el-form-item label="学校/机构" prop="school">
          <el-input v-model="form.school" placeholder="请输入学校名称" prefix-icon="School" />
        </el-form-item>

        <!-- 【修改处】图片验证码区域 -->
        <el-form-item label="验证码" prop="captchaAnswer">
          <div class="captcha-wrapper">
            <!-- 输入框：占据剩余空间，高度自动适应 -->
            <el-input
              v-model="form.captchaAnswer"
              placeholder="请输入计算结果"
              style="flex: 1;"
              maxlength="4"
            />
            <!-- 图片：固定宽度，点击刷新 -->
            <img
              :src="captchaImg"
              @click="refreshCaptcha"
              class="captcha-img"
              alt="看不清？点我"
              title="看不清？点击刷新"
            />
          </div>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item class="submit-item">
          <el-button type="primary" class="submit-btn" @click="handleRegister" :loading="loading">
            立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="footer-link">
        已有账号？<router-link to="/login" class="link-text">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'  // ✅ 添加

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  account: '',
  password: '',
  confirmPwd: '',
  identity: 'teacher',
  name: '',
  school: '',
  captchaAnswer: ''
})

// 验证码图片和ID
const captchaImg = ref('')
const captchaId = ref('')

const refreshCaptcha = async () => {
  try {
    const res = await fetch(`http://localhost:8080/captcha?t=${Date.now()}`)
    const data = await res.json()
    if (data.code === 200 && data.data) {
      captchaId.value = data.data.captchaId
      captchaImg.value = data.data.image
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

onMounted(refreshCaptcha)

// 表单校验规则
const rules = reactive({
  account: [{ required: true, message: '请输入账号或手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }],
  confirmPwd: [{ required: true, message: '请再次输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  school: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  captchaAnswer: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})

// ✅ 注册逻辑 - 调用真实后端
const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.password !== form.confirmPwd) {
        return ElMessage.warning('两次输入的密码不一致')
      }

      loading.value = true
      try {
        // 先验证验证码
        const captchaRes = await fetch('http://localhost:8080/captcha/verify', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: `captchaId=${captchaId.value}&answer=${form.captchaAnswer}`
        })
        const captchaData = await captchaRes.json()
        if (captchaData.code !== 200) {
          ElMessage.error('验证码错误，请重新输入')
          refreshCaptcha()
          loading.value = false
          return
        }

        // 注册数据，匹配后端 RegisterDTO
        const res = await request.post('/user/register', {
          username: form.account,
          password: form.password,
          phone: form.account,
          name: form.name,
          school: form.school,
          identity: form.identity,      // teacher / student
          studentId: ''
        })
        if (res.code === 200) {
          ElMessage.success('注册成功！请登录')
          router.push('/login')
        } else {
          ElMessage.error(res.msg || '注册失败')
          refreshCaptcha()
        }
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error('网络异常，请稍后重试')
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 全局背景：极光渐变 */
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 极光背景动画 */
.aurora-bg {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  z-index: -1;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 卡片容器：磨砂玻璃效果 */
.register-card-container {
  width: 100%;
  max-width: 500px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  box-sizing: border-box;
  margin: 20px;
}

/* 头部排版 */
.card-header { text-align: center; margin-bottom: 30px; }
.title { font-size: 24px; color: #303133; margin: 0 0 8px 0; font-weight: 600; }
.subtitle { font-size: 14px; color: #909399; margin: 0; }

/* 身份选择器样式优化 */
.custom-radio-group { width: 100%; display: flex; }
.custom-radio-group .el-radio-button { flex: 1; }
.custom-radio-group :deep(.el-radio-button__inner) { width: 100%; padding: 10px 0; } /* 稍微调小一点padding以匹配整体紧凑感 */

/* 【重点修改】验证码布局 */
.captcha-wrapper {
  display: flex;
  gap: 10px; /* 输入框和图片之间的间距 */
  width: 100%;
  align-items: center; /* 垂直居中对齐 */
}

.captcha-img {
  height: 32px; /* 关键：固定高度，与默认大小的 el-input 一致 */
  width: 110px; /* 固定宽度 */
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6; /* 加个边框让它看起来像个输入组件的一部分 */
  object-fit: cover;
  transition: all 0.3s;
}

.captcha-img:hover {
  border-color: #409eff;
  opacity: 0.8;
}

/* 提交按钮 */
.submit-item { margin-top: 10px; }
.submit-btn {
  width: 100%;
  height: 40px; /* 这里的按钮高度也可以稍微调小一点，显得更精致 */
  font-size: 16px;
  letter-spacing: 2px;
  border-radius: 6px;
  background-color: #409eff;
  border: none;
}
.submit-btn:hover { background-color: #66b1ff; }

/* 底部链接 */
.footer-link { text-align: center; margin-top: 20px; font-size: 14px; color: #909399; }
.link-text { color: #409eff; text-decoration: none; margin-left: 5px; }
.link-text:hover { text-decoration: underline; }
</style>
//只需要把 captchaImg.value 改成你的后端接口地址即可