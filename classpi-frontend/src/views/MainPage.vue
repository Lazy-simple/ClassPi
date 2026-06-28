<template>
  <el-container style="height: 100vh; background-color: #f3f4f6;">
    <el-container>
      <el-header style="padding: 0; height: 60px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <Header />
      </el-header>
      <el-main style="padding: 20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Header from '@/components/Layout/Header.vue'
</script>