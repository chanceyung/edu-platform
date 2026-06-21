package com.eduplatform.schoolorg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 年级新增/修改入参。
 */
@Data
public class OrgGradeSaveDTO {

    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @NotBlank(message = "年级名称不能为空")
    @Size(max = 64)
    private String name;

    @NotNull(message = "年级层级不能为空")
    private Integer level;

    private Integer sort = 0;
}
