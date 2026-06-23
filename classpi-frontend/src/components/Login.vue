<template>
    <div class="container">
        <div class="logo">
            <img src="@/assets/image/logo.png" width="150">
        </div>
        <div class="image-section">    
            <img src="@/assets/image/login.png">
        </div>

        <div class="login-section" style="width: 470px; height: 520px;">
            <h2 style="margin-bottom: 20px; color: black;text-align: center;">账号登录</h2>
            
            <!-- 登陆标签切换 -->
             <div class="login-tabs" :style="{'--active-tab-left':activeTabLeft}">
                <span class="tab" :class="{ active:activeTab === 'account'}" @click="setActiveTab('account')">账号登录</span> 
                <span class="tab" :class="{ active:activeTab === 'sms'}" @click="setActiveTab('sms')">&nbsp;&nbsp;手机短信登录</span> 
                <span class="tab":class="{ active:activeTab === 'wechat'}" @click="setActiveTab('wechat')">微信登录</span>
            </div>

            <!-- 账号登陆表单 -->
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

            <!-- 短信验证码登录表单 -->
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

            <!-- 微信扫码登录表单 -->
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

<script>
    import { ref, computed } from 'vue';
    import { useRouter } from 'vue-router';
    import { useUserStore } from '@/store/userStore'; //导入Pinia存储
    import authService from '@/services/authService';

    export default {
    setup() {

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

       // 账号登录逻辑
        // const handleLogin = () => {
        //     if (activeTab.value === 'account') {
        //         const users = JSON.parse(localStorage.getItem('users')) || [];
        //         const user = users.find(u => u.email === account.value && u.password === password.value);
        //         if (user) {
        //         // 1. 模拟后端生成 Token（实际项目由后端返回）
        //         const token = 'mock-token-' + Date.now(); // 包含时间戳，模拟有效期
                
        //         // 2. 存储 Token 和用户信息（根据 "自动登录" 选择存储方式）
        //         userStore.setUser(token, user, autoLogin.value);
                
        //         // 3. 跳转至主页面
        //         router.push('/main-page');
        //         alert('登录成功');
        //         } else {
        //         alert('账号或密码错误');
        //         }
        //     }
        // };


      const handleLogin = () => {
        if (activeTab.value === 'account') {
          // 显示加载状态
          console.log('开始登录...', account.value);

          authService.login(account.value, password.value)
              .then(response => {
                console.log('登录成功:', response);

                // 存储用户信息到 Pinia
                userStore.setUser(
                    response.token,
                    response.user,
                    autoLogin.value
                );

                // 跳转到主页面
                router.push('/main-page');
                alert('登录成功！欢迎 ' + (response.user?.name || response.user?.username || '用户'));
              })
              .catch(error => {
                console.error('登录失败:', error);
                alert(error.message || '账号或密码错误');
              });
        }
      };


        const handleWechatLogin = () => {
        console.log('微信登录请求');
        };

        return {
        activeTab,
        activeTabLeft,
        account,
        password,
        phone,
        smsCode,
        autoLogin,
        countdown,
        setActiveTab,
        sendSmsCode,
        handleLogin,
        handleWechatLogin
        };
    }
    };
</script>

<style scoped>
    /*容器布局：使用Flexibox实现左右排列*/
.container{
    display: flex;
    position: relative;
    left: 0px;
}
/*左侧图片区域*/
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
/*右侧登陆区域*/
.login-section{
    flex: 1;
    transform: scale(1.2);
    padding: 20px;
    background-color: white;
    position: relative;
    left: -60px;
    top:50px;
}
/*登陆标签样式*/
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
  width: 15%;      /* 下划线宽度 */
  height: 2px;     /* 下划线粗细 */
  background-color: #409eff; /* 下划线颜色 */
  transform: translateX(-50%);  /*水平居中 */
}
/*登陆表单样式*/
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
    padding: 12px;
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
    cursor: pointer;
    background-color: rgba(0, 123, 255, 0.7);
}

.remember-me{
    color: #777;
    display: flex;
    /*align-items: center;*/
    margin: 15px 0;
    position: relative;
    left: 25px;
}
.remember-me:focus{
    outline: none;
    color: #007bff;
}
.remember-me input{
    width: auto;
    transform: scale(1.15);
    cursor: pointer;
    position: relative;
    top: 5px;
    left: -10px;
}
.remember-me input:focus{
    outline: none;
    color: #007bff;
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

/*短信登陆*/
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
    cursor: pointer;
    font-size: 16px;
}
.sms-code-container button:hover{
    background-color: #007bff;
    border: 1px solid #007bff;
    outline: none;
}

/*微信登陆*/
.wechat-login {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
}
.wechat-login button {
    width: 90%;
    height: 50px;
    padding: 12px;
    background-color: #07C160;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 20px;
}
.qr-code-placeholder {
    width: 200px;
    height: 200px;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    color: #777;
}
</style>