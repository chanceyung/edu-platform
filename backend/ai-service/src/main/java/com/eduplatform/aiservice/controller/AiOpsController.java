package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.eval.EvalService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 运营看板接口。
 */
@Tag(name = "AI运营看板")
@RestController
@RequestMapping("/v1/ai/ops")
@RequiredArgsConstructor
public class AiOpsController {

    private final EvalService evalService;

    @Operation(summary = "AI运营概览（调用量/Token/成本/失败率/延迟/类型分布）")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        return R.ok(evalService.dashboard());
    }

    @Operation(summary = "评测集回归（金标准准确率）")
    @PostMapping("/eval")
    public R<Map<String, Object>> eval() {
        return R.ok(evalService.runEval());
    }
}
