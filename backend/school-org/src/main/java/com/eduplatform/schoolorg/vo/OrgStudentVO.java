package com.eduplatform.schoolorg.vo;

import com.eduplatform.schoolorg.entity.OrgStudent;
import lombok.Builder;
import lombok.Data;

/**
 * 学生出参 VO。返回前端专用，禁止用 entity。
 * 实际转换应走 MapStruct（待引入），这里用静态工厂示意。
 */
@Data
@Builder
public class OrgStudentVO {

    private Long id;
    private String studentNo;
    private String name;
    private Integer gender;
    private Long gradeId;
    private Long classId;

    public static OrgStudentVO of(OrgStudent e) {
        if (e == null) {
            return null;
        }
        return OrgStudentVO.builder()
                .id(e.getId())
                .studentNo(e.getStudentNo())
                .name(e.getName())
                .gender(e.getGender())
                .gradeId(e.getGradeId())
                .classId(e.getClassId())
                .build();
    }
}
