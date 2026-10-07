<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="queryParams.deptId"
            :data="deptTreeOptions"
            :props="deptTreeProps"
            check-strictly
            placeholder="请选择部门"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="在职" value="1" />
            <el-option label="离职" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            style="width: 260px"
          />
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
        <el-button type="warning" :icon="Download" :loading="exporting" @click="handleExport">导出 Excel</el-button>
      </div>
      <el-table v-loading="loading" :data="employeeList" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="#" width="55" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column label="性别" width="70" align="center">
          <template #default="{ row }">{{ row.gender === '1' || row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="deptName" label="部门" min-width="120" />
        <el-table-column prop="position" label="职位" min-width="120" />
        <el-table-column prop="salary" label="薪资" min-width="110" align="right">
          <template #default="{ row }">¥{{ Number(row.salary).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="hireDate" label="入职日期" min-width="120" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === '1' || row.status === 1"
              @change="(val) => handleStatusChange(row as Employee, val as boolean)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row as Employee)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row as Employee)">删除</el-button>
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
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio label="1">男</el-radio>
                <el-radio label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptTreeData"
                :props="deptTreeProps"
                check-strictly
                placeholder="请选择部门"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input-number v-model="form.salary" :min="0" :precision="2" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker v-model="form.hireDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择入职日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">在职</el-radio>
            <el-radio label="0">离职</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue'
import {
  pageEmployee,
  addEmployee,
  updateEmployee,
  deleteEmployee,
  changeEmployeeStatus,
  exportEmployee
} from '@/api/employee'
import { deptTree } from '@/api/dept'
import type { Employee, SysDept } from '@/types'
import { downloadBlob, formatDateOnly } from '@/utils'

const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  deptId: undefined as number | undefined,
  status: '',
  startDate: '',
  endDate: ''
})

const dateRange = ref<[string, string] | null>(null)
const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const exporting = ref<boolean>(false)
const employeeList = ref<Employee[]>([])
const total = ref<number>(0)
const selectionIds = ref<number[]>([])
const deptTreeData = ref<SysDept[]>([])

const dialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('')
const isEdit = ref<boolean>(false)
const formRef = ref<FormInstance>()

const form = reactive<any>({
  empId: undefined,
  name: '',
  gender: '1',
  phone: '',
  email: '',
  deptId: undefined,
  position: '',
  salary: 0,
  hireDate: '',
  status: '1'
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
  salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 部门下拉树选项（含全部）
const deptTreeOptions = ref<SysDept[]>([])

// 树选择器属性配置（含 value 字段，需 as any 绕过 TreeOptionProps 类型限制）
const deptTreeProps = { label: 'deptName', children: 'children', value: 'deptId' } as any

function handleDateChange(val: [string, string] | null): void {
  if (val) {
    queryParams.startDate = val[0]
    queryParams.endDate = val[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
}

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const res: any = await pageEmployee(queryParams)
    employeeList.value = res.data.rows || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadDeptTree(): Promise<void> {
  try {
    const res: any = await deptTree()
    deptTreeData.value = res.data || []
    deptTreeOptions.value = res.data || []
  } catch (e) {
    // 忽略
  }
}

function handleSearch(): void {
  queryParams.page = 1
  loadList()
}

function handleReset(): void {
  queryParams.name = ''
  queryParams.deptId = undefined
  queryParams.status = ''
  queryParams.startDate = ''
  queryParams.endDate = ''
  dateRange.value = null
  handleSearch()
}

function handleSizeChange(): void {
  queryParams.page = 1
  loadList()
}

function handleCurrentChange(): void {
  loadList()
}

function handleSelectionChange(selection: Employee[]): void {
  selectionIds.value = selection.map((i) => i.empId)
}

function handleAdd(): void {
  isEdit.value = false
  dialogTitle.value = '新增员工'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Employee): void {
  isEdit.value = true
  dialogTitle.value = '编辑员工'
  Object.assign(form, {
    empId: row.empId,
    name: row.name,
    gender: String(row.gender),
    phone: row.phone,
    email: row.email || '',
    deptId: row.deptId,
    position: row.position,
    salary: Number(row.salary),
    hireDate: formatDateOnly(row.hireDate),
    status: String(row.status)
  })
  dialogVisible.value = true
}

function resetForm(): void {
  Object.assign(form, {
    empId: undefined,
    name: '',
    gender: '1',
    phone: '',
    email: '',
    deptId: undefined,
    position: '',
    salary: 0,
    hireDate: '',
    status: '1'
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
        await updateEmployee(form)
        ElMessage.success('修改成功')
      } else {
        await addEmployee(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(row: Employee): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除员工 ${row.name} 吗？`, '提示', { type: 'warning' })
    await deleteEmployee(String(row.empId))
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消
  }
}

async function handleBatchDelete(): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectionIds.value.length} 条数据吗？`, '提示', { type: 'warning' })
    await deleteEmployee(selectionIds.value.join(','))
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消
  }
}

async function handleStatusChange(row: Employee, val: boolean): Promise<void> {
  const status = val ? '1' : '0'
  try {
    await changeEmployeeStatus(row.empId, status)
    row.status = status
    ElMessage.success('状态修改成功')
  } catch (e) {
    // 失败不修改
  }
}

// 导出 Excel
async function handleExport(): Promise<void> {
  exporting.value = true
  try {
    const blob = await exportEmployee({
      page: 1,
      size: 10000,
      name: queryParams.name,
      deptId: queryParams.deptId,
      status: queryParams.status,
      startDate: queryParams.startDate,
      endDate: queryParams.endDate
    }) as unknown as Blob
    // 若后端返回的是 JSON 错误（而非 xlsx），则 blob.type 为 application/json
    if (blob.type && blob.type.includes('application/json')) {
      const text = await blob.text()
      try {
        const err = JSON.parse(text)
        ElMessage.error(err.msg || '导出失败')
      } catch {
        ElMessage.error('导出失败')
      }
      return
    }
    const filename = `员工列表_${formatDateOnly(new Date())}.xlsx`
    downloadBlob(blob, filename)
    ElMessage.success('导出成功')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadDeptTree()
  loadList()
})
</script>
