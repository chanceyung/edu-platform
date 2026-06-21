-- P0 补全：教师 + 任课表 + 家长 + 亲子关系
CREATE TABLE IF NOT EXISTS org_teacher (
    id              BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(16)  NOT NULL COMMENT '租户',
    school_id       BIGINT       NOT NULL COMMENT '所属学校',
    name            VARCHAR(64)  NOT NULL COMMENT '姓名',
    employee_no     VARCHAR(64)  NULL     COMMENT '工号',
    phone           VARCHAR(20)  NULL     COMMENT '手机号',
    subject         VARCHAR(32)  NULL     COMMENT '任教学科',
    title           VARCHAR(64)  NULL     COMMENT '职称',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1在职 2离职',
    user_id         BIGINT       NULL     COMMENT '关联sys_user的ID',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator         VARCHAR(128) NULL, updater VARCHAR(128) NULL,
    is_deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_school (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师';

CREATE TABLE IF NOT EXISTS org_teacher_class_subject (
    id              BIGINT UNSIGNED NOT NULL,
    tenant_id       VARCHAR(16)  NOT NULL,
    teacher_id      BIGINT       NOT NULL COMMENT '教师ID',
    class_id        BIGINT       NOT NULL COMMENT '班级ID',
    subject         VARCHAR(32)  NOT NULL COMMENT '学科',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_class_subject (teacher_id, class_id, subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任课表';

CREATE TABLE IF NOT EXISTS org_parent (
    id              BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(16)  NOT NULL,
    name            VARCHAR(64)  NOT NULL COMMENT '家长姓名',
    phone           VARCHAR(20)  NULL     COMMENT '手机号',
    relationship    VARCHAR(16)  NULL     COMMENT '关系(父亲/母亲/其他)',
    user_id         BIGINT       NULL     COMMENT '关联sys_user',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator         VARCHAR(128) NULL, updater VARCHAR(128) NULL,
    is_deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家长';

CREATE TABLE IF NOT EXISTS org_student_parent (
    id              BIGINT UNSIGNED NOT NULL,
    tenant_id       VARCHAR(16)  NOT NULL,
    student_id      BIGINT       NOT NULL COMMENT '学生ID',
    parent_id       BIGINT       NOT NULL COMMENT '家长ID',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_parent (student_id, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生-家长关系';
