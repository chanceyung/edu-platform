package com.eduplatform.schoolorg.vo;

import com.eduplatform.schoolorg.entity.OrgGrade;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrgGradeVO {

    private Long id;
    private Long schoolId;
    private String name;
    private Integer level;
    private Integer sort;
    private Integer status;

    public static OrgGradeVO of(OrgGrade e) {
        if (e == null) return null;
        return OrgGradeVO.builder()
                .id(e.getId())
                .schoolId(e.getSchoolId())
                .name(e.getName())
                .level(e.getLevel())
                .sort(e.getSort())
                .status(e.getStatus())
                .build();
    }
}
