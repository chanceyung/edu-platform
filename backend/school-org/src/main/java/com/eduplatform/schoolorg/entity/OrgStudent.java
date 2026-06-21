package com.eduplatform.schoolorg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生学籍实体。
 * 命名规范：表名 org_ 前缀；继承 BaseEntity（审计/租户/软删）。
 * 仅示范字段结构，完整字段按需求补全。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_student")
public class OrgStudent extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 学号 */
    private String studentNo;

    /** 姓名 */
    private String name;

    /** 性别 1男 2女（枚举映射，禁止魔法数字裸用） */
    private Integer gender;

    /** 年级ID */
    private Long gradeId;

    /** 班级ID */
    private Long classId;

    /** 状态 1在读 2毕业 3转学 */
    private Integer status;
}
