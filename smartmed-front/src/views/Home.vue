<script setup>
import { ref, onMounted } from 'vue'
import { getUserProfile } from '../api/userProfile'
import { useRouter } from 'vue-router'
import { getMedicineList } from '../api/medicine'
import { getDiseaseList } from '../api/disease'

const user = ref(null)
const router = useRouter()
const searchText = ref('')
const searchResults = ref([])
const searching = ref(false)

onMounted(async () => {
  try {
    user.value = await getUserProfile()
  } catch (e) {
    user.value = null
  }
})

const handleSearch = () => {
  if (!searchText.value.trim()) return
  router.push({ path: '/search', query: { keyword: searchText.value } })
}

const goToDetail = (item) => {
  if (item._type === 'medicine') {
    router.push(`/medicines/${item.medicineId || item.id}`)
  } else {
    router.push(`/diseases/${item.diseaseId || item.id}`)
  }
}
</script>

<template>
  <div class="home-bright">
    <div class="bright-banner">
      <img src="@/assets/images/banner.png" alt="SmartMed Banner" class="banner-img" />
      <div class="banner-content">
        <h1>
          <span class="gradient-text">SmartMed</span> 智慧医疗
        </h1>
        <p>AI驱动的健康管理与智能问诊平台</p>
        <div class="banner-btns" v-if="!user">
          <router-link to="/login">
            <el-button type="primary" size="large" class="banner-btn">立即登录</el-button>
          </router-link>
          <router-link to="/register">
            <el-button size="large" class="banner-btn" style="margin-left: 16px;">注册新账号</el-button>
          </router-link>
        </div>
        <div class="home-search-area">
          <el-input
            v-model="searchText"
            placeholder="搜索药品、疾病名称或症状，回车或点击搜索"
            class="home-search-input"
            size="large"
            clearable
            @keyup.enter="handleSearch"
            style="width: 700px;"
          >
            <template #append>
              <el-button type="primary" :loading="searching" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
          <div v-if="searchResults.length" class="home-search-result">
            <div
              v-for="item in searchResults"
              :key="item.medicineId || item.diseaseId || item.id"
              class="search-result-card"
              @click="goToDetail(item)"
            >
              <i :class="item._type === 'medicine' ? 'el-icon-medicine-box' : 'el-icon-cpu'" style="margin-right:8px;"></i>
              <span class="result-title">{{ item.name }}</span>
              <span v-if="item._type === 'medicine'" class="result-tag">药品</span>
              <span v-else class="result-tag disease">疾病</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="features-bright">
      <div class="feature-card-bright">
        <i class="el-icon-cpu feature-icon"></i>
        <h3>AI智能问诊</h3>
        <p>大模型驱动，7x24小时健康咨询，个性化分析与建议。</p>
      </div>
      <div class="feature-card-bright">
        <i class="el-icon-data-analysis feature-icon"></i>
        <h3>权威数据</h3>
        <p>海量药品/疾病数据库，实时更新，科学可靠。</p>
      </div>
      <div class="feature-card-bright">
        <i class="el-icon-user feature-icon"></i>
        <h3>专属健康档案</h3>
        <p>个人病史、过敏史、反馈一站式管理</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-bright {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fbff 0%, #eaf6ff 100%);
  position: relative;
  overflow-x: hidden;
}
.bright-banner {
  position: relative;
  width: 100%;
  height: 340px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 32px;
  border-radius: 18px;
  box-shadow: 0 4px 32px #b3c6ff33;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.92) blur(0.5px);
}
.banner-content {
  position: absolute;
  left: 50%; top: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255,255,255,0.85);
  border-radius: 20px;
  box-shadow: 0 8px 32px 0 #b3c6ff33;
  padding: 40px 56px;
  text-align: center;
  color: #222;
  min-width: 420px;
  border: 1.5px solid #e0e7ff;
}
.banner-content h1 {
  font-size: 2.6rem;
  font-weight: bold;
  margin-bottom: 12px;
  letter-spacing: 2px;
}
.gradient-text {
  background: linear-gradient(90deg, #409EFF 0%, #00c6ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.banner-content p {
  font-size: 1.2rem;
  margin-bottom: 24px;
  color: #409EFF;
}
.banner-btns {
  display: flex;
  justify-content: center;
  gap: 16px;
}
.banner-btn {
  border-radius: 22px !important;
  font-size: 1.1rem;
  font-weight: 500;
  box-shadow: 0 2px 12px #409eff33;
  transition: background 0.2s, color 0.2s;
}
.banner-btn:hover {
  background: #0072ff !important;
  color: #fff !important;
}
.features-bright {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin: 64px 0 0 0;
  z-index: 2;
  position: relative;
}
.feature-card-bright {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 24px #b3c6ff22;
  padding: 36px 28px;
  width: 300px;
  text-align: center;
  color: #222;
  border: 1.5px solid #e0e7ff;
  transition: box-shadow 0.2s, transform 0.2s, background 0.2s;
}
.feature-card-bright:hover {
  box-shadow: 0 8px 32px #409eff55;
  background: #f0f7ff;
  transform: translateY(-8px) scale(1.04);
}
.feature-icon {
  font-size: 44px;
  color: #409EFF;
  margin-bottom: 16px;
}
@media (max-width: 900px) {
  .features-bright {
    flex-direction: column;
    align-items: center;
    gap: 24px;
  }
  .banner-content {
    min-width: 0;
    width: 90vw;
    padding: 32px 8vw;
  }
}
.home-search-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 32px 0 0 0;
}
.home-search-input {
  font-size: 22px !important;
  border-radius: 32px !important;
  box-shadow: 0 4px 24px #409eff22;
  padding: 12px 24px;
  background: #fff;
}
.home-search-result {
  margin-top: 32px;
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: center;
}
.search-result-card {
  display: flex;
  align-items: center;
  background: linear-gradient(90deg, #e0e7ff 0%, #c6e2ff 100%);
  color: #409EFF;
  border-radius: 18px;
  font-size: 18px;
  font-weight: 600;
  box-shadow: 0 2px 12px #409eff22;
  padding: 14px 32px;
  cursor: pointer;
  border: 2px solid #b3c6ff;
  transition: background 0.2s, color 0.2s, border 0.2s, transform 0.2s, box-shadow 0.2s;
}
.search-result-card:hover {
  background: linear-gradient(90deg, #409EFF 0%, #00c6ff 100%);
  color: #fff;
  border: 2px solid #409EFF;
  transform: scale(1.08) translateY(-2px);
  box-shadow: 0 8px 32px #409eff33;
}
.result-title {
  margin-right: 16px;
}
.result-tag {
  background: #fff;
  color: #409EFF;
  border-radius: 8px;
  padding: 2px 10px;
  font-size: 14px;
  margin-left: 8px;
}
.result-tag.disease {
  color: #e53935;
  background: #fff0f0;
}
</style>