package com.eduplatform.analytics.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 个人学情分析（某学生跨考试/作业）。
 */
@Data
@Builder
public class StudentAnalysisVO {

    private Long studentId;
    /** 考试平均得分率（0-100） */
    private double examAvgRate;
    /** 作业平均得分率（0-100） */
    private double homeworkAvgRate;
    /** 是否掉队预警（综合得分率<60%） */
    private boolean atRisk;
    /** 最近考试记录 */
    private List<ExamScoreItem> examScores;

    @Data
    @Builder
    public static class ExamScoreItem {
        private Long examId;
        private Integer score;       // 千分制
        private Integer paperScore;  // 试卷总分
        private double rate;         // 得分率
        private String status;
    }
}
