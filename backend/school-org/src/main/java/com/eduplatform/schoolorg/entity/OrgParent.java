package com.eduplatform.schoolorg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_parent")
public class OrgParent extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String phone;
    private String relationship;
    private Long userId;
}
