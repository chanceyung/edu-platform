package com.eduplatform.schoolorg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduplatform.schoolorg.entity.OrgStudent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生学籍 Mapper。只做持久化，禁止写业务 SQL（业务在 Service）。
 */
@Mapper
public interface OrgStudentMapper extends BaseMapper<OrgStudent> {
}
