package com.gameapp.core.util;

public class AppException extends RuntimeException {
    private String displayMessage;
    private String errorCode;

    private String[] params;

    public AppException(String message) {
        this.displayMessage = message;
        this.errorCode = null;
    }

    public AppException(AppError error, String displayMessage) {
        this.displayMessage = displayMessage;
        this.errorCode = error.getErrorCode();
    }

    public AppException(AppError error, String displayMessage, String... params) {
        this.displayMessage = displayMessage;
        this.errorCode = error.getErrorCode();
        this.params = params;
    }
}
