package com.eduplatform.aiservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_kb_document")
public class AiKbDocument extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String title;
    private String sourceType;
    private String subject;
    private Integer gradeLevel;
    private Integer chunkCount;
    private Integer status;
}
