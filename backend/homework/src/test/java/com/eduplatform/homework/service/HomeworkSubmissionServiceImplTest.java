package com.eduplatform.homework.service;

import com.eduplatform.homework.dto.HomeworkSubmissionDTO;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import com.eduplatform.homework.service.impl.HomeworkSubmissionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 作业提交单元测试（TDD）：作业不存在 / 重复提交 / 成功。
 */
@ExtendWith(MockitoExtension.class)
class HomeworkSubmissionServiceImplTest {

    @Mock private HomeworkSubmissionMapper submissionMapper;
    @Mock private HomeworkMapper homeworkMapper;

    @InjectMocks private HomeworkSubmissionServiceImpl submissionService;

    private HomeworkSubmissionDTO dto() {
        HomeworkSubmissionDTO dto = new HomeworkSubmissionDTO();
        dto.setHomeworkId(1L);
        dto.setStudentId(2L);
        dto.setContent("我的答案");
        return dto;
    }

    @Test
    void submit_应抛异常_when_作业不存在() {
        when(homeworkMapper.selectById(any())).thenReturn(null);
        assertThatThrownBy(() -> submissionService.submit(dto()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("作业不存在");
        verify(submissionMapper, never()).insert(any(HomeworkSubmission.class));
    }

    @Test
    void submit_应抛异常_when_重复提交() {
        when(homeworkMapper.selectById(any())).thenReturn(new Homework());
        when(submissionMapper.selectOne(any())).thenReturn(new HomeworkSubmission());
        assertThatThrownBy(() -> submissionService.submit(dto()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("已提交");
        verify(submissionMapper, never()).insert(any(HomeworkSubmission.class));
    }

    @Test
    void submit_应成功_when_首次提交() {
        when(homeworkMapper.selectById(any())).thenReturn(new Homework());
        when(submissionMapper.selectOne(any())).thenReturn(null);
        when(submissionMapper.insert(any(HomeworkSubmission.class))).thenReturn(1);
        submissionService.submit(dto());
        verify(submissionMapper, times(1)).insert(any(HomeworkSubmission.class));
    }
}
