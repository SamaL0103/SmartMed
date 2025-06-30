<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMedicineList } from '../api/medicine'
import { getDiseaseList } from '../api/disease'

const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.keyword || '')
const medicines = ref([])
const diseases = ref([])
const loading = ref(false)

const fetchResults = async () => {
  if (!keyword.value) return
  loading.value = true
  const [meds, dis] = await Promise.all([
    getMedicineList({ keyword: keyword.value }),
    getDiseaseList({ illnessName: keyword.value })
  ])
  medicines.value = meds
  diseases.value = dis
  loading.value = false
}

onMounted(fetchResults)
watch(() => route.query.keyword, val => {
  keyword.value = val
  fetchResults()
})
</script>

<template>
  <div class="search-result-page">
    <h2>“{{ keyword }}” 的搜索结果</h2>
    <el-divider />
    <div v-if="loading" style="text-align:center;margin:40px 0;">
      <el-spin />
    </div>
    <template v-else>
      <div v-if="!medicines.length && !diseases.length">
        <el-empty description="没有找到相关药品或疾病" />
      </div>
      <div v-else class="result-flex">
        <div v-if="medicines.length" class="result-block">
          <h3>相关药品</h3>
          <el-row :gutter="24">
            <el-col :span="8" v-for="item in medicines" :key="item.medicineId">
              <el-card class="medicine-card" @click="router.push(`/medicines/${item.medicineId}`)">
                <span class="med-title">{{ item.name }}</span>
                <el-tag style="margin-left:8px;">{{ item.category }}</el-tag>
                <div class="desc">{{ item.efficacy }}</div>
              </el-card>
            </el-col>
          </el-row>
        </div>
        <div v-if="diseases.length" class="result-block">
          <h3>相关疾病</h3>
          <el-row :gutter="24">
            <el-col :span="8" v-for="item in diseases" :key="item.diseaseId">
              <el-card class="disease-card" @click="router.push(`/diseases/${item.diseaseId}`)">
                <span class="dis-title">{{ item.name }}</span>
                <el-tag style="margin-left:8px;">{{ item.category }}</el-tag>
                <div class="desc">{{ item.symptoms }}</div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.search-result-page {
  max-width: 1400px;
  margin: 40px auto;
  background: #fff;
  border-radius: 32px;
  padding: 48px 40px;
  box-shadow: 0 8px 32px #409eff22;
}
.result-flex {
  display: flex;
  gap: 48px;
  justify-content: flex-start;
}
.result-block {
  flex: 1;
}
.medicine-card, .disease-card {
  margin-bottom: 24px;
  border-radius: 18px;
  box-shadow: 0 4px 24px #409eff22;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 2px solid #e0e7ff;
  background: #f8fbff;
}
.medicine-card:hover, .disease-card:hover {
  box-shadow: 0 12px 40px #409eff33;
  transform: translateY(-4px) scale(1.04);
  border-color: #409EFF;
}
.med-title {
  font-weight: bold;
  font-size: 20px;
  color: #409EFF;
}
.dis-title {
  font-weight: bold;
  font-size: 20px;
  color: #e53935;
}
.desc {
  color: #666;
  font-size: 15px;
  margin-top: 8px;
}
@media (max-width: 900px) {
  .search-result-page {
    padding: 16px 2vw;
    border-radius: 14px;
  }
  .result-flex {
    flex-direction: column;
    gap: 18px;
  }
  .medicine-card, .disease-card {
    border-radius: 10px;
    padding: 0 0 8px 0;
  }
  .med-title, .dis-title {
    font-size: 16px;
  }
  .desc {
    font-size: 13px;
  }
}
</style>