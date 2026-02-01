package com.example.japanweb.exception;

import com.example.japanweb.dto.ApiResponse;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for standardized API error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom API exceptions
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        log.warn("API Exception: {} - {}", ex.getErrorCode().getCode(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ex.getHttpStatus(),
                ex.getMessage(),
                ex.getErrorCode().getCode());

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status(400)
                .message("Validation failed")
                .data(errors)
                .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle constraint violations
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                400,
                ex.getMessage(),
                ErrorCode.VALIDATION_FAILED.getCode());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle authentication errors
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                401,
                "Invalid username or password",
                ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                401,
                ex.getMessage(),
                ErrorCode.AUTH_TOKEN_INVALID.getCode());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                403,
                "Access denied",
                ErrorCode.AUTH_ACCESS_DENIED.getCode());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                400,
                ex.getMessage(),
                ErrorCode.VALIDATION_FAILED.getCode());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle illegal state exceptions (e.g., game session expired)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                400,
                ex.getMessage(),
                ErrorCode.VALIDATION_FAILED.getCode());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle all other uncaught exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);

        ApiResponse<Void> response = ApiResponse.error(
                500,
                "An internal error occurred",
                ErrorCode.INTERNAL_ERROR.getCode());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
