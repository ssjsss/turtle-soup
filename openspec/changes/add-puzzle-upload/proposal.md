## Why

当前题库仅有 8 道预置题目，玩家很快就会玩完。允许用户上传海龟汤题目，由 AI 自动审核逻辑合理性、检测敏感内容、评估难度、判断分类，审核通过后进入抽题池。这将让题库持续增长，形成社区内容生态。

## What Changes

- **新增** 用户上传题目功能，填写谜面、答案、作者署名
- **新增** AI 审核流程：一次性审核逻辑合理性、政治敏感、难度评估、分类判断
- **新增** puzzle 表审核状态字段（PENDING/APPROVED/REJECTED）和作者署名
- **新增** 抽题接口仅返回审核通过的题目
- **新增** "我的投稿"页面，查看投稿审核状态
- **新增** 上传入口和投稿表单页面

## Capabilities

### New Capabilities
- `puzzle-upload`: 用户上传海龟汤题目，AI 自动审核并分类入库
- `my-submissions`: 用户查看自己的投稿记录及审核状态

### Modified Capabilities
- `puzzle-management`: puzzle 表新增 author、uploader_id、status、review_reason 字段，random 接口排除未审核题目
- `android-client`: 新增上传入口、投稿表单页面、我的投稿页面

## Impact

- `puzzle` 表新增 4 列（author, uploader_id, status, review_reason）
- 现有 8 道题回填 `status='APPROVED'`, `author='经典题库'`
- 新增 `POST /api/puzzle/upload` 接口
- 新增 `GET /api/puzzle/my-submissions` 接口
- 每次上传触发 1 次 LLM API 调用（4 项审核合并）
- 新增前端页面：upload.html、my-submissions.html
