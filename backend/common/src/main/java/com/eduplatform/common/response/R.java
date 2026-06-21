package com.eduplatform.common.response;

import lombok.Data;

/**
 * 统一响应结构。所有 Controller 必须返回 R<T>。
 * 错误码集中定义在 ErrorCode 枚举（待补）。
 */
@Data
public class R<T> {

    private int code;
    private String message;
    private T data;

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<>(0, "success", data);
    }

    public static <T> R<T> ok() {
        return new R<>(0, "success", null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }
}
