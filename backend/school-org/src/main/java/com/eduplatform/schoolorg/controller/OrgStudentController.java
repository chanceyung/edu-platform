package com.eduplatform.schoolorg.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.schoolorg.dto.OrgStudentSaveDTO;
import com.eduplatform.schoolorg.entity.OrgStudent;
import com.eduplatform.schoolorg.service.OrgStudentService;
import com.eduplatform.schoolorg.vo.OrgStudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学生学籍接口。
 */
@Tag(name = "学生学籍")
@RestController
@RequestMapping("/v1/org/students")
@RequiredArgsConstructor
public class OrgStudentController {

    private final OrgStudentService studentService;

    @Operation(summary = "新增学生")
    @PostMapping
    public R<Long> create(@Valid @RequestBody OrgStudentSaveDTO dto) {
        OrgStudent student = new OrgStudent();
        student.setStudentNo(dto.getStudentNo());
        student.setName(dto.getName());
        student.setGender(dto.getGender());
        student.setGradeId(dto.getGradeId());
        student.setClassId(dto.getClassId());
        student.setStatus(1);
        return R.ok(studentService.create(student));
    }

    @Operation(summary = "按学号查询学生")
    @GetMapping("/{studentNo}")
    public R<OrgStudentVO> getByStudentNo(@PathVariable @NotBlank String studentNo) {
        OrgStudent student = studentService.getByStudentNo(studentNo);
        return R.ok(OrgStudentVO.of(student));
    }
}
