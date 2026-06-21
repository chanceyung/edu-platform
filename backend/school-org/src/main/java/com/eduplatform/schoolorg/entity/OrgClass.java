package com.eduplatform.schoolorg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体。归属于年级（gradeId）与学校（schoolId）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_class")
public class OrgClass extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long schoolId;
    private Long gradeId;

    /** 班级名称（如 1班） */
    private String name;

    /** 入学年份 */
    private Integer entryYear;

    /** 班主任教师ID（待 org_teacher 关联） */
    private Long headTeacherId;

    private Integer sort;
    private Integer status;
}
