<script setup>
import { ref, onMounted } from 'vue'
import { getMedicineList } from '../api/medicine'
import { useRouter } from 'vue-router'

const medicines = ref([]) // 药品列表
const searchName = ref('') // 搜索用
const searchEfficacy = ref('') // 搜索用
const router = useRouter()

// 获取药品列表（可带搜索参数）
const fetchMedicines = async () => {
  medicines.value = await getMedicineList({
    name: searchName.value,
    efficacy: searchEfficacy.value
  })
}

// 页面加载时自动获取
onMounted(fetchMedicines)

// 搜索按钮点击
const handleSearch = () => {
  fetchMedicines()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/medicines/${id}`)
}
</script>

<template>
  <div class="medicine-list-container">
    <div>
      <h2>药品信息列表</h2>
      <!-- 搜索区域 -->
      <el-form inline>
        <el-form-item label="药品名称">
          <el-input v-model="searchName" placeholder="输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="功效">
          <el-input v-model="searchEfficacy" placeholder="输入功效" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="32" style="margin-top: 32px;">
        <el-col :span="6" v-for="item in medicines" :key="item.medicineId">
          <el-card class="medicine-card" shadow="hover">
            <div class="card-header">
              <span class="title">{{ item.name }}</span>
              <el-tag
                :class="item.category === '处方药' ? 'rx-tag' : (item.category === '非处方药' ? 'otc-tag' : '')"
              >
                {{ item.category }}
              </el-tag>
            </div>
            <div class="desc">功效：{{ item.efficacy }}</div>
            <el-button type="primary" size="small" @click="goToDetail(item.medicineId)">详情</el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.medicine-list-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 48px 0 0 0;
  background: #f8fbff;
  min-height: 100vh;
}
.el-row {
  margin-top: 0 !important;
}
.medicine-card {
  margin-bottom: 36px;
  border-radius: 24px !important;
  box-shadow: 0 8px 32px #409eff22 !important;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 2px solid #e0e7ff;
  background: #fff !important;
  padding: 0 0 18px 0;
  min-height: 180px;
}
.medicine-card:hover {
  box-shadow: 0 12px 40px #409eff33 !important;
  transform: translateY(-6px) scale(1.04);
  border-color: #409EFF;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 24px 24px 0 24px;
}
.title {
  font-weight: bold;
  font-size: 22px;
  color: #222;
}
.el-tag {
  background: linear-gradient(90deg, #e0e7ff 0%, #c6e2ff 100%);
  color: #409EFF;
  border: none;
  font-weight: 500;
  font-size: 13px;
  border-radius: 8px;
  padding: 2px 12px;
}
.desc {
  margin-bottom: 16px;
  color: #666;
  font-size: 16px;
  padding: 0 24px;
}
.el-button {
  border-radius: 18px !important;
  font-weight: 500;
  font-size: 16px;
  margin-left: 24px;
  margin-top: 12px;
  background: linear-gradient(90deg, #409EFF 0%, #00c6ff 100%) !important;
  color: #fff !important;
  border: none !important;
  box-shadow: 0 2px 8px #409eff22;
  transition: background 0.2s;
}
.el-button:hover {
  background: #0072ff !important;
}
.rx-tag {
  background: #fff0f0 !important;
  color: #e53935 !important;
  border: 1.5px solid #e53935 !important;
  font-weight: bold;
}
.otc-tag {
  background: #f0fff0 !important;
  color: #43a047 !important;
  border: 1.5px solid #43a047 !important;
  font-weight: bold;
}
@media (max-width: 1200px) {
  .medicine-list-container {
    max-width: 98vw;
    padding: 18px 0 0 0;
  }
  .el-col {
    min-width: 320px;
  }
}
@media (max-width: 900px) {
  .medicine-list-container {
    padding: 8px 0 0 0;
  }
  .el-form {
    padding: 10px 6px 0 6px;
    border-radius: 8px;
  }
  .medicine-card {
    border-radius: 12px !important;
    padding: 0 0 8px 0;
    min-height: 120px;
  }
  .card-header {
    padding: 10px 8px 0 8px;
  }
  .title {
    font-size: 16px;
  }
  .desc {
    font-size: 13px;
    padding: 0 8px;
  }
  .el-button {
    font-size: 13px;
    margin-left: 8px;
    margin-top: 4px;
    border-radius: 8px !important;
  }
}
</style>