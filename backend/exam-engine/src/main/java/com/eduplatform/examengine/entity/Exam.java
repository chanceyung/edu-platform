package com.eduplatform.examengine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考试（发布：试卷→班级+时间）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam")
public class Exam extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;
    private Long paperId;
    private Long classId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /** 时长（分钟） */
    private Integer duration;
    /** 0全部 1指定 */
    private Integer accessType;
    /** 1未发布 2进行中 3已结束 */
    private Integer status;
}
