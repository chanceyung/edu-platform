-- question-bank 选项表（选择题选项，关联题目）
CREATE TABLE IF NOT EXISTS qbank_option (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    question_id  BIGINT       NOT NULL COMMENT '题目ID',
    content      TEXT         NOT NULL COMMENT '选项内容（富文本）',
    is_correct   TINYINT      NOT NULL DEFAULT 0 COMMENT '0错误 1正确答案',
    sort         INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项（选择题用）';

-- 待补：qbank_knowledge_point（知识点树）
