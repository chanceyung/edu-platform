-- ai-service 信任内核：AI 调用日志（每次 AI 操作可追溯，CLAUDE.md 铁律）
-- 用途：AI 批改/组卷等所有 AI 操作必须写此表，可审计/可回滚/可追责
CREATE TABLE IF NOT EXISTS ai_task_log (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号',
    task_type     VARCHAR(64)  NOT NULL COMMENT '任务类型（GRADING批改/COMPOSE组卷/LESSON备课等）',
    model         VARCHAR(64)  NULL     COMMENT '模型（glm-5.2/glm-4-air等）',
    input         TEXT         NULL     COMMENT '输入摘要（JSON，脱敏）',
    output        TEXT         NULL     COMMENT '输出摘要（JSON）',
    input_tokens  INT          NULL     COMMENT '输入token',
    output_tokens INT          NULL     COMMENT '输出token',
    latency_ms    INT          NULL     COMMENT '耗时（毫秒）',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1成功 2失败',
    actor         VARCHAR(128) NULL     COMMENT '触发者（用户/系统）',
    biz_ref_id    BIGINT       NULL     COMMENT '业务关联ID（如 submissionId，可回溯到业务）',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_biz_ref (task_type, biz_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI调用日志（信任内核）';
