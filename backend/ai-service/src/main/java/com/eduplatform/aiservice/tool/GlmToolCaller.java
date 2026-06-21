package com.eduplatform.aiservice.tool;

import com.eduplatform.aiservice.client.ZhipuClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GLM 函数调用引擎。让 GLM 知道有哪些工具可用，解析 tool_calls 并调度。
 * 这是 Agent 的"大脑→手脚"连接——GLM 决定调用什么工具，ToolRegistry 执行。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlmToolCaller {

    private final ZhipuClient zhipu;
    private final ToolRegistry toolRegistry;
    private final ObjectMapper json = new ObjectMapper();

    /**
     * 给 GLM 一个任务，它自主决定调用哪些工具来完成。
     * @param userGoal 用户的自然语言目标（如"帮我批改作业1的所有学生"）
     * @param maxRounds 最大工具调用轮数（防死循环）
     * @return 最终对话内容 + 工具调用记录
     */
    public Map<String, Object> executeWithTools(String userGoal, int maxRounds) {
        List<Map<String, Object>> toolCallLog = new ArrayList<>();
        List<ZhipuClient.ChatMessage> messages = new ArrayList<>(List.of(
                new ZhipuClient.ChatMessage("system",
                        "你是教学助手AI。你可以调用工具帮老师完成任务。只调用必要的工具。"),
                new ZhipuClient.ChatMessage("user", userGoal)
        ));

        for (int round = 0; round < maxRounds; round++) {
            // 调 GLM（带 tools）
            ZhipuClient.ChatResult result = zhipu.chatWithTools(messages, buildToolsJson());
            JsonNode content = parseJson(result.content());

            // GLM 决定不调用工具 → 返回最终答案
            if (content == null || !content.has("tool_calls")) {
                log.info("GLM 返回最终答案（第{}轮）", round + 1);
                return Map.of("answer", result.content(), "toolCalls", toolCallLog, "rounds", round + 1);
            }

            // 解析 tool_calls 并逐个执行
            ArrayNode toolCalls = (ArrayNode) content.get("tool_calls");
            for (JsonNode tc : toolCalls) {
                String toolName = tc.get("name").asText();
                Map<String, Object> args = json.convertValue(tc.get("arguments"), Map.class);
                log.info("第{}轮 GLM 调用工具: {} args={}", round + 1, toolName, args);

                Map<String, Object> toolResult;
                try {
                    toolResult = toolRegistry.dispatch(toolName, args);
                } catch (Exception e) {
                    toolResult = Map.of("error", e.getMessage());
                }
                toolCallLog.add(Map.of("round", round + 1, "tool", toolName, "args", args, "result", toolResult));

                // 把工具结果反馈给 GLM（让它知道执行情况）
                messages.add(new ZhipuClient.ChatMessage("assistant", result.content()));
                messages.add(new ZhipuClient.ChatMessage("user",
                        "工具 " + toolName + " 执行结果: " + safeToJson(toolResult)));
            }
        }

        log.warn("达到最大轮数 {}，停止工具调用", maxRounds);
        return Map.of("answer", "达到最大工具调用轮数", "toolCalls", toolCallLog, "rounds", maxRounds);
    }

    /** 构建 GLM function calling 的 tools 参数 */
    private String buildToolsJson() {
        ArrayNode tools = json.createArrayNode();
        for (ToolDefinition tool : toolRegistry.all()) {
            ObjectNode toolNode = tools.addObject();
            toolNode.put("type", "function");
            ObjectNode fn = toolNode.putObject("function");
            fn.put("name", tool.name());
            fn.put("description", tool.description());
            ObjectNode params = fn.putObject("parameters");
            params.put("type", "object");
            ObjectNode props = params.putObject("properties");
            ArrayNode required = params.putArray("required");
            if (tool.inputSchema() != null) {
                tool.inputSchema().forEach((key, type) -> {
                    String jsonType = type == Long.class || type == Integer.class ? "integer" : "string";
                    props.putObject(key).put("type", jsonType);
                    required.add(key);
                });
            }
        }
        return tools.toString();
    }

    private String safeToJson(Object obj) {
        try {
            return json.writeValueAsString(obj);
        } catch (Exception e) {
            return obj != null ? obj.toString() : "null";
        }
    }

    private JsonNode parseJson(String text) {
        if (text == null) return null;
        try {
            return json.readTree(text);
        } catch (Exception e) {
            // GLM 可能返回非纯 JSON（含 markdown），尝试提取
            int start = text.indexOf('{');
            int end = text.lastIndexOf('}');
            if (start >= 0 && end > start) {
                try {
                    return json.readTree(text.substring(start, end + 1));
                } catch (Exception ignored) {}
            }
            return null;
        }
    }
}
