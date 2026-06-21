package com.eduplatform.schoolorg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.schoolorg.dto.OrgSchoolSaveDTO;
import com.eduplatform.schoolorg.entity.OrgSchool;
import com.eduplatform.schoolorg.mapper.OrgSchoolMapper;
import com.eduplatform.schoolorg.service.OrgSchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学校业务实现。业务逻辑写此；事务注解只标需要的方法；禁止吞异常。
 */
@Service
@RequiredArgsConstructor
public class OrgSchoolServiceImpl implements OrgSchoolService {

    private final OrgSchoolMapper schoolMapper;

    @Override
    public Long create(OrgSchoolSaveDTO dto) {
        // 业务校验：编码租户内唯一（租户隔离拦截器待接入，当前先按 code 查）
        Long exists = schoolMapper.selectCount(
                new LambdaQueryWrapper<OrgSchool>().eq(OrgSchool::getCode, dto.getCode()));
        if (exists != null && exists > 0) {
            throw new IllegalArgumentException("学校编码已存在: " + dto.getCode());
        }
        OrgSchool school = new OrgSchool();
        school.setName(dto.getName());
        school.setCode(dto.getCode());
        school.setAddress(dto.getAddress());
        school.setSchoolType(dto.getSchoolType());
        school.setStatus(1);
        schoolMapper.insert(school);
        return school.getId();
    }

    @Override
    public OrgSchool getById(Long id) {
        return schoolMapper.selectById(id);
    }

    @Override
    public List<OrgSchool> list() {
        return schoolMapper.selectList(null);
    }
}
