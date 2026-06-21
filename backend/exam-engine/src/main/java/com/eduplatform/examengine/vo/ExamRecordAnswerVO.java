package com.eduplatform.examengine.vo;

import com.eduplatform.examengine.entity.ExamRecordAnswer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamRecordAnswerVO {

    private Long id;
    private Long questionId;
    private String answer;
    private Integer customerScore;
    private Integer doRight;

    public static ExamRecordAnswerVO of(ExamRecordAnswer e) {
        if (e == null) return null;
        return ExamRecordAnswerVO.builder()
                .id(e.getId())
                .questionId(e.getQuestionId())
                .answer(e.getAnswer())
                .customerScore(e.getCustomerScore())
                .doRight(e.getDoRight())
                .build();
    }
}
