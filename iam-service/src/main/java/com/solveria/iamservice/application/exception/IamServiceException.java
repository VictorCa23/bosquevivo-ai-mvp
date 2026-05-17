package com.solveria.iamservice.application.exception;

public class IamServiceException extends RuntimeException {

    private final String errorCode;

    public IamServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public IamServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
