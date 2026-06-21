package com.eduplatform.examengine.service;

import com.eduplatform.examengine.dto.ExamComposeDTO;
import com.eduplatform.examengine.entity.ExamPaper;
import com.eduplatform.examengine.entity.ExamPaperQuestion;
import com.eduplatform.examengine.mapper.ExamPaperMapper;
import com.eduplatform.examengine.mapper.ExamPaperQuestionMapper;
import com.eduplatform.examengine.service.impl.ExamPaperServiceImpl;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import com.eduplatform.questionbank.mapper.QbankOptionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 试卷组卷单元测试（TDD）：试卷不存在 / 题目不存在 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class ExamPaperServiceImplTest {

    @Mock private ExamPaperMapper paperMapper;
    @Mock private ExamPaperQuestionMapper paperQuestionMapper;
    @Mock private QbankQuestionMapper questionMapper;
    @Mock private QbankOptionMapper optionMapper;

    @InjectMocks private ExamPaperServiceImpl paperService;

    private ExamComposeDTO dto(Long paperId) {
        ExamComposeDTO dto = new ExamComposeDTO();
        dto.setPaperId(paperId);
        ExamComposeDTO.ComposeItem item = new ExamComposeDTO.ComposeItem();
        item.setQuestionId(100L);
        item.setScore(5000);
        dto.setItems(java.util.List.of(item));
        return dto;
    }

    @Test
    void compose_应抛异常_when_试卷不存在() {
        when(paperMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> paperService.compose(dto(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("试卷不存在");
        verify(paperQuestionMapper, never()).insert(any(ExamPaperQuestion.class));
    }

    @Test
    void compose_应抛异常_when_题目不存在() {
        when(paperMapper.selectById(any())).thenReturn(new ExamPaper());
        when(paperQuestionMapper.selectCount(any())).thenReturn(0L);
        when(questionMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> paperService.compose(dto(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("题目不存在");
    }

    @Test
    void compose_应成功_when_试卷题目均存在() {
        when(paperMapper.selectById(any())).thenReturn(new ExamPaper());
        when(paperQuestionMapper.selectCount(any())).thenReturn(0L);
        when(questionMapper.selectById(any())).thenReturn(new QbankQuestion());
        when(paperQuestionMapper.insert(any(ExamPaperQuestion.class))).thenReturn(1);
        paperService.compose(dto(1L));
        verify(paperQuestionMapper, times(1)).insert(any(ExamPaperQuestion.class));
        verify(paperMapper, times(1)).updateById(any(ExamPaper.class));
    }
}
