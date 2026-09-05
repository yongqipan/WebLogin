<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '../api'

const router = useRouter()
const username = ref<string>(localStorage.getItem('login_user') || '')

const idInput = ref('')
const searching = ref(false)
const notice = ref('')

function notify(text: string) {
  notice.value = text
  try {
    window.alert(text)
  } catch {
    // 部分内嵌预览环境会拦截 alert；页面内已同步展示 notice，保证提示可见
  }
}

async function handleSearch() {
  const raw = idInput.value.trim()
  if (!raw) {
    notify('请输入 Bug ID')
    return
  }
  if (!/^\d+$/.test(raw)) {
    notify('请输入有效的 Bug ID（正整数）')
    return
  }
  const id = Number(raw)
  notice.value = ''
  searching.value = true
  try {
    await get(`/api/bugs/${id}`)
    router.push(`/bugs/${id}`)
  } catch (e) {
    const msg = e instanceof Error ? e.message : '查找失败'
    if (msg.includes('记录不存在')) {
      notify(`没有找到bugID ${id}, 请检查！`)
    } else {
      notify(msg)
    }
  } finally {
    searching.value = false
  }
}
</script>

<template>
  <div class="home-wrap">
    <main class="home-main">
      <div class="home-card">
        <div class="greet">你好，{{ username }}</div>
        <h1>Bug 首页</h1>
        <p class="sub">输入 Bug ID 查找 Bug，找到后将跳转到 Bug 详情页</p>

        <form class="search-form" @submit.prevent="handleSearch">
          <input
            v-model="idInput"
            class="search-input"
            type="text"
            inputmode="numeric"
            autocomplete="off"
            placeholder="请输入 Bug ID"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" type="button" :disabled="searching" @click="handleSearch">
            {{ searching ? '查找中...' : '查找' }}
          </button>
        </form>

        <p v-if="notice" class="notice">{{ notice }}</p>
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
  align-items: flex-start;
  justify-content: center;
  padding: 48px 32px;
}

.home-card {
  width: 100%;
  max-width: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 41, 55, 0.08);
  padding: 36px 40px;
  text-align: center;
}

.greet {
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.home-card h1 {
  font-size: 26px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
}

.sub {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 24px;
}

.search-form {
  display: flex;
  gap: 10px;
  max-width: 420px;
  margin: 0 auto 8px;
}

.search-input {
  flex: 1;
  height: 46px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  padding: 0 16px;
  font-size: 14px;
  color: #374151;
  outline: none;
  background: #f9fafb;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.search-input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.12);
  background: #fff;
}

.search-btn {
  flex-shrink: 0;
  height: 46px;
  padding: 0 26px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.search-btn:hover {
  opacity: 0.92;
}

.search-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.notice {
  max-width: 420px;
  margin: 10px auto 0;
  padding: 10px 14px;
  border-radius: 8px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  font-size: 13px;
  text-align: center;
  word-break: break-word;
}
</style>
