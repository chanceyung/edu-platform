package com.eduplatform.homework.vo;

import com.eduplatform.homework.entity.Homework;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HomeworkVO {

    private Long id;
    private String title;
    private Long classId;
    private Long subjectId;
    private Integer type;
    private String content;
    private LocalDateTime deadline;
    private Integer status;

    public static HomeworkVO of(Homework e) {
        if (e == null) return null;
        return HomeworkVO.builder()
                .id(e.getId()).title(e.getTitle()).classId(e.getClassId()).subjectId(e.getSubjectId())
                .type(e.getType()).content(e.getContent()).deadline(e.getDeadline()).status(e.getStatus())
                .build();
    }
}
