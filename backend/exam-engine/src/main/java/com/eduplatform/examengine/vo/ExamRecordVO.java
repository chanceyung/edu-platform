package com.eduplatform.examengine.vo;

import com.eduplatform.examengine.entity.ExamRecord;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ExamRecordVO {

    private Long id;
    private Long examId;
    private Long studentId;
    private LocalDateTime submitTime;
    private Integer systemScore;
    private Integer paperScore;
    private Integer status;
    private List<ExamRecordAnswerVO> answers;

    public static ExamRecordVO of(ExamRecord e) {
        if (e == null) return null;
        return ExamRecordVO.builder()
                .id(e.getId())
                .examId(e.getExamId())
                .studentId(e.getStudentId())
                .submitTime(e.getSubmitTime())
                .systemScore(e.getSystemScore())
                .paperScore(e.getPaperScore())
                .status(e.getStatus())
                .build();
    }
}
