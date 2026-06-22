<template>
    <div class="logo">
        <img src="@/assets/image/logo.png" width="150">
    </div>

    <div class="login-section">
        <h2 style="margin-bottom: 10px; color: black;text-align: center;font-weight: 400;">忘记密码</h2>
        <div class="login-form">
            <input type="text" placeholder="请输入邮箱/手机号">

            <button>下一步</button>
            <p class="register-link">以上方式均不可用您可以进行？<router-link to="../assets/image/cal.png">申诉</router-link></p>
        </div>
    </div>
</template>

<script>
    import { ref } from 'vue';

    export default {
        setup() {
            const email = ref('');

            const handleNext = () => {
            console.log('发送重置密码链接到:', email.value);
            alert('重置密码链接已发送到您的邮箱');
            };

            return {
            email,
            handleNext
            };
        }
    };
</script>

<style scoped>
.logo{
    transform: scale(1.1);
    position: relative;
    top: -280px;
    left: -600px;
}

.login-section{
    transform: scale(1.15);
    width: 150%;
    padding: 20px;
    background-color: white;
    position: relative;
    left: -80px;
}
.login-form input{
    width: 90%;
    padding: 15px 15px;
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
    margin-bottom: 16px;
}
.login-form button:hover{
    cursor: pointer;
    background-color: rgba(0, 123, 255, 0.7);
}
.register-link{
    margin-top: -10px;
    color: black;
    position: relative;
    left:155px;
    top:5px;
}
.register-link a{
    color: #007bff;
    text-decoration: none;
}
</style>