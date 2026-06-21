package com.eduplatform.aiservice.service.impl;

import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.aiservice.service.AiLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 备课实现。调智谱GLM生成教案+课堂练习。
 */
@Service
@RequiredArgsConstructor
public class AiLessonServiceImpl implements AiLessonService {

    private final ZhipuClient zhipu;
    private final AiTaskLogMapper logMapper;

    @Value("${zhipu.models.flagship:glm-5.2}") private String model;

    @Override
    public String generateLesson(String topic, Integer gradeLevel) {
        String grade = gradeLevel == 7 ? "初一" : gradeLevel == 8 ? "初二" : "初三";
        String prompt = "你是经验丰富的中学教师，请为以下内容生成一份完整教案：\n"
                + "年级：" + grade + "\n"
                + "课题：" + topic + "\n"
                + "请包含以下部分（用markdown格式）：\n"
                + "## 教学目标\n## 教学重点与难点\n## 教学过程（导入/新授/练习/小结）\n## 课堂练习（3-5题）\n"
                + "内容要具体、可操作，适合初中生。";

        long start = System.currentTimeMillis();
        ZhipuClient.ChatResult result = zhipu.chat(List.of(new ZhipuClient.ChatMessage("user", prompt)));

        AiTaskLog taskLog = new AiTaskLog();
        taskLog.setTaskType("LESSON");
        taskLog.setModel(model);
        taskLog.setInput(grade + "课题:" + topic);
        taskLog.setOutput(result.content().length() > 500 ? result.content().substring(0, 500) : result.content());
        taskLog.setInputTokens(result.inputTokens());
        taskLog.setOutputTokens(result.outputTokens());
        taskLog.setLatencyMs((int) (System.currentTimeMillis() - start));
        taskLog.setStatus(1);
        taskLog.setActor("system");
        logMapper.insert(taskLog);

        return result.content();
    }
}
