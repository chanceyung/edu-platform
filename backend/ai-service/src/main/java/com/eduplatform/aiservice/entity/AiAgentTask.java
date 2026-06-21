package com.eduplatform.aiservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_agent_task")
public class AiAgentTask extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String goal;
    /** 0待开始 1运行中 2已完成 3已取消 4失败 */
    private Integer status;
    private Integer progressPct;
    private String currentStep;
    private String checkpointJson;
    private String resultJson;
}
