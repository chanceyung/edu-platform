package com.eduplatform.schoolorg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrgClassSaveDTO {

    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @NotNull(message = "年级ID不能为空")
    private Long gradeId;

    @NotBlank(message = "班级名称不能为空")
    @Size(max = 64)
    private String name;

    private Integer entryYear;
    private Long headTeacherId;
    private Integer sort = 0;
}
