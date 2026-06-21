package com.eduplatform.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业。type: 1试题 2拍照 3文本。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("homework")
public class Homework extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;
    private Long classId;
    private Long subjectId;
    /** 1试题 2拍照 3文本 */
    private Integer type;
    private String content;
    private LocalDateTime deadline;
    /** 1进行中 2已截止 */
    private Integer status;
}
