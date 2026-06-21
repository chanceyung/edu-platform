package com.eduplatform.common.exception;

import com.eduplatform.common.response.ErrorCode;
import lombok.Getter;

/**
 * 业务异常。Service 层抛出，GlobalExceptionHandler 统一捕获返回 R。
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.code = errorCode.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
