<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { get, type Bug } from '../api'
import { SEVERITY_LIST, STATUS_LIST } from '../api'

const bugs = ref<Bug[]>([])
const total = ref(0)
const loading = ref(false)
const error = ref('')

const filters = ref({
  keyword: '',
  status: '',
  severity: '',
  assignee: '',
})
const page = ref(1)
const size = 10

const severityClass: Record<string, string> = {
  致命: 'sev-fatal',
  严重: 'sev-serious',
  一般: 'sev-normal',
  轻微: 'sev-minor',
}

const statusClass: Record<string, string> = {
  打开: 'st-open',
  处理中: 'st-progress',
  已修复: 'st-fixed',
  已关闭: 'st-closed',
}

const typeClass: Record<string, string> = {
  缺陷: 'type-defect',
  新功能: 'type-feature',
}

async function loadBugs() {
  loading.value = true
  error.value = ''
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('size', String(size))
    if (filters.value.keyword.trim()) params.set('keyword', filters.value.keyword.trim())
    if (filters.value.status) params.set('status', filters.value.status)
    if (filters.value.severity) params.set('severity', filters.value.severity)
    if (filters.value.assignee.trim()) params.set('assignee', filters.value.assignee.trim())

    const res = await get<{ total: number; list: Bug[] }>(`/api/bugs?${params.toString()}`)
    bugs.value = res.data.list
    total.value = res.data.total
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadBugs()
}

function resetFilters() {
  filters.value = { keyword: '', status: '', severity: '', assignee: '' }
  page.value = 1
  loadBugs()
}

function prevPage() {
  if (page.value > 1) {
    page.value -= 1
    loadBugs()
  }
}

function nextPage() {
  if (page.value * size < total.value) {
    page.value += 1
    loadBugs()
  }
}

function fmtTime(v: string): string {
  return v.replace('T', ' ').slice(0, 16)
}

onMounted(loadBugs)
</script>

<template>
  <div class="list-page">
    <div class="page-header">
      <div>
        <h1>Bug 列表</h1>
        <p class="sub">共 {{ total }} 条记录</p>
      </div>
      <RouterLink to="/bugs/new" class="create-btn">+ 新建 Bug</RouterLink>
    </div>

    <div class="filter-bar">
      <input
        v-model="filters.keyword"
        class="filter-input grow"
        type="text"
        placeholder="搜索标题或描述关键词"
        @keyup.enter="search"
      />
      <select v-model="filters.status" class="filter-select">
        <option value="">全部状态</option>
        <option v-for="s in STATUS_LIST" :key="s" :value="s">{{ s }}</option>
      </select>
      <select v-model="filters.severity" class="filter-select">
        <option value="">全部严重程度</option>
        <option v-for="s in SEVERITY_LIST" :key="s" :value="s">{{ s }}</option>
      </select>
      <input
        v-model="filters.assignee"
        class="filter-input assignee"
        type="text"
        placeholder="指派处理人"
        @keyup.enter="search"
      />
      <button class="search-btn" @click="search">查询</button>
      <button class="reset-btn" @click="resetFilters">重置</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="table-card">
      <table v-if="bugs.length > 0">
        <thead>
          <tr>
            <th style="width: 60px">ID</th>
            <th>标题</th>
            <th style="width: 90px">类型</th>
            <th style="width: 100px">状态</th>
            <th style="width: 100px">严重程度</th>
            <th style="width: 120px">指派处理人</th>
            <th style="width: 150px">创建时间</th>
            <th style="width: 80px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="bug in bugs" :key="bug.id">
            <td class="id-cell">#{{ bug.id }}</td>
            <td>
              <RouterLink :to="`/bugs/${bug.id}`" class="title-link">{{ bug.title }}</RouterLink>
            </td>
            <td>
              <span class="badge" :class="typeClass[bug.type] || ''">{{ bug.type }}</span>
            </td>
            <td>
              <span class="badge" :class="statusClass[bug.status] || ''">{{ bug.status }}</span>
            </td>
            <td>
              <span class="badge" :class="severityClass[bug.severity] || ''">{{ bug.severity }}</span>
            </td>
            <td>{{ bug.assignee || '—' }}</td>
            <td class="muted">{{ fmtTime(bug.createdAt) }}</td>
            <td>
              <RouterLink :to="`/bugs/${bug.id}`" class="view-link">详情</RouterLink>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading && !error" class="empty">
        <p>暂无 Bug 记录</p>
        <RouterLink to="/bugs/new" class="empty-link">创建第一条 Bug</RouterLink>
      </div>
      <div v-else-if="loading" class="empty">加载中...</div>
    </div>

    <div v-if="total > size" class="pagination">
      <button class="page-btn" :disabled="page <= 1" @click="prevPage">上一页</button>
      <span class="page-info">第 {{ page }} / {{ Math.ceil(total / size) }} 页</span>
      <button class="page-btn" :disabled="page * size >= total" @click="nextPage">下一页</button>
    </div>
  </div>
</template>

<style scoped>
.list-page {
  max-width: 1080px;
  margin: 0 auto;
  padding: 28px 24px;
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #1f2937;
}

.sub {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 2px;
}

.create-btn {
  display: inline-block;
  padding: 10px 22px;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: opacity 0.2s;
}

.create-btn:hover {
  opacity: 0.9;
}

.filter-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 16px;
}

.filter-input,
.filter-select {
  height: 38px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 13px;
  background: #fff;
  color: #374151;
  outline: none;
}

.filter-input:focus,
.filter-select:focus {
  border-color: #7c3aed;
}

.grow {
  flex: 1;
}

.assignee {
  width: 130px;
}

.search-btn {
  height: 38px;
  padding: 0 20px;
  border: none;
  border-radius: 8px;
  background: #4f46e5;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
}

.reset-btn {
  height: 38px;
  padding: 0 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
}

.error {
  color: #dc2626;
  font-size: 13px;
  margin-bottom: 12px;
}

.table-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  padding: 12px 16px;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  white-space: nowrap;
}

td {
  padding: 13px 16px;
  font-size: 13.5px;
  color: #374151;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

tbody tr:last-child td {
  border-bottom: none;
}

tbody tr:hover {
  background: #faf9ff;
}

.id-cell {
  color: #9ca3af;
  font-size: 12px;
}

.title-link {
  color: #1f2937;
  font-weight: 500;
  text-decoration: none;
}

.title-link:hover {
  color: #7c3aed;
}

.muted {
  color: #9ca3af;
  font-size: 12.5px;
  white-space: nowrap;
}

.view-link {
  color: #4f46e5;
  text-decoration: none;
  font-size: 13px;
}

.view-link:hover {
  text-decoration: underline;
}

.badge {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.st-open {
  background: #eff6ff;
  color: #2563eb;
}

.st-progress {
  background: #fef3c7;
  color: #b45309;
}

.st-fixed {
  background: #ecfdf5;
  color: #059669;
}

.st-closed {
  background: #f3f4f6;
  color: #6b7280;
}

.sev-fatal {
  background: #fee2e2;
  color: #b91c1c;
}

.sev-serious {
  background: #fef3c7;
  color: #b45309;
}

.sev-normal {
  background: #eff6ff;
  color: #2563eb;
}

.sev-minor {
  background: #f3f4f6;
  color: #6b7280;
}

.type-defect {
  background: #fee2e2;
  color: #b91c1c;
}

.type-feature {
  background: #eff6ff;
  color: #2563eb;
}

.empty {
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty p {
  margin-bottom: 12px;
}

.empty-link {
  color: #4f46e5;
  font-size: 13px;
  text-decoration: none;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 14px;
  margin-top: 16px;
}

.page-btn {
  height: 34px;
  padding: 0 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 13px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 13px;
  color: #6b7280;
}
</style>
