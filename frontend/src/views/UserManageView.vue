<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  get,
  post,
  put,
  del,
  ROLE_LABELS,
  USER_ROLES,
  type AdminUser,
  type UserRole,
} from '../api'

const users = ref<AdminUser[]>([])
const loading = ref(false)
const error = ref('')
const notice = ref('')

const dialogOpen = ref(false)
const isNew = ref(true)
const editingId = ref<number | null>(null)
const saving = ref(false)
const form = ref<{ username: string; password: string; role: UserRole }>({
  username: '',
  password: '',
  role: 'user',
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const res = await get<AdminUser[]>('/api/admin/users')
    users.value = res.data
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function openNew() {
  isNew.value = true
  editingId.value = null
  form.value = { username: '', password: '', role: 'user' }
  error.value = ''
  dialogOpen.value = true
}

function openEdit(user: AdminUser) {
  isNew.value = false
  editingId.value = user.id
  form.value = { username: user.username, password: '', role: user.role }
  error.value = ''
  dialogOpen.value = true
}

function closeDialog() {
  if (saving.value) return
  dialogOpen.value = false
}

function notify(text: string) {
  notice.value = text
  setTimeout(() => {
    if (notice.value === text) notice.value = ''
  }, 3000)
}

async function handleSave() {
  error.value = ''
  const username = form.value.username.trim()
  if (!username) {
    error.value = '用户名不能为空'
    return
  }
  const password = form.value.password
  if (isNew.value && !password) {
    error.value = '密码不能为空'
    return
  }

  saving.value = true
  try {
    if (isNew.value) {
      await post('/api/admin/users', {
        username,
        password,
        role: form.value.role,
      })
      notify('用户添加成功')
    } else {
      const payload: Record<string, string> = {
        username,
        role: form.value.role,
      }
      if (password) {
        payload.password = password
      }
      await put(`/api/admin/users/${editingId.value}`, payload)
      notify('用户保存成功')
    }
    dialogOpen.value = false
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  } finally {
    saving.value = false
  }
}

const deleteTarget = ref<AdminUser | null>(null)
const deleting = ref(false)

async function confirmDelete() {
  if (!deleteTarget.value || deleting.value) return
  deleting.value = true
  error.value = ''
  try {
    await del(`/api/admin/users/${deleteTarget.value.id}`)
    const removedName = deleteTarget.value.username
    deleteTarget.value = null
    notify(`用户 ${removedName} 已删除`)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '删除失败'
  } finally {
    deleting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="user-page">
    <div class="page-header">
      <div>
        <h1>用户管理</h1>
        <p class="sub">添加、修改用户并调整角色；本页面仅管理员可访问</p>
      </div>
      <button class="create-btn" @click="openNew">+ 新建用户</button>
    </div>

    <p v-if="notice" class="notice">{{ notice }}</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div class="table-card">
      <table v-if="users.length > 0">
        <thead>
          <tr>
            <th style="width: 80px">ID</th>
            <th>用户名</th>
            <th style="width: 160px">角色</th>
            <th style="width: 120px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td class="muted">{{ user.id }}</td>
            <td class="username">{{ user.username }}</td>
            <td>
              <span class="badge" :class="user.role === 'admin' ? 'role-admin' : 'role-user'">
                {{ ROLE_LABELS[user.role] }}
              </span>
            </td>
            <td>
              <button class="edit-btn" @click="openEdit(user)">编辑</button>
              <button class="delete-btn" @click="deleteTarget = user">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else-if="loading" class="empty">加载中...</div>
      <div v-else-if="!error" class="empty">暂无用户</div>
    </div>

    <div v-if="dialogOpen" class="overlay" @click.self="closeDialog">
      <div class="dialog">
        <h2>{{ isNew ? '新建用户' : '编辑用户' }}</h2>

        <div class="field">
          <label for="u-username">用户名 *</label>
          <input id="u-username" v-model="form.username" type="text" class="text-input" placeholder="登录用户名" />
        </div>

        <div class="field">
          <label for="u-password">密码{{ isNew ? ' *' : '' }}</label>
          <input
            id="u-password"
            v-model="form.password"
            type="password"
            class="text-input"
            autocomplete="new-password"
            :placeholder="isNew ? '设置登录密码' : '留空则不修改密码'"
          />
        </div>

        <div class="field">
          <label for="u-role">角色 *</label>
          <select id="u-role" v-model="form.role" class="text-input">
            <option v-for="r in USER_ROLES" :key="r" :value="r">{{ ROLE_LABELS[r] }}</option>
          </select>
        </div>

        <p v-if="error" class="error">{{ error }}</p>

        <div class="actions">
          <button class="primary-btn" :disabled="saving" @click="handleSave">
            {{ saving ? '保存中...' : '保存' }}
          </button>
          <button class="cancel-btn" :disabled="saving" @click="closeDialog">取消</button>
        </div>
      </div>
    </div>
    <div v-if="deleteTarget" class="overlay" @click.self="deleteTarget = null">
      <div class="dialog delete-dialog">
        <h2>删除用户</h2>
        <p class="delete-tip">
          确定要删除用户「{{ deleteTarget.username }}」吗？删除后不可恢复。
        </p>
        <p v-if="error" class="error">{{ error }}</p>
        <div class="actions">
          <button class="danger-btn" :disabled="deleting" @click="confirmDelete">
            {{ deleting ? '删除中...' : '确认删除' }}
          </button>
          <button class="cancel-btn" :disabled="deleting" @click="deleteTarget = null">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-page {
  max-width: 860px;
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
  padding: 10px 22px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.create-btn:hover {
  opacity: 0.9;
}

.notice {
  margin-bottom: 12px;
  padding: 10px 14px;
  border-radius: 8px;
  background: #ecfdf5;
  border: 1px solid #a7f3d0;
  color: #047857;
  font-size: 13px;
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

.username {
  font-weight: 500;
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

.role-admin {
  background: #ede9fe;
  color: #6d28d9;
}

.role-user {
  background: #f3f4f6;
  color: #6b7280;
}

.edit-btn {
  padding: 5px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #4f46e5;
  font-size: 13px;
  cursor: pointer;
}

.edit-btn:hover {
  border-color: #7c3aed;
  color: #7c3aed;
}

.delete-btn {
  margin-left: 8px;
  padding: 5px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #dc2626;
  font-size: 13px;
  cursor: pointer;
}

.delete-btn:hover {
  border-color: #dc2626;
  background: #fef2f2;
}

.delete-dialog {
  max-width: 340px;
}

.delete-tip {
  font-size: 14px;
  color: #374151;
  margin: 4px 0 12px;
  line-height: 1.6;
}

.danger-btn {
  flex: 1;
  height: 42px;
  border: none;
  border-radius: 10px;
  background: #dc2626;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.danger-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
}

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.dialog {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: 14px;
  padding: 28px;
}

.dialog h2 {
  font-size: 18px;
  color: #1f2937;
  margin-bottom: 18px;
}

.field {
  margin-bottom: 16px;
}

.field label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.text-input {
  width: 100%;
  height: 40px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  color: #374151;
  background: #fff;
  outline: none;
  font-family: inherit;
}

.text-input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.primary-btn {
  flex: 1;
  height: 42px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 0 20px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  background: #fff;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
}
</style>
