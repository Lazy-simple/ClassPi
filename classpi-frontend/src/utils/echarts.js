import * as echarts from 'echarts'

export function initEcharts(dom) {
  return echarts.init(dom)
}

export function resizeChart(myChart) {
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}