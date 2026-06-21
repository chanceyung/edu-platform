package com.eduplatform.aiservice.controller;

import com.eduplatform.aiservice.rag.KbService;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RAG 知识库接口（入库+检索）。
 */
@Tag(name = "RAG知识库")
@RestController
@RequestMapping("/v1/ai/kb")
@RequiredArgsConstructor
public class KbController {

    private final KbService kbService;

    @Operation(summary = "入库文档（自动切块+向量化）")
    @PostMapping("/ingest")
    public R<Long> ingest(@RequestBody IngestRequest req) {
        return R.ok(kbService.ingest(req.title(), req.sourceType(), req.subject(), req.gradeLevel(), req.content()));
    }

    @Operation(summary = "检索相关知识块")
    @GetMapping("/search")
    public R<List<KbService.KbSearchResult>> search(@RequestParam @NotBlank String query,
                                                     @RequestParam(defaultValue = "3") int topK) {
        return R.ok(kbService.search(query, topK));
    }

    public record IngestRequest(String title, String sourceType, String subject, Integer gradeLevel, String content) {}
}
