import request from '@/utils/request'
import type { Result } from './types'
import type { SysDept } from '@/types'

// 部门新增/编辑参数
export interface DeptForm {
  deptId?: number
  parentId: number
  deptName: string
  orderNum: number
  status: string
}

// 获取部门树
export function deptTree(): Promise<Result<SysDept[]>> {
  return request.get('/system/dept/tree')
}

// 查询单个部门
export function getDept(id: number): Promise<Result<SysDept>> {
  return request.get(`/system/dept/${id}`)
}

// 新增部门
export function addDept(data: DeptForm): Promise<Result> {
  return request.post('/system/dept', data)
}

// 修改部门
export function updateDept(data: DeptForm): Promise<Result> {
  return request.put('/system/dept', data)
}

// 删除部门
export function deleteDept(id: number): Promise<Result> {
  return request.delete(`/system/dept/${id}`)
}
