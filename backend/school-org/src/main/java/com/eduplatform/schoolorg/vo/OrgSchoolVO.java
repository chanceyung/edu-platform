package com.eduplatform.schoolorg.vo;

import com.eduplatform.schoolorg.entity.OrgSchool;
import lombok.Builder;
import lombok.Data;

/**
 * 学校出参 VO。返回前端专用，禁止用 entity。
 */
@Data
@Builder
public class OrgSchoolVO {

    private Long id;
    private String name;
    private String code;
    private String address;
    private String schoolType;
    private Integer status;

    public static OrgSchoolVO of(OrgSchool e) {
        if (e == null) {
            return null;
        }
        return OrgSchoolVO.builder()
                .id(e.getId())
                .name(e.getName())
                .code(e.getCode())
                .address(e.getAddress())
                .schoolType(e.getSchoolType())
                .status(e.getStatus())
                .build();
    }
}
