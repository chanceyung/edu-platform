package com.eduplatform.schoolorg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.entity.OrgTeacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrgTeacherMapper extends BaseMapper<OrgTeacher> {

    @Select("SELECT c.* FROM org_teacher_class_subject tcs " +
            "JOIN org_class c ON tcs.class_id = c.id AND c.is_deleted = 0 " +
            "WHERE tcs.teacher_id = #{teacherId}")
    List<OrgClass> selectClassesByTeacherId(Long teacherId);
}
