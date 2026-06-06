package com.turtlesoup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.turtlesoup.entity.ChatLog;
import com.turtlesoup.entity.GameSession;
import com.turtlesoup.entity.Puzzle;
import com.turtlesoup.mapper.ChatLogMapper;
import com.turtlesoup.mapper.GameSessionMapper;
import com.turtlesoup.mapper.PuzzleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

    private final GameSessionMapper sessionMapper;
    private final ChatLogMapper chatLogMapper;
    private final PuzzleMapper puzzleMapper;
    private final LlmService llmService;

    public GameService(GameSessionMapper sessionMapper, ChatLogMapper chatLogMapper,
                       PuzzleMapper puzzleMapper, LlmService llmService) {
        this.sessionMapper = sessionMapper;
        this.chatLogMapper = chatLogMapper;
        this.puzzleMapper = puzzleMapper;
        this.llmService = llmService;
    }

    public GameSession startGame(Long userId, Long puzzleId) {
        Puzzle puzzle = puzzleMapper.selectById(puzzleId);
        if (puzzle == null) {
            throw new RuntimeException("题目不存在");
        }

        GameSession session = new GameSession();
        session.setUserId(userId);
        session.setPuzzleId(puzzleId);
        session.setStatus("IN_PROGRESS");
        sessionMapper.insert(session);
        return session;
    }

    @Transactional
    public Map<String, Object> ask(Long sessionId, Long userId, String question) {
        GameSession session = validateSession(sessionId, userId);

        Puzzle puzzle = puzzleMapper.selectById(session.getPuzzleId());
        String aiReply = llmService.judge(question, puzzle.getAnswer());

        ChatLog log = new ChatLog();
        log.setSessionId(sessionId);
        log.setUserQuestion(question);
        log.setAiReply(aiReply);
        chatLogMapper.insert(log);

        return Map.of("reply", aiReply);
    }

    @Transactional
    public Map<String, Object> guess(Long sessionId, Long userId, String guess) {
        GameSession session = validateSession(sessionId, userId);

        Puzzle puzzle = puzzleMapper.selectById(session.getPuzzleId());
        boolean correct = llmService.matchesAnswer(guess, puzzle.getAnswer());

        if (correct) {
            session.setStatus("SOLVED");
            session.setEndTime(LocalDateTime.now());
            sessionMapper.updateById(session);

            return Map.of(
                    "correct", true,
                    "message", "恭喜你猜对了！",
                    "answer", puzzle.getAnswer()
            );
        }

        return Map.of(
                "correct", false,
                "message", "不对，再想想"
        );
    }

    @Transactional
    public Map<String, Object> abandon(Long sessionId, Long userId) {
        GameSession session = validateSession(sessionId, userId);

        session.setStatus("ABANDONED");
        session.setEndTime(LocalDateTime.now());
        sessionMapper.updateById(session);

        Puzzle puzzle = puzzleMapper.selectById(session.getPuzzleId());
        return Map.of(
                "message", "游戏已放弃",
                "answer", puzzle.getAnswer()
        );
    }

    public List<GameSession> getHistory(Long userId) {
        LambdaQueryWrapper<GameSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameSession::getUserId, userId)
                .orderByDesc(GameSession::getStartTime);
        return sessionMapper.selectList(wrapper);
    }

    public List<ChatLog> getChatLogs(Long sessionId, Long userId) {
        // 验证权限
        GameSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该会话");
        }

        LambdaQueryWrapper<ChatLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatLog::getSessionId, sessionId)
                .orderByAsc(ChatLog::getCreateTime);
        return chatLogMapper.selectList(wrapper);
    }

    private GameSession validateSession(Long sessionId, Long userId) {
        GameSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该会话");
        }
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new RuntimeException("游戏已结束");
        }
        return session;
    }
}
