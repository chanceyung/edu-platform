package com.eduplatform.aiservice.service;

import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 批改服务（P2 核心：主观题 AI 评分+评语）。
 * 流程：取作业+提交 → 调智谱GLM评分 → 更新submission → 写ai_task_log（信任内核，可追溯）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiGradingService {

    private final ZhipuClient zhipu;
    private final HomeworkSubmissionMapper submissionMapper;
    private final HomeworkMapper homeworkMapper;
    private final AiTaskLogMapper logMapper;
    private final ObjectMapper json = new ObjectMapper();

    @Value("${zhipu.models.flagship:glm-5.2}") private String model;

    /**
     * AI 批改作业提交。
     * @param homeworkId  作业ID
     * @param studentId   学生ID
     */
    public void grade(Long homeworkId, Long studentId) {
        Homework hw = homeworkMapper.selectById(homeworkId);
        if (hw == null) throw new IllegalArgumentException("作业不存在: " + homeworkId);
        HomeworkSubmission sub = submissionMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HomeworkSubmission>()
                        .eq(HomeworkSubmission::getHomeworkId, homeworkId)
                        .eq(HomeworkSubmission::getStudentId, studentId));
        if (sub == null) throw new IllegalArgumentException("学生提交不存在");

        long start = System.currentTimeMillis();
        String prompt = buildPrompt(hw.getContent(), sub.getContent());
        ZhipuClient.ChatResult result = zhipu.chat(List.of(new ZhipuClient.ChatMessage("user", prompt)));

        // 解析 GLM 返回的 {score, comment}
        int scoreYuan; String comment;
        try {
            JsonNode node = json.readTree(extractJson(result.content()));
            scoreYuan = node.get("score").asInt();
            comment = node.get("comment").asText();
        } catch (Exception e) {
            log.warn("AI 批改返回解析失败，降级: {}", result.content());
            scoreYuan = 0; comment = "AI 批改结果解析失败，请人工复核";
        }

        // 更新 submission
        sub.setScore(scoreYuan * 1000);  // 千分制
        sub.setComment(comment);
        sub.setStatus(2);
        submissionMapper.updateById(sub);

        // 写 ai_task_log（信任内核，CLAUDE.md 铁律：AI 必可追溯）
        AiTaskLog taskLog = new AiTaskLog();
        taskLog.setTaskType("GRADING");
        taskLog.setModel(model);
        taskLog.setInput("作业" + homeworkId + "/学生" + studentId);
        taskLog.setOutput(comment);
        taskLog.setInputTokens(result.inputTokens());
        taskLog.setOutputTokens(result.outputTokens());
        taskLog.setLatencyMs((int) (System.currentTimeMillis() - start));
        taskLog.setStatus(1);
        taskLog.setActor("system");
        taskLog.setBizRefId(sub.getId());
        logMapper.insert(taskLog);
    }

    private String buildPrompt(String requirement, String studentAnswer) {
        return "你是严谨的中学老师，批改学生作业。\n"
                + "作业要求：" + requirement + "\n"
                + "学生答案：" + studentAnswer + "\n"
                + "满分10分。请根据作答质量评分（0-10的整数），并给出简短中文评语（不超过50字）。\n"
                + "严格只返回JSON，不要任何其他文字：{\"score\":数字,\"comment\":\"评语\"}";
    }

    /** 提取 GLM 返回中的 JSON（兼容 markdown 代码块包裹） */
    private String extractJson(String text) {
        String t = text.trim();
        if (t.contains("```")) {
            int s = t.indexOf('{');
            int e = t.lastIndexOf('}');
            if (s >= 0 && e > s) return t.substring(s, e + 1);
        }
        return t;
    }
}
