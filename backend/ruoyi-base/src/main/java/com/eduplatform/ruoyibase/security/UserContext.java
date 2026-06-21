package com.eduplatform.ruoyibase.security;

import lombok.Data;

/**
 * 当前登录用户上下文（ThreadLocal）。供 MetaObjectHandler 等获取当前操作人。
 */
@Data
public class UserContext {

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    private Long userId;
    private String username;
    private String realName;
    private String tenantId;
    private String roleCode;

    public static UserContext get() {
        return HOLDER.get();
    }

    public static void set(UserContext ctx) {
        HOLDER.set(ctx);
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static String currentUsername() {
        UserContext ctx = get();
        return ctx != null ? ctx.getUsername() : "system";
    }

    public static String currentRoleCode() {
        UserContext ctx = get();
        return ctx != null ? ctx.getRoleCode() : null;
    }

    public static String currentTenantId() {
        UserContext ctx = get();
        return ctx != null ? ctx.getTenantId() : "default";
    }

    public static Long currentUserId() {
        UserContext ctx = get();
        return ctx != null ? ctx.getUserId() : null;
    }
}
