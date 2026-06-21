package com.eduplatform.schoolorg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.common.response.R;
import com.eduplatform.schoolorg.entity.OrgParent;
import com.eduplatform.schoolorg.mapper.OrgParentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家长接口。
 */
@Tag(name = "家长")
@RestController
@RequestMapping("/v1/org/parents")
@RequiredArgsConstructor
public class OrgParentController {

    private final OrgParentMapper parentMapper;

    @Operation(summary = "新增家长")
    @PostMapping
    public R<Long> create(@RequestBody OrgParent parent) {
        parentMapper.insert(parent);
        return R.ok(parent.getId());
    }

    @Operation(summary = "按学生ID查家长")
    @GetMapping("/by-student/{studentId}")
    public R<List<OrgParent>> byStudent(@PathVariable Long studentId) {
        return R.ok(parentMapper.selectByStudentId(studentId));
    }
}
