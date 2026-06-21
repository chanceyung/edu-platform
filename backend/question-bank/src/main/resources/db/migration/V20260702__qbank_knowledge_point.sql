-- question-bank 知识点树（树形结构，题目归属知识点）
CREATE TABLE IF NOT EXISTS qbank_knowledge_point (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    parent_id    BIGINT       NOT NULL DEFAULT 0 COMMENT '父知识点ID（0=根节点）',
    name         VARCHAR(128) NOT NULL COMMENT '知识点名称',
    sort         INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 2停用',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点树';
