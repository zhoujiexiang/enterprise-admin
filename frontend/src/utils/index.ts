// 通用工具函数

/**
 * 格式化日期：YYYY-MM-DD HH:mm:ss
 */
export function formatDate(date: Date | string | number | undefined | null): string {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  const pad = (n: number): string => (n < 10 ? '0' + n : '' + n)
  const year = d.getFullYear()
  const month = pad(d.getMonth() + 1)
  const day = pad(d.getDate())
  const hour = pad(d.getHours())
  const minute = pad(d.getMinutes())
  const second = pad(d.getSeconds())
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

/**
 * 仅日期格式：YYYY-MM-DD
 */
export function formatDateOnly(date: Date | string | number | undefined | null): string {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  const pad = (n: number): string => (n < 10 ? '0' + n : '' + n)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

/**
 * 通过 Blob 下载文件
 */
export function downloadBlob(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  // 释放 URL 对象
  URL.revokeObjectURL(url)
}
