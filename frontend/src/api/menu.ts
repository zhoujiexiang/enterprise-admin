import request from '@/utils/request'
import type { Result } from './types'
import type { SysMenu } from '@/types'

// 菜单新增/编辑参数
export interface MenuForm {
  menuId?: number
  parentId: number
  menuName: string
  path?: string
  component?: string
  perms?: string
  menuType: string
  icon?: string
  orderNum: number
  visible?: string
}

// 获取菜单树
export function menuTree(query?: string): Promise<Result<SysMenu[]>> {
  return request.get('/system/menu/tree', { params: { query } })
}

// 查询单个菜单
export function getMenu(id: number): Promise<Result<SysMenu>> {
  return request.get(`/system/menu/${id}`)
}

// 新增菜单
export function addMenu(data: MenuForm): Promise<Result> {
  return request.post('/system/menu', data)
}

// 修改菜单
export function updateMenu(data: MenuForm): Promise<Result> {
  return request.put('/system/menu', data)
}

// 删除菜单
export function deleteMenu(id: number): Promise<Result> {
  return request.delete(`/system/menu/${id}`)
}
