package com.eduplatform.questionbank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 题目新增/修改入参。题型/分值/题干校验。
 */
@Data
public class QbankQuestionSaveDTO {

    @NotNull(message = "题型不能为空")
    @Min(value = 1, message = "题型非法")
    @Max(value = 5, message = "题型非法")
    private Integer type;

    private Long subjectId;
    private Integer gradeLevel;

    @NotNull(message = "分值不能为空")
    @Positive(message = "分值必须大于0")
    private Integer score;

    private Integer difficult;
    private Long knowledgePointId;

    @NotBlank(message = "题干不能为空")
    private String stem;

    private String answer;
    private String analysis;
}
