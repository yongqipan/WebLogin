<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  try {
    const res = await fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value }),
    })
    const data = await res.json()
    if (data.code === 0) {
      localStorage.setItem('login_user', data.data.username)
      localStorage.setItem('login_role', data.data.role || 'user')
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } else {
      error.value = data.message
    }
  } catch {
    error.value = '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="brand-panel">
      <div class="brand-content">
        <div class="brand-logo">
          <img src="../assets/logo.svg" alt="logo" />
        </div>
        <h1 class="brand-name">BugTracker</h1>
        <p class="brand-slogan">登录以管理 Bug 追踪与处理进度</p>

        <ul class="feature-list">
          <li>
            <span class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="4" y="11" width="16" height="9" rx="2" />
                <path d="M8 11V7a4 4 0 0 1 8 0v4" />
              </svg>
            </span>
            <div>
              <strong>账户安全</strong>
              <p>登录鉴权与访问控制</p>
            </div>
          </li>
          <li>
            <span class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 21v-2a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6v2" />
              </svg>
            </span>
            <div>
              <strong>全程留痕</strong>
              <p>每次修改记录字段级变更历史</p>
            </div>
          </li>
          <li>
            <span class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="12" rx="2" />
                <path d="M8 20h8M12 16v4" />
              </svg>
            </span>
            <div>
              <strong>团队协作</strong>
              <p>Bug 共享，指派处理人与状态跟进</p>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <div class="form-panel">
      <div class="form-box">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>登录您的账号以继续</p>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="field">
            <label for="username">用户名</label>
            <div class="input-wrap">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 21v-2a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6v2" />
              </svg>
              <input id="username" v-model="username" type="text" placeholder="请输入用户名" autocomplete="username" />
            </div>
          </div>

          <div class="field">
            <label for="password">密码</label>
            <div class="input-wrap">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="4" y="11" width="16" height="9" rx="2" />
                <path d="M8 11V7a4 4 0 0 1 8 0v4" />
              </svg>
              <input id="password" v-model="password" type="password" placeholder="请输入密码" autocomplete="current-password" />
            </div>
          </div>

          <p v-if="error" class="error">{{ error }}</p>

          <button class="primary-btn" type="submit" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </form>

        <div class="divider"><span>或</span></div>

        <a class="sso-btn" href="/casdoor">
          <svg class="sso-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="3" />
            <path d="M4 12a8 8 0 0 1 14.9-4M20 12a8 8 0 0 1-14.9 4" />
          </svg>
          使用 Casdoor 单点登录
        </a>

        <p class="copyright">© 2026 BugTracker. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  background: #fff;
}

.brand-panel {
  flex: 1 1 55%;
  background: linear-gradient(150deg, #4f46e5 0%, #7c3aed 55%, #9333ea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  position: relative;
  overflow: hidden;
}

.brand-panel::before,
.brand-panel::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}

.brand-panel::before {
  width: 420px;
  height: 420px;
  top: -140px;
  right: -120px;
}

.brand-panel::after {
  width: 320px;
  height: 320px;
  bottom: -110px;
  left: -90px;
}

.brand-content {
  max-width: 440px;
  color: #fff;
  position: relative;
  z-index: 1;
}

.brand-logo {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28px;
}

.brand-logo img {
  width: 40px;
  height: 40px;
}

.brand-name {
  font-size: 40px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 10px;
}

.brand-slogan {
  font-size: 17px;
  opacity: 0.9;
  margin-bottom: 48px;
}

.feature-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.feature-list li {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.feature-icon {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-icon svg {
  width: 22px;
  height: 22px;
}

.feature-list strong {
  font-size: 16px;
  display: block;
  margin-bottom: 2px;
}

.feature-list p {
  font-size: 13px;
  opacity: 0.8;
  margin: 0;
}

.form-panel {
  flex: 1 1 45%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
}

.form-box {
  width: 100%;
  max-width: 380px;
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
}

.form-header p {
  font-size: 14px;
  color: #6b7280;
}

.field {
  margin-bottom: 20px;
}

.field label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.input-wrap {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #9ca3af;
  pointer-events: none;
}

.input-wrap input {
  width: 100%;
  height: 46px;
  padding: 0 14px 0 42px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #f9fafb;
}

.input-wrap input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12);
  background: #fff;
}

.primary-btn {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
  margin-top: 8px;
}

.primary-btn:hover {
  opacity: 0.92;
}

.primary-btn:active {
  transform: scale(0.99);
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #dc2626;
  font-size: 13px;
  margin: -8px 0 12px;
}

.divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 28px 0;
  color: #9ca3af;
  font-size: 13px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e5e7eb;
}

.sso-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  height: 46px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  background: #fff;
  transition: border-color 0.2s, background 0.2s;
}

.sso-btn:hover {
  border-color: #7c3aed;
  background: #faf5ff;
}

.sso-icon {
  width: 18px;
  height: 18px;
  color: #7c3aed;
}

.copyright {
  margin-top: 40px;
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
}

@media (max-width: 860px) {
  .brand-panel {
    display: none;
  }

  .form-panel {
    flex: 1 1 100%;
  }
}
</style>
