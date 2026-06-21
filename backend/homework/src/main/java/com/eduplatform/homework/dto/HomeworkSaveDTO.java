package com.eduplatform.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HomeworkSaveDTO {

    @NotBlank(message = "作业标题不能为空")
    private String title;

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    private Long subjectId;

    /** 1试题 2拍照 3文本 */
    @NotNull(message = "作业类型不能为空")
    private Integer type;

    private String content;
    private LocalDateTime deadline;
}
