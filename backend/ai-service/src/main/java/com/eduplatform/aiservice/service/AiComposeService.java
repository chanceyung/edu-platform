package com.eduplatform.aiservice.service;

/**
 * AI 智能组卷。按年级/题型/难度自动从题库选题，调组卷接口添加到试卷。
 */
public interface AiComposeService {

    /**
     * 自动组卷：查候选 → 随机选题 → 添加到试卷。
     * @return 实际选中的题数
     */
    int autoCompose(Long paperId, Integer gradeLevel, Integer type, Integer difficulty, int count);
}
