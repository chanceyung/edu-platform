package com.eduplatform.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.homework.dto.HomeworkReviewDTO;
import com.eduplatform.homework.dto.HomeworkSubmissionDTO;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.entity.HomeworkSubmission;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.mapper.HomeworkSubmissionMapper;
import com.eduplatform.homework.service.HomeworkSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 作业提交业务实现。submit 校验作业存在+未重复；review 更新批改结果。
 */
@Service
@RequiredArgsConstructor
public class HomeworkSubmissionServiceImpl implements HomeworkSubmissionService {

    private final HomeworkSubmissionMapper submissionMapper;
    private final HomeworkMapper homeworkMapper;

    @Override
    public Long submit(HomeworkSubmissionDTO dto) {
        Homework hw = homeworkMapper.selectById(dto.getHomeworkId());
        if (hw == null) {
            throw new IllegalArgumentException("作业不存在: " + dto.getHomeworkId());
        }
        HomeworkSubmission exist = submissionMapper.selectOne(new LambdaQueryWrapper<HomeworkSubmission>()
                .eq(HomeworkSubmission::getHomeworkId, dto.getHomeworkId())
                .eq(HomeworkSubmission::getStudentId, dto.getStudentId()));
        if (exist != null) {
            throw new IllegalArgumentException("该学生已提交此作业");
        }
        HomeworkSubmission sub = new HomeworkSubmission();
        sub.setHomeworkId(dto.getHomeworkId());
        sub.setStudentId(dto.getStudentId());
        sub.setContent(dto.getContent());
        sub.setSubmitTime(LocalDateTime.now());
        sub.setScore(0);
        sub.setStatus(1); // 已交未批
        submissionMapper.insert(sub);
        return sub.getId();
    }

    @Override
    public void review(HomeworkReviewDTO dto) {
        HomeworkSubmission sub = submissionMapper.selectById(dto.getSubmissionId());
        if (sub == null) {
            throw new IllegalArgumentException("提交不存在: " + dto.getSubmissionId());
        }
        sub.setScore(dto.getScore());
        sub.setComment(dto.getComment());
        sub.setStatus(2); // 已批
        submissionMapper.updateById(sub);
    }

    @Override
    public HomeworkSubmission getByHomeworkAndStudent(Long homeworkId, Long studentId) {
        return submissionMapper.selectOne(new LambdaQueryWrapper<HomeworkSubmission>()
                .eq(HomeworkSubmission::getHomeworkId, homeworkId)
                .eq(HomeworkSubmission::getStudentId, studentId));
    }
}
