package com.eduplatform.analytics.service;

import com.eduplatform.analytics.vo.ClassScoreVO;
import com.eduplatform.analytics.vo.DashboardVO;
import com.eduplatform.analytics.vo.ErrorBookVO;
import com.eduplatform.analytics.vo.StudentAnalysisVO;

/**
 * 成绩学情分析服务。
 */
public interface AnalyticsService {

    ClassScoreVO analyzeExamScores(Long examId);

    StudentAnalysisVO analyzeStudent(Long studentId);

    /** 错题本（学生所有答错的题） */
    ErrorBookVO getErrorBook(Long studentId);

    /** 校领导看板（全校统计） */
    DashboardVO getDashboard();
}
