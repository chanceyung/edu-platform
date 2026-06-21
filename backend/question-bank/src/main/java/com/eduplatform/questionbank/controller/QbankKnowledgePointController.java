package com.eduplatform.questionbank.controller;

import com.eduplatform.common.response.R;
import com.eduplatform.questionbank.entity.QbankKnowledgePoint;
import com.eduplatform.questionbank.mapper.QbankKnowledgePointMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识点树接口（树形CRUD）。
 */
@Tag(name = "知识点树")
@RestController
@RequestMapping("/v1/qbank/knowledge-points")
@RequiredArgsConstructor
public class QbankKnowledgePointController {

    private final QbankKnowledgePointMapper kpMapper;

    @Operation(summary = "知识点树（全树，前端构建 parent-children）")
    @GetMapping
    public R<List<QbankKnowledgePoint>> list() {
        return R.ok(kpMapper.selectList(new LambdaQueryWrapper<QbankKnowledgePoint>()
                .orderByAsc(QbankKnowledgePoint::getSort)));
    }

    @Operation(summary = "新增知识点")
    @PostMapping
    public R<Long> create(@RequestBody QbankKnowledgePoint kp) {
        if (kp.getParentId() == null) kp.setParentId(0L);
        if (kp.getStatus() == null) kp.setStatus(1);
        kpMapper.insert(kp);
        return R.ok(kp.getId());
    }

    @Operation(summary = "按父节点查子知识点")
    @GetMapping("/children/{parentId}")
    public R<List<QbankKnowledgePoint>> children(@PathVariable Long parentId) {
        return R.ok(kpMapper.selectList(new LambdaQueryWrapper<QbankKnowledgePoint>()
                .eq(QbankKnowledgePoint::getParentId, parentId)
                .orderByAsc(QbankKnowledgePoint::getSort)));
    }
}
