package com.eduplatform.examengine.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.examengine.dto.ExamRecordSubmitDTO;
import com.eduplatform.examengine.service.ExamRecordService;
import com.eduplatform.examengine.vo.ExamRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 答卷接口（提交+判分）。
 */
@Tag(name = "答卷/判分")
@RestController
@RequestMapping("/v1/exam/records")
@RequiredArgsConstructor
public class ExamRecordController {

    private final ExamRecordService recordService;

    @Operation(summary = "提交答卷（客观题自动判分）")
    @PostMapping("/submit")
    public R<Long> submit(@Valid @RequestBody ExamRecordSubmitDTO dto) {
        return R.ok(recordService.submit(dto));
    }

    @Operation(summary = "查询学生答卷")
    @GetMapping
    public R<ExamRecordVO> get(@RequestParam Long examId, @RequestParam Long studentId) {
        return R.ok(ExamRecordVO.of(recordService.getByExamAndStudent(examId, studentId)));
    }
}
