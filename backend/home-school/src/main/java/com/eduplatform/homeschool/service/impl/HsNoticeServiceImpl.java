package com.eduplatform.homeschool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.homeschool.dto.HsNoticeSaveDTO;
import com.eduplatform.homeschool.entity.HsNotice;
import com.eduplatform.homeschool.mapper.HsNoticeMapper;
import com.eduplatform.homeschool.service.HsNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HsNoticeServiceImpl implements HsNoticeService {
    private final HsNoticeMapper noticeMapper;

    @Override
    public Long create(HsNoticeSaveDTO dto) {
        HsNotice n = new HsNotice();
        n.setTitle(dto.getTitle());
        n.setContent(dto.getContent());
        n.setTargetClassId(dto.getTargetClassId());
        n.setNoticeType(dto.getNoticeType() == null ? 1 : dto.getNoticeType());
        n.setStatus(1);
        noticeMapper.insert(n);
        return n.getId();
    }

    @Override
    public List<HsNotice> listByClass(Long classId) {
        return noticeMapper.selectList(new LambdaQueryWrapper<HsNotice>()
                .and(w -> w.isNull(HsNotice::getTargetClassId).or().eq(HsNotice::getTargetClassId, classId))
                .orderByDesc(HsNotice::getCreateTime));
    }
}
