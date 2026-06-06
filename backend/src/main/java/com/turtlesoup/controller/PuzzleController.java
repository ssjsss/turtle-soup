package com.turtlesoup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.turtlesoup.common.Result;
import com.turtlesoup.entity.Puzzle;
import com.turtlesoup.mapper.PuzzleMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/puzzle")
public class PuzzleController {

    private final PuzzleMapper puzzleMapper;

    public PuzzleController(PuzzleMapper puzzleMapper) {
        this.puzzleMapper = puzzleMapper;
    }

    @GetMapping("/random")
    public Result<Map<String, Object>> random() {
        // 查询所有题目
        List<Puzzle> puzzles = puzzleMapper.selectList(null);
        if (puzzles.isEmpty()) {
            return Result.error("暂无题目");
        }

        // 随机选一道
        int index = (int) (Math.random() * puzzles.size());
        Puzzle puzzle = puzzles.get(index);

        // 使用次数 +1
        puzzle.setUsedCount(puzzle.getUsedCount() + 1);
        puzzleMapper.updateById(puzzle);

        Map<String, Object> result = new HashMap<>();
        result.put("id", puzzle.getId());
        result.put("title", puzzle.getTitle());
        result.put("content", puzzle.getContent());
        result.put("difficulty", puzzle.getDifficulty());

        return Result.ok(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Puzzle puzzle = puzzleMapper.selectById(id);
        if (puzzle == null) {
            return Result.error("题目不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", puzzle.getId());
        result.put("title", puzzle.getTitle());
        result.put("content", puzzle.getContent());
        result.put("difficulty", puzzle.getDifficulty());

        return Result.ok(result);
    }
}
