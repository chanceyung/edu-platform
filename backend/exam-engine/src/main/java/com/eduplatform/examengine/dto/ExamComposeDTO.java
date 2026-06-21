package com.eduplatform.examengine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 组卷入参：向试卷添加题目。
 */
@Data
public class ExamComposeDTO {

    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    @NotEmpty(message = "题目不能为空")
    @Valid
    private List<ComposeItem> items;

    @Data
    public static class ComposeItem {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        @NotNull(message = "分值不能为空")
        private Integer score;
    }
}
