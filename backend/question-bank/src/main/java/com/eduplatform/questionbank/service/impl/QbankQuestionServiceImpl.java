package com.eduplatform.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.questionbank.dto.QbankQuestionSaveDTO;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import com.eduplatform.questionbank.service.QbankQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题目业务实现。
 */
@Service
@RequiredArgsConstructor
public class QbankQuestionServiceImpl implements QbankQuestionService {

    private final QbankQuestionMapper questionMapper;

    @Override
    public Long create(QbankQuestionSaveDTO dto) {
        QbankQuestion q = new QbankQuestion();
        q.setType(dto.getType());
        q.setSubjectId(dto.getSubjectId());
        q.setGradeLevel(dto.getGradeLevel());
        q.setScore(dto.getScore());
        q.setDifficult(dto.getDifficult());
        q.setKnowledgePointId(dto.getKnowledgePointId());
        q.setStem(dto.getStem());
        q.setAnswer(dto.getAnswer());
        q.setAnalysis(dto.getAnalysis());
        q.setStatus(1);
        questionMapper.insert(q);
        return q.getId();
    }

    @Override
    public List<QbankQuestion> list(Long subjectId, Integer gradeLevel, Integer type) {
        LambdaQueryWrapper<QbankQuestion> wrapper = new LambdaQueryWrapper<>();
        if (subjectId != null) wrapper.eq(QbankQuestion::getSubjectId, subjectId);
        if (gradeLevel != null) wrapper.eq(QbankQuestion::getGradeLevel, gradeLevel);
        if (type != null) wrapper.eq(QbankQuestion::getType, type);
        wrapper.orderByDesc(QbankQuestion::getCreateTime);
        return questionMapper.selectList(wrapper);
    }

    @Override
    public QbankQuestion getById(Long id) {
        return questionMapper.selectById(id);
    }
}
