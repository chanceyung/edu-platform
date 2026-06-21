package com.eduplatform.schoolorg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.schoolorg.dto.OrgGradeSaveDTO;
import com.eduplatform.schoolorg.entity.OrgGrade;
import com.eduplatform.schoolorg.entity.OrgSchool;
import com.eduplatform.schoolorg.mapper.OrgGradeMapper;
import com.eduplatform.schoolorg.mapper.OrgSchoolMapper;
import com.eduplatform.schoolorg.service.OrgGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 年级业务实现。跨实体校验（学校存在）在同模块内注入 OrgSchoolMapper。
 */
@Service
@RequiredArgsConstructor
public class OrgGradeServiceImpl implements OrgGradeService {

    private final OrgGradeMapper gradeMapper;
    private final OrgSchoolMapper schoolMapper;

    @Override
    public Long create(OrgGradeSaveDTO dto) {
        // 校验学校存在
        OrgSchool school = schoolMapper.selectById(dto.getSchoolId());
        if (school == null) {
            throw new IllegalArgumentException("学校不存在: " + dto.getSchoolId());
        }
        // 校验该校年级名不重复
        Long exists = gradeMapper.selectCount(new LambdaQueryWrapper<OrgGrade>()
                .eq(OrgGrade::getSchoolId, dto.getSchoolId())
                .eq(OrgGrade::getName, dto.getName()));
        if (exists != null && exists > 0) {
            throw new IllegalArgumentException("该年级已存在: " + dto.getName());
        }
        OrgGrade grade = new OrgGrade();
        grade.setSchoolId(dto.getSchoolId());
        grade.setName(dto.getName());
        grade.setLevel(dto.getLevel());
        grade.setSort(dto.getSort() == null ? 0 : dto.getSort());
        grade.setStatus(1);
        gradeMapper.insert(grade);
        return grade.getId();
    }

    @Override
    public List<OrgGrade> listBySchool(Long schoolId) {
        return gradeMapper.selectList(new LambdaQueryWrapper<OrgGrade>()
                .eq(OrgGrade::getSchoolId, schoolId)
                .orderByAsc(OrgGrade::getSort));
    }
}
