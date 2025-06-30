<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getUserProfile, logout } from '../../api/userProfile'
import defaultAvatar from '../../assets/images/user-avatar.png'

const user = ref(null)
const router = useRouter()

const avatarUrl = computed(() => {
  if (!user.value || !user.value.avatar) return defaultAvatar
  let url = user.value.avatar
  if (!url.startsWith('http')) {
    url = 'http://localhost:8085' + url
  }
  return url
})

const fetchUser = async () => {
  try {
    user.value = await getUserProfile()
  } catch (e) {
    user.value = null
  }
}

const handleLogout = async () => {
  try {
    await logout()
  } catch (e) {}
  window.location.reload()
}

onMounted(fetchUser)
</script>

<template>
  <nav class="navbar-glass">
    <div class="navbar-left">
      <span class="logo">
        <i class="el-icon-cpu logo-icon"></i>
        SmartMed
      </span>
      <router-link to="/" class="nav-link">首页</router-link>
      <router-link to="/chat" class="nav-link">智能医生</router-link>
      <router-link to="/diseases" class="nav-link">疾病信息</router-link>
      <router-link to="/medicines" class="nav-link">药品信息</router-link>
    </div>
    <div class="navbar-right">
      <router-link to="/profile" class="nav-link">个人中心</router-link>
      <template v-if="user">
        <img :src="avatarUrl" class="avatar" @error="e => e.target.src = defaultAvatar" />
        <span class="nickname">{{ user.username }}</span>
        <el-button type="primary" class="logout-btn" @click="handleLogout">退出</el-button>
      </template>
      <template v-else>
        <router-link to="/login" class="nav-link">登录</router-link>
        <router-link to="/register" class="nav-link">注册</router-link>
      </template>
    </div>
  </nav>
</template>

<style scoped>
.navbar-glass {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 0 48px;
  height: 68px;
  border-radius: 0 0 20px 20px;
  box-shadow: 0 4px 24px rgba(64, 158, 255, 0.15);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-weight: bold;
  font-size: 24px;
  color: #007BFF;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 1.2px;
  text-shadow: 0 1px 1px rgba(0, 123, 255, 0.1);
}

.logo-icon {
  font-size: 28px;
  color: #007BFF;
}

.navbar-left,
.navbar-right {
  display: flex;
  align-items: center;
}

.nav-link {
  margin: 0 14px;
  color: #333;
  font-weight: 500;
  font-size: 16px;
  position: relative;
  transition: all 0.25s ease;
  padding: 6px 0;
  text-decoration: none;
}

.nav-link.router-link-exact-active,
.nav-link:hover {
  color: #007BFF;
}

.nav-link.router-link-exact-active::after,
.nav-link:hover::after {
  content: '';
  display: block;
  height: 3px;
  width: 100%;
  background-color: #007BFF;
  border-radius: 2px;
  position: absolute;
  left: 0;
  bottom: -4px;
  transition: all 0.2s ease;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 10px 0 18px;
  border: 2px solid #dce8ff;
  box-shadow: 0 2px 6px rgba(0, 123, 255, 0.2);
}

.nickname {
  margin-right: 18px;
  font-weight: 500;
  color: #222;
  letter-spacing: 0.5px;
}

.logout-btn {
  border-radius: 20px !important;
  background: linear-gradient(90deg, #007BFF 0%, #00c6ff 100%) !important;
  color: #fff !important;
  border: none !important;
  font-weight: 500;
  padding: 6px 22px;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.3);
  transition: all 0.25s ease;
}

.logout-btn:hover {
  background: linear-gradient(90deg, #005bb5 0%, #0099cc 100%) !important;
}
</style>