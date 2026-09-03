<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const username = ref<string>(localStorage.getItem('login_user') || '')

watch(
  () => route.fullPath,
  () => {
    const current = localStorage.getItem('login_user')
    if (current) {
      username.value = current
    }
  },
  { immediate: true },
)

const showNav = computed(() => route.path !== '/login')

function isActive(path: string): boolean {
  return route.path === path || (path !== '/home' && route.path.startsWith(path))
}

async function handleLogout() {
  try {
    await fetch('/api/logout')
  } catch {
    // 即使后端失效失败也继续本地登出
  }
  localStorage.removeItem('login_user')
  username.value = ''
  router.push('/login')
}
</script>

<template>
  <nav v-if="showNav" class="top-nav">
    <div class="nav-brand">
      <img src="./assets/logo.svg" alt="logo" />
      <span>BugTracker</span>
    </div>

    <div class="nav-links">
      <RouterLink to="/bugs" class="nav-link" :class="{ active: isActive('/bugs') }">
        Bug 列表
      </RouterLink>
      <RouterLink to="/bugs/new" class="nav-link" :class="{ active: route.path === '/bugs/new' }">
        新建 Bug
      </RouterLink>
    </div>

    <div class="nav-user">
      <span class="user-chip">
        <span class="user-dot"></span>
        {{ username }}
      </span>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </nav>

  <main :class="{ 'no-nav': !showNav }">
    <RouterView />
  </main>
</template>

<style scoped>
.top-nav {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  padding: 0 28px;
  gap: 24px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 700;
  color: #1f2937;
}

.nav-brand img {
  width: 30px;
  height: 30px;
  border-radius: 7px;
}

.nav-links {
  display: flex;
  gap: 6px;
  flex: 1;
}

.nav-link {
  padding: 7px 16px;
  border-radius: 8px;
  font-size: 14px;
  color: #6b7280;
  text-decoration: none;
  transition: background 0.15s, color 0.15s;
}

.nav-link:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.nav-link.active {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 20px;
  background: #f3f4f6;
  color: #374151;
  font-size: 13px;
  font-weight: 500;
}

.user-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
}

.logout-btn {
  padding: 7px 16px;
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
</style>
