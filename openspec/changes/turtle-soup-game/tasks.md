## 1. 项目初始化与数据库

- [x] 1.1 使用 Spring Initializr 创建 SpringBoot 3 项目，添加 Web、MyBatis-Plus、MySQL、Validation 依赖
- [x] 1.2 配置 MySQL 数据源和 MyBatis-Plus
- [x] 1.3 编写完整 SQL 建表语句，创建 user、puzzle、game_session、chat_log 四张表
- [x] 1.4 创建对应实体类（User、Puzzle、GameSession、ChatLog）
- [x] 1.5 编写初始化数据 SQL，预置 5-10 道海龟汤题目

## 2. 用户认证模块

- [x] 2.1 实现用户注册接口 POST /api/auth/register（bcrypt 加密密码，返回 JWT）
- [x] 2.2 实现用户登录接口 POST /api/auth/login（验证密码，返回 JWT）
- [x] 2.3 实现 JWT 工具类（生成、解析、校验 Token）
- [x] 2.4 实现 JWT 拦截器，保护需要登录的接口
- [x] 2.5 实现获取个人信息接口 GET /api/user/profile
- [x] 2.6 实现更新个人信息接口 PUT /api/user/profile

## 3. 题库管理模块

- [x] 3.1 实现随机获取题目接口 GET /api/puzzle/random（随机选一条，used_count +1，不返回答案）
- [x] 3.2 实现获取题目详情接口 GET /api/puzzle/{id}
- [x] 3.3 实现管理员新增题目接口 POST /api/puzzle（可选，第一版可 SQL 直接入库）

## 4. AI 判断模块

- [x] 4.1 封装 LLM API 调用工具类（支持 OpenAI / 通义千问，可配置切换）
- [x] 4.2 设计 System Prompt：角色设定 + 故事真相 + 严格输出约束
- [x] 4.3 实现 AI 判断核心方法：接收用户问题 + 标准答案 → 返回"是/否/无关紧要"
- [x] 4.4 添加输出后处理兜底逻辑（正则匹配提取，无法匹配时默认"无关紧要"）
- [x] 4.5 添加 10 秒超时和异常处理

## 5. 游戏会话模块

- [x] 5.1 实现开始游戏接口 POST /api/game/start（创建 game_session，状态 IN_PROGRESS）
- [x] 5.2 实现提问接口 POST /api/game/{sessionId}/ask（调用 AI 判断 + 保存 chat_log）
- [x] 5.3 实现猜真相接口 POST /api/game/{sessionId}/guess（LLM 语义比对，更新 SOLVED）
- [x] 5.4 实现放弃游戏接口 POST /api/game/{sessionId}/abandon（更新 ABANDONED，返回答案）
- [x] 5.5 实现获取历史游戏记录接口 GET /api/game/history

## 6. 对话日志模块

- [x] 6.1 实现查询会话对话记录接口 GET /api/game/{sessionId}/chat（按时间正序）
- [x] 6.2 添加权限校验：仅允许会话创建者查看对话记录

## 7. Android 客户端

- [x] 7.1 搭建 Android 项目框架（WebView + H5 页面方案）
- [x] 7.2 实现登录注册页面（H5：表单 + 调用后端接口）
- [x] 7.3 实现主页面（H5：随机抽题按钮 + 谜面展示）
- [x] 7.4 实现游戏对话页面（H5：仿聊天界面，提问气泡 + AI 回答气泡）
- [x] 7.5 实现猜真相弹出框和放弃按钮
- [x] 7.6 实现历史记录页面（H5：游戏记录列表 + 对话回放）
- [x] 7.7 实现个人中心页面（H5：用户信息展示 + 修改昵称 + 退出登录）
- [x] 7.8 封装 HTTP 请求工具（统一处理 JWT 和错误）

## 8. 联调与测试

- [x] 8.1 后端所有接口自测（Postman 或 curl）
- [x] 8.2 Android 客户端与后端联调，验证完整游戏流程
- [x] 8.3 异常场景测试（Token 过期、API 超时、题库为空等）
