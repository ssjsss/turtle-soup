## ADDED Requirements

### Requirement: 查看我的投稿
系统 SHALL 允许用户查看自己的投稿记录及审核状态。

#### Scenario: 查看投稿列表
- **WHEN** 已登录用户请求我的投稿
- **THEN** 系统返回该用户的所有投稿，按创建时间倒序，包含题目标题、状态、审核原因（如有）

#### Scenario: 审核状态展示
- **WHEN** 投稿状态为 PENDING
- **THEN** 前端显示"审核中"
- **WHEN** 投稿状态为 APPROVED
- **THEN** 前端显示"已通过"
- **WHEN** 投稿状态为 REJECTED
- **THEN** 前端显示"已拒绝"及拒绝原因
