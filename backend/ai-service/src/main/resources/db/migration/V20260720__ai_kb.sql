-- P9 RAG 知识库：文档 + 切块（向量用 MySQL JSON 存，PoC 阶段够用）
CREATE TABLE IF NOT EXISTS ai_kb_document (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL DEFAULT 'default',
    title         VARCHAR(256) NOT NULL COMMENT '文档标题',
    source_type   VARCHAR(64)  NOT NULL COMMENT '来源类型(STANDARD课标/TEXTBOOK教材/QUESTION题目/TEMPLATE教案模板)',
    subject       VARCHAR(32)  NULL     COMMENT '学科',
    grade_level   INT          NULL     COMMENT '年级',
    chunk_count   INT          NOT NULL DEFAULT 0 COMMENT '切块数',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1已索引 2待索引',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_source_type (source_type, subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG知识库文档';

CREATE TABLE IF NOT EXISTS ai_kb_chunk (
    id              BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(16)  NOT NULL DEFAULT 'default',
    document_id     BIGINT       NOT NULL COMMENT '所属文档',
    content         TEXT         NOT NULL COMMENT '切块内容',
    metadata_json   TEXT         NULL     COMMENT '元数据（学科/年级/章节/知识点等）',
    embedding       JSON         NULL     COMMENT '向量（Embedding-3 2048维，PoC用MySQL JSON）',
    chunk_index     INT          NOT NULL DEFAULT 0 COMMENT '切块序号',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_document_id (document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG知识库切块';
