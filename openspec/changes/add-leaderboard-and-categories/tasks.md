## 1. 数据库变更

- [ ] 1.1 puzzle 表新增 category 列，更新实体类 Puzzle.java
- [ ] 1.2 编写 SQL 回填现有 8 道题的分类数据
- [ ] 1.3 在服务器上执行 ALTER TABLE + 回填 SQL

## 2. 后端 - 分类筛选

- [ ] 2.1 PuzzleController 的 random 接口支持 category 参数筛选
- [ ] 2.2 前端 index.html 添加分类标签按钮，点击后传入 category 参数

## 3. 后端 - 排行榜

- [ ] 3.1 新增 LeaderboardController，实现 GET /api/leaderboard
- [ ] 3.2 排行榜 SQL 实现积分聚合 + 去重（同用户同题只计一次）
- [ ] 3.3 新增 leaderboard.html 排行榜页面，展示 top 10

## 4. 前端收尾

- [ ] 4.1 底部导航栏新增"排行"入口
- [ ] 4.2 首页分类标签样式（选中高亮）
- [ ] 4.3 index.html 抽题卡片展示分类和难度

## 5. 部署验证

- [ ] 5.1 本地编译通过
- [ ] 5.2 上传服务器重建部署
- [ ] 5.3 猜对几道题验证排行榜数据
