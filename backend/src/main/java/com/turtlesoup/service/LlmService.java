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

    private static final Pattern ANSWER_PATTERN = Pattern.compile("(是|否|无关紧要)");

    public LlmService(@Value("${llm.api-key}") String apiKey,
                      @Value("${llm.model}") String model,
                      @Value("${llm.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 判断用户提问，返回"是"、"否"或"无关紧要"
     */
    public String judge(String userQuestion, String truthAnswer) {
        String systemPrompt = buildSystemPrompt(truthAnswer);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userQuestion)
                ),
                "temperature", 0.1,
                "max_tokens", 20
        );

        try {
            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractAnswer(response.body());
            }
            return "无关紧要";

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "系统繁忙，请重试";
        }
    }

    /**
     * 语义比对：判断用户猜测是否与真相匹配
     */
    public boolean matchesAnswer(String userGuess, String truthAnswer) {
        String systemPrompt = "你的任务是判断用户的猜测是否与正确答案在语义上一致。仅回答\"正确\"或\"错误\"，不要做任何解释。";

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content",
                                "正确答案：" + truthAnswer + "\n\n用户猜测：" + userGuess)
                ),
                "temperature", 0.1,
                "max_tokens", 10
        );

        try {
            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String content = root.path("choices").get(0)
                        .path("message").path("content").asText().trim();
                return content.contains("正确");
            }
            return false;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String buildSystemPrompt(String truthAnswer) {
        return """
                你是一个海龟汤推理游戏的裁判。以下是故事的完整真相：
                
                %s
                
                玩家会向你提问关于这个故事的问题，试图推理出真相。
                
                你的回答规则（必须严格遵守）：
                1. 如果玩家的问题指向的结论与真相一致，仅回答："是"
                2. 如果玩家的问题指向的结论与真相相反或不符，仅回答："否"
                3. 如果玩家的问题与这个故事完全无关，仅回答："无关紧要"
                
                禁止做任何解释、说明或补充。
                禁止回答超过四个字的内容。
                禁止在"是"或"否"后面添加任何标点或文字。
                """.formatted(truthAnswer);
    }

    private String extractAnswer(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0)
                    .path("message").path("content").asText().trim();

            var matcher = ANSWER_PATTERN.matcher(content);
            if (matcher.find()) {
                return matcher.group(0);
            }
        } catch (Exception ignored) {
        }
        return "无关紧要";
    }
}
