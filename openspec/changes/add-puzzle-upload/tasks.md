## 1. 数据库变更

- [ ] 1.1 puzzle 表新增 author、uploader_id、status、review_reason 字段
- [ ] 1.2 现有数据回填：status=APPROVED, author=经典题库
- [ ] 1.3 Puzzle 实体类新增对应字段和 getter/setter
- [ ] 1.4 在服务器执行 ALTER TABLE + 回填 SQL

## 2. AI 审核模块

- [ ] 2.1 LlmService 新增 review 方法，一次调用完成四项审核
- [ ] 2.2 审核 prompt 设计（逻辑+政治+难度+分类，返回 JSON）
- [ ] 2.3 审核结果解析（解析 JSON，兜底处理格式异常）
- [ ] 2.4 难度自动评估逻辑
- [ ] 2.5 分类自动判断逻辑

## 3. 上传接口

- [ ] 3.1 新增 POST /api/puzzle/upload 接口
- [ ] 3.2 PuzzleService 实现上传 + 审核 + 入库逻辑
- [ ] 3.3 新增 GET /api/puzzle/my-submissions 接口
- [ ] 3.4 PuzzleController random 接口增加 status='APPROVED' 过滤

## 4. 前端页面

- [ ] 4.1 新增 upload.html 上传表单页（谜面、答案、作者署名）
- [ ] 4.2 新增 my-submissions.html 我的投稿页
- [ ] 4.3 index.html 首页增加上传入口按钮
- [ ] 4.4 个人中心页增加"我的投稿"入口
- [ ] 4.5 题目卡片展示 author 作者署名

## 5. 部署验证

- [ ] 5.1 本地编译通过
- [ ] 5.2 上传服务器重建部署
- [ ] 5.3 上传一道测试题验证完整审核流程
- [ ] 5.4 验证审核通过/拒绝两种结果
