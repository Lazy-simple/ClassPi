<template>
  <el-table border :data="tableData">
    <el-table-column v-for="col in columns" :key="col.prop" v-bind="col" />
    <slot name="operate"></slot>
  </el-table>
</template>
<script setup>
defineProps(['tableData', 'columns'])
</script>