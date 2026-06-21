package com.eduplatform.homework.service;

import com.eduplatform.homework.dto.HomeworkReviewDTO;
import com.eduplatform.homework.dto.HomeworkSubmissionDTO;
import com.eduplatform.homework.entity.HomeworkSubmission;

/**
 * 作业提交业务接口（提交+批改）。
 */
public interface HomeworkSubmissionService {

    /** 提交作业：校验作业存在 + 未重复提交 */
    Long submit(HomeworkSubmissionDTO dto);

    /** 批改：更新得分/评语/状态（人工批改；AI 批改由 ai-service 调用） */
    void review(HomeworkReviewDTO dto);

    HomeworkSubmission getByHomeworkAndStudent(Long homeworkId, Long studentId);
}
