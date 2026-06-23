<template>
  <div class="forgot-container">
    <!-- 左侧装饰区（与登录页风格统一） -->
    <div class="left-panel">
      <img src="@/assets/image/login.png" alt="Welcome" class="welcome-img" />
      <h2 class="slogan">安全找回您的密码</h2>
      <p class="sub-slogan">我们将通过您绑定的邮箱或手机号发送重置链接</p>
    </div>

    <!-- 右侧表单区 -->
    <div class="right-panel">
      <el-card class="forgot-card" shadow="hover">
        <div class="header">
          <img src="@/assets/image/logo.png" alt="Logo" class="logo" />
          <h3>忘记密码</h3>
          <p class="sub-title">请输入您注册时使用的邮箱或手机号</p>
        </div>

        <el-form :model="form" size="large" label-position="top" @keyup.enter="handleNext">
          <el-form-item label="账号">
            <el-input
              v-model="form.account"
              placeholder="请输入邮箱或手机号"
              :disabled="loading"
            >
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-button
            type="primary"
            class="submit-btn"
            round
            :loading="loading"
            @click="handleNext"
          >
            下一步
          </el-button>
        </el-form>

        <div class="footer-link">
          以上方式均不可用？<el-link type="primary" href="/appeal" :underline="false">进行申诉</el-link>
        </div>

        <!-- 返回登录 -->
        <div class="back-link">
          <el-link href="/login" :underline="false">
            <el-icon><ArrowLeft /></el-icon> 返回登录
          </el-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
// import { sendResetLink } from '@/api/user'

const loading = ref(false)
const form = reactive({
  account: ''
})

const handleNext = async () => {
  // 1. 基础校验
  if (!form.account.trim()) {
    ElMessage.warning('请先输入您的账号')
    return
  }

  loading.value = true
  try {
    // 2. 调用 API 发送重置链接
    // const res = await sendResetLink({ account: form.account })

    // 模拟请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('重置密码链接已发送到您的邮箱/手机，请注意查收')

    // 实际项目中，这里通常会跳转到一个“请查收邮件”的提示页
    // router.push('/forgot/success')
  } catch (error) {
    console.error(error)
    ElMessage.error('发送失败，请检查账号是否正确或稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 整体布局：左右分栏 */
.forgot-container {
  display: flex;
  height: 100vh;
}

/* 左侧样式 */
.left-panel {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
}

.welcome-img {
  width: 70%;
  max-width: 500px;
  margin-bottom: 20px;
  filter: drop-shadow(0 10px 20px rgba(0,0,0,0.2));
}

.slogan {
  font-size: 28px;
  font-weight: 400;
  letter-spacing: 2px;
  margin-bottom: 10px;
}

.sub-slogan {
  font-size: 14px;
  opacity: 0.8;
}

/* 右侧样式 */
.right-panel {
  width: 500px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
}

.forgot-card {
  width: 400px;
  border-radius: 12px;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 50px;
  height: 50px;
  margin-bottom: 10px;
}

.header h3 {
  margin: 10px 0 5px;
  font-size: 24px;
  color: #333;
}

.sub-title {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  margin-top: 10px;
}

.footer-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #999;
}

.back-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
</style>