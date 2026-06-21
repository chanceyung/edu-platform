package com.eduplatform.aiservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 智谱 GLM 网关。统一接入智谱全家桶，业务模块禁止直连智谱。
 * 用 JDK17 HttpClient（无额外依赖）。API Key 从配置注入（环境变量，严禁入库）。
 */
@Slf4j
@Component
public class ZhipuClient {

    @Value("${zhipu.api-key:}") private String apiKey;
    @Value("${zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}") private String baseUrl;
    @Value("${zhipu.models.flagship:glm-5.2}") private String flagshipModel;

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper json = new ObjectMapper();

    public record ChatMessage(String role, String content) {}
    public record ChatResult(String content, int inputTokens, int outputTokens) {}

    /** 用旗舰模型对话（复杂长程任务） */
    public ChatResult chat(List<ChatMessage> messages) {
        return chat(flagshipModel, messages);
    }

    public ChatResult chat(String model, List<ChatMessage> messages) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("智谱 API Key 未配置（环境变量 ZHIPU_API_KEY）");
        }
        try {
            String body = json.writeValueAsString(Map.of("model", model, "messages", messages));
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(120))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("智谱 API 返回 " + resp.statusCode() + ": " + resp.body());
            }
            JsonNode node = json.readTree(resp.body());
            return new ChatResult(
                    node.at("/choices/0/message/content").asText(),
                    node.at("/usage/prompt_tokens").asInt(),
                    node.at("/usage/completion_tokens").asInt()
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("调用智谱 API 失败: " + e.getMessage(), e);
        }
    }
}
