-- exam-engine 试卷表 + 试卷题目关联表（手动组卷）
-- 设计：试卷-题目用关联表（可查询），非学之思的 frame JSON（也可后续支持）
CREATE TABLE IF NOT EXISTS exam_paper (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号',
    name          VARCHAR(128) NOT NULL COMMENT '试卷名称',
    subject_id    BIGINT       NULL     COMMENT '学科ID',
    grade_level   INT          NULL     COMMENT '年级',
    total_score   INT          NOT NULL DEFAULT 0 COMMENT '试卷总分（千分制）',
    question_count INT         NOT NULL DEFAULT 0 COMMENT '题目数量',
    paper_type    TINYINT      NOT NULL DEFAULT 1 COMMENT '1固定试卷 2随机试卷（后续）',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 2停用',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_subject_grade (subject_id, grade_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷';

CREATE TABLE IF NOT EXISTS exam_paper_question (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    paper_id     BIGINT       NOT NULL COMMENT '试卷ID',
    question_id  BIGINT       NOT NULL COMMENT '题目ID',
    score        INT          NOT NULL COMMENT '该题分值（千分制）',
    sort         INT          NOT NULL DEFAULT 0 COMMENT '题号顺序',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_paper_question (paper_id, question_id),
    KEY idx_paper_id (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联（组卷）';
