package com.eduplatform.schoolorg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_teacher")
public class OrgTeacher extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long schoolId;
    private String name;
    private String employeeNo;
    private String phone;
    private String subject;
    private String title;
    private Integer status;
    private Long userId;
}
