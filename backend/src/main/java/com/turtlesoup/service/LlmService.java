package com.turtlesoup.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public LlmService(@Value("${llm.api-key}") String apiKey,
                      @Value("${llm.model}") String model,
                      @Value("${llm.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public record JudgeResult(String reply, boolean solved) {}

    public JudgeResult judge(String userInput, String truthAnswer) {
        String systemPrompt = """
            你是一个海龟汤推理游戏的裁判。以下是故事的完整真相：
            
            %s
            
            玩家会发来一句话，你需要判断这是"提问"还是"宣告自己的推理结论"。
            
            ---
            如果是提问，回答规则：
            1. 与真相一致 → 回答"是"
            2. 与真相相反 → 回答"否"
            3. 与故事无关 → 回答"无关紧要"
            
            ---
            如果是宣告结论，判定规则：
            - 玩家说出了真相的核心逻辑 → 回答"恭喜猜对了"
            - 核心逻辑与真相不符 → 回答"猜错了再想想"
            
            注意：不需要逐字逐句匹配，核心因果关系正确就算对。
            
            ---
            严格禁止回答以上五种之外的任何内容，禁止做任何解释。
            """.formatted(truthAnswer);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userInput)
                ),
                "temperature", 0.1,
                "max_tokens", 30
        );

        try {
            String content = callLlm(body);
            if (content.contains("恭喜猜对了") || content.contains("猜对了")) {
                return new JudgeResult("恭喜猜对了", true);
            }
            if (content.contains("猜错了")) {
                return new JudgeResult("猜错了再想想", false);
            }
            if (content.contains("是")) return new JudgeResult("是", false);
            if (content.contains("否")) return new JudgeResult("否", false);
        } catch (Exception ignored) {}
        return new JudgeResult("无关紧要", false);
    }

    /**
     * 审核上传题目：逻辑、政治、难度、分类
     */
    public record ReviewResult(boolean logicOk, String logicReason,
                               boolean politicalOk, String politicalReason,
                               String difficulty, String category) {
        public boolean passed() { return logicOk && politicalOk; }
        public String rejectReason() {
            if (!logicOk) return "逻辑问题：" + logicReason;
            if (!politicalOk) return "内容问题：" + politicalReason;
            return "";
        }
    }

    public ReviewResult review(String title, String content, String answer) {
        String prompt = """
            你是海龟汤题目的审核员。请审核以下投稿。
            
            标题：%s
            谜面：%s
            答案：%s
            
            请按以下标准审核，返回纯JSON（不要markdown代码块）：
            
            {
              "logic_ok": true或false,
              "logic_reason": "逻辑问题说明（通过时为空字符串）",
              "political_ok": true或false,
              "political_reason": "敏感内容说明（通过时为空字符串）",
              "difficulty": "简单/中等/困难",
              "category": "脑洞/恐怖/搞笑"
            }
            
            审核标准：
            逻辑：谜面和答案之间是否存在合理的因果推理关系？答案是否能够从谜面推理得出？
            政治：是否涉及台独/藏独/疆独/港独/法轮功/色情/暴力宣扬等敏感内容？
            难度：信息量少推理链短→简单，需要多步推理→中等，需要打破思维定式→困难
            分类：按以下规则判断优先级
              - 搞笑：不能有死人，答案很无厘头
              - 脑洞：有幽灵/超能力/未来科技/人格实体化等超现实元素（优先级最高）
              - 恐怖：有死人（优先级中等）
            
            只有 logic_ok 和 political_ok 都为 true 才算审核通过。
            """.formatted(title, content, answer);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", prompt),
                        Map.of("role", "user", "content", "请审核")
                ),
                "temperature", 0.1,
                "max_tokens", 300
        );

        try {
            String result = callLlm(body);
            return parseReview(result);
        } catch (Exception e) {
            return new ReviewResult(false, "审核服务异常，请稍后重试",
                    false, "审核服务异常，请稍后重试", "中等", "脑洞");
        }
    }

    private ReviewResult parseReview(String raw) {
        try {
            // 清理可能出现的 markdown 代码块标记
            String json = raw.trim();
            if (json.startsWith("```")) {
                json = json.substring(json.indexOf("\n") + 1);
                if (json.endsWith("```")) {
                    json = json.substring(0, json.lastIndexOf("```")).trim();
                }
            }
            JsonNode root = objectMapper.readTree(json);
            return new ReviewResult(
                    root.path("logic_ok").asBoolean(true),
                    root.path("logic_reason").asText(""),
                    root.path("political_ok").asBoolean(true),
                    root.path("political_reason").asText(""),
                    root.path("difficulty").asText("中等"),
                    root.path("category").asText("脑洞")
            );
        } catch (Exception e) {
            return new ReviewResult(false, "审核结果解析失败",
                    false, "审核结果解析失败", "中等", "脑洞");
        }
    }

    private String callLlm(Map<String, Object> body) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").get(0)
                    .path("message").path("content").asText().trim();
        }
        throw new IOException("LLM API error: " + response.statusCode());
    }
}
