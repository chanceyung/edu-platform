package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.dto.OrgClassSaveDTO;
import com.eduplatform.schoolorg.entity.OrgClass;

import java.util.List;

public interface OrgClassService {

    /**
     * 新增班级。校验：年级存在 + 该年级班级名不重复。
     * @throws IllegalArgumentException 年级不存在或班级已存在
     */
    Long create(OrgClassSaveDTO dto);

    List<OrgClass> listByGrade(Long gradeId);
}
