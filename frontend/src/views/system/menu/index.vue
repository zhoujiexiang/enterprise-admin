<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.query" placeholder="请输入菜单名称" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadList">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 树形表格 -->
    <div class="table-container">
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd()">新增</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="menuId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="true"
        border
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column label="图标" width="70" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="menuTypeTag(row.menuType)">{{ menuTypeText(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" min-width="120" />
        <el-table-column prop="component" label="组件路径" min-width="160" />
        <el-table-column prop="perms" label="权限标识" min-width="160" />
        <el-table-column label="可见" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visible === '1' || row.visible === 1 ? 'success' : 'info'">
              {{ row.visible === '1' || row.visible === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Plus" @click="handleAdd(row as SysMenu)">新增</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row as SysMenu)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row as SysMenu)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeOptions"
            :props="{ label: 'menuName', children: 'children', value: 'menuId' } as any"
            check-strictly
            placeholder="选择上级菜单（顶级选 0）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 'C'" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="如 system/user/index" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'M'" label="权限标识">
          <el-input v-model="form.perms" placeholder="如 system:user:list" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="图标">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名，如 User" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="999" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="是否可见">
          <el-radio-group v-model="form.visible">
            <el-radio label="1">显示</el-radio>
            <el-radio label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { menuTree, addMenu, updateMenu, deleteMenu } from '@/api/menu'
import type { SysMenu } from '@/types'

const queryParams = reactive({ query: '' })
const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const menuList = ref<SysMenu[]>([])

const dialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('')
const isEdit = ref<boolean>(false)
const formRef = ref<FormInstance>()

const form = reactive<any>({
  menuId: undefined,
  parentId: 0,
  menuName: '',
  path: '',
  component: '',
  perms: '',
  menuType: 'C',
  icon: '',
  orderNum: 0,
  visible: '1'
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  orderNum: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

// 树选择器选项：加入顶级选项
const menuTreeOptions = computed<SysMenu[]>(() => {
  const top: SysMenu = {
    menuId: 0,
    parentId: -1,
    menuName: '顶级菜单',
    menuType: 'M',
    orderNum: 0,
    status: '1',
    children: menuList.value
  }
  return [top]
})

function menuTypeText(type: string): string {
  const map: Record<string, string> = { M: '目录', C: '菜单', F: '按钮' }
  return map[type] || type
}

function menuTypeTag(type: string): 'primary' | 'success' | 'warning' {
  const map: Record<string, 'primary' | 'success' | 'warning'> = { M: 'primary', C: 'success', F: 'warning' }
  return map[type] || 'primary'
}

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const res: any = await menuTree(queryParams.query)
    menuList.value = res.data || []
  } finally {
    loading.value = false
  }
}

function handleReset(): void {
  queryParams.query = ''
  loadList()
}

function handleAdd(row?: SysMenu): void {
  isEdit.value = false
  dialogTitle.value = '新增菜单'
  resetForm()
  if (row && row.menuId) {
    form.parentId = row.menuId
  }
  dialogVisible.value = true
}

function handleEdit(row: SysMenu): void {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'
  Object.assign(form, {
    menuId: row.menuId,
    parentId: row.parentId,
    menuName: row.menuName,
    path: row.path || '',
    component: row.component || '',
    perms: row.perms || '',
    menuType: row.menuType,
    icon: row.icon || '',
    orderNum: row.orderNum,
    visible: String(row.visible ?? '1')
  })
  dialogVisible.value = true
}

function resetForm(): void {
  Object.assign(form, {
    menuId: undefined,
    parentId: 0,
    menuName: '',
    path: '',
    component: '',
    perms: '',
    menuType: 'C',
    icon: '',
    orderNum: 0,
    visible: '1'
  })
  formRef.value?.resetFields()
}

async function submitForm(): Promise<void> {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateMenu(form)
        ElMessage.success('修改成功')
      } else {
        await addMenu(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(row: SysMenu): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除菜单 ${row.menuName} 吗？`, '提示', { type: 'warning' })
    await deleteMenu(row.menuId)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消
  }
}

onMounted(() => {
  loadList()
})
</script>
