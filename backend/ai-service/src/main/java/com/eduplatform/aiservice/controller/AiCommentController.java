package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.service.AiCommentService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI 评语接口。
 */
@Tag(name = "AI评语")
@RestController
@RequestMapping("/v1/ai/comment")
@RequiredArgsConstructor
public class AiCommentController {

    private final AiCommentService aiCommentService;

    @Operation(summary = "生成学生评语（智谱GLM，基于学情数据）")
    @PostMapping("/generate")
    public R<String> generate(@RequestParam Long studentId) {
        return R.ok(aiCommentService.generateComment(studentId));
    }
}
