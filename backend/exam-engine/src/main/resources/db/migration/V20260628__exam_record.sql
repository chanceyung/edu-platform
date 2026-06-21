-- exam-engine 答卷双层：整卷记录 + 逐题答案（学之思判分模型借鉴）
-- 提交时客观题(选择/判断)自动判分，主观题(简答/填空)待人工/AI
CREATE TABLE IF NOT EXISTS exam_record (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号',
    exam_id       BIGINT       NOT NULL COMMENT '考试ID',
    student_id    BIGINT       NOT NULL COMMENT '学生ID',
    submit_time   DATETIME     NULL     COMMENT '提交时间',
    system_score  INT          NOT NULL DEFAULT 0 COMMENT '系统判分（客观题，千分制）',
    paper_score   INT          NOT NULL DEFAULT 0 COMMENT '试卷总分（千分制）',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1待判分(含主观) 2完成(全客观已判)',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_exam_student (exam_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试答卷（整卷）';

CREATE TABLE IF NOT EXISTS exam_record_answer (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号',
    record_id     BIGINT       NOT NULL COMMENT '答卷ID',
    question_id   BIGINT       NOT NULL COMMENT '题目ID',
    answer        VARCHAR(1000) NULL    COMMENT '学生答案（客观题：选项ID JSON 数组）',
    customer_score INT         NOT NULL DEFAULT 0 COMMENT '该题得分（千分制）',
    do_right      TINYINT      NULL     COMMENT '0错 1对 NULL待判（主观题）',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答卷逐题答案';
