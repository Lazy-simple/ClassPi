<template>
  <div class="login-container">
    <!-- 左侧图片与欢迎区域 -->
    <div class="left-panel">
      <img src="@/assets/image/login.png" alt="Welcome" class="welcome-img" />
      <div class="welcome-text">
        <h1>欢迎回到课堂派</h1>
        <p>连接师生，智慧教学</p>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="right-panel">
      <el-card class="login-card" shadow="hover">
        <div class="logo-area">
          <img src="@/assets/image/logo.png" alt="Logo" height="50" />
          <h2>账号登录</h2>
        </div>

        <!-- 登录方式切换 -->
        <el-tabs v-model="activeTab" class="login-tabs" stretch>
          <el-tab-pane label="密码登录" name="password">
            <el-form :model="form" label-position="top">
              <el-form-item label="账号">
                <el-input v-model="form.account" placeholder="请输入邮箱或手机号">
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password>
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="短信登录" name="sms">
            <el-form :model="form" label-position="top">
              <el-form-item label="手机号">
                <el-input v-model="form.phone" placeholder="请输入手机号">
                  <template #prefix><el-icon><Iphone /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item label="验证码">
                <div class="sms-group">
                  <el-input v-model="form.code" placeholder="验证码">
                    <template #prefix><el-icon><Message /></el-icon></template>
                  </el-input>
                  <el-button type="primary" plain style="margin-left: 10px;">获取验证码</el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <el-button type="primary" class="submit-btn" @click="handleLogin" round>立即登录</el-button>

        <div class="extra-links">
          <el-link href="/register" :underline="false">还没有账号？去注册</el-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('password')

const form = reactive({
  account: '',
  password: '',
  phone: '',
  code: ''
})

const handleLogin = () => {
  // 模拟登录成功
  router.push('/home')
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background-color: #f0f2f5;
}

/* 左侧样式 */
.left-panel {
  flex: 1;
  /* 设置背景图，center 居中，cover 保证铺满不留白 */
  background: url('@/assets/image/login.png') center / cover no-repeat;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
}

/* 给文字加一个半透明的黑色遮罩，防止背景图太花导致文字看不清 */
.welcome-text {
  background: rgba(0, 0, 0, 0.4);
  padding: 30px 50px;
  border-radius: 10px;
}

.welcome-text h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
}

/* 右侧样式保持不变 */
.right-panel {
  width: 450px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  box-shadow: -5px 0 15px rgba(0,0,0,0.05);
}

.login-card {
  width: 360px;
  border: none;
  padding: 20px;
}

.logo-area {
  text-align: center;
  margin-bottom: 30px;
}

.logo-area h2 {
  margin: 15px 0 0;
  font-size: 24px;
  color: #333;
}

.sms-group {
  display: flex;
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  margin-top: 20px;
}

.extra-links {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}
</style>