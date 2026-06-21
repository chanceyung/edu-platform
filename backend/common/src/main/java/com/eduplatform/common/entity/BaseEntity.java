package com.eduplatform.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计基类。所有业务实体必须继承，保证审计字段与租户隔离一致。
 * 严禁在子类重复定义这些字段。
 */
@Data
public abstract class BaseEntity {

    /** 租户编号（学校=租户，行级隔离） */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private String tenantId;

    /** 创建时间（自动填充） */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（自动填充） */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人（自动填充） */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    /** 更新人（自动填充） */
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /** 逻辑删除标记 0:正常 1:删除（禁止物理删除业务数据） */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
