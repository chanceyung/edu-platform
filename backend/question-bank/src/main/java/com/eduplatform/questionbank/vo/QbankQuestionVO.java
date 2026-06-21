package com.eduplatform.questionbank.vo;

import com.eduplatform.questionbank.entity.QbankQuestion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QbankQuestionVO {

    private Long id;
    private Integer type;
    private Long subjectId;
    private Integer gradeLevel;
    private Integer score;
    private Integer difficult;
    private Long knowledgePointId;
    private String stem;
    private String answer;
    private String analysis;
    private Integer status;

    public static QbankQuestionVO of(QbankQuestion e) {
        if (e == null) return null;
        return QbankQuestionVO.builder()
                .id(e.getId())
                .type(e.getType())
                .subjectId(e.getSubjectId())
                .gradeLevel(e.getGradeLevel())
                .score(e.getScore())
                .difficult(e.getDifficult())
                .knowledgePointId(e.getKnowledgePointId())
                .stem(e.getStem())
                .answer(e.getAnswer())
                .analysis(e.getAnalysis())
                .status(e.getStatus())
                .build();
    }
}
