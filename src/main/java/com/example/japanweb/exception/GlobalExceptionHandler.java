package com.example.japanweb.exception;

import com.example.japanweb.dto.common.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for standardized API error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        log.warn(
                "Handled ApiException code={} status={} message={}",
                ex.getErrorCode().getCode(),
                ex.getHttpStatus().value(),
                ex.getMessage()
        );
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.putIfAbsent(fieldName, errorMessage);
        });

        return buildErrorResponse(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getDefaultMessage(), errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return buildErrorResponse(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getDefaultMessage(), errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                ErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage(),
                null
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwt(ExpiredJwtException ex) {
        return buildErrorResponse(
                ErrorCode.AUTH_TOKEN_EXPIRED,
                ErrorCode.AUTH_TOKEN_EXPIRED.getDefaultMessage(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(JwtException ex) {
        return buildErrorResponse(
                ErrorCode.AUTH_TOKEN_INVALID,
                ErrorCode.AUTH_TOKEN_INVALID.getDefaultMessage(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthentication(AuthenticationException ex) {
        return buildErrorResponse(
                ErrorCode.AUTH_TOKEN_INVALID,
                ErrorCode.AUTH_TOKEN_INVALID.getDefaultMessage(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(
                ErrorCode.AUTH_ACCESS_DENIED,
                ErrorCode.AUTH_ACCESS_DENIED.getDefaultMessage(),
                ex.getMessage()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadInput(RuntimeException ex) {
        return buildErrorResponse(
                ErrorCode.VALIDATION_INVALID_FORMAT,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                ErrorCode.INTERNAL_ERROR.getDefaultMessage(),
                null
        );
    }

    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(
            ErrorCode errorCode,
            String message,
            Object details
    ) {
        HttpStatus status = errorCode.getHttpStatus();
        ApiResponse<Object> response = ApiResponse.error(
                status,
                message,
                errorCode.getCode(),
                details
        );

        return ResponseEntity.status(status).body(response);
    }
}
