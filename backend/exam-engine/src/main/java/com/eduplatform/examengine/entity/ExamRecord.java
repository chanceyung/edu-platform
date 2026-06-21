package com.eduplatform.examengine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考试答卷（整卷）。借鉴学之思判分模型：system_score(客观自动)+status(待判分/完成)。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_record")
public class ExamRecord extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long examId;
    private Long studentId;
    private LocalDateTime submitTime;

    /** 系统判分（客观题，千分制） */
    private Integer systemScore;

    /** 试卷总分（千分制） */
    private Integer paperScore;

    /** 1待判分(含主观) 2完成(全客观已判) */
    private Integer status;
}
