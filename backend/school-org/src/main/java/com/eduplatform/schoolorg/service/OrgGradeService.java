package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.dto.OrgGradeSaveDTO;
import com.eduplatform.schoolorg.entity.OrgGrade;

import java.util.List;

/**
 * 年级业务接口。
 */
public interface OrgGradeService {

    /**
     * 新增年级。校验：学校存在 + 该校年级名不重复。
     * @throws IllegalArgumentException 学校不存在或年级已存在
     */
    Long create(OrgGradeSaveDTO dto);

    /**
     * 按学校列出年级。
     */
    List<OrgGrade> listBySchool(Long schoolId);
}
