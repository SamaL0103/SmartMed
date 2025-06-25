<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMedicineDetail } from '../api/medicine'
import request from '../api/request'

const route = useRoute()
const router = useRouter()
const medicine = ref(null)         // 药品基本信息
const priceList = ref([])          // 药品价格列表

// 获取药品详情和价格
onMounted(async () => {
  const id = route.params.id // 路由参数，和router/index.js配置一致
  // 1. 获取药品详情（参数名要和后端@RequestParam("id")一致）
  medicine.value = await getMedicineDetail(id)
  // 2. 获取药品价格（路径参数要和后端@PathVariable("id")一致）
  priceList.value = await request.get(`/medicinePrice/price/${id}`)
})

// 返回上一页
const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="detail-bg">
    <div class="detail-card">
      <div v-if="medicine">
        <div class="detail-header">
          <el-button class="back-btn" @click="goBack" type="primary" plain>
            <i class="el-icon-arrow-left" style="margin-right:6px;"></i>返回
          </el-button>
          <h2>{{ medicine.name }}</h2>
        </div>
        <el-descriptions title="药品详情" :column="1" border>
          <el-descriptions-item label="类别">{{ medicine.category }}</el-descriptions-item>
          <el-descriptions-item label="功效">{{ medicine.efficacy }}</el-descriptions-item>
          <el-descriptions-item label="用法">{{ medicine.usageMethod }}</el-descriptions-item>
          <el-descriptions-item label="禁忌">{{ medicine.contraindications }}</el-descriptions-item>
          <el-descriptions-item label="不良反应">{{ medicine.sideEffects }}</el-descriptions-item>
        </el-descriptions>
        <h3 class="related-title">价格比价</h3>
        <el-table :data="priceList.slice(0, 3)" style="width: 100%; margin-top: 10px;">
          <el-table-column prop="storeName" label="商家" />
          <el-table-column prop="specification" label="规格" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="url" label="购买链接">
            <template #default="scope">
              <a :href="scope.row.url" target="_blank" style="color: #409EFF;">跳转</a>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else>
        <el-empty description="未找到药品信息" />
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
.related-title {
  margin-top: 36px;
  margin-bottom: 18px;
  font-size: 1.2rem;
  color: #409EFF;
  font-weight: 600;
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
  .el-table {
    border-radius: 6px;
  }
}
</style>