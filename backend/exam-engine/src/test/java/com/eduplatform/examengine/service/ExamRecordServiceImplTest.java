package com.eduplatform.examengine.service;

import com.eduplatform.examengine.dto.ExamRecordSubmitDTO;
import com.eduplatform.examengine.entity.*;
import com.eduplatform.examengine.mapper.*;
import com.eduplatform.examengine.service.impl.ExamRecordServiceImpl;
import com.eduplatform.questionbank.entity.QbankOption;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankOptionMapper;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 答卷判分单元测试（TDD）：客观题选对→system_score累加 / 选错→0 / 考试不存在。
 */
@ExtendWith(MockitoExtension.class)
class ExamRecordServiceImplTest {

    @Mock private ExamRecordMapper recordMapper;
    @Mock private ExamRecordAnswerMapper answerMapper;
    @Mock private ExamMapper examMapper;
    @Mock private ExamPaperMapper paperMapper;
    @Mock private ExamPaperQuestionMapper paperQuestionMapper;
    @Mock private QbankQuestionMapper questionMapper;
    @Mock private QbankOptionMapper optionMapper;

    @InjectMocks private ExamRecordServiceImpl recordService;

    private ExamRecordSubmitDTO dto(Long examId, Long questionId, String answer) {
        ExamRecordSubmitDTO dto = new ExamRecordSubmitDTO();
        dto.setExamId(examId);
        dto.setStudentId(9001L);
        ExamRecordSubmitDTO.AnswerItem item = new ExamRecordSubmitDTO.AnswerItem();
        item.setQuestionId(questionId);
        item.setAnswer(answer);
        dto.setAnswers(List.of(item));
        return dto;
    }

    @Test
    void submit_应抛异常_when_考试不存在() {
        when(examMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> recordService.submit(dto(1L, 10L, "[100]")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("考试不存在");
        verify(recordMapper, never()).insert(any(ExamRecord.class));
    }

    @Test
    void submit_客观题选对应判对并累分() {
        Exam exam = new Exam();
        exam.setPaperId(50L);
        when(examMapper.selectById(any())).thenReturn(exam);
        when(paperMapper.selectById(any())).thenReturn(new ExamPaper());
        // 题目（单选 type=1）
        QbankQuestion q = new QbankQuestion();
        q.setType(1);
        when(questionMapper.selectById(any())).thenReturn(q);
        // 试卷题目分值 5000
        ExamPaperQuestion pq = new ExamPaperQuestion();
        pq.setScore(5000);
        when(paperQuestionMapper.selectOne(any())).thenReturn(pq);
        // 正确选项 id=100
        QbankOption correct = new QbankOption();
        correct.setId(100L);
        correct.setIsCorrect(1);
        when(optionMapper.selectList(any())).thenReturn(List.of(correct));
        when(recordMapper.insert(any(ExamRecord.class))).thenAnswer(inv -> {
            ((ExamRecord) inv.getArgument(0)).setId(8001L);
            return 1;
        });

        Long id = recordService.submit(dto(1L, 10L, "[100]"));

        assertThat(id).isEqualTo(8001L);
        // record 更新时 system_score 应为 5000
        org.mockito.ArgumentCaptor<ExamRecord> captor = org.mockito.ArgumentCaptor.forClass(ExamRecord.class);
        verify(recordMapper).updateById(captor.capture());
        assertThat(captor.getValue().getSystemScore()).isEqualTo(5000);
        assertThat(captor.getValue().getStatus()).isEqualTo(2); // 全客观→完成
    }

    @Test
    void submit_客观题选错应判错零分() {
        Exam exam = new Exam();
        exam.setPaperId(50L);
        when(examMapper.selectById(any())).thenReturn(exam);
        when(paperMapper.selectById(any())).thenReturn(new ExamPaper());
        QbankQuestion q = new QbankQuestion();
        q.setType(1);
        when(questionMapper.selectById(any())).thenReturn(q);
        ExamPaperQuestion pq = new ExamPaperQuestion();
        pq.setScore(5000);
        when(paperQuestionMapper.selectOne(any())).thenReturn(pq);
        QbankOption correct = new QbankOption();
        correct.setId(100L);
        correct.setIsCorrect(1);
        when(optionMapper.selectList(any())).thenReturn(List.of(correct));
        when(recordMapper.insert(any(ExamRecord.class))).thenReturn(1);

        recordService.submit(dto(1L, 10L, "[999]")); // 选错

        org.mockito.ArgumentCaptor<ExamRecord> captor = org.mockito.ArgumentCaptor.forClass(ExamRecord.class);
        verify(recordMapper).updateById(captor.capture());
        assertThat(captor.getValue().getSystemScore()).isEqualTo(0); // 错→0分
    }
}
