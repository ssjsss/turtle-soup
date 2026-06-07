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
import java.util.regex.Pattern;

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

    /**
     * 判断用户输入，返回结构化结果。
     * reply: 是/否/无关紧要/恭喜猜对了/猜错了再想想
     * solved: 是否猜对
     */
    public record JudgeResult(String reply, boolean solved) {}

    public JudgeResult judge(String userInput, String truthAnswer) {
        String systemPrompt = """
            你是一个海龟汤推理游戏的裁判。以下是故事的完整真相：
            
            %s
            
            玩家会发来一句话，你需要判断这是"提问"还是"宣告自己的推理结论"。
            
            ---
            如果是提问（玩家在询问某个事实），回答规则：
            1. 与真相一致 → 回答"是"
            2. 与真相相反 → 回答"否"
            3. 与故事无关 → 回答"无关紧要"
            
            ---
            如果是宣告结论（玩家在尝试说出完整推理），判定规则：
            - 玩家说出了真相的核心逻辑 → 回答"恭喜猜对了"
            - 核心逻辑与真相不符 → 回答"猜错了再想想"
            
            注意：不需要逐字逐句完全匹配，只要核心因果关系正确就算对。
            例如真相是"A杀死了B因为C"，玩家说"A害死了B，原因是C"也应该算对。
            
            ---
            严格禁止回答以上五种之外的任何内容。
            禁止做任何解释、说明或补充。
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
            String requestBody = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseResult(response.body());
            }
            return new JudgeResult("无关紧要", false);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new JudgeResult("系统繁忙，请重试", false);
        }
    }

    private JudgeResult parseResult(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0)
                    .path("message").path("content").asText().trim();

            if (content.contains("恭喜猜对了") || content.contains("猜对了")) {
                return new JudgeResult("恭喜猜对了", true);
            }
            if (content.contains("猜错了")) {
                return new JudgeResult("猜错了再想想", false);
            }
            if (content.contains("是")) {
                return new JudgeResult("是", false);
            }
            if (content.contains("否")) {
                return new JudgeResult("否", false);
            }
        } catch (Exception ignored) {
        }
        return new JudgeResult("无关紧要", false);
    }
}
