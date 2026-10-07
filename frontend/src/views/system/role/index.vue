<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="queryParams.roleKey" placeholder="请输入权限字符" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
      </div>
      <el-table v-loading="loading" :data="roleList" border>
        <el-table-column type="index" label="#" width="55" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="140" />
        <el-table-column prop="roleKey" label="权限字符" min-width="140" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' || row.status === 1 ? 'success' : 'danger'">
              {{ row.status === '1' || row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row as SysRole)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row as SysRole)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <div class="tree-wrapper">
            <el-tree
              ref="menuTreeRef"
              :data="menuTreeData"
              :props="{ label: 'menuName', children: 'children' }"
              node-key="menuId"
              show-checkbox
              :default-checked-keys="form.menuIds"
              check-strictly
            />
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { pageRole, addRole, updateRole, deleteRole, getRoleMenuIds } from '@/api/role'
import { menuTree } from '@/api/menu'
import type { SysRole, SysMenu } from '@/types'
import { formatDate } from '@/utils'

const queryParams = reactive({
  page: 1,
  size: 10,
  roleName: '',
  roleKey: '',
  status: ''
})

const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const roleList = ref<SysRole[]>([])
const total = ref<number>(0)
const menuTreeData = ref<SysMenu[]>([])

const dialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('')
const isEdit = ref<boolean>(false)
const formRef = ref<FormInstance>()
const menuTreeRef = ref<any>()

const form = reactive<any>({
  roleId: undefined,
  roleName: '',
  roleKey: '',
  status: '1',
  remark: '',
  menuIds: []
})

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限字符', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const res: any = await pageRole(queryParams)
    roleList.value = res.data.rows || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadMenuTree(): Promise<void> {
  try {
    const res: any = await menuTree()
    menuTreeData.value = res.data || []
  } catch (e) {
    // 忽略
  }
}

function handleSearch(): void {
  queryParams.page = 1
  loadList()
}

function handleReset(): void {
  queryParams.roleName = ''
  queryParams.roleKey = ''
  queryParams.status = ''
  handleSearch()
}

function handleSizeChange(): void {
  queryParams.page = 1
  loadList()
}

function handleCurrentChange(): void {
  loadList()
}

function handleAdd(): void {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(row: SysRole): Promise<void> {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(form, {
    roleId: row.roleId,
    roleName: row.roleName,
    roleKey: row.roleKey,
    status: String(row.status),
    remark: row.remark || '',
    menuIds: []
  })
  dialogVisible.value = true
  // 加载已勾选的菜单 ID
  try {
    const res: any = await getRoleMenuIds(row.roleId)
    const ids: number[] = res.data || []
    form.menuIds = ids
    // 设置树选中
    nextTickSetTree(ids)
  } catch (e) {
    // 忽略
  }
}

function nextTickSetTree(ids: number[]): void {
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys(ids)
  }, 100)
}

function resetForm(): void {
  Object.assign(form, {
    roleId: undefined,
    roleName: '',
    roleKey: '',
    status: '1',
    remark: '',
    menuIds: []
  })
  formRef.value?.resetFields()
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys([])
  }, 100)
}

async function submitForm(): Promise<void> {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
      form.menuIds = checkedKeys
      if (isEdit.value) {
        await updateRole(form)
        ElMessage.success('修改成功')
      } else {
        await addRole(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(row: SysRole): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除角色 ${row.roleName} 吗？`, '提示', { type: 'warning' })
    await deleteRole(String(row.roleId))
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消
  }
}

onMounted(() => {
  loadMenuTree()
  loadList()
})
</script>

<style scoped>
.tree-wrapper {
  width: 100%;
  max-height: 280px;
  overflow: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 8px;
}
</style>
