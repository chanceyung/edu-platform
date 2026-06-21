package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.tool.GlmToolCaller;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Agent 接口。用户给自然语言目标，GLM 自主规划+调用工具+交付结果。
 * 这就是"老师从操作者变为委派者"的核心入口。
 */
@Tag(name = "AI Agent")
@RestController
@RequestMapping("/v1/ai/agent")
@RequiredArgsConstructor
public class AgentController {

    private final GlmToolCaller toolCaller;

    @Operation(summary = "给Agent一个目标，它自主调用工具完成（GLM function calling）")
    @PostMapping("/execute")
    public R<Map<String, Object>> execute(@RequestBody AgentRequest req) {
        return R.ok(toolCaller.executeWithTools(req.goal(), req.maxRounds() > 0 ? req.maxRounds() : 5));
    }

    public record AgentRequest(@NotBlank String goal, Integer maxRounds) {}
}
