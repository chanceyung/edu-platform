package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.dto.OrgClassSaveDTO;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.entity.OrgGrade;
import com.eduplatform.schoolorg.mapper.OrgClassMapper;
import com.eduplatform.schoolorg.mapper.OrgGradeMapper;
import com.eduplatform.schoolorg.service.impl.OrgClassServiceImpl;
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
 * 班级业务单元测试（TDD）。覆盖：年级不存在 / 班级重复 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class OrgClassServiceImplTest {

    @Mock
    private OrgClassMapper classMapper;
    @Mock
    private OrgGradeMapper gradeMapper;

    @InjectMocks
    private OrgClassServiceImpl classService;

    private OrgClassSaveDTO newDto(Long gradeId, String name) {
        OrgClassSaveDTO dto = new OrgClassSaveDTO();
        dto.setSchoolId(1L);
        dto.setGradeId(gradeId);
        dto.setName(name);
        return dto;
    }

    @Test
    void create_应抛异常_when_年级不存在() {
        when(gradeMapper.selectById(any())).thenReturn(null);

        assertThatThrownBy(() -> classService.create(newDto(1L, "1班")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("年级不存在");
        verify(classMapper, never()).insert(any(OrgClass.class));
    }

    @Test
    void create_应抛异常_when_该年级班级已存在() {
        when(gradeMapper.selectById(any())).thenReturn(new OrgGrade());
        when(classMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> classService.create(newDto(1L, "1班")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("该班级已存在");
        verify(classMapper, never()).insert(any(OrgClass.class));
    }

    @Test
    void create_应成功_when_年级存在且班级可用() {
        when(gradeMapper.selectById(any())).thenReturn(new OrgGrade());
        when(classMapper.selectCount(any())).thenReturn(0L);
        when(classMapper.insert(any(OrgClass.class))).thenAnswer(invocation -> {
            ((OrgClass) invocation.getArgument(0)).setId(3001L);
            return 1;
        });

        Long id = classService.create(newDto(1L, "1班"));

        assertThat(id).isEqualTo(3001L);
        verify(classMapper, times(1)).insert(any(OrgClass.class));
    }
}
