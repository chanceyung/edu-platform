package com.eduplatform.aiservice.tool;

import com.eduplatform.examengine.dto.ExamComposeDTO;
import com.eduplatform.examengine.service.ExamPaperService;
import com.eduplatform.homework.dto.HomeworkReviewDTO;
import com.eduplatform.homework.service.HomeworkSubmissionService;
import com.eduplatform.aiservice.service.AiGradingService;
import com.eduplatform.aiservice.service.AiCommentService;
import com.eduplatform.aiservice.service.AiLessonService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 工具注册器：启动时把系统能力封装为 MCP 工具注册到 ToolRegistry。
 * 这就是 Agent 的"手脚"——注册后 GLM 可通过 function calling 调用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistrar {

    private final ToolRegistry registry;
    private final AiGradingService gradingService;
    private final AiCommentService commentService;
    private final AiLessonService lessonService;
    private final ExamPaperService paperService;
    private final HomeworkSubmissionService submissionService;

    @PostConstruct
    public void registerAll() {
        // 1. AI批改工具（WRITE——改作业分数）
        registry.register(new ToolDefinition(
                "homework_grade", "ai-service", "AI批改学生作业（智谱GLM评分+评语）",
                ToolDefinition.SideEffect.WRITE, false,
                List.of("ADMIN", "TEACHER"),
                Map.of("homeworkId", Long.class, "studentId", Long.class),
                input -> {
                    Long hwId = ((Number) input.get("homeworkId")).longValue();
                    Long stuId = ((Number) input.get("studentId")).longValue();
                    gradingService.grade(hwId, stuId);
                    return Map.of("status", "graded", "homeworkId", hwId, "studentId", stuId);
                }
        ));

        // 2. AI评语工具（WRITE——生成评语）
        registry.register(new ToolDefinition(
                "student_comment", "ai-service", "AI生成学生评语（基于学情数据）",
                ToolDefinition.SideEffect.WRITE, false,
                List.of("ADMIN", "TEACHER"),
                Map.of("studentId", Long.class),
                input -> {
                    Long stuId = ((Number) input.get("studentId")).longValue();
                    String comment = commentService.generateComment(stuId);
                    return Map.of("comment", comment);
                }
        ));

        // 3. AI备课工具（READ_ONLY——只生成内容不改数据）
        registry.register(new ToolDefinition(
                "lesson_plan", "ai-service", "AI生成教案（教学目标/重点/过程/练习）",
                ToolDefinition.SideEffect.READ_ONLY, false,
                List.of("ADMIN", "TEACHER"),
                Map.of("topic", String.class, "gradeLevel", Integer.class),
                input -> {
                    String topic = (String) input.get("topic");
                    Integer grade = input.get("gradeLevel") != null ? ((Number) input.get("gradeLevel")).intValue() : 7;
                    String lesson = lessonService.generateLesson(topic, grade);
                    return Map.of("lesson", lesson);
                }
        ));

        // 4. 组卷工具（WRITE——修改试卷）
        registry.register(new ToolDefinition(
                "qbank_compose_paper", "exam-engine", "手动选题添加到试卷",
                ToolDefinition.SideEffect.WRITE, false,
                List.of("ADMIN", "TEACHER"),
                Map.of("paperId", Long.class, "items", List.class),
                input -> {
                    Long paperId = ((Number) input.get("paperId")).longValue();
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) input.get("items");
                    List<ExamComposeDTO.ComposeItem> items = itemsRaw.stream().map(m -> {
                        ExamComposeDTO.ComposeItem item = new ExamComposeDTO.ComposeItem();
                        item.setQuestionId(((Number) m.get("questionId")).longValue());
                        item.setScore(((Number) m.get("score")).intValue());
                        return item;
                    }).toList();
                    paperService.compose(new ExamComposeDTO() {{ setPaperId(paperId); setItems(items); }});
                    return Map.of("status", "composed", "count", items.size());
                }
        ));

        // 5. 发布家校通知（EXTERNAL_NOTIFY——对外发送，需人工确认）
        registry.register(new ToolDefinition(
                "hs_publish_notice", "home-school", "发布家校通知（对外发送，需人工确认）",
                ToolDefinition.SideEffect.EXTERNAL_NOTIFY, true,
                List.of("ADMIN", "TEACHER"),
                Map.of("title", String.class, "content", String.class),
                input -> {
                    // 实际发送逻辑由 home-school 模块处理，这里返回待确认
                    return Map.of("status", "pending_review", "title", input.get("title"), "message", "通知已生成，等待人工确认后发送");
                }
        ));

        log.info("=== MCP 工具注册完成：{} 个工具 ===", registry.all().size());
        registry.all().forEach(t -> log.info("  - {} ({}) [{}]", t.name(), t.sideEffect(), t.requiredRoles()));
    }
}
