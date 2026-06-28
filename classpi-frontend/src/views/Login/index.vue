<template>
  <div class="login-page">
    <!-- 1. 动态极光背景层 -->
    <div class="aurora-bg"></div>
    <!-- 2. 主容器 -->
    <div class="container">
      <!-- 左侧区域：Logo + 静态插画 -->
      <div class="left-section">
        <div class="logo-wrapper">
          <img src="@/assets/image/logo.png" alt="ClassPi Logo" class="brand-logo" />
        </div>
        <img src="@/assets/image/login.png" alt="Illustration" class="hero-img" />
      </div>
      <!-- 右侧区域：磨砂玻璃登录卡片 -->
      <div class="login-card">
        <h2 class="card-title">账号登录</h2>
        <!-- Tab 切换栏 -->
        <div class="login-tabs">
          <span class="tab-item" :class="{ active: activeTab === 'account' }" @click="activeTab = 'account'">
            账号密码
          </span>
          <span class="tab-item" :class="{ active: activeTab === 'sms' }" @click="activeTab = 'sms'">
            手机短信
          </span>
          <span class="tab-item" :class="{ active: activeTab === 'wechat' }" @click="switchToWechat">
            微信登录
          </span>
          <!-- 底部滑动条 -->
          <div class="tab-indicator" :style="{ left: indicatorLeft }"></div>
        </div>
        <!-- 表单内容区 -->
        <div class="form-content">
          <!-- 账号密码登录 -->
          <div v-if="activeTab === 'account'" class="fade-in">
            <input type="text" placeholder="请输入邮箱/手机号/账号" v-model="account" />
            <input type="password" placeholder="请输入密码" v-model="password" />
            <div class="options-row">
              <label class="checkbox-label">
                <input type="checkbox" v-model="autoLogin" />
                <span>下次自动登录</span>
              </label>
              <router-link to="/forgot-password" class="link-text">忘记密码？</router-link>
            </div>
            <button class="submit-btn" @click="handleLogin">立即登录</button>
            <p class="register-tip">还没有账号？<router-link to="/register">去注册</router-link></p>
          </div>
          <!-- 短信验证码登录（已修复布局） -->
          <div v-if="activeTab === 'sms'" class="fade-in">
            <input type="text" placeholder="请输入手机号" v-model="phone" />
            <!-- 【修复点1】验证码输入框和按钮的flex布局 -->
            <div class="sms-row">
              <input type="text" placeholder="输入验证码" v-model="smsCode" />
              <button class="sms-btn" :disabled="countdown > 0" @click="sendSmsCode">
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
            <div class="options-row">
              <label class="checkbox-label">
                <input type="checkbox" v-model="autoLogin" />
                <span>下次自动登录</span>
              </label>
            </div>
            <button class="submit-btn" @click="handleLogin">立即登录</button>
            <p class="register-tip">还没有账号？<router-link to="/register">去注册</router-link></p>
          </div>
          <!-- 微信登录 (已修改) -->
          <div v-if="activeTab === 'wechat'" class="fade-in wechat-box">
            <div class="qr-container">
              <!-- 1. 加载中状态 -->
              <div v-if="qrLoading" class="qr-loading-state">
                <div class="spinner"></div>
                <p>正在生成二维码...</p>
              </div>
              <!-- 2. 成功加载二维码 -->
              <img
                v-else-if="qrCodeUrl"
                :src="qrCodeUrl"
                alt="微信扫码登录"
                class="qr-img"
                @click="refreshQrCode"
                title="点击刷新二维码"
              />
              <!-- 3. 失败或空状态 -->
              <div v-else class="qr-error-state" @click="getWechatQrCode">
                <span>❌ 二维码加载失败</span>
                <small>点击屏幕重试</small>
              </div>
            </div>
            <p class="scan-tip">请使用微信扫一扫登录</p>
            <button class="wechat-btn" @click="refreshQrCode" :disabled="qrLoading">
              {{ qrLoading ? '生成中...' : '刷新二维码' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user'
// 引入登录接口（你的api/user.js里的login）
import { login } from '@/api/user'

const router = useRouter();
const activeTab = ref('account');
const account = ref('');
const password = ref('');
const phone = ref('');
const smsCode = ref('');
const autoLogin = ref(false);
const countdown = ref(0);
// --- 微信二维码相关变量 ---
const qrCodeUrl = ref('');
const qrLoading = ref(false);
let timer = null;

// 计算指示条位置
const indicatorLeft = computed(() => {
  if (activeTab.value === 'account') return '0%';
  if (activeTab.value === 'sms') return '33.33%';
  return '66.66%';
});

const sendSmsCode = () => {
  if (!phone.value) return ElMessage.warning('请先输入手机号');
  if (countdown.value > 0) return;
  countdown.value = 60;
  ElMessage.success('验证码已发送');
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) clearInterval(timer);
  }, 1000);
};

const userStore = useUserStore()

// 登录核心方法（账号+短信统一身份跳转）
// 登录核心方法（账号+短信统一身份跳转）
const handleLogin = async () => {
  if (activeTab.value === 'account') {
    if (!account.value || !password.value) {
      ElMessage.warning('请输入账号和密码');
      return;
    }

    try {
      const res = await login({
        username: account.value,
        password: password.value
      });

      console.log('登录响应:', res);

      if (res.code !== 200 && res.code !== 0) {
        ElMessage.error(res.message || '登录失败');
        return;
      }

      if (!res.data) {
        ElMessage.error('登录数据为空');
        return;
      }

      // ✅ 适配：后端直接返回用户信息
      const userInfo = res.data;

      // ✅ 检查必要字段
      if (!userInfo.identity) {
        ElMessage.error('用户身份信息缺失');
        return;
      }

      // ✅ 生成临时 token（实际项目应该从后端获取）
      // 方式1：使用 btoa 编码
      const token = btoa(JSON.stringify({
        id: userInfo.id,
        username: userInfo.username,
        timestamp: Date.now()
      }));

      // 方式2：如果后端返回了 token 就用后端的
      // const token = userInfo.token || 'temp-token';

      ElMessage.success('登录成功');

      // 存储数据
      console.log('=== 登录成功，存储用户信息 ===');
      console.log('userInfo:', userInfo);
      console.log('identity:', userInfo.identity);
      
      userStore.setUser({ token, userInfo });
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userInfo.id || userInfo.userId)
      localStorage.setItem('userInfo', JSON.stringify(userInfo));

      // 跳转
      const identity = userInfo.identity;
      console.log('=== 准备跳转 ===');
      console.log('身份:', identity);
      console.log('目标路由:', identity === 'teacher' ? '/main/dashboard' : '/main/student-course');
      
      if (identity === 'teacher') {
        router.push('/main/dashboard');
      } else if (identity === 'student') {
        router.push('/main/student-course');
      } else {
        ElMessage.warning('未知身份: ' + identity);
      }

    } catch (err) {
      console.error('登录错误:', err);
      ElMessage.error(err.response?.data?.msg || err.msg || '账号或密码错误');
    }
  }
};


// 切换微信tab
const switchToWechat = () => {
  activeTab.value = 'wechat';
  getWechatQrCode();
};

// 模拟获取微信二维码
const getWechatQrCode = async () => {
  qrLoading.value = true;
  qrCodeUrl.value = '';
  try {
    await new Promise(resolve => setTimeout(resolve, 800));
    qrCodeUrl.value = 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=WeChat-Login-Demo';
  } catch (error) {
    console.error('获取二维码失败:', error);
    ElMessage.error('获取二维码失败，请重试');
  } finally {
    qrLoading.value = false;
  }
};

// 刷新二维码
const refreshQrCode = () => {
  if (!qrLoading.value) {
    getWechatQrCode();
  }
};

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
/* --- 1. 动态极光背景 --- */
.login-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}
.aurora-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradientFlow 10s ease infinite;
  z-index: -1;
}
@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* --- 2. 布局与容器 --- */
.container {
  display: flex;
  width: 1000px;
  max-width: 90%;
  height: 600px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

/* --- 3. 左侧区域 --- */
.left-section {
  flex: 1.2;
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  position: relative;
}
.brand-logo {
  width: 140px;
  margin-bottom: 20px;
}
.hero-img {
  width: 100%;
  max-width: 450px;
  object-fit: contain;
}

/* --- 4. 右侧登录卡片 --- */
.login-card {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  background: transparent;
}
.card-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
  font-weight: 600;
}

/* Tabs 样式 */
.login-tabs {
  display: flex;
  position: relative;
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding-bottom: 12px;
  cursor: pointer;
  color: #666;
  font-size: 15px;
  transition: color 0.3s;
}
.tab-item.active {
  color: #23a6d5;
  font-weight: bold;
}
.tab-indicator {
  position: absolute;
  bottom: -1px;
  width: 33.33%;
  height: 3px;
  background-color: #23a6d5;
  transition: left 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  border-radius: 3px 3px 0 0;
}

/* 表单输入框样式 */
input[type='text'],
input[type='password'] {
  width: 100%;
  padding: 14px 15px;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
}
input:focus {
  border-color: #23a6d5;
  box-shadow: 0 0 0 3px rgba(35, 166, 213, 0.1);
}

/* 选项行 */
.options-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  font-size: 13px;
}
.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #666;
}
.checkbox-label input {
  margin-right: 6px;
  accent-color: #23a6d5;
}
.link-text {
  color: #666;
  text-decoration: none;
}
.link-text:hover {
  color: #23a6d5;
}

/* 按钮通用样式 */
.submit-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(90deg, #23a6d5, #23d5ab);
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.3s;
}
.submit-btn:hover {
  opacity: 0.9;
}
.register-tip {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: #666;
}
.register-tip a {
  color: #23a6d5;
  text-decoration: none;
  font-weight: bold;
}

/* 【修复点2】验证码输入框和按钮的样式 */
.sms-row {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.sms-row input {
  flex: 1;
  margin-bottom: 0;
}
.sms-btn {
  width: 120px;
  height: 48px; /* 和输入框高度一致 */
  border: 1px solid #23a6d5;
  background: white;
  color: #23a6d5;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  white-space: nowrap;
}
.sms-btn:disabled {
  border-color: #ccc;
  color: #ccc;
  background: #f9f9f9;
  cursor: not-allowed;
}

/* --- 微信登录区样式 --- */
.wechat-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qr-container {
  width: 200px;
  height: 200px;
  border: 1px dashed #ccc;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 15px;
  background: #f9f9f9;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.qr-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: opacity 0.3s;
}
.qr-img:hover {
  opacity: 0.8;
}

.qr-loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  font-size: 14px;
}
.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #eee;
  border-top-color: #07c160;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 10px;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

.qr-error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  cursor: pointer;
  font-size: 14px;
}
.qr-error-state small {
  margin-top: 5px;
  color: #bbb;
}

.scan-tip {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.wechat-btn {
  width: 100%;
  padding: 12px;
  background: #07c160;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.3s;
}
.wechat-btn:hover:not(:disabled) {
  background: #06ad56;
}
.wechat-btn:disabled {
  background: #a0d8b8;
  cursor: not-allowed;
}

/* 简单的淡入动画 */
.fade-in {
  animation: fadeIn 0.4s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>