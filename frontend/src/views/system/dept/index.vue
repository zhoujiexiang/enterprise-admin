<template>
  <div class="app-container">
    <!-- 操作栏 -->
    <div class="table-container">
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd()">新增</el-button>
        <el-button :icon="Refresh" @click="loadList">刷新</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="deptList"
        row-key="deptId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="true"
        border
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="orderNum" label="排序" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' || row.status === 1 ? 'success' : 'danger'">
              {{ row.status === '1' || row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Plus" @click="handleAdd(row as SysDept)">新增</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row as SysDept)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row as SysDept)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="deptTreeOptions"
            :props="{ label: 'deptName', children: 'children', value: 'deptId' } as any"
            check-strictly
            placeholder="选择上级部门（顶级选 0）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="999" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { deptTree, addDept, updateDept, deleteDept } from '@/api/dept'
import type { SysDept } from '@/types'

const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const deptList = ref<SysDept[]>([])

const dialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('')
const isEdit = ref<boolean>(false)
const formRef = ref<FormInstance>()

const form = reactive<any>({
  deptId: undefined,
  parentId: 0,
  deptName: '',
  orderNum: 0,
  status: '1'
})

const rules: FormRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  orderNum: [{ required: true, message: '请输入排序', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const deptTreeOptions = computed<SysDept[]>(() => {
  const top: SysDept = {
    deptId: 0,
    parentId: -1,
    deptName: '顶级部门',
    orderNum: 0,
    status: '1',
    children: deptList.value
  }
  return [top]
})

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const res: any = await deptTree()
    deptList.value = res.data || []
  } finally {
    loading.value = false
  }
}

function handleAdd(row?: SysDept): void {
  isEdit.value = false
  dialogTitle.value = '新增部门'
  resetForm()
  if (row && row.deptId) {
    form.parentId = row.deptId
  }
  dialogVisible.value = true
}

function handleEdit(row: SysDept): void {
  isEdit.value = true
  dialogTitle.value = '编辑部门'
  Object.assign(form, {
    deptId: row.deptId,
    parentId: row.parentId,
    deptName: row.deptName,
    orderNum: row.orderNum,
    status: String(row.status)
  })
  dialogVisible.value = true
}

function resetForm(): void {
  Object.assign(form, {
    deptId: undefined,
    parentId: 0,
    deptName: '',
    orderNum: 0,
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
        await updateDept(form)
        ElMessage.success('修改成功')
      } else {
        await addDept(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(row: SysDept): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除部门 ${row.deptName} 吗？`, '提示', { type: 'warning' })
    await deleteDept(row.deptId)
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
