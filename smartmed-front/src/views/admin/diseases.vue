<script setup>
import { ref, reactive, onMounted } from 'vue'
import AdminNavbar from '../../components/common/AdminNavbar.vue'
import { getDiseaseList, saveDisease, deleteDisease } from '../../api/disease'
import { ElMessage, ElMessageBox } from 'element-plus'

const diseases = ref([])
const searchType = ref('name')
const searchValue = ref('')
const addDialog = reactive({ visible: false })
const addForm = reactive({ name: '', category: '', causes: '', symptoms: '' })
const editDialog = reactive({ visible: false })
const editForm = reactive({ diseaseId: '', name: '', category: '', causes: '', symptoms: '' })

const fetchList = async () => {
  const params = {}
  if (searchValue.value) {
    if (searchType.value === 'name') params.illnessName = searchValue.value
    if (searchType.value === 'kind') params.kind = searchValue.value
    if (searchType.value === 'symptom') params.illnessSymptom = searchValue.value
  }
  diseases.value = await getDiseaseList(params)
}
onMounted(fetchList)

const openAdd = () => {
  addForm.name = ''
  addForm.category = ''
  addForm.causes = ''
  addForm.symptoms = ''
  addDialog.visible = true
}
const openEdit = (row) => {
  editForm.diseaseId = row.diseaseId
  editForm.name = row.name
  editForm.category = row.category
  editForm.causes = row.causes
  editForm.symptoms = row.symptoms
  editDialog.visible = true
}
const submitAdd = async () => {
  if (!addForm.name || !addForm.category || !addForm.causes || !addForm.symptoms) {
    ElMessage.error('请填写完整信息')
    return
  }
  const data = {
    illnessName: addForm.name.trim(),
    kindId: addForm.category.trim(),
    includeReason: addForm.causes.trim(),
    illnessSymptom: addForm.symptoms.trim()
  }
  await saveDisease(data)
  ElMessage.success('添加成功')
  addDialog.visible = false
  diseases.value = await getDiseaseList({})
}
const submitEdit = async () => {
  if (!editForm.name || !editForm.category || !editForm.causes || !editForm.symptoms) {
    ElMessage.error('请填写完整信息')
    return
  }
  const data = {
    id: editForm.diseaseId,
    illnessName: editForm.name.trim(),
    kindId: editForm.category.trim(),
    includeReason: editForm.causes.trim(),
    illnessSymptom: editForm.symptoms.trim()
  }
  await saveDisease(data)
  ElMessage.success('保存成功')
  editDialog.visible = false
  diseases.value = await getDiseaseList({})
}
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该疾病吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteDisease(id)
      ElMessage.success('删除成功')
      diseases.value = await getDiseaseList({})
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
          <el-option label="分类" value="kind" />
          <el-option label="症状" value="symptom" />
        </el-select>
        <el-input v-model="searchValue" :placeholder="searchType==='name'?'按疾病名称搜索':(searchType==='kind'?'按分类搜索':'按症状搜索')" style="width: 200px; margin-right: 12px;" clearable @clear="fetchList" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button type="success" @click="openAdd">添加疾病</el-button>
      </div>
      <el-table :data="diseases" style="width: 100%; margin-top: 18px;">
        <el-table-column prop="diseaseId" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="100"/>
        <el-table-column prop="category" label="分类" width="120"/>
        <el-table-column prop="causes" label="病因" width="240"/>
        <el-table-column prop="symptoms" label="症状" width="240"/>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.diseaseId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="addDialog.visible" title="添加疾病">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="addForm.category" />
        </el-form-item>
        <el-form-item label="病因">
          <el-input v-model="addForm.causes" />
        </el-form-item>
        <el-form-item label="症状">
          <el-input v-model="addForm.symptoms" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">添加</el-button>
      </template>

    </el-dialog>
    <el-dialog v-model="editDialog.visible" title="编辑疾病">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="editForm.category" />
        </el-form-item>
        <el-form-item label="病因">
          <el-input v-model="editForm.causes" />
        </el-form-item>
        <el-form-item label="症状">
          <el-input v-model="editForm.symptoms" />
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
.admin-page { max-width: 1100px; margin: 32px auto; }
.toolbar { margin-bottom: 12px; display: flex; align-items: center; }
</style>