package com.eduplatform.examengine.service;

import com.eduplatform.examengine.dto.ExamSaveDTO;
import com.eduplatform.examengine.entity.Exam;

import java.util.List;

/**
 * 考试业务接口。
 */
public interface ExamService {

    /**
     * 发布考试。校验试卷与班级存在。
     * @throws IllegalArgumentException 试卷或班级不存在
     */
    Long create(ExamSaveDTO dto);

    List<Exam> list();

    List<Exam> listByClass(Long classId);

    Exam getById(Long id);
}
