package com.eduplatform.homework.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 批改入参（人工批改；AI 批改将复用此结构，由 ai-service 生成 score/comment）。
 */
@Data
public class HomeworkReviewDTO {

    @NotNull(message = "提交ID不能为空")
    private Long submissionId;

    /** 得分（千分制） */
    @NotNull(message = "得分不能为空")
    private Integer score;

    /** 批改评语 */
    private String comment;
}
