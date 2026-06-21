package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.dto.OrgSchoolSaveDTO;
import com.eduplatform.schoolorg.entity.OrgSchool;
import com.eduplatform.schoolorg.mapper.OrgSchoolMapper;
import com.eduplatform.schoolorg.service.impl.OrgSchoolServiceImpl;
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
 * 学校业务单元测试（TDD）。用 Mockito 隔离 Mapper。
 * 覆盖核心业务：编码唯一校验。
 */
@ExtendWith(MockitoExtension.class)
class OrgSchoolServiceImplTest {

    @Mock
    private OrgSchoolMapper schoolMapper;

    @InjectMocks
    private OrgSchoolServiceImpl schoolService;

    private OrgSchoolSaveDTO newDto(String code) {
        OrgSchoolSaveDTO dto = new OrgSchoolSaveDTO();
        dto.setName("测试中学");
        dto.setCode(code);
        dto.setSchoolType("MIDDLE");
        return dto;
    }

    @Test
    void create_应抛异常_when_学校编码已存在() {
        // given: 编码已存在
        when(schoolMapper.selectCount(any())).thenReturn(1L);

        // when / then
        assertThatThrownBy(() -> schoolService.create(newDto("S001")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("S001");
        // 校验未发生插入
        verify(schoolMapper, never()).insert(any(OrgSchool.class));
    }

    @Test
    void create_应成功并返回主键_when_学校编码可用() {
        // given: 编码可用
        when(schoolMapper.selectCount(any())).thenReturn(0L);
        when(schoolMapper.insert(any(OrgSchool.class))).thenAnswer(invocation -> {
            ((OrgSchool) invocation.getArgument(0)).setId(1001L);
            return 1;
        });

        // when
        Long id = schoolService.create(newDto("S002"));

        // then
        assertThat(id).isEqualTo(1001L);
        verify(schoolMapper, times(1)).insert(any(OrgSchool.class));
    }
}
