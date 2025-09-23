package com.finq.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;
    private Map<String, String> details;

    public ErrorResponse(String errorCode, int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(String errorCode, int status, String message, Map<String, String> details) {
        this(errorCode, status , message);
        this.details = details;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

}
