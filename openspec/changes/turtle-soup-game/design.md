## Context

全新项目，从零构建海龟汤推理游戏。用户通过向 AI 提问逐步推理出海龟汤故事的真相。项目定位为个人全栈作品，需兼顾技术完整性和开发效率。

当前工作区为空（仅 README.md），无存量代码。

## Goals / Non-Goals

**Goals:**
- 提供完整的用户注册登录系统
- 实现海龟汤题库管理与随机抽题
- 集成 LLM API 实现"是/否/无关"的 AI 问答判断
- 管理单局游戏会话，支持"猜真相"判定
- 持久化所有对话日志
- 提供 Android 客户端完成游戏交互
- 清晰的 RESTful API 设计，前后端分离

**Non-Goals:**
- 第一版不做用户上传题目功能（避免审核工作流复杂度）
- 不做排行榜、社交功能
- 不做 iOS 客户端（仅 Android）
- 不自行训练模型（使用现成 LLM API）

## Decisions

### 1. 后端技术栈：SpringBoot 3 + MySQL 8.0

**理由**：SpringBoot 生态成熟，Java/Spring 在国内市场广泛使用，对个人求职最友好。MySQL 是最通用的关系型数据库。

**备选方案**：Node.js/Express 更快但 SpringBoot 规范性更强；PostgreSQL 功能更强但 MySQL 更通用。

### 2. AI 判断：LLM API（OpenAI / 通义千问）而非规则匹配

**理由**：纯关键词/正则匹配准确率极低，"无关紧要"几乎无法判断，用户体验差。LLM API 单次调用成本极低（约 0.001-0.01 元），通过 system prompt 可精确约束输出格式。

**备选方案**：本地小模型需 GPU 资源，Android 端跑不动；纯规则匹配无法保证体验。

**System Prompt 设计要点**：
- 明确角色：你是海龟汤游戏的裁判
- 提供完整故事真相
- 严格约束输出：仅回答"是"、"否"、"无关紧要"，不做任何解释
- 判断标准：与真相相关且有助于推理 → 是/否；与故事无关 → 无关紧要

### 3. 认证方案：JWT Token

**理由**：无状态认证，适合移动端。Token 过期后前端自动跳转登录页。密码使用 bcrypt 加密。

### 4. 数据库：4 张表

```
user (id, username, password, nickname, create_time, update_time)
puzzle (id, title, content, answer, used_count, difficulty, create_time)
game_session (id, user_id, puzzle_id, status, start_time, end_time)
chat_log (id, session_id, user_question, ai_reply, create_time)
```

`chat_log` 通过 `session_id` 关联 `game_session`，而不是直接关联 `puzzle_id`。这样同一用户多次玩同一道题的对话不会混淆。

### 5. Android 客户端：H5 + WebView 壳方案

**理由**：项目核心亮点在后端架构和 AI 逻辑，前端用 HTML5 页面 + Android WebView 壳可以最快完成完整闭环。后续可升级为 Flutter 或原生。

**备选方案**：Flutter 体验更好但学习成本高；原生 Kotlin 开发量最大。

### 6. RESTful API 设计

```
POST   /api/auth/register       用户注册
POST   /api/auth/login          用户登录
GET    /api/user/profile        获取个人信息
PUT    /api/user/profile        更新个人信息
GET    /api/puzzle/random       随机获取一道题
GET    /api/puzzle/{id}         获取题目详情
POST   /api/game/start          开始新游戏
POST   /api/game/{sessionId}/ask    提问
POST   /api/game/{sessionId}/guess   猜真相
GET    /api/game/{sessionId}/chat    获取对话记录
GET    /api/game/history         获取历史游戏记录
```

## Risks / Trade-offs

| 风险 | 缓解措施 |
|------|---------|
| LLM API 偶发超时或不可用 | 设置 10 秒超时，超时后返回"系统繁忙，请重试"，前端做友好提示 |
| LLM 不遵守输出约束，回复多余内容 | System prompt 强约束 + 后端做输出后处理（正则匹配，只取"是/否/无关"） |
| API 调用费用累积 | 初期题库 20-30 道，每人每天玩 5-10 局，单月费用可控（约 1-5 美元）。可加频率限制 |
| 用户用自然语言"猜真相"难以准确匹配标准答案 | 用 LLM 做语义比对，设定相似度阈值，不要求逐字匹配 |
| H5 页面体验不如原生 | 先用 WebView 方案快速闭环，后续可升级 |
