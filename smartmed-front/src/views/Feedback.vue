<script setup>
import { ref, onMounted } from 'vue'
import { submitFeedback, getUserFeedbacks } from '../api/feedback'
import { getUserProfile } from '../api/userProfile'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const statusMap = {
  Pending: '待处理',
  InProgress: '处理中',
  Resolved: '已解决'
}

const feedbackContent = ref('')
const feedbackList = ref([])
const loading = ref(false)
const user = ref(null)

const fetchFeedbacks = async () => {
  if (!user.value) return
  feedbackList.value = await getUserFeedbacks(user.value.userId)
}

const handleSubmit = async () => {
  if (!feedbackContent.value.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  loading.value = true
  try {
    await submitFeedback(user.value.userId, feedbackContent.value)
    ElMessage.success('反馈提交成功')
    feedbackContent.value = ''
    fetchFeedbacks()
  } catch (e) {
    // 错误已由拦截器弹窗
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  user.value = await getUserProfile()
  fetchFeedbacks()
})
</script>

<template>
  <div class="feedback-bg">
    <div class="feedback-card">
      <h2>意见反馈</h2>
      <el-form @submit.prevent="handleSubmit">
        <el-form-item label="反馈内容">
          <el-input
            v-model="feedbackContent"
            type="textarea"
            :rows="4"
            placeholder="请填写您的意见或建议"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交反馈</el-button>
        </el-form-item>
      </el-form>
      <h3 class="feedback-title">我的反馈记录</h3>
      <el-table :data="feedbackList" style="width: 100%; margin-top: 10px;">
        <el-table-column prop="content" label="内容">
          <template #default="scope">
            <div v-html="scope.row.content"></div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            {{ statusMap[scope.row.status] || scope.row.status }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间">
          <template #default="scope">
            {{ dayjs(scope.row.createdAt).format('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.feedback-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #f8fbff 60%, #e3f0ff 100%);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 60px;
}
.feedback-card {
  width: 600px;
  background: #fff;
  border-radius: 32px;
  box-shadow: 0 8px 40px #409eff22;
  padding: 40px 36px 36px 36px;
  margin-bottom: 60px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}
.feedback-title {
  margin-top: 36px;
  margin-bottom: 18px;
  font-size: 1.2rem;
  color: #409EFF;
  font-weight: 600;
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
  .feedback-card {
    width: 98vw;
    padding: 16px 2vw 18px 2vw;
    border-radius: 14px;
  }
}
</style>