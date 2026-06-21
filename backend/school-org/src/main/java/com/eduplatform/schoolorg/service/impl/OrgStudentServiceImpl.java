package com.eduplatform.schoolorg.service.impl;

import com.eduplatform.schoolorg.entity.OrgStudent;
import com.eduplatform.schoolorg.mapper.OrgStudentMapper;
import com.eduplatform.schoolorg.service.OrgStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 学生学籍业务实现。
 * 纪律：业务逻辑写这里；事务注解只标需要事务的方法；禁止吞异常。
 */
@Service
@RequiredArgsConstructor
public class OrgStudentServiceImpl implements OrgStudentService {

    private final OrgStudentMapper studentMapper;

    @Override
    public OrgStudent getByStudentNo(String studentNo) {
        // TODO(P0): 接入数据权限拦截（教师只见自己班）+ 租户隔离
        return studentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrgStudent>()
                        .eq(OrgStudent::getStudentNo, studentNo));
    }

    @Override
    public Long create(OrgStudent student) {
        // 业务校验：学号唯一（示范，完整校验待补）
        if (getByStudentNo(student.getStudentNo()) != null) {
            throw new IllegalArgumentException("学号已存在: " + student.getStudentNo());
        }
        studentMapper.insert(student);
        return student.getId();
    }
}
