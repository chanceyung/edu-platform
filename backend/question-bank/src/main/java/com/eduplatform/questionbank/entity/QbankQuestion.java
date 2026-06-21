package com.eduplatform.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目实体（5题型/千分制/难度/知识点）。
 * 题型：1单选 2多选 3判断 4填空 5简答。
 * 分值千分制（5分存5000）——借鉴学之思设计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qbank_question")
public class QbankQuestion extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 题型 1单选 2多选 3判断 4填空 5简答 */
    private Integer type;

    /** 学科ID */
    private Long subjectId;

    /** 年级 7/8/9 */
    private Integer gradeLevel;

    /** 分值（千分制） */
    private Integer score;

    /** 难度 1-5 */
    private Integer difficult;

    /** 知识点ID */
    private Long knowledgePointId;

    /** 题干（富文本） */
    private String stem;

    /** 参考答案 */
    private String answer;

    /** 解析 */
    private String analysis;

    /** 状态 1启用 2停用 */
    private Integer status;
}
