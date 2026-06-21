package com.eduplatform.questionbank.service;

import com.eduplatform.questionbank.dto.QbankQuestionSaveDTO;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import com.eduplatform.questionbank.service.impl.QbankQuestionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 题目业务单元测试（TDD）。
 */
@ExtendWith(MockitoExtension.class)
class QbankQuestionServiceImplTest {

    @Mock
    private QbankQuestionMapper questionMapper;

    @InjectMocks
    private QbankQuestionServiceImpl questionService;

    @Test
    void create_应成功并返回主键() {
        when(questionMapper.insert(any(QbankQuestion.class))).thenAnswer(invocation -> {
            ((QbankQuestion) invocation.getArgument(0)).setId(5001L);
            return 1;
        });

        QbankQuestionSaveDTO dto = new QbankQuestionSaveDTO();
        dto.setType(1);
        dto.setScore(5000);
        dto.setStem("1+1=?");

        Long id = questionService.create(dto);

        assertThat(id).isEqualTo(5001L);
        verify(questionMapper, times(1)).insert(any(QbankQuestion.class));
    }
}
