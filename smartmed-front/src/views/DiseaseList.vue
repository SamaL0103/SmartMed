<script setup>
import { ref, onMounted } from 'vue'
import { getDiseaseList } from '../api/disease'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const diseases = ref([])
const searchName = ref('')
const searchKind = ref('')
const searchSymptom = ref('')
const router = useRouter()

const fetchDiseases = async () => {
  diseases.value = await getDiseaseList({
    illnessName: searchName.value,
    kind: searchKind.value,
    illnessSymptom: searchSymptom.value
  })
}

onMounted(fetchDiseases)

const handleSearch = () => {
  fetchDiseases()
}

const goToDetail = (id) => {
  router.push(`/diseases/${id}`)
}

const goToMedicine = (medicine) => {
  if (!medicine || !medicine.id) {
    ElMessage.error('未获取到药品ID，无法跳转');
    return;
  }
  router.push(`/medicines/${medicine.id}`)
}
</script>

<template>
  <div class="disease-list-container">
    <div>
      <h2>疾病信息列表</h2>
      <el-form inline>
        <el-form-item label="疾病名称">
          <el-input v-model="searchName" placeholder="输入疾病名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="searchKind" placeholder="输入分类" clearable />
        </el-form-item>
        <el-form-item label="症状">
          <el-input v-model="searchSymptom" placeholder="输入症状关键词" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="32" style="margin-top: 32px;">
        <el-col :span="6" v-for="item in diseases" :key="item.diseaseId">
          <el-card class="disease-card" shadow="hover">
            <div class="card-header">
              <span class="title">{{ item.name }}</span>
              <el-tag>{{ item.category }}</el-tag>
            </div>
            <div class="desc">常见症状：{{ item.symptoms }}</div>
            <el-button type="primary" size="small" @click="goToDetail(item.diseaseId)">详情</el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.disease-list-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 48px 0 0 0;
  background: #f8fbff;
  min-height: 100vh;
}
.el-row {
  margin-top: 0 !important;
}
.disease-card {
  margin-bottom: 36px;
  border-radius: 24px !important;
  box-shadow: 0 8px 32px #409eff22 !important;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 2px solid #e0e7ff;
  background: #fff !important;
  padding: 0 0 18px 0;
  min-height: 180px;
}
.disease-card:hover {
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
@media (max-width: 1200px) {
  .disease-list-container {
    max-width: 98vw;
    padding: 18px 0 0 0;
  }
  .el-col {
    min-width: 320px;
  }
}
@media (max-width: 900px) {
  .disease-list-container {
    padding: 8px 0 0 0;
  }
  .el-form {
    padding: 10px 6px 0 6px;
    border-radius: 8px;
  }
  .disease-card {
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