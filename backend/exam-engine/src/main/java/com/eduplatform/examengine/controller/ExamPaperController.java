package com.eduplatform.examengine.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.examengine.dto.ExamComposeDTO;
import com.eduplatform.examengine.dto.ExamPaperSaveDTO;
import com.eduplatform.examengine.service.ExamPaperService;
import com.eduplatform.examengine.vo.ExamPaperDetailVO;
import com.eduplatform.examengine.vo.ExamPaperVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 试卷接口（含组卷）。
 */
@Tag(name = "试卷/组卷")
@RestController
@RequestMapping("/v1/exam/papers")
@RequiredArgsConstructor
public class ExamPaperController {

    private final ExamPaperService paperService;

    @Operation(summary = "新建试卷")
    @PostMapping
    public R<Long> create(@Valid @RequestBody ExamPaperSaveDTO dto) {
        return R.ok(paperService.create(dto));
    }

    @Operation(summary = "组卷：向试卷添加题目")
    @PostMapping("/compose")
    public R<Void> compose(@Valid @RequestBody ExamComposeDTO dto) {
        paperService.compose(dto);
        return R.ok();
    }

    @Operation(summary = "试卷列表")
    @GetMapping
    public R<List<ExamPaperVO>> list() {
        return R.ok(paperService.list().stream().map(ExamPaperVO::of).toList());
    }

    @Operation(summary = "试卷详情（含题目内容+选项）")
    @GetMapping("/{id}/detail")
    public R<ExamPaperDetailVO> detail(@PathVariable Long id) {
        return R.ok(paperService.getDetail(id));
    }

    @Operation(summary = "试卷题目ID列表")
    @GetMapping("/{id}/questions")
    public R<List<Long>> questions(@PathVariable Long id) {
        return R.ok(paperService.listQuestionIds(id));
    }
}
