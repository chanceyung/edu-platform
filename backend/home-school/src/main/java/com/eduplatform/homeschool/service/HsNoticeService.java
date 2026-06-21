package com.eduplatform.homeschool.service;

import com.eduplatform.homeschool.dto.HsNoticeSaveDTO;
import com.eduplatform.homeschool.entity.HsNotice;
import java.util.List;

public interface HsNoticeService {
    Long create(HsNoticeSaveDTO dto);
    List<HsNotice> listByClass(Long classId);
}
