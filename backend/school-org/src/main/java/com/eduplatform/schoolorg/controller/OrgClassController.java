package com.eduplatform.schoolorg.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.schoolorg.dto.OrgClassSaveDTO;
import com.eduplatform.schoolorg.service.OrgClassService;
import com.eduplatform.schoolorg.vo.OrgClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班级")
@RestController
@RequestMapping("/v1/org/classes")
@RequiredArgsConstructor
public class OrgClassController {

    private final OrgClassService classService;

    @Operation(summary = "新增班级")
    @PostMapping
    public R<Long> create(@Valid @RequestBody OrgClassSaveDTO dto) {
        return R.ok(classService.create(dto));
    }

    @Operation(summary = "按年级列出班级")
    @GetMapping
    public R<List<OrgClassVO>> list(@RequestParam Long gradeId) {
        List<OrgClassVO> list = classService.listByGrade(gradeId).stream().map(OrgClassVO::of).toList();
        return R.ok(list);
    }
}
