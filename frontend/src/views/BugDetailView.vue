<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, FIELD_LABELS, type Bug, type BugHistoryItem } from '../api'

const route = useRoute()
const router = useRouter()

const bugId = Number(route.params.id)
const bug = ref<Bug | null>(null)
const history = ref<BugHistoryItem[]>([])
const loading = ref(true)
const error = ref('')

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

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [bugRes, historyRes] = await Promise.all([
      get<Bug>(`/api/bugs/${bugId}`),
      get<BugHistoryItem[]>(`/api/bugs/${bugId}/history`),
    ])
    bug.value = bugRes.data
    history.value = historyRes.data
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function fieldLabel(field: string): string {
  return FIELD_LABELS[field] || field
}

function fmtTime(v: string): string {
  return v.replace('T', ' ').slice(0, 19)
}

function displayValue(v: unknown): string {
  if (v === null || v === undefined || v === '') return '（空）'
  return String(v)
}

function isInitialHistory(item: BugHistoryItem): boolean {
  return item.changes.length > 0 && item.changes.every((c) => c.old === null)
}

onMounted(load)
</script>

<template>
  <div class="detail-page">
    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="bug" class="detail-wrap">
      <div class="page-header">
        <div class="title-area">
          <button class="back-btn" @click="router.push('/bugs')">← 返回列表</button>
          <h1>
            <span class="bug-id">#{{ bug.id }}</span>
            {{ bug.title }}
          </h1>
        </div>
        <RouterLink :to="`/bugs/${bug.id}/edit`" class="edit-btn">编辑 Bug</RouterLink>
      </div>

      <div class="meta-row">
        <span class="badge" :class="statusClass[bug.status] || ''">{{ bug.status }}</span>
        <span class="badge" :class="severityClass[bug.severity] || ''">{{ bug.severity }}</span>
        <span class="meta-text">创建人：{{ bug.creator }}</span>
        <span class="meta-text">指派处理人：{{ bug.assignee || '—' }}</span>
        <span class="meta-text muted">创建于 {{ fmtTime(bug.createdAt) }}</span>
        <span class="meta-text muted">更新于 {{ fmtTime(bug.updatedAt) }}</span>
      </div>

      <div class="card">
        <h2 class="card-title">详细描述</h2>
        <p class="desc" :class="{ 'desc-empty': !bug.description }">
          {{ bug.description || '暂无详细描述' }}
        </p>
      </div>

      <div class="card">
        <h2 class="card-title">修改历史</h2>
        <div v-if="history.length === 0" class="empty">暂无修改记录</div>

        <div v-else class="timeline">
          <div v-for="item in history" :key="item.id" class="timeline-item">
            <div class="timeline-marker"></div>
            <div class="timeline-body">
              <div class="timeline-head">
                <span class="operator">{{ item.operator }}</span>
                <span class="time">{{ fmtTime(item.operatedAt) }}</span>
                <span v-if="isInitialHistory(item)" class="action-tag">创建 Bug</span>
                <span v-else class="action-tag">编辑</span>
              </div>

              <ul class="change-list">
                <li v-for="(c, i) in item.changes" :key="i">
                  <span class="change-field">{{ fieldLabel(c.field) }}</span>
                  <span class="change-old">{{ displayValue(c.old) }}</span>
                  <span class="arrow">→</span>
                  <span class="change-new">{{ displayValue(c.new) }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="loading" class="empty-page">加载中...</div>
  </div>
</template>

<style scoped>
.detail-page {
  max-width: 860px;
  margin: 0 auto;
  padding: 28px 24px;
}

.error {
  color: #dc2626;
  font-size: 13px;
  margin-bottom: 12px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}

.title-area h1 {
  font-size: 22px;
  color: #1f2937;
  margin-top: 8px;
  word-break: break-word;
}

.bug-id {
  color: #9ca3af;
  font-weight: 600;
  margin-right: 6px;
}

.back-btn {
  padding: 6px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
}

.back-btn:hover {
  border-color: #7c3aed;
  color: #7c3aed;
}

.edit-btn {
  flex-shrink: 0;
  padding: 10px 22px;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: opacity 0.2s;
}

.edit-btn:hover {
  opacity: 0.9;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 18px;
  margin-bottom: 20px;
}

.meta-text {
  font-size: 13px;
  color: #374151;
}

.muted {
  color: #9ca3af;
}

.badge {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
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

.card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.desc {
  font-size: 14px;
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.desc-empty {
  color: #9ca3af;
}

.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: #e5e7eb;
}

.timeline-item {
  position: relative;
  padding-bottom: 22px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-marker {
  position: absolute;
  left: -20px;
  top: 5px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #c7d2fe;
}

.timeline-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.operator {
  font-size: 13px;
  font-weight: 600;
  color: #4f46e5;
}

.time {
  font-size: 12px;
  color: #9ca3af;
}

.action-tag {
  font-size: 11px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 10px;
  border-radius: 10px;
}

.change-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.change-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  flex-wrap: wrap;
}

.change-field {
  color: #6b7280;
  background: #f9fafb;
  border: 1px solid #f3f4f6;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
}

.change-old {
  color: #9ca3af;
  text-decoration: line-through;
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.change-new {
  color: #059669;
  font-weight: 500;
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  color: #d1d5db;
}

.empty,
.empty-page {
  text-align: center;
  padding: 30px;
  color: #9ca3af;
}
</style>
