<script setup lang="ts">
import { ref } from 'vue'
import { onMounted } from 'vue'

const username = ref<string>(localStorage.getItem('login_user') || '')

onMounted(() => {
  const current = localStorage.getItem('login_user')
  if (current) {
    username.value = current
  }
})
</script>

<template>
  <div class="home-wrap">
    <main class="home-main">
      <div class="home-card">
        <div class="avatar">
          {{ (username || 'U').charAt(0).toUpperCase() }}
        </div>
        <h1>欢迎使用 BugTracker</h1>
        <p class="welcome-user">当前用户：<strong>{{ username }}</strong></p>
        <div class="status-badge">
          <span class="status-dot"></span>
          已安全登录
        </div>

        <div class="actions">
          <RouterLink to="/bugs" class="primary-link">进入 Bug 列表</RouterLink>
          <RouterLink to="/bugs/new" class="ghost-link">新建 Bug</RouterLink>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.home-wrap {
  min-height: calc(100vh - 60px);
  background: #f5f6fa;
}

.home-main {
  min-height: calc(100vh - 60px);
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
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 10px;
}

.welcome-user {
  font-size: 15px;
  color: #6b7280;
  margin-bottom: 20px;
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
  margin-bottom: 28px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.primary-link,
.ghost-link {
  padding: 10px 22px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: opacity 0.2s;
}

.primary-link {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
}

.primary-link:hover {
  opacity: 0.9;
}

.ghost-link {
  border: 1px solid #d1d5db;
  color: #374151;
}

.ghost-link:hover {
  border-color: #7c3aed;
  color: #7c3aed;
}
</style>
