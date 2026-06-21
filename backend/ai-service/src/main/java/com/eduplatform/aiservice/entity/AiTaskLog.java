package com.eduplatform.aiservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 调用日志（信任内核）。所有 AI 操作必须记录，可审计/可回溯。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_task_log")
public class AiTaskLog extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 任务类型 GRADING批改 / COMPOSE组卷 / LESSON备课 等 */
    private String taskType;

    /** 模型 glm-5.2 / glm-4-air 等 */
    private String model;

    /** 输入摘要（JSON，脱敏） */
    private String input;

    /** 输出摘要（JSON） */
    private String output;

    private Integer inputTokens;
    private Integer outputTokens;
    /** 耗时毫秒 */
    private Integer latencyMs;
    /** 1成功 2失败 */
    private Integer status;
    /** 触发者 */
    private String actor;
    /** 业务关联ID（如 submissionId） */
    private Long bizRefId;
}
