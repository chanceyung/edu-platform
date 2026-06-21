package com.eduplatform.examengine.service;

import com.eduplatform.examengine.dto.ExamSaveDTO;
import com.eduplatform.examengine.entity.Exam;
import com.eduplatform.examengine.entity.ExamPaper;
import com.eduplatform.examengine.mapper.ExamMapper;
import com.eduplatform.examengine.mapper.ExamPaperMapper;
import com.eduplatform.examengine.service.impl.ExamServiceImpl;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.mapper.OrgClassMapper;
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
 * 考试发布单元测试（TDD）：试卷不存在 / 班级不存在 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock private ExamMapper examMapper;
    @Mock private ExamPaperMapper paperMapper;
    @Mock private OrgClassMapper classMapper;

    @InjectMocks private ExamServiceImpl examService;

    private ExamSaveDTO dto() {
        ExamSaveDTO dto = new ExamSaveDTO();
        dto.setName("期中测试");
        dto.setPaperId(1L);
        dto.setClassId(2L);
        return dto;
    }

    @Test
    void create_应抛异常_when_试卷不存在() {
        when(paperMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> examService.create(dto()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("试卷不存在");
        verify(examMapper, never()).insert(any(Exam.class));
    }

    @Test
    void create_应抛异常_when_班级不存在() {
        when(paperMapper.selectById(any())).thenReturn(new ExamPaper());
        when(classMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> examService.create(dto()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("班级不存在");
        verify(examMapper, never()).insert(any(Exam.class));
    }

    @Test
    void create_应成功_when_试卷班级均存在() {
        when(paperMapper.selectById(any())).thenReturn(new ExamPaper());
        when(classMapper.selectById(any())).thenReturn(new OrgClass());
        when(examMapper.insert(any(Exam.class))).thenAnswer(inv -> {
            ((Exam) inv.getArgument(0)).setId(7001L);
            return 1;
        });
        assertThat(examService.create(dto())).isEqualTo(7001L);
        verify(examMapper, times(1)).insert(any(Exam.class));
    }
}
