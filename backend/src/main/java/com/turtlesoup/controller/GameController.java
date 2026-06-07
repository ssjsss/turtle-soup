package com.turtlesoup.controller;

import com.turtlesoup.common.Result;
import com.turtlesoup.entity.ChatLog;
import com.turtlesoup.entity.GameSession;
import com.turtlesoup.entity.Puzzle;
import com.turtlesoup.mapper.PuzzleMapper;
import com.turtlesoup.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final PuzzleMapper puzzleMapper;

    public GameController(GameService gameService, PuzzleMapper puzzleMapper) {
        this.gameService = gameService;
        this.puzzleMapper = puzzleMapper;
    }

    @PostMapping("/start")
    public Result<Map<String, Object>> start(@RequestBody StartRequest req,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        GameSession session = gameService.startGame(userId, req.puzzleId());

        Puzzle puzzle = puzzleMapper.selectById(req.puzzleId());

        return Result.ok(Map.of(
                "sessionId", session.getId(),
                "title", puzzle.getTitle(),
                "content", puzzle.getContent()
        ));
    }

    @PostMapping("/{sessionId}/ask")
    public Result<Map<String, Object>> ask(@PathVariable Long sessionId,
                                            @RequestBody AskRequest req,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = gameService.ask(sessionId, userId, req.question());
        return Result.ok(result);
    }

    @PostMapping("/{sessionId}/abandon")
    public Result<Map<String, Object>> abandon(@PathVariable Long sessionId,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(gameService.abandon(sessionId, userId));
    }

    @GetMapping("/{sessionId}/chat")
    public Result<List<Map<String, Object>>> chatLogs(@PathVariable Long sessionId,
                                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ChatLog> logs = gameService.getChatLogs(sessionId, userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatLog log : logs) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getId());
            item.put("userQuestion", log.getUserQuestion());
            item.put("aiReply", log.getAiReply());
            item.put("createTime", log.getCreateTime().toString());
            result.add(item);
        }
        return Result.ok(result);
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> history(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<GameSession> sessions = gameService.getHistory(userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (GameSession session : sessions) {
            Puzzle puzzle = puzzleMapper.selectById(session.getPuzzleId());
            Map<String, Object> item = new HashMap<>();
            item.put("sessionId", session.getId());
            item.put("puzzleId", session.getPuzzleId());
            item.put("title", puzzle != null ? puzzle.getTitle() : "未知");
            item.put("status", session.getStatus());
            item.put("startTime", session.getStartTime().toString());
            if (session.getEndTime() != null) {
                item.put("endTime", session.getEndTime().toString());
            }
            result.add(item);
        }
        return Result.ok(result);
    }

    public record StartRequest(Long puzzleId) {}
    public record AskRequest(String question) {}
}
