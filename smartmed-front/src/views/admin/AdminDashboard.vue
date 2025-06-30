<script setup>
import { ref, onMounted } from 'vue'
import { ElCard, ElRow, ElCol, ElStatistic, ElButton } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../../api/request'
import AdminNavbar from '../../components/common/AdminNavbar.vue'
import { getStatisticByName } from '../../api/statistic'

const stats = ref({ userCount: 0, diseaseCount: 0, medicineCount: 0, pendingFeedbackCount: 0 })
const loading = ref(false)
const router = useRouter()

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await request.get('/statistic/dashboard')
    const data = res.data || res
    stats.value.userCount = data.total_users || 0
    stats.value.diseaseCount = data.total_diseases || 0
    stats.value.medicineCount = data.total_medicines || 0
    stats.value.pendingFeedbackCount = data.pending_feedback || 0
  } finally {
    loading.value = false
  }
}

onMounted(fetchStats)

const navs = [
  { label: '疾病管理', path: '/admin/diseases' },
  { label: '药品管理', path: '/admin/medicines' },
  { label: '反馈管理', path: '/admin/feedbacks' },
  { label: '用户管理', path: '/admin/users' }
]
</script>

<template>
  <AdminNavbar />
  <el-row :gutter="20" justify="center" style="margin-top: 40px;">
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="用户数" :value="stats.userCount" :loading="loading" />
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="疾病数" :value="stats.diseaseCount" :loading="loading" />
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="药品数" :value="stats.medicineCount" :loading="loading" />
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="待处理反馈数" :value="stats.pendingFeedbackCount" :loading="loading" />
      </el-card>
    </el-col>
  </el-row>
  <el-row :gutter="20" justify="center" style="margin-top: 40px;">
    <el-col :span="24">
      <el-card>
        <el-row :gutter="20" justify="center">
          <el-col v-for="nav in navs" :key="nav.path" :span="6">
            <el-button type="primary" size="large" @click="router.push(nav.path)">{{ nav.label }}</el-button>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
.el-card {
  text-align: center;
}
</style>