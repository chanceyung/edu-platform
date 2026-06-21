-- question-bank 题目表（参考学之思设计：5题型/千分制/难度）
-- 命名规范：qbank_ 前缀；审计字段齐全
-- 说明：题干/答案/解析暂用 TEXT 字段直存（含图/公式后改 text_content 外置）
CREATE TABLE IF NOT EXISTS qbank_question (
    id                 BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id          VARCHAR(16)  NOT NULL COMMENT '租户编号',
    type               TINYINT      NOT NULL COMMENT '题型 1单选 2多选 3判断 4填空 5简答',
    subject_id         BIGINT       NULL     COMMENT '学科ID（后续 org_subject）',
    grade_level        INT          NULL     COMMENT '年级 7/8/9',
    score              INT          NOT NULL COMMENT '分值（千分制，如5分存5000）',
    difficult          TINYINT      NULL     COMMENT '难度 1-5',
    knowledge_point_id BIGINT       NULL     COMMENT '知识点ID（后续 qbank_knowledge_point）',
    stem               TEXT         NOT NULL COMMENT '题干（含公式/图片，富文本）',
    answer             TEXT         NULL     COMMENT '参考答案',
    analysis           TEXT         NULL     COMMENT '解析',
    status             TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 2停用',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator            VARCHAR(128) NULL,
    updater            VARCHAR(128) NULL,
    is_deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
    PRIMARY KEY (id),
    KEY idx_subject_grade (subject_id, grade_level),
    KEY idx_knowledge_point (knowledge_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目';

-- 待补：qbank_option（选项）/ qbank_knowledge_point（知识点树）/ sys_text_content（富文本外置）
