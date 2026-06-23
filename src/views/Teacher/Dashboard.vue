<template>
  <div class="dash-wrap">
    <el-card title="班级学习活跃度热力图">
      <div id="heatChart" style="width:100%;height:420px;"></div>
    </el-card>
  </div>
</template>
<script setup>
import { onMounted } from 'vue'
import { initEcharts, resizeChart } from '@/utils/echarts'
import { getHeatData } from '@/api/statistics'
let chart = null
onMounted(async ()=>{
  const res = await getHeatData(1)
  const data = res.data
  const dom = document.getElementById('heatChart')
  chart = initEcharts(dom)
  resizeChart(chart)
  const option = {
    title:{text:'每日作业提交活跃度'},
    tooltip:{},
    xAxis:{type:'category',data:data.xData},
    yAxis:{type:'category',data:data.yData},
    visualMap:{min:0,max:100,orient:'horizontal',bottom:'5%'},
    series:[{
      type:'heatmap',
      name:'活跃度',
      data:data.seriesData,
      label:{show:true}
    }]
  }
  chart.setOption(option)
})
</script>
<style scoped>
.dash-wrap {padding:20px;}
</style>