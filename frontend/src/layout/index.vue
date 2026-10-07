<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="layout-aside">
      <Sidebar :is-collapse="isCollapse" />
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <Navbar @toggle-sidebar="isCollapse = !isCollapse" :is-collapse="isCollapse" />
      </el-header>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

// 侧边栏折叠状态
const isCollapse = ref<boolean>(false)
</script>

<style scoped>
.layout-container {
  height: 100%;
}
.layout-aside {
  background-color: #304156;
  transition: width 0.28s;
  overflow: hidden;
}
.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
  height: 50px;
  line-height: 50px;
}
.layout-main {
  background-color: #f0f2f5;
  padding: 16px;
  overflow: auto;
}
</style>
