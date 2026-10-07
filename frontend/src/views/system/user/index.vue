<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
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
        <el-button type="danger" :icon="Delete" :disabled="selectionIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
      </div>
      <el-table v-loading="loading" :data="userList" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="#" width="55" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === '1' || row.status === 1"
              @change="(val) => handleStatusChange(row as SysUser, val as boolean)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row as SysUser)">编辑</el-button>
            <el-button type="warning" link :icon="Key" @click="handleResetPwd(row as SysUser)">重置密码</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row as SysUser)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item v-if="!isEdit" label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="r in roleOptions" :key="r.roleId" :label="r.roleName" :value="r.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPwdVisible" :title="resetPwdTitle" width="460px" @close="resetPwdFormRef?.resetFields()">
      <el-form ref="resetPwdFormRef" :model="resetPwdForm" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="resetPwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetPwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="resetPwdForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
            @keyup.enter="submitResetPwd"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { pageUser, addUser, updateUser, deleteUser, resetPwdUser, changeUserStatus } from '@/api/user'
import { pageRole } from '@/api/role'
import type { SysUser, SysRole } from '@/types'
import { formatDate } from '@/utils'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  username: '',
  phone: '',
  status: ''
})

const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const userList = ref<SysUser[]>([])
const total = ref<number>(0)
const selectionIds = ref<number[]>([])
const roleOptions = ref<SysRole[]>([])

// 对话框
const dialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('')
const isEdit = ref<boolean>(false)
const formRef = ref<FormInstance>()

const form = reactive<any>({
  userId: undefined,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  status: '1',
  roleIds: []
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少 6 位', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

// 重置密码
const resetPwdVisible = ref<boolean>(false)
const resetPwdTitle = ref<string>('重置密码')
const resetPwdFormRef = ref<FormInstance>()
const resetPwdForm = reactive({ userId: 0, oldPassword: '', newPassword: '', confirmPassword: '' })
const resetPwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
    { validator: (_r, v, cb) => (v && v === resetPwdForm.oldPassword ? cb(new Error('新密码不能与原密码相同')) : cb()), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (_r, v, cb) => (v === resetPwdForm.newPassword ? cb() : cb(new Error('两次输入的密码不一致'))), trigger: 'blur' }
  ]
}

// 加载用户列表
async function loadList(): Promise<void> {
  loading.value = true
  try {
    const res: any = await pageUser(queryParams)
    userList.value = res.data.rows || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

// 加载角色选项
async function loadRoles(): Promise<void> {
  try {
    const res: any = await pageRole({ page: 1, size: 1000 })
    roleOptions.value = res.data.rows || []
  } catch (e) {
    // 忽略
  }
}

function handleSearch(): void {
  queryParams.page = 1
  loadList()
}

function handleReset(): void {
  queryParams.username = ''
  queryParams.phone = ''
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

function handleSelectionChange(selection: SysUser[]): void {
  selectionIds.value = selection.map((i) => i.userId)
}

// 新增
function handleAdd(): void {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

// 编辑
async function handleEdit(row: SysUser): Promise<void> {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, {
    userId: row.userId,
    username: row.username,
    password: '',
    nickname: row.nickname,
    phone: row.phone,
    status: String(row.status),
    roleIds: row.roleIds || []
  })
  dialogVisible.value = true
}

function resetForm(): void {
  Object.assign(form, {
    userId: undefined,
    username: '',
    password: '',
    nickname: '',
    phone: '',
    status: '1',
    roleIds: []
  })
  formRef.value?.resetFields()
}

// 提交表单
async function submitForm(): Promise<void> {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateUser(form)
        ElMessage.success('修改成功')
      } else {
        await addUser(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

// 删除
async function handleDelete(row: SysUser): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除用户 ${row.username} 吗？`, '提示', { type: 'warning' })
    await deleteUser(String(row.userId))
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消
  }
}

// 批量删除
async function handleBatchDelete(): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectionIds.value.length} 条数据吗？`, '提示', { type: 'warning' })
    await deleteUser(selectionIds.value.join(','))
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消
  }
}

// 状态切换
async function handleStatusChange(row: SysUser, val: boolean): Promise<void> {
  const status = val ? '1' : '0'
  try {
    await changeUserStatus(row.userId, status)
    row.status = status
    ElMessage.success('状态修改成功')
  } catch (e) {
    // 失败时不修改
  }
}

// 重置密码
function handleResetPwd(row: SysUser): void {
  resetPwdForm.userId = row.userId
  resetPwdForm.oldPassword = ''
  resetPwdForm.newPassword = ''
  resetPwdForm.confirmPassword = ''
  resetPwdTitle.value = `重置密码 - ${row.username}`
  resetPwdVisible.value = true
}

async function submitResetPwd(): Promise<void> {
  if (!resetPwdFormRef.value) return
  await resetPwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await resetPwdUser({
        userId: resetPwdForm.userId,
        oldPassword: resetPwdForm.oldPassword,
        newPassword: resetPwdForm.newPassword
      })
      ElMessage.success('密码重置成功')
      resetPwdVisible.value = false
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadRoles()
  loadList()
})
</script>
