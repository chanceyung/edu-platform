package com.eduplatform.homework.service;

import com.eduplatform.homework.dto.HomeworkSaveDTO;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.service.impl.HomeworkServiceImpl;
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
 * 作业布置单元测试（TDD）：班级不存在 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class HomeworkServiceImplTest {

    @Mock private HomeworkMapper homeworkMapper;
    @Mock private OrgClassMapper classMapper;

    @InjectMocks private HomeworkServiceImpl homeworkService;

    private HomeworkSaveDTO dto() {
        HomeworkSaveDTO dto = new HomeworkSaveDTO();
        dto.setTitle("数学作业1");
        dto.setClassId(1L);
        dto.setType(3);
        return dto;
    }

    @Test
    void create_应抛异常_when_班级不存在() {
        when(classMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> homeworkService.create(dto()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("班级不存在");
        verify(homeworkMapper, never()).insert(any(Homework.class));
    }

    @Test
    void create_应成功_when_班级存在() {
        when(classMapper.selectById(any())).thenReturn(new OrgClass());
        when(homeworkMapper.insert(any(Homework.class))).thenAnswer(inv -> {
            ((Homework) inv.getArgument(0)).setId(9001L);
            return 1;
        });
        assertThat(homeworkService.create(dto())).isEqualTo(9001L);
        verify(homeworkMapper, times(1)).insert(any(Homework.class));
    }
}
