package com.eduplatform.homeschool.vo;

import com.eduplatform.homeschool.entity.HsNotice;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HsNoticeVO {
    private Long id;
    private String title;
    private String content;
    private Long targetClassId;
    private Integer noticeType;
    private Integer status;

    public static HsNoticeVO of(HsNotice e) {
        if (e == null) return null;
        return HsNoticeVO.builder()
                .id(e.getId()).title(e.getTitle()).content(e.getContent())
                .targetClassId(e.getTargetClassId()).noticeType(e.getNoticeType()).status(e.getStatus())
                .build();
    }
}
