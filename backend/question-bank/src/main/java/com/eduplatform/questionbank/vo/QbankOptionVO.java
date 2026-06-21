package com.eduplatform.questionbank.vo;

import com.eduplatform.questionbank.entity.QbankOption;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QbankOptionVO {

    private Long id;
    private Long questionId;
    private String content;
    private Integer isCorrect;
    private Integer sort;

    public static QbankOptionVO of(QbankOption e) {
        if (e == null) return null;
        return QbankOptionVO.builder()
                .id(e.getId())
                .questionId(e.getQuestionId())
                .content(e.getContent())
                .isCorrect(e.getIsCorrect())
                .sort(e.getSort())
                .build();
    }
}
