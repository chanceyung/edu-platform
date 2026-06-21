package com.eduplatform.questionbank.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.questionbank.dto.QbankOptionSaveDTO;
import com.eduplatform.questionbank.service.QbankOptionService;
import com.eduplatform.questionbank.vo.QbankOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目选项接口。
 */
@Tag(name = "题目选项")
@RestController
@RequestMapping("/v1/qbank/options")
@RequiredArgsConstructor
public class QbankOptionController {

    private final QbankOptionService optionService;

    @Operation(summary = "新增选项")
    @PostMapping
    public R<Long> create(@Valid @RequestBody QbankOptionSaveDTO dto) {
        return R.ok(optionService.create(dto));
    }

    @Operation(summary = "按题目列出选项")
    @GetMapping
    public R<List<QbankOptionVO>> list(@RequestParam Long questionId) {
        List<QbankOptionVO> list = optionService.listByQuestion(questionId)
                .stream().map(QbankOptionVO::of).toList();
        return R.ok(list);
    }
}
