package com.demo.authservice.exception;

import com.demo.common.exception.BusinessException;

public class AuthBusinessException extends BusinessException {
    private final int code;
    public AuthBusinessException(int code, String message) {
        super(code,message);
        this.code = code;
    }
    public int getCode() { return code; }
}
