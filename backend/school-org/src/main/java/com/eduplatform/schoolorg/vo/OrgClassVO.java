package com.eduplatform.schoolorg.vo;

import com.eduplatform.schoolorg.entity.OrgClass;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrgClassVO {

    private Long id;
    private Long schoolId;
    private Long gradeId;
    private String name;
    private Integer entryYear;
    private Long headTeacherId;
    private Integer sort;
    private Integer status;

    public static OrgClassVO of(OrgClass e) {
        if (e == null) return null;
        return OrgClassVO.builder()
                .id(e.getId())
                .schoolId(e.getSchoolId())
                .gradeId(e.getGradeId())
                .name(e.getName())
                .entryYear(e.getEntryYear())
                .headTeacherId(e.getHeadTeacherId())
                .sort(e.getSort())
                .status(e.getStatus())
                .build();
    }
}
