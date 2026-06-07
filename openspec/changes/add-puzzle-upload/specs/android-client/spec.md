## MODIFIED Requirements

### Requirement: 主页面与抽题
客户端 SHALL 在题目卡片上展示作者署名。

#### Scenario: 随机抽题并开始游戏
- **WHEN** 用户点击"随机抽题"按钮
- **THEN** 客户端调用随机抽题接口获取题目，展示谜面、分类、难度和作者名，用户可点击"开始游戏"进入对话页面

## ADDED Requirements

### Requirement: 上传入口
客户端 SHALL 在首页或导航栏提供上传海龟汤题目的入口。

#### Scenario: 进入上传页面
- **WHEN** 用户点击"投稿"入口
- **THEN** 客户端展示上传表单页面

### Requirement: 上传表单页面
客户端 SHALL 提供上传题目表单，包含谜面、答案、作者署名三个必填字段。

#### Scenario: 提交题目
- **WHEN** 用户填写完整信息并点击提交
- **THEN** 客户端调用上传接口，等待审核结果，展示审核状态

#### Scenario: 审核通过
- **WHEN** 上传审核通过
- **THEN** 展示"审核通过！已加入题库"，可返回首页

#### Scenario: 审核拒绝
- **WHEN** 上传审核被拒绝
- **THEN** 展示拒绝原因，鼓励用户修改后重试

### Requirement: 我的投稿页面
客户端 SHALL 提供"我的投稿"页面查看历史投稿状态。

#### Scenario: 查看投稿
- **WHEN** 用户进入我的投稿页面
- **THEN** 展示投稿列表，每项显示标题、状态、审核原因（如有）
