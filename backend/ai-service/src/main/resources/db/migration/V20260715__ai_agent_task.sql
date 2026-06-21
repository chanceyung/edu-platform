-- P8 Agent 长程任务表
CREATE TABLE IF NOT EXISTS ai_agent_task (
    id              BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(16)  NOT NULL COMMENT '租户编号',
    goal            TEXT         NOT NULL COMMENT '用户目标（自然语言）',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '0待开始 1运行中 2已完成 3已取消 4失败',
    progress_pct    INT          NOT NULL DEFAULT 0 COMMENT '进度百分比 0-100',
    current_step    VARCHAR(256) NULL     COMMENT '当前步骤描述',
    checkpoint_json TEXT         NULL     COMMENT '检查点（JSON，含已完成步骤+中间状态）',
    result_json     TEXT         NULL     COMMENT '最终结果（JSON）',
    actor           VARCHAR(128) NULL     COMMENT '发起人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_actor (actor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent长程任务';
