package com.eduplatform.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业提交。status: 1已交未批 2已批。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("homework_submission")
public class HomeworkSubmission extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long homeworkId;
    private Long studentId;
    private String content;
    private LocalDateTime submitTime;
    /** 得分（千分制，批改后） */
    private Integer score;
    /** 1已交未批 2已批 */
    private Integer status;
    /** 批改评语 */
    private String comment;
}
