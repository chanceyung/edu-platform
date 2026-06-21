package com.eduplatform.schoolorg.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.schoolorg.dto.OrgSchoolSaveDTO;
import com.eduplatform.schoolorg.entity.OrgSchool;
import com.eduplatform.schoolorg.service.OrgSchoolService;
import com.eduplatform.schoolorg.vo.OrgSchoolVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学校接口。只做参数校验/调用 service/组装 VO；禁止业务逻辑、禁止直连 DB。
 */
@Tag(name = "学校")
@RestController
@RequestMapping("/v1/org/schools")
@RequiredArgsConstructor
public class OrgSchoolController {

    private final OrgSchoolService schoolService;

    @Operation(summary = "新增学校")
    @PostMapping
    public R<Long> create(@Valid @RequestBody OrgSchoolSaveDTO dto) {
        return R.ok(schoolService.create(dto));
    }

    @Operation(summary = "按主键查询学校")
    @GetMapping("/{id}")
    public R<OrgSchoolVO> get(@PathVariable Long id) {
        return R.ok(OrgSchoolVO.of(schoolService.getById(id)));
    }

    @Operation(summary = "学校列表")
    @GetMapping
    public R<List<OrgSchoolVO>> list() {
        List<OrgSchoolVO> list = schoolService.list().stream().map(OrgSchoolVO::of).toList();
        return R.ok(list);
    }
}
