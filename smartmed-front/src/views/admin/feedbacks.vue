<script setup>
import { ref, onMounted } from 'vue'
import AdminNavbar from '../../components/common/AdminNavbar.vue'
import { getAllFeedback, getStatusFeedbacks, updateFeedbackStatus, deleteFeedback } from '../../api/feedback'
import { ElMessage, ElMessageBox } from 'element-plus'

const feedbacks = ref([])
const status = ref('')
const statusOptions = [
  { label: '待处理', value: 'Pending' },
  { label: '处理中', value: 'InProgress' },
  { label: '处理完成', value: 'Resolved' }
]

const fetchList = async () => {
  let list
  if (!status.value) {
    list = await getAllFeedback()
  } else {
    list = await getStatusFeedbacks(status.value)
  }
  feedbacks.value = (list || []).slice().sort((a, b) =>a.feedbackId - b.feedbackId)
}
onMounted(fetchList)

const updateStatus = async (row, newStatus) => {
     console.log(row)
  await updateFeedbackStatus(row.feedbackId, newStatus)
  ElMessage.success('状态已更新')
  fetchList()
}
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该反馈吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteFeedback(id)
      ElMessage.success('删除成功')
      fetchList()
    })
}

function formatDateTime(val) {
  if (!val) return ''
  // 兼容带T和时区的ISO格式
  const d = new Date(val)
  const pad = n => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}
</script>

<template>
  <AdminNavbar />
  <div class="admin-page">
    <el-card>
      <div class="toolbar">
        <el-select v-model="status" placeholder="按状态筛选" style="width: 180px; margin-right: 12px;" @change="fetchList">
          <el-option label="全部" value="" />
          <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
      </div>
      <el-table :data="feedbacks" style="width: 100%; margin-top: 18px;">
        <el-table-column prop="feedbackId" label="反馈ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="content" label="内容" width="240"/>
        <el-table-column prop="status" label="状态" width="140">
          <template #default="{ row }">
            <el-select v-model="row.status" placeholder="请选择" size="small" @change="val => updateStatus(row, val)">
              <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="120">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row.feedbackId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.admin-page { max-width: 1100px; margin: 32px auto; }
.toolbar { margin-bottom: 12px; display: flex; align-items: center; }
</style>