<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { get, post, put, type Product } from '../api'

const products = ref<Product[]>([])
const loading = ref(false)
const error = ref('')
const notice = ref('')

const dialogOpen = ref(false)
const isNew = ref(true)
const editingId = ref<number | null>(null)
const saving = ref(false)
const name = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const res = await get<Product[]>('/api/products')
    products.value = res.data
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function openNew() {
  isNew.value = true
  editingId.value = null
  name.value = ''
  error.value = ''
  dialogOpen.value = true
}

function openEdit(product: Product) {
  isNew.value = false
  editingId.value = product.id
  name.value = product.name
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
  const trimmed = name.value.trim()
  if (!trimmed) {
    error.value = '产品名不能为空'
    return
  }
  saving.value = true
  try {
    if (isNew.value) {
      await post('/api/admin/products', { name: trimmed })
      notify('产品添加成功')
    } else {
      await put(`/api/admin/products/${editingId.value}`, { name: trimmed })
      notify('产品保存成功')
    }
    dialogOpen.value = false
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  } finally {
    saving.value = false
  }
}

function fmtTime(v: string): string {
  return v.replace('T', ' ').slice(0, 19)
}

onMounted(load)
</script>

<template>
  <div class="product-page">
    <div class="page-header">
      <div>
        <h1>产品管理</h1>
        <p class="sub">动态添加、修改 Bug 归属的产品；产品名不能为空</p>
      </div>
      <button class="create-btn" @click="openNew">+ 新建产品</button>
    </div>

    <p v-if="notice" class="notice">{{ notice }}</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div class="table-card">
      <table v-if="products.length > 0">
        <thead>
          <tr>
            <th style="width: 80px">ID</th>
            <th>产品名</th>
            <th style="width: 190px">创建时间</th>
            <th style="width: 120px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td class="muted">{{ product.id }}</td>
            <td class="name">{{ product.name }}</td>
            <td class="muted">{{ fmtTime(product.createdAt) }}</td>
            <td>
              <button class="edit-btn" @click="openEdit(product)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else-if="loading" class="empty">加载中...</div>
      <div v-else-if="!error" class="empty">暂无产品</div>
    </div>

    <div v-if="dialogOpen" class="overlay" @click.self="closeDialog">
      <div class="dialog">
        <h2>{{ isNew ? '新建产品' : '编辑产品' }}</h2>

        <div class="field">
          <label for="p-name">产品名 *</label>
          <input
            id="p-name"
            v-model="name"
            type="text"
            class="text-input"
            maxlength="100"
            placeholder="请输入产品名称"
            @keyup.enter="handleSave"
          />
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
  </div>
</template>

<style scoped>
.product-page {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 0 28px;
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

.name {
  font-weight: 500;
}

.muted {
  color: #9ca3af;
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
