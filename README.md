# BugTracker（WebLogin 扩展）

一个前后端分离的 Bug 追踪系统，基于 WebLogin 登录体系构建。支持数据库账号登录与 Casdoor 单点登录（SSO）两种方式，登录后可创建、查找、修改 Bug，并自动记录每次修改的字段级变更历史。

## 功能特性

- 数据库账号登录：基于 MySQL 的用户名密码校验
- Casdoor 单点登录：对接 Casdoor 实现 OAuth2 授权码流程
- 双登录入口并存：普通登录与 Casdoor SSO 可同时使用
- Bug 追踪：登录后可创建 Bug、按条件查找 Bug、查看详情、编辑保存
- 修改留痕：每次保存自动记录字段级变更（字段、旧值、新值、操作人、时间）
- 团队共享：所有已登录用户共享同一份 Bug 数据
- 后端 Session 鉴权：`/api/bugs/**` 接口必须登录后才能访问
- 使用官方 `casdoor-spring-boot-starter` 集成，无手写 OAuth 代码

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 / Java 17 / Maven / JdbcTemplate |
| 前端 | Vue 3 / Vite / TypeScript / Vue Router 4 |
| 数据库 | MySQL 8 |
| 认证 | Casdoor（可选，仅 SSO 需要） |

## 项目结构

```
├── backend/                 # Spring Boot 后端（端口 8080）
│   ├── src/main/java/       # Java 源码
│   │   └── com/example/login/
│   │       ├── LoginController.java     # 数据库登录 / 登出接口
│   │       ├── CasdoorController.java   # Casdoor SSO 接口
│   │       ├── ApiResponse.java         # 统一响应封装
│   │       ├── LoginInterceptor.java    # Session 登录拦截器
│   │       ├── WebConfig.java           # 拦截器注册
│   │       └── bug/
│   │           ├── Bug.java             # Bug 实体
│   │           ├── BugController.java   # Bug REST 接口
│   │           ├── BugService.java      # 业务逻辑（字段 diff、历史）
│   │           ├── BugRepository.java   # JdbcTemplate 数据访问
│   │           ├── BugEnums.java        # 状态 / 严重程度枚举
│   │           ├── BugHistory.java      # 变更历史模型
│   │           └── BugQuery.java        # 列表筛选参数
│   └── src/main/resources/
│       └── application.yml  # 后端配置
├── frontend/                # Vue 3 前端（端口 5173）
│   └── src/
│       ├── router/          # Vue Router 路由与登录守卫
│       ├── api.ts           # 统一 API 请求封装
│       ├── App.vue          # 全局导航 + 页面出口
│       └── views/
│           ├── LoginView.vue      # 登录页
│           ├── HomeView.vue       # 登录后欢迎页
│           ├── BugListView.vue    # Bug 列表 + 筛选
│           ├── BugFormView.vue    # Bug 新建 / 编辑表单
│           └── BugDetailView.vue  # Bug 详情 + 修改历史时间线
└── schema/
    └── schema.sql           # 数据库初始化脚本
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 22+（前端 `package.json` 要求 `^22.18.0 || >=24.12.0`）
- MySQL 8

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < schema/schema.sql
```

该脚本会创建 `login_demo` 库，以及 `user`（登录账号）、`bug`（Bug 记录）、`bug_history`（字段级变更历史）三张表，并写入两个测试账号（`admin` / `user1`，密码均为 `123456`）。脚本可重复执行（表结构 `IF NOT EXISTS`，种子数据 `INSERT IGNORE`）。

> 提示：Casdoor SSO 登录使用的 `user1`（组织 `testorg`）保存在 Casdoor 侧，不在本脚本中。

### 2. 编译并启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/login-backend-0.0.1-SNAPSHOT.jar
```

后端默认监听 `http://localhost:8080`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认监听 `http://localhost:5173`，开发服务器已将 `/api` 代理到后端 `8080` 端口。

### 4. 访问

浏览器打开 `http://localhost:5173` 即可进入登录页。登录成功后默认进入 Bug 列表（`/bugs`），可创建、搜索、编辑 Bug 并查看修改历史。

## 测试账号

| 登录方式 | 用户名 | 密码 |
|---------|--------|------|
| 数据库登录 | `admin` | `123456` |
| 数据库登录 | `user1` | `123456` |
| Casdoor SSO | `user1`（组织 `testorg`） | `654321` |

## 配置说明

### 后端（`backend/src/main/resources/application.yml`）

| 配置项 | 说明 |
|--------|------|
| `spring.datasource` | MySQL 连接（库 `login_demo`，用户 `appuser`） |
| `server.port` | 后端端口，默认 `8080` |
| `casdoor.endpoint` | Casdoor 服务地址 |
| `casdoor.client-id` / `client-secret` | Casdoor 应用凭据 |
| `casdoor.certificate` | Casdoor 应用的 JWT 签名证书（用于校验 SSO 返回的 Token） |
| `casdoor.organization-name` | Casdoor 组织名 |
| `casdoor.application-name` | Casdoor 应用名 |
| `app.frontend-origin` | 前端地址（用于 SSO 登录后跳转） |

### 前端（`frontend/vite.config.ts`）

开发服务器将 `/api` 前缀的请求代理到 `http://localhost:8080`，避免跨域问题。

## 登录接口

- `POST /api/login`：数据库登录，请求体 `{"username": "...", "password": "..."}`，成功返回 `{"code":0,"data":{"username":"..."}}` 并建立登录 Session
- `GET /api/auth/casdoor/login`：跳转到 Casdoor 登录页
- `GET /api/auth/casdoor/callback`：Casdoor 回调，成功后重定向回前端并携带登录用户

## Bug 接口

以下接口均需登录（携带 Session Cookie），未登录返回 HTTP 401。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/bugs` | Bug 列表，支持 `keyword` / `status` / `severity` / `assignee` / `page` / `size` 筛选，按创建时间倒序 |
| GET | `/api/bugs/{id}` | Bug 详情 |
| GET | `/api/bugs/{id}/history` | 字段级修改历史（按时间正序） |
| POST | `/api/bugs` | 创建 Bug，body：`{"title": "...", "description": "...", "severity": "一般", "assignee": "..."}` |
| PUT | `/api/bugs/{id}` | 编辑保存，body 同创建（含 `status`），仅记录实际变化的字段 |

`status` 取值：打开 / 处理中 / 已修复 / 已关闭；`severity` 取值：轻微 / 一般 / 严重 / 致命。`creator` / 操作人均取自登录 Session，前端不可伪造。

## 常见问题

### 数据库连接失败

确认 MySQL 已启动，且 `login_demo` 库和 `appuser` 用户已就绪。可先用 `schema/schema.sql` 初始化。

### SSO 登录失败

- 确认 Casdoor 服务可用，且 `casdoor.endpoint` 配置正确
- 确认 Casdoor 应用（`weblogin`）的 `redirect_uris` 中包含本系统的回调地址
- 确认 `casdoor.certificate` 与 Casdoor 应用的签名证书一致（RSA 证书的 PEM 内容）
