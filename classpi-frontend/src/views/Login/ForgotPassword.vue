<template>
  <div class="page-wrap">
    <!-- 1. 高级感动态网格背景 -->
    <div class="mesh-gradient"></div>
    <div class="noise-overlay"></div> <!-- 增加纸张质感的噪点 -->

    <!-- 2. 极致玻璃卡片 -->
    <div class="pwd-card">
      <h2 class="card-title">找回密码</h2>
      <p class="card-subtitle">请按照以下步骤重置您的账户密码</p>

      <!-- 3. 纯 CSS 高级步骤条 -->
      <div class="custom-steps-container">
        <div class="step-item" :class="{ active: step === 0, done: step > 0 }">
          <div class="step-circle">
            <span v-if="step <= 0">1</span>
            <el-icon v-else size="14"><Check /></el-icon>
          </div>
          <div class="step-label">验证账号</div>
        </div>
        <div class="step-line" :class="{ done: step > 0 }"></div>
        <div class="step-item" :class="{ active: step === 1, done: step > 1 }">
          <div class="step-circle">
            <span v-if="step <= 1">2</span>
            <el-icon v-else size="14"><Check /></el-icon>
          </div>
          <div class="step-label">重置密码</div>
        </div>
        <div class="step-line" :class="{ done: step > 1 }"></div>
        <div class="step-item" :class="{ active: step === 2 }">
          <div class="step-circle">
            <span v-if="step <= 2">3</span>
            <el-icon v-else size="14"><Check /></el-icon>
          </div>
          <div class="step-label">完成</div>
        </div>
      </div>

      <!-- 4. 内容区域 -->
      <transition name="fade-slide" mode="out-in">
        <div class="step-content-wrapper" :key="step">
          <!-- 步骤 1 -->
          <div v-if="step === 0" class="form-section">
            <el-form :model="form" label-position="top" @submit.prevent="nextStep">
              <el-form-item label="账号/手机号">
                <el-input
                  v-model="form.account"
                  placeholder="请输入注册时的账号或手机号"
                  size="large"
                  prefix-icon="User"
                />
              </el-form-item>
              <el-form-item>
                <el-button class="primary-btn" size="large" @click="nextStep">
                  下一步 <el-icon class="btn-icon"><ArrowRight /></el-icon>
                </el-button>
              </el-form-item>
            </el-form>
            <div class="tip-area">
              账号无法使用？<router-link to="/register" class="link-text">注册新账号</router-link>
            </div>
          </div>

          <!-- 步骤 2 -->
          <div v-if="step === 1" class="form-section">
            <el-form :model="form" label-position="top" @submit.prevent="nextStep">
              <el-form-item label="新密码">
                <el-input
                  v-model="form.newPwd"
                  type="password"
                  show-password
                  placeholder="设置新密码"
                  size="large"
                  prefix-icon="Lock"
                />
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input
                  v-model="form.confirmPwd"
                  type="password"
                  show-password
                  placeholder="再次输入新密码"
                  size="large"
                  prefix-icon="Lock"
                />
              </el-form-item>
              <el-form-item>
                <el-button class="primary-btn" size="large" @click="nextStep">
                  确认重置 <el-icon class="btn-icon"><CircleCheckFilled /></el-icon>
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 步骤 3 -->
          <div v-if="step === 2" class="success-section">
            <div class="success-icon-wrapper">
              <el-icon size="60" color="#fff"><CircleCheckFilled /></el-icon>
            </div>
            <h3 class="success-title">密码重置成功！</h3>
            <p class="success-desc">您的密码已更新，请使用新密码登录。</p>
            <el-button class="secondary-btn" size="large" @click="$router.push('/login')">前往登录</el-button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, ArrowRight, CircleCheckFilled } from '@element-plus/icons-vue'

const step = ref(0)
const form = ref({ account: '', newPwd: '', confirmPwd: '' })

const nextStep = () => {
  if (step.value === 0 && !form.value.account) { ElMessage.warning('请输入账号/手机号'); return; }
  if (step.value === 1) {
    if (!form.value.newPwd || !form.value.confirmPwd) { ElMessage.warning('请填写完整密码信息'); return; }
    if (form.value.newPwd !== form.value.confirmPwd) { ElMessage.warning('两次输入的密码不一致'); return; }
  }
  step.value++
}
</script>

<style scoped>
/* ================= 全局布局与高级背景 ================= */
.page-wrap {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #0f172a; /* 深邃的暗夜蓝底色 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 动态网格渐变背景 (Mesh Gradient) */
.mesh-gradient {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 0;
  background:
    radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.4) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(236, 72, 153, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(14, 165, 233, 0.3) 0%, transparent 70%);
  animation: meshMove 15s infinite alternate ease-in-out;
}

@keyframes meshMove {
  0% { transform: scale(1) rotate(0deg); }
  100% { transform: scale(1.1) rotate(2deg); }
}

/* 噪点纹理 (增加真实质感) */
.noise-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 1;
  opacity: 0.05;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
  pointer-events: none;
}

/* ================= 极致玻璃卡片 ================= */
.pwd-card {
  position: relative;
  z-index: 10;
  width: 480px;
  padding: 48px;
  /* 核心：半透明 + 模糊 + 边框高光 + 复杂阴影 */
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  box-shadow:
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.05) inset;
  text-align: center;
}

.card-title {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 700;
  color: #f8fafc; /* 亮白色文字 */
  letter-spacing: -0.5px;
}

.card-subtitle {
  margin: 0 0 36px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

/* ================= 自定义步骤条 ================= */
.custom-steps-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;
  padding: 0 10px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
}

.step-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.step-label {
  margin-top: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 500;
  transition: color 0.3s;
}

.step-line {
  flex-grow: 1;
  height: 2px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0 12px;
  margin-bottom: 24px;
  transition: background 0.4s ease;
  z-index: 1;
}

/* 激活状态 */
.step-item.active .step-circle {
  background: #0ea5e9;
  color: #fff;
  box-shadow: 0 0 20px rgba(14, 165, 233, 0.5);
  border-color: transparent;
  transform: scale(1.1);
}
.step-item.active .step-label {
  color: #0ea5e9;
  font-weight: 600;
}

/* 完成状态 */
.step-item.done .step-circle {
  background: #10b981;
  color: #fff;
  border-color: transparent;
}
.step-item.done .step-label {
  color: #10b981;
}
.step-line.done {
  background: #10b981;
}

/* ================= 表单与按钮 ================= */
.step-content-wrapper {
  min-height: 260px;
}

:deep(.el-form-item__label) {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 600;
  margin-bottom: 6px;
}

:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.05);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 10px 16px;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

:deep(.el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: #0ea5e9;
  box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.2);
}

:deep(.el-input__inner) {
  color: #f8fafc;
}
:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.3);
}
:deep(.el-input__prefix .el-icon) {
  color: rgba(255, 255, 255, 0.4);
}

.primary-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #0ea5e9 0%, #6366f1 100%);
  border: none;
  color: #fff;
  margin-top: 10px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(99, 102, 241, 0.4);
}

.btn-icon { margin-left: 8px; }

.tip-area {
  margin-top: 20px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.link-text {
  color: #0ea5e9;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: opacity 0.2s;
}
.link-text:hover { opacity: 0.8; text-decoration: underline; }

/* ================= 成功页样式 ================= */
.success-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 20px;
}

.success-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #34d399, #10b981);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
  animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.success-title {
  font-size: 22px;
  color: #f8fafc;
  margin: 0 0 8px;
  font-weight: 700;
}

.success-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 32px;
}

.secondary-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  color: #f8fafc;
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-weight: 600;
}
.secondary-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

/* ================= 动画 ================= */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

@keyframes popIn {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}
</style>