package com.eduplatform.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qbank_knowledge_point")
public class QbankKnowledgePoint extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private Integer status;
}
