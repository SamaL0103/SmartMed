<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'

const router = useRouter()
const form = ref({
  userAccount: '',
  userPwd: ''
})
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    // login 返回的就是后端 data 字段（因为有axios拦截器）
    const user = await login(form.value)
    // 判断是否管理员
    if (user.isAdmin) {
      router.push('/admin/dashboard') // 跳转到后台管理首页
    } else {
      router.push('/') // 跳转到首页
    }
  } catch (e) {
    // 错误已由拦截器弹窗
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <el-form :model="form" label-width="80px" @submit.prevent="handleLogin">
      <el-form-item label="账号">
        <el-input v-model="form.userAccount" autocomplete="username" placeholder="昵称" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.userPwd" type="password" autocomplete="current-password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
        <el-button type="text" @click="$router.push('/register')">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 80px auto;
  padding: 32px 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #eee;
}
</style>