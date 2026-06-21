package com.eduplatform.homework.service;

import com.eduplatform.homework.dto.HomeworkSaveDTO;
import com.eduplatform.homework.entity.Homework;

import java.util.List;

/**
 * 作业业务接口。
 */
public interface HomeworkService {

    /** 布置作业，校验班级存在 */
    Long create(HomeworkSaveDTO dto);

    List<Homework> listByClass(Long classId);

    Homework getById(Long id);
}
