## Why

构建一款前后端分离的安卓移动端海龟汤推理问答游戏。用户通过向 AI 提问逐步推理出海龟汤故事的真相，AI 仅回答"是/否/无关"。项目以 SpringBoot + MySQL + Android 客户端 + LLM API 为技术栈，适合作为个人全栈项目展示前后端分离、RESTful API 设计、AI 集成等核心能力。

## What Changes

- **新增** 用户系统（注册、登录、个人信息管理）
- **新增** 海龟汤题库管理（随机抽题、题目 CRUD）
- **新增** AI 推理问答核心（调用 LLM API 判断 是/否/无关）
- **新增** 游戏会话管理（单局游戏状态追踪、猜真相判定）
- **新增** 对话日志记录（完整保存每轮问答）
- **新增** Android 客户端（登录注册、抽题、仿聊天界面、历史记录、个人中心）
- **新增** SpringBoot 后端 RESTful API 服务

## Capabilities

### New Capabilities
- `user-auth`: 用户注册、登录、个人信息管理
- `puzzle-management`: 海龟汤题库的增删改查与随机抽取
- `game-session`: 游戏会话管理，包括开始游戏、提问、猜真相、结束游戏
- `ai-judgment`: 调用 LLM API 判断用户提问，返回"是/否/无关"
- `chat-history`: 对话日志记录与查询
- `android-client`: Android 客户端页面与交互

### Modified Capabilities
<!-- 全新项目，无已有 capability 需要修改 -->

## Impact

- 新增 MySQL 数据库，包含 4 张表：user、puzzle、game_session、chat_log
- 新增 SpringBoot 后端项目，暴露 RESTful API
- 集成 LLM API（如 OpenAI / 通义千问）作为 AI 判断引擎
- 新增 Android 客户端项目（Flutter 或 H5 + WebView）
