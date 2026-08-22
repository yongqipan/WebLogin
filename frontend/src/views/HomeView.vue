<script setup lang="ts">
defineProps<{ username: string }>()
const emit = defineEmits<{ (e: 'logout'): void }>()

function handleLogout() {
  emit('logout')
}
</script>

<template>
  <div class="home-wrap">
    <div class="home-box">
      <h1>欢迎登录本系统</h1>
      <p class="welcome-user">当前用户：{{ username }}</p>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </div>
</template>

<style scoped>
.home-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #43cea2, #185a9d);
}
.home-box {
  text-align: center;
  color: #fff;
}
.home-box h1 {
  font-size: 32px;
  margin-bottom: 16px;
}
.welcome-user {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}
.logout-btn {
  padding: 10px 28px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  color: #185a9d;
  font-size: 14px;
  cursor: pointer;
}
</style>
