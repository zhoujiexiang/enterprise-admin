import request from '@/utils/request'
import type { Result } from './types'
import type { DashboardOverview, TrendItem, DeptRatioItem, SalaryRankItem } from '@/types'

// 大屏概览数据
export function getOverview(): Promise<Result<DashboardOverview>> {
  return request.get('/dashboard/overview')
}

// 员工入职趋势
export function getTrend(): Promise<Result<TrendItem[]>> {
  return request.get('/dashboard/trend')
}

// 部门人数占比
export function getDeptRatio(): Promise<Result<DeptRatioItem[]>> {
  return request.get('/dashboard/deptRatio')
}

// 部门薪资排行
export function getSalaryRank(): Promise<Result<SalaryRankItem[]>> {
  return request.get('/dashboard/salaryRank')
}
