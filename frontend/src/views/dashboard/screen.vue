<template>
  <div class="screen-container">
    <header class="screen-header">
      <h1 class="screen-title">企业数据可视化大屏</h1>
      <div class="screen-time">{{ currentTime }}</div>
    </header>

    <!-- 指标卡 -->
    <div class="metrics-row">
      <div class="metric-card" v-for="card in metricCards" :key="card.title">
        <div class="metric-icon">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-value">{{ card.value }}</div>
          <div class="metric-title">{{ card.title }}</div>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="charts-row">
      <div class="chart-box">
        <div class="chart-title">员工入职趋势</div>
        <div ref="trendRef" class="chart-canvas"></div>
      </div>
      <div class="chart-box">
        <div class="chart-title">部门人数占比</div>
        <div ref="ratioRef" class="chart-canvas"></div>
      </div>
      <div class="chart-box">
        <div class="chart-title">部门薪资排行</div>
        <div ref="salaryRef" class="chart-canvas"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import {
  getOverview,
  getTrend,
  getDeptRatio,
  getSalaryRank
} from '@/api/dashboard'
import type { DashboardOverview, TrendItem, DeptRatioItem, SalaryRankItem } from '@/types'

// 概览数据
const overview = ref<DashboardOverview>({ totalEmp: 0, activeEmp: 0, deptCount: 0, monthNew: 0 })
const trendList = ref<TrendItem[]>([])
const deptRatioList = ref<DeptRatioItem[]>([])
const salaryRankList = ref<SalaryRankItem[]>([])

// 当前时间
const currentTime = ref<string>('')

let timer: number | undefined

function updateTime(): void {
  const d = new Date()
  const pad = (n: number): string => (n < 10 ? '0' + n : '' + n)
  currentTime.value = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// 指标卡
const metricCards = computed(() => [
  { title: '员工总数', value: overview.value.totalEmp, icon: 'User' },
  { title: '在职员工', value: overview.value.activeEmp, icon: 'UserFilled' },
  { title: '部门数量', value: overview.value.deptCount, icon: 'OfficeBuilding' },
  { title: '本月入职', value: overview.value.monthNew, icon: 'Calendar' }
])

// 图表实例
let trendChart: echarts.ECharts | null = null
let ratioChart: echarts.ECharts | null = null
let salaryChart: echarts.ECharts | null = null

const trendRef = ref<HTMLElement | null>(null)
const ratioRef = ref<HTMLElement | null>(null)
const salaryRef = ref<HTMLElement | null>(null)

// 初始化趋势图
function initTrendChart(): void {
  if (!trendRef.value) return
  trendChart = echarts.init(trendRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '8%', right: '5%', bottom: '12%', top: '12%' },
    xAxis: {
      type: 'category',
      data: trendList.value.map((i) => i.month),
      axisLine: { lineStyle: { color: '#7e8db3' } },
      axisLabel: { color: '#a3b0d6' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#7e8db3' } },
      axisLabel: { color: '#a3b0d6' },
      splitLine: { lineStyle: { color: 'rgba(126,141,179,0.2)' } }
    },
    series: [
      {
        name: '入职人数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3, color: '#00d4ff' },
        itemStyle: { color: '#00d4ff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0,212,255,0.5)' },
            { offset: 1, color: 'rgba(0,212,255,0.05)' }
          ])
        },
        data: trendList.value.map((i) => i.count)
      }
    ]
  })
}

// 初始化部门占比图
function initRatioChart(): void {
  if (!ratioRef.value) return
  ratioChart = echarts.init(ratioRef.value)
  ratioChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'middle',
      textStyle: { color: '#a3b0d6' }
    },
    series: [
      {
        name: '部门占比',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        label: { color: '#a3b0d6' },
        itemStyle: { borderColor: '#0a1b3a', borderWidth: 2 },
        data: deptRatioList.value.map((i) => ({
          name: i.deptName,
          value: i.count
        }))
      }
    ]
  })
}

// 初始化薪资排行图
function initSalaryChart(): void {
  if (!salaryRef.value) return
  salaryChart = echarts.init(salaryRef.value)
  salaryChart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c} 元' },
    grid: { left: '15%', right: '5%', bottom: '12%', top: '12%' },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#7e8db3' } },
      axisLabel: { color: '#a3b0d6' },
      splitLine: { lineStyle: { color: 'rgba(126,141,179,0.2)' } }
    },
    yAxis: {
      type: 'category',
      data: salaryRankList.value.map((i) => i.deptName),
      axisLine: { lineStyle: { color: '#7e8db3' } },
      axisLabel: { color: '#a3b0d6' }
    },
    series: [
      {
        name: '平均薪资',
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#1e8fff' },
            { offset: 1, color: '#00d4ff' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: { show: true, position: 'right', color: '#a3b0d6' },
        data: salaryRankList.value.map((i) => i.avgSalary)
      }
    ]
  })
}

// 加载数据
async function loadData(): Promise<void> {
  try {
    const [overviewRes, trendRes, ratioRes, salaryRes] = await Promise.all([
      getOverview(),
      getTrend(),
      getDeptRatio(),
      getSalaryRank()
    ])
    overview.value = overviewRes.data
    trendList.value = trendRes.data || []
    deptRatioList.value = ratioRes.data || []
    salaryRankList.value = salaryRes.data || []
    initTrendChart()
    initRatioChart()
    initSalaryChart()
  } catch (e) {
    // 错误已在拦截器处理
  }
}

// 窗口大小变化时重绘
function handleResize(): void {
  trendChart?.resize()
  ratioChart?.resize()
  salaryChart?.resize()
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  ratioChart?.dispose()
  salaryChart?.dispose()
  trendChart = null
  ratioChart = null
  salaryChart = null
})
</script>

<style scoped>
.screen-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(160deg, #0a1b3a 0%, #0d1f4a 60%, #0a1830 100%);
  color: #fff;
  padding: 16px;
  box-sizing: border-box;
}
.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  margin-bottom: 16px;
}
.screen-title {
  font-size: 28px;
  letter-spacing: 4px;
  color: #00d4ff;
  text-shadow: 0 0 12px rgba(0, 212, 255, 0.6);
}
.screen-time {
  font-size: 16px;
  color: #a3b0d6;
}
.metrics-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}
.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: rgba(16, 42, 89, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 8px;
}
.metric-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1e8fff, #00d4ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}
.metric-value {
  font-size: 32px;
  font-weight: 700;
  color: #00d4ff;
  line-height: 1.2;
  text-align: center;
}
.metric-title {
  font-size: 14px;
  color: #a3b0d6;
  margin-top: 4px;
  text-align: center;
}
.charts-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  height: 380px;
}
.chart-box {
  background: rgba(16, 42, 89, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
}
.chart-title {
  font-size: 16px;
  color: #00d4ff;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  margin-bottom: 8px;
  text-align: center;
}
.chart-canvas {
  flex: 1;
  width: 100%;
}
</style>
