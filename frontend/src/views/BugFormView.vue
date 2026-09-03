<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post, put, SEVERITY_LIST, STATUS_LIST, type Bug } from '../api'

const route = useRoute()
const router = useRouter()

const editingId = route.name === 'bug-edit' ? Number(route.params.id) : null
const isEdit = editingId !== null

const loading = ref(false)
const saving = ref(false)
const error = ref('')

const form = ref({
  title: '',
  description: '',
  status: '打开',
  severity: '一般',
  assignee: '',
})

const hasUnsaved = ref(false)

onMounted(async () => {
  if (!isEdit) return
  loading.value = true
  try {
    const res = await get<Bug>(`/api/bugs/${editingId}`)
    const bug = res.data
    form.value = {
      title: bug.title,
      description: bug.description || '',
      status: bug.status,
      severity: bug.severity,
      assignee: bug.assignee || '',
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
    router.replace('/bugs')
  } finally {
    loading.value = false
  }
})

async function handleSubmit() {
  error.value = ''
  if (!form.value.title.trim()) {
    error.value = '标题不能为空'
    return
  }
  saving.value = true
  try {
    const payload = {
      title: form.value.title.trim(),
      description: form.value.description.trim(),
      status: form.value.status,
      severity: form.value.severity,
      assignee: form.value.assignee.trim(),
    }
    if (isEdit) {
      await put(`/api/bugs/${editingId}`, payload)
      hasUnsaved.value = false
      router.push(`/bugs/${editingId}`)
    } else {
      const res = await post<Bug>('/api/bugs', payload)
      hasUnsaved.value = false
      router.push(`/bugs/${res.data.id}`)
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  } finally {
    saving.value = false
  }
}

function handleBack() {
  if (isEdit && hasUnsaved.value) {
    const ok = window.confirm('有未保存的修改，确定离开吗？')
    if (!ok) return
  }
  router.push(isEdit ? `/bugs/${editingId}` : '/bugs')
}
</script>

<template>
  <div class="form-page">
    <div class="page-header">
      <div>
        <h1>{{ isEdit ? '编辑 Bug' : '新建 Bug' }}</h1>
        <p class="sub">带 * 为必填项</p>
      </div>
      <button class="back-btn" @click="handleBack">返回</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="form-card">
      <div v-if="loading" class="empty">加载中...</div>

      <form v-else @submit.prevent="handleSubmit">
        <div class="field">
          <label for="title">标题 *</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            class="text-input"
            placeholder="简要描述问题"
            maxlength="200"
            @input="hasUnsaved = true"
          />
        </div>

        <div class="field">
          <label for="description">详细描述</label>
          <textarea
            id="description"
            v-model="form.description"
            class="textarea"
            rows="6"
            placeholder="复现步骤、期望结果、实际结果等"
            @input="hasUnsaved = true"
          ></textarea>
        </div>

        <div class="field-row">
          <div class="field">
            <label for="status">状态</label>
            <select id="status" v-model="form.status" class="select-input" @change="hasUnsaved = true">
              <option v-for="s in STATUS_LIST" :key="s" :value="s">{{ s }}</option>
            </select>
          </div>
          <div class="field">
            <label for="severity">严重程度</label>
            <select id="severity" v-model="form.severity" class="select-input" @change="hasUnsaved = true">
              <option v-for="s in SEVERITY_LIST" :key="s" :value="s">{{ s }}</option>
            </select>
          </div>
        </div>

        <div class="field">
          <label for="assignee">指派处理人</label>
          <input
            id="assignee"
            v-model="form.assignee"
            type="text"
            class="text-input"
            placeholder="填写处理该 Bug 的用户名（可留空）"
            @input="hasUnsaved = true"
          />
        </div>

        <div class="actions">
          <button class="primary-btn" type="submit" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
          <button type="button" class="cancel-btn" @click="handleBack">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.form-page {
  max-width: 760px;
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

.back-btn {
  padding: 8px 18px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  font-size: 13px;
  cursor: pointer;
}

.back-btn:hover {
  border-color: #7c3aed;
  color: #7c3aed;
}

.error {
  color: #dc2626;
  font-size: 13px;
  margin-bottom: 12px;
}

.form-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 28px;
}

.field {
  margin-bottom: 20px;
}

.field-row {
  display: flex;
  gap: 16px;
}

.field-row .field {
  flex: 1;
}

.field label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.text-input,
.textarea,
.select-input {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: #374151;
  background: #fff;
  outline: none;
  font-family: inherit;
}

.text-input:focus,
.textarea:focus,
.select-input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
}

.textarea {
  resize: vertical;
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.primary-btn {
  padding: 10px 30px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.primary-btn:hover {
  opacity: 0.9;
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 10px 24px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  background: #fff;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
}
</style>
