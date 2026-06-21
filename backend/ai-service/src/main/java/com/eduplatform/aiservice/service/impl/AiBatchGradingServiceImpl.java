package com.eduplatform.aiservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 批量批改：一次批改全班作业，单学生失败不阻断。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiBatchGradingServiceImpl {

    private final com.eduplatform.aiservice.service.AiGradingService gradingService;
    private final HomeworkMapper homeworkMapper;
    private final HomeworkSubmissionMapper submissionMapper;
    private final AiTaskLogMapper logMapper;

    /**
     * 批量批改某作业的所有已提交学生。
     * @return {total, success, failed, details}
     */
    public Map<String, Object> gradeBatch(Long homeworkId) {
        Homework hw = homeworkMapper.selectById(homeworkId);
        if (hw == null) {
            throw new IllegalArgumentException("作业不存在: " + homeworkId);
        }

        List<HomeworkSubmission> submissions = submissionMapper.selectList(new LambdaQueryWrapper<HomeworkSubmission>()
                .eq(HomeworkSubmission::getHomeworkId, homeworkId)
                .eq(HomeworkSubmission::getStatus, 1)); // 只批未批的

        int success = 0, failed = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (HomeworkSubmission sub : submissions) {
            try {
                gradingService.grade(homeworkId, sub.getStudentId());
                success++;
                details.add(Map.of("studentId", sub.getStudentId(), "status", "graded"));
            } catch (Exception e) {
                failed++;
                details.add(Map.of("studentId", sub.getStudentId(), "status", "failed", "error", e.getMessage()));
                log.warn("批量批改失败 homeworkId={} studentId={}: {}", homeworkId, sub.getStudentId(), e.getMessage());
            }
        }

        // 写汇总日志
        AiTaskLog taskLog = new AiTaskLog();
        taskLog.setTaskType("BATCH_GRADING");
        taskLog.setInput("作业" + homeworkId + " 共" + submissions.size() + "份");
        taskLog.setOutput("成功" + success + "失败" + failed);
        taskLog.setStatus(failed == 0 ? 1 : 2);
        taskLog.setActor("system");
        taskLog.setBizRefId(homeworkId);
        logMapper.insert(taskLog);

        return Map.of("total", submissions.size(), "success", success, "failed", failed, "details", details);
    }
}
