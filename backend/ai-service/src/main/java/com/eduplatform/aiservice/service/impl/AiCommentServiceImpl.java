package com.eduplatform.aiservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.aiservice.service.AiCommentService;
import com.eduplatform.examengine.entity.ExamRecord;
import com.eduplatform.examengine.mapper.ExamRecordMapper;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 评语生成。基于学生考试+作业数据，调智谱GLM生成个性化中文评语。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCommentServiceImpl implements AiCommentService {

    private final ZhipuClient zhipu;
    private final ExamRecordMapper examRecordMapper;
    private final HomeworkSubmissionMapper submissionMapper;
    private final AiTaskLogMapper logMapper;

    @Value("${zhipu.models.standard:glm-5.1}") private String model;

    @Override
    public String generateComment(Long studentId) {
        // 查学生考试记录
        List<ExamRecord> exams = examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getStudentId, studentId));
        // 查学生作业记录（已批）
        List<HomeworkSubmission> hws = submissionMapper.selectList(new LambdaQueryWrapper<HomeworkSubmission>()
                .eq(HomeworkSubmission::getStudentId, studentId)
                .eq(HomeworkSubmission::getStatus, 2));

        // 构建学情摘要
        StringBuilder summary = new StringBuilder();
        if (!exams.isEmpty()) {
            double avgRate = exams.stream()
                    .filter(r -> r.getPaperScore() > 0)
                    .mapToDouble(r -> r.getSystemScore() * 100.0 / r.getPaperScore())
                    .average().orElse(0);
            summary.append("考试").append(exams.size()).append("次，平均得分率").append(Math.round(avgRate)).append("%。");
        }
        if (!hws.isEmpty()) {
            long goodCount = hws.stream().filter(s -> s.getScore() >= 8000).count();
            summary.append("作业").append(hws.size()).append("次，其中优秀").append(goodCount).append("次。");
        }
        if (summary.length() == 0) {
            summary.append("暂无考试和作业数据。");
        }

        // 调智谱生成评语
        String prompt = "你是班主任，为学生写一段期末评语（100字以内，中文，积极鼓励为主）。\n"
                + "学生学情：" + summary + "\n"
                + "请直接输出评语，不要加引号或其他标记。";

        long start = System.currentTimeMillis();
        ZhipuClient.ChatResult result = zhipu.chatStandard(List.of(new ZhipuClient.ChatMessage("user", prompt)));

        // 写 ai_task_log
        AiTaskLog taskLog = new AiTaskLog();
        taskLog.setTaskType("COMMENT");
        taskLog.setModel(model);
        taskLog.setInput("学生" + studentId + "学情：" + summary);
        taskLog.setOutput(result.content());
        taskLog.setInputTokens(result.inputTokens());
        taskLog.setOutputTokens(result.outputTokens());
        taskLog.setLatencyMs((int) (System.currentTimeMillis() - start));
        taskLog.setStatus(1);
        taskLog.setActor("system");
        taskLog.setBizRefId(studentId);
        logMapper.insert(taskLog);

        return result.content();
    }
}
