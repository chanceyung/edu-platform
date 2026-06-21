package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.dto.OrgSchoolSaveDTO;
import com.eduplatform.schoolorg.entity.OrgSchool;

import java.util.List;

/**
 * 学校业务接口。
 */
public interface OrgSchoolService {

    /**
     * 新增学校。校验编码租户内唯一。
     * @param dto 入参
     * @return 学校主键
     * @throws IllegalArgumentException 编码已存在
     */
    Long create(OrgSchoolSaveDTO dto);

    /**
     * 按主键查询。
     */
    OrgSchool getById(Long id);

    /**
     * 列表查询（当前租户）。
     */
    List<OrgSchool> list();
}
