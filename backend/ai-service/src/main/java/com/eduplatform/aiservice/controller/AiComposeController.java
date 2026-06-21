package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.service.AiComposeService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI 智能组卷接口。
 */
@Tag(name = "AI智能组卷")
@RestController
@RequestMapping("/v1/ai/compose")
@RequiredArgsConstructor
public class AiComposeController {

    private final AiComposeService aiComposeService;

    @Operation(summary = "自动组卷（按年级/题型/难度从题库选题）")
    @PostMapping("/auto")
    public R<Integer> autoCompose(@RequestParam Long paperId,
                                  @RequestParam(required = false) Integer gradeLevel,
                                  @RequestParam(required = false) Integer type,
                                  @RequestParam(required = false) Integer difficulty,
                                  @RequestParam(defaultValue = "10") int count) {
        return R.ok(aiComposeService.autoCompose(paperId, gradeLevel, type, difficulty, count));
    }
}
