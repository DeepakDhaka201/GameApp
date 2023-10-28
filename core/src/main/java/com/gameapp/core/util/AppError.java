package com.gameapp.core.util;

import lombok.Getter;

@Getter
public enum AppError {
    OTP_EXPIRED("ERROR-001"),
    INVALID_OTP("ERROR-002"),
    UNAUTHORIZED("ERROR-003"),
    INSUFFICIENT_BALANCE("ERROR-04");

    private String errorCode;

    AppError(String errorCode) {
        this.errorCode = errorCode;
    }
}
