<template>
  <div class="not-found-container">
    <el-empty description="哎呀，页面走丢了...">
      <template #image>
        <!-- 这里可以用个简单的 SVG 或者保持默认 -->
        <el-icon size="100" color="#dcdfe6"><WarnTriangleFilled /></el-icon>
      </template>
      <el-button type="primary" round @click="$router.push('/login')">
        返回登录页
      </el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { WarnTriangleFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
}
</style>