package com.finq.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseApiResponse<T> {
    T data;
    List<ErrorDto> errors;
    private Boolean error;
    private String status;
    private String message;

    public BaseApiResponse(T data) {
        this.data = data;
    }

    public BaseApiResponse(List<ErrorDto> errors) {
        this.error = true;
        this.errors = errors;
    }
    public BaseApiResponse(ErrorDto errorDto){
        this.error = true;
        this.errors = new ArrayList<>();
        this.errors.add(errorDto);
    }
    public BaseApiResponse(T data, String message){
        this.data = data;
        this.message = message;
    }
    public BaseApiResponse(String message){
        System.out.println(message);
        this.message = message;
    }
}
