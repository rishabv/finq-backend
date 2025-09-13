package com.finq.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
    private String msg;
    private String code;
    private Object rejectedValue;
    private String stackTrace;
    private LocalDateTime timestamp;

    public ErrorDto(String msg) {
        this.msg = msg;
    }

    public ErrorDto(String msg, String code, Object rejectedValue, LocalDateTime time) {
        this.msg = msg;
        this.code = code;
        this.rejectedValue = rejectedValue;
        this.timestamp = time;
    }

    public ErrorDto(String msg, String stackTrace) {
        this.msg = msg;
        this.stackTrace = stackTrace;
    }
}
