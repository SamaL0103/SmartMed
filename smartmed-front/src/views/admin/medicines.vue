<script setup>
import { ref, reactive, onMounted } from 'vue'
import AdminNavbar from '../../components/common/AdminNavbar.vue'
import { getMedicineList, saveMedicine, deleteMedicine } from '../../api/medicine'
import { ElMessage, ElMessageBox } from 'element-plus'

const medicines = ref([])
const searchType = ref('name')
const searchValue = ref('')
const addDialog = reactive({ visible: false })
const addForm = reactive({ name: '', category: '', efficacy: '', usageMethod: '', contraindications: '', sideEffects: '' })
const editDialog = reactive({ visible: false })
const editForm = reactive({ medicineId: '', name: '', category: '', efficacy: '', usageMethod: '', contraindications: '', sideEffects: '' })

const fetchList = async () => {
  const params = {}
  if (searchValue.value) {
    if (searchType.value === 'name') params.name = searchValue.value
    if (searchType.value === 'efficacy') params.efficacy = searchValue.value
  }
  medicines.value = await getMedicineList(params)
}
onMounted(fetchList)

const openAdd = () => {
  addForm.name = ''
  addForm.category = ''
  addForm.efficacy = ''
  addForm.usageMethod = ''
  addForm.contraindications = ''
  addForm.sideEffects = ''
  addDialog.visible = true
}
const openEdit = (row) => {
  editForm.medicineId = row.medicineId
  editForm.name = row.name
  editForm.category = row.category
  editForm.efficacy = row.efficacy
  editForm.usageMethod = row.usageMethod
  editForm.contraindications = row.contraindications
  editForm.sideEffects = row.sideEffects
  editDialog.visible = true
}
const submitAdd = async () => {
  if (!addForm.name || !addForm.category || !addForm.efficacy || !addForm.usageMethod) {
    ElMessage.error('请填写完整信息')
    return
  }
  const data = {
    name: addForm.name.trim(),
    category: addForm.category.trim(),
    efficacy: addForm.efficacy.trim(),
    usageMethod: addForm.usageMethod.trim(),
    contraindications: addForm.contraindications?.trim() || '',
    sideEffects: addForm.sideEffects?.trim() || ''
  }
  await saveMedicine(data)
  ElMessage.success('添加成功')
  addDialog.visible = false
  medicines.value = await getMedicineList({})
}
const submitEdit = async () => {
  if (!editForm.name || !editForm.category || !editForm.efficacy || !editForm.usageMethod) {
    ElMessage.error('请填写完整信息')
    return
  }
  const data = {
    id: editForm.medicineId,
    name: editForm.name.trim(),
    category: editForm.category.trim(),
    efficacy: editForm.efficacy.trim(),
    usageMethod: editForm.usageMethod.trim(),
    contraindications: editForm.contraindications?.trim() || '',
    sideEffects: editForm.sideEffects?.trim() || ''
  }
  await saveMedicine(data)
  ElMessage.success('保存成功')
  editDialog.visible = false
  medicines.value = await getMedicineList({})
}
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该药品吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteMedicine(id)
      ElMessage.success('删除成功')
      medicines.value = await getMedicineList({})
    })
}
</script>

<template>
  <AdminNavbar />
  <div class="admin-page">
    <el-card>
      <div class="toolbar">
        <el-select v-model="searchType" style="width: 120px; margin-right: 12px;">
          <el-option label="名称" value="name" />
          <el-option label="功效" value="efficacy" />
        </el-select>
        <el-input v-model="searchValue" :placeholder="searchType==='name'?'按药品名称搜索':'按功效搜索'" style="width: 200px; margin-right: 12px;" clearable @clear="fetchList" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button type="success" @click="openAdd">添加药品</el-button>
      </div>
      <el-table :data="medicines" style="width: 100%; margin-top: 18px;">
        <el-table-column prop="medicineId" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="100"/>
        <el-table-column prop="category" label="分类" width="100"/>
        <el-table-column prop="efficacy" label="功效" width="140"/>
        <el-table-column prop="usageMethod" label="用法" width="140"/>
        <el-table-column prop="contraindications" label="禁忌" width="140"/>
        <el-table-column prop="sideEffects" label="副作用"width="140"/>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.medicineId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="addDialog.visible" title="添加药品">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="addForm.category" />
        </el-form-item>
        <el-form-item label="功效">
          <el-input v-model="addForm.efficacy" />
        </el-form-item>
        <el-form-item label="用法">
          <el-input v-model="addForm.usageMethod" />
        </el-form-item>
        <el-form-item label="禁忌">
          <el-input v-model="addForm.contraindications" />
        </el-form-item>
        <el-form-item label="副作用">
          <el-input v-model="addForm.sideEffects" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">添加</el-button>
      </template>

    </el-dialog>
    <el-dialog v-model="editDialog.visible" title="编辑药品">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="editForm.category" />
        </el-form-item>
        <el-form-item label="功效">
          <el-input v-model="editForm.efficacy" />
        </el-form-item>
        <el-form-item label="用法">
          <el-input v-model="editForm.usageMethod" />
        </el-form-item>
        <el-form-item label="禁忌">
          <el-input v-model="editForm.contraindications" />
        </el-form-item>
        <el-form-item label="副作用">
          <el-input v-model="editForm.sideEffects" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">添加</el-button>
      </template>

    </el-dialog>
  </div>
</template>

<style scoped>
.admin-page { max-width: 1200px; margin: 32px auto; }
.toolbar { margin-bottom: 12px; display: flex; align-items: center; }
</style>