<script setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import { sendChatQuery, getChatHistory, clearChatHistory } from '../api/chat'
import { getUserProfile } from '../api/userProfile'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import userAvatar from '../assets/images/user-avatar.png'
import aiAvatar from '../assets/images/ai-avatar.png'
import { marked } from 'marked'

const router = useRouter()
const input = ref('')
const chatList = ref([]) // 聊天历史
const loading = ref(false)
const relatedDiseases = ref([])
const medicinesWithPrices = ref([])
const chatBox = ref(null)
const user = ref(null)

const userAvatarUrl = computed(() => {
  if (!user.value || !user.value.avatar) return userAvatar
  let url = user.value.avatar
  if (!url.startsWith('http')) {
    url = 'http://localhost:8085' + url
  }
  return url
})

onMounted(async () => {
  user.value = await getUserProfile()
  // AI欢迎语
  chatList.value.push({
    from: 'ai',
    content: '您好！我是您的智能健康助手，请直接输入您的健康问题，我会结合您的病史和过敏史，给您最简明的建议。'
  })
})

// 发送消息
const handleSend = async () => {
  if (!input.value.trim()) return
  loading.value = true
  chatList.value.push({from: 'user', content: input.value})
  try {
    const res = await sendChatQuery(user.value.userId, input.value)
    // 只显示AI回复的message字段
    chatList.value.push({from: 'ai', content: res.message})
    // 相关疾病和药品
    relatedDiseases.value = res.relatedDiseases || []
    medicinesWithPrices.value = []
    if (res.medicinesWithPrices && Object.keys(res.medicinesWithPrices).length > 0) {
      for (const [medicine, prices] of Object.entries(res.medicinesWithPrices)) {
        medicinesWithPrices.value.push({
          medicine: typeof medicine === 'string' ? JSON.parse(medicine) : medicine,
          prices
        })
      }
    }
    input.value = ''
    nextTick(() => {
      if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
    })
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    loading.value = false
  }
}

// 跳转到疾病详情
const goToDisease = (id) => {
  if (!id) {
    ElMessage.error('未获取到疾病ID，无法跳转');
    return;
  }
  router.push(`/diseases/${id}`)
}

// 跳转到药品详情
const goToMedicine = (id) => {
  if (!id) {
    ElMessage.error('未获取到药品ID，无法跳转');
    return;
  }
  router.push(`/medicines/${id}`)
}
</script>

<template>
  <div class="main-content">
    <div class="chat-container">
      <h2>智能健康问答</h2>
      <div class="chat-history" ref="chatBox">
        <div
            v-for="(item, idx) in chatList"
            :key="idx"
            :class="['chat-item', item.from]"
        >
          <div class="avatar">
            <img v-if="item.from === 'user'" :src="userAvatarUrl" alt="我"/>
            <img v-else :src="aiAvatar" alt="AI"/>
          </div>
          <div class="bubble" v-if="item.from === 'ai'" v-html="marked(item.content)"
               style="white-space: pre-line;"></div>
          <div class="bubble" v-else>{{ item.content }}</div>
        </div>
      </div>
      <div class="chat-input-bar">
        <el-input
            v-model="input"
            placeholder="请输入健康相关问题"
            @keyup.enter="handleSend"
            :disabled="loading"
        />
        <el-button type="primary" :loading="loading" @click="handleSend">发送</el-button>
      </div>
      <div v-if="relatedDiseases.length" style="margin-top: 32px;">
        <h3>相关疾病</h3>
        <el-table :data="relatedDiseases" style="width: 100%;">
          <el-table-column prop="name" label="疾病名称">
            <template #default="scope">
              <el-link type="primary" @click="goToDisease(scope.row.diseaseId || scope.row.id)">
                {{ scope.row.name }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类"/>
          <el-table-column prop="symptoms" label="常见症状"/>
        </el-table>
      </div>
      <div v-if="medicinesWithPrices.length" style="margin-top: 32px;">
        <h3>相关药品及价格</h3>
        <div v-for="item in medicinesWithPrices" :key="item.medicine.id" style="margin-bottom: 24px;">
          <div>
            <el-link type="primary" @click="goToMedicine(item.medicine.medicineId || item.medicine.id)">
              {{ item.medicine.name }}
            </el-link>
            <span>（{{ item.medicine.efficacy }}）</span>
          </div>
          <el-table :data="item.prices" style="width: 100%; margin-top: 8px;">
            <el-table-column prop="platform" label="平台"/>
            <el-table-column prop="price" label="价格"/>
            <el-table-column prop="url" label="购买链接">
              <template #default="scope">
                <a :href="scope.row.url" target="_blank" style="color: #409EFF;">跳转</a>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  width: 800px;
  margin: 40px auto;
  background: #fff;
  border-radius: 18px;
  padding: 32px 24px;
  box-shadow: 0 2px 12px #b3c6ff22;
  display: flex;
  flex-direction: column;
  height: 750px;
  position: relative;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f7f7f7;
  border-radius: 12px 12px 0 0;
}

.chat-item {
  display: flex;
  align-items: flex-end;
  margin-bottom: 12px;
}

.chat-item.user {
  flex-direction: row-reverse;
}

.chat-item .avatar {
  width: 36px;
  height: 36px;
  margin: 0 8px;
}

.chat-item .avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.chat-item .bubble {
  max-width: 70%;
  padding: 10px 16px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 1px 4px #eee;
  font-size: 15px;
  word-break: break-all;
  line-height: 1.6;
}

.chat-item.user .bubble {
  background: #409EFF;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.chat-item.ai .bubble {
  background: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 4px;
}

.chat-input-bar {
  display: flex;
  align-items: center;
  margin-top: 12px;
  background: #f8fbff;
  border-radius: 0 0 12px 12px;
  padding: 12px 10px;
  box-shadow: 0 -1px 6px #b3c6ff11;
}

.el-input {
  border-radius: 8px !important;
  margin-right: 12px;
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

.el-table {
  border-radius: 12px;
  box-shadow: 0 2px 12px #b3c6ff11;
  margin-top: 10px;
  background: #fff;
}

.el-table th, .el-table td {
  font-size: 15px;
}

@media (max-width: 900px) {
  .chat-container {
    width: 98vw;
    min-width: 0;
    padding: 10px 2vw;
    border-radius: 8px;
    height: 90vw;
    min-height: 500px;
  }

  .chat-history {
    padding: 6px;
    border-radius: 8px 8px 0 0;
  }

  .chat-input-bar {
    padding: 6px 4px;
    border-radius: 0 0 8px 8px;
  }

  .el-button {
    font-size: 13px;
    border-radius: 8px !important;
  }

  .el-table {
    border-radius: 6px;
  }
}
</style>