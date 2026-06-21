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
 * 模型三级分层：flagship(glm-5.2 最高级) / standard(glm-5.1 一般) / lite(glm-4.7 轻量)。
 * 用 JDK17 HttpClient（无额外依赖）。API Key 从配置注入（环境变量，严禁入库）。
 */
@Slf4j
@Component
public class ZhipuClient {

    @Value("${zhipu.api-key:}") private String apiKey;
    @Value("${zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}") private String baseUrl;
    @Value("${zhipu.models.flagship:glm-5.2}") private String flagshipModel;
    @Value("${zhipu.models.standard:glm-5.1}") private String standardModel;
    @Value("${zhipu.models.lite:glm-4.7}") private String liteModel;

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper json = new ObjectMapper();

    public record ChatMessage(String role, String content) {}
    public record ChatResult(String content, int inputTokens, int outputTokens) {}

    // ===== 三级便捷方法 =====

    /** 最高级 GLM-5.2：Agent编排/备课/复杂推理 */
    public ChatResult chat(List<ChatMessage> messages) {
        return doChat(flagshipModel, messages, null);
    }

    /** 一般 GLM-5.1：AI批改/AI评语/学情分析 */
    public ChatResult chatStandard(List<ChatMessage> messages) {
        return doChat(standardModel, messages, null);
    }

    /** 轻量 GLM-4.7：简单问答/消息生成/降本 */
    public ChatResult chatLite(List<ChatMessage> messages) {
        return doChat(liteModel, messages, null);
    }

    /** 指定模型 */
    public ChatResult chat(String model, List<ChatMessage> messages) {
        return doChat(model, messages, null);
    }

    /** 带工具的对话（GLM function calling，用旗舰模型） */
    public ChatResult chatWithTools(List<ChatMessage> messages, String toolsJson) {
        return doChat(flagshipModel, messages, toolsJson);
    }

    // ===== 获取模型名（业务层记日志用） =====

    public String getFlagshipModel() { return flagshipModel; }
    public String getStandardModel() { return standardModel; }
    public String getLiteModel() { return liteModel; }

    @Value("${zhipu.models.embedding:embedding-3}") private String embeddingModel;

    /** 向量化文本（Embedding-3，2048维） */
    @SuppressWarnings("unchecked")
    public String embed(String text) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("智谱 API Key 未配置");
        }
        try {
            String body = json.writeValueAsString(Map.of("model", embeddingModel, "input", text));
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/embeddings"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("Embedding API 返回 " + resp.statusCode() + ": " + resp.body());
            }
            JsonNode node = json.readTree(resp.body());
            return json.writeValueAsString(node.at("/data/0/embedding"));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Embedding 调用失败: " + e.getMessage(), e);
        }
    }
    // ===== 核心调用 =====

    private ChatResult doChat(String model, List<ChatMessage> messages, String toolsJson) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("智谱 API Key 未配置（环境变量 ZHIPU_API_KEY）");
        }
        try {
            String body;
            if (toolsJson != null && !toolsJson.isBlank()) {
                body = ToolFunctionHelper.buildChatWithToolsBody(model, messages, toolsJson);
            } else {
                body = json.writeValueAsString(Map.of("model", model, "messages", messages));
            }
            log.debug("调用智谱 model={} msgs={}", model, messages.size());
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
