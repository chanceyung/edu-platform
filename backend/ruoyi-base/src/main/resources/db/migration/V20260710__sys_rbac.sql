-- ruoyi-base 精简RBAC：用户/角色/菜单 + 关联表（参考RuoYi MIT设计，自写精简版）
CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT UNSIGNED NOT NULL COMMENT '主键',
    tenant_id     VARCHAR(16)  NOT NULL DEFAULT 'default' COMMENT '租户（学校）编号',
    username      VARCHAR(64)  NOT NULL COMMENT '用户名',
    password      VARCHAR(128) NOT NULL COMMENT '密码（BCrypt）',
    real_name     VARCHAR(64)  NULL     COMMENT '真实姓名',
    phone         VARCHAR(20)  NULL     COMMENT '手机号',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_username (tenant_id, username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE IF NOT EXISTS sys_role (
    id            BIGINT UNSIGNED NOT NULL,
    tenant_id     VARCHAR(16)  NOT NULL DEFAULT 'default',
    role_code     VARCHAR(64)  NOT NULL COMMENT '角色编码（ADMIN/TEACHER/STUDENT/PARENT）',
    role_name     VARCHAR(64)  NOT NULL COMMENT '角色名称',
    status        TINYINT      NOT NULL DEFAULT 1,
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator       VARCHAR(128) NULL,
    updater       VARCHAR(128) NULL,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_role_code (tenant_id, role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id            BIGINT UNSIGNED NOT NULL,
    tenant_id     VARCHAR(16)  NOT NULL DEFAULT 'default',
    user_id       BIGINT       NOT NULL,
    role_id       BIGINT       NOT NULL,
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

CREATE TABLE IF NOT EXISTS sys_menu (
    id            BIGINT UNSIGNED NOT NULL,
    tenant_id     VARCHAR(16)  NOT NULL DEFAULT 'default',
    parent_id     BIGINT       NOT NULL DEFAULT 0,
    menu_name     VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    menu_type     TINYINT      NOT NULL COMMENT '1目录 2菜单 3按钮',
    permission    VARCHAR(128) NULL     COMMENT '权限标识（如 org:school:create）',
    path          VARCHAR(128) NULL     COMMENT '前端路由',
    sort          INT          NOT NULL DEFAULT 0,
    status        TINYINT      NOT NULL DEFAULT 1,
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单/权限';

CREATE TABLE IF NOT EXISTS sys_role_menu (
    id            BIGINT UNSIGNED NOT NULL,
    role_id       BIGINT       NOT NULL,
    menu_id       BIGINT       NOT NULL,
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联';

-- 初始数据：默认管理员账号（密码=admin123的BCrypt加密，$2a$10$N.ZOn9G6/YLFixAtpMg6h.NRGrXJvj9F6OG0dOiXw5J8Zefm9FBSm）
INSERT IGNORE INTO sys_user (id, tenant_id, username, password, real_name, status) VALUES
(1, 'default', 'admin', '$2a$10$N.ZOn9G6/YLFixAtpMg6h.NRGrXJvj9F6OG0dOiXw5J8Zefm9FBSm', 'Administrator', 1);
INSERT IGNORE INTO sys_role (id, tenant_id, role_code, role_name) VALUES
(1, 'default', 'ADMIN', 'Administrator'),
(2, 'default', 'TEACHER', 'Teacher'),
(3, 'default', 'STUDENT', 'Student');
INSERT IGNORE INTO sys_user_role (id, tenant_id, user_id, role_id) VALUES
(1, 'default', 1, 1);
