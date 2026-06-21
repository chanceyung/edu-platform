-- school-org 组织根：学校表
-- 命名规范：org_ 前缀；审计字段齐全；租户内编码唯一
-- 纪律：软删（is_deleted），禁物理删除；不可逆操作须评审
CREATE TABLE IF NOT EXISTS org_school (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    name         VARCHAR(128) NOT NULL COMMENT '学校名称',
    code         VARCHAR(64)  NOT NULL COMMENT '学校编码（租户内唯一）',
    address      VARCHAR(255) NULL     COMMENT '地址',
    school_type  VARCHAR(32)  NOT NULL DEFAULT 'MIDDLE' COMMENT '学校类型（初中=MIDDLE）',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_school_code (tenant_id, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校';

-- 待补迁移：org_grade / org_class / org_teacher / org_teacher_class_subject /
--           org_student(已建于 V20260620) 关联 / org_parent / org_student_parent
