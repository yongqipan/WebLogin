# User Instruction Memory

This file records user instructions, preferences, and teachings for reference in future interactions.

## Format

### User Instruction Entry
User instruction entries should follow this format:

[User Instruction Summary]
- Date: [YYYY-MM-DD]
- Context: [Mentioned scenario or time]
- Instructions:
  - [Content of user teaching or instruction, described line by line]

### Project Knowledge Entry
Entries discovered by the Agent during task execution should follow this format:

[Project Knowledge Summary]
- Date: [YYYY-MM-DD]
- Context: Discovered by Agent while performing [specific task description]
- Category: [Operations & Deployment|Build Methods|Testing Methods|Troubleshooting & Debugging|Workflow & Collaboration|Environment Configuration]
- Instructions:
  - [Specific knowledge points, described line by line]

## Deduplication Strategy
- Before adding a new entry, check for similar or identical instructions.
- If a duplicate is found, skip the new entry or merge it with the existing one.
- When merging, update the context or date information.
- This helps avoid redundant entries and keeps the memory file tidy.

## Entries

[User Instruction Summary]
- Date: 2026-09-07
- Context: Bug #1 修复指令中确立的 bug 修复流程角色操作规范，之后所有 bug 均按此执行
- Instructions:
  - 开发工程师（修复 bug、以 admin 身份操作）在把 bug 状态改为"已修复"之前，必须先调用 bug 进度更新（POST /api/bugs/{id}/progress）记录：bug 的根本原因、解决方案、修改与新增的文件列表
  - 测试工程师（验收 bug、以 user1 身份操作）在验收通过后、把 bug 状态改为"已关闭"之前，必须先更新进度记录：写明验证步骤与实际结果
  - 该"先更新进展、再流转状态"的顺序适用于今后所有 bug 修复流程
