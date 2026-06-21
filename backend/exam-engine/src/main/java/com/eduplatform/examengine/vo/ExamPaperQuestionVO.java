package com.eduplatform.examengine.vo;

import com.eduplatform.examengine.entity.ExamPaperQuestion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamPaperQuestionVO {

    private Long id;
    private Long questionId;
    private Integer score;
    private Integer sort;

    public static ExamPaperQuestionVO of(ExamPaperQuestion e) {
        if (e == null) return null;
        return ExamPaperQuestionVO.builder()
                .id(e.getId())
                .questionId(e.getQuestionId())
                .score(e.getScore())
                .sort(e.getSort())
                .build();
    }
}
