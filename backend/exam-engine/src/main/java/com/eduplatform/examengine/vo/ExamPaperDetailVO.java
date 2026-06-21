package com.eduplatform.examengine.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 试卷详情（含题目内容+选项），供前端展示与学生作答。
 */
@Data
@Builder
public class ExamPaperDetailVO {

    private Long id;
    private String name;
    private Long subjectId;
    private Integer gradeLevel;
    private Integer totalScore;
    private Integer questionCount;
    private Integer paperType;
    private List<Question> questions;

    @Data
    @Builder
    public static class Question {
        private Long questionId;
        private String stem;
        private Integer type;
        private Integer score;
        private Integer sort;
        private List<Option> options;
    }

    @Data
    @Builder
    public static class Option {
        private Long id;
        private String content;
        private Integer isCorrect;
    }
}
