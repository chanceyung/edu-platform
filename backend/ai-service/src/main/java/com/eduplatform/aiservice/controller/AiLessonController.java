package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.service.AiLessonService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI 备课接口。
 */
@Tag(name = "AI备课")
@RestController
@RequestMapping("/v1/ai/lesson")
@RequiredArgsConstructor
public class AiLessonController {

    private final AiLessonService aiLessonService;

    @Operation(summary = "生成教案（智谱GLM，含目标/重点/过程/练习）")
    @PostMapping("/generate")
    public R<String> generate(@RequestParam String topic, @RequestParam Integer gradeLevel) {
        return R.ok(aiLessonService.generateLesson(topic, gradeLevel));
    }
}
