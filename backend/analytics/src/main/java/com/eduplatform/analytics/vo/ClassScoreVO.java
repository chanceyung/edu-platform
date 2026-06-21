package com.eduplatform.analytics.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 班级成绩分析（某次考试）。
 */
@Data
@Builder
public class ClassScoreVO {

    private Long examId;
    private String examName;
    private int studentCount;
    /** 平均分（千分制） */
    private int avgScore;
    /** 最高分 */
    private int maxScore;
    /** 最低分 */
    private int minScore;
    /** 及格率（>=60%，0-100） */
    private double passRate;
    /** 分数段分布 */
    private List<ScoreSegment> segments;
    /** 学生成绩明细（按分降序） */
    private List<StudentScoreDetail> students;

    @Data
    @Builder
    public static class ScoreSegment {
        private String label;   // 如 "90-100"、"80-89"
        private int count;
    }

    @Data
    @Builder
    public static class StudentScoreDetail {
        private Long studentId;
        private Integer score;   // 千分制
        private String status;   // "已完成" / "含主观待判"
    }
}
