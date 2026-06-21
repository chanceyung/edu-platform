package com.eduplatform.examengine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 提交答卷入参。
 */
@Data
public class ExamRecordSubmitDTO {

    @NotNull(message = "考试ID不能为空")
    private Long examId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotEmpty(message = "答案不能为空")
    @Valid
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;
        /** 学生答案：客观题为选项ID JSON数组如 "[123,456]"；主观题为文本 */
        private String answer;
    }
}
