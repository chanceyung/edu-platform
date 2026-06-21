package com.eduplatform.examengine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamSaveDTO {

    @NotBlank(message = "考试名称不能为空")
    private String name;

    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
}
