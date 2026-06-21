package com.eduplatform.examengine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 答卷逐题答案。do_right: 0错 1对 NULL待判(主观)。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_record_answer")
public class ExamRecordAnswer extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long recordId;
    private Long questionId;

    /** 学生答案（客观题：选项ID JSON 数组） */
    private String answer;

    /** 该题得分（千分制） */
    private Integer customerScore;

    /** 0错 1对 NULL待判 */
    private Integer doRight;
}
