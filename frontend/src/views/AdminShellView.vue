<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const current = computed(() => {
  if (route.path.startsWith('/admin/products')) return 'products'
  return 'users'
})
</script>

<template>
  <div class="admin-shell">
    <div class="shell-inner">
      <div class="tabs">
        <RouterLink
          to="/admin/users"
          class="tab-link"
          :class="{ active: current === 'users' }"
        >
          用户管理
        </RouterLink>
        <RouterLink
          to="/admin/products"
          class="tab-link"
          :class="{ active: current === 'products' }"
        >
          产品管理
        </RouterLink>
      </div>
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  min-height: calc(100vh - 64px);
}

.shell-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 28px 24px 0;
}

.tabs {
  display: flex;
  gap: 6px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 20px;
}

.tab-link {
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  text-decoration: none;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: color 0.2s;
}

.tab-link:hover {
  color: #4f46e5;
}

.tab-link.active {
  color: #4f46e5;
  border-bottom-color: #4f46e5;
}
</style>
