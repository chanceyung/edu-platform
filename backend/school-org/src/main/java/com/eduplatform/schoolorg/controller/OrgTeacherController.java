package com.eduplatform.schoolorg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.common.response.R;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.entity.OrgTeacher;
import com.eduplatform.schoolorg.mapper.OrgTeacherMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师接口（含任课班级查询）。
 */
@Tag(name = "教师")
@RestController
@RequestMapping("/v1/org/teachers")
@RequiredArgsConstructor
public class OrgTeacherController {

    private final OrgTeacherMapper teacherMapper;

    @Operation(summary = "新增教师")
    @PostMapping
    public R<Long> create(@RequestBody OrgTeacher teacher) {
        if (teacher.getStatus() == null) teacher.setStatus(1);
        teacherMapper.insert(teacher);
        return R.ok(teacher.getId());
    }

    @Operation(summary = "按学校列出教师")
    @GetMapping
    public R<List<OrgTeacher>> list(@RequestParam Long schoolId) {
        return R.ok(teacherMapper.selectList(new LambdaQueryWrapper<OrgTeacher>()
                .eq(OrgTeacher::getSchoolId, schoolId)));
    }

    @Operation(summary = "查询教师任课的班级列表")
    @GetMapping("/{teacherId}/classes")
    public R<List<OrgClass>> teacherClasses(@PathVariable Long teacherId) {
        return R.ok(teacherMapper.selectClassesByTeacherId(teacherId));
    }
}
