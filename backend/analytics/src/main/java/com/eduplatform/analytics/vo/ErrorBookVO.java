package com.eduplatform.analytics.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ErrorBookVO {
    private Long studentId;
    private int totalErrors;
    private List<ErrorItem> errors;

    @Data
    @Builder
    public static class ErrorItem {
        private Long questionId;
        private Long examId;
        private String answer;     // 学生答案
        private Integer doRight;   // 0=错
    }
}
