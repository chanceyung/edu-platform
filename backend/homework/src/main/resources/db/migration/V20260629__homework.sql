-- homework 作业表 + 提交表（自研，学之思无独立作业）
CREATE TABLE IF NOT EXISTS homework (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    title        VARCHAR(128) NOT NULL COMMENT '作业标题',
    class_id     BIGINT       NOT NULL COMMENT '班级ID',
    subject_id   BIGINT       NULL     COMMENT '学科ID',
    type         TINYINT      NOT NULL COMMENT '1试题 2拍照 3文本',
    content      TEXT         NULL     COMMENT '作业内容（要求/题干）',
    deadline     DATETIME     NULL     COMMENT '截止时间',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1进行中 2已截止',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业';

CREATE TABLE IF NOT EXISTS homework_submission (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    homework_id  BIGINT       NOT NULL COMMENT '作业ID',
    student_id   BIGINT       NOT NULL COMMENT '学生ID',
    content      TEXT         NULL     COMMENT '提交内容（答案文本/图片URL）',
    submit_time  DATETIME     NULL     COMMENT '提交时间',
    score        INT          NOT NULL DEFAULT 0 COMMENT '得分（千分制，批改后）',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1已交未批 2已批',
    comment      TEXT         NULL     COMMENT '批改评语',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_homework_student (homework_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交';
