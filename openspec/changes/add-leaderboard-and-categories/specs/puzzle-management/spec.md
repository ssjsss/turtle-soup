## MODIFIED Requirements

### Requirement: 随机获取题目
系统 SHALL 支持用户随机获取一道海龟汤谜题，可选按分类筛选。

#### Scenario: 成功随机获取（不指定分类）
- **WHEN** 用户请求随机题目，不传 category 参数
- **THEN** 系统从题库中随机选择一道，返回标题、谜面、分类和难度（不返回答案），并将该题目的 used_count 加 1

#### Scenario: 成功随机获取（指定分类）
- **WHEN** 用户请求随机题目，传入 category=脑洞
- **THEN** 系统从该分类的题库中随机选择一道，返回标题、谜面、分类和难度

#### Scenario: 指定分类无题目
- **WHEN** 用户请求随机题目，传入 category=恐怖，但该分类下无题目
- **THEN** 系统返回提示"该分类暂无题目"

#### Scenario: 题库为空
- **WHEN** 题库中没有任何题目
- **THEN** 系统返回提示"暂无题目"

### Requirement: 获取题目详情
系统 SHALL 支持按 ID 获取题目详情，返回中包含分类信息。

#### Scenario: 获取成功
- **WHEN** 用户请求指定 ID 的题目
- **THEN** 系统返回标题、谜面、分类和难度内容（不返回答案）

#### Scenario: 题目不存在
- **WHEN** 题目 ID 不存在
- **THEN** 系统返回 404

## ADDED Requirements

### Requirement: 题目包含分类
puzzle 表 SHALL 包含 category 字段，取值限制为"脑洞"、"恐怖"、"搞笑"三者之一。

#### Scenario: 题目展示分类
- **WHEN** 系统返回题目信息
- **THEN** 返回数据包含 category 字段
