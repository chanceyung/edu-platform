package com.eduplatform.questionbank.service;

import com.eduplatform.questionbank.dto.QbankQuestionSaveDTO;
import com.eduplatform.questionbank.entity.QbankQuestion;

import java.util.List;

/**
 * 题目业务接口。
 */
public interface QbankQuestionService {

    /**
     * 新增题目。
     */
    Long create(QbankQuestionSaveDTO dto);

    /**
     * 按学科/年级/题型查询（参数均可空）。
     */
    List<QbankQuestion> list(Long subjectId, Integer gradeLevel, Integer type);

    /**
     * 按主键查询。
     */
    QbankQuestion getById(Long id);
}
