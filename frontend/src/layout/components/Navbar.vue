<template>
  <div class="navbar-container">
    <div class="navbar-left">
      <el-icon class="hamburger" @click="toggleSidebar">
        <component :is="isCollapse ? 'Expand' : 'Fold'" />
      </el-icon>
      <!-- 面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item
          v-for="(item, index) in breadcrumbs"
          :key="index"
          :to="item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="30" :src="avatar || undefined" class="user-avatar">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <span class="username">{{ nickname || username || '用户' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="screen">数据大屏</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { resetRouter } from '@/router'

defineProps<{
  isCollapse: boolean
}>()

const emit = defineEmits<{
  (e: 'toggleSidebar'): void
}>()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const nickname = computed(() => userStore.nickname)
const username = computed(() => userStore.username)
const avatar = computed(() => userStore.avatar)

// 切换侧边栏
function toggleSidebar(): void {
  emit('toggleSidebar')
}

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta && item.meta.title)
  const result: { path: string; title: string }[] = []
  matched.forEach((item) => {
    result.push({
      path: item.path,
      title: item.meta.title as string
    })
  })
  return result
})

// 下拉菜单命令处理
function handleCommand(command: string): void {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中')
  } else if (command === 'screen') {
    const routeData = router.resolve('/dashboard/screen')
    window.open(routeData.href, '_blank')
  }
}

// 退出登录
async function handleLogout(): Promise<void> {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logoutAction()
    resetRouter()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (e) {
    // 用户取消
  }
}
</script>

<style scoped>
.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
  padding: 0 20px;
}
.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.hamburger {
  font-size: 20px;
  cursor: pointer;
}
.breadcrumb {
  line-height: 50px;
}
.navbar-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}
.user-avatar {
  background-color: #409eff;
}
.username {
  font-size: 14px;
  color: #303133;
}
</style>
