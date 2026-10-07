import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'
import Layout from '@/layout/index.vue'

// 使用 import.meta.glob 预扫描所有 views 组件，供动态路由匹配
const modules = import.meta.glob('@/views/**/*.vue')

// 根据后端返回的 component 字符串映射到组件
function loadComponent(component: string): any {
  // 后端返回如 "system/user/index"，前端拼接为 /src/views/system/user/index.vue
  const path = `/src/views/${component}.vue`
  if (modules[path]) {
    return modules[path]
  }
  // 兜底：找不到组件时返回 404
  console.warn(`未找到组件: ${component}，路径: ${path}`)
  return () => import('@/views/404.vue')
}

// 静态路由
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true }
  },
  {
    path: '/dashboard/screen',
    name: 'DashboardScreen',
    component: () => import('@/views/dashboard/screen.vue'),
    meta: { hidden: true, title: '数据可视化大屏' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
    meta: { hidden: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'DashboardRedirect',
        component: () => import('@/views/dashboard/screen.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  }
]

// 动态路由占位：根路由，children 运行时添加
export const asyncRoute: RouteRecordRaw = {
  path: '/',
  component: Layout,
  name: 'Root',
  children: []
}

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 将后端返回的菜单数据转换为路由
// 后端 RouterVO 只有 name/path/component/hidden/children，无 meta
// path 为相对路径（父级如 "system"，子级如 "user"），需拼接父级前缀
function buildRoutes(routers: any[], basePath = ''): RouteRecordRaw[] {
  const result: RouteRecordRaw[] = []
  routers.forEach((item) => {
    // 拼接完整路径：父级前缀 + 当前 path
    const fullPath = resolveRouterPath(basePath, item.path)
    const hasChildren = item.children && item.children.length > 0
    const route: any = {
      path: fullPath,
      name: item.name,
      component: hasChildren ? Layout : (item.component ? loadComponent(item.component) : Layout),
      meta: {
        title: item.name,          // 后端无 meta，直接用 name 作标题
        icon: item.meta?.icon,     // 后端未提供 icon，保留扩展位
        noCache: item.meta?.noCache,
        hidden: item.hidden
      },
      children: []
    }
    if (hasChildren) {
      route.children = buildRoutes(item.children, fullPath)
    }
    result.push(route)
  })
  return result
}

// 拼接父子路由路径：父级 basePath 为空时返回 /path，否则返回 /parent/child
function resolveRouterPath(basePath: string, path: string): string {
  if (!path) return basePath || '/'
  if (path.startsWith('/')) return path
  const p = basePath ? `${basePath}/${path}` : `/${path}`
  // 避免 // 重复
  return p.replace(/\/+/g, '/')
}

// 是否已加载过动态路由（避免重复添加）
let isRoutesLoaded = false

// 重置动态路由（用于登出）
export function resetRouter(): void {
  isRoutesLoaded = false
  // 移除所有动态添加的路由
  const allRoutes = router.getRoutes()
  allRoutes.forEach((r) => {
    if (r.name && !['Login', 'NotFound', 'DashboardScreen', 'DashboardRedirect'].includes(r.name as string)) {
      router.removeRoute(r.name)
    }
  })
}

// 加载动态路由
export async function loadAsyncRoutes(): Promise<void> {
  const userStore = useUserStore()
  if (isRoutesLoaded) return
  if (!userStore.routers || userStore.routers.length === 0) {
    await userStore.getRoutersAction()
  }
  const dynamicRoutes = buildRoutes(userStore.routers)
  dynamicRoutes.forEach((route) => {
    router.addRoute(route)
  })
  isRoutesLoaded = true
}

// 全局路由守卫
const whiteList = ['/login', '/404', '/dashboard/screen']

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      return
    }
    // 已登录但未加载路由：先获取用户信息与动态路由
    if (!isRoutesLoaded) {
      try {
        if (!userStore.roles || userStore.roles.length === 0) {
          await userStore.getInfoAction()
        }
        await loadAsyncRoutes()
        // 重新跳转以确保匹配新路由
        next({ ...to, replace: true })
        return
      } catch (e) {
        userStore.resetState()
        next(`/login?redirect=${to.path}`)
        return
      }
    }
    next()
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
