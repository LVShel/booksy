package com.shelest.booksy.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final String errorCode;   // for frontend handling
    private final String error;       // user friendly message
    private final String message;     // technical message
    private final int status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private List<String> fieldErrors;

    public ApiError(String errorCode, String error, String message, int status) {
        this.errorCode = errorCode;
        this.error = error;
        this.message = message;
        this.status = status;
    }

    public ApiError(String errorCode, String error, int status, List<String> fieldErrors) {
        this.errorCode = errorCode;
        this.error = error;
        this.message = "Validation failed";
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}
