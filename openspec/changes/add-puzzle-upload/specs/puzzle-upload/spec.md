## ADDED Requirements

### Requirement: 用户上传题目
系统 SHALL 允许已登录用户提交海龟汤题目，包含谜面、答案和作者署名。

#### Scenario: 成功上传并审核通过
- **WHEN** 已登录用户提交完整题目信息，且 AI 审核通过
- **THEN** 系统创建 puzzle 记录（status=APPROVED），返回成功及审核结果

#### Scenario: 审核拒绝
- **WHEN** 题目逻辑不合理或涉及敏感内容
- **THEN** 系统创建 puzzle 记录（status=REJECTED），返回拒绝原因

#### Scenario: 字段缺失
- **WHEN** 用户未填写谜面、答案或作者署名
- **THEN** 系统返回错误提示"请填写完整信息"

### Requirement: AI 审核
系统 SHALL 调用 LLM API 对上传题目进行四项审核：逻辑检查、政治敏感检查、难度评估、分类判断。

#### Scenario: 四项检查全部通过
- **WHEN** 逻辑合理、无敏感内容
- **THEN** AI 返回 logic_ok=true, political_ok=true，同时提供难度和分类，题目入库

#### Scenario: 逻辑不通过
- **WHEN** 谜面与答案之间无合理推理关系
- **THEN** AI 返回 logic_ok=false，附原因说明

#### Scenario: 涉及敏感内容
- **WHEN** 题目涉及政治敏感内容
- **THEN** AI 返回 political_ok=false，附原因说明

### Requirement: 抽题排除未审核题目
系统 SHALL 在随机抽题时仅返回 status='APPROVED' 的题目。

#### Scenario: 只抽取已审核题目
- **WHEN** 用户请求随机抽题
- **THEN** 系统仅从 status='APPROVED' 的题目中随机选取
