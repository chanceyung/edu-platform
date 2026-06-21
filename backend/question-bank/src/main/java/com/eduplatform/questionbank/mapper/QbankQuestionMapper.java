package com.eduplatform.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduplatform.questionbank.entity.QbankQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目 Mapper。
 */
@Mapper
public interface QbankQuestionMapper extends BaseMapper<QbankQuestion> {
}
