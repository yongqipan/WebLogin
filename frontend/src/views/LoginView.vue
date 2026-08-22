<script setup lang="ts">
import { ref } from 'vue'

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
      emit('logged-in', data.data.username)
    } else {
      error.value = data.message
    }
  } catch {
    error.value = '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const emit = defineEmits<{ (e: 'logged-in', username: string): void }>()
</script>

<template>
  <div class="login-wrap">
    <form class="login-box" @submit.prevent="handleLogin">
      <h1>用户登录</h1>
      <input v-model="username" type="text" placeholder="用户名" autocomplete="username" />
      <input v-model="password" type="password" placeholder="密码" autocomplete="current-password" />
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? '登录中...' : '登 录' }}</button>
      <a class="casdoor-link" href="/casdoor">使用 Casdoor 单点登录</a>
    </form>
  </div>
</template>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
}
.login-box {
  width: 320px;
  padding: 40px 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.login-box h1 {
  text-align: center;
  font-size: 22px;
  color: #333;
  margin-bottom: 8px;
}
.login-box input {
  height: 42px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}
.login-box input:focus {
  border-color: #667eea;
}
.login-box button {
  height: 42px;
  border: none;
  border-radius: 8px;
  background: #667eea;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
}
.login-box button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.error {
  color: #e74c3c;
  font-size: 13px;
  text-align: center;
}
.casdoor-link {
  text-align: center;
  font-size: 13px;
  color: #667eea;
  text-decoration: none;
}
.casdoor-link:hover {
  text-decoration: underline;
}
</style>
