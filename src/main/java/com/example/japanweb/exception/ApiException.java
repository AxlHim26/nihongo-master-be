package com.example.japanweb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base application exception with error code support.
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object details;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage(), null, null);
    }

    public ApiException(ErrorCode errorCode, String message) {
        this(errorCode, message, null, null);
    }

    public ApiException(ErrorCode errorCode, String message, Object details) {
        this(errorCode, message, details, null);
    }

    public ApiException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode, message, null, cause);
    }

    private ApiException(ErrorCode errorCode, String message, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
