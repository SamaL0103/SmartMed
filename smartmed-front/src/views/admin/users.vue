<script setup>
import { ref, reactive, onMounted } from 'vue'
import AdminNavbar from '../../components/common/AdminNavbar.vue'
import { getUserList, addUser, updateUser, deleteUser } from '../../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const editDialog = reactive({ visible: false, form: {} })

const fetchList = async () => {
  users.value = await getUserList()
}
onMounted(fetchList)

const openEdit = (row) => {
  if (row) {
    editDialog.form = { ...row }
  } else {
    editDialog.form = { username: '', phone: '', age: '', gender: 'Male', isAdmin: false, password: '' }
  }
  editDialog.visible = true
}
const submitEdit = async () => {
  // 校验必填项
  if (!editDialog.form.username || !editDialog.form.phone || !editDialog.form.age || !editDialog.form.gender) {
    ElMessage.error('请填写完整信息')
    return
  }
  if (!editDialog.form.userId) {
    // 新增
    const data = { ...editDialog.form }
    await addUser(data)
  } else {
    // 编辑
    const data = { ...editDialog.form }
    await updateUser(data)
  }
  ElMessage.success('保存成功')
  editDialog.visible = false
  fetchList()
}
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteUser(id)
      ElMessage.success('删除成功')
      fetchList()
    })
}
</script>

<template>
  <AdminNavbar />
  <div class="admin-page">
    <el-card>
      <div class="toolbar">
        <el-button type="success" @click="openEdit()">添加用户</el-button>
      </div>
      <el-table :data="users" style="width: 100%; margin-top: 18px;">
        <el-table-column prop="userId" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="电话" width="110"/>
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="110" />
        <el-table-column prop="isAdmin" label="管理员" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isAdmin ? 'success' : 'info'">{{ row.isAdmin ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.userId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="editDialog.visible" :title="editDialog.form.userId ? '编辑用户' : '添加用户'">
      <el-form :model="editDialog.form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editDialog.form.username" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editDialog.form.phone" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="editDialog.form.age" type="number" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editDialog.form.gender">
            <el-option label="男" value="Male" />
            <el-option label="女" value="Female" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员">
          <el-switch v-model="editDialog.form.isAdmin" active-text="是" inactive-text="否" />
        </el-form-item>
        <el-form-item v-if="!editDialog.form.userId" label="密码">
          <el-input v-model="editDialog.form.password" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-page { max-width: 1100px; margin: 32px auto; }
.toolbar { margin-bottom: 12px; display: flex; align-items: center; }
</style>