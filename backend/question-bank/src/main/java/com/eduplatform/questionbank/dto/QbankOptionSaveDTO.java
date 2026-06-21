package com.eduplatform.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QbankOptionSaveDTO {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotBlank(message = "选项内容不能为空")
    private String content;

    /** 0错误 1正确 */
    private Integer isCorrect = 0;

    private Integer sort = 0;
}
