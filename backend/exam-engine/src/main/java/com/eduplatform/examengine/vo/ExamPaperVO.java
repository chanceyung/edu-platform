package com.eduplatform.examengine.vo;

import com.eduplatform.examengine.entity.ExamPaper;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamPaperVO {

    private Long id;
    private String name;
    private Long subjectId;
    private Integer gradeLevel;
    private Integer totalScore;
    private Integer questionCount;
    private Integer paperType;
    private Integer status;
    /** 试卷题目列表（详情用） */
    private List<ExamPaperQuestionVO> questions;

    public static ExamPaperVO of(ExamPaper e) {
        if (e == null) return null;
        return ExamPaperVO.builder()
                .id(e.getId())
                .name(e.getName())
                .subjectId(e.getSubjectId())
                .gradeLevel(e.getGradeLevel())
                .totalScore(e.getTotalScore())
                .questionCount(e.getQuestionCount())
                .paperType(e.getPaperType())
                .status(e.getStatus())
                .build();
    }
}
