package com.eduplatform.examengine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.examengine.dto.ExamRecordSubmitDTO;
import com.eduplatform.examengine.entity.*;
import com.eduplatform.examengine.mapper.*;
import com.eduplatform.examengine.service.ExamRecordService;
import com.eduplatform.questionbank.entity.QbankOption;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankOptionMapper;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 答卷业务实现。提交时客观题自动判分（学生选项 == 正确选项集合）。
 * 主观题待人工/AI 判（do_right=null, status=待判分）。
 */
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl implements ExamRecordService {

    private final ExamRecordMapper recordMapper;
    private final ExamRecordAnswerMapper answerMapper;
    private final ExamMapper examMapper;
    private final ExamPaperMapper paperMapper;
    private final ExamPaperQuestionMapper paperQuestionMapper;
    private final QbankQuestionMapper questionMapper;
    private final QbankOptionMapper optionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(ExamRecordSubmitDTO dto) {
        Exam exam = examMapper.selectById(dto.getExamId());
        if (exam == null) {
            throw new IllegalArgumentException("考试不存在: " + dto.getExamId());
        }
        ExamPaper paper = paperMapper.selectById(exam.getPaperId());
        int paperScore = paper == null || paper.getTotalScore() == null ? 0 : paper.getTotalScore();

        ExamRecord record = new ExamRecord();
        record.setExamId(dto.getExamId());
        record.setStudentId(dto.getStudentId());
        record.setSubmitTime(LocalDateTime.now());
        record.setSystemScore(0);
        record.setPaperScore(paperScore);
        record.setStatus(1);
        recordMapper.insert(record);

        int systemScore = 0;
        boolean hasSubjective = false;
        for (ExamRecordSubmitDTO.AnswerItem item : dto.getAnswers()) {
            QbankQuestion q = questionMapper.selectById(item.getQuestionId());
            if (q == null) {
                throw new IllegalArgumentException("题目不存在: " + item.getQuestionId());
            }
            ExamPaperQuestion pq = paperQuestionMapper.selectOne(new LambdaQueryWrapper<ExamPaperQuestion>()
                    .eq(ExamPaperQuestion::getPaperId, exam.getPaperId())
                    .eq(ExamPaperQuestion::getQuestionId, item.getQuestionId()));
            int qScore = pq == null || pq.getScore() == null ? 0 : pq.getScore();

            ExamRecordAnswer ans = new ExamRecordAnswer();
            ans.setRecordId(record.getId());
            ans.setQuestionId(item.getQuestionId());
            ans.setAnswer(item.getAnswer());

            Integer type = q.getType();
            if (type != null && type <= 3) {
                // 客观题（单选1/多选2/判断3）：自动判分
                Set<Long> correctIds = optionMapper.selectList(new LambdaQueryWrapper<QbankOption>()
                                .eq(QbankOption::getQuestionId, item.getQuestionId())).stream()
                        .filter(o -> o.getIsCorrect() != null && o.getIsCorrect() == 1)
                        .map(QbankOption::getId).collect(Collectors.toSet());
                Set<Long> studentIds = parseOptionIds(item.getAnswer());
                boolean right = studentIds.equals(correctIds);
                ans.setDoRight(right ? 1 : 0);
                ans.setCustomerScore(right ? qScore : 0);
                if (right) systemScore += qScore;
            } else {
                // 主观题（填空4/简答5）：待判
                ans.setDoRight(null);
                ans.setCustomerScore(0);
                hasSubjective = true;
            }
            answerMapper.insert(ans);
        }

        record.setSystemScore(systemScore);
        record.setStatus(hasSubjective ? 1 : 2);
        recordMapper.updateById(record);
        return record.getId();
    }

    @Override
    public ExamRecord getByExamAndStudent(Long examId, Long studentId) {
        return recordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStudentId, studentId));
    }

    /** 解析 "[123,456]" → Set<Long> */
    private Set<Long> parseOptionIds(String json) {
        if (json == null || json.isBlank()) return Set.of();
        String s = json.replaceAll("[\\[\\]\\s]", "");
        if (s.isEmpty()) return Set.of();
        return Arrays.stream(s.split(",")).map(String::trim)
                .filter(x -> !x.isEmpty()).map(Long::valueOf).collect(Collectors.toSet());
    }
}
