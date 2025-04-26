package com.shelest.booksy.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final String error;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final List<String> fieldErrors;

    public ApiError(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.fieldErrors = null;
    }

    public ApiError(String error, int status, List<String> fieldErrors) {
        this.error = error;
        this.message = "Validation failed";
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}
