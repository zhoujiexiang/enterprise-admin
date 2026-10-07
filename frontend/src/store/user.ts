import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCaptcha, login, getInfo, getRouters, logout } from '@/api/auth'
import type { LoginData } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import type { RouterItem } from '@/types'

// 用户状态管理
export const useUserStore = defineStore(
  'user',
  () => {
    // state
    const token = ref<string>(getToken() || '')
    const userId = ref<number>(0)
    const username = ref<string>('')
    const nickname = ref<string>('')
    const avatar = ref<string>('')
    const phone = ref<string>('')
    const status = ref<string>('')
    const roles = ref<string[]>([])
    const permissions = ref<string[]>([])
    const routers = ref<RouterItem[]>([])

    // 登录
    async function loginAction(data: LoginData): Promise<void> {
      const res = await login(data)
      token.value = res.data.token
      setToken(res.data.token)
    }

    // 获取用户信息
    async function getInfoAction(): Promise<void> {
      const res = await getInfo()
      const info = res.data
      userId.value = info.user.userId
      username.value = info.user.username
      nickname.value = info.user.nickname
      avatar.value = info.user.avatar
      phone.value = info.user.phone
      status.value = info.user.status
      roles.value = info.roles
      permissions.value = info.permissions
    }

    // 获取动态路由
    async function getRoutersAction(): Promise<RouterItem[]> {
      const res = await getRouters()
      routers.value = res.data || []
      return routers.value
    }

    // 退出登录
    async function logoutAction(): Promise<void> {
      try {
        await logout()
      } catch (e) {
        // 忽略退出接口错误
      }
      resetState()
    }

    // 重置状态
    function resetState(): void {
      token.value = ''
      userId.value = 0
      username.value = ''
      nickname.value = ''
      avatar.value = ''
      phone.value = ''
      status.value = ''
      roles.value = []
      permissions.value = []
      routers.value = []
      removeToken()
    }

    return {
      token,
      userId,
      username,
      nickname,
      avatar,
      phone,
      status,
      roles,
      permissions,
      routers,
      loginAction,
      getInfoAction,
      getRoutersAction,
      logoutAction,
      resetState
    }
  },
  {
    // 持久化 token 与基本信息
    persist: {
      key: 'admin-user',
      pick: ['token', 'userId', 'username', 'nickname', 'avatar', 'phone', 'status', 'roles', 'permissions', 'routers']
    }
  }
)
