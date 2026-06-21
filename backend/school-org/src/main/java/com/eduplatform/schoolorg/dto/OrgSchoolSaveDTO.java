package com.eduplatform.schoolorg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学校新增/修改入参。校验注解强制约束，Controller 用 @Valid 触发。
 */
@Data
public class OrgSchoolSaveDTO {

    @NotBlank(message = "学校名称不能为空")
    @Size(max = 128, message = "学校名称最长128字符")
    private String name;

    @NotBlank(message = "学校编码不能为空")
    @Size(max = 64, message = "学校编码最长64字符")
    private String code;

    @Size(max = 255, message = "地址最长255字符")
    private String address;

    /** 学校类型，默认 MIDDLE（初中） */
    private String schoolType = "MIDDLE";
}
