package com.eduplatform.aiservice.service;

/**
 * AI 评语生成服务。
 */
public interface AiCommentService {

    /**
     * 根据学生考试+作业数据，调智谱GLM生成中文评语。
     * @param studentId 学生ID
     * @return AI 生成的评语文本
     */
    String generateComment(Long studentId);
}
