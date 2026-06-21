package com.eduplatform.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举（千分制分段）。
 * 1xxx 参数校验 / 2xxx 鉴权 / 3xxx 业务 / 5xxx 系统
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 1xxx 参数校验
    PARAM_INVALID(1000, "参数无效"),
    PARAM_MISSING(1001, "参数缺失"),

    // 2xxx 鉴权
    UNAUTHORIZED(2000, "未登录或token失效"),
    FORBIDDEN(2001, "无权限"),
    TOKEN_EXPIRED(2002, "token已过期"),
    LOGIN_FAILED(2003, "用户名或密码错误"),
    ACCOUNT_LOCKED(2004, "账号已锁定"),

    // 3xxx 业务
    DATA_NOT_FOUND(3000, "数据不存在"),
    DATA_DUPLICATE(3001, "数据重复"),
    DATA_CONFLICT(3002, "数据冲突"),
    AI_CALL_FAILED(3003, "AI服务调用失败"),
    EXAM_NOT_FOUND(3004, "考试不存在"),
    HOMEWORK_NOT_FOUND(3005, "作业不存在"),

    // 5xxx 系统
    INTERNAL_ERROR(5000, "系统内部错误"),
    DB_ERROR(5001, "数据库错误"),
    NETWORK_ERROR(5002, "网络错误");

    private final int code;
    private final String message;
}
