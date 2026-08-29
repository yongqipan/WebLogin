# WebLogin 登录系统

一个前后端分离的登录认证系统，支持数据库账号登录与 Casdoor 单点登录（SSO）两种方式。

## 功能特性

- 数据库账号登录：基于 MySQL 的用户名密码校验
- Casdoor 单点登录：对接 Casdoor 实现 OAuth2 授权码流程
- 双登录入口并存：普通登录与 Casdoor SSO 可同时使用
- 品牌化登录页：双栏布局，响应式适配移动端
- 使用官方 `casdoor-spring-boot-starter` 集成，无手写 OAuth 代码

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 / Java 17 / Maven |
| 前端 | Vue 3 / Vite / TypeScript / Node.js |
| 数据库 | MySQL 8 |
| 认证 | Casdoor（可选，仅 SSO 需要） |

## 项目结构

```
├── backend/                 # Spring Boot 后端（端口 8080）
│   ├── src/main/java/       # Java 源码
│   │   └── com/example/login/
│   │       ├── LoginController.java     # 数据库登录接口
│   │       ├── CasdoorController.java   # Casdoor SSO 接口
│   │       └── ApiResponse.java         # 统一响应封装
│   └── src/main/resources/
│       └── application.yml  # 后端配置
├── frontend/                # Vue 3 前端（端口 5173）
│   └── src/views/
│       ├── LoginView.vue    # 登录页
│       └── HomeView.vue     # 登录后首页
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

该脚本会创建 `login_demo` 库和 `user` 表，并写入两个测试账号（`admin` / `user1`，密码均为 `123456`）。

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

浏览器打开 `http://localhost:5173` 即可进入登录页。

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

- `POST /api/login`：数据库登录，请求体 `{"username": "...", "password": "..."}`，成功返回 `{"code":0,"data":{"username":"..."}}`
- `GET /api/auth/casdoor/login`：跳转到 Casdoor 登录页
- `GET /api/auth/casdoor/callback`：Casdoor 回调，成功后重定向回前端并携带登录用户

## 常见问题

### 数据库连接失败

确认 MySQL 已启动，且 `login_demo` 库和 `appuser` 用户已就绪。可先用 `schema/schema.sql` 初始化。

### SSO 登录失败

- 确认 Casdoor 服务可用，且 `casdoor.endpoint` 配置正确
- 确认 Casdoor 应用（`weblogin`）的 `redirect_uris` 中包含本系统的回调地址
- 确认 `casdoor.certificate` 与 Casdoor 应用的签名证书一致（RSA 证书的 PEM 内容）
