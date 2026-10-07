import request from '@/utils/request'
import type { Result, PageResult, PageQuery } from './types'
import type { SysUser } from '@/types'

// 用户查询参数
export interface UserQuery extends PageQuery {
  username?: string
  phone?: string
  status?: string
}

// 用户新增/编辑参数
export interface UserForm {
  userId?: number
  username?: string
  password?: string
  nickname?: string
  phone?: string
  status?: string
  roleIds?: number[]
}

// 分页查询用户
export function pageUser(params: UserQuery): Promise<PageResult<SysUser>> {
  return request.get('/system/user/page', { params })
}

// 查询单个用户
export function getUser(id: number): Promise<Result<SysUser>> {
  return request.get(`/system/user/${id}`)
}

// 新增用户
export function addUser(data: UserForm): Promise<Result> {
  return request.post('/system/user', data)
}

// 修改用户
export function updateUser(data: UserForm): Promise<Result> {
  return request.put('/system/user', data)
}

// 删除用户（ids 逗号分隔）
export function deleteUser(ids: string): Promise<Result> {
  return request.delete(`/system/user/${ids}`)
}

// 重置密码（需校验原密码）
export function resetPwdUser(data: { userId: number; oldPassword: string; newPassword: string }): Promise<Result> {
  return request.put('/system/user/resetPwd', data)
}

// 切换用户状态
export function changeUserStatus(userId: number, status: string): Promise<Result> {
  return request.put('/system/user/changeStatus', { userId, status })
}
