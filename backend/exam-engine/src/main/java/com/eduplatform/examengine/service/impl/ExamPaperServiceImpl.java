package com.eduplatform.examengine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.examengine.dto.ExamComposeDTO;
import com.eduplatform.examengine.dto.ExamPaperSaveDTO;
import com.eduplatform.examengine.entity.ExamPaper;
import com.eduplatform.examengine.entity.ExamPaperQuestion;
import com.eduplatform.examengine.mapper.ExamPaperMapper;
import com.eduplatform.examengine.mapper.ExamPaperQuestionMapper;
import com.eduplatform.examengine.service.ExamPaperService;
import com.eduplatform.examengine.vo.ExamPaperDetailVO;
import com.eduplatform.questionbank.entity.QbankOption;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankOptionMapper;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 试卷业务实现。组卷跨模块校验题目（注入 QbankQuestionMapper）。
 */
@Service
@RequiredArgsConstructor
public class ExamPaperServiceImpl implements ExamPaperService {

    private final ExamPaperMapper paperMapper;
    private final ExamPaperQuestionMapper paperQuestionMapper;
    private final QbankQuestionMapper questionMapper;
    private final QbankOptionMapper optionMapper;

    @Override
    public Long create(ExamPaperSaveDTO dto) {
        ExamPaper paper = new ExamPaper();
        paper.setName(dto.getName());
        paper.setSubjectId(dto.getSubjectId());
        paper.setGradeLevel(dto.getGradeLevel());
        paper.setPaperType(dto.getPaperType() == null ? 1 : dto.getPaperType());
        paper.setTotalScore(0);
        paper.setQuestionCount(0);
        paper.setStatus(1);
        paperMapper.insert(paper);
        return paper.getId();
    }

    @Override
    public List<ExamPaper> list() {
        return paperMapper.selectList(new LambdaQueryWrapper<ExamPaper>()
                .orderByDesc(ExamPaper::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void compose(ExamComposeDTO dto) {
        ExamPaper paper = paperMapper.selectById(dto.getPaperId());
        if (paper == null) {
            throw new IllegalArgumentException("试卷不存在: " + dto.getPaperId());
        }
        // 当前试卷已有题数作为起始 sort
        Long existCount = paperQuestionMapper.selectCount(new LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getPaperId, dto.getPaperId()));
        int sort = existCount == null ? 0 : existCount.intValue();
        int addScore = 0;
        for (ExamComposeDTO.ComposeItem item : dto.getItems()) {
            QbankQuestion q = questionMapper.selectById(item.getQuestionId());
            if (q == null) {
                throw new IllegalArgumentException("题目不存在: " + item.getQuestionId());
            }
            ExamPaperQuestion rel = new ExamPaperQuestion();
            rel.setPaperId(dto.getPaperId());
            rel.setQuestionId(item.getQuestionId());
            rel.setScore(item.getScore());
            rel.setSort(++sort);
            paperQuestionMapper.insert(rel);
            addScore += item.getScore();
        }
        paper.setTotalScore((paper.getTotalScore() == null ? 0 : paper.getTotalScore()) + addScore);
        paper.setQuestionCount((paper.getQuestionCount() == null ? 0 : paper.getQuestionCount()) + dto.getItems().size());
        paperMapper.updateById(paper);
    }

    @Override
    public ExamPaperDetailVO getDetail(Long paperId) {
        ExamPaper paper = paperMapper.selectById(paperId);
        if (paper == null) return null;
        List<ExamPaperQuestion> rels = paperQuestionMapper.selectList(new LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getPaperId, paperId)
                .orderByAsc(ExamPaperQuestion::getSort));
        List<ExamPaperDetailVO.Question> questions = rels.stream().map(rel -> {
            QbankQuestion q = questionMapper.selectById(rel.getQuestionId());
            List<ExamPaperDetailVO.Option> options = optionMapper.selectList(
                    new LambdaQueryWrapper<QbankOption>().eq(QbankOption::getQuestionId, rel.getQuestionId()))
                    .stream().map(o -> ExamPaperDetailVO.Option.builder()
                            .id(o.getId()).content(o.getContent()).isCorrect(o.getIsCorrect()).build())
                    .collect(Collectors.toList());
            return ExamPaperDetailVO.Question.builder()
                    .questionId(rel.getQuestionId())
                    .stem(q == null ? null : q.getStem())
                    .type(q == null ? null : q.getType())
                    .score(rel.getScore())
                    .sort(rel.getSort())
                    .options(options)
                    .build();
        }).collect(Collectors.toList());
        return ExamPaperDetailVO.builder()
                .id(paper.getId())
                .name(paper.getName())
                .subjectId(paper.getSubjectId())
                .gradeLevel(paper.getGradeLevel())
                .totalScore(paper.getTotalScore())
                .questionCount(paper.getQuestionCount())
                .paperType(paper.getPaperType())
                .questions(questions)
                .build();
    }

    @Override
    public List<Long> listQuestionIds(Long paperId) {
        return paperQuestionMapper.selectList(new LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getPaperId, paperId)
                .orderByAsc(ExamPaperQuestion::getSort))
                .stream().map(ExamPaperQuestion::getQuestionId).toList();
    }
}
