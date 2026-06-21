package com.eduplatform.examengine.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.examengine.dto.ExamSaveDTO;
import com.eduplatform.examengine.entity.Exam;
import com.eduplatform.examengine.service.ExamService;
import com.eduplatform.examengine.vo.ExamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试接口（发布）。
 */
@Tag(name = "考试")
@RestController
@RequestMapping("/v1/exam/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @Operation(summary = "发布考试")
    @PostMapping
    public R<Long> create(@Valid @RequestBody ExamSaveDTO dto) {
        return R.ok(examService.create(dto));
    }

    @Operation(summary = "考试列表")
    @GetMapping
    public R<List<ExamVO>> list(@RequestParam(required = false) Long classId) {
        List<Exam> exams = classId == null ? examService.list() : examService.listByClass(classId);
        return R.ok(exams.stream().map(ExamVO::of).toList());
    }

    @Operation(summary = "考试详情")
    @GetMapping("/{id}")
    public R<ExamVO> get(@PathVariable Long id) {
        return R.ok(ExamVO.of(examService.getById(id)));
    }
}
