<template>
  <el-card shadow="hover">
    <template #header>🤖 AI助教智能评语</template>
    <div class="ai-text">{{ aiContent }}</div>
  </el-card>
</template>
<script setup>
defineProps({
  aiContent: {
    type: String,
    default: '暂无AI评价'
  }
})
</script>
<style scoped>
.ai-text {
  line-height: 1.8;
  color: #333;
}
</style>