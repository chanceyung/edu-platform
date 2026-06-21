package com.eduplatform.examengine.vo;

import com.eduplatform.examengine.entity.Exam;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExamVO {

    private Long id;
    private String name;
    private Long paperId;
    private Long classId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Integer status;

    public static ExamVO of(Exam e) {
        if (e == null) return null;
        return ExamVO.builder()
                .id(e.getId())
                .name(e.getName())
                .paperId(e.getPaperId())
                .classId(e.getClassId())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .duration(e.getDuration())
                .status(e.getStatus())
                .build();
    }
}
