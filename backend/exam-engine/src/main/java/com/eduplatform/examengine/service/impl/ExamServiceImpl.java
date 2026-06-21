package com.eduplatform.examengine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.examengine.dto.ExamSaveDTO;
import com.eduplatform.examengine.entity.Exam;
import com.eduplatform.examengine.entity.ExamPaper;
import com.eduplatform.examengine.mapper.ExamMapper;
import com.eduplatform.examengine.mapper.ExamPaperMapper;
import com.eduplatform.examengine.service.ExamService;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.mapper.OrgClassMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试业务实现。跨模块校验试卷（ExamPaperMapper）与班级（OrgClassMapper）。
 */
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamPaperMapper paperMapper;
    private final OrgClassMapper classMapper;

    @Override
    public Long create(ExamSaveDTO dto) {
        ExamPaper paper = paperMapper.selectById(dto.getPaperId());
        if (paper == null) {
            throw new IllegalArgumentException("试卷不存在: " + dto.getPaperId());
        }
        OrgClass clazz = classMapper.selectById(dto.getClassId());
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在: " + dto.getClassId());
        }
        Exam exam = new Exam();
        exam.setName(dto.getName());
        exam.setPaperId(dto.getPaperId());
        exam.setClassId(dto.getClassId());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setDuration(dto.getDuration());
        exam.setAccessType(0);
        exam.setStatus(1);
        examMapper.insert(exam);
        return exam.getId();
    }

    @Override
    public List<Exam> list() {
        return examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .orderByDesc(Exam::getCreateTime));
    }

    @Override
    public List<Exam> listByClass(Long classId) {
        return examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getClassId, classId)
                .orderByDesc(Exam::getCreateTime));
    }

    @Override
    public Exam getById(Long id) {
        return examMapper.selectById(id);
    }
}
