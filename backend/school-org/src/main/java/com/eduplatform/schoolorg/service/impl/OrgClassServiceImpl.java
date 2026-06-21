package com.eduplatform.schoolorg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.schoolorg.dto.OrgClassSaveDTO;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.entity.OrgGrade;
import com.eduplatform.schoolorg.mapper.OrgClassMapper;
import com.eduplatform.schoolorg.mapper.OrgGradeMapper;
import com.eduplatform.schoolorg.service.OrgClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgClassServiceImpl implements OrgClassService {

    private final OrgClassMapper classMapper;
    private final OrgGradeMapper gradeMapper;

    @Override
    public Long create(OrgClassSaveDTO dto) {
        OrgGrade grade = gradeMapper.selectById(dto.getGradeId());
        if (grade == null) {
            throw new IllegalArgumentException("年级不存在: " + dto.getGradeId());
        }
        Long exists = classMapper.selectCount(new LambdaQueryWrapper<OrgClass>()
                .eq(OrgClass::getGradeId, dto.getGradeId())
                .eq(OrgClass::getName, dto.getName()));
        if (exists != null && exists > 0) {
            throw new IllegalArgumentException("该班级已存在: " + dto.getName());
        }
        OrgClass clazz = new OrgClass();
        clazz.setSchoolId(dto.getSchoolId());
        clazz.setGradeId(dto.getGradeId());
        clazz.setName(dto.getName());
        clazz.setEntryYear(dto.getEntryYear());
        clazz.setHeadTeacherId(dto.getHeadTeacherId());
        clazz.setSort(dto.getSort() == null ? 0 : dto.getSort());
        clazz.setStatus(1);
        classMapper.insert(clazz);
        return clazz.getId();
    }

    @Override
    public List<OrgClass> listByGrade(Long gradeId) {
        return classMapper.selectList(new LambdaQueryWrapper<OrgClass>()
                .eq(OrgClass::getGradeId, gradeId)
                .orderByAsc(OrgClass::getSort));
    }
}
