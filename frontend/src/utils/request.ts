import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import type { Result } from '@/api/types'

// Axios 实例封装
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器：附加 JWT Token
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: any) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理响应与错误
service.interceptors.response.use(
  (response: AxiosResponse<Result<any>>) => {
    const res = response.data
    // 处理 blob 文件流（导出 Excel 等）
    if (response.config.responseType === 'blob') {
      return response.data as any
    }
    const code = res.code || 200
    if (code === 200) {
      return res as any
    }
    // 401 未登录或登录过期
    if (code === 401) {
      ElMessage.error('登录状态已过期，请重新登录')
      removeToken()
      // 避免循环引用，使用 location 跳转
      setTimeout(() => {
        location.href = '/login'
      }, 1000)
      return Promise.reject(new Error('未授权'))
    }
    // 其他业务错误
    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg || '操作失败'))
  },
  (error: any) => {
    let message = '网络异常'
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        message = '登录状态已过期，请重新登录'
        removeToken()
        setTimeout(() => {
          location.href = '/login'
        }, 1000)
      } else if (status === 403) {
        message = '没有权限访问该资源'
      } else if (status === 404) {
        message = '请求的资源不存在'
      } else if (status >= 500) {
        message = '服务器内部错误'
      } else {
        message = error.response.data?.msg || '请求失败'
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
