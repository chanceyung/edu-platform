package com.eduplatform.schoolorg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduplatform.schoolorg.entity.OrgSchool;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学校 Mapper。只做持久化，禁止写业务。
 */
@Mapper
public interface OrgSchoolMapper extends BaseMapper<OrgSchool> {
}
