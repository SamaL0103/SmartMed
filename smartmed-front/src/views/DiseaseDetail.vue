<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDiseaseDetail, getRelatedMedicines } from '../api/disease'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const disease = ref(null)
const relatedMedicines = ref([])

onMounted(async () => {
  const id = route.params.id
  disease.value = await getDiseaseDetail(id)
  relatedMedicines.value = await getRelatedMedicines(id)
})

const goBack = () => {
  router.back()
}

const goToMedicineDetail = (medicine) => {
  const id = medicine.medicineId || medicine.id
  if (!id) {
    ElMessage.error('未获取到药品ID，无法跳转');
    return;
  }
  router.push(`/medicines/${id}`)
}
</script>

<template>
  <div class="detail-bg">
    <div class="detail-card">
      <div v-if="disease">
        <div class="detail-header">
          <el-button class="back-btn" @click="goBack" type="primary" plain>
            <i class="el-icon-arrow-left" style="margin-right:6px;"></i>返回
          </el-button>
          <h2>{{ disease.name }}</h2>
        </div>
        <el-descriptions title="疾病详情" :column="1" border>
          <el-descriptions-item label="分类">{{ disease.category }}</el-descriptions-item>
          <el-descriptions-item label="病因">{{ disease.causes }}</el-descriptions-item>
          <el-descriptions-item label="常见症状">{{ disease.symptoms }}</el-descriptions-item>
        </el-descriptions>
        <h3 class="related-title">相关药品</h3>
        <div class="related-medicine-list">
          <el-empty v-if="!relatedMedicines.length" description="暂无相关药品" />
          <div v-else>
            <div
              v-for="item in relatedMedicines"
              :key="item.medicineId || item.id"
              class="related-medicine-card"
              @click="goToMedicineDetail(item)"
            >
              <i class="el-icon-medicine-box related-icon"></i>
              <span>{{ item.name }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <el-empty description="未找到疾病信息" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #f8fbff 60%, #e3f0ff 100%);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 60px;
}
.detail-card {
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
.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}
.detail-header h2 {
  font-size: 2rem;
  font-weight: bold;
  color: #409EFF;
  margin-left: 18px;
}
.el-descriptions {
  margin-bottom: 24px;
  background: #f8fbff;
  border-radius: 12px;
  padding: 18px 12px;
}
.el-descriptions__title {
  font-size: 1.1rem;
  color: #409EFF;
  font-weight: 600;
}
.el-button[type="text"] {
  color: #409EFF;
  font-weight: 500;
  font-size: 15px;
  margin-right: 8px;
}
.back-btn {
  border-radius: 18px !important;
  background: linear-gradient(90deg, #409EFF 0%, #00c6ff 100%) !important;
  color: #fff !important;
  border: none !important;
  font-weight: 500;
  padding: 6px 22px;
  box-shadow: 0 2px 8px #409eff22;
  margin-right: 18px;
  font-size: 15px;
  display: flex;
  align-items: center;
  transition: background 0.2s;
}
.back-btn:hover {
  background: linear-gradient(90deg, #0072ff 0%, #00c6ff 100%) !important;
  color: #fff !important;
}
@media (max-width: 900px) {
  .detail-card {
    width: 98vw;
    padding: 16px 2vw 18px 2vw;
    border-radius: 14px;
  }
  .detail-header h2 {
    font-size: 1.2rem;
    margin-left: 8px;
  }
  .el-descriptions {
    padding: 8px 2px;
    border-radius: 6px;
  }
}
.related-title {
  margin-top: 36px;
  margin-bottom: 18px;
  font-size: 1.2rem;
  color: #409EFF;
  font-weight: 600;
}
.related-medicine-list {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}
.related-medicine-card {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(90deg, #e0e7ff 0%, #c6e2ff 100%);
  color: #409EFF;
  border-radius: 18px;
  font-size: 17px;
  font-weight: 600;
  box-shadow: 0 2px 12px #409eff22;
  padding: 10px 28px;
  cursor: pointer;
  border: 2px solid #b3c6ff;
  transition: background 0.2s, color 0.2s, border 0.2s, transform 0.2s, box-shadow 0.2s;
}
.related-medicine-card:hover {
  background: linear-gradient(90deg, #409EFF 0%, #00c6ff 100%);
  color: #fff;
  border: 2px solid #409EFF;
  transform: scale(1.08) translateY(-2px);
  box-shadow: 0 8px 32px #409eff33;
}
.related-icon {
  font-size: 20px;
}
</style>