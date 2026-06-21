package com.eduplatform.homework.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.homework.dto.HomeworkReviewDTO;
import com.eduplatform.homework.dto.HomeworkSubmissionDTO;
import com.eduplatform.homework.service.HomeworkSubmissionService;
import com.eduplatform.homework.vo.HomeworkSubmissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 作业提交接口（提交+批改）。
 */
@Tag(name = "作业提交")
@RestController
@RequestMapping("/v1/homework/submissions")
@RequiredArgsConstructor
public class HomeworkSubmissionController {

    private final HomeworkSubmissionService submissionService;

    @Operation(summary = "提交作业")
    @PostMapping
    public R<Long> submit(@Valid @RequestBody HomeworkSubmissionDTO dto) {
        return R.ok(submissionService.submit(dto));
    }

    @Operation(summary = "批改作业（人工；AI 批改复用）")
    @PostMapping("/review")
    public R<Void> review(@Valid @RequestBody HomeworkReviewDTO dto) {
        submissionService.review(dto);
        return R.ok();
    }

    @Operation(summary = "查询学生提交")
    @GetMapping
    public R<HomeworkSubmissionVO> get(@RequestParam Long homeworkId, @RequestParam Long studentId) {
        return R.ok(HomeworkSubmissionVO.of(submissionService.getByHomeworkAndStudent(homeworkId, studentId)));
    }
}
