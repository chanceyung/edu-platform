package com.eduplatform.aiservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiKbChunk;
import com.eduplatform.aiservice.mapper.AiKbChunkMapper;
import com.eduplatform.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 补全 embedding 接口（将未向量化的切块批量补齐）。
 */
@Slf4j
@Tag(name = "RAG补全向量化")
@RestController
@RequestMapping("/v1/ai/kb/reembed")
@RequiredArgsConstructor
public class ReembedController {

    private final AiKbChunkMapper chunkMapper;
    private final ZhipuClient zhipu;

    @Operation(summary = "批量补全未向量化的切块（每次最多20个，500ms间隔）")
    @PostMapping
    public R<Map<String, Object>> reembed(@RequestParam(defaultValue = "20") int batch) {
        // 查未向量化的切块
        List<AiKbChunk> pending = chunkMapper.selectList(new LambdaQueryWrapper<AiKbChunk>()
                .isNull(AiKbChunk::getEmbedding)
                .last("LIMIT " + batch));

        if (pending.isEmpty()) {
            return R.ok(Map.of("processed", 0, "message", "全部切块已向量化"));
        }

        int success = 0, failed = 0;
        for (AiKbChunk chunk : pending) {
            try {
                Thread.sleep(500); // 限流
                String embedding = zhipu.embed(chunk.getContent());
                chunkMapper.update(null, new LambdaUpdateWrapper<AiKbChunk>()
                        .eq(AiKbChunk::getId, chunk.getId())
                        .set(AiKbChunk::getEmbedding, embedding));
                success++;
            } catch (Exception e) {
                log.warn("补全向量化失败 chunkId={}: {}", chunk.getId(), e.getMessage());
                failed++;
            }
        }

        // 统计剩余
        Long remaining = chunkMapper.selectCount(new LambdaQueryWrapper<AiKbChunk>()
                .isNull(AiKbChunk::getEmbedding));

        log.info("批量向量化完成: 成功{} 失败{} 剩余{}", success, failed, remaining);
        return R.ok(Map.of("processed", success, "failed", failed, "remaining", remaining));
    }
}
