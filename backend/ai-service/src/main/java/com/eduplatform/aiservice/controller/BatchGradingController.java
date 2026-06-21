package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.service.impl.AiBatchGradingServiceImpl;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 批量批改接口。
 */
@Tag(name = "AI批量批改")
@RestController
@RequestMapping("/v1/ai/batch-grading")
@RequiredArgsConstructor
public class BatchGradingController {

    private final AiBatchGradingServiceImpl batchService;

    @Operation(summary = "批量批改全班作业（单学生失败不阻断）")
    @PostMapping("/grade-all")
    public R<Map<String, Object>> gradeAll(@RequestParam Long homeworkId) {
        return R.ok(batchService.gradeBatch(homeworkId));
    }
}
