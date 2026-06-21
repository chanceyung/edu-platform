package com.eduplatform.analytics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.analytics.service.AnalyticsService;
import com.eduplatform.analytics.vo.ClassScoreVO;
import com.eduplatform.analytics.vo.DashboardVO;
import com.eduplatform.analytics.vo.ErrorBookVO;
import com.eduplatform.analytics.vo.StudentAnalysisVO;
import com.eduplatform.examengine.entity.Exam;
import com.eduplatform.examengine.entity.ExamPaper;
import com.eduplatform.examengine.entity.ExamRecord;
import com.eduplatform.examengine.entity.ExamRecordAnswer;
import com.eduplatform.examengine.mapper.ExamMapper;
import com.eduplatform.examengine.mapper.ExamPaperMapper;
import com.eduplatform.examengine.mapper.ExamRecordAnswerMapper;
import com.eduplatform.examengine.mapper.ExamRecordMapper;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import com.eduplatform.questionbank.entity.QbankQuestion;
import com.eduplatform.questionbank.mapper.QbankQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 学情分析实现。聚合 exam_record + homework_submission 数据。
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ExamRecordMapper examRecordMapper;
    private final ExamRecordAnswerMapper examRecordAnswerMapper;
    private final ExamMapper examMapper;
    private final ExamPaperMapper examPaperMapper;
    private final HomeworkSubmissionMapper submissionMapper;
    private final HomeworkMapper homeworkMapper;
    private final QbankQuestionMapper questionMapper;

    @Override
    public ClassScoreVO analyzeExamScores(Long examId) {
        Exam exam = examMapper.selectById(examId);
        List<ExamRecord> records = examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getExamId, examId));

        if (records.isEmpty()) {
            return ClassScoreVO.builder()
                    .examId(examId)
                    .examName(exam == null ? "" : exam.getName())
                    .studentCount(0).avgScore(0).maxScore(0).minScore(0).passRate(0)
                    .segments(List.of()).students(List.of()).build();
        }

        List<Integer> scores = records.stream().map(ExamRecord::getSystemScore).sorted().toList();
        int count = scores.size();
        int sum = scores.stream().mapToInt(Integer::intValue).sum();
        int avg = count > 0 ? sum / count : 0;
        int max = scores.get(count - 1);
        int min = scores.get(0);
        int paperScore = records.get(0).getPaperScore();
        double passRate = paperScore > 0 ? scores.stream().filter(s -> s >= paperScore * 0.6).count() * 100.0 / count : 0;

        // 分数段
        List<ClassScoreVO.ScoreSegment> segments = buildSegments(scores, paperScore);

        // 学生明细（降序）
        List<ClassScoreVO.StudentScoreDetail> details = records.stream()
                .sorted(Comparator.comparingInt(ExamRecord::getSystemScore).reversed())
                .map(r -> ClassScoreVO.StudentScoreDetail.builder()
                        .studentId(r.getStudentId())
                        .score(r.getSystemScore())
                        .status(r.getStatus() == 2 ? "已完成" : "含主观待判")
                        .build())
                .toList();

        return ClassScoreVO.builder()
                .examId(examId)
                .examName(exam == null ? "" : exam.getName())
                .studentCount(count)
                .avgScore(avg).maxScore(max).minScore(min)
                .passRate(Math.round(passRate * 10) / 10.0)
                .segments(segments).students(details).build();
    }

    @Override
    public StudentAnalysisVO analyzeStudent(Long studentId) {
        // 考试得分率
        List<ExamRecord> examRecords = examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getStudentId, studentId));
        double examAvgRate = 0;
        List<StudentAnalysisVO.ExamScoreItem> examItems = new ArrayList<>();
        if (!examRecords.isEmpty()) {
            double totalRate = 0;
            for (ExamRecord r : examRecords) {
                double rate = r.getPaperScore() > 0 ? r.getSystemScore() * 100.0 / r.getPaperScore() : 0;
                totalRate += rate;
                examItems.add(StudentAnalysisVO.ExamScoreItem.builder()
                        .examId(r.getExamId())
                        .score(r.getSystemScore())
                        .paperScore(r.getPaperScore())
                        .rate(Math.round(rate * 10) / 10.0)
                        .status(r.getStatus() == 2 ? "已完成" : "含主观待判")
                        .build());
            }
            examAvgRate = totalRate / examRecords.size();
        }

        // 作业得分率
        List<HomeworkSubmission> hwSubs = submissionMapper.selectList(new LambdaQueryWrapper<HomeworkSubmission>()
                .eq(HomeworkSubmission::getStudentId, studentId)
                .eq(HomeworkSubmission::getStatus, 2));
        double hwAvgRate = 0;
        if (!hwSubs.isEmpty()) {
            hwAvgRate = hwSubs.stream()
                    .filter(s -> s.getScore() > 0)
                    .mapToInt(s -> Math.min(s.getScore() * 100 / 10000, 100))
                    .average().orElse(0);
        }

        // 预警：综合 < 60%
        double combined = (examAvgRate + hwAvgRate) / 2;

        return StudentAnalysisVO.builder()
                .studentId(studentId)
                .examAvgRate(Math.round(examAvgRate * 10) / 10.0)
                .homeworkAvgRate(Math.round(hwAvgRate * 10) / 10.0)
                .atRisk(combined < 60)
                .examScores(examItems)
                .build();
    }

    /** 构建 5 段分布：90-100/80-89/70-79/60-69/<60 */
    private List<ClassScoreVO.ScoreSegment> buildSegments(List<Integer> scores, int paperScore) {
        String[] labels = {"90-100", "80-89", "70-79", "60-69", "<60"};
        int[] counts = new int[5];
        for (int s : scores) {
            double pct = paperScore > 0 ? s * 100.0 / paperScore : 0;
            if (pct >= 90) counts[0]++;
            else if (pct >= 80) counts[1]++;
            else if (pct >= 70) counts[2]++;
            else if (pct >= 60) counts[3]++;
            else counts[4]++;
        }
        List<ClassScoreVO.ScoreSegment> segs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            segs.add(ClassScoreVO.ScoreSegment.builder().label(labels[i]).count(counts[i]).build());
        }
        return segs;
    }

    @Override
    public ErrorBookVO getErrorBook(Long studentId) {
        // 查该学生所有答卷
        List<ExamRecord> records = examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getStudentId, studentId));
        List<ErrorBookVO.ErrorItem> errors = new ArrayList<>();
        for (ExamRecord r : records) {
            // 查逐题答案中答错的（do_right=0）
            List<ExamRecordAnswer> wrongAnswers = examRecordAnswerMapper.selectList(
                    new LambdaQueryWrapper<ExamRecordAnswer>()
                            .eq(ExamRecordAnswer::getRecordId, r.getId())
                            .eq(ExamRecordAnswer::getDoRight, 0));
            for (ExamRecordAnswer a : wrongAnswers) {
                errors.add(ErrorBookVO.ErrorItem.builder()
                        .questionId(a.getQuestionId())
                        .examId(r.getExamId())
                        .answer(a.getAnswer())
                        .doRight(0)
                        .build());
            }
        }
        return ErrorBookVO.builder()
                .studentId(studentId)
                .totalErrors(errors.size())
                .errors(errors)
                .build();
    }

    @Override
    public DashboardVO getDashboard() {
        long totalExams = examMapper.selectCount(null);
        long totalPapers = examPaperMapper.selectCount(null);
        long totalQuestions = questionMapper.selectCount(null);
        long totalHomeworks = homeworkMapper.selectCount(null);

        // 最近5次考试统计
        List<Exam> recentExams = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .orderByDesc(Exam::getCreateTime).last("LIMIT 5"));
        List<DashboardVO.ExamSummary> summaries = new ArrayList<>();
        for (Exam exam : recentExams) {
            List<ExamRecord> records = examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>()
                    .eq(ExamRecord::getExamId, exam.getId()));
            int avg = records.isEmpty() ? 0 : records.stream().mapToInt(ExamRecord::getSystemScore).sum() / records.size();
            summaries.add(DashboardVO.ExamSummary.builder()
                    .examId(exam.getId())
                    .name(exam.getName())
                    .studentCount(records.size())
                    .avgScore(avg)
                    .build());
        }

        return DashboardVO.builder()
                .totalExams((int) totalExams)
                .totalPapers((int) totalPapers)
                .totalQuestions((int) totalQuestions)
                .totalHomeworks((int) totalHomeworks)
                .recentExams(summaries)
                .build();
    }
}
