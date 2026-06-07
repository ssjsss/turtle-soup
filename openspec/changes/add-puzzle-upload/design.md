## Context

当前系统有排行榜和分类功能（`add-leaderboard-and-categories`），puzzle 表已包含 category、difficulty 字段。本项目在现有基础上新增用户上传和 AI 审核流程。

## Goals / Non-Goals

**Goals:**
- 用户可提交海龟汤题目（谜面、答案、作者署名）
- AI 一次性审核：逻辑合理性、政治敏感、难度评估、分类判断
- 审核通过后进入抽题池，拒绝时告知原因
- 提供"我的投稿"页面查看审核状态
- 抽题接口仅返回审核通过的题目

**Non-Goals:**
- 不做编辑重提功能（第一版）
- 不做管理员审核后台
- 不做举报/下架功能

## Decisions

### 1. 一次 LLM 调用完成四项审核

**理由**：四次调用串行等待太久（3×4=12秒），合成一个 prompt 一次返回四项结果，3-5 秒出结果。成本同样是 0.002 元。

**Prompt 设计**：

```
你是海龟汤题目的审核员。请审核以下投稿，返回 JSON：

{
  "logic_ok": true/false,
  "logic_reason": "逻辑问题说明",
  "political_ok": true/false,
  "political_reason": "敏感内容说明",
  "difficulty": "简单/中等/困难",
  "category": "脑洞/恐怖/搞笑"
}

审核标准：
- 逻辑：谜面和答案之间是否存在合理的因果推理关系
- 政治：是否涉及台独/藏独/疆独/港独/法轮功等敏感内容
- 难度：信息量少推理链短→简单，需要多步推理→中等，隐藏关键信息→困难
- 分类：按项目规则判断

只有 logic_ok 和 political_ok 都通过才算审核通过。
```

### 2. puzzle 表新增字段

```
ALTER TABLE puzzle ADD COLUMN author VARCHAR(50) DEFAULT '经典题库';
ALTER TABLE puzzle ADD COLUMN uploader_id BIGINT DEFAULT NULL;
ALTER TABLE puzzle ADD COLUMN status VARCHAR(20) DEFAULT 'APPROVED';
ALTER TABLE puzzle ADD COLUMN review_reason VARCHAR(255) DEFAULT NULL;
```

### 3. 抽题接口过滤

`GET /api/puzzle/random` 和 `GET /api/puzzle/random?category=X` 的 WHERE 条件增加 `status = 'APPROVED'`。

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| AI 审核误判（合理题被拒） | review_reason 告知原因，后续可加人工复审 |
| AI 审核漏过敏感内容 | prompt 明确列出敏感红线，political_ok 判断从严 |
| LLM 返回非标准 JSON 导致解析失败 | 后处理兜底：解析失败时标记为 PENDING 等待人工处理 |
