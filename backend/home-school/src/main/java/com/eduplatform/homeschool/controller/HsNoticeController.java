package com.eduplatform.homeschool.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.homeschool.dto.HsNoticeSaveDTO;
import com.eduplatform.homeschool.service.HsNoticeService;
import com.eduplatform.homeschool.vo.HsNoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "家校通知")
@RestController
@RequestMapping("/v1/hs/notices")
@RequiredArgsConstructor
public class HsNoticeController {
    private final HsNoticeService noticeService;

    @Operation(summary = "发布通知")
    @PostMapping
    public R<Long> create(@Valid @RequestBody HsNoticeSaveDTO dto) {
        return R.ok(noticeService.create(dto));
    }

    @Operation(summary = "按班级查通知（含全校通知）")
    @GetMapping
    public R<List<HsNoticeVO>> list(@RequestParam(required = false) Long classId) {
        return R.ok(noticeService.listByClass(classId).stream().map(HsNoticeVO::of).toList());
    }
}
