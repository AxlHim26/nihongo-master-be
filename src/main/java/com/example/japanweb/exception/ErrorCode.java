package com.example.japanweb.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum defining all application error codes for consistent error handling.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Authentication Errors (1xxx)
    AUTH_INVALID_CREDENTIALS("1001", "Invalid username or password"),
    AUTH_TOKEN_EXPIRED("1002", "Authentication token has expired"),
    AUTH_TOKEN_INVALID("1003", "Invalid authentication token"),
    AUTH_ACCESS_DENIED("1004", "Access denied"),
    AUTH_USER_NOT_FOUND("1005", "User not found"),
    AUTH_USERNAME_EXISTS("1006", "Username already exists"),
    AUTH_EMAIL_EXISTS("1007", "Email already exists"),

    // Validation Errors (2xxx)
    VALIDATION_FAILED("2001", "Validation failed"),
    VALIDATION_MISSING_FIELD("2002", "Required field is missing"),
    VALIDATION_INVALID_FORMAT("2003", "Invalid format"),

    // Resource Errors (3xxx)
    RESOURCE_NOT_FOUND("3001", "Resource not found"),
    RESOURCE_ALREADY_EXISTS("3002", "Resource already exists"),

    // Grammar Errors (4xxx)
    GRAMMAR_BOOK_NOT_FOUND("4001", "Grammar book not found"),
    GRAMMAR_CHAPTER_NOT_FOUND("4002", "Grammar chapter not found"),
    GRAMMAR_POINT_NOT_FOUND("4003", "Grammar point not found"),

    // Vocabulary Errors (5xxx)
    VOCAB_COURSE_NOT_FOUND("5001", "Vocabulary course not found"),
    VOCAB_ENTRY_NOT_FOUND("5002", "Vocabulary entry not found"),
    VOCAB_IMPORT_FAILED("5003", "Failed to import vocabulary"),
    VOCAB_INVALID_EXCEL("5004", "Invalid Excel file format"),

    // Game Errors (6xxx)
    GAME_SESSION_NOT_FOUND("6001", "Game session not found or expired"),
    GAME_SESSION_EXPIRED("6002", "Game session has expired"),
    GAME_INVALID_QUESTION("6003", "Invalid question for this session"),
    GAME_INSUFFICIENT_WORDS("6004", "Not enough words to start game"),

    // SRS Errors (7xxx)
    SRS_INVALID_RATING("7001", "Invalid SRS rating"),
    SRS_PROGRESS_NOT_FOUND("7002", "Learning progress not found"),

    // Video Errors (8xxx)
    VIDEO_NOT_FOUND("8001", "Video not found"),
    VIDEO_STREAM_FAILED("8002", "Failed to stream video"),
    VIDEO_INVALID_RANGE("8003", "Invalid Range header"),

    // System Errors (9xxx)
    INTERNAL_ERROR("9001", "An internal error occurred"),
    SERVICE_UNAVAILABLE("9002", "Service is temporarily unavailable");

    private final String code;
    private final String defaultMessage;
}
