package com.example.japanweb.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

/**
 * Standard API response envelope for all REST endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String message;
    private String errorCode;
    private T data;
    private Object errors;
    private String path;
    private String timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .path(resolveRequestPath())
                .timestamp(resolveTimestamp())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return created(data, "Created successfully");
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .path(resolveRequestPath())
                .timestamp(resolveTimestamp())
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorCode) {
        return error(status, message, errorCode, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorCode, Object errors) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .errorCode(errorCode)
                .errors(errors)
                .path(resolveRequestPath())
                .timestamp(resolveTimestamp())
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String errorCode) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .path(resolveRequestPath())
                .timestamp(resolveTimestamp())
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String errorCode, Object errors) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .errors(errors)
                .path(resolveRequestPath())
                .timestamp(resolveTimestamp())
                .build();
    }

    private static String resolveRequestPath() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }

        HttpServletRequest request = attributes.getRequest();
        return request.getRequestURI();
    }

    private static String resolveTimestamp() {
        return Instant.now().toString();
    }
}
