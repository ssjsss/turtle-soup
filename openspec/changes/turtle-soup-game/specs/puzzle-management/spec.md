## ADDED Requirements

### Requirement: 随机获取题目
系统 SHALL 支持用户随机获取一道海龟汤谜题。

#### Scenario: 成功随机获取
- **WHEN** 用户请求随机题目
- **THEN** 系统从题库中随机选择一道，返回标题和谜面（不返回答案），并将该题目的 used_count 加 1

#### Scenario: 题库为空
- **WHEN** 题库中没有任何题目
- **THEN** 系统返回提示"暂无题目"

### Requirement: 获取题目详情
系统 SHALL 支持按 ID 获取题目详情。

#### Scenario: 获取成功
- **WHEN** 用户请求指定 ID 的题目
- **THEN** 系统返回标题和谜面内容（不返回答案）

#### Scenario: 题目不存在
- **WHEN** 题目 ID 不存在
- **THEN** 系统返回 404

### Requirement: 管理员新增题目
系统 SHALL 支持管理员向题库添加新题目。

#### Scenario: 新增题目
- **WHEN** 管理员提交标题、谜面、答案和难度
- **THEN** 系统创建新题目记录并返回成功
