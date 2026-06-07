package com.turtlesoup.controller;

import com.turtlesoup.common.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    private final JdbcTemplate jdbc;

    public LeaderboardController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/leaderboard")
    public Result<List<Map<String, Object>>> leaderboard() {
        String sql = """
            SELECT t.nickname, SUM(t.score_val) AS score
            FROM (
                SELECT DISTINCT u.nickname, gs.puzzle_id,
                    CASE p.difficulty
                        WHEN '简单' THEN 2
                        WHEN '中等' THEN 3
                        WHEN '困难' THEN 5
                        ELSE 0
                    END AS score_val
                FROM user u
                JOIN game_session gs ON u.id = gs.user_id AND gs.status = 'SOLVED'
                JOIN puzzle p ON gs.puzzle_id = p.id
            ) t
            GROUP BY t.nickname
            ORDER BY score DESC
            LIMIT 10
            """;

        List<Map<String, Object>> ranks = jdbc.queryForList(sql);
        // 添加排名序号
        for (int i = 0; i < ranks.size(); i++) {
            ranks.get(i).put("rank", i + 1);
        }
        return Result.ok(ranks);
    }
}
