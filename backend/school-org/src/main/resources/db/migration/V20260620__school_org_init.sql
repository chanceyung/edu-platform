-- school-org 模块建表脚本（Flyway）
-- 命名规范：表名 org_ 前缀；必有审计字段（tenant_id/create_time/update_time/creator/updater/is_deleted）
-- 纪律：禁止物理删除业务数据（用 is_deleted 软删）；不可逆操作（DROP/TRUNCATE）须评审

-- 学生学籍表（示范结构，完整字段按需求补）
CREATE TABLE IF NOT EXISTS org_student (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL COMMENT '租户编号（学校）',
    student_no    VARCHAR(64)  NOT NULL COMMENT '学号',
    name          VARCHAR(64)  NOT NULL COMMENT '姓名',
    gender        TINYINT      NULL     COMMENT '性别 1男 2女',
    grade_id      BIGINT       NULL     COMMENT '年级ID',
    class_id      BIGINT       NULL     COMMENT '班级ID',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1在读 2毕业 3转学',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_student_no (tenant_id, student_no),
    KEY idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学籍';

-- 待补：org_school / org_grade / org_class / org_term / org_teacher /
--       org_teacher_class_subject / org_parent / org_student_parent / org_student_class(历史班级)
