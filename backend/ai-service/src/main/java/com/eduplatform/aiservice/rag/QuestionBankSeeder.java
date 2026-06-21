package com.eduplatform.aiservice.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * ape210k 数学题库灌入（从本地JSON文件批量导入）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionBankSeeder {

    private final KbService kbService;
    private final ObjectMapper json = new ObjectMapper();
    private boolean seeded = false;

    @PostConstruct
    public void seed() {
        if (seeded) return;
        try { Thread.sleep(15000); } catch (InterruptedException ignored) {} // 等前面的Seeder
        try {
            var check = kbService.search("ape210k数学题库灌入检测", 1);
            if (!check.isEmpty() && check.get(0).score() > 0.3) return;
        } catch (Exception ignored) {}

        File sampleFile = new File("D:/Aisoft/Hongtian-office/edu-platform/data_samples/ape210k_sample_60.json");
        if (!sampleFile.exists()) {
            log.info("ape210k样本文件不存在({})，跳过题库灌入", sampleFile.getPath());
            return;
        }

        log.info("=== 开始灌入 ape210k 数学题库 ===");
        int count = 0;
        try {
            List<?> questions = json.readValue(sampleFile, List.class);
            for (Object q : questions) {
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) q;
                String question = (String) item.get("question");
                String answer = String.valueOf(item.get("answer"));
                String equation = (String) item.get("equation");
                String topic = (String) item.getOrDefault("topic", "");

                // 每道题作为一个独立知识块入库
                String content = String.format("题目：%s\n答案：%s\n解题方程：%s\n分类：%s",
                        question, answer, equation != null ? equation : "无", topic);

                try {
                    kbService.ingest("数学题-" + topic + "-" + (count + 1), "QUESTION", "Math", 0, content);
                    count++;
                } catch (Exception e) {
                    log.trace("题目入库失败 #{}: {}", count + 1, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("题库文件解析失败: {}", e.getMessage());
        }
        log.info("=== ape210k 题库灌入完成：{} 道 ===", count);
        seeded = true;
    }
}
