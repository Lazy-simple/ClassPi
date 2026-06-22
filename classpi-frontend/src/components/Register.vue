<template>
    <div class="container">
        <div class="logo">
            <img src="@/assets/image/logo.png" width="150">
        </div>
        <div class="image-section">    
            <img src="@/assets/image/register.png">
        </div>

         <div class="login-section" style="width: 500px; height: 780px;">
            <h2 style="margin-bottom: 20px; color: black;text-align: center;">注册账号</h2>

            <!-- 注册表单 -->
            <div class="login-form">
                <input type="text" placeholder="请输入邮箱/手机号/账号" v-model="account">
                <input type="password" placeholder="请输入密码" v-model="password">
                <input type="password" placeholder="请再次输入密码确认" v-model="confirmPassword">
            
                <h3 style="margin-left: 15px;color: black;text-align: left;margin-bottom: 16px;">选择身份</h3>
                
                <!-- 身份选择按钮 -->
                 <div class="identity-group">
                    <button
                        class="identity-btn"
                        :class="{ active:selectedIdentity === 'teacher' }"
                        @click="selectedIdentity = 'teacher'"
                    >
                        <img src="@/assets/image/teacher.png" width="50"><span>老师</span>
                    </button>
                    <button
                        class="identity-btn"
                        :class="{ active:selectedIdentity === 'student' }"
                        @click="selectedIdentity = 'student'"
                    >
                        <img src="@/assets/image/student.png" width="50"><span>学生</span>
                    </button>
                 </div>

                <input type="text" placeholder="请输入姓名" v-model="name">
                <input type="text" placeholder="请输入学校/机构" v-model="school">

                 <input 
                    type="text" 
                    placeholder="请输入学号"
                    v-if="selectedIdentity === 'student'"
                    v-model="studentId"
                >

                <div class="calc-container">
                    <input type="text" placeholder="请输入计算结果" v-model="calcResult">
                    <div class="image-cal"> 
                        <img src="@/assets/image/cal.png">
                    </div>
                </div>
               

                <button @click="handleRegister">注册</button>
                <p class="register-link">已有账号？<router-link to="/login">去登录</router-link></p>
            </div>
        </div>
    </div>
</template>

<script>
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import registerService from '@/services/registerService';

    export default {
        setup() {
            const router = useRouter();
            const selectedIdentity = ref('teacher');
            const account = ref('');
            const password = ref('');
            const confirmPassword = ref('');
            const name = ref('');
            const school = ref('');
            const studentId = ref('');
    

            const handleRegister = async() => {
            if (password.value !== confirmPassword.value) {
                alert('两次输入的密码不一致');
                return;
            }
            
           try {
                const response = await registerService.register(
                account.value,
                password.value,
                selectedIdentity.value,
                name.value,
                school.value,
                studentId.value
                );
                alert('注册成功！');
                router.push('/login');
            } catch (error) {
                // 显示具体错误信息
                alert(error.response?.data?.message || '注册失败：' + error.message);
            }
            };
            // const users = JSON.parse(localStorage.getItem('users')) || [];
            // const newUser = {
            //     email: email.value,
            //     password: password.value,
            //     identity: selectedIdentity.value,
            //     name: name.value,
            //     school: school.value,
            //     studentId: studentId.value
            // };
            // users.push(newUser);
            // localStorage.setItem('users', JSON.stringify(users));
            // alert('注册成功，请登录');
            // router.push('/login');
            // };

            return {
            selectedIdentity,
            account,
            password,
            confirmPassword,
            name,
            school,
            studentId,
            handleRegister
            };
        }
    };
</script>

<style scoped>
    /*容器布局：使用Flexibox实现左右排列*/
.container{
    display: flex;
}
/*左侧图片区域*/
.image-section img{
    opacity: 0.95;
    transform: scale(1.2);
    background-color: rgba(0,0,0,0);
    height: auto;
    width: 750px;
    position: relative;
    top:90px;
    left: -100px;
}
.logo{
    transform: scale(1.17);
    position: relative;
    top: 46px;
    left: -46px;
}
/*右侧登陆区域*/
.login-section{
    flex: 1;
    transform: scale(1.1);
    padding: 20px;
    background-color: white;
    position: relative;
    left: 0px;
    top:0px;
}
/*登陆表单样式*/
.login-form input{
    width: 90%;
    padding: 15px 15px;
    margin-bottom: 16px;
    border: 1px solid rgba(0,0,0,0.1);
    border-radius: 5px;
    font-size: 18px;
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
.identity-group{
    display: flex;
    width: 85%;
    margin-left: 11px;
    margin-bottom: 16px;
    gap: 20px;/*按钮间距*/
}
.identity-group button{
    transform: scale(1.1);
    height: 65px;
    padding: 12px;
    background-color:white;
    color: black;
    border: none;
    border-radius: 0px;
}
.identity-group button:hover{
    cursor: default;
    background-color: white;
}
.identity-group button:focus{
    border:1px solid #007bff;
    background-color: white;
}
.identity-group span{
    font-size: 15px;
    font-weight: 400;
    position: relative;
    top:-25px;
    left: -18px;
}
.identity-group img{
    position: relative;
    top: -5px;
    left: -30px;
}
.calc-container{
    display: flex;
    width: 90%;
    margin-left: 15px;
    margin-bottom: 16px;
    gap: 10px;
}
.calc-container input{
    flex: 1;
    margin-bottom: 0;
    left: 0;
}
.calc-container .image-cal{
    flex: 1;
    margin-left: 15px;
    height: 60px;
}
.calc-container .image-cal img{
    width: 100%;
    height: 100%;
    object-fit: cover;
    border: 1px solid rgba(0,0,0,0.1);
    border-radius: 5px;
}
.register-link{
    margin-top: 20px;
    color: black;
    position: relative;
    left:300px;
    top:-26px;
}
.register-link a{
    color: #007bff;
    text-decoration: none;
}
</style>