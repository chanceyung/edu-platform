package com.eduplatform.aiservice.service;

/**
 * AI 备课助手。
 */
public interface AiLessonService {

    /**
     * 根据主题+年级，调智谱GLM生成教案（含教学目标/重点难点/教学过程/课堂练习）。
     */
    String generateLesson(String topic, Integer gradeLevel);
}
