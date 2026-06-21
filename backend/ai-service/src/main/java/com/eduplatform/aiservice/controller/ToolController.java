package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.tool.ToolDefinition;
import com.eduplatform.aiservice.tool.ToolRegistry;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MCP 工具接口（查看可用工具列表 + 手动调度工具）。
 */
@Tag(name = "MCP工具")
@RestController
@RequestMapping("/v1/ai/tools")
@RequiredArgsConstructor
public class ToolController {

    private final ToolRegistry registry;

    @Operation(summary = "列出所有已注册工具（Agent 可用的手脚）")
    @GetMapping
    public R<List<Map<String, Object>>> list() {
        return R.ok(registry.all().stream().map(t -> Map.<String, Object>of(
                "name", t.name(),
                "module", t.module(),
                "description", t.description(),
                "sideEffect", t.sideEffect().name(),
                "humanInTheLoop", t.humanInTheLoop(),
                "requiredRoles", t.requiredRoles() != null ? t.requiredRoles() : List.of()
        )).collect(Collectors.toList()));
    }

    @Operation(summary = "手动调度工具（测试用，正式由 GLM function calling 触发）")
    @PostMapping("/dispatch")
    public R<Map<String, Object>> dispatch(@RequestBody DispatchRequest req) throws Exception {
        return R.ok(registry.dispatch(req.toolName(), req.input()));
    }

    public record DispatchRequest(String toolName, Map<String, Object> input) {}
}
