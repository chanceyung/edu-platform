package com.eduplatform.examengine.service;

import com.eduplatform.examengine.dto.ExamRecordSubmitDTO;
import com.eduplatform.examengine.entity.ExamRecord;

/**
 * 答卷业务接口（提交+客观题自动判分）。
 */
public interface ExamRecordService {

    /**
     * 提交答卷：创建整卷+逐题答案，客观题(选择/判断)自动判分，主观题(填空/简答)待判。
     * @return 答卷ID
     */
    Long submit(ExamRecordSubmitDTO dto);

    /** 按考试+学生查答卷 */
    ExamRecord getByExamAndStudent(Long examId, Long studentId);
}
