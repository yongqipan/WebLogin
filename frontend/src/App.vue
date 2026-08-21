<script setup lang="ts">
import { ref } from 'vue'
import LoginView from './views/LoginView.vue'
import HomeView from './views/HomeView.vue'

const isLoggedIn = ref<boolean>(!!localStorage.getItem('login_user'))

function handleLoggedIn() {
  isLoggedIn.value = true
}

function handleLogout() {
  localStorage.removeItem('login_user')
  isLoggedIn.value = false
}
</script>

<template>
  <LoginView v-if="!isLoggedIn" @logged-in="handleLoggedIn" />
  <HomeView v-else @logout="handleLogout" />
</template>
