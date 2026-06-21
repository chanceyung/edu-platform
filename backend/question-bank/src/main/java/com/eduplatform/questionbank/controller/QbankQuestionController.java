package com.eduplatform.questionbank.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.questionbank.dto.QbankQuestionSaveDTO;
import com.eduplatform.questionbank.service.QbankQuestionService;
import com.eduplatform.questionbank.vo.QbankQuestionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目接口。
 */
@Tag(name = "题目")
@RestController
@RequestMapping("/v1/qbank/questions")
@RequiredArgsConstructor
public class QbankQuestionController {

    private final QbankQuestionService questionService;

    @Operation(summary = "新增题目")
    @PostMapping
    public R<Long> create(@Valid @RequestBody QbankQuestionSaveDTO dto) {
        return R.ok(questionService.create(dto));
    }

    @Operation(summary = "题目列表（按学科/年级/题型过滤）")
    @GetMapping
    public R<List<QbankQuestionVO>> list(@RequestParam(required = false) Long subjectId,
                                         @RequestParam(required = false) Integer gradeLevel,
                                         @RequestParam(required = false) Integer type) {
        List<QbankQuestionVO> list = questionService.list(subjectId, gradeLevel, type)
                .stream().map(QbankQuestionVO::of).toList();
        return R.ok(list);
    }

    @Operation(summary = "按主键查询题目")
    @GetMapping("/{id}")
    public R<QbankQuestionVO> get(@PathVariable Long id) {
        return R.ok(QbankQuestionVO.of(questionService.getById(id)));
    }
}
