// 业务类型定义（字段名与后端 Java 字段对应，使用驼峰命名）

/**
 * 系统用户
 */
export interface SysUser {
  userId: number
  username: string
  nickname: string
  avatar?: string
  phone?: string
  status: string
  roleIds?: number[]
  roleNames?: string[]
  createTime?: string
}

/**
 * 系统：角色
 */
export interface SysRole {
  roleId: number
  roleName: string
  roleKey: string
  status: string
  remark?: string
  menuIds?: number[]
  createTime?: string
}

/**
 * 系统：菜单
 */
export interface SysMenu {
  menuId: number
  parentId: number
  menuName: string
  path?: string
  component?: string
  perms?: string
  menuType: string // M 目录 C 菜单 F 按钮
  icon?: string
  orderNum: number
  visible?: string // 0 隐藏 1 显示
  status?: string
  children?: SysMenu[]
}

/**
 * 系统：部门
 */
export interface SysDept {
  deptId: number
  parentId: number
  deptName: string
  orderNum: number
  status: string
  children?: SysDept[]
}

/**
 * 员工
 */
export interface Employee {
  empId: number
  name: string
  gender: string
  phone: string
  email?: string
  deptId: number
  deptName?: string
  position: string
  salary: number
  hireDate: string
  status: string
  createTime?: string
}

/**
 * 员工查询参数
 */
export interface EmployeeQuery {
  page?: number
  size?: number
  name?: string
  deptId?: number
  status?: string
  startDate?: string
  endDate?: string
}

/**
 * 动态路由项（后端返回结构）
 */
export interface RouterItem {
  name: string
  path: string
  component?: string
  hidden?: boolean
  redirect?: string
  meta?: {
    title?: string
    icon?: string
    noCache?: boolean
    [key: string]: any
  }
  children?: RouterItem[]
}

/**
 * 大屏概览
 */
export interface DashboardOverview {
  totalEmp: number
  activeEmp: number
  deptCount: number
  monthNew: number
}

/**
 * 趋势项
 */
export interface TrendItem {
  month: string
  count: number
}

/**
 * 部门占比项
 */
export interface DeptRatioItem {
  deptName: string
  count: number
}

/**
 * 薪资排行项
 */
export interface SalaryRankItem {
  deptName: string
  avgSalary: number
}
