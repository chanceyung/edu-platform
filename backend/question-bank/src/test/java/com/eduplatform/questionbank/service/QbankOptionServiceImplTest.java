package com.eduplatform.questionbank.service;

import com.eduplatform.questionbank.dto.QbankOptionSaveDTO;
import com.eduplatform.questionbank.entity.QbankOption;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankOptionMapper;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import com.eduplatform.questionbank.service.impl.QbankOptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 题目选项单元测试（TDD）：题目不存在 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class QbankOptionServiceImplTest {

    @Mock
    private QbankOptionMapper optionMapper;
    @Mock
    private QbankQuestionMapper questionMapper;

    @InjectMocks
    private QbankOptionServiceImpl optionService;

    @Test
    void create_应抛异常_when_题目不存在() {
        when(questionMapper.selectById(any())).thenReturn(null);
        QbankOptionSaveDTO dto = new QbankOptionSaveDTO();
        dto.setQuestionId(1L);
        dto.setContent("A. 1");
        assertThatThrownBy(() -> optionService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("题目不存在");
        verify(optionMapper, never()).insert(any(QbankOption.class));
    }

    @Test
    void create_应成功_when_题目存在() {
        when(questionMapper.selectById(any())).thenReturn(new QbankQuestion());
        when(optionMapper.insert(any(QbankOption.class))).thenAnswer(inv -> {
            ((QbankOption) inv.getArgument(0)).setId(6001L);
            return 1;
        });
        QbankOptionSaveDTO dto = new QbankOptionSaveDTO();
        dto.setQuestionId(1L);
        dto.setContent("A. 2");
        dto.setIsCorrect(1);
        assertThat(optionService.create(dto)).isEqualTo(6001L);
        verify(optionMapper, times(1)).insert(any(QbankOption.class));
    }
}
