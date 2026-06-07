package com.turtlesoup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.turtlesoup.common.Result;
import com.turtlesoup.entity.Puzzle;
import com.turtlesoup.mapper.PuzzleMapper;
import com.turtlesoup.service.PuzzleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/puzzle")
public class PuzzleController {

    private final PuzzleMapper puzzleMapper;
    private final PuzzleService puzzleService;

    private static final Set<String> VALID_CATEGORIES = Set.of("脑洞", "恐怖", "搞笑");

    public PuzzleController(PuzzleMapper puzzleMapper, PuzzleService puzzleService) {
        this.puzzleMapper = puzzleMapper;
        this.puzzleService = puzzleService;
    }

    @GetMapping("/random")
    public Result<Map<String, Object>> random(@RequestParam(required = false) String category) {
        if (category != null && !VALID_CATEGORIES.contains(category)) {
            return Result.error("无效的分类");
        }

        LambdaQueryWrapper<Puzzle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Puzzle::getStatus, "APPROVED");
        if (category != null) {
            wrapper.eq(Puzzle::getCategory, category);
        }

        List<Puzzle> puzzles = puzzleMapper.selectList(wrapper);
        if (puzzles.isEmpty()) {
            return Result.error(category != null ? "该分类暂无题目" : "暂无题目");
        }

        int index = (int) (Math.random() * puzzles.size());
        Puzzle puzzle = puzzles.get(index);
        puzzle.setUsedCount(puzzle.getUsedCount() + 1);
        puzzleMapper.updateById(puzzle);

        return Result.ok(puzzleToMap(puzzle));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Puzzle puzzle = puzzleMapper.selectById(id);
        if (puzzle == null) return Result.error("题目不存在");
        return Result.ok(puzzleToMap(puzzle));
    }

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestBody UploadRequest req,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PuzzleService.UploadResult result = puzzleService.upload(
                userId, req.title(), req.content(), req.answer(), req.author());

        Map<String, Object> data = new HashMap<>();
        data.put("passed", result.passed());
        data.put("message", result.message());
        data.put("difficulty", result.difficulty());
        data.put("category", result.category());
        return Result.ok(data);
    }

    @GetMapping("/my-submissions")
    public Result<List<Map<String, Object>>> mySubmissions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Puzzle> puzzles = puzzleService.getMySubmissions(userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Puzzle p : puzzles) {
            Map<String, Object> item = puzzleToMap(p);
            item.put("status", p.getStatus());
            item.put("reviewReason", p.getReviewReason());
            item.put("createTime", p.getCreateTime().toString());
            result.add(item);
        }
        return Result.ok(result);
    }

    private Map<String, Object> puzzleToMap(Puzzle p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("title", p.getTitle());
        map.put("content", p.getContent());
        map.put("difficulty", p.getDifficulty());
        map.put("category", p.getCategory());
        map.put("author", p.getAuthor());
        return map;
    }

    public record UploadRequest(String title, String content, String answer, String author) {}
}
