package com.example.japanweb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Response wrapper for all REST endpoints.
 * 
 * @param <T> Type of the data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
    private String errorCode;

    /**
     * Create a successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * Create a successful response with custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a successful response for creation
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .status(201)
                .message("Created successfully")
                .data(data)
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(int status, String message, String errorCode) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    /**
     * Create a not found error response
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .status(404)
                .message(message)
                .errorCode("NOT_FOUND")
                .build();
    }

    /**
     * Create a bad request error response
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return ApiResponse.<T>builder()
                .status(400)
                .message(message)
                .errorCode("BAD_REQUEST")
                .build();
    }
}
