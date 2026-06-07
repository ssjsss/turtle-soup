## MODIFIED Requirements

### Requirement: 随机获取题目
系统 SHALL 在随机抽题时仅返回审核通过（status='APPROVED'）的题目，可选按分类筛选。

#### Scenario: 成功随机获取（不指定分类）
- **WHEN** 用户请求随机题目，不传 category 参数
- **THEN** 系统从 status='APPROVED' 的题库中随机选择一道，返回标题、谜面、分类、难度和作者（不返回答案），并将该题目的 used_count 加 1

#### Scenario: 成功随机获取（指定分类）
- **WHEN** 用户请求随机题目，传入 category=脑洞
- **THEN** 系统从该分类且 status='APPROVED' 的题目中随机选择一道

#### Scenario: 指定分类无题目
- **WHEN** 用户请求随机题目，传入 category=恐怖，但该分类下无已审核题目
- **THEN** 系统返回提示"该分类暂无题目"

#### Scenario: 题库为空
- **WHEN** 题库中没有任何已审核题目
- **THEN** 系统返回提示"暂无题目"

## ADDED Requirements

### Requirement: 题目展示作者
系统 SHALL 在题目详情中返回作者署名。

#### Scenario: 展示作者
- **WHEN** 系统返回题目信息
- **THEN** 返回数据包含 author 字段
