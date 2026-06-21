-- school-org 组织：班级表
-- 关系：grade 1→N class；租户内(年级+班级名)唯一
CREATE TABLE IF NOT EXISTS org_class (
    id               BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id        VARCHAR(16)  NOT NULL COMMENT '租户编号',
    school_id        BIGINT       NOT NULL COMMENT '学校ID',
    grade_id         BIGINT       NOT NULL COMMENT '年级ID',
    name             VARCHAR(64)  NOT NULL COMMENT '班级名称（如 1班）',
    entry_year       INT          NULL     COMMENT '入学年份',
    head_teacher_id  BIGINT       NULL     COMMENT '班主任教师ID（待 org_teacher 建立后关联）',
    sort             INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 2停用',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator          VARCHAR(128) NULL,
    updater          VARCHAR(128) NULL,
    is_deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_grade_class_name (tenant_id, grade_id, name),
    KEY idx_grade_id (grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级';
