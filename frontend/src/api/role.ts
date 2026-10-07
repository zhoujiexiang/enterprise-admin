import request from '@/utils/request'
import type { Result, PageResult, PageQuery } from './types'
import type { SysRole } from '@/types'

// 角色查询参数
export interface RoleQuery extends PageQuery {
  roleName?: string
  roleKey?: string
  status?: string
}

// 角色新增/编辑参数
export interface RoleForm {
  roleId?: number
  roleName?: string
  roleKey?: string
  status?: string
  remark?: string
  menuIds?: number[]
}

// 分页查询角色
export function pageRole(params: RoleQuery): Promise<PageResult<SysRole>> {
  return request.get('/system/role/page', { params })
}

// 查询单个角色
export function getRole(id: number): Promise<Result<SysRole>> {
  return request.get(`/system/role/${id}`)
}

// 新增角色
export function addRole(data: RoleForm): Promise<Result> {
  return request.post('/system/role', data)
}

// 修改角色
export function updateRole(data: RoleForm): Promise<Result> {
  return request.put('/system/role', data)
}

// 删除角色
export function deleteRole(ids: string): Promise<Result> {
  return request.delete(`/system/role/${ids}`)
}

// 获取角色的菜单 ID 列表
export function getRoleMenuIds(id: number): Promise<Result<number[]>> {
  return request.get(`/system/role/${id}/menuIds`)
}

// 给角色分配菜单
export function assignRoleMenus(id: number, menuIds: number[]): Promise<Result> {
  return request.put(`/system/role/${id}/menus`, menuIds)
}
