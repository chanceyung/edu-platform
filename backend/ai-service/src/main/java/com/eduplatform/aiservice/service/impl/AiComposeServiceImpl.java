package com.eduplatform.aiservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.aiservice.service.AiComposeService;
import com.eduplatform.examengine.dto.ExamComposeDTO;
import com.eduplatform.examengine.entity.ExamPaperQuestion;
import com.eduplatform.examengine.mapper.ExamPaperQuestionMapper;
import com.eduplatform.examengine.service.ExamPaperService;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI 智能组卷实现。规则筛选候选（确定性）+ 随机选题（保证覆盖面）+ 排除已有题。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiComposeServiceImpl implements AiComposeService {

    private final QbankQuestionMapper questionMapper;
    private final ExamPaperService paperService;
    private final ExamPaperQuestionMapper paperQuestionMapper;

    @Override
    public int autoCompose(Long paperId, Integer gradeLevel, Integer type, Integer difficulty, int count) {
        // 查试卷已有的题目（排除重复）
        Set<Long> existIds = paperQuestionMapper.selectList(new LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getPaperId, paperId))
                .stream().map(ExamPaperQuestion::getQuestionId).collect(Collectors.toSet());

        // 查候选题（按条件筛选，排除已有）
        LambdaQueryWrapper<QbankQuestion> wrapper = new LambdaQueryWrapper<>();
        if (gradeLevel != null) wrapper.eq(QbankQuestion::getGradeLevel, gradeLevel);
        if (type != null) wrapper.eq(QbankQuestion::getType, type);
        if (difficulty != null) wrapper.eq(QbankQuestion::getDifficult, difficulty);
        wrapper.eq(QbankQuestion::getStatus, 1);
        if (!existIds.isEmpty()) wrapper.notIn(QbankQuestion::getId, existIds);

        List<QbankQuestion> candidates = questionMapper.selectList(wrapper);
        if (candidates.isEmpty()) {
            log.warn("智能组卷：无候选题目 gradeLevel={} type={} difficulty={}", gradeLevel, type, difficulty);
            return 0;
        }

        // 随机选题
        Collections.shuffle(candidates);
        int selectCount = Math.min(count, candidates.size());
        List<QbankQuestion> selected = candidates.subList(0, selectCount);

        // 构造 compose items
        List<ExamComposeDTO.ComposeItem> items = selected.stream()
                .map(q -> {
                    ExamComposeDTO.ComposeItem item = new ExamComposeDTO.ComposeItem();
                    item.setQuestionId(q.getId());
                    item.setScore(q.getScore() != null ? q.getScore() : 5000);  // 用题目自带分值
                    return item;
                })
                .toList();

        // 调组卷（复用 ExamPaperService）
        ExamComposeDTO dto = new ExamComposeDTO();
        dto.setPaperId(paperId);
        dto.setItems(items);
        paperService.compose(dto);

        log.info("智能组卷：paperId={} 选中{}题（候选{}）", paperId, selectCount, candidates.size());
        return selectCount;
    }
}
