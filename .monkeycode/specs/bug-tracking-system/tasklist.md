# Tasklist: bug-tracking-system

Feature Name: bug-tracking-system
Updated: 2026-09-03

## 后端

- [x] 1.1 数据库：schema.sql 追加 bug / bug_history 表，并执行到 MySQL
- [x] 1.2 登录 Session 化：LoginController 成功写入 session `loginUser`；CasdoorController 统一 `loginUser`
- [x] 1.3 实现 BugRepository / BugService / BugController（列表筛选、详情、创建、编辑 diff、历史）
- [x] 1.4 实现 LoginInterceptor + WebConfig 拦截 `/api/bugs/**`
- [x] 1.5 后端端到端冒烟验证（curl：未登录 401、登录后增删改查、历史与无变化保存）

## 前端

- [x] 2.1 引入 vue-router@4：router/index.ts、main.ts 挂载、App.vue 改 router-view
- [x] 2.2 API 封装（fetch + code 判断 + 401 跳登录）
- [x] 2.3 BugListView（列表 + 筛选 + 新建入口）
- [x] 2.4 BugFormView（新建/编辑复用表单）
- [x] 2.5 BugDetailView（详情 + 字段级修改历史时间线）
- [x] 2.6 HomeView 加入口、登录跳转逻辑调整
- [x] 2.7 前端 type-check 与端到端验证

## 收尾

- [ ] 3.1 更新 tasklist、同步项目文档、提交推送