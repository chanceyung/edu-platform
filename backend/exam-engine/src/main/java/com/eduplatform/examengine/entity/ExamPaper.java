package com.eduplatform.examengine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_paper")
public class ExamPaper extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;
    private Long subjectId;
    private Integer gradeLevel;

    /** 试卷总分（千分制） */
    private Integer totalScore;

    /** 题目数量 */
    private Integer questionCount;

    /** 1固定试卷 2随机试卷（后续） */
    private Integer paperType;

    /** 1启用 2停用 */
    private Integer status;
}
