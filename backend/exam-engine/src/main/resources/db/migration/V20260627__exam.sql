-- exam-engine 考试表（发布考试：试卷→班级+时间）
CREATE TABLE IF NOT EXISTS exam (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号',
    name          VARCHAR(128) NOT NULL COMMENT '考试名称',
    paper_id      BIGINT       NOT NULL COMMENT '试卷ID',
    class_id      BIGINT       NOT NULL COMMENT '班级ID',
    start_time    DATETIME     NULL     COMMENT '开始时间',
    end_time      DATETIME     NULL     COMMENT '结束时间',
    duration      INT          NULL     COMMENT '时长（分钟）',
    access_type   TINYINT      NOT NULL DEFAULT 0 COMMENT '0全部 1指定（后续）',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1未发布 2进行中 3已结束',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_paper_id (paper_id),
    KEY idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试';
