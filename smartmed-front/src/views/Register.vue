<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/user'

const router = useRouter()
const form = ref({
  userName: '',
  userPwd: '',
  userTel: '',
  userAge: null,
  userSex: '男'
})
const loading = ref(false)

const handleRegister = async () => {
  loading.value = true
  try {
    // 参数名要和后端@RequestParam一致
    await register(form.value)
    // 注册成功后跳转到登录页
    router.push('/login')
  } catch (e) {
    // 错误已由拦截器弹窗
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-container">
    <el-form :model="form" label-width="80px" @submit.prevent="handleRegister">
      <el-form-item label="昵称">
        <el-input v-model="form.userName" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.userPwd" type="password" />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.userTel" />
      </el-form-item>
      <el-form-item label="年龄">
        <el-input-number v-model="form.userAge" :min="0" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.userSex">
          <el-radio label="男">男</el-radio>
          <el-radio label="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
        <el-button class="back-btn" type="primary" plain @click="$router.push('/login')">
          <i class="el-icon-arrow-left" style="margin-right:6px;"></i>返回登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.register-container {
  max-width: 500px;
  margin: 80px auto;
  padding: 32px 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #eee;
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
</style>