## ADDED Requirements

### Requirement: AI 判断用户提问
系统 SHALL 调用 LLM API，根据海龟汤故事的标准答案判断用户提问，仅返回"是"、"否"或"无关紧要"。

#### Scenario: 问题与答案相关且匹配
- **WHEN** 用户提问内容与真相答案指向一致
- **THEN** AI 返回"是"

#### Scenario: 问题与答案相关但不匹配
- **WHEN** 用户提问内容与真相答案指向相反
- **THEN** AI 返回"否"

#### Scenario: 问题与故事无关
- **WHEN** 用户提问内容与故事真相无任何关联
- **THEN** AI 返回"无关紧要"

### Requirement: AI 回答格式约束
系统 SHALL 通过 system prompt 严格约束 LLM 输出格式，并通过后端做后处理兜底。

#### Scenario: 输出格式符合预期
- **WHEN** LLM 返回的文本中可匹配到"是"、"否"或"无关紧要"
- **THEN** 后端提取匹配到的答案返回给前端

#### Scenario: 输出格式异常
- **WHEN** LLM 返回的文本无法匹配预期格式
- **THEN** 后端返回"无关紧要"作为默认兜底

### Requirement: API 超时处理
系统 SHALL 设置 10 秒超时，超时后友好提示用户。

#### Scenario: LLM API 超时
- **WHEN** LLM API 调用超过 10 秒未返回
- **THEN** 系统返回"系统繁忙，请重试"
