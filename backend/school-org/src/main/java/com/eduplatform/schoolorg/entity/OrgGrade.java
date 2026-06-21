package com.eduplatform.schoolorg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 年级实体。归属于学校（schoolId）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_grade")
public class OrgGrade extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 学校ID */
    private Long schoolId;

    /** 年级名称（七年级/八年级/九年级） */
    private String name;

    /** 年级层级（7/8/9） */
    private Integer level;

    /** 排序 */
    private Integer sort;

    /** 状态 1启用 2停用 */
    private Integer status;
}
