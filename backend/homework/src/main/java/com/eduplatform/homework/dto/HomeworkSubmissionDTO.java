package com.eduplatform.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HomeworkSubmissionDTO {

    @NotNull(message = "作业ID不能为空")
    private Long homeworkId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotBlank(message = "提交内容不能为空")
    private String content;
}
