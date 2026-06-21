package com.eduplatform.schoolorg.service;

import com.eduplatform.schoolorg.entity.OrgStudent;

/**
 * 学生学籍业务接口。业务逻辑定义在此，实现在 impl。
 */
public interface OrgStudentService {

    /**
     * 根据学号查询学生。
     * @param studentNo 学号
     * @return 学生实体（不存在返回 null）
     */
    OrgStudent getByStudentNo(String studentNo);

    /**
     * 新增学生（校验学号唯一、自动填充审计字段）。
     * @param student 学生
     * @return 主键
     */
    Long create(OrgStudent student);
}
