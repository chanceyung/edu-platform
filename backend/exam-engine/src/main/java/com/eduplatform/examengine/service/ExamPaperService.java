package com.eduplatform.examengine.service;

import com.eduplatform.examengine.dto.ExamComposeDTO;
import com.eduplatform.examengine.dto.ExamPaperSaveDTO;
import com.eduplatform.examengine.entity.ExamPaper;
import com.eduplatform.examengine.vo.ExamPaperDetailVO;

import java.util.List;

/**
 * 试卷业务接口（含组卷）。
 */
public interface ExamPaperService {

    /** 新建空试卷（totalScore=0, questionCount=0） */
    Long create(ExamPaperSaveDTO dto);

    /** 试卷列表 */
    List<ExamPaper> list();

    /**
     * 组卷：向试卷添加题目。校验试卷与每道题目存在，累加试卷总分与题数。
     * @throws IllegalArgumentException 试卷或题目不存在
     */
    void compose(ExamComposeDTO dto);

    /** 试卷详情（含题目内容+选项），供前端展示/作答 */
    ExamPaperDetailVO getDetail(Long paperId);

    /** 试卷题目关联ID列表 */
    List<Long> listQuestionIds(Long paperId);
}
