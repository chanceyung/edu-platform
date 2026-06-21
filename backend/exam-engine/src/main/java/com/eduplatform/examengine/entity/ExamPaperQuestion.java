package com.eduplatform.examengine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷-题目关联（组卷）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_paper_question")
public class ExamPaperQuestion extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long paperId;
    private Long questionId;

    /** 该题分值（千分制） */
    private Integer score;

    /** 题号顺序 */
    private Integer sort;
}
