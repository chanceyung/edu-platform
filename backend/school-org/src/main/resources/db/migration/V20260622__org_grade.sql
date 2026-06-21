-- school-org 组织：年级表
-- 关系：school 1→N grade；租户内(学校+年级名)唯一
CREATE TABLE IF NOT EXISTS org_grade (
    id           BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id    VARCHAR(16)  NOT NULL COMMENT '租户编号',
    school_id    BIGINT       NOT NULL COMMENT '学校ID',
    name         VARCHAR(64)  NOT NULL COMMENT '年级名称（七年级/八年级/九年级）',
    level        INT          NOT NULL COMMENT '年级层级（7/8/9）',
    sort         INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 2停用',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator      VARCHAR(128) NULL,
    updater      VARCHAR(128) NULL,
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_school_grade_name (tenant_id, school_id, name),
    KEY idx_school_id (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级';

-- 待补：org_class（V20260623）
