import request from '@/utils/request'
import type { Result, PageResult, PageQuery } from './types'
import type { Employee } from '@/types'

// 员工查询参数
export interface EmployeeQuery extends PageQuery {
  name?: string
  deptId?: number
  status?: string
  startDate?: string
  endDate?: string
}

// 员工新增/编辑参数
export interface EmployeeForm {
  empId?: number
  name: string
  gender: string
  phone: string
  email?: string
  deptId: number
  position: string
  salary: number
  hireDate: string
  status: string
}

// 分页查询员工
export function pageEmployee(params: EmployeeQuery): Promise<PageResult<Employee>> {
  return request.get('/employee/page', { params })
}

// 查询单个员工
export function getEmployee(id: number): Promise<Result<Employee>> {
  return request.get(`/employee/${id}`)
}

// 新增员工
export function addEmployee(data: EmployeeForm): Promise<Result> {
  return request.post('/employee', data)
}

// 修改员工
export function updateEmployee(data: EmployeeForm): Promise<Result> {
  return request.put('/employee', data)
}

// 删除员工
export function deleteEmployee(ids: string): Promise<Result> {
  return request.delete(`/employee/${ids}`)
}

// 切换员工状态
export function changeEmployeeStatus(empId: number, status: string): Promise<Result> {
  return request.put('/employee/changeStatus', { empId, status })
}

// 导出员工 Excel（返回 Blob 文件流）
export function exportEmployee(params: EmployeeQuery): Promise<Blob> {
  return request.get('/employee/export', {
    params,
    responseType: 'blob'
  }) as unknown as Promise<Blob>
}
