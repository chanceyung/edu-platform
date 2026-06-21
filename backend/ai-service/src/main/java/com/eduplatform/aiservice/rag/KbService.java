package com.eduplatform.aiservice.rag;

import com.eduplatform.aiservice.client.ZhipuClient;
import com.eduplatform.aiservice.entity.AiKbChunk;
import com.eduplatform.aiservice.entity.AiKbDocument;
import com.eduplatform.aiservice.mapper.AiKbChunkMapper;
import com.eduplatform.aiservice.mapper.AiKbDocumentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG 知识库服务：文档入库（切块+向量化） + 相似检索。
 * 向量化用智谱 Embedding-3，检索用余弦相似度（PoC 用内存计算）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KbService {

    private final AiKbDocumentMapper docMapper;
    private final AiKbChunkMapper chunkMapper;
    private final ZhipuClient zhipu;
    private final ObjectMapper json = new ObjectMapper();

    /**
     * 入库一篇文档：切块 → 向量化 → 存库。
     */
    public Long ingest(String title, String sourceType, String subject, Integer gradeLevel, String fullText) {
        // 创建文档
        AiKbDocument doc = new AiKbDocument();
        doc.setTitle(title);
        doc.setSourceType(sourceType);
        doc.setSubject(subject);
        doc.setGradeLevel(gradeLevel);
        doc.setStatus(2); // 待索引
        docMapper.insert(doc);

        // 切块（按段落+固定长度）
        List<String> chunks = splitText(fullText, 300);
        for (int i = 0; i < chunks.size(); i++) {
            AiKbChunk chunk = new AiKbChunk();
            chunk.setTenantId("default");
            chunk.setDocumentId(doc.getId());
            chunk.setContent(chunks.get(i));
            chunk.setChunkIndex(i);
            chunk.setMetadataJson(buildMetadata(subject, gradeLevel, title));

            // 向量化（500ms间隔防API限流）
            try {
                Thread.sleep(500); // 限流：每次embedding间隔500ms
                String embedding = embed(chunks.get(i));
                chunk.setEmbedding(embedding);
            } catch (Exception e) {
                log.warn("切块向量化失败 idx={}: {}", i, e.getMessage());
            }

            chunkMapper.insert(chunk);
        }

        // 更新文档状态
        doc.setChunkCount(chunks.size());
        doc.setStatus(1);
        docMapper.updateById(doc);
        log.info("文档入库: {} 切块数: {}", title, chunks.size());
        return doc.getId();
    }

    /**
     * 检索 Top-K 最相关切块。
     */
    public List<KbSearchResult> search(String query, int topK) {
        // 1. 向量化查询
        String queryEmbedding = embed(query);

        // 2. 遍历所有切块计算余弦相似度（PoC，数据量大时换 Milvus）
        List<AiKbChunk> allChunks = chunkMapper.selectWithEmbedding(500);
        if (allChunks.isEmpty()) return List.of();

        double[] queryVec = parseEmbedding(queryEmbedding);
        return allChunks.stream()
                .map(chunk -> {
                    double[] chunkVec = parseEmbedding(chunk.getEmbedding());
                    double score = cosineSimilarity(queryVec, chunkVec);
                    return new KbSearchResult(chunk.getContent(), score, chunk.getMetadataJson());
                })
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .limit(topK)
                .collect(Collectors.toList());
    }

    /**
     * 构建增强 prompt：原始 prompt + 检索到的知识。
     */
    public String augmentPrompt(String originalPrompt, String query) {
        List<KbSearchResult> results = search(query, 3);
        if (results.isEmpty()) return originalPrompt;

        StringBuilder sb = new StringBuilder(originalPrompt);
        sb.append("\n\n=== 参考资料（来自知识库，请优先引用） ===\n");
        for (int i = 0; i < results.size(); i++) {
            sb.append("[参考").append(i + 1).append("] ").append(results.get(i).content()).append("\n\n");
        }
        return sb.toString();
    }

    // ===== 内部方法 =====

    private String embed(String text) {
        try {
            return zhipu.embed(text);
        } catch (Exception e) {
            throw new RuntimeException("向量化失败: " + e.getMessage(), e);
        }
    }

    private List<String> splitText(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();
        // 按段落切，超长再按句切
        for (String para : text.split("\n\n")) {
            if (para.length() <= maxChars) {
                chunks.add(para.trim());
            } else {
                for (int i = 0; i < para.length(); i += maxChars) {
                    chunks.add(para.substring(i, Math.min(i + maxChars, para.length())).trim());
                }
            }
        }
        return chunks.stream().filter(s -> !s.isEmpty()).toList();
    }

    private String buildMetadata(String subject, Integer grade, String title) {
        try {
            return json.writeValueAsString(Map.of("subject", subject != null ? subject : "", "grade", grade != null ? grade : 0, "source", title));
        } catch (Exception e) {
            return "{}";
        }
    }

    private double[] parseEmbedding(String json) {
        try {
            List<Double> list = this.json.readValue(json, List.class);
            return list.stream().mapToDouble(Double::doubleValue).toArray();
        } catch (Exception e) {
            return new double[0];
        }
    }

    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length || a.length == 0) return 0;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return normA == 0 || normB == 0 ? 0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public record KbSearchResult(String content, double score, String metadata) {}
}
