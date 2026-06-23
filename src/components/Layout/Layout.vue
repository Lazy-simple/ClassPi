<template>
  <el-container style="height:100vh">
    <el-aside width="200px">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header>
        <Header />
      </el-header>
      <el-main style="background:#f3f4f6">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import Header from './Header.vue'
import Sidebar from './Sidebar.vue'
</script><template>
             <div class="container">
                 <div class="logo">
                     <img src="@/assets/image/logo.png" width="150">
                 </div>
                 <div class="image-section">
                     <img src="@/assets/image/login.png">
                 </div>

                 <div class="login-section" style="width: 470px; height: 520px;">
                     <h2 style="margin-bottom: 20px; color: black;text-align: center;">账号登录</h2>

                     <div class="login-tabs" :style="{'--active-tab-left':activeTabLeft}">
                         <span class="tab" :class="{ active:activeTab === 'account'}" @click="setActiveTab('account')">账号登录</span>
                         <span class="tab" :class="{ active:activeTab === 'sms'}" @click="setActiveTab('sms')">&nbsp;&nbsp;手机短信登录</span>
                         <span class="tab":class="{ active:activeTab === 'wechat'}" @click="setActiveTab('wechat')">微信登录</span>
                     </div>

                     <div class="login-form" v-if="activeTab === 'account'">
                         <input type="text" placeholder="请输入邮箱/手机号/账号" v-model="account">
                         <input type="password" placeholder="请输入密码" v-model="password">

                         <div class="remember-me">
                             <input type="checkbox" id="auto-login" v-model="autoLogin">
                             <label for="auto-login" style="cursor: pointer;">下次自动登录</label>
                         </div>
                         <router-link to="/forgot-password" class="forgot-password" >忘记密码？</router-link>
                         <button @click="handleLogin">登录</button>
                         <p class="register-link">还没有账号？<router-link to="/register">去注册</router-link></p>
                     </div>

                     <div class="login-form" v-if="activeTab === 'sms'">
                         <input type="text" placeholder="请输入手机号" v-model="phone">
                         <div class="sms-code-container">
                             <input type="text" placeholder="请输入短信验证码" v-model="smsCode">
                             <button @click="sendSmsCode":disabled="countdown > 0">
                                  {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
                             </button>
                         </div>
                         <div class="remember-me">
                             <input type="checkbox" id="auto-login" v-model="autoLogin">
                             <label for="auto-login" style="cursor: pointer;">下次自动登录</label>
                         </div>
                         <router-link to="/forgot-password" class="forgot-password" >忘记密码？</router-link>
                         <button @click="handleLogin">登录</button>
                         <p class="register-link">还没有账号？<router-link to="/register">去注册</router-link></p>
                     </div>

                     <div class="wechat-login" v-if="activeTab === 'wechat'">
                         <div class="qr-code-placeholder">
                             <p>微信扫码登录</p>
                         </div>
                         <button @click="handleWechatLogin">微信一键登录</button>
                         <div class="remember-me">
                             <input type="checkbox" id="auto-login" v-model="autoLogin">
                             <label for="auto-login" style="cursor: pointer;">下次自动登录</label>
                         </div>
                     </div>
                 </div>
             </div>
         </template>

         <script setup>
         import { ref, computed } from 'vue';
         import { useRouter } from 'vue-router';
         import { useUserStore } from '@/store/user';
         import { login } from '@/api/user';
         import { ElMessage } from 'element-plus'

         const userStore = useUserStore();
         const router = useRouter();
         const activeTab = ref('account');
         const account = ref('');
         const password = ref('');
         const phone = ref('');
         const smsCode = ref('');
         const autoLogin = ref(false);
         const countdown = ref(0);
         let countdownInterval = null;

         const activeTabLeft = computed(() => {
         switch(activeTab.value){
             case 'account':return '16.666%';
             case 'sms':return '50%';
             case 'wechat': return '83.333%';
             default:return '16.666%';
         }
         });

         const setActiveTab = (tab) => {
         activeTab.value = tab;
         };

         const sendSmsCode = () => {
         if (countdown.value > 0) return;
         countdown.value = 60;
         countdownInterval = setInterval(() => {
             countdown.value--;
             if (countdown.value <= 0) {
             clearInterval(countdownInterval);
             }
         }, 1000);
         };

         const handleLogin = async () => {
         if (activeTab.value === 'account') {
             if (!account.value || !password.value) {
                 ElMessage.warning('账号密码不能为空')
                 return
             }
             const res = await login({account:account.value,password:password.value})
             userStore.set(res.data.token, res.data.user, autoLogin.value)
             ElMessage.success('登录成功')
             router.push('/main')
         }
         };

         const handleWechatLogin = () => {
         ElMessage.info('微信登录功能待后端对接')
         };
         </script>

         <style scoped>
         .container{
             display: flex;
             position: relative;
             left: 0px;
         }
         .image-section img{
             transform: scale(1);
             opacity: 0.95;
             height: auto;
             width: 750px;
             position: relative;
             left:-108px;
             top:-5px ;
         }
         .logo{
             transform: scale(1.13);
             position: relative;
             top: -20px;
             left: -60px;
         }
         .login-section{
             flex: 1;
             transform: scale(1.2);
             padding: 20px;
             background-color: white;
             position: relative;
             left: -60px;
             top:50px;
         }
         .login-tabs{
             display: flex;
             margin-bottom: 30px;
             font-size: 20px;
             position: relative;
             left: 0;
             top: 0;
             padding: 0;
         }
         .tab{
             flex: 1;
             text-align: center;
             padding: 5px 0;
             cursor: pointer;
             color: #777;
             font-weight: 400;
             margin:0;
         }
         .tab.active{
             margin-left: -10px;
         }
         .tab.active::after {
           content: '';
           position: absolute;
           left: var(--active-tab-left);
           bottom: -11px;
           width: 15%;
           height: 2px;
           background-color: #409eff;
           transform: translateX(-50%);
         }
         .login-form input{
             width: 90%;
             padding: 20px 15px;
             margin-bottom: 16px;
             border: 1px solid rgba(0,0,0,0.1);
             border-radius: 5px;
             font-size: 14px;
             position: relative;
             left: 15px;
         }
         .login-form input::placeholder{
             color:rgba(0,0,0,0.3);
             font-weight: 525;
         }
         .login-form input:focus{
             border: 1px solid #007bff;
             outline: none;
         }
         .login-form button{
             width: 90%;
             height: 50px;
             padding: 12;
             background-color: #007bff;
             color: white;
             border: none;
             border-radius: 5px;
             cursor: pointer;
             font-size: 16px;
             font-weight: 500;
             position: relative;
             left: 15px;
         }
         .login-form button:hover{
             background-color: rgba(0, 123, 255, 0.7);
         }
         .remember-me{
             color: #777;
             display: flex;
             margin: 15px 0;
             position: relative;
             left: 25px;
         }
         .remember-me input{
             width: auto;
             transform: scale(1.15);
             cursor: pointer;
             position: relative;
             top: 5px;
             left: -10px;
         }
         .forgot-password{
             color: #777;
             text-decoration: none;
             float: right;
             position: relative;
             top: -41px;
             left: -20px;
         }
         .register-link{
             margin-top: 20px;
             color:black;
             position: relative;
             left:260px;
             top:10px;
         }
         .register-link a{
             color: #007bff;
             text-decoration: none;
         }
         .sms-code-container{
             display: flex;
             width: 90%;
             margin-bottom: 20px;
             position: relative;
             left: 0px;
         }
         .sms-code-container input{
             flex: 2;
             margin-bottom: 11px;
             margin-right: 10px;
         }
         .sms-code-container button{
             flex: 0.8;
             height: 58px;
             background-color:#007bff;
             color: white;
             border: none;
             border-radius: 5px;
             font-size: 16px;
         }
         .wechat-login {
             display: flex;
             flex-direction: column;
             align-items: center;
             padding: 20px;
         }
         .wechat-login button {
             width: 90%;
             height: 50px;
             background-color: #07C160;
             color: white;
             border: none;
             border-radius: 5px;
             font-size: 16px;
             margin-bottom: 20px;
         }
         .qr-code-placeholder {
             width: 200px;
             height: 200px;
             background-color: #f5f5f5;
             display: flex;
             align-items: center;
             justify-content: center;
             color: #777;
         }
         </style>