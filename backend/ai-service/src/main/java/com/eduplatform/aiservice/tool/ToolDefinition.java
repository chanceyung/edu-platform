package com.eduplatform.aiservice.tool;

import java.util.List;
import java.util.Map;

/**
 * MCP 工具定义（参照 ai-platform/README §三 的 YAML 格式，Java 版）。
 * 每个工具声明 name/module/side_effect/permissions/input_schema。
 */
public record ToolDefinition(
        String name,
        String module,
        String description,
        SideEffect sideEffect,
        boolean humanInTheLoop,
        List<String> requiredRoles,
        Map<String, Class<?>> inputSchema,
        ToolExecutor executor
) {
    /** 副作用等级（信任内核用） */
    public enum SideEffect {
        READ_ONLY,      // 只读，自动放行
        WRITE,          // 写入，记日志
        DESTRUCTIVE,    // 删除/破坏性，强制人工确认
        EXTERNAL_NOTIFY  // 对外通知（发家长/发通知），强制人工确认
    }

    /** 工具执行器函数式接口 */
    @FunctionalInterface
    public interface ToolExecutor {
        Map<String, Object> execute(Map<String, Object> input) throws Exception;
    }
}
