package com.eduplatform.schoolorg.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.schoolorg.dto.OrgGradeSaveDTO;
import com.eduplatform.schoolorg.service.OrgGradeService;
import com.eduplatform.schoolorg.vo.OrgGradeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 年级接口。
 */
@Tag(name = "年级")
@RestController
@RequestMapping("/v1/org/grades")
@RequiredArgsConstructor
public class OrgGradeController {

    private final OrgGradeService gradeService;

    @Operation(summary = "新增年级")
    @PostMapping
    public R<Long> create(@Valid @RequestBody OrgGradeSaveDTO dto) {
        return R.ok(gradeService.create(dto));
    }

    @Operation(summary = "按学校列出年级")
    @GetMapping
    public R<List<OrgGradeVO>> list(@RequestParam Long schoolId) {
        List<OrgGradeVO> list = gradeService.listBySchool(schoolId).stream().map(OrgGradeVO::of).toList();
        return R.ok(list);
    }
}
