package com.turtlesoup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.turtlesoup.entity.Puzzle;
import com.turtlesoup.mapper.PuzzleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuzzleService {

    private final PuzzleMapper puzzleMapper;
    private final LlmService llmService;

    public PuzzleService(PuzzleMapper puzzleMapper, LlmService llmService) {
        this.puzzleMapper = puzzleMapper;
        this.llmService = llmService;
    }

    public record UploadResult(boolean passed, String message,
                               String difficulty, String category) {}

    public UploadResult upload(Long userId, String title, String content,
                                String answer, String author) {
        // AI 审核
        LlmService.ReviewResult review = llmService.review(title, content, answer);

        Puzzle puzzle = new Puzzle();
        puzzle.setTitle(title);
        puzzle.setContent(content);
        puzzle.setAnswer(answer);
        puzzle.setAuthor(author);
        puzzle.setUploaderId(userId);
        puzzle.setUsedCount(0);

        if (review.passed()) {
            puzzle.setStatus("APPROVED");
            puzzle.setDifficulty(review.difficulty());
            puzzle.setCategory(review.category());
            puzzle.setReviewReason("");
            puzzleMapper.insert(puzzle);
            return new UploadResult(true, "审核通过，已加入题库！",
                    review.difficulty(), review.category());
        } else {
            puzzle.setStatus("REJECTED");
            puzzle.setDifficulty("中等");
            puzzle.setCategory("脑洞");
            puzzle.setReviewReason(review.rejectReason());
            puzzleMapper.insert(puzzle);
            return new UploadResult(false, review.rejectReason(), "", "");
        }
    }

    public List<Puzzle> getMySubmissions(Long userId) {
        LambdaQueryWrapper<Puzzle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Puzzle::getUploaderId, userId)
                .orderByDesc(Puzzle::getCreateTime);
        return puzzleMapper.selectList(wrapper);
    }
}
