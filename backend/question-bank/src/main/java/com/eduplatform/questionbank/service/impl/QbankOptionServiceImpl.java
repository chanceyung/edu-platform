package com.eduplatform.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.questionbank.dto.QbankOptionSaveDTO;
import com.eduplatform.questionbank.entity.QbankOption;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankOptionMapper;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import com.eduplatform.questionbank.service.QbankOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题目选项业务实现。校验题目存在（跨实体，同模块注入 QbankQuestionMapper）。
 */
@Service
@RequiredArgsConstructor
public class QbankOptionServiceImpl implements QbankOptionService {

    private final QbankOptionMapper optionMapper;
    private final QbankQuestionMapper questionMapper;

    @Override
    public Long create(QbankOptionSaveDTO dto) {
        QbankQuestion question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new IllegalArgumentException("题目不存在: " + dto.getQuestionId());
        }
        QbankOption option = new QbankOption();
        option.setQuestionId(dto.getQuestionId());
        option.setContent(dto.getContent());
        option.setIsCorrect(dto.getIsCorrect() == null ? 0 : dto.getIsCorrect());
        option.setSort(dto.getSort() == null ? 0 : dto.getSort());
        optionMapper.insert(option);
        return option.getId();
    }

    @Override
    public List<QbankOption> listByQuestion(Long questionId) {
        return optionMapper.selectList(new LambdaQueryWrapper<QbankOption>()
                .eq(QbankOption::getQuestionId, questionId)
                .orderByAsc(QbankOption::getSort));
    }
}
