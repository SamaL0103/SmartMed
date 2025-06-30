<script setup>
import Navbar from './components/common/Navbar.vue'
import Footer from './components/common/Footer.vue'
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <div class="app-container">
    <Navbar v-if="!route.path.startsWith('/admin')" />
    <router-view />
    <Footer v-if="!route.path.startsWith('/admin')" />
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 页面主内容自动撑满中间高度 */
router-view {
  flex: 1;
  padding: 20px;
  box-sizing: border-box;
}

/* 响应式样式扩展点 */
@media (min-width: 1024px) {
  .app-container {
    padding: 0 40px;
  }
}
</style>
