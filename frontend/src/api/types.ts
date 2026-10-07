// API 统一响应类型定义

/**
 * 统一响应结构
 */
export interface Result<T = any> {
  code: number
  msg: string
  data: T
}

/**
 * 分页响应结构
 */
export interface PageResult<T = any> {
  code: number
  msg: string
  data: {
    total: number
    rows: T[]
  }
}

/**
 * 分页查询参数
 */
export interface PageQuery {
  page: number
  size: number
  [key: string]: any
}
