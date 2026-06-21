package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.service.AiGradingService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI 批改接口。调用智谱 GLM 评分主观题，全程写 ai_task_log（信任内核）。
 */
@Tag(name = "AI批改")
@RestController
@RequestMapping("/v1/ai/grading")
@RequiredArgsConstructor
public class AiGradingController {

    private final AiGradingService aiGradingService;

    @Operation(summary = "AI批改作业（智谱GLM，可追溯）")
    @PostMapping("/grade")
    public R<Void> grade(@RequestParam Long homeworkId, @RequestParam Long studentId) {
        aiGradingService.grade(homeworkId, studentId);
        return R.ok();
    }
}
