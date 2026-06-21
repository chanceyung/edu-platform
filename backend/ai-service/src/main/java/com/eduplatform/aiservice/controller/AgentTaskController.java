package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.entity.AiAgentTask;
import com.eduplatform.aiservice.orchestrator.AgentTaskService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * Agent 任务接口（SSE 流式进度 + 任务查询）。
 */
@Tag(name = "Agent任务")
@RestController
@RequestMapping("/v1/ai/agent-tasks")
@RequiredArgsConstructor
public class AgentTaskController {

    private final AgentTaskService service;

    @Operation(summary = "创建并执行Agent任务（SSE流式进度推送）")
    @PostMapping("/run")
    public SseEmitter run(@RequestBody RunRequest req) {
        return service.createAndRun(req.goal());
    }

    @Operation(summary = "任务详情")
    @GetMapping("/{id}")
    public R<AiAgentTask> get(@PathVariable Long id) {
        return R.ok(service.getById(id));
    }

    @Operation(summary = "最近任务列表")
    @GetMapping
    public R<List<AiAgentTask>> list() {
        return R.ok(service.listRecent());
    }

    public record RunRequest(@NotBlank String goal) {}
}
