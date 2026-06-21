package com.eduplatform.aiservice.orchestrator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.aiservice.entity.AiAgentTask;
import com.eduplatform.aiservice.entity.AiTaskLog;
import com.eduplatform.aiservice.mapper.AiAgentTaskMapper;
import com.eduplatform.aiservice.mapper.AiTaskLogMapper;
import com.eduplatform.aiservice.tool.GlmToolCaller;
import com.eduplatform.common.exception.BizException;
import com.eduplatform.common.response.ErrorCode;
import com.eduplatform.ruoyibase.security.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 长程任务管理：创建→运行（SSE推送进度）→完成/恢复/取消。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentTaskService {

    private final AiAgentTaskMapper taskMapper;
    private final AiTaskLogMapper logMapper;
    private final GlmToolCaller toolCaller;
    private final ObjectMapper json = new ObjectMapper();

    /** 创建任务并同步执行（SSE 推进度） */
    public SseEmitter createAndRun(String goal) {
        // 创建任务
        AiAgentTask task = new AiAgentTask();
        task.setGoal(goal);
        task.setStatus(0);
        task.setProgressPct(0);
        task.setCurrentStep("初始化");
        taskMapper.insert(task);

        SseEmitter emitter = new SseEmitter(300_000L); // 5 min timeout
        Long taskId = task.getId();

        // 异步执行
        new Thread(() -> {
            try {
                runTask(taskId, goal, emitter);
            } catch (Exception e) {
                log.error("Agent任务执行失败 taskId={}", taskId, e);
                failTask(taskId, e.getMessage(), emitter);
            }
        });

        return emitter;
    }

    private void runTask(Long taskId, String goal, SseEmitter emitter) throws IOException {
        updateProgress(taskId, 10, "GLM 分析目标并规划步骤...", emitter);

        // 调用 GLM Agent（带工具）
        updateProgress(taskId, 30, "GLM 调用工具执行...", emitter);
        Map<String, Object> result = toolCaller.executeWithTools(goal, 3);

        updateProgress(taskId, 80, "整理结果...", emitter);

        // 保存结果
        AiAgentTask task = taskMapper.selectById(taskId);
        task.setStatus(2);
        task.setProgressPct(100);
        task.setCurrentStep("完成");
        task.setResultJson(json.writeValueAsString(result));
        taskMapper.updateById(task);

        // 推送最终结果
        emitter.send(SseEmitter.event().name("complete").data(Map.of(
                "taskId", taskId,
                "status", "completed",
                "answer", result.get("answer"),
                "toolCalls", result.get("toolCalls")
        )));
        emitter.complete();
    }

    private void updateProgress(Long taskId, int pct, String step, SseEmitter emitter) throws IOException {
        AiAgentTask task = new AiAgentTask();
        task.setId(taskId);
        task.setStatus(1);
        task.setProgressPct(pct);
        task.setCurrentStep(step);
        taskMapper.updateById(task);
        emitter.send(SseEmitter.event().name("progress").data(Map.of(
                "taskId", taskId, "progress", pct, "step", step
        )));
    }

    private void failTask(Long taskId, String error, SseEmitter emitter) {
        try {
            AiAgentTask task = new AiAgentTask();
            task.setId(taskId);
            task.setStatus(4);
            task.setCurrentStep("失败: " + error);
            taskMapper.updateById(task);
            emitter.send(SseEmitter.event().name("error").data(Map.of("taskId", taskId, "error", error)));
            emitter.complete();
        } catch (Exception ignored) {}
    }

    /** 查任务详情 */
    public AiAgentTask getById(Long id) {
        AiAgentTask t = taskMapper.selectById(id);
        if (t == null) throw new BizException(ErrorCode.DATA_NOT_FOUND);
        return t;
    }

    /** 列出任务 */
    public List<AiAgentTask> listRecent() {
        return taskMapper.selectList(new LambdaQueryWrapper<AiAgentTask>()
                .orderByDesc(AiAgentTask::getCreateTime).last("LIMIT 20"));
    }
}
