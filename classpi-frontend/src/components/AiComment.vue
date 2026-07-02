<template>
  <div class="ai-comment-content" v-if="aiContent">
    <div v-for="(line, index) in lines" :key="index" class="comment-line">
      <span v-if="line.startsWith('**')" class="bold">{{ line.replace(/\*\*/g, '') }}</span>
      <span v-else>{{ line }}</span>
    </div>
  </div>
  <div v-else class="empty-tip">暂无评价内容</div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  aiContent: {
    type: String,
    default: ''
  }
})

const lines = computed(() => {
  if (!props.aiContent) return []
  return props.aiContent.split('\n').filter(line => line.trim())
})
</script>

<style scoped>
.ai-comment-content {
  white-space: pre-wrap;
  line-height: 1.8;
  font-size: 15px;
  padding: 10px;
}

.comment-line {
  margin-bottom: 4px;
}

.bold {
  font-weight: 600;
  color: #303133;
}

.empty-tip {
  color: #909399;
  text-align: center;
  padding: 20px;
}
</style>