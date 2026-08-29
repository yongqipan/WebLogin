<script setup lang="ts">
defineProps<{ username: string }>()
const emit = defineEmits<{ (e: 'logout'): void }>()

function handleLogout() {
  emit('logout')
}
</script>

<template>
  <div class="home-wrap">
    <div class="home-nav">
      <div class="nav-brand">
        <img src="../assets/logo.svg" alt="logo" />
        <span>WebLogin</span>
      </div>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>

    <main class="home-main">
      <div class="home-card">
        <div class="avatar">
          {{ username.charAt(0).toUpperCase() }}
        </div>
        <h1>欢迎登录本系统</h1>
        <p class="welcome-user">当前用户：<strong>{{ username }}</strong></p>
        <div class="status-badge">
          <span class="status-dot"></span>
          已安全登录
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.home-wrap {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f6fa;
}

.home-nav {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.nav-brand img {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.logout-btn {
  padding: 8px 20px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 13px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}

.logout-btn:hover {
  border-color: #7c3aed;
  color: #7c3aed;
}

.home-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.home-card {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 41, 55, 0.08);
  padding: 48px 40px;
  text-align: center;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5, #9333ea);
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.home-card h1 {
  font-size: 26px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 10px;
}

.welcome-user {
  font-size: 15px;
  color: #6b7280;
  margin-bottom: 24px;
}

.welcome-user strong {
  color: #7c3aed;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  border-radius: 20px;
  background: #ecfdf5;
  color: #059669;
  font-size: 13px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
}
</style>
