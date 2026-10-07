import request from '@/utils/request'
import type { Result } from './types'

// 登录请求参数
export interface LoginData {
  username: string
  password: string
  captcha: string
  captchaKey: string
}

// 验证码响应
export interface CaptchaResult {
  captchaKey: string
  img: string
}

// 登录响应
export interface LoginResult {
  token: string
}

// 获取用户信息响应
export interface GetInfoResult {
  user: {
    userId: number
    username: string
    nickname: string
    avatar: string
    phone: string
    status: string
  }
  roles: string[]
  permissions: string[]
}

// 获取验证码
export function getCaptcha(): Promise<Result<CaptchaResult>> {
  return request.get('/auth/captcha')
}

// 登录
export function login(data: LoginData): Promise<Result<LoginResult>> {
  return request.post('/auth/login', data)
}

// 获取用户信息
export function getInfo(): Promise<Result<GetInfoResult>> {
  return request.get('/auth/getInfo')
}

// 获取动态路由
export function getRouters(): Promise<Result<any[]>> {
  return request.get('/auth/getRouters')
}

// 退出登录
export function logout(): Promise<Result> {
  return request.post('/auth/logout')
}
