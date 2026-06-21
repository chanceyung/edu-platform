package com.eduplatform.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.homework.dto.HomeworkSaveDTO;
import com.eduplatform.homework.entity.Homework;
import com.eduplatform.homework.mapper.HomeworkMapper;
import com.eduplatform.homework.service.HomeworkService;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.mapper.OrgClassMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作业业务实现。跨模块校验班级（OrgClassMapper）。
 */
@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkMapper homeworkMapper;
    private final OrgClassMapper classMapper;

    @Override
    public Long create(HomeworkSaveDTO dto) {
        OrgClass clazz = classMapper.selectById(dto.getClassId());
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在: " + dto.getClassId());
        }
        Homework hw = new Homework();
        hw.setTitle(dto.getTitle());
        hw.setClassId(dto.getClassId());
        hw.setSubjectId(dto.getSubjectId());
        hw.setType(dto.getType());
        hw.setContent(dto.getContent());
        hw.setDeadline(dto.getDeadline());
        hw.setStatus(1);
        homeworkMapper.insert(hw);
        return hw.getId();
    }

    @Override
    public List<Homework> listByClass(Long classId) {
        return homeworkMapper.selectList(new LambdaQueryWrapper<Homework>()
                .eq(Homework::getClassId, classId)
                .orderByDesc(Homework::getCreateTime));
    }

    @Override
    public Homework getById(Long id) {
        return homeworkMapper.selectById(id);
    }
}
