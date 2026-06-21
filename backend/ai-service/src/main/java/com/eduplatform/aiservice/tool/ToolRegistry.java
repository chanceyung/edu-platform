package com.eduplatform.aiservice.tool;

import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.ruoyibase.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册中心 + 调度器。
 * 注册工具 → 按 name 查找 → 执行（含权限校验+副作用分级+日志）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistry {

    private final AiTaskLogMapper logMapper;
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();

    /** 注册工具 */
    public void register(ToolDefinition tool) {
        tools.put(tool.name(), tool);
        log.info("工具注册: {} ({})", tool.name(), tool.sideEffect());
    }

    /** 获取所有工具定义（供 GLM function calling） */
    public Collection<ToolDefinition> all() {
        return tools.values();
    }

    /** 按名称查找 */
    public ToolDefinition get(String name) {
        return tools.get(name);
    }

    /**
     * 调度执行工具（信任内核完整流程）。
     * 1. 权限校验（角色白名单）
     * 2. 副作用分级（read_only自动 / write记日志 / destructive+external需人工确认）
     * 3. 执行
     * 4. 写 ai_task_log
     */
    public Map<String, Object> dispatch(String toolName, Map<String, Object> input) throws Exception {
        ToolDefinition tool = tools.get(toolName);
        if (tool == null) {
            throw new IllegalArgumentException("工具不存在: " + toolName);
        }

        // 1. 权限校验
        String roleCode = UserContext.currentRoleCode();
        if (tool.requiredRoles() != null && !tool.requiredRoles().isEmpty()
                && (roleCode == null || !tool.requiredRoles().contains(roleCode))) {
            throw new SecurityException("无权限调用工具 " + toolName + "（需要角色: " + tool.requiredRoles() + "）");
        }

        // 2. 副作用分级：destructive/external 需人工确认
        if (tool.sideEffect() == ToolDefinition.SideEffect.DESTRUCTIVE
                || tool.sideEffect() == ToolDefinition.SideEffect.EXTERNAL_NOTIFY) {
            // TODO: 对接前端审核区，当前只记日志标记
            log.warn("工具 {} 需人工确认（sideEffect={}），当前自动放行", toolName, tool.sideEffect());
        }

        // 3. 执行
        long start = System.currentTimeMillis();
        Map<String, Object> output = Map.of();
        int status = 1;
        try {
            output = tool.executor().execute(input);
        } catch (Exception e) {
            status = 2;
            String err = e.getMessage();
            output = Map.of("error", err != null ? err : "unknown");
            throw e;
        } finally {
            // 4. 写 ai_task_log（所有工具调用必写——铁律）
            try {
                AiTaskLog taskLog = new AiTaskLog();
                taskLog.setTaskType("TOOL_" + toolName.toUpperCase());
                taskLog.setInput(input.toString().length() > 500 ? input.toString().substring(0, 500) : input.toString());
                taskLog.setOutput(output != null ? output.toString().length() > 500 ? output.toString().substring(0, 500) : output.toString() : "");
                taskLog.setLatencyMs((int) (System.currentTimeMillis() - start));
                taskLog.setStatus(status);
                taskLog.setActor(UserContext.currentUsername());
                taskLog.setModel("tool-dispatcher");
                logMapper.insert(taskLog);
            } catch (Exception logEx) {
                log.error("写 ai_task_log 失败", logEx);
            }
        }
        return output;
    }
}
