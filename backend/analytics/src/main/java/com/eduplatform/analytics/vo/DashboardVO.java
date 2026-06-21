package com.eduplatform.analytics.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardVO {
    private int totalExams;
    private int totalQuestions;
    private int totalPapers;
    private int totalHomeworks;
    private List<ExamSummary> recentExams;

    @Data
    @Builder
    public static class ExamSummary {
        private Long examId;
        private String name;
        private int studentCount;
        private int avgScore;
    }
}
