<template>
  <template v-if="!item.hidden">
    <!-- 单个可见子项：直接渲染为 el-menu-item -->
    <template
      v-if="hasOneShowingChild(item.children, item) && (!onlyChild?.children || onlyChild?.noShowingChildren) && !item.alwaysShow"
    >
      <el-menu-item v-if="onlyChild" :index="resolvePath(onlyChild.path)">
        <el-icon v-if="onlyChild.meta?.icon || item.meta?.icon">
          <component :is="onlyChild.meta?.icon || item.meta?.icon" />
        </el-icon>
        <template #title>
          <span>{{ onlyChild.meta?.title || onlyChild.name || item.meta?.title || item.name }}</span>
        </template>
      </el-menu-item>
    </template>
    <!-- 多子项：渲染为 el-sub-menu 并递归 -->
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title || item.name }}</span>
      </template>
      <SidebarItem
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </template>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  item: any
  basePath: string
}>()

// 唯一可见子项
const onlyChild = ref<any>(null)

// 判断是否只有一个可见子项
function hasOneShowingChild(children: any[] = [], parent: any): boolean {
  if (!children) {
    children = []
  }
  const showingChildren = children.filter((item) => {
    if (item.hidden) {
      return false
    }
    onlyChild.value = item
    return true
  })
  if (showingChildren.length === 1) {
    return true
  }
  if (showingChildren.length === 0) {
    onlyChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }
  return false
}

// 拼接路径
function resolvePath(routePath: string): string {
  if (/^(https?:|mailto:|tel:)/.test(routePath)) {
    return routePath
  }
  if (/^(https?:|mailto:|tel:)/.test(props.basePath)) {
    return props.basePath
  }
  if (routePath.startsWith('/')) {
    return routePath
  }
  const base = props.basePath.endsWith('/') ? props.basePath.slice(0, -1) : props.basePath
  return `${base}/${routePath}`
}
</script>
