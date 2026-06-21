package com.eduplatform.examengine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamPaperSaveDTO {

    @NotBlank(message = "试卷名称不能为空")
    private String name;

    private Long subjectId;
    private Integer gradeLevel;

    /** 1固定试卷 2随机（后续） */
    private Integer paperType = 1;
}
