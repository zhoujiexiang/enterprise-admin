import request from '@/utils/request'
import type { Result } from './types'

// 文件上传响应
export interface UploadResult {
  url: string
}

// 上传文件（form-data，field: file）
export function uploadFile(file: File): Promise<Result<UploadResult>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
