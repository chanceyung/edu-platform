package com.eduplatform.aiservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_kb_chunk")
public class AiKbChunk {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String tenantId;
    private Long documentId;
    private String content;
    private String metadataJson;
    private String embedding;
    private Integer chunkIndex;
    private LocalDateTime createTime;
}
