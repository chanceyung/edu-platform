package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.entity.OrgStudent;
import com.eduplatform.schoolorg.mapper.OrgStudentMapper;
import com.eduplatform.schoolorg.service.impl.OrgStudentServiceImpl;
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
 * 学生业务单元测试（补齐铁律：每个 service 必须有测试）。
 * 覆盖：学号重复 / 学号可用。
 */
@ExtendWith(MockitoExtension.class)
class OrgStudentServiceImplTest {

    @Mock
    private OrgStudentMapper studentMapper;

    @InjectMocks
    private OrgStudentServiceImpl studentService;

    @Test
    void create_应抛异常_when_学号已存在() {
        when(studentMapper.selectOne(any())).thenReturn(new OrgStudent());

        OrgStudent s = new OrgStudent();
        s.setStudentNo("2026001");
        assertThatThrownBy(() -> studentService.create(s))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("学号已存在");
        verify(studentMapper, never()).insert(any(OrgStudent.class));
    }

    @Test
    void create_应成功_when_学号可用() {
        when(studentMapper.selectOne(any())).thenReturn(null);
        when(studentMapper.insert(any(OrgStudent.class))).thenAnswer(invocation -> {
            ((OrgStudent) invocation.getArgument(0)).setId(4001L);
            return 1;
        });

        OrgStudent s = new OrgStudent();
        s.setStudentNo("2026001");
        Long id = studentService.create(s);

        assertThat(id).isEqualTo(4001L);
        verify(studentMapper, times(1)).insert(any(OrgStudent.class));
    }
}
