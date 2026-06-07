## Context

基于已完成的 `turtle-soup-game` 变更，当前系统已有完整的游戏流程。puzzle 表当前有 8 条数据，game_session 表记录了猜对/放弃的状态。

需要在现有基础上新增排行榜和分类两个功能，改动范围控制在 puzzle 表、抽题接口和新增排行榜接口。

## Goals / Non-Goals

**Goals:**
- 玩家猜对题目后按难度获得积分（简单2分/中等3分/困难5分）
- 同一用户对同一道题只计首次猜对的分
- 排行榜展示前 10 名，按积分降序排列
- 题目支持分类（脑洞/恐怖/搞笑），抽题时可筛选分类
- 现有 8 道题回填适当分类

**Non-Goals:**
- 不上传新题目（保留到后续变更）
- 不做排行榜历史/周榜/月榜
- 不做分类管理后台（分类硬编码三种）

## Decisions

### 1. 积分计算方式：实时聚合查询

不使用 `user` 表冗余存储积分。排行榜 API 实时 `JOIN game_session + puzzle` 聚合计算。

```sql
SELECT u.id, u.nickname,
  SUM(CASE p.difficulty WHEN '简单' THEN 2 WHEN '中等' THEN 3 WHEN '困难' THEN 5 END) AS score
FROM user u
JOIN game_session gs ON u.id = gs.user_id AND gs.status = 'SOLVED'
JOIN puzzle p ON gs.puzzle_id = p.id
GROUP BY u.id
ORDER BY score DESC
LIMIT 10
```

去重通过 `game_session` 本身的唯一性保证（同一用户对同一 puzzle 首次 SOLVED 计分，若出现多条只会计一条的原因是……等等这个 SQL 会重复计分）。

**修正**：需要确保同一用户对同一 puzzle 只计一次。最简单的方案：新增一张 `user_score` 表，在猜对时写入（幂等），排行榜直接查这张表。

……但这样增加了复杂度。实际上更简单：在 GameService 猜对时，查询该用户是否已有该 puzzle 的 SOLVED 记录，有则只更新状态不加分，无则正常处理。分数不存表，排行榜用子查询去重。

**最终决定**：不用新表，排行榜 SQL 用 `DISTINCT` 子查询去重：

```sql
SELECT nickname, SUM(score_val) AS score FROM (
  SELECT DISTINCT u.nickname, gs.puzzle_id,
    CASE p.difficulty WHEN '简单' THEN 2 WHEN '中等' THEN 3 WHEN '困难' THEN 5 END AS score_val
  FROM user u
  JOIN game_session gs ON u.id = gs.user_id AND gs.status = 'SOLVED'
  JOIN puzzle p ON gs.puzzle_id = p.id
) t GROUP BY nickname ORDER BY score DESC LIMIT 10
```

### 2. puzzle 分类：新增 category 列 + 枚举校验

puzzle 表新增 `category VARCHAR(20) NOT NULL DEFAULT '脑洞'`。Controller 层校验 category 值必须在 `脑洞/恐怖/搞笑` 范围内。

现有 8 道题回填分类：
- 半根火柴、水草、满地木屑、三兄弟 → 脑洞
- 葬礼、夜半敲门 → 恐怖
- 跳火车、牛吃草 → 搞笑

## Risks / Trade-offs

| 风险 | 缓解 |
|------|------|
| 排行榜 SQL 数据量大时变慢 | 当前用户量和题目量小（< 1000），JOIN 聚合完全 OK |
| 分类数据回填主观 | 分类本身就是主观的，后续可调整 |
