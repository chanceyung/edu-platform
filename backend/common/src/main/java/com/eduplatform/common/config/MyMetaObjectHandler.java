package com.eduplatform.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计字段自动填充。
 * tenant_id 暂填 "default"（待接入多租户上下文拦截器后，从当前用户/请求头获取真实租户）。
 * createTime/updateTime/creator/updater/isDeleted 统一填充。
 * 所有继承 BaseEntity 的实体在 insert/update 时自动生效。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // TODO(多租户): 从租户上下文（ThreadLocal/请求头）获取真实 tenantId
        this.strictInsertFill(metaObject, "tenantId", String.class, "default");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "creator", String.class, "system");
        this.strictInsertFill(metaObject, "updater", String.class, "system");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updater", String.class, "system");
    }
}
