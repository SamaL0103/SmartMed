<script setup>

</script>

<template>
  <footer class="footer">
    <div>
      <span>© 2025DHU SmartMed系统 | </span>
      <router-link to="/feedback" class="footer-link">意见反馈</router-link>
    </div>
    <div class="contact">
      联系方式：smartmed@example.com
    </div>
  </footer>
</template>

<style scoped>
.footer {
  width: 100%;
  background: rgba(245,245,255,0.95);
  color: #666;
  text-align: center;
  padding: 18px 0 10px 0;
  font-size: 14px;
  border-top: 1px solid #e0e0e0;
  position: fixed;
  left: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: 0 -2px 12px #b3c6ff22;
}
.footer .contact {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
  letter-spacing: 0.5px;
}
.footer-link {
  color: #409EFF;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.2s;
}
.footer-link:hover {
  color: #007BFF;
}
@media (max-width: 700px) {
  .footer {
    font-size: 12px;
    padding: 10px 0 6px 0;
  }
  .footer .contact {
    font-size: 10px;
  }
}
</style>