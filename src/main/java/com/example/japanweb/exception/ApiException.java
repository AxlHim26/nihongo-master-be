package com.example.japanweb.exception;

import lombok.Getter;

/**
 * Base application exception with error code support.
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final int httpStatus;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.httpStatus = determineHttpStatus(errorCode);
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = determineHttpStatus(errorCode);
    }

    public ApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = determineHttpStatus(errorCode);
    }

    private int determineHttpStatus(ErrorCode code) {
        String codePrefix = code.getCode().substring(0, 1);
        return switch (codePrefix) {
            case "1" -> 401; // Auth errors
            case "2" -> 400; // Validation errors
            case "3", "4", "5", "6", "7" -> 404; // Not found errors for resources
            case "8" -> 400; // Video errors (bad request or not found)
            default -> 500; // System errors
        };
    }
}
