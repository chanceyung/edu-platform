package com.eduplatform.questionbank.service;

import com.eduplatform.questionbank.dto.QbankOptionSaveDTO;
import com.eduplatform.questionbank.entity.QbankOption;

import java.util.List;

/**
 * 题目选项业务接口。
 */
public interface QbankOptionService {

    /**
     * 新增选项。校验题目存在。
     * @throws IllegalArgumentException 题目不存在
     */
    Long create(QbankOptionSaveDTO dto);

    /**
     * 按题目列出选项（按 sort 排序）。
     */
    List<QbankOption> listByQuestion(Long questionId);
}
