package com.eduplatform.homework.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.homework.dto.HomeworkSaveDTO;
import com.eduplatform.homework.service.HomeworkService;
import com.eduplatform.homework.vo.HomeworkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作业接口（布置）。
 */
@Tag(name = "作业")
@RestController
@RequestMapping("/v1/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    @Operation(summary = "布置作业")
    @PostMapping
    public R<Long> create(@Valid @RequestBody HomeworkSaveDTO dto) {
        return R.ok(homeworkService.create(dto));
    }

    @Operation(summary = "按班级列出作业")
    @GetMapping
    public R<List<HomeworkVO>> list(@RequestParam Long classId) {
        return R.ok(homeworkService.listByClass(classId).stream().map(HomeworkVO::of).toList());
    }

    @Operation(summary = "作业详情")
    @GetMapping("/{id}")
    public R<HomeworkVO> get(@PathVariable Long id) {
        return R.ok(HomeworkVO.of(homeworkService.getById(id)));
    }
}
