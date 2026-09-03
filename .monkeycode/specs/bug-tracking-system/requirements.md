# Requirements Document

## Introduction

在现有 WebLogin 登录系统的基础上，扩展一个 Bug 追踪子系统。用户完成登录后进入 Bug 管理界面，可以创建 Bug、查找 Bug、修改并保存 Bug，系统保留每次修改的字段级变更记录，便于追溯问题演化过程。

Bug 数据对所有登录用户共享：任意已登录用户均可查看和编辑全部 Bug，不做按创建人的数据隔离，也不区分管理员与普通用户角色。

## Glossary

- **Bug**：一条待处理或已处理的问题记录，包含标题、描述、状态、严重程度、指派处理人、创建人、创建时间、更新时间。
- **字段级变更记录**：Bug 每次保存时，记录本次操作中发生变化的字段从旧值到新值的明细，不含未变化字段。
- **创建人 / 指派处理人**：系统中的已登录用户名。
- **状态**：Bug 生命周期标识，取值为 打开 / 处理中 / 已修复 / 已关闭。
- **严重程度**：Bug 影响级别，取值为 轻微 / 一般 / 严重 / 致命。

## Requirements

### Requirement 1: 登录鉴权前置

**User Story:** AS 用户, I want 必须先登录才能使用 Bug 功能, so that Bug 数据只对合法用户开放。

#### Acceptance Criteria

1. WHEN 未登录用户访问 Bug 管理页面或 Bug 相关 API，系统 SHALL 拒绝访问并引导至登录页。
2. WHEN 用户通过数据库账号或 Casdoor SSO 完成登录，系统 SHALL 允许该用户进入 Bug 追踪界面。
3. WHEN 已登录用户操作 Bug 相关 API，系统 SHALL 识别当前用户名并将其记录为操作人。

### Requirement 2: 创建 Bug

**User Story:** AS 用户, I want 创建一条新的 Bug 记录, so that 问题可以被登记并进入处理流程。

#### Acceptance Criteria

1. WHEN 已登录用户提交标题、描述、严重程度、指派处理人的创建请求，系统 SHALL 生成一条状态为"打开"的 Bug。
2. WHEN 创建请求缺少标题，系统 SHALL 拒绝创建并提示"标题不能为空"。
3. WHEN 创建成功，系统 SHALL 返回新 Bug 的完整信息，包括 ID、创建人和创建时间。
4. WHEN Bug 创建成功，系统 SHALL 自动写入一条记录创建行为的变更历史。

### Requirement 3: 查找 Bug

**User Story:** AS 用户, I want 按条件查找 Bug, so that 可以快速定位目标问题。

#### Acceptance Criteria

1. WHEN 已登录用户请求 Bug 列表，系统 SHALL 按创建时间倒序返回全部 Bug。
2. WHEN 用户输入关键词，系统 SHALL 按标题或描述模糊匹配 Bug。
3. WHEN 用户选择状态或严重程度条件，系统 SHALL 过滤出匹配的 Bug。
4. WHEN 用户指定指派处理人，系统 SHALL 过滤出指派给该用户的 Bug。
5. WHEN 存在多个筛选条件，系统 SHALL 以 AND 关系组合过滤。

### Requirement 4: 查看 Bug 详情与修改历史

**User Story:** AS 用户, I want 查看 Bug 详情及其全部修改记录, so that 了解问题当前状态和演化过程。

#### Acceptance Criteria

1. WHEN 用户打开某条 Bug，系统 SHALL 展示该 Bug 全部字段及当前值。
2. WHEN 该 Bug 存在变更历史，系统 SHALL 按时间从旧到新展示每次修改，内容包括操作人、操作时间以及每个变更字段的旧值和新值。
3. WHEN 未发生变更记录的字段，系统 SHALL 不将其列入修改历史。

### Requirement 5: 修改并保存 Bug

**User Story:** AS 用户, I want 编辑 Bug 字段并保存, so that Bug 状态与信息保持最新。

#### Acceptance Criteria

1. WHEN 已登录用户编辑标题、描述、状态、严重程度、指派处理人并保存，系统 SHALL 更新对应字段并刷新更新时间。
2. WHEN 保存时没有任何字段发生变化，系统 SHALL 忽略该次保存，不产生新的变更历史。
3. WHEN 保存导致一个或多个字段变化，系统 SHALL 为每个变化的字段记录一条变更明细（字段名、旧值、新值、操作人、操作时间）。
4. WHEN 保存请求缺少标题，系统 SHALL 拒绝保存并提示"标题不能为空"。
5. WHEN 用户提交的状态或严重程度取值非法，系统 SHALL 拒绝保存并返回参数错误。

### Requirement 6: 数据共享与一致性

**User Story:** AS 用户, I want 所有登录用户看到一致的 Bug 数据, so that 团队基于同一份数据协作。

#### Acceptance Criteria

1. WHEN 任一用户创建或修改 Bug，系统 SHALL 立即对所有已登录用户可见。
2. WHEN 并发修改同一条 Bug，系统 SHALL 以最后一次成功保存为准，且每次保存的变更历史不被丢失。
