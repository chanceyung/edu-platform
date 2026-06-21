package com.eduplatform.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eduplatform.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目选项（选择题用：单选/多选/判断）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qbank_option")
public class QbankOption extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 题目ID */
    private Long questionId;

    /** 选项内容（富文本） */
    private String content;

    /** 0错误 1正确 */
    private Integer isCorrect;

    /** 排序 */
    private Integer sort;
}
