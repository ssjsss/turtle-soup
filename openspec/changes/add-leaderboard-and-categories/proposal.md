## Why

当前游戏缺少玩家激励体系和题目分类导航。加入排行榜让玩家有竞争动力，题目分类让玩家能按兴趣选择不同类型的海龟汤故事。

## What Changes

- **新增** 积分排行榜功能，按用户猜对题目的难度累计积分（简单2分/中等3分/困难5分）
- **新增** 题目分类（脑洞/恐怖/搞笑），puzzle 表新增 category 字段，抽题接口支持分类筛选
- **新增** 排行榜 API `GET /api/leaderboard`，返回前10名用户
- **新增** 排行榜前端页面
- **修改** `GET /api/puzzle/random` 支持可选 `category` 参数筛选

## Capabilities

### New Capabilities
- `leaderboard`: 用户积分排行榜，按猜对题目累计积分排名

### Modified Capabilities
- `puzzle-management`: puzzle 表新增 category 字段，抽题接口支持分类筛选

## Impact

- `puzzle` 表新增 `category` 列
- 现有 8 道题目回填分类数据
- `PuzzleController` 新增分类筛选逻辑
- 新增 `LeaderboardController` 和排行榜查询
- 前端新增分类标签和排行榜页面
