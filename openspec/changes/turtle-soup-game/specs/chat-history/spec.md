## ADDED Requirements

### Requirement: 自动保存对话记录
系统 SHALL 在每次用户提问后自动保存对话记录。

#### Scenario: 提问后自动保存
- **WHEN** AI 完成对用户提问的判断
- **THEN** 系统将用户问题、AI 回答、当前时间自动存入 chat_log 表，关联到当前 game_session

### Requirement: 查询会话对话记录
系统 SHALL 允许用户查看某个游戏会话的全部对话记录。

#### Scenario: 查询对话记录
- **WHEN** 用户请求查看指定 game_session 的对话记录
- **THEN** 系统返回该会话的所有 chat_log 记录，按时间正序排列，每条包含用户问题和 AI 回答

#### Scenario: 无权限查看
- **WHEN** 用户请求查看非自己创建的 game_session 对话记录
- **THEN** 系统返回空列表或拒绝访问
