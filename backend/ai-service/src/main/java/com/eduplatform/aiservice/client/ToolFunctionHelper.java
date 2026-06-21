package com.eduplatform.aiservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 扩展 ZhipuClient 支持函数调用。
 * 用一个独立的辅助类，避免修改已有的 ZhipuClient（外科手术式改动）。
 */
@Slf4j
public class ToolFunctionHelper {

    private static final ObjectMapper json = new ObjectMapper();

    /**
     * 构建带 tools 的 chat 请求体。
     */
    public static String buildChatWithToolsBody(String model, List<ZhipuClient.ChatMessage> messages, String toolsJson) {
        try {
            var body = new java.util.LinkedHashMap<String, Object>();
            body.put("model", model);
            body.put("messages", messages);
            if (toolsJson != null && !toolsJson.isBlank()) {
                body.put("tools", json.readValue(toolsJson, List.class));
                body.put("tool_choice", "auto");
            }
            return json.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("构建 chat 请求体失败", e);
        }
    }
}
