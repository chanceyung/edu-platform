package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.dto.OrgGradeSaveDTO;
import com.eduplatform.schoolorg.entity.OrgGrade;
import com.eduplatform.schoolorg.entity.OrgSchool;
import com.eduplatform.schoolorg.mapper.OrgGradeMapper;
import com.eduplatform.schoolorg.mapper.OrgSchoolMapper;
import com.eduplatform.schoolorg.service.impl.OrgGradeServiceImpl;
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
 * 年级业务单元测试（TDD）。覆盖：学校不存在 / 年级重复 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class OrgGradeServiceImplTest {

    @Mock
    private OrgGradeMapper gradeMapper;
    @Mock
    private OrgSchoolMapper schoolMapper;

    @InjectMocks
    private OrgGradeServiceImpl gradeService;

    private OrgGradeSaveDTO newDto(Long schoolId, String name) {
        OrgGradeSaveDTO dto = new OrgGradeSaveDTO();
        dto.setSchoolId(schoolId);
        dto.setName(name);
        dto.setLevel(7);
        return dto;
    }

    @Test
    void create_应抛异常_when_学校不存在() {
        when(schoolMapper.selectById(any())).thenReturn(null);

        assertThatThrownBy(() -> gradeService.create(newDto(1L, "七年级")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("学校不存在");
        verify(gradeMapper, never()).insert(any(OrgGrade.class));
    }

    @Test
    void create_应抛异常_when_该校年级已存在() {
        when(schoolMapper.selectById(any())).thenReturn(new OrgSchool());
        when(gradeMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> gradeService.create(newDto(1L, "七年级")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("该年级已存在");
        verify(gradeMapper, never()).insert(any(OrgGrade.class));
    }

    @Test
    void create_应成功_when_学校存在且年级可用() {
        when(schoolMapper.selectById(any())).thenReturn(new OrgSchool());
        when(gradeMapper.selectCount(any())).thenReturn(0L);
        when(gradeMapper.insert(any(OrgGrade.class))).thenAnswer(invocation -> {
            ((OrgGrade) invocation.getArgument(0)).setId(2001L);
            return 1;
        });

        Long id = gradeService.create(newDto(1L, "七年级"));

        assertThat(id).isEqualTo(2001L);
        verify(gradeMapper, times(1)).insert(any(OrgGrade.class));
    }
}
