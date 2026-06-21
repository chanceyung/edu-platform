package com.eduplatform.schoolorg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学生新增/修改入参。
 */
@Data
public class OrgStudentSaveDTO {

    @NotBlank(message = "学号不能为空")
    @Size(max = 64)
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64)
    private String name;

    /** 性别 1男 2女 */
    private Integer gender;

    @NotNull(message = "年级ID不能为空")
    private Long gradeId;

    @NotNull(message = "班级ID不能为空")
    private Long classId;
}
