package com.eduplatform.analytics.controller;

import com.eduplatform.analytics.service.AnalyticsService;
import com.eduplatform.analytics.vo.ClassScoreVO;
import com.eduplatform.analytics.vo.DashboardVO;
import com.eduplatform.analytics.vo.ErrorBookVO;
import com.eduplatform.analytics.vo.StudentAnalysisVO;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 成绩学情分析接口。
 */
@Tag(name = "成绩学情分析")
@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "班级成绩分析（某次考试统计）")
    @GetMapping("/exam/{examId}")
    public R<ClassScoreVO> analyzeExam(@PathVariable Long examId) {
        return R.ok(analyticsService.analyzeExamScores(examId));
    }

    @Operation(summary = "个人学情分析（跨考试+作业综合+掉队预警）")
    @GetMapping("/student/{studentId}")
    public R<StudentAnalysisVO> analyzeStudent(@PathVariable Long studentId) {
        return R.ok(analyticsService.analyzeStudent(studentId));
    }

    @Operation(summary = "错题本（学生所有答错的题）")
    @GetMapping("/errorbook/{studentId}")
    public R<ErrorBookVO> errorBook(@PathVariable Long studentId) {
        return R.ok(analyticsService.getErrorBook(studentId));
    }

    @Operation(summary = "校领导看板（全校统计+最近考试）")
    @GetMapping("/dashboard")
    public R<DashboardVO> dashboard() {
        return R.ok(analyticsService.getDashboard());
    }
}
