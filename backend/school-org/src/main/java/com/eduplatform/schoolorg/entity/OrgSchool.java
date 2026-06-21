package com.eduplatform.schoolorg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学校实体（组织根）。继承 BaseEntity（审计/租户/软删）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_school")
public class OrgSchool extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 学校名称 */
    private String name;

    /** 学校编码（租户内唯一） */
    private String code;

    /** 地址 */
    private String address;

    /** 学校类型（初中=MIDDLE） */
    private String schoolType;

    /** 状态 1启用 2停用 */
    private Integer status;
}
