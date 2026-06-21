-- home-school 通知表（家校沟通）
CREATE TABLE IF NOT EXISTS hs_notice (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号',
    title         VARCHAR(200) NOT NULL COMMENT '标题',
    content       TEXT         NOT NULL COMMENT '内容',
    target_class_id BIGINT     NULL     COMMENT '目标班级ID（null=全校）',
    notice_type   TINYINT      NOT NULL DEFAULT 1 COMMENT '1通知 2成绩推送 3作业提醒',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1已发布',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_target_class (target_class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家校通知';
