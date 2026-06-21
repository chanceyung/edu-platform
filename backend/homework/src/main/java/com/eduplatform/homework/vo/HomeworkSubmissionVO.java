package com.eduplatform.homework.vo;

import com.eduplatform.homework.entity.HomeworkSubmission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HomeworkSubmissionVO {

    private Long id;
    private Long homeworkId;
    private Long studentId;
    private String content;
    private LocalDateTime submitTime;
    private Integer score;
    private Integer status;
    private String comment;

    public static HomeworkSubmissionVO of(HomeworkSubmission e) {
        if (e == null) return null;
        return HomeworkSubmissionVO.builder()
                .id(e.getId()).homeworkId(e.getHomeworkId()).studentId(e.getStudentId())
                .content(e.getContent()).submitTime(e.getSubmitTime()).score(e.getScore())
                .status(e.getStatus()).comment(e.getComment()).build();
    }
}
