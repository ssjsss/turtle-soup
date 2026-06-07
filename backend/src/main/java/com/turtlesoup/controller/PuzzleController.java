package com.turtlesoup.controller;

import com.turtlesoup.common.Result;
import com.turtlesoup.entity.Puzzle;
import com.turtlesoup.mapper.PuzzleMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/puzzle")
public class PuzzleController {

    private final PuzzleMapper puzzleMapper;

    private static final Set<String> VALID_CATEGORIES = Set.of("脑洞", "恐怖", "搞笑");

    public PuzzleController(PuzzleMapper puzzleMapper) {
        this.puzzleMapper = puzzleMapper;
    }

    @GetMapping("/random")
    public Result<Map<String, Object>> random(@RequestParam(required = false) String category) {
        // 分类校验
        if (category != null && !VALID_CATEGORIES.contains(category)) {
            return Result.error("无效的分类，可选：脑洞/恐怖/搞笑");
        }

        // 按分类筛选
        List<Puzzle> puzzles;
        if (category != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", category);
            puzzles = puzzleMapper.selectByMap(map);
        } else {
            puzzles = puzzleMapper.selectList(null);
        }

        if (puzzles.isEmpty()) {
            return Result.error(category != null ? "该分类暂无题目" : "暂无题目");
        }

        // 随机选一道
        int index = (int) (Math.random() * puzzles.size());
        Puzzle puzzle = puzzles.get(index);

        // 使用次数 +1
        puzzle.setUsedCount(puzzle.getUsedCount() + 1);
        puzzleMapper.updateById(puzzle);

        return Result.ok(puzzleToMap(puzzle));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Puzzle puzzle = puzzleMapper.selectById(id);
        if (puzzle == null) {
            return Result.error("题目不存在");
        }
        return Result.ok(puzzleToMap(puzzle));
    }

    private Map<String, Object> puzzleToMap(Puzzle puzzle) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", puzzle.getId());
        result.put("title", puzzle.getTitle());
        result.put("content", puzzle.getContent());
        result.put("difficulty", puzzle.getDifficulty());
        result.put("category", puzzle.getCategory());
        return result;
    }
}
