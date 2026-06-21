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
        // 从 UserContext（ThreadLocal）获取当前操作人/租户（P6-3 RBAC 后填充真实值）
        try {
            Class<?> ctxClass = Class.forName("com.eduplatform.ruoyibase.security.UserContext");
            Object ctx = ctxClass.getMethod("get").invoke(null);
            if (ctx != null) {
                String username = (String) ctxClass.getMethod("currentUsername").invoke(null);
                String tenantId = (String) ctxClass.getMethod("currentTenantId").invoke(null);
                this.strictInsertFill(metaObject, "tenantId", String.class, tenantId);
                this.strictInsertFill(metaObject, "creator", String.class, username);
                this.strictInsertFill(metaObject, "updater", String.class, username);
            } else {
                this.strictInsertFill(metaObject, "tenantId", String.class, "default");
                this.strictInsertFill(metaObject, "creator", String.class, "system");
                this.strictInsertFill(metaObject, "updater", String.class, "system");
            }
        } catch (Exception e) {
            // UserContext 不可用（common 不依赖 ruoyi-base），降级默认值
            this.strictInsertFill(metaObject, "tenantId", String.class, "default");
            this.strictInsertFill(metaObject, "creator", String.class, "system");
            this.strictInsertFill(metaObject, "updater", String.class, "system");
        }
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            Class<?> ctxClass = Class.forName("com.eduplatform.ruoyibase.security.UserContext");
            Object ctx = ctxClass.getMethod("get").invoke(null);
            String username = ctx != null
                    ? (String) ctxClass.getMethod("currentUsername").invoke(null) : "system";
            this.strictUpdateFill(metaObject, "updater", String.class, username);
        } catch (Exception e) {
            this.strictUpdateFill(metaObject, "updater", String.class, "system");
        }
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
