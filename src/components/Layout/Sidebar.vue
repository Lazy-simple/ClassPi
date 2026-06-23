<template>
  <el-menu :default-active="activePath" router background="#304156" text-color="#fff">
    <el-menu-item index="/main/dashboard">数据驾驶舱</el-menu-item>
    <el-sub-menu v-if="userStore.userInfo?.role === 1" index="teacher">
      <template #title>教师功能</template>
      <el-menu-item index="/main/teacher-course">课程管理</el-menu-item>
      <el-menu-item index="/main/publish-homework">发布作业</el-menu-item>
      <el-menu-item index="/main/check-homework">批阅作业</el-menu-item>
      <el-menu-item index="/main/score">成绩管理</el-menu-item>
    </el-sub-menu>
    <el-sub-menu v-if="userStore.userInfo?.role === 2" index="student">
      <template #title>学生功能</template>
      <el-menu-item index="/main/student-course">选课中心</el-menu-item>
      <el-menu-item index="/main/submit-homework">提交作业</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>
<script setup>
import { useUserStore } from '@/store/user'
import { useRoute } from 'vue-router'
const userStore = useUserStore()
const route = useRoute()
const activePath = route.path
</script>