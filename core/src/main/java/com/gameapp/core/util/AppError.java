package com.gameapp.core.util;

import lombok.Getter;

@Getter
public enum AppError {
    OTP_EXPIRED("ERROR-001"),
    INVALID_OTP("ERROR-002"),
    UNAUTHORIZED("ERROR-003"),
    INSUFFICIENT_BALANCE("ERROR-04"),
    OTP_NOT_SENT("ERROR-005"),
    BET_NOT_FOUND("ERROR-006"),
    INVALID_OPERATION("ERROR-007"),
    PERIOD_NOT_FOUND("ERROR-008"),
    LUDO_TABLE_NOT_FOUND("ERROR-009"),
    LUDO_TABLE_FULL("ERROR-010"),
    LUDO_TABLE_ALREADY_JOINED("ERROR-011"),
    LUDO_TABLE_ALREADY_EXIST("ERROR-012");

    private final String errorCode;

    AppError(String errorCode) {
        this.errorCode = errorCode;
    }
}
