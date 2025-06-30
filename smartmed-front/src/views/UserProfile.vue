<script setup>
import { ref, reactive, onMounted, watchEffect } from 'vue'
import { getUserProfile, updateUserProfile, updatePassword, uploadAvatar } from '../api/userProfile'
import { getAllergies, addAllergy, updateAllergy, deleteAllergy } from '../api/allergy'
import { getHistories, addHistory, updateHistory, deleteHistory } from '../api/history'
import { ElMessage, ElMessageBox } from 'element-plus'
import defaultAvatar from '../assets/images/user-avatar.png'

const user = ref(null)
const editMode = ref(false)
const passwordDialog = ref(false)
const passwordForm = reactive({ oldPass: '', newPass: '' })
const avatarUploading = ref(false)

const allergies = ref([])
const histories = ref([])

const allergyForm = reactive({ allergen: '', description: '' })
const allergyEditId = ref(null)

const historyForm = reactive({ disease: '', description: '' })
const historyEditId = ref(null)

onMounted(async () => {
  user.value = await getUserProfile()
  if (user.value) {
    allergies.value = await getAllergies(user.value.userId)
    histories.value = await getHistories(user.value.userId)
  }
})

watchEffect(() => {
  console.log('user:', user.value)
  console.log('user.avatar:', user.value && user.value.avatar)
})

// 头像上传
const handleAvatarChange = async (file) => {
  avatarUploading.value = true
  try {
    const res = await uploadAvatar(file.raw)
    let url = typeof res === 'string' ? res : res.data || res.url || ''
    url = url.trim()
    if (url && !url.startsWith('http')) {
      url = 'http://localhost:8085' + url
    }
    user.value.avatar = url
    console.log('最终头像URL:', user.value.avatar)
    ElMessage.success('头像上传成功')
  } catch (e) {} finally {
    avatarUploading.value = false
  }
}

// 编辑资料
const handleEdit = () => { editMode.value = true }
const handleSave = async () => {
  await updateUserProfile({
    username: user.value.username,
    phone: user.value.phone,
    age: user.value.age,
    gender: user.value.gender,
    avatar: user.value.avatar
  })
  ElMessage.success('资料已更新')
  editMode.value = false
}

// 修改密码
const handlePasswordUpdate = async () => {
  await updatePassword(passwordForm)
  ElMessage.success('密码修改成功')
  passwordDialog.value = false
  passwordForm.oldPass = ''
  passwordForm.newPass = ''
}

// 过敏史管理
const handleAddAllergy = async () => {
  await addAllergy(user.value.userId, allergyForm)
  ElMessage.success('添加成功')
  allergies.value = await getAllergies(user.value.userId)
  allergyForm.allergen = ''
  allergyForm.description = ''
}
const handleEditAllergy = (item) => {
  allergyEditId.value = item.allergyId
  allergyForm.allergen = item.allergen
  allergyForm.description = item.description
}
const handleUpdateAllergy = async () => {
  await updateAllergy(user.value.userId, allergyEditId.value, allergyForm)
  ElMessage.success('修改成功')
  allergies.value = await getAllergies(user.value.userId)
  allergyEditId.value = null
  allergyForm.allergen = ''
  allergyForm.description = ''
}
const handleDeleteAllergy = async (id) => {
  await ElMessageBox.confirm('确定删除该过敏记录吗？', '提示')
  await deleteAllergy(user.value.userId, id)
  ElMessage.success('删除成功')
  allergies.value = await getAllergies(user.value.userId)
}

// 病史管理
const handleAddHistory = async () => {
  await addHistory(user.value.userId, historyForm)
  ElMessage.success('添加成功')
  histories.value = await getHistories(user.value.userId)
  historyForm.disease = ''
  historyForm.description = ''
}
const handleEditHistory = (item) => {
  historyEditId.value = item.historyId
  historyForm.disease = item.disease
  historyForm.description = item.description
}
const handleUpdateHistory = async () => {
  await updateHistory(user.value.userId, historyEditId.value, historyForm)
  ElMessage.success('修改成功')
  histories.value = await getHistories(user.value.userId)
  historyEditId.value = null
  historyForm.disease = ''
  historyForm.description = ''
}
const handleDeleteHistory = async (id) => {
  await ElMessageBox.confirm('确定删除该病史记录吗？', '提示')
  await deleteHistory(user.value.userId, id)
  ElMessage.success('删除成功')
  histories.value = await getHistories(user.value.userId)
}
</script>

<template>
  <div class="main-content">
    <div class="profile-container" v-if="user">
    <h2>个人主页</h2>
    <el-row>
      <el-col :span="6">
        <el-upload
          class="avatar-uploader"
          :show-file-list="false"
          :before-upload="() => false"
          :on-change="handleAvatarChange"
        >
          <div class="avatar-wrapper">
            <img :src="user.avatar && !user.avatar.startsWith('http') ? 'http://localhost:8085' + user.avatar : user.avatar" class="avatar" />
            <div class="el-upload__text">点击上传头像</div>
          </div>
        </el-upload>
      </el-col>
      <el-col :span="18">
        <el-form :model="user" label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="user.username" :disabled="!editMode" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="user.phone" :disabled="!editMode" />
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number v-model="user.age" :disabled="!editMode" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="user.gender" :disabled="!editMode">
              <el-option label="男" value="Male" />
              <el-option label="女" value="Female" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button v-if="!editMode" @click="handleEdit">编辑资料</el-button>
            <el-button v-else type="primary" @click="handleSave">保存</el-button>
            <el-button type="text" @click="passwordDialog = true">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="passwordDialog" title="修改密码">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPass" type="password" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPass" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordUpdate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 过敏史管理 -->
    <h3 style="margin-top:32px;">过敏史</h3>
    <el-form inline>
      <el-form-item label="过敏源">
        <el-input v-model="allergyForm.allergen" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="allergyForm.description" />
      </el-form-item>
      <el-form-item>
        <el-button v-if="!allergyEditId" type="primary" @click="handleAddAllergy">添加</el-button>
        <el-button v-else type="primary" @click="handleUpdateAllergy">保存</el-button>
        <el-button v-if="allergyEditId" @click="allergyEditId = null">取消</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="allergies" style="width:100%;margin-top:10px;">
      <el-table-column prop="allergen" label="过敏源" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="handleEditAllergy(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDeleteAllergy(scope.row.allergyId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 病史管理 -->
    <h3 style="margin-top:32px;">病史</h3>
    <el-form inline>
      <el-form-item label="疾病">
        <el-input v-model="historyForm.disease" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="historyForm.description" />
      </el-form-item>
      <el-form-item>
        <el-button v-if="!historyEditId" type="primary" @click="handleAddHistory">添加</el-button>
        <el-button v-else type="primary" @click="handleUpdateHistory">保存</el-button>
        <el-button v-if="historyEditId" @click="historyEditId = null">取消</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="histories" style="width:100%;margin-top:10px;">
      <el-table-column prop="diseaseName" label="疾病" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="handleEditHistory(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDeleteHistory(scope.row.historyId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 40px auto;
  background: #fff;
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 0 8px 32px #409eff22;
}
.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e7ff;
  box-shadow: 0 2px 8px #409eff22;
}
.el-upload__text {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: rgba(0,0,0,0.45);
  font-size: 16px;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
  z-index: 2;
}
.avatar-wrapper:hover .el-upload__text {
  opacity: 1;
}
.el-form {
  background: #f8fbff;
  border-radius: 12px;
  padding: 18px 12px 0 12px;
  margin-bottom: 18px;
  box-shadow: 0 1px 6px #b3c6ff11;
}
.el-table {
  border-radius: 16px;
  box-shadow: 0 4px 24px #409eff22;
  margin-top: 16px;
  background: #fff;
  font-size: 16px;
}
.el-table th, .el-table td {
  font-size: 16px;
  padding: 14px 12px;
}
.el-button {
  border-radius: 16px !important;
  font-weight: 500;
  font-size: 15px;
  background: linear-gradient(90deg, #409EFF 0%, #00c6ff 100%) !important;
  color: #fff !important;
  border: none !important;
  box-shadow: 0 2px 8px #409eff22;
  transition: background 0.2s;
}
.el-button:hover {
  background: #0072ff !important;
}
@media (max-width: 900px) {
  .profile-container {
    padding: 12px 4vw;
    border-radius: 8px;
  }
  .avatar-wrapper {
    width: 80px;
    height: 80px;
  }
  .el-form {
    padding: 8px 2px 0 2px;
    border-radius: 6px;
  }
  .el-table {
    border-radius: 6px;
  }
  .el-button {
    font-size: 13px;
    border-radius: 8px !important;
  }
}
</style>