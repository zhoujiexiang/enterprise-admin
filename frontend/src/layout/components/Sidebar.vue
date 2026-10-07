<template>
  <div class="sidebar-wrapper">
    <div class="logo-container">
      <img src="/vite.svg" alt="logo" class="logo-img" v-if="!isCollapse" />
      <span v-if="!isCollapse" class="logo-text">企业管理后台</span>
      <span v-else class="logo-text-mini">EA</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :unique-opened="true"
      :collapse-transition="false"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <SidebarItem
        v-for="route in menuRoutes"
        :key="route.path"
        :item="route"
        :base-path="'/' + route.path"
      />
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarItem from './SidebarItem.vue'
import { useUserStore } from '@/store/user'

defineProps<{
  isCollapse: boolean
}>()

const route = useRoute()
const userStore = useUserStore()

// 当前激活菜单
const activeMenu = computed<string>(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})

// 菜单数据：使用 store 中的动态路由
const menuRoutes = computed(() => {
  return userStore.routers || []
})
</script>

<style scoped>
.sidebar-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: #fff;
  overflow: hidden;
}
.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}
.logo-text {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}
.logo-text-mini {
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
}
:deep(.el-menu) {
  border-right: none;
}
</style>
