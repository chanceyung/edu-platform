package com.eduplatform.aiservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduplatform.aiservice.entity.AiKbChunk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiKbChunkMapper extends BaseMapper<AiKbChunk> {

    @Select("SELECT id, content, metadata_json, embedding FROM ai_kb_chunk WHERE embedding IS NOT NULL LIMIT #{limit}")
    List<AiKbChunk> selectWithEmbedding(int limit);
}
