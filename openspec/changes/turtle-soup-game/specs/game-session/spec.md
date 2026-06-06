## ADDED Requirements

### Requirement: 开始新游戏
系统 SHALL 允许用户指定一道题目开始新游戏会话。

#### Scenario: 成功开始游戏
- **WHEN** 已登录用户指定题目 ID 请求开始游戏
- **THEN** 系统创建 game_session 记录（状态为 IN_PROGRESS），返回 session_id 和谜面内容

#### Scenario: 题目不存在
- **WHEN** 用户指定的题目 ID 不存在
- **THEN** 系统返回 404

### Requirement: 向 AI 提问
系统 SHALL 允许用户在游戏会话中向 AI 提问。

#### Scenario: 成功提问并获取回答
- **WHEN** 用户在 IN_PROGRESS 状态的会话中输入问题
- **THEN** 系统调用 AI 判断接口，将问题和回答存入 chat_log，返回 AI 回答（是/否/无关紧要）

#### Scenario: 会话已结束
- **WHEN** 用户向已结束（SOLVED 或 ABANDONED）的会话提问
- **THEN** 系统返回错误"游戏已结束"

### Requirement: 猜真相
系统 SHALL 允许用户提交自己的推理答案来猜真相。

#### Scenario: 猜对真相
- **WHEN** 用户在 IN_PROGRESS 会话中提交的答案与标准答案语义匹配
- **THEN** 系统将 session 状态更新为 SOLVED，记录 end_time，返回"恭喜你猜对了！"及完整答案

#### Scenario: 猜错真相
- **WHEN** 用户提交的答案与标准答案不匹配
- **THEN** 系统返回提示"不对，再想想"，session 状态保持不变

### Requirement: 放弃游戏
系统 SHALL 允许用户放弃当前游戏。

#### Scenario: 放弃游戏
- **WHEN** 用户在 IN_PROGRESS 会话中选择放弃
- **THEN** 系统将 session 状态更新为 ABANDONED，记录 end_time，返回完整答案

### Requirement: 获取历史游戏记录
系统 SHALL 允许用户查看自己的历史游戏记录。

#### Scenario: 获取历史记录
- **WHEN** 已登录用户请求历史记录
- **THEN** 系统返回该用户所有 game_session 记录，按时间倒序，包含题目标题、状态、开始/结束时间
