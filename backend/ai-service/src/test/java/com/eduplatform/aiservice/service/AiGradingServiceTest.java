package com.eduplatform.aiservice.service;

import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.aiservice.service.AiGradingService;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AI 批改单元测试（TDD）：mock ZhipuClient，验证更新 submission + 写 ai_task_log。
 */
@ExtendWith(MockitoExtension.class)
class AiGradingServiceTest {

    @Mock private ZhipuClient zhipu;
    @Mock private HomeworkSubmissionMapper submissionMapper;
    @Mock private HomeworkMapper homeworkMapper;
    @Mock private AiTaskLogMapper logMapper;

    @InjectMocks private AiGradingService aiGradingService;

    @Test
    void grade_应调GLM评分并更新submission和写log() {
        // given
        Homework hw = new Homework();
        hw.setContent("描述光合作用");
        when(homeworkMapper.selectById(any())).thenReturn(hw);
        HomeworkSubmission sub = new HomeworkSubmission();
        sub.setId(100L);
        sub.setContent("植物利用阳光合成养分");
        when(submissionMapper.selectOne(any())).thenReturn(sub);
        // GLM 返回评分 JSON
        when(zhipu.chat(any())).thenReturn(new ZhipuClient.ChatResult("{\"score\":8,\"comment\":\"回答正确\"}", 120, 40));

        // when
        aiGradingService.grade(1L, 2L);

        // then: 更新 submission（千分制 8000，状态已批）
        ArgumentCaptor<HomeworkSubmission> subCaptor = ArgumentCaptor.forClass(HomeworkSubmission.class);
        verify(submissionMapper).updateById(subCaptor.capture());
        assertThat(subCaptor.getValue().getScore()).isEqualTo(8000);
        assertThat(subCaptor.getValue().getStatus()).isEqualTo(2);
        assertThat(subCaptor.getValue().getComment()).isEqualTo("回答正确");

        // then: 写 ai_task_log（信任内核）
        ArgumentCaptor<AiTaskLog> logCaptor = ArgumentCaptor.forClass(AiTaskLog.class);
        verify(logMapper).insert(logCaptor.capture());
        AiTaskLog log = logCaptor.getValue();
        assertThat(log.getTaskType()).isEqualTo("GRADING");
        assertThat(log.getStatus()).isEqualTo(1);
        assertThat(log.getBizRefId()).isEqualTo(100L);
    }
}
