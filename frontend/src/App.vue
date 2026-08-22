<script setup lang="ts">
import { onMounted, ref } from 'vue'
import LoginView from './views/LoginView.vue'
import HomeView from './views/HomeView.vue'

const isLoggedIn = ref<boolean>(!!localStorage.getItem('login_user'))
const loginUser = ref<string>(localStorage.getItem('login_user') || '')

onMounted(() => {
  const params = new URLSearchParams(window.location.search)

  if (params.get('casdoor') === 'success') {
    const username = params.get('username') || 'casdoor-user'
    localStorage.setItem('login_user', username)
    loginUser.value = username
    isLoggedIn.value = true
    window.history.replaceState({}, '', window.location.pathname)
    return
  }

  if (params.get('casdoor') === 'error') {
    alert('Casdoor 登录失败，请重试')
    window.history.replaceState({}, '', window.location.pathname)
  }

  if (window.location.pathname === '/casdoor') {
    window.location.href = '/api/auth/casdoor/login'
  }
})

function handleLoggedIn(username: string) {
  localStorage.setItem('login_user', username)
  loginUser.value = username
  isLoggedIn.value = true
}

function handleLogout() {
  localStorage.removeItem('login_user')
  loginUser.value = ''
  isLoggedIn.value = false
}
</script>

<template>
  <LoginView v-if="!isLoggedIn" @logged-in="handleLoggedIn" />
  <HomeView v-else :username="loginUser" @logout="handleLogout" />
</template>
