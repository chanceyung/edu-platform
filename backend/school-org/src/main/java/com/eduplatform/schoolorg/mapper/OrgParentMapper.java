package com.eduplatform.schoolorg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduplatform.schoolorg.entity.OrgParent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrgParentMapper extends BaseMapper<OrgParent> {

    @Select("SELECT p.* FROM org_parent p JOIN org_student_parent sp ON p.id = sp.parent_id AND p.is_deleted = 0 WHERE sp.student_id = #{studentId}")
    List<OrgParent> selectByStudentId(Long studentId);
}
