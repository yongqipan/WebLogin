export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

export interface Bug {
  id: number
  title: string
  description: string | null
  type: string
  status: string
  severity: string
  creator: string
  assignee: string | null
  createdAt: string
  updatedAt: string
}

export interface Change {
  field: string
  old: unknown
  new: unknown
}

export interface BugHistoryItem {
  id: number
  bugId: number
  operator: string
  operatedAt: string
  changes: Change[]
}

export interface BugListResult {
  total: number
  list: Bug[]
}

function redirectToLogin(): void {
  localStorage.removeItem('login_user')
  localStorage.removeItem('login_role')
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<ApiResult<T>> {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  if (res.status === 401) {
    redirectToLogin()
    throw new Error('未登录')
  }

  let json: ApiResult<T>
  try {
    json = (await res.json()) as ApiResult<T>
  } catch {
    throw new Error('服务器响应异常')
  }

  if (json.code !== 0) {
    throw new Error(json.message || '请求失败')
  }
  return json
}

export function get<T>(path: string): Promise<ApiResult<T>> {
  return request<T>(path)
}

export function post<T>(path: string, body: unknown): Promise<ApiResult<T>> {
  return request<T>(path, { method: 'POST', body: JSON.stringify(body) })
}

export function put<T>(path: string, body: unknown): Promise<ApiResult<T>> {
  return request<T>(path, { method: 'PUT', body: JSON.stringify(body) })
}

export function del<T>(path: string): Promise<ApiResult<T>> {
  return request<T>(path, { method: 'DELETE' })
}

export const TYPE_LIST = ['缺陷', '新功能']
export const STATUS_LIST = ['打开', '处理中', '已修复', '已关闭']
export const SEVERITY_LIST = ['轻微', '一般', '严重', '致命']
export const FIELD_LABELS: Record<string, string> = {
  title: '标题',
  description: '描述',
  type: '类型',
  status: '状态',
  severity: '严重程度',
  assignee: '指派处理人',
}

export type UserRole = 'admin' | 'user'

export interface AdminUser {
  id: number
  username: string
  role: UserRole
}

export const USER_ROLES: UserRole[] = ['admin', 'user']

export const ROLE_LABELS: Record<UserRole, string> = {
  admin: '管理员',
  user: '普通用户',
}
